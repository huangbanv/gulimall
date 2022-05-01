package com.zhangjun.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zhangjun.common.utils.R;
import com.zhangjun.common.vo.MemberRespVo;
import com.zhangjun.gulimall.order.feign.CartFeignService;
import com.zhangjun.gulimall.order.feign.MemberFeignService;
import com.zhangjun.gulimall.order.feign.WmsFeignService;
import com.zhangjun.gulimall.order.interceptor.LoginUserInterceptor;
import com.zhangjun.gulimall.order.vo.MemberAddressVo;
import com.zhangjun.gulimall.order.vo.OrderConfirmVo;
import com.zhangjun.gulimall.order.vo.OrderItemVo;
import com.zhangjun.gulimall.order.vo.SkuStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.Query;

import com.zhangjun.gulimall.order.dao.OrderDao;
import com.zhangjun.gulimall.order.entity.OrderEntity;
import com.zhangjun.gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            orderConfirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
            orderConfirmVo.setItems(currentUserCartItems);
        }, executor).thenRunAsync(()->{
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = wmsFeignService.getSkusHasStock(collect);
            List<SkuStockVo> data = r.getData(new TypeReference<List<SkuStockVo>>() {});
            if(data != null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                orderConfirmVo.setStocks(map);
            }
        });


        Integer integration = memberRespVo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        //TODO 放重令牌
        CompletableFuture.allOf(addressFuture,cartFuture).get();
        return orderConfirmVo;
    }

}