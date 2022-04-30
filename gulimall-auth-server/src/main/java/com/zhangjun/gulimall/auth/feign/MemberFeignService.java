package com.zhangjun.gulimall.auth.feign;

import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.auth.vo.SocialUser;
import com.zhangjun.gulimall.auth.vo.UserLoginVo;
import com.zhangjun.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-28 13:36
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    public R register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser vo);

}
