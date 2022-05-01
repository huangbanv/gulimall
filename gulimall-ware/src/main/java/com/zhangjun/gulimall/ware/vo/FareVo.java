package com.zhangjun.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 20:00
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
