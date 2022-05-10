package com.zhangjun.gulimall.seckill.to;

import com.zhangjun.gulimall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-08 14:27
 */
@Data
public class SeckillSkuRedisTo {

    private Long promotionId;

    private Long promotionSessionId;

    private String randomCode;

    private Long skuId;

    private BigDecimal seckillPrice;

    private BigDecimal seckillCount;

    private BigDecimal seckillLimit;

    private Integer seckillSort;

    private SkuInfoVo skuInfo;

    private Long startTime;
    private Long endTime;
}
