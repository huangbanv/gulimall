package com.zhangjun.gulimall.member.dao;

import com.zhangjun.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ??Ա?ȼ?
 * 
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:12:18
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {


    MemberLevelEntity getDefaultLevel();
}
