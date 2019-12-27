package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.TcsSysConstMapper;
import com.asianwallets.base.feign.ClearingFeign;
import com.asianwallets.base.service.ClearingService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.utils.MD5Util;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.utils.SignTools;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class ClearingServiceImpl implements ClearingService {

    @Autowired
    private ClearingFeign clearingFeign;

    @Autowired
    private TcsSysConstMapper tcsSysConstMapper;

    /**
     * 资金变动接口
     * 场景支付成功后上报清结算系统
     *
     * @return
     */
    @Override
    public BaseResponse fundChange(FundChangeDTO fundChangeDTO) {
        log.info("------------  基础模块上报清结算 资金变动接口 ------------ fundChangeDTO : {} ", JSON.toJSON(fundChangeDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            //调用资金变动接口的签名
            fundChangeDTO.setSignMsg(createSignature(fundChangeDTO));
            FundChangeDTO fundChangeVO = clearingFeign.intoAndOutMerhtAccount(fundChangeDTO);
            log.info("------------ 基础模块上报清结算 资金变动接口 返回 ------------ fundChangeVO : {} ", JSON.toJSON(fundChangeVO));
            if (fundChangeVO != null && "T000".equals(fundChangeVO.getRespCode())) {
                baseResponse.setData(fundChangeVO);
                baseResponse.setMsg(fundChangeVO.getRespMsg());
                baseResponse.setCode(fundChangeVO.getRespCode());
            } else {
                baseResponse.setCode("T001");
            }
        } catch (Exception e) {
            log.info("************基础模块资金变动接口发生异常**************",e.getMessage());
            baseResponse.setCode("T001");
        }
        log.info("------------ 基础模块 上报清结算 返回 fundChange ------------ BaseResponse : {}", JSON.toJSON(baseResponse));
        return baseResponse;
    }

    /**
     * 资金冻结接口
     *
     * @return
     */
    @Override
    public BaseResponse freezingFunds(FinancialFreezeDTO financialFreezeDTO) {
        log.info("------------基础模块 上报清结算 资金冻结接口 ------------ financialFreezeDTO : {} ", JSON.toJSON(financialFreezeDTO));
        BaseResponse baseResponse = new BaseResponse();
        try {
            //调用资金冻结接口生成签名
            financialFreezeDTO.setSignMsg(createSignature(financialFreezeDTO));
            FinancialFreezeDTO financialFreezeVO = clearingFeign.CSFrozenFunds(financialFreezeDTO);
            log.info("------------基础模块 上报清结算 资金冻结接口 返回 ------------ financialFreezeVO : {} ", JSON.toJSON(financialFreezeVO));
            if (financialFreezeVO != null && "T000".equals(financialFreezeVO.getRespCode())) {
                baseResponse.setData(financialFreezeVO);
                baseResponse.setMsg(financialFreezeVO.getRespMsg());
                baseResponse.setCode(financialFreezeVO.getRespCode());
            } else {
                baseResponse.setCode("T001");
            }
        }catch (Exception e){
            log.info("*************基础模块资金冻结接口发生异常**************",e.getMessage());
            baseResponse.setCode("T001");
        }
        log.info("------------ 基础模块 交易项目 上报清结算 返回 fundChange ------------ BaseResponse : {}", JSON.toJSON(baseResponse));
        return baseResponse;
    }


    /**
     * 生成清结算接口签名
     *
     * @param obj 接口参数对象
     * @return 签名
     */
    private String createSignature(Object obj) {
        Map<String, Object> commonMap = ReflexClazzUtils.getFieldNames(obj);
        HashMap<String, String> paramMap = new HashMap<>();
        for (String str : commonMap.keySet()) {
            paramMap.put(str, String.valueOf(commonMap.get(str)));
        }
        //密文字符串拼装处理
        String str = SignTools.getSignStr(paramMap);
        String md5Key = tcsSysConstMapper.selectUrlByKey(AsianWalletConstant.CSAPI_MD5KEY);
        String signature = MD5Util.getMD5String(md5Key + str).toLowerCase();
        log.info("*******************基础模块 生成清结算接口签名: ****************", signature);
        return signature;
    }
}
