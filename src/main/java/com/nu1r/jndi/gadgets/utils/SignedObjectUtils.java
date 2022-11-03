package com.nu1r.jndi.gadgets.utils;

import java.io.Serializable;
import java.security.*;
import java.security.SignedObject;

/**
 * 二次反序列化
 *
 * @author nu1r
 */
public class SignedObjectUtils {
    public static SignedObject warpWithSignedObject(Serializable obj) throws Exception {
        KeyPairGenerator keyPairGenerator;
        keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(1024);
        KeyPair    keyPair       = keyPairGenerator.genKeyPair();
        PrivateKey privateKey    = keyPair.getPrivate();
        Signature  signingEngine = Signature.getInstance("DSA");
        return new java.security.SignedObject(obj, privateKey, signingEngine);
    }
}
