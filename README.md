# proxy是什么
`C/S`架构的内网穿透工具。在代理服务器部署`server`端，在本地部署`client`端。即可将`server`流量转发到本地。
1. 支持多服务注册
2. 支持鉴权

# 使用方法
参考配置文件`client.config`注释配置代理。一般情况使用只用配置`proxy`。
在代理服务器启动`ServerApplication`。
在本地启动`ClientApplication`。Done。

## 例子
默认配置文件转发30000端口到本地8080端口，转发30001端口到本地3306端口。
在本地启用`ServerApplication`、`ClientApplication`。命令行连接本地`Mysql`, mysql -h 127.0.0.1 -P 30001 -u root -p`。

# 原理图
