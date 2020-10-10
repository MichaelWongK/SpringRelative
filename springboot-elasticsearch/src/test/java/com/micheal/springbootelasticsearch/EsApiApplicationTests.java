package com.micheal.springbootelasticsearch;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/1 0:03
 * @Description
 */
@SpringBootTest
public class EsApiApplicationTests {

    // 面向对象操作
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    // 测试索引的创建 request put micheal_index
    @Test
    void testCreateIndex() throws IOException {
        // 1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("micheal_index");
        // 2.客户端请求执行 IndicesClient, 请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }
}
