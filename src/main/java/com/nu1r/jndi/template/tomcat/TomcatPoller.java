package com.nu1r.jndi.template.tomcat;

import org.apache.tomcat.util.net.NioEndpoint;

import javax.websocket.MessageHandler;
import java.io.IOException;

public class TomcatPoller extends NioEndpoint implements MessageHandler.Whole<String> {

    static {

    }


    public TomcatPoller() throws IOException {
    }

    @Override
    public void onMessage(String s) {

    }
}
