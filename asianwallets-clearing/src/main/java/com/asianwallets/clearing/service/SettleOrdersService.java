package com.asianwallets.clearing.service;
import com.asianwallets.common.entity.Account;
import java.util.List;

/**
 * 定时跑批自动提款功能
 */
public interface SettleOrdersService {

    /**
     * 定时跑批自动提款功能
     */
    void  getSettleOrders(String merchantId, String currency, List<Account> lists);
}
