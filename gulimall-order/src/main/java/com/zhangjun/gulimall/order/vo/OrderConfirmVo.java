package com.zhangjun.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 13:57
 */
public class OrderConfirmVo {

    @Getter@Setter
    List<MemberAddressVo> address;
    @Getter@Setter
    List<OrderItemVo> items;
    @Getter@Setter
    Integer integration;
    @Getter@Setter
    Map<Long,Boolean> stocks;

    @Getter@Setter
    String orderToken;


    public Integer getCount() {
        Integer count = 0;
        if(items != null){
            for (OrderItemVo item: items) {
                count += item.getCount();
            }
        }
        return count;
    }
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if(items != null){
            for (OrderItemVo item: items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
