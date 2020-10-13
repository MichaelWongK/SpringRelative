package com.micheal.springbootelasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.micheal.springbootelasticsearch.pojo.Content;
import com.micheal.springbootelasticsearch.service.ContentService;
import com.micheal.springbootelasticsearch.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/13 14:14
 * @Description
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public Boolean parseContent(String keywords) throws Exception {
        List<Content> contents = HtmlParseUtil.parseJD(keywords);
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2ms");

        contents.forEach(content -> bulkRequest.add(
                    new IndexRequest("jd_goods")
                    .source(JSON.toJSONString(content), XContentType.JSON)));

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("========================");
        System.out.println(bulk.hasFailures());
        return !bulk.hasFailures();
    }

    @Override
    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {
        if (pageNo < 1) {
            pageNo = 1;
        }

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        // 精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false); // 多个高亮显示关闭
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hits : searchResponse.getHits().getHits()) {

            // 解析高亮
            Map<String, HighlightField> highlightFields =   hits.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hits.getSourceAsMap();
            // 解析高粱关键字，将原來字段替换为高亮子段
            if (title != null) {
                Text[] fragments = title.getFragments();
                String resultTitle = "";
                for (Text text : fragments) {
                    resultTitle += text;
                }
                sourceAsMap.put("title", resultTitle);
            }

            list.add(sourceAsMap);
        }

        return list;
    }
}
