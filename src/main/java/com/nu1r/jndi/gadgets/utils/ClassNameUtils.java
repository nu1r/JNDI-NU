package com.nu1r.jndi.gadgets.utils;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassNameUtils {
    public static ClassLoader loader = new SuClassLoader();

    public static Set<String> set = null;

    /**
     * 生成一个咋一下不出来问题的，但是在用户实际环境不存在的类名
     * 本来想直接用哥斯拉的 txt，但估计特征都被搞完了，这里自实现一个方法
     * 因为 apache 基金会的开源项目非常多，几乎大多数项目都会用到，所以看到 org.apache 包名的类也不会惊讶
     * 这里的逻辑是，获取目前项目中所有 org.apache 包下的类名，随机取两个，第一个取前三个包名，第二个取后三个包名进行拼接
     *
     * @return 返回类型
     */
    public static String generateClassName() {
        if (set == null) {
            set = getClassSet("org.apache");
        }
        Object[] array = set.toArray();

        String name1 = array[(int) (Math.random() * array.length)].toString();
        String name2 = name1;

        while (name1.equals(name2)) {
            name2 = array[(int) (Math.random() * array.length)].toString();
        }

        // 获取第一个包的前三个包名
        name1 = name1.substring(0, name1.indexOf(".", 11));

        // 获取第二个包的后三个包名
        String temp = name2.substring(0, name2.lastIndexOf("."));
        temp = temp.substring(0, temp.lastIndexOf("."));
        temp = temp.substring(0, temp.lastIndexOf("."));
        name2 = name2.substring(temp.length());

        String newName = name1 + name2;

        if (set.contains(newName)) {
            return generateClassName();
        } else {
            return newName;
        }
    }

    public static Set<String> getClassSet(String packageName) {
        Set<String> classSet = new HashSet<>();
        try {
            Enumeration<URL> urls = loader.getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry     = jarEntries.nextElement();
                                    String   jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        if (!className.contains("$") && className.startsWith(packageName)) {
                                            classSet.add(className);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return classSet;
    }
}
