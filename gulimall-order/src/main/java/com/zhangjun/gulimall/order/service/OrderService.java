package com.zhangjun.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.to.mq.SeckillOrderTo;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.order.entity.OrderEntity;
import com.zhangjun.gulimall.order.vo.OrderConfirmVo;
import com.zhangjun.gulimall.order.vo.OrderSubmitVo;
import com.zhangjun.gulimall.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * ????
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:18:10
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity entity);

    void payOrder(String orderSn, Integer type);

    PageUtils queryPageWithItem(Map<String, Object> params);

    void createSeckillOrder(SeckillOrderTo seckillOrderTo);
}

