package com.qi4l.jndi.gadgets.utils;

public class StringUtil {

    public static String getClassName(String url) {
        return url.substring(0, url.length() - 1);
    }

    public static String getVersion(String url) {
        return url.substring(url.length() - 1);
    }
}
