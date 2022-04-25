package com.zhangjun.gulimall.product.vo;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-15 14:41
 */
@Data
public class AttrRespVo extends AttrVo{

    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
