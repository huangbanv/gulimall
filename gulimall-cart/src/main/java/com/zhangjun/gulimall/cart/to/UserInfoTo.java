package com.zhangjun.gulimall.cart.to;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-30 10:17
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;

    private boolean tempUser = false;
}
