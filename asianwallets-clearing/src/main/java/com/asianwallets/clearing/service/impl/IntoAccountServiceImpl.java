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
            if (!(ioma != null && ioma.getVersion() != null && !ioma.getVersion().equals("") && ioma.getInputCharset() > 0
                    && ioma.getLanguage() > 0 && ioma.getMerchantid() != null && !ioma.getMerchantid().equals("") && ioma.getIsclear() > 0
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
            //判断版本号是否正常
            if (!ioma.getVersion().equalsIgnoreCase("v1.0")) {
                //提交的版本号有误
                repqo.setRespCode(Const.CSCode.CODE_CS0001);
                repqo.setRespMsg(Const.CSCode.MSG_CS0001);
                log.info("*********************************提交的版本号有误****************************Version=" + ioma.getVersion() + "**********");
                return repqo;
            }
            //判断字符集
            if (ioma.getInputCharset() == 0 || ioma.getInputCharset() > 3) {
                //提交的字符集有误");
                repqo.setRespCode(Const.CSCode.CODE_CS0002);
                repqo.setRespMsg(Const.CSCode.MSG_CS0002);
                log.info("*********************************提交的字符集有误****************************");
                return repqo;
            }
            //判断语言
            if (ioma.getLanguage() == 0 || ioma.getLanguage() > 3) {
                //提交的语言有误
                repqo.setRespCode(Const.CSCode.CODE_CS0003);
                repqo.setRespMsg(Const.CSCode.MSG_CS0003);
                log.info("*********************************提交的字符集有误****************************");
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
            //手续费币种，通道成本币种和结算币种必须一致
            if (!ioma.getSltcurrency().equals(ioma.getFeecurrency()) || !ioma.getSltcurrency().equals(ioma.getChannelCostcurrency())) {
                //手续费币种，通道成本必须和结算币种一致
                repqo.setRespCode(Const.CSCode.CODE_CS00014);
                repqo.setRespMsg(Const.CSCode.MSG_CS00014);
                log.info("*********************************手续费币种，通道成本必须和结算币种一致****************************");
                return repqo;
            }
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
                log.info("********************************* 商户查询信息异常 ****************************"+ioma.getMerchantid());
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
                repqo.setVersion(ioma.getVersion());
                m2.put("version", ioma.getVersion());
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
            repqo.setVersion(ioma.getVersion());
            m2.put("version", ioma.getVersion());
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
        Map<String, String> m1 = new HashMap<>();
        //拼装签名参数
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
        DecimalFormat decimalFormat5 = new DecimalFormat("###0.00000");//格式化设置
        m1.put("version", ioma.getVersion());
        repqo.setVersion(ioma.getVersion());
        m1.put("inputCharset", ioma.getInputCharset() + "");
        repqo.setInputCharset(ioma.getInputCharset());
        m1.put("language", ioma.getLanguage() + "");
        repqo.setLanguage(ioma.getLanguage());
        m1.put("merchantid", ioma.getMerchantid());
        repqo.setMerchantid(ioma.getMerchantid());
        m1.put("isclear", ioma.getIsclear() + "");
        repqo.setIsclear(ioma.getIsclear());
        m1.put("refcnceFlow", ioma.getRefcnceFlow());
        repqo.setRefcnceFlow(ioma.getRefcnceFlow());
        m1.put("tradetype", ioma.getTradetype());
        repqo.setTradetype(ioma.getTradetype());
        m1.put("merOrderNo", ioma.getMerOrderNo());
        repqo.setMerOrderNo(ioma.getMerOrderNo());
        m1.put("txncurrency", ioma.getTxncurrency());
        repqo.setTxncurrency(ioma.getTxncurrency());
        m1.put("txnamount", decimalFormat.format(ioma.getTxnamount()));
        repqo.setTxnamount(ioma.getTxnamount());
        m1.put("shouldDealtime", ioma.getShouldDealtime());
        repqo.setShouldDealtime(ioma.getShouldDealtime());
        m1.put("sysorderid", ioma.getSysorderid());
        repqo.setSysorderid(ioma.getSysorderid());
        m1.put("fee", decimalFormat.format(ioma.getFee()));
        repqo.setFee(ioma.getFee());
        m1.put("channelCost", decimalFormat.format(ioma.getChannelCost()));
        repqo.setChannelCost(ioma.getChannelCost());
        m1.put("balancetype", ioma.getBalancetype() + "");
        repqo.setBalancetype(ioma.getBalancetype());
        m1.put("txndesc", ioma.getTxndesc());
        repqo.setTxndesc(ioma.getTxndesc());
        m1.put("txnexrate", decimalFormat5.format(ioma.getTxnexrate()));
        repqo.setTxnexrate(ioma.getTxnexrate());
        m1.put("remark", ioma.getRemark());
        repqo.setRemark(ioma.getRemark());
        m1.put("sltamount", decimalFormat.format(ioma.getSltamount()));
        repqo.setSltamount(ioma.getSltamount());
        m1.put("sltcurrency", ioma.getSltcurrency());
        repqo.setSltcurrency(ioma.getSltcurrency());
        m1.put("feecurrency", ioma.getFeecurrency());
        repqo.setFeecurrency(ioma.getFeecurrency());
        m1.put("channelCostcurrency", ioma.getChannelCostcurrency());
        repqo.setChannelCostcurrency(ioma.getChannelCostcurrency());
        m1.put("gatewayFee", decimalFormat.format(ioma.getGatewayFee()));//交易状态手续费
        repqo.setGatewayFee(ioma.getGatewayFee());
        //退还收单手续费的币种
        m1.put("refundOrderFeeCurrency",ioma.getRefundOrderFeeCurrency());
        repqo.setRefundOrderFeeCurrency(ioma.getRefundOrderFeeCurrency());
        //退还收单手续费
        m1.put("refundOrderFee",decimalFormat.format(ioma.getRefundOrderFee()));
        repqo.setRefundOrderFee(ioma.getRefundOrderFee());
        return m1;
    }


}
