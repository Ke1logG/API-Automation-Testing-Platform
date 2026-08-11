package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.campusmart.db.entity.UserWallet;
import com.example.api.campusmart.db.mapper.UserWalletMapper;
import org.springframework.stereotype.Service;


@Service
public class WalletDbServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements WalletDbService {

    @Override
    public UserWallet getByUserId(Long userId) {
        return lambdaQuery()
                .eq(UserWallet::getUserID, userId)
                .one();
    }
}
