# Spring Security Oauth2 认证（获取token/刷新token）流程（password模式）

# 1.本文介绍的认证流程范围

本文主要对从用户发起获取token的请求（/oauth/token），到请求结束返回token中间经过的几个关键点进行说明。

# 2.认证会用到的相关请求

注：所有请求均为post请求。

- **获取access_token请求（/oauth/token）**
  请求所需参数：client_id、client_secret、grant_type、username、password

```
http://localhost/oauth/token?client_id=demoClientId&client_secret=demoClientSecret&grant_type=password&username=demoUser&password=50575tyL86xp29O380t11
```

- **检查头肯是否有效请求（/oauth/check_token）**
  请求所需参数：token

```
http://localhost/oauth/check_token?token=f57ce129-2d4d-4bd7-1111-f31ccc69d4d11
```

- **刷新token请求（/oauth/token）**
  请求所需参数：grant_type、refresh_token、client_id、client_secret
  其中grant_type为固定值：grant_type=refresh_token

```
http://localhost/oauth/token?grant_type=refresh_token&refresh_token=fbde81ee-f419-42b1-1234-9191f1f95be9&client_id=demoClientId&client_secret=demoClientSecret1
```

# 2.认证核心流程

注：文中介绍的认证服务器端token存储在Reids，用户信息存储使用数据库，文中会包含相关的部分代码。

## 2.1.获取token的主要流程：

加粗内容为每一步的重点，不想细看的可以只看加粗内容：

1. 用户发起获取token的请求。
2. 过滤器会**验证path**是否是认证的请求/oauth/token，如果为false，则直接返回没有后续操作。
3. 过滤器通过clientId查询**生成一个Authentication对象**。
4. 然后会通过username和生成的Authentication对象**生成一个UserDetails对象**，并检查用户是否存在。
5. **以上全部通过会进入地址/oauth/token**，即TokenEndpoint的postAccessToken方法中。
6. postAccessToken方法中会**验证Scope**，然后**验证是否是refreshToken请求**等。
7. 之后调用AbstractTokenGranter中的grant方法。
8. grant方法中调用AbstractUserDetailsAuthenticationProvider的authenticate方法，**通过username和Authentication对象来检索用户是否存在**。
9. 然后通过DefaultTokenServices类**从tokenStore中获取OAuth2AccessToken对象**。
10. 然后将**OAuth2AccessToken对象包装进响应流返回**。

## 2.2.刷新token（refresh token）的流程

刷新token（refresh token）的流程与获取token的流程只有⑨有所区别：

- 获取token调用的是AbstractTokenGranter中的getAccessToken方法，然后调用tokenStore中的getAccessToken方法获取token。
- 刷新token调用的是RefreshTokenGranter中的getAccessToken方法，然后使用tokenStore中的refreshAccessToken方法获取token。

## 2.3.tokenStore的特点

tokenStore通常情况为自定义实现，一般放置在缓存或者数据库中。此处可以利用自定义tokenStore来实现多种需求，如：

- 同已用户每次获取token，获取到的都是同一个token，只有token失效后才会获取新token。
- 同一用户每次获取token都生成一个完成周期的token并且保证每次生成的token都能够使用（多点登录）。
- 同一用户每次获取token都保证只有最后一个token能够使用，之前的token都设为无效（单点token）。

# 3.获取token的详细流程（代码截图）

## 3.1.代码截图梳理流程

1.一个比较重要的过滤器
![这里写图片描述](https://img-blog.csdn.net/2018051122213623?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
2.此处是①中的attemptAuthentication方法
![这里写图片描述](https://img-blog.csdn.net/20180511222224707?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
3.此处是②中调用的authenticate方法
![这里写图片描述](https://img-blog.csdn.net/20180511222330493?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
4.此处是③中调用的AbstractUserDetailsAuthenticationProvider类的authenticate方法
![这里写图片描述](https://img-blog.csdn.net/20180511222451388?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
5.此处是④中调用的DaoAuthenticationProvider类的retrieveUser方法
![这里写图片描述](https://img-blog.csdn.net/20180511222706410?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
6.此处为⑤中调用的ClientDetailsUserDetailsService类的loadUserByUsername方法，执行完后接着返回执行④之后的方法
![这里写图片描述](https://img-blog.csdn.net/20180511222844956?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
7.此处为④中调用的DaoAuthenticationProvider类的additionalAuthenticationChecks方法，此处执行完则主要过滤器执行完毕，后续会进入/oauth/token映射的方法。
![这里写图片描述](https://img-blog.csdn.net/20180511222944638?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
8.此处进入/oauth/token映射的TokenEndpoint类的postAccessToken方法
![这里写图片描述](https://img-blog.csdn.net/20180511223311632?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
9.此处为⑧中调用的AbstractTokenGranter类的grant方法
![这里写图片描述](https://img-blog.csdn.net/20180511223622691?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
10.此处为⑨中调用的ResourceOwnerPasswordTokenGranter类中的getOAuth2Authentication方法
![这里写图片描述](https://img-blog.csdn.net/20180511223727144?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
11.此处为⑩中调用的自定义的CustomUserAuthenticationProvider类中的authenticate方法，此处校验用户密码是否正确，此处执行完则返回⑨执行后续方法。
![这里写图片描述](https://img-blog.csdn.net/20180511223854515?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
12.此处为⑨中调用的DefaultTokenServices中的createAccessToken方法
![这里写图片描述](https://img-blog.csdn.net/20180511224049265?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
13.此处为12中调用的RedisTokenStore中的getAccessToken方法等，此处执行完，则一直向上返回到⑧中执行后续方法。
![这里写图片描述](https://img-blog.csdn.net/20180511224258633?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
14.此处为⑧中获取到token后需要包装返回流操作
![这里写图片描述](https://img-blog.csdn.net/20180511224758841?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JsdXV1c2Vh/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

