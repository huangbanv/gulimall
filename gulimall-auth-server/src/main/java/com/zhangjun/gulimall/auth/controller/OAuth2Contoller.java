package com.zhangjun.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhangjun.common.utils.HttpUtils;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.auth.feign.MemberFeignService;
import com.zhangjun.common.vo.MemberRespVo;
import com.zhangjun.gulimall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-28 21:25
 */
@Controller
public class OAuth2Contoller {

    @Value("${gitee.clientsecret}")
    private String client_secret;

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code")String code, RedirectAttributes redirectAttributes, HttpSession session) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("grant_type","authorization_code");
        map.put("code",code);
        map.put("client_id","59c63150088008dc611242fb170f1d11c19386cf6e030c5b14fddedf37be977c");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("client_secret",client_secret);
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", null, null, map);

        if(response.getStatusLine().getStatusCode() == 200){
            String postJson = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(postJson, SocialUser.class);
            HttpResponse response1 = HttpUtils.doGet("https://gitee.com", "/api/v5/user?access_token=" + socialUser.getAccess_token(), "get", null, null);
            System.out.println(response1);
            if(response1.getStatusLine().getStatusCode()  == 200){
                String getJson = EntityUtils.toString(response1.getEntity());
                String email = (String) JSON.parseObject(getJson).get("email");
                String id = String.valueOf( JSON.parseObject(getJson).get("id"));
                String name = (String) JSON.parseObject(getJson).get("name");
                socialUser.setId(id);
                socialUser.setEmail(email);
                socialUser.setName(name);
                R r = memberFeignService.oauthLogin(socialUser);
                if(r.getCode() == 0){
                    MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {});
                    session.setAttribute("loginUser",data);
                    if(data.getPassword()==null){
                        //第三方登录需要立即修改密码
                        return "redirect:http://gulimall.com";
                    }else {
                        return "redirect:http://gulimall.com";
                    }

                }else {
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg","第三方登录失败，请重试");
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.gulimall.com/login.html";
                }
            }else {
                Map<String,String> errors = new HashMap<>();
                errors.put("msg","第三方登录失败，请重试");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
