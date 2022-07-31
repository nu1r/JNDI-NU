package com.nu1r.jndi.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StringUtil {

    public static String getClassName(String url) {
        return url.substring(0, url.length() - 1);
    }

    public static String getVersion(String url) {
        return url.substring(url.length() - 1);
    }
}
