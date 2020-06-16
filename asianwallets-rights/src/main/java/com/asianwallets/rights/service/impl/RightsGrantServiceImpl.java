package com.asianwallets.rights.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.RightsConstant;
import com.asianwallets.common.dto.RightsGrantDTO;
import com.asianwallets.common.dto.RightsGrantInsertDTO;
import com.asianwallets.common.dto.SendReceiptDTO;
import com.asianwallets.common.entity.InstitutionRights;
import com.asianwallets.common.entity.RightsGrant;
import com.asianwallets.common.entity.RightsUserGrant;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.ExportRightsGrantVO;
import com.asianwallets.common.vo.ExportRightsUserGrantVO;
import com.asianwallets.common.vo.RightsUserGrantDetailVO;
import com.asianwallets.rights.dao.InstitutionRightsMapper;
import com.asianwallets.rights.dao.RightsGrantMapper;
import com.asianwallets.rights.dao.RightsUserGrantMapper;
import com.asianwallets.rights.service.CommonService;
import com.asianwallets.rights.service.RightsGrantService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 权益发放管理模块业务层的实现类
 */
@Service
@Slf4j
public class RightsGrantServiceImpl implements RightsGrantService {

    @Autowired
    private RightsGrantMapper rightsGrantMapper;

    @Autowired
    private RightsUserGrantMapper rightsUserGrantMapper;

    @Autowired
    private InstitutionRightsMapper institutionRightsMapper;

    @Autowired
    private CommonService commonService;

    //======================================【票券】=================================================

    /**
     * 发券接口【对外API】
     * @param sendReceiptDTO 发券DTO
     * @return
     */
    @Override
    @Transactional
    public JSONObject sendReceipt(SendReceiptDTO sendReceiptDTO) {
        log.info("===================【发券接口】===================【请求参数】 sendReceiptDTO: {}", JSON.toJSONString(sendReceiptDTO));
        String[] sendCount = null;
        if(RightsConstant.MOBILE_SYS.equals(sendReceiptDTO.getSendType())){
            //短信平台则手机号不能为空
            if(StringUtils.isEmpty(sendReceiptDTO.getMobileNo())){
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            sendCount = sendReceiptDTO.getMobileNo().split(",");
        }else {
            //邮箱平台则邮箱不能为空
            if(StringUtils.isEmpty(sendReceiptDTO.getEmail())){
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            sendCount = sendReceiptDTO.getEmail().split(",");
        }
        if(sendCount.length<=0){
            log.info("===================【发券的真正数量】===================",sendCount.length);
            throw new BusinessException(EResultEnum.OTA_ACTIVITY_AMOUNT_IS_ILLEGAL.getCode());
        }
        //设置发券数量
        sendReceiptDTO.setSendCount(sendCount.length);
        RightsGrant rightsGrant = rightsGrantMapper.selectByDealId(sendReceiptDTO.getDealId());
        if (rightsGrant == null) {
            log.info("===================【发券接口】===================【权益不存在】");
            throw new BusinessException(EResultEnum.EQUITY_DOES_NOT_EXIST.getCode());
        }
        //剩余数量
        Integer surplusAmount = rightsGrant.getSurplusAmount();
        //比较剩余数量
        if (sendReceiptDTO.getSendCount() > surplusAmount) {
            log.info("===================【发券接口】===================【票券数量不足】");
            throw new BusinessException(EResultEnum.OTA_ACTIVITY_AMOUNT_IS_ILLEGAL.getCode());
        }
        //当前时间
        long nowTime = new Date().getTime();
        //活动开始和结束时间与当前时间的判断
        if (rightsGrant.getEndTime() != null && nowTime > rightsGrant.getEndTime().getTime()) {
            log.info("===================【发券接口】===================【大于活动结束时间】");
            throw new BusinessException(EResultEnum.EVENT_END_TIME_IS_ILLEGAL.getCode());
        }
        //剩余数量
        surplusAmount = surplusAmount - sendReceiptDTO.getSendCount();
        rightsGrant.setSurplusAmount(surplusAmount);
        //领取数量
        rightsGrant.setGetAmount(rightsGrant.getActivityAmount() - surplusAmount);
        rightsGrant.setUpdateTime(new Date());
        rightsGrant.setExt3("发券更新");
        //更新权益发放表
        rightsGrantMapper.updateByPrimaryKeySelective(rightsGrant);
        List<String> ticketIdList = new ArrayList<>();
        for (int i = 0; i < sendReceiptDTO.getSendCount(); i++) {
            RightsUserGrant rightsUserGrant = new RightsUserGrant();
            String ticketId = IDS.uniqueID().toString();
            ticketIdList.add(ticketId);
            rightsUserGrant.setId(IDS.uuid2());
            rightsUserGrant.setTicketId(ticketId);
            rightsUserGrant.setDealId(sendReceiptDTO.getDealId());
            rightsUserGrant.setSystemOrderId(sendReceiptDTO.getSystemOrderId());
            rightsUserGrant.setGetAmount(1);
            if(!StringUtils.isEmpty(sendReceiptDTO.getMobileNo())){
                rightsUserGrant.setMobileNo(sendCount[i]);
            }else {
                rightsUserGrant.setEmail(sendCount[i]);
            }
            rightsUserGrant.setSendType(sendReceiptDTO.getSendType());
            rightsUserGrant.setContent(sendReceiptDTO.getContent());

            rightsUserGrant.setSystemName(rightsGrant.getSystemName());
            rightsUserGrant.setBatchNo(rightsGrant.getBatchNo());
            rightsUserGrant.setInstitutionId(rightsGrant.getInstitutionId());
            rightsUserGrant.setInstitutionName(rightsGrant.getInstitutionName());
            rightsUserGrant.setMerchantId(rightsGrant.getMerchantId());
            rightsUserGrant.setMerchantName(rightsGrant.getMerchantName());
            rightsUserGrant.setRightsType(rightsGrant.getRightsType());
            rightsUserGrant.setActivityTheme(rightsGrant.getActivityTheme());
            rightsUserGrant.setActivityAmount(rightsGrant.getActivityAmount());
            rightsUserGrant.setStartTime(rightsGrant.getStartTime());
            rightsUserGrant.setEndTime(rightsGrant.getEndTime());
            rightsUserGrant.setUseTime(rightsGrant.getUseTime());
            rightsUserGrant.setUserId(rightsGrant.getUserId());
            rightsUserGrant.setCancelVerificationAmount(rightsGrant.getCancelVerificationAmount());
            rightsUserGrant.setSurplusAmount(rightsGrant.getSurplusAmount());
            rightsUserGrant.setTicketStatus(RightsConstant.TICKETS_NOT_USE);
            rightsUserGrant.setTicketAmount(rightsGrant.getTicketAmount());
            rightsUserGrant.setPackageValue(rightsGrant.getPackageValue());
            rightsUserGrant.setFullReductionAmount(rightsGrant.getFullReductionAmount());
            rightsUserGrant.setDiscount(rightsGrant.getDiscount());
            rightsUserGrant.setCapAmount(rightsGrant.getCapAmount());
            rightsUserGrant.setDeductionAmount(rightsGrant.getDeductionAmount());
            rightsUserGrant.setTicketBuyPrice(rightsGrant.getTicketBuyPrice());
            rightsUserGrant.setGetLimit(rightsGrant.getGetLimit());
            rightsUserGrant.setShopAddresses(rightsGrant.getShopAddresses());
            rightsUserGrant.setSetText(rightsGrant.getSetText());
            rightsUserGrant.setSetImages(rightsGrant.getSetImages());
            rightsUserGrant.setRuleDescription(rightsGrant.getRuleDescription());
            rightsUserGrant.setRightsCurrency(rightsGrant.getRightsCurrency());
            rightsUserGrant.setServerUrl(rightsGrant.getServerUrl());
            rightsUserGrant.setDistributionPrice(rightsGrant.getDistributionPrice());
            rightsUserGrant.setOverlay(rightsGrant.getOverlay());
            rightsUserGrant.setExt4(rightsGrant.getExt4());
            rightsUserGrant.setGetTime(new Date());
            rightsUserGrant.setCancelVerificationTime(new Date());
            rightsUserGrant.setEnabled(true);
            rightsUserGrant.setCreateTime(new Date());
            rightsUserGrant.setCreator(sendReceiptDTO.getUserName());
            int result = rightsUserGrantMapper.insert(rightsUserGrant);
            if(result>0){
               this.commonService.sendMobileAndEmail(rightsUserGrant);
            }
        }
        log.info("===================【发券接口】===================【发出的券号】",JSON.toJSONString(ticketIdList));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ticketIdList", ticketIdList);
        return jsonObject;
    }

    /**
     * 分页查询权益票券信息
     * @param rightsGrantDTO 输入DTO
     * @return
     */
    @Override
    public PageInfo<RightsUserGrant> pageFindRightsUserGrant(RightsGrantDTO rightsGrantDTO) {
        return new PageInfo<>(rightsGrantMapper.pageFindRightsUserGrant(rightsGrantDTO));
    }

    /**
     * 导出权益票券信息
     * @param rightsGrantDTO 输入DTO
     * @return
     */
    @Override
    public List<ExportRightsUserGrantVO> exportRightsUserGrant(RightsGrantDTO rightsGrantDTO) {
        return rightsGrantMapper.exportRightsUserGrant(rightsGrantDTO);
    }

    /**
     * 查询权益票券详情
     * @param ticketId 票券编号
     * @return
     */
    @Override
    public RightsUserGrantDetailVO getRightsUserGrantDetail(String ticketId) {
        return rightsUserGrantMapper.getRightsUserGrantDetail(ticketId);
    }


    //======================================【权益发放】=================================================

    /**
     * 分页查询权益发放管理信息
     * @param rightsGrantDTO
     * @return
     */
    @Override
    public PageInfo<RightsGrant> pageFindRightsGrant(RightsGrantDTO rightsGrantDTO) {
        List<RightsGrant> rightsGrantLists = rightsGrantMapper.pageFindRightsGrant(rightsGrantDTO);
        return new PageInfo<>(rightsGrantLists);
    }

    /**
     *
     * @param rightsGrantDTO
     * @return
     */
    public RightsGrant selectRightsGrantInfo(RightsGrantDTO rightsGrantDTO){
        RightsGrant rightsGrant = rightsGrantMapper.selectRightsGrantInfo(rightsGrantDTO);
        return rightsGrant;
    }

    /**
     * 导出权益发放管理信息
     * @param rightsGrantDTO
     * @return
     */
    @Override
    public List<ExportRightsGrantVO> exportRightsGrants(RightsGrantDTO rightsGrantDTO) {
        List<ExportRightsGrantVO> rightsGrantLists = rightsGrantMapper.exportRightsGrants(rightsGrantDTO);
        return rightsGrantLists;
    }

    /**
     * 新增权益发放管理信息
     * @param username
     * @param rightsGrantInsertDTO
     * @return
     */
    @Override
    @Transactional
    public int addRightsGrant(String username, RightsGrantInsertDTO rightsGrantInsertDTO) {
        //根据机构权益订单号判断机构权益存不存在
        InstitutionRights institutionRights = institutionRightsMapper.getInstitutionRights(rightsGrantInsertDTO.getBatchNo());
        if (institutionRights == null) {
            throw new BusinessException(EResultEnum.EQUITY_DOES_NOT_EXIST.getCode());
        }
        //新增之前，确认一下该机构权益是不是已经存在，之后上送不能超过活动的总数量
        RightsGrant oldRightsGrant = rightsGrantMapper.selectByBatchNo(rightsGrantInsertDTO.getBatchNo());
        if (oldRightsGrant != null && rightsGrantInsertDTO.getActivityAmount()>institutionRights.getSurplusAmount()) {
            throw new BusinessException(EResultEnum.OTA_ACTIVITY_AMOUNT_IS_ILLEGAL.getCode());
        }
        //当前时间
        long nowTime = new Date().getTime();
        //结束时间与当前时间的判断
        if (rightsGrantInsertDTO.getEndTime() != null && nowTime > DateToolUtils.getReqDateG(rightsGrantInsertDTO.getEndTime()).getTime()) {
            throw new BusinessException(EResultEnum.EVENT_END_TIME_IS_ILLEGAL.getCode());
        }
        //票券金额和票券购买价的比较，票券购买价不能大于票券金额
        if(rightsGrantInsertDTO.getTicketBuyPrice()!=null && rightsGrantInsertDTO.getTicketBuyPrice().compareTo(rightsGrantInsertDTO.getTicketAmount())==1){
            throw new BusinessException(EResultEnum.TICKET_BUY_PRICE_NOT_THAN_TICKET_PRICE.getCode());
        }
        //创建权益发放管理记录
        RightsGrant rightsGrant = this.createRightsGrant(rightsGrantInsertDTO, institutionRights, username);
//        try {
//            HttpResponse httpResponse = HttpClientUtils.reqPost(rightsGrantInsertDTO.getReportUrl(), rightsGrantInsertDTO, null);
//            if(httpResponse.getHttpStatus()!=200){
//                throw new BusinessException(EResultEnum.INSERT_RIGHTS_FAILED.getCode());
//            }
//        }catch (Exception e){
//            log.info("********************权益上送到OTA系统******************发生异常",e.getMessage());
//            throw new BusinessException(EResultEnum.INSERT_RIGHTS_FAILED.getCode());
//        }
        //团购号
        rightsGrant.setDealId(IDS.uniqueID().toString());
        //发放平台返回的交易流水号
        rightsGrant.setSystemOrderId(IDS.uniqueID().toString());
        //新增权益发放管理信息
        int result = rightsGrantMapper.insert(rightsGrant);
        if (result > 0) {
            //更新机构权益表剩余数量
            result = institutionRightsMapper.updateSurplusAmountByBatchNo(institutionRights.getSurplusAmount() - rightsGrantInsertDTO.getActivityAmount(), rightsGrantInsertDTO.getBatchNo());
        }
        return result;
    }

    /**
     * @param rightsGrantInsertDTO
     * @param institutionRights
     * @return
     */
    private RightsGrant createRightsGrant(RightsGrantInsertDTO rightsGrantInsertDTO, InstitutionRights institutionRights, String username) {
        RightsGrant rightsGrant = new RightsGrant();
        //平台名称
        rightsGrant.setSystemName(rightsGrantInsertDTO.getSystemName());
        //机构权益订单号
        rightsGrant.setBatchNo(rightsGrantInsertDTO.getBatchNo());
        //机构编号
        rightsGrant.setInstitutionId(rightsGrantInsertDTO.getInstitutionId());
        //机构名称
        rightsGrant.setInstitutionName(institutionRights.getInstitutionName());
        //商户编号
        rightsGrant.setMerchantId(rightsGrantInsertDTO.getMerchantId());
        //商户名称
        rightsGrant.setMerchantName(institutionRights.getMerchantName());
        //权益类型
        rightsGrant.setRightsType(institutionRights.getRightsType());
        //活动主题
        rightsGrant.setActivityTheme(rightsGrantInsertDTO.getActivityTheme());
        //活动数量
        rightsGrant.setActivityAmount(rightsGrantInsertDTO.getActivityAmount());
        //剩余数量
        rightsGrant.setSurplusAmount(rightsGrantInsertDTO.getActivityAmount());
        //活动开始时间
        rightsGrant.setStartTime(DateToolUtils.getReqDateG(rightsGrantInsertDTO.getStartTime()));
        //活动结束时间
        rightsGrant.setEndTime(DateToolUtils.getReqDateG(rightsGrantInsertDTO.getEndTime()));
        //票券金额
        rightsGrant.setTicketAmount(rightsGrantInsertDTO.getTicketAmount());
        //票券购买价
        rightsGrant.setTicketBuyPrice(rightsGrantInsertDTO.getTicketBuyPrice());

        //抵扣金额=票券金额*机构扣率
        if(institutionRights.getDiscount()!=null){
            rightsGrant.setDeductionAmount(rightsGrantInsertDTO.getTicketAmount().multiply(institutionRights.getDiscount()));
            //扣率
            rightsGrant.setDiscount(institutionRights.getDiscount());
        }
        //套餐金额=机构套餐金额
        rightsGrant.setPackageValue(institutionRights.getPackageValue());
        //满减金额
        rightsGrant.setFullReductionAmount(institutionRights.getFullReductionAmount());
        //封顶金额
        rightsGrant.setCapAmount(institutionRights.getCapAmount());
        //使用限制 1-不限 2-每订单/张
        rightsGrant.setGetLimit(institutionRights.getGetLimit());
        //可用门店地址
        rightsGrant.setShopAddresses(institutionRights.getShopAddresses());
        //套餐文字
        rightsGrant.setSetText(institutionRights.getSetText());
        //套餐图片
        rightsGrant.setSetImages(institutionRights.getSetImages());
        //规则说明
        rightsGrant.setRuleDescription(institutionRights.getRuleDescription());
        //权益币种
        rightsGrant.setRightsCurrency(institutionRights.getRightsCurrency());
        //分销价
        rightsGrant.setDistributionPrice(rightsGrantInsertDTO.getDistributionPrice());
        //是否叠加
        rightsGrant.setOverlay(institutionRights.getOverlay());
        //不可用时间
        rightsGrant.setExt4(institutionRights.getExtend1());
        //管理表的id
        rightsGrant.setId(IDS.uniqueID().toString());
        rightsGrant.setEnabled(true);
        //创建时间
        rightsGrant.setCreateTime(new Date());
        //创建人
        rightsGrant.setCreator(username);

        return rightsGrant;
    }
}
