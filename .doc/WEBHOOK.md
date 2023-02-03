# Webhook 通知

```yaml

webhook:
  # 在更新成功时执行通知
  on-success:
    url: "https://api.example.com/{YOUR_KEY}/notify?=更新%(type)域名 %(domain) 完成，新的IP地址为 %(address)。" # 运营商给出的Webhook地址
  # 在更新失败时执行通知
  on-failed:
    url: "" # 若URL留空则代表不启用通知
  # 在无需变更时执行通知
  on-unchanged:
    url: "https://api.example.com/{YOUR_KEY}/notify?=失败" # 运营商给出的Webhook地址
```

## 请求类型

Webhook请求通过 `GET` 实现。

若针对每种类型配置了 `request` 的内容，则会采用POST的方式发送请求，否则采用GET的方式发送请求。

## 消息变量

在URL中使用时，请使用`%()`包裹变量名。

如 `%(domain)` 代表域名变量。

|   变量名   |        变量描述         |                    示例内容                    |
|:-------:|:-------------------:|:------------------------------------------:|
| domain  |       更新的目标域名       |          `www.example.net` / ...           |
|  type   |       更新的域名类型       |              `IPv4` / `IPv6`               |
| address |  获取到的当前地址(取决于域名类型)  | `223.5.5.5` / `7641:5e09:d869::ca48` / ... |
| message | 用于指代合成后的`message`配置 |                                            |

