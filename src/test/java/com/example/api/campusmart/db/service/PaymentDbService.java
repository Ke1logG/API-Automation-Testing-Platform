package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.campusmart.db.entity.Payment;

/**
 * 支付单数据库操作服务
 */
public interface PaymentDbService extends IService<Payment> {

    /**
     * 模拟支付成功：将订单及对应支付单状态更新为 PAID
     *
     * @param orderId 订单 ID
     * @return 是否更新成功
     */
    boolean simulatePaid(Long orderId);
}
