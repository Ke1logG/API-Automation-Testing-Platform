package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.campusmart.db.entity.Order;
import com.example.api.campusmart.db.entity.Payment;
import com.example.api.campusmart.db.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class PaymentDbServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentDbService {

    @Autowired
    private OrderDbService orderDbService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean simulatePaid(Long orderId) {
        Payment payment = lambdaQuery()
                .eq(Payment::getOrderID, orderId)
                .one();
        if (payment == null) {
            return false;
        }

        
        payment.setStatus("PAID");
        payment.setAlipayTradeNo("SIMULATE_" + orderId);
        payment.setPayTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        updateById(payment);

        Order order = new Order();
        order.setOrderID(orderId);
        order.setStatus("PAID");
        orderDbService.updateById(order);

        return true;
    }
}
