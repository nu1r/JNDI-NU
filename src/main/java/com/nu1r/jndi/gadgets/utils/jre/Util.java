package com.nu1r.jndi.gadgets.utils.jre;

import java.lang.reflect.InvocationHandler;

public class Util {
    public static TCObject makeProxy(Class[] interfaces, InvocationHandler handler, Serialization ser) throws Exception {
        return doMakeProxy(interfaces, handler, ser);
    }

    public static TCObject makeProxy(Class[] interfaces, TCObject handler, Serialization ser) throws Exception {
        return doMakeProxy(interfaces, handler, ser);
    }

    private static TCObject doMakeProxy(Class[] interfaces, Object handler, Serialization ser) throws Exception {
        TCObject         proxy     = new TCObject(ser);
        TCProxyClassDesc proxyDesc = new TCProxyClassDesc();
        for (Class intf : interfaces)
            proxyDesc.addInterface(intf);
        TCClassDesc desc = new TCClassDesc("java.lang.reflect.Proxy");
        desc.addField(new TCClassDesc.Field("h", InvocationHandler.class));
        proxy.addClassDescData(proxyDesc, new TCObject.ObjectData());
        proxy.addClassDescData(desc, (new TCObject.ObjectData()).addData(handler));
        return proxy;
    }
}
