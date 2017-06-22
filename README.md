# little_rpc
by haoyifen on 2017/6/1 17:33

## 来源 
源于以下两篇文章

一个轻量级分布式RPC框架--NettyRpc

http://www.cnblogs.com/luxiaoxun/p/5272384.html

轻量级分布式 RPC 框架

https://my.oschina.net/huangyong/blog/361751
## 改进
### 1. 协程
在他们的基础之上, 将基于线程的调度方式改成了协程. 将模块划分开. RPC_Common模块拥有所有的服务注册, 服务扫描, 服务获取功能, 具体的业务模块将不与这些代码耦合, 使用Spring Boot的自动配置来配置这些功能. 
### 2.模块划分以及Spring Boot自动配置
使用自定义注解@EnableServiceRegistry来启动服务端的配置和注册.
使用自定义注解@EnableServiceDiscovery来启动客户端的服务发现和连接管理功能.

范例可以见little_rpc_server模块和little_rpc_client模块.

## 性能对比
以下测试都是基于4核i7二代笔记本CPU, 2.2G, Win10的测试. 且服务端和客户端运行于同一台笔记本上.


little_rpc因为使用了协程, 能够在使用8个线程的情况下, 达到8K-1W rqs/s的速度, 而无论服务端的阻塞时间多长.

而上面文章中的Netty_RPC使用的是线程进行调用. 当服务端不阻塞, 马上返回, Netty_RPC也能到达8K-1W rqs/s的速度, 但是当服务端阻塞时间很长时, 客户端调用线程也将阻塞.
如果服务端的调用延时为t ms, 客户端的线程数为N.
那么客户端的rqs大致为N*1000/t 每秒, 当t很大时, rqs会迅速衰减. 
客户端8个线程, 服务端的调用耗时10ms时, rqs会降为700-800.
