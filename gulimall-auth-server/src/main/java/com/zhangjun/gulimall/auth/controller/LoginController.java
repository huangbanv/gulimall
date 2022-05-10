package com.zhangjun.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.zhangjun.common.constant.AuthServerConstant;
import com.zhangjun.common.exception.BizCodeEnume;
import com.zhangjun.common.utils.R;
import com.zhangjun.common.vo.MemberRespVo;
import com.zhangjun.gulimall.auth.feign.MemberFeignService;
import com.zhangjun.gulimall.auth.feign.ThirdPartyFeignService;
import com.zhangjun.gulimall.auth.vo.UserLoginVo;
import com.zhangjun.gulimall.auth.vo.UserRegisterVo;
import io.lettuce.core.codec.RedisCodec;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.ldap.HasControls;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-25 21:10
 */
@Controller
public class LoginController {

    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/mail/sendcode")
    public R sendCode(@RequestParam("receiver") String receiver){
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.MAIL_CODE_CACHE_PREFIX + receiver);
        if(!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis() - l < 60000){
                return R.error(BizCodeEnume.MAIL_CODE_EXCEPTION.getCode(),BizCodeEnume.MAIL_CODE_EXCEPTION.getMsg());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        redisTemplate.opsForValue().set(AuthServerConstant.MAIL_CODE_CACHE_PREFIX+receiver,code+"_"+System.currentTimeMillis(),10, TimeUnit.MINUTES);
        try {
            thirdPartyFeignService.sendMail(receiver, code);
        }catch (Exception e){
            System.out.println(e);;
        }
        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result,
                           RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            Map<String,String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);
            /**
             * forward:/reg.html转发，而路径映射默认是get方式，所以会报错
             * 但是使用reg转发刷新的时候会重复提交表单，使用需要使用重定向
             * 而重定向的时候用不了model，需要使用RedirectAttributes
             * 重定向携带数据是用了session的原理，只要跳到下一个页面取出了session
             * 后就会删除
             */
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.MAIL_CODE_CACHE_PREFIX + vo.getMail());
        if(!StringUtils.isEmpty(s)){
            if(code.equals(s.split("_")[0])){
                //令牌机制
                redisTemplate.delete(AuthServerConstant.MAIL_CODE_CACHE_PREFIX + vo.getMail());
                R r = memberFeignService.register(vo);
                if(r.getCode() == 0){
                    return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            }else {
                Map<String,String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }else {
            Map<String,String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes,
                        HttpSession session){
        R r = memberFeignService.login(vo);
        if(r.getCode() == 0){
            MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {});
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect:http://gulimall.com";
        }
        Map<String,String> errors = new HashMap<>();
        errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
        redirectAttributes.addFlashAttribute("errors",errors);
        return "redirect:http://auth.gulimall.com/login.html";


    }
}
