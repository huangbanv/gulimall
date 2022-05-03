package com.zhangjun.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-02 18:15
 */
@Data
public class WareSkuLockVo {
    private String orderSN;
    private List<OrderItemVo> locks;
}
