package com.qi4l.jndi;

import com.qi4l.jndi.gadgets.Config.Config;

public class  Starter {
    public static void main(String[] args) throws Exception {
        Config.applyCmdArgs(args);
        LdapServer.start();
        HTTPServer.start();
        RMIServer.start();
    }
}
