package com.zhangjun.gulimall.ware.vo;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-02 19:42
 */
@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}
