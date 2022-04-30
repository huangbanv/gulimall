package com.zhangjun.gulimall.auth.feign;

import com.zhangjun.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-26 11:01
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {

    @GetMapping("/mail/sendcode")
    void sendMail(@RequestParam("receiver")String receiver,@RequestParam("code")String code);
}
