# Spring Cloud OAuth2 资源服务器CheckToken 源码解析

CheckToken的目的
当用户携带token 请求资源服务器的资源时, OAuth2AuthenticationProcessingFilter 拦截token，进行token 和userdetails 过程，把无状态的token 转化成用户信息。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527105613128.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpbmdqaWFuamlu,size_16,color_FFFFFF,t_70)

**详解**

1. OAuth2AuthenticationManager.authenticate()，filter执行判断的入口
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527105640926.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpbmdqaWFuamlu,size_16,color_FFFFFF,t_70)
2. 当用户携带token 去请求微服务模块，被资源服务器拦截调用RemoteTokenServices.loadAuthentication ,执行所谓的check-token过程。 源码如下
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527105656112.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpbmdqaWFuamlu,size_16,color_FFFFFF,t_70)
3. CheckToken 处理逻辑很简单，就是调用redisTokenStore 查询token的合法性，及其返回用户的部分信息 （username ）
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527105711849.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpbmdqaWFuamlu,size_16,color_FFFFFF,t_70)
4. 继续看 返回给 RemoteTokenServices.loadAuthentication 最后一句 tokenConverter.extractAuthentication 解析组装服务端返回的信息
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527105726855.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpbmdqaWFuamlu,size_16,color_FFFFFF,t_70)最重要的 userTokenConverter.extractAuthentication(map);
5. 最重要的一步，是否判断是否有userDetailsService实现，如果有 的话去查根据 返回的 username 查询一次全部的用户信息，没有实现直接返回username，这也是很多时候问的为什么只能查询到username 也就是 EnablePigxResourceServer.details true 和false 的区别。
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527105821217.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpbmdqaWFuamlu,size_16,color_FFFFFF,t_70)
6. 那根据的你问题，继续看 UerDetailsServiceImpl.loadUserByUsername 根据用户名去换取用户全部信息。
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527105834521.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2RpbmdqaWFuamlu,size_16,color_FFFFFF,t_70)