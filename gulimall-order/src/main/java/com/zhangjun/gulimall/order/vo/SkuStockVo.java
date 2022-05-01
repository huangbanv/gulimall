package com.zhangjun.gulimall.order.vo;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 18:51
 */
@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}
