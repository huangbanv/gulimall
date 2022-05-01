package com.zhangjun.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.ware.feign.MemberFeignService;
import com.zhangjun.gulimall.ware.vo.FareVo;
import com.zhangjun.gulimall.ware.vo.MemberAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.Query;

import com.zhangjun.gulimall.ware.dao.WareInfoDao;
import com.zhangjun.gulimall.ware.entity.WareInfoEntity;
import com.zhangjun.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {});
        if(data != null){
            //此处可以加入第三方查询
            String substring = data.getPhone().substring(data.getPhone().length() - 1);
            fareVo.setAddress(data);
            fareVo.setFare(new BigDecimal(substring));
            return fareVo;
        }
        return null;
    }

}