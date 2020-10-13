package com.micheal.springbootelasticsearch.util;

import com.micheal.springbootelasticsearch.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/13 10:27
 * @Description 网页解析工具类
 */
public class HtmlParseUtil {

    public static void main(String[] args) throws IOException {
//        new HtmlParseUtil().parseJD("安卓").forEach(System.out::println);
    }

    public static List<Content> parseJD(String keyword) throws IOException {
        // 获取请求 https://search.jd.com/Search?keyword=java
        //
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        // 解析网页 (jsoup返回document对就是浏览器Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        // 所有js中使用的方法这里都能用
        Element element = document.getElementById("J_goodsList");
        // 获取所有的li元素
        Elements elements = element.getElementsByTag("li");

        List<Content> goodsList = new ArrayList<>();

        // 获取元素中的内容
        elements.forEach(el -> {
            // 图片特别多的网站，所有图片都是延迟加载
            // data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodsList.add(content);
        });

        return goodsList;
    }
}
