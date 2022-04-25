package com.zhangjun.gulimall.product.dao;

import com.zhangjun.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ??Ʒ?
 * 
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-11 21:26:01
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
