package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.MerchantCardCodeMapper;
import com.asianwallets.base.service.CommonService;
import com.asianwallets.base.service.MerchantCardCodeService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.MerchantCardCodeDTO;
import com.asianwallets.common.entity.Merchant;
import com.asianwallets.common.entity.MerchantCardCode;
import com.asianwallets.common.entity.Product;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.MerchantCardCodeVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import java.util.Date;

@Service
public class MerchantCardCodeServiceImpl implements MerchantCardCodeService {

    @Autowired
    private MerchantCardCodeMapper merchantCardCodeMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonService commonService;

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
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        MerchantCardCode merchantCardCode = merchantCardCodeMapper.getMerchantCardCode(merchantCardCodeDTO);
        return merchantCardCode;
    }

    /**
     * 修改商户码牌信息
     * @param userName
     * @param merchantCardCodeDTO
     * @return
     */
    @Override
    public int updateMerchantCardCode(String userName, MerchantCardCodeDTO merchantCardCodeDTO) {
        /**
         * 码牌id
         */
        if(StringUtils.isEmpty(merchantCardCodeDTO.getId())){
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        int num;
        //创建商户码牌对象
        MerchantCardCode merchantCardCode = new MerchantCardCode();
        BeanUtils.copyProperties(merchantCardCodeDTO,merchantCardCode);
        //修改时间
        merchantCardCode.setUpdateTime(new Date());
        //修改人
        merchantCardCode.setModifier(userName);
        //码牌id
        merchantCardCode.setId(merchantCardCodeDTO.getId());
        num=merchantCardCodeMapper.updateByPrimaryKeySelective(merchantCardCode);
        try {
            //更新商户码牌信息后添加的redis里
            redisService.set(AsianWalletConstant.MERCHANT_CARD_CODE.concat("_").concat(merchantCardCodeDTO.getId()), JSON.toJSONString(merchantCardCode));
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }

    /**
     * 查看商户静态码
     * @param merchantCardCodeDTO
     * @return
     */
    @Override
    public MerchantCardCodeVO selectMerchantCardCode(MerchantCardCodeDTO merchantCardCodeDTO) {
        /**
         * 码牌id
         */
        if(StringUtils.isEmpty(merchantCardCodeDTO.getId())){
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        MerchantCardCode merchantCardCode = merchantCardCodeMapper.getMerchantCardCode(merchantCardCodeDTO);
        if(merchantCardCode==null){
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        Merchant merchant = commonService.getMerchant(merchantCardCode.getMerchantId());
        //返回结果
        MerchantCardCodeVO merchantCardCodeVO =new MerchantCardCodeVO();
        merchantCardCodeVO.setMerchantLogo(merchant.getMerchantLogo());
        merchantCardCodeVO.setMerchantName(merchant.getCnName());
        merchantCardCodeVO.setQrcodeUrl(merchantCardCode.getQrcodeUrl());
        merchantCardCodeVO.setQrcodeId(merchantCardCode.getId());
        String [] productCodes = merchantCardCode.getProductCode().split(",");
        String productImgs=null;
        for (String productCode:productCodes){
            Product product = JSON.parseObject(redisService.get(AsianWalletConstant.PRODUCT_CACHE_CODE_KEY.concat("_") + productCode), Product.class);
            if(productImgs==null){
                productImgs=product.getProductImg();
            }else {
                productImgs=productImgs.concat(",").concat(product.getProductImg());
            }
        }
        merchantCardCodeVO.setProductImgs(productImgs);
        return merchantCardCodeVO;
    }


}
