package com.asianwallets.clearing.service.impl;

import com.asianwallets.clearing.dao.TcsStFlowMapper;
import com.asianwallets.clearing.service.SettleService;
import com.asianwallets.clearing.service.TCSStFlowService;
import com.asianwallets.common.entity.TcsStFlow;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description: 定时结算服务
 * @author: YangXu
 * @create: 2019-07-26 15:19
 **/
@Slf4j
@Service
public class SettleServiceImpl implements SettleService {

    @Autowired
    private TcsStFlowMapper tcsStFlowMapper;

    @Autowired
    private TCSStFlowService tcsStFlowService;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 结算批次处理
     **/
    @Override
    public void SettlementForBatch() {
        log.info("**************** SettlementForBatch 批次结算 ************** #开始时间：{}", new Date());
        try {
            TcsStFlow st = new TcsStFlow();
            st.setSTstate(1);
            //应该结算时间小于或等于ShouldSTtime, shouldSTtime  <= #{shouldSTtime,jdbcType=DATE}
            st.setShouldSTtime(new Date());
            List<TcsStFlow> list = tcsStFlowMapper.selectList(st);
            if (list == null || list.size() == 0) {
                log.info("**************** SettlementForBatch 批次结算 ************** 没有查询到符合可以结算的数据");
                return;
            }
            log.info("**************** SettlementForBatch 批次结算 ************** 查询到符合可以结算的数据 【{}】 条", list.size());

            // 第二步按照商户id来封装
            TreeMap<String, List<TcsStFlow>> mermap = new TreeMap<String, List<TcsStFlow>>();
            for (TcsStFlow stflow : list) {
                //要按照结算币种来结算
                String key = stflow.getMerchantid() + "_" + stflow.getTxncurrency();
                if ((!mermap.containsKey(key)) && mermap.get(key) == null) {
                    List<TcsStFlow> slist = new ArrayList<>();
                    slist.add(stflow);
                    mermap.put(key, slist);
                } else if (mermap.containsKey(key) && mermap.get(key) != null) {
                    List<TcsStFlow> slist = mermap.get(key);
                    slist.add(stflow);
                    mermap.put(key, slist);
                }
            }

            // 第三步，按照商户为单位进行结算处理。
            Set<String> ks = mermap.keySet();
            if (ks == null || ks.size() == 0) {
                log.info("**************** SettlementForBatch 批次结算 ************** 以商户为单位排序无数据");
                return;
            }
            Iterator it = ks.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String[] keystr = key.split("_");
                String merchantid = keystr[0];
                String sltncurrency = keystr[1];
                if (key == null || key.equals("")) {
                    continue;
                }
                List<TcsStFlow> slist = mermap.get(key);
                log.info("**************** SettlementForBatch 批次结算 ************** #商户号为:【{}】 的待结算数据 【{}】 条", merchantid, slist.size());
                if (slist != null && slist.size() > 0) { ////需要将slist中的结算排序一下，入账的资金类型优先结算
                    //Collections.sort(slist, new Comparator<TcsStFlow>() {
                    //    @Override
                    //    public int compare(TcsStFlow o1, TcsStFlow o2) {
                    //        return o1.getSort().compareTo(o2.getSort());
                    //    }
                    //});) > 0) {

                    log.info("**************** SettlementForBatch 批次结算 ************** 开始执行商户号为:【{}】，币种为：【{}】的待结算数据", merchantid, sltncurrency);
                    //以商户+交易币种为组进行结算，以组提交事物
                    tcsStFlowService.SettlementForMerchantGroup2(merchantid, sltncurrency, slist);
                    log.info("**************** SettlementForBatch 批次结算 ************** 结束执行商户号为:【{}】，币种为：【{}】的待结算数据", merchantid, sltncurrency);
                }
            }
        } catch (Exception e) {
            log.error("**************** SettlementForBatch 批次结算 ************** 异常：{}", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
    }


}
