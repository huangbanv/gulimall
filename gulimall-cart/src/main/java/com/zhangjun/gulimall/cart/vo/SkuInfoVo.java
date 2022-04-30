package com.zhangjun.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-30 12:23
 */
@Data
public class SkuInfoVo {
    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * sku?
     */
    private String skuName;
    /**
     * sku???????
     */
    private String skuDesc;
    /**
     * ????????id
     */
    private Long catalogId;
    /**
     * Ʒ??id
     */
    private Long brandId;
    /**
     * Ĭ??ͼƬ
     */
    private String skuDefaultImg;
    /**
     * ???
     */
    private String skuTitle;
    /**
     * ?????
     */
    private String skuSubtitle;
    /**
     * ?۸
     */
    private BigDecimal price;
    /**
     * ?
     */
    private Long saleCount;

}
