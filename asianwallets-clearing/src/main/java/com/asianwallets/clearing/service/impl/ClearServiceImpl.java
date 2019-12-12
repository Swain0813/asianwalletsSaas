package com.asianwallets.clearing.service.impl;
import com.asianwallets.clearing.dao.TcsCtFlowMapper;
import com.asianwallets.clearing.service.ClearService;
import com.asianwallets.clearing.service.TCSCtFlowService;
import com.asianwallets.common.entity.TcsCtFlow;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @description: 定时清算服务
 * @author: YangXu
 * @create: 2019-07-26 15:20
 **/
@Slf4j
@Service
public class ClearServiceImpl implements ClearService {

    @Autowired
    private TcsCtFlowMapper tcsCtFlowMapper;

    @Autowired
    private TCSCtFlowService tcsCtFlowService;


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 以商户清算账户分组批次清算
     **/
    @Override
    public void ClearForGroupBatch() {
        log.info("**************** ClearForGroupBatch 批次清算 ************** #开始时间：{}", new Date());
        try {
            TcsCtFlow st = new TcsCtFlow();
            st.setCTstate(1);
            List<TcsCtFlow> list = tcsCtFlowMapper.select(st);
            if (list == null || list.size() == 0) {
                log.info("**************** ClearForGroupBatch 批次清算 ************** #没有查询到符合可以清算的数据");
                return;
            }
            log.info("**************** ClearForGroupBatch 批次清算 ************** 查询到可以清算的数据 {} 条", list.size());
            //需要将待清算数据做一个分组排序，优先处理入账资金记录：商户编号，币种，业务类型
            TreeMap<String, List<TcsCtFlow>> mermap = new TreeMap<String, List<TcsCtFlow>>();//分组map
            for (TcsCtFlow cl : list) {
                //商户编号，币种，业务类型来分组
                String key = cl.getMerchantid() + "_" + cl.getTxncurrency() + "_" + cl.getBusinessType();
                log.info("**************** ClearForGroupBatch 批次清算 ************** 开始分组，key:" + key);
                if ((!mermap.containsKey(key)) && mermap.get(key) == null) {
                    //不存在这个Key就新建再添加
                    List<TcsCtFlow> slist = new ArrayList<>();
                    slist.add(cl);
                    mermap.put(key, slist);
                } else if (mermap.containsKey(key) && mermap.get(key) != null) {
                    //存在就直接put进去
                    List<TcsCtFlow> slist = mermap.get(key);
                    slist.add(cl);
                    mermap.put(key, slist);
                }
            }
            //分组完成；
            log.info("**************** ClearForGroupBatch 批次清算 ************** #分组操作完成");
            //开始以Key来提取分好的组，以组来清算
            Set<String> ks = mermap.keySet();
            if (ks == null || ks.size() == 0) {
                log.info("**************** ClearForGroupBatch 批次清算 ************** #分组后的key集合为空");
                return;
            }
            Iterator it = ks.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key == null || key.equals("")) {
                    log.info("**************** ClearForGroupBatch 批次清算 ************** #获取key值为空");
                    continue;
                }
                String[] keystr = key.split("_");
                List<TcsCtFlow> slist = mermap.get(key);
                log.info("**************** ClearForGroupBatch 批次清算 **************，key为: 【{}】 的待清算数据 【{}】 条", key, slist.size());
                if (slist == null || slist.size() == 0 || keystr == null || keystr.length != 3) {
                    log.info("**************** ClearForGroupBatch 批次清算 ************** key为: 【{}】 的list为空", key);
                    continue;
                }
                String merchantid = keystr[0];
                String sltncurrency = keystr[1];
                String businessTypeStr = keystr[2];
                if (merchantid == null || merchantid.equals("") || sltncurrency == null
                        || sltncurrency.equals("") || businessTypeStr == null || businessTypeStr.equals("")) {
                    log.info("**************** ClearForGroupBatch 批次清算 ************** key 为 【{}】的分组在解析key成为merchantid,sltncurrency,businessType的过程中有空值", key);
                    continue;
                }

                //需要将slist中的结算排序一下，入账的资金类型优先结算
                log.info("**************** ClearForGroupBatch 批次清算 **************#开始执行key为 【{}】的待清算数据", key);
                int businessType = Integer.parseInt(businessTypeStr);
                tcsCtFlowService.ClearForMerchantGroup(merchantid, sltncurrency, businessType, slist);//以商户+币种+业务类型为组进行清算
                log.info("o**************** ClearForGroupBatch 批次清算 **************# 结束执行key为【{}】的待清算数据", key);
            }

        } catch (Exception e) {
            log.error("**************** ClearForGroupBatch 批次清算 ************** 异常：{}", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        } finally {
            log.info("**************** ClearForGroupBatch 批次清算 ************** 结束时间：{}", new Date());
        }
    }


}
