package com.zhangjun.gulimall.ware.service.impl;

import com.zhangjun.common.exception.NoStockException;
import com.zhangjun.gulimall.ware.feign.ProductFeignService;
import com.zhangjun.gulimall.ware.vo.OrderItemVo;
import com.zhangjun.gulimall.ware.vo.SkuHasStockVo;
import com.zhangjun.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.Query;
import com.zhangjun.gulimall.ware.dao.WareSkuDao;
import com.zhangjun.gulimall.ware.entity.WareSkuEntity;
import com.zhangjun.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count==null?false:count>0);
            return vo;
        }).collect(Collectors.toList());
        return collect;

    }

    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());
        collect.forEach(hasStock -> {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if(wareIds == null || wareIds.size() == 0){
                throw new NoStockException(skuId);
            }
            for(Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getNum());
                if(count == 1){
                    skuStocked = true;
                    break;
                }else {

                }
            }
            if(!skuStocked){
                throw new NoStockException(skuId);
            }
        });
        return true;
    }
    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}