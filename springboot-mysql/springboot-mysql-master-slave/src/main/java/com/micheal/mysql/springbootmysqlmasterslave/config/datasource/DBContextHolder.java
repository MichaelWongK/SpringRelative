package com.micheal.mysql.springbootmysqlmasterslave.config.datasource;

import com.micheal.mysql.springbootmysqlmasterslave.common.enums.DBTypeEnum;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/9/1 23:04
 * @Description 通过ThreadLocal将数据源设置到每个线程上下文中
 */
public class DBContextHolder {

    private static final ThreadLocal<DBTypeEnum> contextHolder = new ThreadLocal<>();

    private static final AtomicInteger counter = new AtomicInteger(-1);

    private static void set(DBTypeEnum dbType) {
        contextHolder.set(dbType);
    }

    public static DBTypeEnum get() {
        return contextHolder.get();
    }

    public static void master() {
        set(DBTypeEnum.MASTER);
        System.out.println("切换到master");
    }

    public static void slave() {
        set(DBTypeEnum.SLAVE1);
        System.out.println("切换到slave1");

//        //  多个slave 轮询 查询
//        int index = counter.getAndIncrement() % 2;
//        if (counter.get() > 9999) {
//            counter.set(-1);
//        }
//        if (index == 0) {
//            set(DBTypeEnum.SLAVE1);
//            System.out.println("切换到slave1");
//        }else {
//            set(DBTypeEnum.SLAVE2);
//            System.out.println("切换到slave2");
//        }
    }
}
