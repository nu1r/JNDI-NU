package com.feihong.ldap.gadgets.utils;

public class SuClassLoader extends ClassLoader {

    public SuClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }
}
