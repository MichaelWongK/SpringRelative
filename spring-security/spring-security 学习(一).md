## Spring Security 解析(一) —— 授权过程

> 项目环境:
>
> - JDK1.8
> - Spring boot 2.x
> - Spring Security 5.x

### 一、 一个简单的Security Demo

#### 1、 自定义的UserDetailsService实现

  自定义MyUserDetailsUserService类，实现 UserDetailsService 接口的 loadUserByUsername()方法，这里就简单的返回一个Spring Security 提供的 User 对象。为了后面方便演示Spring Security 的权限控制，这里使用**AuthorityUtils.commaSeparatedStringToAuthorityList("admin")** 设置了user账号有一个admin的角色权限信息。实际项目中可以在这里通过访问数据库获取到用户及其角色、权限信息。

```
@Component
public class MyUserDetailsUserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 不能直接使用 创建 BCryptPasswordEncoder 对象来加密， 这种加密方式 没有 {bcrypt}  前缀，
        // 会导致在  matches 时导致获取不到加密的算法出现
        // java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"  问题
        // 问题原因是 Spring Security5 使用 DelegatingPasswordEncoder(委托)  替代 NoOpPasswordEncoder，
        // 并且 默认使用  BCryptPasswordEncoder 加密（注意 DelegatingPasswordEncoder 委托加密方法BCryptPasswordEncoder  加密前  添加了加密类型的前缀）  https://blog.csdn.net/alinyua/article/details/80219500
        return new User("user",  PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456"), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
复制代码
```

  注意Spring Security 5 开始没有使用 **NoOpPasswordEncoder**作为其默认的密码编码器，而是默认使用 **DelegatingPasswordEncoder** 作为其密码编码器，其 encode 方法是通过 密码编码器的名称作为前缀 + 委托各类密码编码器来实现encode的。

```
public String encode(CharSequence rawPassword) {
        return "{" + this.idForEncode + "}" + this.passwordEncoderForEncode.encode(rawPassword);
    }
复制代码
```

  这里的 idForEncode 就是密码编码器的简略名称，可以通过 **PasswordEncoderFactories.createDelegatingPasswordEncoder()** 内部实现看到默认是使用的前缀是 bcrypt 也就是 BCryptPasswordEncoder

```
public class PasswordEncoderFactories {
    public static PasswordEncoder createDelegatingPasswordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("ldap", new LdapShaPasswordEncoder());
        encoders.put("MD4", new Md4PasswordEncoder());
        encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
        encoders.put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
        encoders.put("sha256", new StandardPasswordEncoder());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }
}
复制代码
```

#### 2、 设置Spring Security配置

  定义SpringSecurityConfig 配置类，并继承**WebSecurityConfigurerAdapter**覆盖其configure(HttpSecurity http) 方法。

```
@Configuration
@EnableWebSecurity //1
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()  //2
            .and()
                .authorizeRequests() //3
                .antMatchers("/index","/").permitAll() //4
                .anyRequest().authenticated(); //6
    }
}
复制代码
```

配置解析：

- @EnableWebSecurity  查看其注解源码，主要是引用WebSecurityConfiguration.class 和 加入了@EnableGlobalAuthentication 注解 ，这里就不介绍了，我们只要明白添加 @EnableWebSecurity 注解将开启 Security 功能。
- formLogin()  使用表单登录（默认请求地址为 /login）,在Spring Security 5 里其实已经将旧版本默认的  httpBasic() 更换成 formLogin() 了，这里为了表明表单登录还是配置了一次。
- authorizeRequests() 开始请求权限配置
- antMatchers() 使用Ant风格的路径匹配，这里配置匹配 / 和 /index
- permitAll() 用户可任意访问
- anyRequest() 匹配所有路径
- authenticated() 用户登录后可访问

------

#### 3、 配置html 和测试接口

   在 resources/static 目录下新建 index.html ， 其内部定义一个访问测试接口的按钮

```
<!DOCTYPE html>
<html lang="en" >
<head>
    <meta charset="UTF-8">
    <title>欢迎</title>
</head>
<body>
        Spring Security 欢迎你！
        <p> <a href="/get_user/test">测试验证Security 权限控制</a></p>
</body>
</html>
```

  创建 rest 风格的获取用户信息接口

```
@RestController
public class TestController {

    @GetMapping("/get_user/{username}")
    public String getUser(@PathVariable  String username){
        return username;
    }
}
```

#### 4、 启动项目测试

1、访问 localhost:8080 无任何阻拦直接成功



![image](http://www.micheal.wang:10020/mongo/read/5f86a723b40e335bf4630397)



2、点击测试验证权限控制按钮 被重定向到了 Security默认的登录页面

![img](http://www.micheal.wang:10020/mongo/read/5f86a732b40e335bf4630399)



3、使用 MyUserDetailsUserService定义的默认账户 user : 123456 进行登录后成功跳转到 /get_user 接口



![img](http://www.micheal.wang:10020/mongo/read/5f86a747b40e335bf463039b)



