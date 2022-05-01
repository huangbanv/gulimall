package com.zhangjun.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.order.entity.OrderEntity;
import com.zhangjun.gulimall.order.vo.OrderConfirmVo;

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
}

