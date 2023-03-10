package com.nu1r.jndi.gadgets.utils;

import org.jboss.serial.io.JBossObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import static com.nu1r.jndi.gadgets.Config.Config.*;
import static com.nu1r.jndi.gadgets.utils.Reflections.getFieldValue;
import static com.nu1r.jndi.gadgets.utils.Reflections.getMethodAndInvoke;

public class Serializer implements Callable<byte[]> {
    private final Object object;

    public Serializer(Object object) {
        this.object = object;
    }

    public byte[] call() throws Exception {
        return serialize(object);
    }

    public static byte[] serialize(final Object obj) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        serialize(obj, out);
        return out.toByteArray();
    }

    public static byte[] serialize(final Object obj, final ByteArrayOutputStream out) throws IOException {
        final ObjectOutputStream objOut;
        objOut = new ObjectOutputStream(out);
        objOut.writeObject(obj);
        byte[] bytes = out.toByteArray();
        objOut.close();
        return bytes;
    }

    public static class SuObjectOutputStream extends ObjectOutputStream {

        public SuObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            super.writeStreamHeader();
            try {
                // 写入
                for (int i = 0; i < DIRTY_LENGTH_IN_TC_RESET; i++) {
                    getMethodAndInvoke(getFieldValue(this, "bout"), "writeByte", new Class[]{int.class}, new Object[]{TC_RESET});
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
