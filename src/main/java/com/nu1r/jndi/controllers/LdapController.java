package com.nu1r.jndi.controllers;

import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedActionTypeException;
import com.nu1r.jndi.exceptions.UnSupportedGadgetTypeException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;

public interface LdapController {
    void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception;
    void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException, UnSupportedGadgetTypeException, UnSupportedActionTypeException;
}
