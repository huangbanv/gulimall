package com.zhangjun.gulimall.order.web;

import com.zhangjun.gulimall.order.service.OrderService;
import com.zhangjun.gulimall.order.vo.OrderConfirmVo;
import com.zhangjun.gulimall.order.vo.OrderSubmitVo;
import com.zhangjun.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.AbstractDocument;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 12:49
 */
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
        if(responseVo.getCode() == 1){
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        }else {
            String msg = "下单失败：";
            switch (responseVo.getCode()){
                case 0: msg+="订单信息过期，请刷新再提交";break;
                case 2: msg+="订单商品价格发生变化，请确认后再提交";break;
                case 3: msg+="库存锁定失败，商品库存不足";break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }

    @GetMapping("/payOrder")
    public String pay(@RequestParam("orderSn")String orderSn,@RequestParam("type")Integer type){
        orderService.payOrder(orderSn,type);
        return "redirect:http://member.gulimall.com/memberOrder.html";
    }
}
