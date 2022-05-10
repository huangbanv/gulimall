package com.zhangjun.gulimall.seckill.service;

import com.zhangjun.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-08 12:21
 */
public interface SeckillService {
    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
