package com.nu1r.jndi.template;

public class MyClassLoader extends ClassLoader {
    MyClassLoader(ClassLoader c){super(c);}


    public static Class defineClass(byte[] bytes, ClassLoader classLoader){
        return new MyClassLoader(classLoader).defineClass(bytes, 0, bytes.length);
    }
}
