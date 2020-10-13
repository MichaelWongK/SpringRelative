package com.micheal.springbootelasticsearch.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/13 14:13
 * @Description 业务层
 */
public interface ContentService {

    /**
     * 解析数据存储至 es 索引中
     * @param keywords
     * @return
     * @throws Exception
     */
    Boolean parseContent(String keywords) throws Exception;

    /**
     * 实现搜索功能 分页
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException;
}
