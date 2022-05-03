package com.zhangjun.gulimall.order.feign;

import com.zhangjun.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-02 13:53
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {


    @GetMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id") Long id);

}
