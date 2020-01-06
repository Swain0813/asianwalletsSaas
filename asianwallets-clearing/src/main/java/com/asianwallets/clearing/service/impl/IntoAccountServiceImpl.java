package com.asianwallets.clearing.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.dao.TcsSysConstMapper;
import com.asianwallets.clearing.service.CommonService;
import com.asianwallets.clearing.service.IntoAccountService;
import com.asianwallets.clearing.service.TCSCtFlowService;
import com.asianwallets.clearing.service.TCSStFlowService;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-07-25 11:39
 **/
@Slf4j
@Service
public class IntoAccountServiceImpl implements IntoAccountService {
    @Autowired
    private TcsSysConstMapper tcsSysConstMapper;

    @Autowired
    private TCSStFlowService tcsStFlowService;

    @Autowired
    private TCSCtFlowService tcsCtFlowService;

    @Autowired
    private CommonService commonService;

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/25
     * @Descripate 资金变动接口
     **/
    @Override
    public FundChangeDTO intoAndOutMerhtAccount(FundChangeDTO ioma) {
        FundChangeDTO repqo = new FundChangeDTO();
        try {
            if (!(ioma != null && ioma.getMerchantid() != null && !ioma.getMerchantid().equals("") && ioma.getIsclear() > 0
                    && ioma.getMerOrderNo() != null && !ioma.getMerOrderNo().equals("") && ioma.getTxncurrency() != null
                    && !ioma.getTxncurrency().equals("") && ioma.getTxnamount() != 0 && ioma.getShouldDealtime() != null && !ioma.getShouldDealtime().equals("")
                    && ioma.getBalancetype() > 0 && ioma.getSignMsg() != null && !ioma.getSignMsg().equals("") && ioma.getFeecurrency() != null
                    && !ioma.getFeecurrency().equals("") && ioma.getSltcurrency() != null && !ioma.getSltcurrency().equals("") && ioma.getSltamount() != 0
                    && ioma.getChannelCostcurrency() != null && !ioma.getChannelCostcurrency().equals("") && ioma.getTradetype() != null
                    && !ioma.getTradetype().equals("") && ioma.getTxnexrate() > 0)) {
                //提交的报文为空
                repqo.setRespCode(Const.CSCode.CODE_CS0000);
                repqo.setRespMsg(Const.CSCode.MSG_CS0000);
                log.info("*********************************提交的报文为空****************************");
                return repqo;
            }
            //判断清结算类型
            if (!(ioma.getIsclear() == 1 || ioma.getIsclear() == 2)) {
                //是否清算资金参数有误
                repqo.setRespCode(Const.CSCode.CODE_CS0004);
                repqo.setRespMsg(Const.CSCode.MSG_CS0004);
                log.info("*********************************提交的是否清算资金参数有误****************************");
                return repqo;
            }
            //判断资金类型
            if (!(ioma.getBalancetype() == 1 || ioma.getBalancetype() == 2)) {
                //提交的资金类型有误
                repqo.setRespCode(Const.CSCode.CODE_CS0006);
                repqo.setRespMsg(Const.CSCode.MSG_CS0006);
                log.info("*********************************提交的资金类型有误****************************");
                return repqo;
            }
            //判断去掉清算资金加冻结这种特殊情况
            if (ioma.getIsclear() == 1 && ioma.getBalancetype() == 2) {
                //清算资金中不能加冻结
                repqo.setRespCode(Const.CSCode.CODE_CS00040);
                repqo.setRespMsg(Const.CSCode.MSG_CS00040);
                log.info("*********************************清算资金中不能加冻结****************************");
                return repqo;
            }
            ////手续费币种，通道成本币种和结算币种必须一致
            //if (!ioma.getSltcurrency().equals(ioma.getFeecurrency()) || !ioma.getSltcurrency().equals(ioma.getChannelCostcurrency())) {
            //    //手续费币种，通道成本必须和结算币种一致
            //    repqo.setRespCode(Const.CSCode.CODE_CS00014);
            //    repqo.setRespMsg(Const.CSCode.MSG_CS00014);
            //    log.info("*********************************手续费币种，通道成本必须和结算币种一致****************************");
            //    return repqo;
            //}
            //判断交易金额和结算金额的是否正负号相同，只能同为负或者同为正
            if ((ioma.getTxnamount() >= 0 & ioma.getSltamount() < 0) || (ioma.getTxnamount() < 0 & ioma.getSltamount() >= 0)) {
                //交易金额和结算金额正负号必须一致
                repqo.setRespCode(Const.CSCode.CODE_CS00015);
                repqo.setRespMsg(Const.CSCode.MSG_CS00015);
                log.info("*********************************交易金额和结算金额正负号必须一致****************************");
                return repqo;
            }
            //判断商户信息是否存在
            try {
                Merchant merchant = commonService.getMerchantInfo(ioma.getMerchantid());
                log.info("*******************************调用资金变动接口的商户信息*******************institution:{}", JSON.toJSON(merchant));
            } catch (Exception e) {
                repqo.setRespCode(Const.CSCode.CODE_CS0005);
                repqo.setRespMsg(Const.CSCode.MSG_CS0005);
                log.info("********************************* 商户查询信息异常 ****************************" + ioma.getMerchantid());
                return repqo;
            }
            //组装签名map
            Map<String, String> m = doStrSingMap(ioma, repqo);
            //验证签名
            String singstr = SignTools.getSignStr(m);
            if (singstr == null || singstr.equals("")) {
                //拼装签名字符串异常
                log.info("********************************* 拼装签名字符串异常 ****************************");
                repqo.setRespCode(Const.CSCode.CODE_CS00039);
                repqo.setRespMsg(Const.CSCode.MSG_CS00039);
                return repqo;
            }
            String md5key = tcsSysConstMapper.getCSAPI_MD5Key();
            if (md5key == null || md5key.equals("")) {
                //获取常量表中MD5Key异常
                log.info("********************************* 获取常量表中MD5Key异常 ****************************");
                repqo.setRespCode(Const.CSCode.CODE_CS00039);
                repqo.setRespMsg(Const.CSCode.MSG_CS00039);
                return repqo;
            }
            log.info("CSCenter----IntoAccountAction---MD5key：" + md5key);
            log.info("CSCenter----IntoAccountAction签名前的明文：" + md5key + singstr);
            String sign = MD5.MD5Encode(md5key + singstr);
            log.info("CSCenter----IntoAccountAction签名后的密文：" + sign);
            if (ioma.getSignMsg() == null || !ioma.getSignMsg().equals(sign)) {
                repqo.setRespCode(Const.CSCode.CODE_CS0008);
                repqo.setRespMsg(Const.CSCode.MSG_CS0008);
                log.info("调用方提交签名信息和系统验证签名信息不一致，上送签名信息 : {}, 系统验证签名信息 : {}", ioma.getSignMsg(), sign);
                return repqo;
            }
            BaseResponse baseResponse = null;
            if (ioma.getIsclear() == 1) {//表示是清算资金
                baseResponse = tcsCtFlowService.IntoAndOutMerhtSTAccount2(ioma);
            } else if (ioma.getIsclear() == 2) {//表示是结算资金
                baseResponse = tcsStFlowService.IntoAndOutMerhtSTAccount2(ioma);
            } else {
                repqo.setRespCode(Const.CSCode.CODE_CS0006);
                repqo.setRespMsg(Const.CSCode.MSG_CS0006);
                log.info("*********************** 提交的资金类型有误 ******************************");
            }
            Map<String, String> m2 = new HashMap<String, String>();
            String repsingstr = null;
            String repsign = null;
            if (baseResponse == null || !baseResponse.getCode().equals(Const.Code.OK)) {
                repqo.setRespCode(Const.CSCode.CODE_CS0009);
                repqo.setRespMsg(Const.CSCode.MSG_CS0009);
                m2.put("respCode", Const.CSCode.CODE_CS0009);
                m2.put("respMsg", Const.CSCode.MSG_CS0009);
                repsingstr = SignTools.getSignStr(m2);
                repsign = MD5.MD5Encode(md5key + repsingstr);
                log.info("***************  IntoAndOutMerhtCLAccount2 **************** 查询返回签名明文：{}", repsingstr);
                log.info("***************  IntoAndOutMerhtCLAccount2 **************** 查询返回签名密文：{}", repsign);
                repqo.setSignMsg(repsign);
                throw new BusinessException(EResultEnum.ERROR.getCode());
            }
            repqo.setRespCode("T000");
            m2.put("respCode", "T000");
            repqo.setRespMsg("success");
            m2.put("respMsg", "success");
            repsingstr = SignTools.getSignStr(m2);
            repsign = MD5.MD5Encode(md5key + repsingstr);
            log.info("***************  IntoAndOutMerhtCLAccount2 **************** 查询返回签名明文：{}", repsingstr);
            log.info("***************  IntoAndOutMerhtCLAccount2 **************** 查询返回签名密文：{}", repsign);
            repqo.setSignMsg(repsign);
        } catch (Exception e) {
            log.info("*************** 清算 IntoAndOutMerhtCLAccount2 **************** Exception：{}", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
        return repqo;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/25
     * @Descripate 组装签名的map
     **/
    public Map<String, String> doStrSingMap(FundChangeDTO ioma, FundChangeDTO repqo) {
        //拼装签名参数
        repqo.setMerchantid(ioma.getMerchantid());
        repqo.setIsclear(ioma.getIsclear());
        repqo.setRefcnceFlow(ioma.getRefcnceFlow());
        repqo.setTradetype(ioma.getTradetype());
        repqo.setMerOrderNo(ioma.getMerOrderNo());
        repqo.setTxncurrency(ioma.getTxncurrency());
        repqo.setTxnamount(ioma.getTxnamount());
        repqo.setShouldDealtime(ioma.getShouldDealtime());
        repqo.setSysorderid(ioma.getSysorderid());
        repqo.setFee(ioma.getFee());
        repqo.setChannelCost(ioma.getChannelCost());
        repqo.setBalancetype(ioma.getBalancetype());
        repqo.setTxndesc(ioma.getTxndesc());
        repqo.setTxnexrate(ioma.getTxnexrate());
        repqo.setRemark(ioma.getRemark());
        repqo.setSltamount(ioma.getSltamount());
        repqo.setSltcurrency(ioma.getSltcurrency());
        repqo.setFeecurrency(ioma.getFeecurrency());
        repqo.setChannelCostcurrency(ioma.getChannelCostcurrency());
        repqo.setGatewayFee(ioma.getGatewayFee());
        //退还收单手续费的币种
        repqo.setRefundOrderFeeCurrency(ioma.getRefundOrderFeeCurrency());
        //退还收单手续费
        repqo.setRefundOrderFee(ioma.getRefundOrderFee());
        Map<String, Object> paramMap = ReflexClazzUtils.getFieldNames(ioma);
        HashMap<String, String> m1 = new HashMap<>();
        for (String str : paramMap.keySet()) {
            if (!str.equals("signMsg")) {
                m1.put(str, String.valueOf(paramMap.get(str)));
            }
        }
        return m1;
    }


}
