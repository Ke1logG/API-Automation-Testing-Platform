package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.campusmart.db.entity.WalletFlow;
import com.example.api.campusmart.db.mapper.WalletFlowMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WalletFlowDbServiceImpl extends ServiceImpl<WalletFlowMapper, WalletFlow> implements WalletFlowDbService {

    @Override
    public List<WalletFlow> listByUserId(Long userId) {
        return lambdaQuery()
                .eq(WalletFlow::getUserID, userId)
                .orderByDesc(WalletFlow::getCreateTime)
                .list();
    }
}
