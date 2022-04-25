package com.zhangjun.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 *  封装页面可能传来的所有查询条件
 * @author 张钧
 * @Description
 * @create 2022-04-20 19:20
 */
@Data
public class SearchParam {
    private String keyword;
    private Long catalog3Id;
    private String sort;
    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandId;
    private List<String> attrs;
    private Integer pageNum = 1;
    private String queryString;
}
