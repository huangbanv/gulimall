package com.zhangjun.gulimall.auth.vo;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-29 13:51
 */
@Data
public class SocialUser {
        private String id;
        private String access_token;
        private String token_type;
        private String expires_in;
        private String refresh_token;
        private String scope;
        private String created_at;
        private String email;
        private String name;
}
