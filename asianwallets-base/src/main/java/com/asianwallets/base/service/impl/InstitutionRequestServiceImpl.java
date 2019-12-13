package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.InstitutionRequestParametersMapper;
import com.asianwallets.base.service.InstitutionRequestService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.entity.InstitutionRequestParameters;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.List;

/**
 * 机构请求参数设置实现类
 */
@Service
@Transactional
public class InstitutionRequestServiceImpl implements InstitutionRequestService {

    @Autowired
    private InstitutionRequestParametersMapper institutionRequestParametersMapper;

    @Autowired
    private RedisService redisService;

    /**
     * 新增机构请求参数设置
     * @param username
     * @param institutionRequests
     * @return
     */
    @Override
    public int addInstitutionRequest(String username, List<InstitutionRequestDTO> institutionRequests){
        List<InstitutionRequestParameters> institutionRequestParameters = Lists.newArrayList();
        for(InstitutionRequestDTO institutionRequest:institutionRequests){
            //机构编号的非空check
            if(StringUtils.isEmpty(institutionRequest.getInstitutionCode())){
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //机构名称的非空check
            if(StringUtils.isEmpty(institutionRequest.getInstitutionName())){
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //交易方向的非空check
            if(StringUtils.isEmpty(institutionRequest.getTradeDirection())){
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //如果该机构已经添加过机构请求参数设置,直接报信息已存在
            List<InstitutionRequestParameters> lists = institutionRequestParametersMapper.getInstitutionRequest(institutionRequest.getInstitutionCode());
            if(lists!=null && lists.size()>0){
                throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
            }
            //交易方向是线下的场合
            if(TradeConstant.TRADE_UPLINE==institutionRequest.getTradeDirection()){
                //产品编号 线下必填
                institutionRequest.setProductCode(true);
                //设备编号 线下必填
                institutionRequest.setImei(true);
                //操作员ID 线下必填
                institutionRequest.setOperatorId(true);
                //token 线下必填
                institutionRequest.setToken(true);
            }
            //创建机构请求参数设置
            InstitutionRequestParameters institutionRequestParameters1 = new InstitutionRequestParameters();
            BeanUtils.copyProperties(institutionRequest, institutionRequestParameters1);
            //id
            institutionRequestParameters1.setId(IDS.uuid2());
            //创建时间
            institutionRequestParameters1.setCreateTime(new Date());
            //创建人
            institutionRequestParameters1.setCreator(username);
            //新增的默认都是启用的
            institutionRequestParameters1.setEnabled(true);
            institutionRequestParameters.add(institutionRequestParameters1);
        }
        int num =institutionRequestParametersMapper.insertList(institutionRequestParameters);
        try {
            //新增时放入redis里面以便以后下单用 institutionReqPmsCacheKey_机构编号_交易方向
            for(InstitutionRequestParameters institutionRequestParameter:institutionRequestParameters){
                redisService.set(AsianWalletConstant.INSTITUTION_REQPMS_CACHE_KEY.concat("_").concat(institutionRequestParameter.getInstitutionCode()).concat("_").concat(String.valueOf(institutionRequestParameter.getTradeDirection())),
                        JSON.toJSONString(institutionRequestParameter));
            }
        }catch (Exception e){
            //Redis同步错误
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return  num;
    }

    /**
     * 分页查询机构请求参数设置
     * @param institutionRequestDTO
     * @return
     */
    @Override
    public PageInfo<InstitutionRequestParameters> pageInstitutionRequest(InstitutionRequestDTO institutionRequestDTO){
        return new PageInfo(institutionRequestParametersMapper.pageInstitutionRequest(institutionRequestDTO));
    }


    /**
     * 根据机构编号查询机构请求参数设置的详情
     * @param institutionRequestDTO
     * @return
     */
    @Override
    public List<InstitutionRequestParameters> getInstitutionRequests(InstitutionRequestDTO institutionRequestDTO){
        return institutionRequestParametersMapper.getInstitutionRequests(institutionRequestDTO);
    }

    /**
     * 修改机构请求参数设置
     * @param username
     * @param institutionRequests
     * @return
     */
    @Override
    public int updateInstitutionRequest(String username, List<InstitutionRequestDTO> institutionRequests){
        List<InstitutionRequestParameters> institutionRequestParameters = Lists.newArrayList();
        for(InstitutionRequestDTO institutionRequest:institutionRequests){
             if(institutionRequest.getId()==null){
                 //如果没有id则说明是新增的场合
                 addInstitutionRequest(username,institutionRequests);
                 continue;
             }
            //根据id判断修改机构请求参数设置信息存不存在
            InstitutionRequestParameters institutionRequestParameters1 = institutionRequestParametersMapper.selectByPrimaryKey(institutionRequest.getId());
            if(institutionRequestParameters1==null){
                throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
            }
            BeanUtils.copyProperties(institutionRequest, institutionRequestParameters1, ReflexClazzUtils.getNullPropertyNames(institutionRequest));
            //修改人
            institutionRequestParameters1.setModifier(username);
            //修改时间
            institutionRequestParameters1.setUpdateTime(new Date());
            institutionRequestParameters.add(institutionRequestParameters1);
        }
        int num =institutionRequestParametersMapper.updateBatchList(institutionRequestParameters);
        try {
            //修改时需要重新放入redis里面以便以后下单用 institutionReqPmsCacheKey_机构编号_交易方向
            for(InstitutionRequestParameters institutionRequestParameter:institutionRequestParameters){
                redisService.set(AsianWalletConstant.INSTITUTION_REQPMS_CACHE_KEY.concat("_").concat(institutionRequestParameter.getInstitutionCode()).concat("_").concat(String.valueOf(institutionRequestParameter.getTradeDirection())),
                        JSON.toJSONString(institutionRequestParameter));
            }
        }catch (Exception e){
            //Redis同步错误
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return  num;
    }

}
