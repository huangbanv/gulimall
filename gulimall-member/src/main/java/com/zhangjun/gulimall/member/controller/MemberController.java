package com.zhangjun.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zhangjun.common.exception.BizCodeEnume;
import com.zhangjun.gulimall.member.exception.MailExistException;
import com.zhangjun.gulimall.member.exception.UsernameExistException;
import com.zhangjun.gulimall.member.feign.CouponFeignService;
import com.zhangjun.gulimall.member.vo.MemberLoginVo;
import com.zhangjun.gulimall.member.vo.MemberRegisterVo;
import com.zhangjun.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zhangjun.gulimall.member.entity.MemberEntity;
import com.zhangjun.gulimall.member.service.MemberService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.R;



/**
 * ??Ա
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:12:18
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    CouponFeignService couponFeignService;

    @PostMapping("/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser vo){
        MemberEntity entity = memberService.login(vo);
        return R.ok().put("data",entity);
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo){
        try{
            memberService.register(memberRegisterVo);
        }catch (MailExistException e){
            return R.error(BizCodeEnume.MAIL_EXIST_EXCEPTION.getCode(), BizCodeEnume.MAIL_EXIST_EXCEPTION.getMsg());
        }catch (UsernameExistException e){
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){
        MemberEntity entity = memberService.login(vo);
        if(entity != null){
            return R.ok().setData(entity);
        }else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }
    }

    @RequestMapping("/coupons")
    public R test(){
        MemberEntity member = new MemberEntity();
        member.setNickname("王五");
        R membercoupons = couponFeignService.membercoupons();
        return R.ok().put("member",member).put("coupons",membercoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
