package com.asianwallets.base.service;

import com.asianwallets.common.dto.MerchantCardCodeDTO;
import com.asianwallets.common.entity.MerchantCardCode;
import com.github.pagehelper.PageInfo;

public interface MerchantCardCodeService {

    /**
     * 分页查询商户码牌信息信息
     * @param merchantCardCodeDTO
     * @return
     */
    PageInfo<MerchantCardCode> pageFindMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO);

    /**
     * 查询商户码牌详情信息
     * @param merchantCardCodeDTO
     * @return
     */
    MerchantCardCode getMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO);

    /**
     * 修改商户码牌信息
     * @param userName
     * @param merchantCardCodeDTO
     * @return
     */
    int updateMerchantCardCode(String userName, MerchantCardCodeDTO merchantCardCodeDTO);

}
