package com.zhangjun.gulimall.search.controller;

import com.zhangjun.common.exception.BizCodeEnume;
import com.zhangjun.common.to.es.SkuEsModel;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-17 20:39
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModel){
        boolean b = false;
        try {
            //是否有错
            b = productSaveService.productStatusUp(skuEsModel);
        }catch (Exception e){
            log.error("esc商品上架错误:{}",e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg());
        }
        if(b){
            return R.ok();
        }else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg());
        }

    }
}
