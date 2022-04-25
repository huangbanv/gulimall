package com.zhangjun.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.zhangjun.gulimall.product.entity.AttrEntity;
import com.zhangjun.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-16 9:09
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * ????id
     */
    private Long attrGroupId;
    /**
     * ???
     */
    private String attrGroupName;
    /**
     * ???
     */
    private Integer sort;
    /**
     * ???
     */
    private String descript;
    /**
     * ??ͼ?
     */
    private String icon;
    private Long catelogId;
    private List<AttrEntity> attrs;
}
