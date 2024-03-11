package com.qi4l.jndi.gadgets.utils;

import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.utils.utf8OverlongEncoding.UTF8OverlongObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import static com.qi4l.jndi.gadgets.Config.Config.IS_DIRTY_IN_TC_RESET;

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

    public static void qiserialize(Object obj, final OutputStream out) throws Exception {
        final ObjectOutputStream objOut;

        if (IS_DIRTY_IN_TC_RESET) {
            objOut = new SuObjectOutputStream(out);
        } else {
            objOut = new ObjectOutputStream(out);
        }
        objOut.writeObject(obj);
    }

    public static void qiserialize4l(Object obj, final OutputStream out) throws Exception {
        final ObjectOutputStream objOut = new UTF8OverlongObjectOutputStream(out);
        objOut.writeObject(obj);
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
                for (int i = 0; i < Config.DIRTY_LENGTH_IN_TC_RESET; i++) {
                    Reflections.getMethodAndInvoke(Reflections.getFieldValue(this, "bout"), "writeByte", new Class[]{int.class}, new Object[]{TC_RESET});
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
