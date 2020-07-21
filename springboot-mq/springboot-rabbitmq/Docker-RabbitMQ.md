# 用docker部署RabbitMQ环境

前置条件：

已经安装好docker

1.docker search 搜索，默认下载标签为latest的镜像（无法打开web管理页面）

![image-20200721200450770](..\..\files\image-20200721200450770.png)

2.下载镜像（有时候网络问题超时，多尝试几次即可。我这里选择的是可以访问web管理界面的tag）

```
docker search rabbitmq
```

3.创建容器并运行（15672是管理界面的端口，5672是服务的端口。这里顺便将管理系统的用户名和密码设置为admin admin）

```
docker run -dit --name Myrabbitmq -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 15672:15672 -p 5672:5672 rabbitmq:management
```

输入ip:15672

![image-20200721201449748](D:\workspace\git\SpringRelative\files\image-20200721201449748.png)



输入账号密码登录即可















