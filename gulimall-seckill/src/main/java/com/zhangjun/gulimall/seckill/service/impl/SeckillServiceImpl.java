package com.zhangjun.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.zhangjun.common.to.mq.SeckillOrderTo;
import com.zhangjun.common.utils.R;
import com.zhangjun.common.vo.MemberRespVo;
import com.zhangjun.gulimall.seckill.feign.CouponFeignService;
import com.zhangjun.gulimall.seckill.feign.ProductFeignService;
import com.zhangjun.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.zhangjun.gulimall.seckill.service.SeckillService;
import com.zhangjun.gulimall.seckill.to.SeckillSkuRedisTo;
import com.zhangjun.gulimall.seckill.vo.SeckillSessiosWithSkus;
import com.zhangjun.gulimall.seckill.vo.SeckillSkuVo;
import com.zhangjun.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-08 12:21
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";

    private final String SKU_STOCK_CACHE_SEMAPHORE = "seckill:stock:";

    @Override
    public void uploadSeckillSkuLatest3Days() {
        R r = couponFeignService.getLates3DaySession();
        log.info("data:{}", r.getData(new TypeReference<List<SeckillSessiosWithSkus>>() {
        }));
        if (r.getCode() == 0) {
            List<SeckillSessiosWithSkus> data = r.getData(new TypeReference<List<SeckillSessiosWithSkus>>() {
            });
            if(data!=null&&data.size()>0){
                saveSessionInfos(data);
                saveSessionSkuInfos(data);
            }
        }
    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        long time = System.currentTimeMillis();
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                long start = Long.parseLong(s[0]);
                long end = Long.parseLong(s[1]);
                System.out.println("start:" + start);
                System.out.println("time:" + time);
                System.out.println("end:" + end);
                if (time >= start && time <= end) {
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = hashOps.multiGet(range);
                    System.out.println(list);
                    if (list != null) {
                        return list.stream().map(item ->
                                        JSON.parseObject(item, SeckillSkuRedisTo.class))
                                .collect(Collectors.toList());
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    long time = System.currentTimeMillis();
                    if (!(time >= skuRedisTo.getStartTime() && time <= skuRedisTo.getEndTime())) {
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            SeckillSkuRedisTo redis = JSON.parseObject(json, SeckillSkuRedisTo.class);
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long time = System.currentTimeMillis();
            if (time >= startTime && time <= endTime) {
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionSessionId() + "_" + redis.getSkuId();
                if (randomCode.equals(key) && killId.equals(skuId)) {
                    if (num <= redis.getSeckillLimit().intValue()) {
                        String redisKey = respVo.getId() + "_" + skuId;
                        Boolean buy = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), endTime - time, TimeUnit.MILLISECONDS);
                        if (Boolean.TRUE.equals(buy)) {
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_CACHE_SEMAPHORE + randomCode);
                            boolean b = semaphore.tryAcquire(num);
                            if (b) {
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setOrderSn(timeId);
                                orderTo.setMemberId(respVo.getId());
                                orderTo.setNum(num);
                                orderTo.setSkuId(redis.getSkuId());
                                orderTo.setSeckillPrice(redis.getSeckillPrice());
                                orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);
                                return timeId;
                            } else {
                                return null;
                            }
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        return null;
    }

    private void saveSessionInfos(List<SeckillSessiosWithSkus> sessions) {
        sessions.forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessiosWithSkus> sessions) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        sessions.forEach(session -> {
            session.getRelationSkus().forEach(seckillSkuVo -> {
                String token = UUID.randomUUID().toString().replace("_", "");
                if (Boolean.FALSE.equals(hashOps.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString()))) {
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(skuInfo);
                    }
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    redisTo.setRandomCode(token);
                    String jsonString = JSON.toJSONString(redisTo);
                    hashOps.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), jsonString);
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_CACHE_SEMAPHORE + token);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
                }
            });
        });
    }
}
