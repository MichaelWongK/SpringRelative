package com.micheal.mysql.springbootmysqlmasterslave.aop;

import com.micheal.mysql.springbootmysqlmasterslave.config.datasource.DBContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/9/1 23:15
 * @Description
 */
@Aspect
@Component
public class DataSourceAop {

    @Pointcut("!@annotation(com.micheal.mysql.springbootmysqlmasterslave.annotation.Master) " +
            "&& (execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.select*(..)) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.get*(..)))")
    public void readPointcut() {

    }

    @Pointcut("@annotation(com.micheal.mysql.springbootmysqlmasterslave.annotation.Master) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.insert*(..)) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.add*(..)) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.save*(..)) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.update*(..)) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.edit*(..)) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.delete*(..)) " +
            "|| execution(* com.micheal.mysql.springbootmysqlmasterslave.service..*.remove*(..))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
        DBContextHolder.slave();
    }

    @Before("writePointcut()")
    public void write() {
        DBContextHolder.master();
    }


    /**
     * 另一种写法：if...else...  判断哪些需要读从数据库，其余的走主数据库
     */
//    @Before("execution(* com.cjs.example.service.impl.*.*(..))")
//    public void before(JoinPoint jp) {
//        String methodName = jp.getSignature().getName();
//
//        if (StringUtils.startsWithAny(methodName, "get", "select", "find")) {
//            DBContextHolder.slave();
//        }else {
//            DBContextHolder.master();
//        }
//    }
}
