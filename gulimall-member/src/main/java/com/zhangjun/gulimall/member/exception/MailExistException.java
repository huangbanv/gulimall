package com.zhangjun.gulimall.member.exception;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-26 21:23
 */
public class MailExistException extends RuntimeException{
    public MailExistException() {
        super("邮箱已存在");
    }
}
