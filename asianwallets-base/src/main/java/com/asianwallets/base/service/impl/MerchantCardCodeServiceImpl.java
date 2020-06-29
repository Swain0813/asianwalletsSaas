package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.MerchantCardCodeMapper;
import com.asianwallets.base.service.MerchantCardCodeService;
import com.asianwallets.common.dto.MerchantCardCodeDTO;
import com.asianwallets.common.entity.MerchantCardCode;
import com.asianwallets.common.entity.PreOrders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantCardCodeServiceImpl implements MerchantCardCodeService {

    @Autowired
    private MerchantCardCodeMapper merchantCardCodeMapper;

    /**
     * 分页查询商户码牌信息信息
     * @param merchantCardCodeDTO
     * @return
     */
    @Override
    public PageInfo<MerchantCardCode> pageFindMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO) {
        return new PageInfo<>(merchantCardCodeMapper.pageFindMerchantCardCode(merchantCardCodeDTO));
    }

    /**
     * 查询商户码牌详情信息
     * @param merchantCardCodeDTO
     * @return
     */
    @Override
    public MerchantCardCode getMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO) {
        //id不能为空
        if(StringUtils.isEmpty(merchantCardCodeDTO.getId())){
            throw new BusinessException(EResultEnum.NOTICE_ID_IS_NOT_NULL.getCode());
        }
        MerchantCardCode merchantCardCode = merchantCardCodeMapper.getMerchantCardCode(merchantCardCodeDTO);
        return merchantCardCode;
    }


}
