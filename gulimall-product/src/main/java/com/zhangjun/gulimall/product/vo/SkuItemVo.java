package com.zhangjun.gulimall.product.vo;

import com.zhangjun.gulimall.product.entity.SkuImagesEntity;
import com.zhangjun.gulimall.product.entity.SkuInfoEntity;
import com.zhangjun.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-24 15:07
 */
@Data
public class SkuItemVo {
    /**
     * sku基本信息  pms_sku_info
     */
    SkuInfoEntity info;
    /**
     * 是否有货
     */
    Boolean hasStock = true;
    /**
     * sku图片信息  pms_sku_images
     */
    List<SkuImagesEntity> images;
    /**
     * spu的销售属性
     */
    List<SkuItemSaleAttrVo> saleAttr;
    /**
     * spu的介绍
     */
    SpuInfoDescEntity desc;
    /**
     * 规格参数
     */
    List<SpuItemAttrGroupVo> groupAttrs;

    SeckillInfoVo seckillInfo;
}
