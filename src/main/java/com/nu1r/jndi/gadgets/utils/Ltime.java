package com.nu1r.jndi.gadgets.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fusesource.jansi.Ansi.ansi;

public class Ltime {
    //yyyy-MM-dd
    public static String getLocalTime() {
        Date       d   = new Date();
        DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(d);
    }
}
