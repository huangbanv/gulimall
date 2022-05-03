package com.zhangjun.common.exception;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-02 20:09
 */
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品id:"+skuId+"没有足够的库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
