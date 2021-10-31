# 阿里云DDNS更新器

![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/aliddns-updater)
[![License](https://img.shields.io/github/license/CarmJos/aliddns-updater)](https://opensource.org/licenses/GPL-3.0)
![](https://visitor-badge.glitch.me/badge?page_id=aliddns-updater.readme)

阿里云服务，域名DDNS更新器，支持IPV6。

## 使用方法

下载后，放到单独的文件夹内，在控制台中执行以下命令即可运行。
```shell
java -jar aliddns-updater-<version>.jar
```

## 配置文件示例

```yaml
version: 1.7

#输出域名记录查询返回信息
debug: false

Service:
  #更新间隔(毫秒)
  period: 900000
  # 阿里云地域ID，可以不改动
  # 请参考 https://help.aliyun.com/knowledge_detail/40654.html
  region-id: "cn-hangzhou"
  # IP地址查询相关设定
  # 用于获取对应的IP地址，以更新到域名
  # 如不需要IPV6，则可以直接将地址留空。
  ipQuery:
    IPv4: "http://ifconfig.me/ip"
    IPv6: "https://v6.ip.zxinc.org/getip"


#更新任务列表
UpdateRequests:
  test:
    domain: "test.cn"  # 域名，如 www.baidu.com 中的 baidu.com
    AccessKey: "xx" # 访问密钥 (在个人控制台中获取)
    AccessSecret: "xx" # 访问密码 (在个人控制台中获取)
    record: "www" #主机记录
    ipv6: false # 该记录是否为IPv6[AAAA]记录 (默认为false)
```

## 守护进程示例

### Linux systemd

请参考 [systemd自启动java程序](https://www.cnblogs.com/yoyotl/p/8178363.html) 。


## 支持与捐赠

若您觉得本插件做的不错，您可以捐赠支持我！

感谢您成为开源项目的支持者！

<img height=25% width=25% src="https://raw.githubusercontent.com/CarmJos/CarmJos/main/img/donate-code.jpg" />

## 开源协议

本项目源码采用 [GNU General Public License v3.0](https://opensource.org/licenses/GPL-3.0) 开源协议。
> ### 关于 GPL 协议
> GNU General Public Licence (GPL) 有可能是开源界最常用的许可模式。GPL 保证了所有开发者的权利，同时为使用者提供了足够的复制，分发，修改的权利：
>
> #### 可自由复制
> 你可以将软件复制到你的电脑，你客户的电脑，或者任何地方。复制份数没有任何限制。
> #### 可自由分发
> 在你的网站提供下载，拷贝到U盘送人，或者将源代码打印出来从窗户扔出去（环保起见，请别这样做）。
> #### 可以用来盈利
> 你可以在分发软件的时候收费，但你必须在收费前向你的客户提供该软件的 GNU GPL 许可协议，以便让他们知道，他们可以从别的渠道免费得到这份软件，以及你收费的理由。
> #### 可自由修改
> 如果你想添加或删除某个功能，没问题，如果你想在别的项目中使用部分代码，也没问题，唯一的要求是，使用了这段代码的项目也必须使用 GPL 协议。
>
> 需要注意的是，分发的时候，需要明确提供源代码和二进制文件，另外，用于某些程序的某些协议有一些问题和限制，你可以看一下 @PierreJoye 写的 Practical Guide to GPL Compliance 一文。使用 GPL 协议，你必须在源代码代码中包含相应信息，以及协议本身。
>
> *以上文字来自 [五种开源协议GPL,LGPL,BSD,MIT,Apache](https://www.oschina.net/question/54100_9455) 。*
