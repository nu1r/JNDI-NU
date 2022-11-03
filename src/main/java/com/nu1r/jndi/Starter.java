package com.nu1r.jndi;

import com.nu1r.jndi.gadgets.Config.Config;

public class  Starter {
    public static void main(String[] args) throws Exception {
        Config.applyCmdArgs(args);
        LdapServer.start();
        HTTPServer.start();
        RMIServer.start();
    }
}
