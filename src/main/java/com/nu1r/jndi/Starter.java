package com.nu1r.jndi;

import com.nu1r.jndi.gadgets.Config.Config;
import com.nu1r.jndi.template.Meterpreter;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class  Starter {
    public static void main(String[] args) throws Exception {
        Config.applyCmdArgs(args);
        LdapServer.start();
        HTTPServer.start();
        RMIServer.start();
    }
}
