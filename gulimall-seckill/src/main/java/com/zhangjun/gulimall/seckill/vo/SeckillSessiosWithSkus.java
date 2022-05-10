package com.zhangjun.gulimall.seckill.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-08 13:45
 */
@Data
public class SeckillSessiosWithSkus {

    private Long id;

    private String name;

    private Date startTime;

    private Date endTime;

    private Integer status;

    private Date createTime;

    private List<SeckillSkuVo> relationSkus;
}
