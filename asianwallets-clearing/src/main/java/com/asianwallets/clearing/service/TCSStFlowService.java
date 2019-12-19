package com.asianwallets.clearing.service;

import com.asianwallets.common.entity.TcsStFlow;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;

import java.util.List;


public interface TCSStFlowService {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/25
     * @Descripate 结算账户的资金变动处理方法，主要包含插入结算表记录
     **/
    BaseResponse IntoAndOutMerhtSTAccount2(FundChangeDTO ioma);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/29
     * @Descripate 根据流程梳理要求优化的以组为单位结算并提交事物
     **/
    void SettlementForMerchantGroup2(String merchantid, String sltcurrency, List<TcsStFlow> list);

}
