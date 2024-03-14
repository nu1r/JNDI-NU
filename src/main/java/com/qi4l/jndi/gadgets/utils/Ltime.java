package com.qi4l.jndi.gadgets.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ltime {
    //yyyy-MM-dd
    public static String getLocalTime() {
        Date       d   = new Date();
        DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(d);
    }
}
