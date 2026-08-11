package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.campusmart.db.entity.Order;

/**
 * 订单数据库操作服务
 */
public interface OrderDbService extends IService<Order> {

    boolean updateStatusToPaid(Long orderId);
}
