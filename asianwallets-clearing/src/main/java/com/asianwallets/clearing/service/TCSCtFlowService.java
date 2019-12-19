package com.asianwallets.clearing.service;
import com.asianwallets.common.entity.TcsCtFlow;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;

import java.util.List;

public interface TCSCtFlowService {

    /**
     * @Author YangXu
     * @Date 2019/7/25
     * @Descripate  清算账户资金变动处理，主要操作包含插入清算表记录
     * @return
     **/
    BaseResponse IntoAndOutMerhtSTAccount2(FundChangeDTO ioma);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 以商户编号，币种，业务类型，待清算分组list进行一组批次清算方法
     **/
     BaseResponse ClearForMerchantGroup(String mid, String currency, int businessType, List<TcsCtFlow> list);
}
