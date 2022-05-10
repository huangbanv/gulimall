package com.zhangjun.gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zhangjun.common.to.mq.OrderTo;
import com.zhangjun.common.to.mq.StockDetailTo;
import com.zhangjun.common.to.mq.StockLockedTo;
import com.zhangjun.common.utils.R;
import com.zhangjun.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.zhangjun.gulimall.ware.entity.WareOrderTaskEntity;
import com.zhangjun.gulimall.ware.service.WareSkuService;
import com.zhangjun.gulimall.ware.vo.OrderVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author 张钧
 * @Description
 * @create 2022-05-04 10:10
 */
@RabbitListener(queues = "stock.release.stock.queue")
@Service
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            System.out.println("解锁库存出错"+e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

    @RabbitHandler
    public void handleOtherCloseRelease(OrderTo order,Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unlockStock(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            System.out.println("订单关闭解锁库存出错"+e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
