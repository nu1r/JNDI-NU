package com.nu1r.jndi.gadgets.utils;

public class SuClassLoader extends ClassLoader {

    public SuClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }
}
