package com.zhangjun.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-24 16:12
 */
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
