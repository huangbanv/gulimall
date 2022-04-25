package com.zhangjun.gulimall.search.service;


import com.zhangjun.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-17 20:41
 */
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModel) throws IOException;
}
