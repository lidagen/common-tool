## 注意事项
### common-redis
+ 该模块引入了bloom过滤器以及分布式锁
+ 引用redis包，需要引用spring-boot-starter-aop、redssion
### common-req 
+ 提供通用的用户登陆验证功能，对请求和响应做出拦截以及数据处理
