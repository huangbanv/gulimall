package com.zhangjun.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-08 20:58
 */
@Data
public class SeckillInfoVo {
    private Long promotionId;

    private Long promotionSessionId;

    private String randomCode;

    private Long skuId;

    private BigDecimal seckillPrice;

    private BigDecimal seckillCount;

    private BigDecimal seckillLimit;

    private Integer seckillSort;


    private Long startTime;
    private Long endTime;
}
