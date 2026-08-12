package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.api.campusmart.db.entity.Goods;
import com.example.api.campusmart.db.entity.Order;
import com.example.api.campusmart.db.entity.Payment;
import com.example.api.campusmart.db.entity.UserWallet;
import com.example.api.campusmart.db.entity.WalletFlow;
import com.example.api.campusmart.db.mapper.GoodsMapper;
import com.example.api.campusmart.db.mapper.OrderMapper;
import com.example.api.campusmart.db.mapper.PaymentMapper;
import com.example.api.campusmart.db.mapper.UserMapper;
import com.example.api.campusmart.db.mapper.UserWalletMapper;
import com.example.api.campusmart.db.mapper.WalletFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;


@Service
public class TestDataCleanupService {

    @Autowired
    private WalletFlowMapper walletFlowMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void cleanupByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        walletFlowMapper.delete(new QueryWrapper<WalletFlow>().in("userID", userIds));
        userWalletMapper.delete(new QueryWrapper<UserWallet>().in("userID", userIds));
        paymentMapper.delete(new QueryWrapper<Payment>().in("userID", userIds));
        orderMapper.delete(new QueryWrapper<Order>().in("buyerID", userIds).or().in("sellerID", userIds));
        goodsMapper.delete(new QueryWrapper<Goods>().in("publishUserID", userIds));
        userMapper.deleteBatchIds(userIds);
    }
}
