package com.zhangjun.gulimall.thirdparty.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.thirdparty.component.MailComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-26 10:50
 */
@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    MailComponent mailComponent;

    @GetMapping("/sendcode")
    public void sendMail(@RequestParam("receiver") String receiver,@RequestParam("code") String code){
        System.out.println(receiver+"——————————"+code);
        mailComponent.sendMail(receiver,code);
    }
}
