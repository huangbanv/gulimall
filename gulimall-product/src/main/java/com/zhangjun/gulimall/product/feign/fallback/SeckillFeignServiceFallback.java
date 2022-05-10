package com.zhangjun.gulimall.product.feign.fallback;

import com.zhangjun.common.exception.BizCodeEnume;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-10 9:10
 */
@Slf4j
@Component
public class SeckillFeignServiceFallback  implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("熔断保护");
        return R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
    }
}
