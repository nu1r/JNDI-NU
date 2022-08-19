package com.nu1r.jndi.gadgets;

public interface ReleaseableObjectPayload<T> extends ObjectPayload<T> {

    void release(T obj) throws Exception;
}
