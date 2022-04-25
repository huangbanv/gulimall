package com.zhangjun.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-24 16:13
 */
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValue;
}
