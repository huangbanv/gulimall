package com.zhangjun.gulimall.order.to;

import com.zhangjun.gulimall.order.entity.OrderEntity;
import com.zhangjun.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-02 12:08
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
