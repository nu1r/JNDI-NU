package com.feihong.ldap.gadgets.utils;

import java.io.FileOutputStream;

public class Utils {

    public static String[] handlerCommand(String command) {
        String info  = command.split("[-]")[1];
        int    index = info.indexOf("#");
        String par1  = info.substring(0, index);
        String par2  = info.substring(index + 1);
        return new String[]{par1, par2};
    }


    public static String base64Decode(String bs) throws Exception {
        Class  base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception ignored) {
            }
        }

        return new String(value);
    }

    public static void writeClassToFile(String fileName, byte[] classBytes) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName+".class");
        fileOutputStream.write(classBytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

}
