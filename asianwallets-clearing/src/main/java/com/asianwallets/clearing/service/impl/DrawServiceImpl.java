package com.asianwallets.clearing.service.impl;
import com.asianwallets.clearing.dao.AccountMapper;
import com.asianwallets.clearing.service.DrawService;
import com.asianwallets.clearing.service.SettleOrdersService;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class DrawServiceImpl implements DrawService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private SettleOrdersService settleOrdersService;

    @Autowired
    private RedisService redisService;

    /**
     * 按商户分组自动提款批处理
     */
    @Override
    public void DrawForBatch() {
        log.info("**************** 开始定时跑批生成自动提款结算交易**************** #开始时间：{}", DateToolUtils.getReqDateH(new Date()));
        if (redisService.get(TradeConstant.FLAY_KEY.concat("_").concat(DateToolUtils.getReqDate())) != null) {
            return;
        }
        try {
            //查询获取账户表中(结算账户余额-冻结账户余额)大于0的数据且设置自动提款的商户
            List<Account> lists = accountMapper.getAccounts();
            if (lists == null || lists.size() == 0) {
                log.info("**************** DrawForBatch 批次自动提款 ************** 没有查询到符合可以自动提款的数据");
                return;
            }
            log.info("**************** DrawForBatch 批次自动提款 ************** 查询到符合可以自动提款的数据 【{}】 条", lists.size());
            //第二步按照商户id来封装
            TreeMap<String, List<Account>> map = new TreeMap<String, List<Account>>();
            for (Account list : lists) {
                //按照商户编号和币种来分组自动提款
                String key = list.getMerchantId() + "_" + list.getCurrency();
                if ((!map.containsKey(key)) && map.get(key) == null) {
                    List<Account> arrayList = new ArrayList<>();
                    arrayList.add(list);
                    map.put(key, arrayList);
                } else if (map.containsKey(key) && map.get(key) != null) {
                    List<Account> accountList = map.get(key);
                    accountList.add(list);
                    map.put(key, accountList);
                }
            }
            // 第三步，按照商户为单位进行处理
            Set<String> ks = map.keySet();
            if (ks == null || ks.size() == 0) {
                log.info("**************** DrawForBatch 批次自动提款 ************** 以商户为单位排序无数据");
                return;
            }
            Iterator it = ks.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String[] keys = key.split("_");
                String merchantId = keys[0];
                String currency = keys[1];
                if (key == null || key.equals("")) {
                    continue;
                }
                List<Account> accountList = map.get(key);
                log.info("**************** DrawForBatch 批次自动提款 ************** #商户编号为:【{}】 的待自动提款数据 【{}】 条", merchantId, accountList.size());
                if (accountList != null && accountList.size() > 0) {
                    log.info("**************** DrawForBatch 批次自动提款 ************** 开始执行商户编号为:【{}】，币种为：【{}】的待自动提款数据", merchantId, currency);
                    settleOrdersService.getSettleOrders(merchantId, currency, accountList);//以商户+交易币种为组进行结算，以组提交事物
                    log.info("**************** DrawForBatch 批次自动提款 ************** 结束执行商户编号为:【{}】，币种为：【{}】的待结算数据", merchantId, currency);
                }
            }
        } catch (Exception e) {
            log.error("**************** DrawForBatch 批次提款 ************** 异常: ", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            //定时跑批生成结算交易完，在redis里记录一下，说明当日已经跑批
            redisService.set(TradeConstant.FLAY_KEY.concat("_").concat(DateToolUtils.getReqDate()), "true", 24 * 60 * 60);//24小时后失效
        }
        log.info("************结束定时跑批生成自动提款结算交易**************** #结束时间：{}", DateToolUtils.getReqDateH(new Date()));
    }
}
