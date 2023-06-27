package com.qi4l.jndi.gadgets.utils.jre;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandleContainer {
    private Object handle;

    private static Method lookup;

    private static Method assign;

    public HandleContainer(Object handle) {
        this.handle = handle;
    }

    static {
        try {
            Class<?> cls = Class.forName("java.io.ObjectOutputStream$HandleTable");
            assign = cls.getDeclaredMethod("assign", new Class[]{Object.class});
            assign.setAccessible(true);
            lookup = cls.getDeclaredMethod("lookup", new Class[]{Object.class});
            lookup.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getHandle(Object obj) {
        try {
            return ((Integer) lookup.invoke(this.handle, new Object[]{obj})).intValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void putHandle(Object obj) {
        if (getHandle(obj) == -1)
            try {
                assign.invoke(this.handle, new Object[]{obj});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
    }
}
