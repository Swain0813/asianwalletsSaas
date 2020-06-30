package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.MerchantCardCodeDTO;
import com.asianwallets.common.entity.MerchantCardCode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantCardCodeMapper extends BaseMapper<MerchantCardCode> {

    /**
     * 分页查询商户码牌信息信息
     * @param merchantCardCodeDTO
     * @return
     */
    List<MerchantCardCode> pageFindMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO);

    /**
     * 查询商户码牌详情信息
     * @param merchantCardCodeDTO
     * @return
     */
    MerchantCardCode getMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO);

}