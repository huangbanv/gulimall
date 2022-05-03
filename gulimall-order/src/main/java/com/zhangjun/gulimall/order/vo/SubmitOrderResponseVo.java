package com.zhangjun.gulimall.order.vo;

import com.zhangjun.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-02 11:23
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;
}
