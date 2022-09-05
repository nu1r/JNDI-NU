package com.nu1r.jndi;

import com.nu1r.jndi.utils.Config;

public class Starter {
    public static void main(String[] args) throws Exception {
        Config.applyCmdArgs(args);
        LdapServer.class.getMethod("start").invoke(LdapServer.class.newInstance());
        HTTPServer.class.getMethod("start").invoke(HTTPServer.class.newInstance());
        RMIServer.start();
    }
}
