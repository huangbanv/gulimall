package com.zhangjun.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-30 9:39
 */
public class Cart {
    List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce = new BigDecimal("0");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if(items!=null&&items.size()>0){
            for (CartItem item:items){
                count+=item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if(items!=null&&items.size()>0){
            for (CartItem item:items){
                count+=item.getCount();
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if(items!=null&&items.size()>0){
            for (CartItem item:items){
                if(item.getCheck()){
                    amount = amount.add(item.getTotalPrice());
                }

            }
        }
        return amount.subtract(getReduce());
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
