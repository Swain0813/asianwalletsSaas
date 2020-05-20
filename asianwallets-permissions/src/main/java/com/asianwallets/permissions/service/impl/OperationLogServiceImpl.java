package com.asianwallets.permissions.service.impl;

import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.OperationLogDTO;
import com.asianwallets.common.entity.Country;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.OperationLog;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.cache.CountryCodeRedisService;
import com.asianwallets.permissions.dao.OperationLogMapper;
import com.asianwallets.permissions.service.CommonService;
import com.asianwallets.permissions.service.OperationLogService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 操作日志模块的实现类
 */
@Service
@Transactional
public class OperationLogServiceImpl extends BaseServiceImpl<OperationLog> implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private CountryCodeRedisService countryCodeRedisService;

    @Autowired
    private CommonService commonService;

    /**
     * 添加操作日志
     *
     * @param operationLogDTO
     * @return
     */
    @Override
    public int addOperationLog(OperationLogDTO operationLogDTO) {
        //创建操作日志对象
        OperationLog operationLog = new OperationLog();
        BeanUtils.copyProperties(operationLogDTO, operationLog);
        operationLog.setId(IDS.uuid2());//id
        operationLog.setCreateTime(new Date());//创建时间
        return operationLogMapper.insert(operationLog);
    }

    /**
     * 查询所有的操作日志
     *
     * @param operationLogDTO
     * @return
     */
    @Override
    public PageInfo<OperationLog> pageOperationLog(OperationLogDTO operationLogDTO) {
        return new PageInfo(operationLogMapper.pageOperLog(operationLogDTO));
    }

    /**
     * 根据商户编号获取商户的国家代码
     * @param merchantId
     * @return
     */
    @Override
    public Country getMerchantCountryCode(String merchantId) {
        if (StringUtils.isEmpty(merchantId)) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Merchant merchant = commonService.getMerchant(merchantId);
        Country country = countryCodeRedisService.getCountry(merchant.getCountry());
        return country;
    }
}
