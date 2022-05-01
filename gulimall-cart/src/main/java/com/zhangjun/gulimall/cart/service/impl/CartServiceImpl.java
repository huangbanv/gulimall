package com.zhangjun.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.cart.feign.ProductFeignService;
import com.zhangjun.gulimall.cart.interceptor.CartInterceptor;
import com.zhangjun.gulimall.cart.service.CartService;
import com.zhangjun.gulimall.cart.to.UserInfoTo;
import com.zhangjun.gulimall.cart.vo.Cart;
import com.zhangjun.gulimall.cart.vo.CartItem;
import com.zhangjun.gulimall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-30 9:56
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        log.info("res:{}",res);
        if(StringUtils.isEmpty(res)){
            CartItem cartItem = new CartItem();
            //异步任务1：查询sku信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                log.info("skuInfo:{}",data);
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setPrice(data.getPrice());
                cartItem.setSkuId(skuId);
                cartItem.setImage(data.getSkuDefaultImg());

            }, executor);

            //异步任务2：查询sku销售属性
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                R skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                List<String> stringList = skuSaleAttrValues.getData("stringList", new TypeReference<List<String>>() {
                });
                for (String s:
                     stringList) {
                    log.info("s:{}",s);
                }
                cartItem.setSkuAttr(stringList);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrValues).get();
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),s);

            return cartItem;
        }
        else {
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
            log.info("cartItem:{}",cartItem);
            return cartItem;
        }

    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        log.info("str:{}",str);
        return JSON.parseObject(str, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        log.info("userkey:{}",userInfoTo.getUserKey());
        String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
        List<CartItem> tempCartItems = getCartItems(tempCartKey);
        if(userInfoTo.getUserId()!=null){
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            if(tempCartItems!=null){
                for (CartItem item : tempCartItems) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                clearCart(tempCartKey);
            }
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }else {
            cart.setItems(tempCartItems);
        }
        return cart;
    }

    /**
     * 清空购物车
     * @param cartkey
     */
    @Override
    public void clearCart(String cartkey) {
        redisTemplate.delete(cartkey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String s = JSON.toJSONString(cartItem);
        getCartOps().put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        getCartOps().put(skuId.toString(),s);
    }

    @Override
    public void deleteItem(Long skuId) {
        getCartOps().delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId() == null){
            return null;
        }else {
            String s = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(s);
            return cartItems.stream().
                    filter(CartItem::getCheck).
                    map(item -> {
                        R r = productFeignService.getPrice(item.getSkuId());
                        BigDecimal price = r.getData(new TypeReference<BigDecimal>() {});
                        item.setPrice(price);
                        return item;
                    }).
                    collect(Collectors.toList());
        }
    }

    /**
     * 获取操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey ;
        if(userInfoTo.getUserId()!= null){
            cartKey = CART_PREFIX+userInfoTo.getUserId();
        }else {
            cartKey = CART_PREFIX+ userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        log.info("values:{}",values);
        if(values!=null && values.size()>0){
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItem item = JSON.parseObject(str, CartItem.class);
                return item;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }
}
