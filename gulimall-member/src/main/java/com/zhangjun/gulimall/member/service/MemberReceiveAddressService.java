package com.zhangjun.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * ??Ա?ջ???ַ
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:12:18
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

