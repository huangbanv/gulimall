package com.zhangjun.common.exception;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-14 11:55
 */
public enum BizCodeEnume {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    MAIL_CODE_EXCEPTION(10002,"验证码获取频率太高，请稍后再说"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    MAIL_EXIST_EXCEPTION(15001,"邮箱存在异常"),
    USER_EXIST_EXCEPTION(15002,"用户存在异常"),
    NO_STOCK_EXCEPTION(21000,"商品库存不足"),
    LOGINACCT_PASSWORD_INVAILD_EXCEPTION(15003,"账号密码错误");
    private int code;
    private String msg;
    BizCodeEnume(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
