package com.zhangjun.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.to.SkuReductionTo;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * ??Ʒ??????Ϣ
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:01:56
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);

}

