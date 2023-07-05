package com.qi4l.jndi.template.Agent.utli;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SuURLConnection extends URLConnection {
    private static List FILES = new ArrayList();

    // 当前 SuURLConnection 对应的对象
    private final byte[] DATA;

    private final String contentType;

    public static String STREAM_HANDLER_CLASSNAME;

    static {
        try {
            Field fld;
            try {
                fld = URL.class.getDeclaredField("handlers");
            } catch (NoSuchFieldException var7) {
                try {
                    fld = URL.class.getDeclaredField("ph_cache");
                } catch (NoSuchFieldException var6) {
                    throw var7;
                }
            }

            fld.setAccessible(true);
            Map handlers = (Map) fld.get((Object) null);
            synchronized (handlers) {
                Object handler;
                if (handlers.containsKey("ysuserial")) {
                    handler = handlers.get("ysuserial");
                } else {
                    handler = Class.forName(STREAM_HANDLER_CLASSNAME).newInstance();
                    handlers.put("ysuserial", handler);
                }

                FILES = (List) handler.getClass().getMethod("getFiles").invoke(handler);
            }
        } catch (Exception var8) {
            throw new RuntimeException(var8.toString());
        }
    }


    /**
     * 将一个 URL 对象 （byte[]）存放在 FILES 中
     *
     * @param data        jar 包字节码数组
     * @param contentType 原本是文件路径，这里因为是虚拟的，所以随便写一个标识位就可以
     * @return 返回 URL 对象
     * @throws MalformedURLException 抛出异常
     */
    public static URL createURL(byte[] data, String contentType) throws MalformedURLException {
        synchronized (FILES) {
            FILES.add(data);
            return new URL("ysuserial", "", FILES.size() - 1 + "/" + contentType);
        }
    }

    /**
     * 构造方法，根据指定的 URL 格式将 DATA 进行指定赋值
     *
     * @param url
     */
    protected SuURLConnection(URL url) {
        super(url);
        String file = url.getFile();
        int    pos  = file.indexOf(47);
        synchronized (FILES) {
            this.DATA = (byte[]) ((byte[]) FILES.get(Integer.parseInt(file.substring(0, pos))));
        }

        this.contentType = file.substring(pos + 1);
    }

    public void connect() throws IOException {
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.DATA);
    }

    public int getContentLength() {
        return this.DATA.length;
    }

    public String getContentType() {
        return this.contentType;
    }
}
