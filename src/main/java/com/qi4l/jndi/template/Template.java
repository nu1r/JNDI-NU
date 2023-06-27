package com.qi4l.jndi.template;

public interface Template {
    void generate();
    byte[] getBytes();
    void cache();
    String getClassName();
}
