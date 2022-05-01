package com.zhangjun.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 10:47
 */
@Controller
public class HelloController {

    @GetMapping("/{page}.html")
    public String page(@PathVariable("page")String page){
        return page;
    }
}
