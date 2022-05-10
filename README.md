# gulimall
### 这是尚硅谷的谷粒商城项目，配置文件我全放在nacos里面了，等到时候做完会放出来
#### 开始学习于 2022‎年‎4‎月‎11‎日，‏‎18:12:49
#### 项目完成于 2022‎年‎5‎月‎10‎日，‏‎13:00:49
#### 本项目使用了人人代码生成器逆向生成了dao层的代码
#### 使用自定义R来用于各个微服务交互封装数据
#### 有多个微服务，分别是authserver第三方登录服务
#### cart购物车服务，coupon优惠券服务，gateway网关服务
#### member会员服务，order订单服务，product商品服务
#### search检索服务，seckill秒杀服务，thirdparty第三方存储服务与邮箱验证码服务
#### ware库存服务
##### 其中，authserver接入了gitee的开放服务，cart服务使用redis来存储购物车信息，未登录时的临时账户信息，已登录时账户信息
##### 由于前台服务并没有前后端分离，使用了thymeleaf来渲染页面，search服务使用了elasticsearch来检索商品
##### seckill使用定时任务从redis中取出定时任务的信息
##### thirdparty使用邮箱来发送邮件，并且使用了阿里云的oss服务来存储图片信息
##### 各个微服务中使用openfeign来互相调用，nacos来做配置中心，sentinel来做服务降级服务熔断，zipkin来可视化监控