package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.asianwallets.base.dao.*;
import com.asianwallets.base.service.MerchantProductService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.*;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.ChaBankRelVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yx
 * @since 2019-12-09
 */
@Slf4j
@Service
@Transactional
public class MerchantProductServiceImpl extends BaseServiceImpl<MerchantProduct> implements MerchantProductService {

    @Autowired
    private MerchantProductMapper merchantProductMapper;
    @Autowired
    private MerchantProductAuditMapper merchantProductAuditMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private MerchantChannelMapper merchantChannelMapper;

    /**
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 添加商户产品
     * @return
     **/
    @Override
    public int addMerchantProduct(String name, List<MerchantProductDTO> merchantProductDTOs) {
        List<MerchantProduct> list = Lists.newArrayList();
        List<MerchantProductAudit> listAudit = Lists.newArrayList();

        for (MerchantProductDTO merchantProductDTO : merchantProductDTOs) {
            //查询商户是否已经分配产品
            if (merchantProductMapper.selectCountbyMerIdProId(merchantProductDTO.getMerchantId(), merchantProductDTO.getProductId()) > 0) {
                continue;
            }

            //根据当前商户id获取代理商信息
            Merchant merchant = merchantMapper.selectByPrimaryKey(merchantProductDTO.getMerchantId());
            if (!StringUtils.isEmpty(merchant.getAgentId())) {
                Merchant agentMerchant = merchantMapper.selectByPrimaryKey(merchant.getAgentId());
                MerchantProduct agentMerchantProduct = merchantProductMapper.getMerchantProductByMerIdAndProId(agentMerchant.getId(), merchantProductDTO.getProductId());
                if (agentMerchantProduct != null && agentMerchantProduct.getRateType().equals(agentMerchantProduct.getRateType())) {
                    //单笔定额的场合
                    if (TradeConstant.FEE_TYPE_QUOTA.equals(merchantProductDTO.getRateType())) {
                        if (merchantProductDTO.getRate().compareTo(agentMerchantProduct.getRate()) == -1) {
                            throw new BusinessException(EResultEnum.RATE_IS_ILLEGAL.getCode());
                        }
                    } else if (TradeConstant.FEE_TYPE_RATE.equals(merchantProductDTO.getRateType())) {
                        //单笔费率
                        if (merchantProductDTO.getRate().compareTo(agentMerchantProduct.getRate()) == -1 ||
                                merchantProductDTO.getMinTate().compareTo(agentMerchantProduct.getMinTate()) == -1 ||
                                merchantProductDTO.getMaxTate().compareTo(agentMerchantProduct.getMaxTate()) == -1) {
                            throw new BusinessException(EResultEnum.RATE_IS_ILLEGAL.getCode());
                        }
                    }
                }else if(agentMerchantProduct != null && !agentMerchantProduct.getRateType().equals(agentMerchantProduct.getRateType())){
                    //商户的产品费率类型和代理商的产品费率类型不一致
                    throw new BusinessException(EResultEnum.RATE_TYPE_IS_DIFFERENT.getCode());
                }
            }

            String id = IDS.uuid2();
            MerchantProduct merchantProduct = new MerchantProduct();
            BeanUtils.copyProperties(merchantProductDTO, merchantProduct);
            merchantProduct.setId(id);
            merchantProduct.setCreateTime(new Date());
            merchantProduct.setCreator(name);
            merchantProduct.setEnabled(false);
            merchantProduct.setAuditStatus(TradeConstant.AUDIT_WAIT);

            MerchantProductAudit merchantProductAudit = new MerchantProductAudit();
            BeanUtils.copyProperties(merchantProduct, merchantProductAudit);

            list.add(merchantProduct);
            listAudit.add(merchantProductAudit);

        }
        if (list != null && list.size() > 0) {
            merchantProductMapper.insertList(list);
            return merchantProductAuditMapper.insertList(listAudit);
        } else {
            throw new BusinessException(EResultEnum.PAYMENTMODE_EXIST.getCode());
        }
    }

    /**
     * @Author YangXu
     * @Date 2019/12/9
     * @Descripate 商户分配通道
     * @return
     **/
    @Override
    public int allotMerProductChannel(String username, MerProDTO merProDTO) {
        log.info("----------------- 商户分配通道 ---------------- username : {},merProDTO : {} ", username, JSONObject.toJSONString(merProDTO));
        //必填的check
        if (merProDTO == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //必填的check
        if (StringUtils.isEmpty(merProDTO.getMerchantId()) || StringUtils.isEmpty(merProDTO.getProductList()) || merProDTO.getProductList().size() == 0) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        List<MerchantChannel> list = Lists.newArrayList();
        for (ProdChannelDTO prodChannelDTO : merProDTO.getProductList()) {
            //根据商户id与产品id查询商户产品中间表id
            MerchantProduct merchantProduct = merchantProductMapper.getMerchantProductByMerIdAndProId(merProDTO.getMerchantId(), prodChannelDTO.getProductId());
            List<MerchantChannel> list1 = merchantChannelMapper.selectByMerProId(merchantProduct.getId());
            //根据商户产品中间表id删除机构通道信息
            merchantChannelMapper.deleteByMerProId(merchantProduct.getId());
            for (ChannelInfoDTO channelInfoDTO : prodChannelDTO.getChannelList()) {
                for (BankInfoDTO bankInfoDTO : channelInfoDTO.getBankList()) {
                    //ChaBankRelVO chaBankRelVO = channelBankMapper.getInfoByCIdAndBId(channelInfoDTO.getChannelId(), bankInfoDTO.getBankId());


                }

            }
        }

        return 0;
    }
}
