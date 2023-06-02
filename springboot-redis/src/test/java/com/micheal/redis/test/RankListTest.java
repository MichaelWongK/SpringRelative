package com.micheal.redis.test;

import com.micheal.redis.util.StringRedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/6/2 9:59
 * @Description
 */
@SpringBootTest
public class RankListTest {

    @Autowired
    private StringRedisUtils stringRedisUtils;

    @Test
    public void selectCompanyName() {
        stringRedisUtils.zAdd("company", "baidu", 9);
        Set<String> company = stringRedisUtils.zReverseRange("company", 0, 9);
        System.out.println(company.toString());
    }

    @Test
    void incrementScore() {
        Double aDouble = stringRedisUtils.zIncrementScore("company", "tsl", 1);
        System.out.println(aDouble);
    }

    @Test
    void batchAddZSet() {
        Set<ZSetOperations.TypedTuple<String>> temp = new HashSet<>();
        temp.add(new DefaultTypedTuple("zijie", Double.valueOf(21)));
        temp.add(new DefaultTypedTuple("ygdy", Double.valueOf(7)));
        temp.add(new DefaultTypedTuple("tencent", Double.valueOf(18)));
        temp.add(new DefaultTypedTuple("meituan", Double.valueOf(9)));
        temp.add(new DefaultTypedTuple("didi", Double.valueOf(6)));
        temp.add(new DefaultTypedTuple("xiecheng", Double.valueOf(9)));
        temp.add(new DefaultTypedTuple("xldz", Double.valueOf(1)));
        temp.add(new DefaultTypedTuple("nanrui", Double.valueOf(12)));
        temp.add(new DefaultTypedTuple("alibaba", Double.valueOf(19)));
        stringRedisUtils.zAdd("company", temp);
        System.out.println();
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     */
    @Test
    void test() {
        Set<ZSetOperations.TypedTuple<String>> company = stringRedisUtils.zReverseRangeWithScores("company", 0, 10);
        System.out.println(company.toString());
        System.out.println();
    }
}
