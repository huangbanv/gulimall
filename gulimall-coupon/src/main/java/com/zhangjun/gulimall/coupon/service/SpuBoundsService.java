package com.zhangjun.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * ??Ʒspu???????
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-12 11:01:56
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

