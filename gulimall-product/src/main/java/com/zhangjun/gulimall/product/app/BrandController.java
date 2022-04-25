package com.zhangjun.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zhangjun.common.valid.AddGroup;
import com.zhangjun.common.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.zhangjun.gulimall.product.entity.BrandEntity;
import com.zhangjun.gulimall.product.service.BrandService;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.R;


/**
 * Ʒ?
 *
 * @author zhangjun
 * @email 2328432115@qq.com
 * @date 2022-04-11 21:26:01
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }
    @GetMapping("/infos")
    public R brandsInfo(@RequestParam("brandIds") List<Long> brandIds){
        List<BrandEntity> brands = brandService.getBrandsByIds(brandIds);
        return R.ok().put("brands", brands);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(
            @Validated(UpdateGroup.class)
            @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }
    @RequestMapping("/update/status")
    public R updateStatus(
            @Validated(UpdateGroup.class)
            @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
