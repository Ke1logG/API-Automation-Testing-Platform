package com.example.api.campusmart.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.campusmart.db.entity.WalletFlow;

import java.util.List;


public interface WalletFlowDbService extends IService<WalletFlow> {

    List<WalletFlow> listByUserId(Long userId);
}
