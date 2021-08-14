## 学习性的东西，不具备生产级使用条件

1. common-api - rpc-common
    * 公共API
    * 通用工具
    * 配置
2. 序列化 - rpc-serializer
    * JSON
    * protostuff
    * kryo
    * hessian
    * fst
3. 服务的发布与引入 - rpc-ioc
    * spring
4. 服务的注册与发现 - rpc-registry
    * zookeeper
5. 通讯 - rpc-remoting
    * netty
6. 负载均衡 - rpc-cluster:loadBalance
    * 随机（加权）、轮洵（加权）、源地址哈希、最小连接数
7. http网关 - rpc-gateway
8. 服务管理 - rpc-admin
    * 服务状态监控
    * 服务状态日志
    * 权重调整
    * 管理分组
    * 服务升降级
    * 调用链路跟踪


## 技能准备
* zookeeper
* spring
* netty
* serializer
* logging

## 目标
* “约定优于配置”，尽量减少配置项；
* 注解，使用注解发布和引入服务
    * 基于spring
    * 自行实现
* 日志（slf4j + logback）
