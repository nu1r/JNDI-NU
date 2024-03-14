package com.qi4l.jndi.gadgets.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ByteUtil {
    public static int getSubarrayIndex(byte[] haystack, byte[] needle) {
        outer:
        for (int i = 0; i <= haystack.length - needle.length; ++i) {
            for (int j = 0; j < needle.length; ++j) {
                if (haystack[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return i;
        }

        return -1;
    }

    public static byte[] deleteAt(byte[] bs, int index) {
        int    length = bs.length - 1;
        byte[] ret    = new byte[length];

        if (index == bs.length - 1) {
            System.arraycopy(bs, 0, ret, 0, length);
        } else if (index < bs.length - 1) {
            for (int i = index; i < length; i++) {
                bs[i] = bs[i + 1];
            }

            System.arraycopy(bs, 0, ret, 0, length);
        }

        return ret;
    }

    public static byte[] addAtIndex(byte[] bs, int index, byte b) {
        int    length = bs.length + 1;
        byte[] ret    = new byte[length];

        System.arraycopy(bs, 0, ret, 0, index);
        ret[index] = b;
        System.arraycopy(bs, index, ret, index + 1, length - index - 1);

        return ret;
    }

    public static byte[] addAtLast(byte[] bs, byte b) {
        int    length = bs.length + 1;
        byte[] ret    = new byte[length];

        System.arraycopy(bs, 0, ret, 0, length - 1);
        ret[length - 1] = b;

        return ret;
    }

    public static byte[] objectsToBytes(Object[] objs) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);
        for (Object obj : objs) {
            treatObject(dos, obj);
        }
        dos.close();
        return baos.toByteArray();
    }

    private static void treatObject(DataOutputStream dos, Object obj)
            throws IOException {
        if (obj instanceof Byte) {
            dos.writeByte((Byte) obj);
        } else if (obj instanceof Short) {
            dos.writeShort((Short) obj);
        } else if (obj instanceof Integer) {
            dos.writeInt((Integer) obj);
        } else if (obj instanceof Long) {
            dos.writeLong((Long) obj);
        } else if (obj instanceof String) {
            dos.writeUTF((String) obj);
        } else {
            ByteArrayOutputStream ba  = new ByteArrayOutputStream();
            ObjectOutputStream    oos = new ObjectOutputStream(ba);
            oos.writeObject(obj);
            oos.close();
            dos.write(ba.toByteArray(), 4, ba.size() - 4); // 4 = skip the header
        }
    }
}
