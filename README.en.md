<p align="center">
  <img src="docs/img/logo.png" width="120">
</p>
<h1 align="center"> JYso </h1>

<p align="center">
<img src="https://img.shields.io/badge/JDK-1.8+-orange" />
<img src="https://img.shields.io/badge/gradle-8.7-blue" />
<img src="https://img.shields.io/badge/SDL-Groovy-green" />

<p align="center"> It can be used as a tool for ysoserial and JNDIExploit at the same time, and has the bypass function of multiple JNDI high versions, WAF, and RASP </p>

## 🚀 Getting Started Guide

📢 Please take a moment to read this document, it will help you quickly get familiar with JYso!

🧐 Use the Documentation [Wiki](https://github.com/qi4L/JYso/wiki).

✔ Download the latest version of [Releases](https://github.com/qi4L/JYso/releases).

## 👍 Features

+ JNDI account password startup
+ JNDI route hiding or encryption
+ JNDI high version Bypass
+ Customize the path, password, HTTP header and value of the memory horse
+ Memory horse supports [Fileless landing Agent insertion](https://xz.aliyun.com/t/10075?time__1311=mq%2BxBD9QDQe4yDBkPoN%2BuDAO%3DnB5x&alichlgref=https%3A%2F%2Fxz.aliyun.com%2Fsearch%3Fkeyword%3Drebeyond)
+ Memory horse writes JRE or environment variables to hide
+ Serialized data plus dirty data
+ [Serialized data is encoded in UTF-8 corresponding to 3 bytes](https://whoopsunix.com/docs/PPPYSO/advance/UTFMIX/)
+ TemplatesImpl _bytecodes feature eliminated and size reduced
+ SignedObject secondary deserialization, can be used to bypass TemplatesImpl blacklist, CC without array and blacklist often seen in CTF, etc.
+ Solve the problem of Shiro Header being too long, get the value of the specified parameter from the request for class loading
+ Dynamically generate obfuscated class names
+ MSF/CS online
+ Code execution through JDBC

If you have other great ideas, please let me know! 😎

## 🐯 Compile

Download gradle8.7+ and configure it in the global environment variable, and execute it in the project root directory

```shell
./gradlew shadowJar
```

## 🌲Directory structure

For more information, please refer to [Directory structure description](docs/directory_structure.md).

## ✨ CTStack

<img src="https://ctstack-oss.oss-cn-beijing.aliyuncs.com/CT%20Stack-2.png" width="30%" />

JYso has joined the [CTStack](https://stack.chaitin.com/tool/detail/1303) community

## ✨ 404StarLink 2.0 - Galaxy

JYso is a member of the 404Team [404StarLink 2.0](https://github.com/knownsec/404StarLink). If you have questions about JYso or want to find a partner to communicate, you can refer to the Starlink group project.

+ https://github.com/knownsec/404StarLink2.0-Galaxy#community

1. [入选2024年KCon兵器谱](https://kcon.knownsec.com/index.php?s=bqp&c=category&id=3)

## 📷 Acknowledgements

- https://github.com/veracode-research/rogue-jndi
- https://github.com/welk1n/JNDI-Injection-Exploit
- https://github.com/welk1n/JNDI-Injection-Bypass
- https://github.com/WhiteHSBG/JNDIExploit
- https://github.com/su18/ysoserial
- https://github.com/rebeyond/Behinder
- https://github.com/Whoopsunix/utf-8-overlong-encoding
- https://github.com/mbechler/marshalsec
- https://t.zsxq.com/17LkqCzk8
- https://mp.weixin.qq.com/s/fcuKNfLXiFxWrIYQPq7OCg
- https://xz.aliyun.com/t/11640?time__1311=mqmx0DBDuDnQ340vo4%2BxCwg%3DQai%3DYzaq4D&alichlgref=https%3A%2F%2Fxz.aliyun.com%2Fu%2F8697
- https://archive.conference.hitb.org/hitbsecconf2021sin/sessions/make-jdbc-attacks-brilliant-again/
- https://tttang.com/archive/1405/#toc_0x03-jdbc-rce
- https://xz.aliyun.com/t/10656?time__1311=mq%2BxBDy7G%3DLOD%2FD0DoYg0%3DDR0HG8KeD&alichlgref=https%3A%2F%2Ftttang.com%2F#toc-7
- https://whoopsunix.com/docs/PPPYSO/advance/UTFMIX/
- https://tttang.com/archive/1405/#toc_groovyclassloader
- https://xz.aliyun.com/t/10656?time__1311=mq%2BxBDy7G%3DLOD%2FD0DoY4AKqiKD%3DOQjqx&alichlgref=https%3A%2F%2Ftttang.com%2F
- https://www.leavesongs.com/PENETRATION/use-tls-proxy-to-exploit-ldaps.html
- https://tttang.com/archive/1405/#toc_druid

