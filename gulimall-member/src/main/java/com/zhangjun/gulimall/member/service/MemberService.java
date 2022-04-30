package com.zhangjun.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.member.entity.MemberEntity;
import com.zhangjun.gulimall.member.exception.MailExistException;
import com.zhangjun.gulimall.member.exception.UsernameExistException;
import com.zhangjun.gulimall.member.vo.MemberLoginVo;
import com.zhangjun.gulimall.member.vo.MemberRegisterVo;
import com.zhangjun.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * ??Ա
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:12:18
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo memberRegisterVo);

    void checkEmailUnique(String email) throws MailExistException;

    void checkUsernameUnique(String userName) throws UsernameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser vo);
}

