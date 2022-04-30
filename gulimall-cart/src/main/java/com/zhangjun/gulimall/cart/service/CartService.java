package com.zhangjun.gulimall.cart.service;

import com.zhangjun.gulimall.cart.vo.Cart;
import com.zhangjun.gulimall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-30 9:56
 */
public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    void clearCart(String cartkey);

    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
