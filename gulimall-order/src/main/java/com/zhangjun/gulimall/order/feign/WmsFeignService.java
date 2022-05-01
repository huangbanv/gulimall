package com.zhangjun.gulimall.order.feign;

import com.zhangjun.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 18:48
 */
@FeignClient("gulimall-ware")
public interface WmsFeignService {


    @PostMapping("/ware/waresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
}
