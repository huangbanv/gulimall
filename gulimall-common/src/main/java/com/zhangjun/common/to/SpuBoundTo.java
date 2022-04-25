package com.zhangjun.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-16 14:44
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal bugBounds;
    private BigDecimal growBounds;
}
