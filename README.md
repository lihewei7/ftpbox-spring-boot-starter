# FTPBox

![](https://gitee.com/lihewei7/img/raw/master/images/ftp003.jpg) 

>**Github：** https://github.com/lihewei7/ftpbox-spring-boot-starter
>
>**Gitee：** https://gitee.com/lihewei7/ftpbox-spring-boot-starter

## FTPBox是什么？

FTPBox 是一个基于 FTP协议的 SpringBoot Starter，使用池技术管理FTP连接，避免频繁创建新连接造成连接耗时问题。提供和 RedisTemplate 一样优雅的 SftpTemplate。主要包含了：文件上传、下载、校验、查看等功能，为用户提供了一种安全的方式来发送和接收文件和文件夹。

## Maven依赖

- FTPBox 已上传至 Maven 中央仓库，在工程中导入依赖即可使用


```xml
<dependency>
    <groupId>io.github.lihewei7</groupId>
    <artifactId>ftpbox-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

| 毕业版本   | 描述                     |
| ------ | ---------------------- |
| v1.0.0 | Version initialization |
| v1.0.1 | Version optimization   |



## 配置

### 单主机配置

- ftp基本配置（密码登录）

```yaml
ftp:
  enabled-log: false
  host: localhost
  port: 22
  username: root
  password: 1234
```

- sftp基本配置（密钥登录）

```yaml
ftp:
  enabled-log: false
  host: 10.1.61.118
  port: 19222
  username: lihw
  check-to-host-key: true
  key-path: /home/lihw/.ssh/id_rsa
  password: 生成密钥时的密码
  connect-timeout: 1500
```

- 连接池配置（可不配置使用默认值）

```yaml
ftp:
  pool:
    min-idle: 1
    max-idle: 8
    max-active: 8
    max-wait: -1
    test-on-borrow: true
    test-on-return: false
    test-while-idle: true
    time-between-eviction-runs: 600000
    min-evictable-idle-time-millis: 1800000
```

### 多主机配置

在多 Host 使用  FtpTemplate 需要为 FTPBox 指定将要使用的主机，详细操作见下方API。hosts 下可配置多台主机。rd-1为主机名（sftp.hosts 下 map 中的 key 代表 hostName ，可自定义主机名）

- 多 host ，密码登录

```yaml
ftp:
  enabled-log: false
  hosts:
    rd-1:
      host: 127.0.0.1
      port: 22
      username: lihw
      password: 1234
    rd-2:
      host: 127.0.0.2
      port: 22
      username: lihw
      password: 1234
```

- 多 host ，密码 + 密钥登录方式

```yaml
ftp:
  enabled-log: false
  hosts:
    rd-118:
      host: 10.1.61.118
      port: 19222
      username: lihw
      password: 1234
      connect-timeout: 1500
    rd-118:
      host: 10.1.61.119
      port: 19222
      username: lihw
      check-to-host-key: true
      key-path: /home/lihw/.ssh/id_rsa
      password: 生成密钥时设置的密码
      connect-timeout: 1500
```

- 多 Host 连接池配置（可不配置使用默认值）

```yaml
ftp:
  pool:
    min-idle-per-key: 1
    max-idle-per-key: 8
    max-active-per-key: 8
    max-active: 8
    max-wait: -1
    test-on-borrow: true
    test-on-return: false
    test-while-idle: true
    time-between-eviction-runs: 600000
    min-evictable-idle-time-millis: 1800000
```

## 使用

FTPBox 提供 FtpTemplate 类，它与 `spring-boot-starter-data-redis` 提供的 RedisTemplate 使用方法相同，任意方式注入即可使用：

1. 导入 FTPBox 依赖
2. 配置服务器（`源服务器`和`目标服务器`)
3. 查看API
4. 按需使用

```java
@Component
public class XXXService {
  
  @Autowired
  private FtpTemplate ftpTemplate;

  public void downloadFileWork(String from, String to) throws Exception {
    ftpTemplate.download(from, to);
  }
  
  public void uploadFileWork(String from, String to) throws Exception {
    ftpTemplate.upload(from, to);
  }
}
```

## API

​	所有方法都可能抛出 `Exception`，这通常代表连接出问题了，也可能是你上传或下载的文件不存在。sftp 操作可能会改变工作目录，因此在连接返回给池前，框架会重置工作目录为原始目录。注意这只会重置远端工作路径，不会重置本地工作路径。下面的介绍全部使用 `配置` 章节中的配置进行说明，因此初始工作目录是 `/root`。

### upload

上传文件，该方法会递归创建上传的远程文件所在的父目录。

```java
// 上传 D:\\a.docx 到 /home/easysftp/a.docx
ftpTemplate.upload("D:\\a.docx", "/home/easysftp/aptx4869.docx");

// 上传 D:\\a.pdf 到 /root/easysftp/a.pdf
ftpTemplate.upload("D:\\a.pdf", "easysftp/a.pdf");

// 上传 D:\\a.doc 到 /root/a.doc
ftpTemplate.upload("D:\\a.doc", "a.doc");
```

### download

下载文件，该方法只会创建下载的本地文件，不会创建本地文件的父目录。

```java
// 下载 /home/easysftp/b.docx 到 D:\\b.docx
ftpTemplate.download("/home/easysftp/b.docx", "D:\\b.docx");

// 下载 /root/easysftp/b.pdf 到 D:\\b.pdf
ftpTemplate.download("easysftp/b.pdf", "D:\\b.pdf");

// 下载 /root/b.doc 到 D:\\b.doc
ftpTemplate.download("b.doc", "D:\\b.doc");
```

### exists

校验文件是否存在，存在返回true，不存在返回false

```java
// 测试 /home/easysftp/c.docx 是否存在
boolean result1 = ftpTemplate.exists("/home/easysftp/c.pdf");
// 测试 /root/easysftp/c.docx 是否存在
boolean result2 = ftpTemplate.exists("easysftp/c.docx");
// 测试 /root/c.docx 是否存在
boolean result3 = ftpTemplate.exists("c.doc");
```

### list

查看文件/目录

```java
// 查看文件 /home/easysftp/d.pdf
LsEntry[] list1 = ftpTemplate.list("/home/easysftp/d.pdf");
// 查看文件 /root/easysftp/d.docx
LsEntry[] list2 = ftpTemplate.list("easysftp/d.docx");
// 查看文件 /root/d.doc
LsEntry[] list3 = ftpTemplate.list("d.doc");

// 查看目录 /home/easysftp
LsEntry[] list4 = ftpTemplate.list("/home/easysftp");
// 查看目录 /root/easysftp
LsEntry[] list5 = ftpTemplate.list("easysftp");
```

### execute

`execute(SftpCallback<T> action)` 用于执行自定义 SFTP 操作，比如查看 SFTP 默认目录（关于 ChannelSftp 的其他用法请参考 jsch 的 API）：[JSch - Java实现的SFTP（文件上传下载） - lihewei - 博客园 (cnblogs.com)](https://www.cnblogs.com/lihw/p/17168705.html)

```java
String dir = ftpTemplate.execute(ChannelSftp::pwd);
//或
String dir2 = ftpTemplate.execute(ChannelSftp -> pwd());
```

### executeWithoutResult

`executeWithoutResult(SftpCallbackWithoutResult action)`用于执行自定义没有返回值的SFTP操作，比如查看默认的SFTP目录（ChannelSftp的其他用途，请参考 jsch 的 API）

```java
ftpTemplate.executeWithoutResult(channelSftp -> System.out.println(channelSftp.getHome()));
```

### 多Host

- `HostsManage.changeHost(string)` ：通过 hostName 指定下次使用的连接。注意它只能指定下一次的连接！！！

```java
HostsManage.changeHost("rd-1");
// 成功打印 rd-1 对应连接的原始目录
ftpTemplate.execute(ChannelSftp::pwd);
// 第二次执行失败，抛出空指针，需要再次指定对应连接才能继续使用
ftpTemplate.execute(ChannelSftp::pwd);
```

- `HostsManage.changeHost(string, boolean)`：连续使用相同 host 进行操作，避免执行一次 SftpTemplate 就要设置一次 hostName。注意要配合 `HostHolder.clearHost()` 使用！！！

```java
HostsManage.changeHost("rd-1", false);
try {
  ftpTemplate.upload("D:\\a.docx", "/home/easysftp/a.docx");
  ftpTemplate.upload("D:\\b.pdf", "easysftp/b.pdf");
  ftpTemplate.upload("D:\\c.doc", "c.doc");
} finally {
  HostsManage.clearHost();
}
```

- `HostsManage.hostNames()` 与 ：获取所有的 host 连接的 name

```java
//有时需要批量执行配置的 n 个 host 连接，此时可以通过该方法获取所有或过滤后的 hostName 集合。
for (String hostName : HostsManage.hostNames()) {
   HostsManage.changeHost(hostName);
   ftpTemplate.upload("D:\\a.docx", "/home/easysftp/a.docx");
}
```

- `HostsManage.hostNames(Predicate<String>)`：获取过滤后的 host 连接的 name

```java
// 获取所有以“rd-”开头的 hostName
for (String hostName : HostsManage.hostNames(s -> s.startsWith("rd-"))) {
  HostsManage.changeHost(hostName);
  ftpTemplate.upload("D:\\a.docx", "/home/easysftp/a.docx");
}
```

