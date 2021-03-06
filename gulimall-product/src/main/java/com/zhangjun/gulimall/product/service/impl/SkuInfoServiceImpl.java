package com.zhangjun.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.product.entity.SkuImagesEntity;
import com.zhangjun.gulimall.product.entity.SpuInfoDescEntity;
import com.zhangjun.gulimall.product.feign.SeckillFeignService;
import com.zhangjun.gulimall.product.service.*;
import com.zhangjun.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.Query;

import com.zhangjun.gulimall.product.dao.SkuInfoDao;
import com.zhangjun.gulimall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    SeckillFeignService seckillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("sku_id",key).or()
                        .like("sku_name",key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("catelog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max) ){
            try {
                if(new BigDecimal(max).compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public List<skuSimpleVo> listSimple() {
        List<SkuInfoEntity> list = this.list(null);
        List<skuSimpleVo> collect = list.stream().map(sku -> {
            skuSimpleVo skuSimpleVo = new skuSimpleVo();
            BeanUtils.copyProperties(sku, skuSimpleVo);
            return skuSimpleVo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        //????????????1
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //sku????????????  pms_sku_info
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);

        //????????????1?????????????????????1?????????
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((result) -> {
            //spu???????????????
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(result.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);

        //????????????1?????????????????????2?????????
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((result) -> {
            //spu?????????
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(result.getSpuId());
            skuItemVo.setDesc(spuInfoDescEntity);
        }, executor);

        //????????????1?????????????????????3?????????
        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((result) -> {
            //????????????
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(result.getSpuId(), result.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);

        //????????????2 ???1??????
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //sku????????????  pms_sku_images
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);

        CompletableFuture<Void> secKillFuture = CompletableFuture.runAsync(() -> {
            R skuSeckillInfo = seckillFeignService.getSkuSeckillInfo(skuId);
            if (skuSeckillInfo.getCode() == 0) {
                SeckillInfoVo seckillInfoData = skuSeckillInfo.getData(new TypeReference<SeckillInfoVo>() {
                });
                skuItemVo.setSeckillInfo(seckillInfoData);
            }
        }, executor);

        //??????????????????????????????
        CompletableFuture.allOf(saleAttrFuture,descFuture,baseAttrFuture,imagesFuture,secKillFuture).get();

        return skuItemVo;
    }

}