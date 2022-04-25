package com.zhangjun.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.product.entity.AttrGroupEntity;
import com.zhangjun.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.zhangjun.gulimall.product.vo.SkuItemVo;
import com.zhangjun.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * ???Է??
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-11 21:26:01
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

