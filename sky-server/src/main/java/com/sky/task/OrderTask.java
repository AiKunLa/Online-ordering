package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ")//每分钟
    public void processTimeoutOrder() {
        log.info("处理超时订单：{}", LocalDateTime.now());

        //查询有无超时订单
        //超时 -> 下单时间  < 当前时间 - 减去15分钟
        //order_time < 当前时间 - 减去15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndTime(Orders.PENDING_PAYMENT, time);

        //将超时订单取消
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);//取消订单
                orders.setCancelReason("订单超时");//取消原因
                orders.setCancelTime(LocalDateTime.now());//取消时间
                //更新
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直在派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理一直在派送中的订单: {}",LocalDateTime.now());
        //查询订单 处理上一天的订单
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList = orderMapper.getByStatusAndTime(Orders.DELIVERY_IN_PROGRESS, time);

        //将订单状态改为已完成
        if (ordersList != null && !ordersList.isEmpty()){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);//已完成
                //更新
                orderMapper.update(orders);
            }
        }
    }
}
