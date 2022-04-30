package com.zhangjun.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-26 21:12
 */
@Data
public class MemberRegisterVo {
    private String userName;

    private String password;

    private String mail;
}
