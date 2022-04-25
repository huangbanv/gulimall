package com.zhangjun.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zhangjun.gulimall.product.vo.skuSimpleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zhangjun.gulimall.product.entity.SkuInfoEntity;
import com.zhangjun.gulimall.product.service.SkuInfoService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.R;



/**
 * sku??Ϣ
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-11 21:26:01
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        System.out.println(params);
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    /**
     * TODO 随着商品增多，之后需要分页列出
     * @return
     */
    @GetMapping("/listsimple")
    public R listSimple(){
        List<skuSimpleVo> skus = skuInfoService.listSimple();

        return R.ok().put("skus",skus);
    }

}
