package com.micheal.springbootelasticsearch.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/13 11:17
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String img;
    private String price;
    private String title;
}
