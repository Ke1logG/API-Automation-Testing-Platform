package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.campusmart.db.entity.Payment;


public interface PaymentDbService extends IService<Payment> {

    
    boolean simulatePaid(Long orderId);
}
