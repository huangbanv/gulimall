package com.zhangjun.gulimall.order.vo;

import lombok.Data;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-01 13:57
 */
@Data
public class MemberAddressVo {
    private Long id;
    /**
     * member_id
     */
    private Long memberId;
    /**
     * ?ջ??????
     */
    private String name;
    /**
     * ?绰
     */
    private String phone;
    /**
     * ???????
     */
    private String postCode;
    /**
     * ʡ??/ֱϽ?
     */
    private String province;
    /**
     * ???
     */
    private String city;
    /**
     * ?
     */
    private String region;
    /**
     * ??ϸ??ַ(?ֵ?)
     */
    private String detailAddress;
    /**
     * ʡ?????
     */
    private String areacode;
    /**
     * ?Ƿ?Ĭ?
     */
    private Integer defaultStatus;
}
