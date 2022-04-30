package com.zhangjun.gulimall.member.exception;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-26 21:23
 */
public class UsernameExistException  extends RuntimeException{
    public UsernameExistException() {
        super("用户名已存在");
    }
}
