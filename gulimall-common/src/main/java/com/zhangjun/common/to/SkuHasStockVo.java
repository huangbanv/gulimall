package com.zhangjun.common.to;

import lombok.Data;


/**
 * @author 张钧
 * @Description
 * @create 2022-04-17 19:48
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
