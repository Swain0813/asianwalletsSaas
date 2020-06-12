package com.asianwallets.rights.service.impl;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.OtaChannel;
import com.asianwallets.common.entity.RightsOrders;
import com.asianwallets.common.entity.RightsOrdersRefund;
import com.asianwallets.common.entity.RightsUserGrant;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.RightsOrdersApiVO;
import com.asianwallets.common.vo.RightsOrdersCancelVO;
import com.asianwallets.common.vo.RightsOrdersVO;
import com.asianwallets.rights.dao.*;
import com.asianwallets.rights.service.CommonRedisService;
import com.asianwallets.rights.service.CommonService;
import com.asianwallets.rights.service.RightsOrdersService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class RightsOrdersServiceImpl extends BaseServiceImpl<RightsOrders> implements RightsOrdersService {

    @Autowired
    private RightsOrdersMapper rightsOrdersMapper;

    @Autowired
    private RightsGrantMapper rightsGrantMapper;

    @Autowired
    private RightsUserGrantMapper rightsUserGrantMapper;

    @Autowired
    private CommonRedisService commonRedisService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RightsOrdersRefundMapper rightsOrdersRefundMapper;

    @Autowired
    private OtaChannelMapper otaChannelMapper;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 权益核销
     **/
    @Override
    public BaseResponse verificationCancel(VerificationCancleDTO verificationCancleDTO) {
        BaseResponse baseResponse = new BaseResponse();
        /***********************************  校验签名  *************************************/
        if (commonService.checkUniversalSign(verificationCancleDTO)) {
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());//验签不匹配
        }

        /************************************** 判断机构信息 ****************************************/
        commonRedisService.getInstitutionInfo(verificationCancleDTO.getInstitutionId());
        /******************************* 创建票券订单 **********************************/

        Object[] obj = this.creatRightsOrders(verificationCancleDTO);
        RightsOrders rightsOrder = (RightsOrders) obj[0];
        List<RightsUserGrant> list = (List<RightsUserGrant>) obj[1];
        //核销返回参数
        RightsOrdersCancelVO rightsOrdersVO = new RightsOrdersCancelVO();
        BeanUtils.copyProperties(rightsOrder, rightsOrdersVO);
        //权益类型
        rightsOrdersVO.setPreferentialType(rightsOrder.getRightsCurrency());
        //权益币种
        rightsOrdersVO.setCurrency(rightsOrder.getRightsCurrency());
        //抵扣金额
        rightsOrdersVO.setDiscountAmount(rightsOrder.getDeductionAmount());
        //实际支付金额
        rightsOrdersVO.setActualPayment(rightsOrder.getActualAmount());
        /************************************** 票券编号是否存在 ****************************************/
        for (RightsUserGrant rightsUserGrant : list) {
            if (rightsUserGrant == null || rightsUserGrant.getTicketStatus() != 2) {
                log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】票券不存在或已使用", verificationCancleDTO.getTicketId());
                throw new BusinessException(EResultEnum.TICKET_NOT_EXTENT.getCode());
            }
            /***************************** 判单当前编号是否归属当前商户或当前店铺 *****************************/
            if (!verificationCancleDTO.getInstitutionId().equals(rightsUserGrant.getInstitutionId())
                    || rightsUserGrant.getMerchantId().contains(verificationCancleDTO.getMerchantId())) {
                log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】票券不属于当前商户或当前店铺", verificationCancleDTO.getTicketId());
                throw new BusinessException(EResultEnum.TICKET_INFO_EXCEPTION.getCode());
            }
            /******************************* 判断活动使用日期和不可以使用日期 **********************************/
            Date now = new Date();
            if (now.getTime() < rightsUserGrant.getStartTime().getTime() || now.getTime() > rightsUserGrant.getEndTime().getTime()) {
                log.info("===================== 权益退款(发放平台)API--活动时间 =================== 票券编号：【{}】票券期限异常", verificationCancleDTO.getTicketId());
                throw new BusinessException(EResultEnum.TICKET_TIME_EXCEPTION.getCode());
            }

            if (StringUtils.isNotEmpty(rightsUserGrant.getExt4())) {
                String[] s = rightsUserGrant.getExt4().split(",");
                for (String time : s) {
                    Date unableDate = DateToolUtils.getDateByStr(time.trim());
                    if (DateToolUtils.compareDate(new Date(), unableDate, Calendar.DATE) == 0) {
                        log.info("===================== 权益退款(发放平台)API--不可使用时间=================== 票券编号：【{}】票券期限异常", verificationCancleDTO.getTicketId());
                        throw new BusinessException(EResultEnum.TICKET_TIME_EXCEPTION.getCode());
                    }
                }
            }

            /********************************  修改票券状态 *********************************/
            if (rightsUserGrantMapper.updateTicketStatus(rightsUserGrant.getId(), TradeConstant.TICKETS_USE) == 1) {
                log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】权益核销成功", verificationCancleDTO.getTicketId());
                rightsOrder.setStatus(TradeConstant.HX_SUCCESS);
                rightsOrdersVO.setStatus("SUCCESS");
                baseResponse.setCode("200");
                baseResponse.setMsg("SUCCESS");
                baseResponse.setData(rightsOrdersVO);
                rightsGrantMapper.updateCancelVerificationAmount(rightsUserGrant.getDealId());
            } else {
                log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】权益核销失败", verificationCancleDTO.getTicketId());
                rightsOrder.setStatus(TradeConstant.HX_FAIL);
                rightsOrdersVO.setStatus("FAIL");
                baseResponse.setCode("10071");
                baseResponse.setMsg("FAIL");
                baseResponse.setData(rightsOrdersVO);
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        //try {
        //    HttpResponse httpResponse = HttpClientUtils.reqPost(verificationCancleDTO.getServerUrl(), rightsOrdersVO, null);
        //    if(httpResponse.getHttpStatus()!=200){
        //
        //    }
        //}catch (Exception e){
        //    log.info("------------------------- 权益核销回调异常 --------------------");
        //}
        //核销完成时间
        rightsOrdersVO.setUpdateTime(new Date());
        //核销完成时间
        rightsOrder.setUpdateTime(new Date());
        rightsOrdersMapper.insert(rightsOrder);
        return baseResponse;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 创建核销订单
     **/
    private Object[] creatRightsOrders(VerificationCancleDTO verificationCancleDTO) {
        RightsOrders rightsOrders = new RightsOrders();
        StringBuffer sb = new StringBuffer();
        List<RightsUserGrant> list = rightsUserGrantMapper.selectByTicketIds(verificationCancleDTO.getTicketId());
        if (list.size() == 0 || list.size() != verificationCancleDTO.getTicketId().size()) {
            log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】票券不存在或已使用", verificationCancleDTO.getTicketId());
            throw new BusinessException(EResultEnum.TICKET_NOT_EXTENT.getCode());
        }
        rightsOrders.setInstitutionId(verificationCancleDTO.getInstitutionId());
        rightsOrders.setMerchantId(verificationCancleDTO.getMerchantId());
        rightsOrders.setRequestOrderNo(verificationCancleDTO.getOrderNo());
        rightsOrders.setOrderNo(verificationCancleDTO.getOrderNo());
        if (list.get(0).getRightsType() == 2) {
            //折扣的场合，订单金额不能为空
            if(verificationCancleDTO.getOrderAmount()==null){
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //折扣时(折扣卷只有一张)
            BigDecimal amount = list.get(0).getDiscount().multiply(verificationCancleDTO.getOrderAmount());
            rightsOrders.setCancelVerificationAmount(amount);
            rightsOrders.setDeductionAmount(amount);
        } else if (list.get(0).getRightsType() == 1) {
            //满减
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal cpa = BigDecimal.ZERO;
            if (list.get(0).getOverlay()) {
                //叠加
                amount = list.stream().map(RightsUserGrant::getTicketAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                cpa = list.stream().sorted(Comparator.comparing(RightsUserGrant::getCapAmount).reversed()).collect(Collectors.toList()).get(0).getCapAmount();
                if (amount.compareTo(cpa) == 1) {
                    rightsOrders.setCancelVerificationAmount(cpa);
                    rightsOrders.setDeductionAmount(cpa);
                } else {
                    rightsOrders.setCancelVerificationAmount(amount);
                    rightsOrders.setDeductionAmount(amount);
                }
            } else {
                //不能叠加
                list = list.stream().sorted(Comparator.comparing(RightsUserGrant::getTicketAmount).reversed()).collect(Collectors.toList()).subList(0, 1);
                amount = list.get(0).getTicketAmount();
                rightsOrders.setCancelVerificationAmount(amount);
                rightsOrders.setDeductionAmount(amount);
            }
        } else {
            BigDecimal amount = BigDecimal.ZERO;
            if (list.get(0).getOverlay()) {
                //叠加
                amount = list.stream().map(RightsUserGrant::getTicketAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                rightsOrders.setCancelVerificationAmount(amount);
                rightsOrders.setDeductionAmount(amount);
            } else {
                //不能叠加
                list = list.stream().sorted(Comparator.comparing(RightsUserGrant::getTicketAmount).reversed()).collect(Collectors.toList()).subList(0, 1);
                amount = list.get(0).getTicketAmount();
                rightsOrders.setCancelVerificationAmount(amount);
                rightsOrders.setDeductionAmount(amount);
            }
        }

        if (verificationCancleDTO.getOrderAmount() != null) {
            rightsOrders.setOrderAmount(verificationCancleDTO.getOrderAmount());
            rightsOrders.setActualAmount(verificationCancleDTO.getOrderAmount().subtract(rightsOrders.getDeductionAmount()));
        }

        for (RightsUserGrant rightsUserGrant : list) {
            sb.append(rightsUserGrant.getTicketId());
            sb.append(",");
            rightsOrders.setTicketAmount(rightsUserGrant.getTicketAmount());
            rightsOrders.setInstitutionName(rightsUserGrant.getInstitutionName());
            rightsOrders.setMerchantName(rightsUserGrant.getMerchantName());
            rightsOrders.setDealId(rightsUserGrant.getDealId());
            rightsOrders.setSystemOrderId(rightsUserGrant.getSystemOrderId());
            rightsOrders.setRightsCurrency(rightsUserGrant.getRightsCurrency());
            rightsOrders.setSystemName(rightsUserGrant.getSystemName());
            rightsOrders.setRightsType(rightsUserGrant.getRightsType());
        }
        int num = sb.toString().lastIndexOf(",");
        rightsOrders.setTicketId(sb.toString().substring(0, num));
        rightsOrders.setTicketNum(verificationCancleDTO.getTicketId().size());
        rightsOrders.setStatus(TradeConstant.HX_WAIT);
        rightsOrders.setServerUrl(verificationCancleDTO.getServerUrl());
        rightsOrders.setEnabled(true);
        rightsOrders.setId("RO" + IDS.uniqueID());
        rightsOrders.setCreateTime(new Date());
        rightsOrders.setRemark(verificationCancleDTO.getRemark());
        Object[] objects = new Object[2];
        objects[0] = rightsOrders;
        objects[1] = list;
        return objects;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 权益核销分页查询
     **/
    @Override
    public PageInfo<RightsOrdersVO> pageRightsOrders(RightsOrdersDTO rightsOrdersDTO) {
        return new PageInfo<RightsOrdersVO>(rightsOrdersMapper.pageRightsOrders(rightsOrdersDTO));
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 导出权益核销
     **/
    @Override
    public List<RightsOrdersVO> exportRightsOrders(RightsOrdersExportDTO rightsOrdersDTO) {
        return rightsOrdersMapper.exportRightsOrders(rightsOrdersDTO);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/31
     * @Descripate 权益核销查询API
     **/
    @Override
    public List<RightsOrdersApiVO> selectRightsOrders(RightsOrdersOutDTO rightsOrdersDTO) {
        if (rightsOrdersDTO.getSign() == null || rightsOrdersDTO.getSignType() == null || rightsOrdersDTO.getInstitutionId() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (!commonService.checkUniversalSign(rightsOrdersDTO)) {
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        return rightsOrdersMapper.selectRightsOrders(rightsOrdersDTO);
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/2
     * @Descripate 权益退款(发放平台)API
     **/
    @Override
    @Transactional
    public BaseResponse sysRightsRefund(RightsRefundDTO rightsRefundDTO) {
        if (rightsRefundDTO.getSign() == null || rightsRefundDTO.getSignType() == null || rightsRefundDTO.getInstitutionId() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (commonService.checkUniversalSign(rightsRefundDTO)) {
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        RightsUserGrant rightsUserGrant = rightsUserGrantMapper.selectByTicketId(rightsRefundDTO.getTicketId());
        /************************************** 票券编号是否存在 ****************************************/
        if (rightsUserGrant == null || !(rightsUserGrant.getTicketStatus() == 2 || rightsUserGrant.getTicketStatus() == 4)) {
            log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】票券不存在或已使用", rightsRefundDTO.getTicketId());
            throw new BusinessException(EResultEnum.TICKET_NOT_EXTENT.getCode());
        }
        /**************************************** 判断票券金额 ****************************************/
        if (rightsRefundDTO.getAmount().compareTo(rightsUserGrant.getTicketAmount()) != 0) {
            log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】金额不合法", rightsRefundDTO.getTicketId());
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        /************************************** 创建权益退款单 ***************************************/
        RightsOrdersRefund rightsOrdersRefund = createRightsOrdersRefund(rightsRefundDTO, rightsUserGrant);
        /*************************************** 修改票券状态 *****************************************/
        if (rightsUserGrantMapper.updateTicketStatusRefund(rightsUserGrant.getId(), TradeConstant.TICKETS_REFUND) == 1) {
            log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】退款成功", rightsRefundDTO.getTicketId());
            rightsUserGrant.setTicketStatus(TradeConstant.TICKETS_REFUND);
            rightsOrdersRefund.setRefundStatus(TradeConstant.REFUND_SUCCESS);
            rightsOrdersRefund.setSysRefundTime(new Date());
            rightsOrdersRefundMapper.insert(rightsOrdersRefund);
            baseResponse.setMsg("SUCCESS");
            baseResponse.setCode("200");
            rightsGrantMapper.updateSurplusAmount(rightsUserGrant.getDealId());
        } else {
            log.info("===================== 权益退款(发放平台)API =================== 票券编号：【{}】退款失败", rightsRefundDTO.getTicketId());
            rightsOrdersRefund.setRefundStatus(TradeConstant.REFUND_FALID);
            rightsOrdersRefundMapper.insert(rightsOrdersRefund);
            baseResponse.setMsg("FAIL");
            baseResponse.setCode("10071");
        }
        return baseResponse;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/2
     * @Descripate 创建权益退款单
     **/
    private RightsOrdersRefund createRightsOrdersRefund(RightsRefundDTO rightsRefundDTO, RightsUserGrant rightsGrant) {
        RightsOrdersRefund rightsOrdersRefund = new RightsOrdersRefund();
        rightsOrdersRefund.setTicketId(rightsRefundDTO.getTicketId());
        rightsOrdersRefund.setTicketAmount(rightsRefundDTO.getAmount());
        rightsOrdersRefund.setSystemName(rightsGrant.getSystemName());
        rightsOrdersRefund.setRefundStatus(TradeConstant.REFUND_WAIT);
        rightsOrdersRefund.setMerRequsetTime(DateUtil.parse(rightsRefundDTO.getRequestTime(), "yyyy-MM-dd HH:mm:ss"));
        //rightsOrdersRefund.setSysRefundTime(new Date());
        rightsOrdersRefund.setId("RR" + IDS.uniqueID());
        rightsOrdersRefund.setCreateTime(new Date());
        //rightsOrdersRefund.setUpdateTime(new Date());
        //rightsOrdersRefund.setCreator("");
        //rightsOrdersRefund.setModifier("");
        //rightsOrdersRefund.setRemark("");
        return rightsOrdersRefund;
    }


    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/2
     * @Descripate 权益退款(机构)API
     **/
    @Override
    public BaseResponse insRightsRefund(RightsRefundDTO rightsRefundDTO) {
        if (rightsRefundDTO.getSign() == null || rightsRefundDTO.getSignType() == null || rightsRefundDTO.getInstitutionId() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        if (commonService.checkUniversalSign(rightsRefundDTO)) {
            throw new BusinessException(EResultEnum.DECRYPTION_ERROR.getCode());
        }
        BaseResponse baseResponse = new BaseResponse();
        RightsUserGrant rightsUserGrant = rightsUserGrantMapper.selectByTicketId(rightsRefundDTO.getTicketId());
        /************************************** 票券编号是否存在 ****************************************/
        if (rightsUserGrant == null || !(rightsUserGrant.getTicketStatus() == 2 || rightsUserGrant.getTicketStatus() == 4)) {
            log.info("===================== 权益退款(机构)API =================== 票券编号：【{}】票券不存在或已使用", rightsRefundDTO.getTicketId());
            throw new BusinessException(EResultEnum.TICKET_NOT_EXTENT.getCode());
        }
        /**************************************** 判断票券金额 ****************************************/
        if (rightsRefundDTO.getAmount().compareTo(rightsUserGrant.getTicketAmount()) != 0) {
            log.info("===================== 权益退款(机构)API =================== 票券编号：【{}】金额不合法", rightsRefundDTO.getTicketId());
            throw new BusinessException(EResultEnum.REFUND_AMOUNT_NOT_LEGAL.getCode());
        }
        /************************************** 创建权益退款单 ***************************************/
        RightsOrdersRefund rightsOrdersRefund = createRightsOrdersRefund(rightsRefundDTO, rightsUserGrant);

        /************************************** 判断OTA平台是否支持撤销 *****************************************/
        OtaChannel otaChannel = otaChannelMapper.selectBySystemName(rightsRefundDTO.getSystemName());
        log.info("===================== 权益退款(机构)API =================== OTA平台 otaChannel：【{}】", JSON.toJSONString(otaChannel));
        if (!otaChannel.getCancelDefault()) {
            log.info("===================== 权益退款(机构)API =================== 票券编号：【{}】OTA平台不支持支持撤销", rightsRefundDTO.getTicketId());
            throw new BusinessException(EResultEnum.OTA_NOT_SUPPORT_CANCLE.getCode());
        }

        /*************************************** 修改票券状态 *****************************************/
        if (rightsUserGrantMapper.updateTicketStatusRefund(rightsUserGrant.getId(), TradeConstant.TICKETS_REFUND) != 1) {
            log.info("===================== 权益退款(机构)API =================== 票券编号：【{}】退款失败", rightsRefundDTO.getTicketId());
            rightsOrdersRefund.setRefundStatus(TradeConstant.REFUND_FALID);
            rightsOrdersRefundMapper.insert(rightsOrdersRefund);
            baseResponse.setMsg("FAIL");
            baseResponse.setCode("10071");
            return baseResponse;
        }

        log.info("===================== 权益退款(机构)API =================== 票券编号：【{}】退款成功", rightsRefundDTO.getTicketId());
        rightsUserGrant.setTicketStatus(TradeConstant.TICKETS_REFUND);
        rightsOrdersRefund.setRefundStatus(TradeConstant.REFUND_SUCCESS);
        rightsOrdersRefund.setSysRefundTime(new Date());
        rightsOrdersRefundMapper.insert(rightsOrdersRefund);
        baseResponse.setMsg("SUCCESS");
        baseResponse.setCode("200");
        rightsGrantMapper.updateSurplusAmount(rightsUserGrant.getDealId());
        /*************************************** 修改成功调OTA平台撤销接口 ************************************/


        return baseResponse;
    }

    /**
     * 查询核销详情
     *
     * @param rightsOrdersDTO
     */
    @Override
    public RightsOrders getRightsOrdersInfo(RightsOrdersDTO rightsOrdersDTO) {
        return rightsOrdersMapper.selectByTicketId(rightsOrdersDTO);
    }
}
