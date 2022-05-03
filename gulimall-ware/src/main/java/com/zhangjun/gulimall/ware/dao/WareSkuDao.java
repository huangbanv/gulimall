package com.zhangjun.gulimall.ware.dao;

import com.zhangjun.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:23:05
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {


    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    Long getSkuStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
