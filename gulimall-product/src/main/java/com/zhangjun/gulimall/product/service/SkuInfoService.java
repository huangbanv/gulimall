package com.zhangjun.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.product.entity.SkuInfoEntity;
import com.zhangjun.gulimall.product.vo.SkuItemVo;
import com.zhangjun.gulimall.product.vo.skuSimpleVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku??Ϣ
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-11 21:26:01
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    List<skuSimpleVo> listSimple();

    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

