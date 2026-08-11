package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.campusmart.db.entity.Order;
import com.example.api.campusmart.db.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
 * 订单数据库操作服务实现
 */
@Service
public class OrderDbServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderDbService {

    @Override
    public boolean updateStatusToPaid(Long orderId) {
        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus("PAID");
        return updateById(order);
    }
}
