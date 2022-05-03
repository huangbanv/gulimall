package com.zhangjun.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 14:00
 */
@Data
public class OrderItemVo {

    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    private BigDecimal weight;
}
