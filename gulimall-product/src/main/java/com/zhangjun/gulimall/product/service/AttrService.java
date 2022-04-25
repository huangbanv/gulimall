package com.zhangjun.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.product.entity.AttrEntity;
import com.zhangjun.gulimall.product.vo.AttrGroupRelationVo;
import com.zhangjun.gulimall.product.vo.AttrRespVo;
import com.zhangjun.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * ??Ʒ?
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-11 21:26:01
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);


    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);


    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 根据attrId集合查出可被检索的所有attrid
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

