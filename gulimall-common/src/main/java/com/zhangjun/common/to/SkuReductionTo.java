package com.zhangjun.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-16 15:13
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
