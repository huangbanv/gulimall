package com.zhangjun.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-09 16:13
 */
@Data
public class SeckillOrderTo {

    private String orderSn;

    private Long promotionSessionId;

    private Long skuId;

    private BigDecimal seckillPrice;

    private Integer num;

    private Long memberId;
}
