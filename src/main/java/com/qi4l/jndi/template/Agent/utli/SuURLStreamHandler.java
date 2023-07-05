package com.qi4l.jndi.template.Agent.utli;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SuURLStreamHandler {
    public static String URL_CONNECTION_CLASSNAME;

    private List files = new ArrayList();

    public SuURLStreamHandler() {
    }

    /**
     * 与 SuURLConnection 联动的 URLStreamHandler
     *
     * @param u 特殊的 URL 对象
     * @return 返回 SuURLConnection 对象
     * @throws IOException 抛出异常
     */
    protected URLConnection openConnection(URL u) throws IOException {
        try {
            Class       clazz       = Class.forName(URL_CONNECTION_CLASSNAME);
            Constructor constructor = clazz.getDeclaredConstructor(URL.class);
            constructor.setAccessible(true);
            return (URLConnection) constructor.newInstance(u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List getFiles() {
        return this.files;
    }
}
