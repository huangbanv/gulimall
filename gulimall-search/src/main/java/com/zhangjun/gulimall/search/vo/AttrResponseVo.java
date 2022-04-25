package com.zhangjun.gulimall.search.vo;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-23 21:32
 */
@Data
public class AttrResponseVo {
    private Long attrId;
    private String attrName;
    private Integer searchType;
    private String icon;
    private String valueSelect;
    private Integer attrType;
    private Long enable;
    private Long catelogId;
    private Integer showDesc;
    private Long attrGroupId;
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
