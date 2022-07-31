package com.nu1r.jndi.template;

public interface Template {
    void generate();
    byte[] getBytes();
    void cache();
    String getClassName();
}
