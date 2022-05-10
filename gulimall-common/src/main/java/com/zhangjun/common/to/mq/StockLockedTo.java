package com.zhangjun.common.to.mq;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-03 20:52
 */
@Data
public class StockLockedTo {
    private Long id;
    private StockDetailTo detail;
}
