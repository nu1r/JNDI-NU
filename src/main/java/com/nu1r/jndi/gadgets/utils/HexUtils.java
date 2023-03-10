package com.nu1r.jndi.gadgets.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class HexUtils {
    public static String generatePassword(String password) {
        String md5Str = getMD5(password);
        if (md5Str != null) {
            return md5Str.substring(0, 16).toLowerCase();
        }

        // 如果生成出错，则使用 p@ssw0rd
        return "0f359740bd1cda99";
    }

    public static String getMD5(String str) {
        // 生成一个MD5加密计算摘要
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return toHexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String res = formatter.toString();
        formatter.close();
        return res;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        byte[] classBytes;
        classBytes = new byte[in.available()];
        in.read(classBytes);
        in.close();
        return classBytes;
    }

    public static String bytesToHexString(byte[] bArray, int length) {
        StringBuffer sb = new StringBuffer(length);

        for (int i = 0; i < length; ++i) {
            String sTemp = Integer.toHexString(255 & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }

            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

}
