package com.zhangjun.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zhangjun.common.exception.NoStockException;
import com.zhangjun.common.utils.R;
import com.zhangjun.common.vo.MemberRespVo;
import com.zhangjun.gulimall.order.constant.OrderConstant;
import com.zhangjun.gulimall.order.dao.OrderItemDao;
import com.zhangjun.gulimall.order.entity.OrderItemEntity;
import com.zhangjun.gulimall.order.feign.CartFeignService;
import com.zhangjun.gulimall.order.feign.MemberFeignService;
import com.zhangjun.gulimall.order.feign.ProductFeignService;
import com.zhangjun.gulimall.order.feign.WmsFeignService;
import com.zhangjun.gulimall.order.interceptor.LoginUserInterceptor;
import com.zhangjun.gulimall.order.service.OrderItemService;
import com.zhangjun.gulimall.order.to.OrderCreateTo;
import com.zhangjun.gulimall.order.vo.*;
import jdk.nashorn.internal.ir.CatchNode;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.Query;

import com.zhangjun.gulimall.order.dao.OrderDao;
import com.zhangjun.gulimall.order.entity.OrderEntity;
import com.zhangjun.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

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
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().
                set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);


        CompletableFuture.allOf(addressFuture,cartFuture).get();
        return orderConfirmVo;
    }

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(1);
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        //使用lua脚本保证查删原子性
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1])else return 0 end";
        String orderToken = vo.getOrderToken();
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()),
                orderToken);
        if(result == 0L){
            responseVo.setCode(0);
            return responseVo;
        }else {
            OrderCreateTo order = createOrder();
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                saveOrder(order);
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSN(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                R r = wmsFeignService.orderLockStock(lockVo);
                if(r.getCode() == 0){
                    responseVo.setOrder(order.getOrder());
                }else {
                    responseVo.setCode(3);
                    try {
                        throw new NoStockException(0L);
                    }catch (NoStockException e){

                    }
                }
            }else {
                responseVo.setCode(2);
            }
        }
        return responseVo;
    }

    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItem = order.getOrderItems();
        orderItemService.saveBatch(orderItem);
    }

    private OrderCreateTo createOrder(){
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        String orderSN = IdWorker.getTimeId();
        OrderEntity orderEntity = buildOrder(orderSN);
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSN);
        computePrice(orderEntity,itemEntities);
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(itemEntities);
        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal(("0.0"));
        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
            gift = gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth().toString()));
        }
        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);

        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0);
    }

    private OrderEntity buildOrder(String orderSN) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setMemberId(respVo.getId());
        entity.setOrderSn(orderSN);
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResponse = fare.getData(new TypeReference<FareVo>() {});
        entity.setFreightAmount(fareResponse.getFare());
        entity.setReceiverCity(fareResponse.getAddress().getCity());
        entity.setReceiverName(fareResponse.getAddress().getName());
        entity.setReceiverDetailAddress(fareResponse.getAddress().getDetailAddress());
        entity.setReceiverPhone(fareResponse.getAddress().getPhone());
        entity.setReceiverProvince(fareResponse.getAddress().getProvince());
        entity.setReceiverPostCode(fareResponse.getAddress().getPostCode());
        entity.setReceiverRegion(fareResponse.getAddress().getRegion());
        return entity;
    }

    private List<OrderItemEntity> buildOrderItems(String orderSN) {
        List<OrderItemVo> cartItems = cartFeignService.getCurrentUserCartItems();
        if(cartItems != null && cartItems.size() >0){
            return cartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSN);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();

        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {});
        itemEntity.setSkuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());

        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        itemEntity.setSkuAttrsVals(StringUtils
                .collectionToDelimitedString(cartItem.getSkuAttr(),";"));
        itemEntity.setSkuQuantity(cartItem.getCount());

        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = origin.subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);

        return itemEntity;
    }

}