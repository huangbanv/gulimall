package com.zhangjun.gulimall.order;

import com.zhangjun.gulimall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    OrderService orderService;
    @Test
    void contextLoads() {
        orderService.payOrder("123",1);
    }

}
