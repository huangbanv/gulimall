package com.zhangjun.gulimall.cart.controller;

import com.zhangjun.gulimall.cart.service.CartService;
import com.zhangjun.gulimall.cart.vo.Cart;
import com.zhangjun.gulimall.cart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-30 9:59
 */
@Controller
@Slf4j
public class CartController {

    @Autowired
    CartService cartService;

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }


    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("check")Integer check){
        cartService.checkItem(skuId,check);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        log.info("cart:{}",cart);
        model.addAttribute("cart",cart);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        //model.addAttribute("skuId",skuId);
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model){
        CartItem item = cartService.getCartItem(skuId);
        log.info("item:{}",item);
        model.addAttribute("item",item);
        return "success";
    }
}
