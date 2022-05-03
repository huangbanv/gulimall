package com.zhangjun.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-02 10:26
 */
@Data
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;
    private String orderToken;
    private BigDecimal payPrice;
    private String note;
}
