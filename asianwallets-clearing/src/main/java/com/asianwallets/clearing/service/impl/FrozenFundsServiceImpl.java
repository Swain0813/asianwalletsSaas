package com.asianwallets.clearing.service.impl;
import com.asianwallets.clearing.constant.Const;
import com.asianwallets.clearing.dao.*;
import com.asianwallets.clearing.service.CommonService;
import com.asianwallets.clearing.service.FrozenFundsService;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-07-25 11:37
 **/
@Slf4j
@Service
public class FrozenFundsServiceImpl implements FrozenFundsService {


    @Autowired
    private TcsSysConstMapper tcsSysConstMapper;

    @Autowired
    private TCSFrozenFundsServiceImpl tcsFrozenFundsService;

    @Autowired
    private CommonService commonService;


    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 资金冻结/解冻接口
     **/
    @Override
    public FinancialFreezeDTO CSFrozenFunds(FinancialFreezeDTO ffr) {
        FinancialFreezeDTO repqo = new FinancialFreezeDTO();
        String md5key = tcsSysConstMapper.getCSAPI_MD5Key();
        if (md5key == null || md5key.equals("")) {
            //获取交易系统MD5Key为空
            log.info("*************** 冻结/解冻 CSFrozenFunds **************** 获取交易系统MD5Key为空，验证不通过,时间：{}", new Date());
            repqo.setRespCode(Const.Code.SELECT_FAILED);
            repqo.setRespMsg(Const.Code.SELECT_FAILED_MSG);
            return repqo;
        }

        try {
            BaseResponse dm = this.verificationAPIInputParamter(ffr, md5key);
            if (dm == null || !dm.getCode().equals(Const.Code.OK)) {
                //输入校验不通过
                repqo.setRespCode(dm.getCode());
                repqo.setRespMsg(dm.getMsg());
                log.info("*************** 冻结/解冻 CSFrozenFunds **************** 输入校验不通过，时间:{}", new Date());
                return repqo;
            }
            // 所有校验通过就进入冻结处理方法
            Object[] obj = (Object[]) dm.getData();
            if (obj == null || obj.length <= 1) {
                //输入校验通过,但是返回参数集合为空
                repqo.setRespCode(dm.getCode());
                repqo.setRespMsg(dm.getMsg());
                log.info("*************** 冻结/解冻 CSFrozenFunds **************** 输入校验通过,但是返回参数集合为空，时间：{}", new Date());
                return repqo;
            }
            TcsFrozenFundsLogs ffl = (TcsFrozenFundsLogs) obj[0];
            Merchant merchant = (Merchant) obj[1];
            if ( ffl == null || merchant == null) {
                //输入校验通过,但是返回商户虚拟户或者冻结记录为空
                repqo.setRespCode(dm.getCode());
                repqo.setRespMsg(dm.getMsg());
                log.info("*************** 冻结/解冻 CSFrozenFunds **************** 输入校验通过,但是返回商户虚拟户或者冻结记录为空，时间:{}", new Date());
                return repqo;
            }
            /***************************************** 清结算系统冻结/解冻处理方法 *****************************************/
            BaseResponse dm1 = tcsFrozenFundsService.frozenFundsLogs(ffl);
            if (dm1 == null || !dm1.getCode().equals(Const.Code.OK)) {
                //冻结处理过程失败
                repqo.setRespCode(dm1.getCode());
                repqo.setRespMsg(dm1.getCode());
                log.info("*************** 冻结/解冻 CSFrozenFunds **************** 冻结处理过程失败，时间:{}", new Date());
                throw new BusinessException(EResultEnum.ERROR.getCode());
            }
            ///签名使用的map
            Map<String, String> map = new HashMap<String, String>();
            //冻结处理成功，封装参数返回
            repqo.setRespCode(Const.Code.CODE_T000);
            map.put("respCode", Const.Code.CODE_T000);
            repqo.setRespMsg("success");
            map.put("respMsg", "success");
            repqo.setId(ffl.getId());
            map.put("id", ffl.getId());
            repqo.setMerchantId(String.valueOf(ffl.getMerchantId()));
            map.put("merchantId", String.valueOf(ffl.getMerchantId()));
            repqo.setMerOrderNo(String.valueOf(ffl.getMerOrderNo()));
            map.put("merOrderNo", String.valueOf(ffl.getMerOrderNo()));
            repqo.setTxncurrency(String.valueOf(ffl.getTxncurrency()));
            map.put("txncurrency", String.valueOf(ffl.getTxncurrency()));
            repqo.setTxnamount(ffl.getTxnamount());
            map.put("txnamount", String.valueOf(ffl.getTxnamount()));
            repqo.setState(ffl.getState());
            map.put("state", String.valueOf(ffl.getState()));
            String repsingstr = SignTools.getSignStr(map);
            String repsign = MD5.MD5Encode(md5key + repsingstr);
            log.info("*************** 冻结/解冻 CSFrozenFunds **************** #冻结资金接口返回签名明文：{}", repsingstr);
            log.info("*************** 冻结/解冻 CSFrozenFunds **************** #冻结资金接口返回签名密文：{}", repsign);
            repqo.setSignMsg(repsign);
        } catch (Exception e) {
            log.info("******************* CSFrozenFunds ******************** Exception：{} ", e);
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
        return repqo;
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/7/26
     * @Descripate 验证冻结接口请求参数方法
     **/
    public BaseResponse verificationAPIInputParamter(FinancialFreezeDTO ffr, String md5key) {
        log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** 开始时间：{}" + new Date());
        BaseResponse message = new BaseResponse();
        message.setCode(Const.Code.FAILED);// 默认失败
        Map<String, String> signmap = new HashMap<String, String>();//签名map
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
        if (ffr == null) {
            //请求参数为空
            message.setCode(Const.Code.CODE_NoParameter);
            message.setMsg(Const.Code.MSG_NoParameter);
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** 请求参数为空,时间：{}", new Date());
            return message;
        }
        //merchantId验证
        if (ffr.getMerchantId() == null || ffr.getMerchantId().equals("")) {
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #merchantId验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_MerchantIdIllegal);
            message.setMsg(Const.Code.MSG_MerchantIdIllegal);
            return message;
        }
        //merOrderNo验证
        if (ffr.getMerOrderNo() == null || ffr.getMerOrderNo().equals("") || ffr.getMerOrderNo().length() > 35) {
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #merOrderNo验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_MerOrderNoIllegal);
            message.setMsg(Const.Code.MSG_MerOrderNoIllegal);
            return message;
        }
        //txncurrency验证
        if (ffr.getTxncurrency() == null || ffr.getTxncurrency().equals("")) {
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #txncurrency验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_CurrencyIllegal);
            message.setMsg(Const.Code.MSG_CurrencyIllegal);
            return message;
        }
        //txnamount验证
        if (ffr.getTxnamount() == 0) {
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #txnamount验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_TxnamountIllegal);
            message.setMsg(Const.Code.MSG_TxnamountIllegal);
            return message;
        }
        ////mvaccountId验证
        //if (ffr.getMvaccountId() == null || ffr.getMvaccountId().equals("")) {
        //    log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #mvaccountId验证不通过,时间：{}", new Date());
        //    message.setCode(Const.Code.CODE_MvaccountIdIllegal);
        //    message.setMsg(Const.Code.MSG_MvaccountIdIllegal);
        //    return message;
        //}
        //state验证
        if (ffr.getState() != 1 && ffr.getState() != 2) {
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #state验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_StateIllegal);
            message.setMsg(Const.Code.MSG_StateIllegal);
            return message;
        }

        //state和资金方向验证
        if (ffr.getState() == 1 && ffr.getTxnamount() < 0) {
            //加冻结资金必须为正数
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #加冻结资金必须为正数，验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_TxnamountIllegal);
            message.setMsg(Const.Code.MSG_TxnamountIllegal + "加冻结资金必须为正数");
            return message;
        } else if (ffr.getState() == 2 && ffr.getTxnamount() > 0) {
            //解冻结，资金必须为负
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #解冻结资金必须为负,验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_TxnamountIllegal);
            message.setMsg(Const.Code.MSG_TxnamountIllegal + "解冻结资金必须为负数");
            return message;
        }
        //desc验证,可以为空
        //signMsg验证
        if (ffr.getSignMsg() == null || ffr.getSignMsg().equals("")) {
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #signMsg验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_SignMsgIllegal);
            message.setMsg(Const.Code.MSG_SignMsgIllegal);
            return message;
        }
        /*
         *非空校验之后需要做逻辑校验，交易机构，商户，账户，验证签名
         */
        //第一步查询机构，商户信息：
        Merchant merchant = null;
        try {
            merchant = commonService.getMerchantInfo(ffr.getMerchantId());
        } catch (Exception e) {
            //机构和商户关系不存在
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #机构和商户关系不存在 ，验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_OrganNotExitMid);
            message.setMsg(Const.Code.MSG_OrganNotExitMid);
            return message;
        }
        //检查签名
        signmap.put("merchantId", ffr.getMerchantId());
        signmap.put("state", ffr.getState() + "");
        signmap.put("desc", ffr.getDesc());
        signmap.put("merOrderNo", ffr.getMerOrderNo());
        signmap.put("txncurrency", ffr.getTxncurrency());
        signmap.put("txnamount", decimalFormat.format(ffr.getTxnamount()));
        String singstr = SignTools.getSignStr(signmap);
        if (singstr == null || singstr.equals("")) {
            //签名字符串为空
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #拼装签名字符串为空，验证不通过,时间：{}", new Date());
            return message;
        }

        log.info("S2S----com.cscenter.module.CSAPI.BLL.FrozenFundsLogsBLL#---MD5key：" + md5key);
        log.info("S2S----com.cscenter.module.CSAPI.BLL.FrozenFundsLogsBLL#签名前的明文：" + md5key + singstr);
        String sign = MD5.MD5Encode(md5key + singstr);
        log.info("S2S----com.cscenter.module.CSAPI.BLL.FrozenFundsLogsBLL#签名后的密文：" + sign);
        log.info("S2S----com.cscenter.module.CSAPI.BLL.FrozenFundsLogsBLL#商户上传密文：" + ffr.getSignMsg());
        if (!ffr.getSignMsg().equals(sign)) {
            //表示签名验证不成功
            log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** #签名验证不成功，验证不通过,时间：{}", new Date());
            message.setCode(Const.Code.CODE_RequestSignError);
            message.setMsg(Const.Code.MSG_RequestSignError);
            return message;
        }
        //封装冻结记录
        TcsFrozenFundsLogs ffl = new TcsFrozenFundsLogs();
        ffl.setId("FT" + IDS.uniqueID());
        ffl.setMerchantId(ffr.getMerchantId());
        ffl.setMerOrderNo(ffr.getMerOrderNo());
        //ffl.setMvaccountId(ffr.getMvaccountId());
        ffl.setState(ffr.getState());
        ffl.setTxnamount(ffr.getTxnamount());
        ffl.setTxncurrency(ffr.getTxncurrency());
        if (ffr.getState() == 1) {
            //加冻结
            ffl.setFrozenDatetime(new Date());
            ffl.setFrozenDesc(ffr.getDesc());
        } else if (ffr.getState() == 2) {
            //解冻结
            ffl.setUnfreezeDatetime(new Date());
            ffl.setUnfrozenDesc(ffr.getDesc());
            ffl.setUpdateDatetime(new Date());
        }
        message.setCode(Const.Code.OK);
        message.setMsg(Const.Code.OK_MSG);
        message.setData(new Object[]{ffl, merchant});
        log.info("*************** 冻结/解冻验参 verificationAPIInputParamter **************** 结束时间：{}", new Date());
        return message;
    }


}
