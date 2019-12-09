package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.base.dao.ChannelBankMapper;
import com.asianwallets.base.dao.ChannelMapper;
import com.asianwallets.base.dao.ProductChannelMapper;
import com.asianwallets.base.dao.ProductMapper;
import com.asianwallets.base.service.ChannelService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelBank;
import com.asianwallets.common.entity.Product;
import com.asianwallets.common.entity.ProductChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private ProductChannelMapper productChannelMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AuditorProvider auditorProvider;

    @Autowired
    private ChannelBankMapper channelBankMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加通道信息
     *
     * @param username   用户名
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    @Override
    @Transactional
    public int addChannel(String username, ChannelDTO channelDTO) {
        //校验产品信息
        if (ArrayUtil.isEmpty(channelDTO.getProductIdList())) {
            log.info("==========【添加通道信息】==========【产品信息为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //校验银行信息
        if (ArrayUtil.isEmpty(channelDTO.getBankIdList())) {
            log.info("==========【添加通道信息】==========【银行信息为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //判断添加通道信息是否存在
        Channel dbChannel = channelMapper.selectByNameAndCurrency(channelDTO.getChannelCnName(), channelDTO.getCurrency());
        if (dbChannel != null) {
            log.info("==========【添加通道信息】==========【通道信息已存在】");
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        //校验产品币种与通道币种是否一致
        for (String productId : channelDTO.getProductIdList()) {
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product == null) {
                log.info("==========【添加通道信息】==========【产品信息不存在】");
                throw new BusinessException(EResultEnum.GET_PRODUCT_INFO_ERROR.getCode());
            }
            if (!channelDTO.getCurrency().equals(product.getCurrency())) {
                log.info("==========【添加通道信息】==========【通道与产品币种不一致】");
                throw new BusinessException(EResultEnum.PRODUCT_CHANNEL_CURRENCY_NO_SAME.getCode());
            }
        }
        Channel channel = new Channel();
        BeanUtils.copyProperties(channelDTO, channel);
//        //通道手续费类型为单笔定额时,最大最小费率设为null
  /*      if (!StringUtils.isEmpty(channelDTO.getChannelFeeType()) && TradeConstant.FEE_TYPE_QUOTA.equals(channelDTO.getChannelFeeType())) {
            //通道手续费最小值
            channel.setChannelMinRate(null);
            //通道手续费最大值
            channel.setChannelMaxRate(null);
        }
        //通道网关手续费类型为单笔定额时,最大最小费率设为null
        if (!StringUtils.isEmpty(channelDTO.getChannelGatewayFeeType()) && TradeConstant.FEE_TYPE_QUOTA.equals(channelDTO.getChannelGatewayFeeType())) {
            //通道网关手续费最小值
            channel.setChannelGatewayMinRate(null);
            //通道网关手续费最大值
            channel.setChannelGatewayMaxRate(null);
        }*/
        String channelId = IDS.uuid2();
        channel.setId(channelId);
        channel.setChannelCode(IDS.uniqueID().toString());
        channel.setCreator(username);
        channel.setCreateTime(new Date());
        List<ProductChannel> productChannelList = Lists.newArrayList();
        //分配产品通道信息
        for (String productId : channelDTO.getProductIdList()) {
            ProductChannel productChannel = new ProductChannel();
            productChannel.setId(IDS.uuid2());
            productChannel.setProductId(productId);
            productChannel.setChannelId(channelId);
            productChannel.setCreator(username);
            productChannel.setCreateTime(new Date());
            productChannelList.add(productChannel);
        }
        productChannelMapper.insertList(productChannelList);
        //分配通道银行信息
        List<ChannelBank> channelBankList = Lists.newArrayList();
        for (String bankId : channelDTO.getBankIdList()) {
            ChannelBank channelBank = new ChannelBank();
            channelBank.setId(IDS.uuid2());
            channelBank.setBankId(bankId);
            channelBank.setChannelId(channelId);
            channelBank.setCreator(username);
            channelBank.setCreateTime(new Date());
            channelBank.setEnabled(true);
            channelBankList.add(channelBank);
        }
        channelBankMapper.insertList(channelBankList);
        //同步Redis
        redisService.set(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channel.getId()), JSON.toJSONString(channel));
        redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channel.getChannelCode()), JSON.toJSONString(channel));
        return channelMapper.insert(channel);
    }

    /**
     * 修改通道信息
     *
     * @param username   用户名
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    @Override
    public int updateChannel(String username, ChannelDTO channelDTO) {
        //校验产品信息
        if (ArrayUtil.isEmpty(channelDTO.getProductIdList())) {
            log.info("==========【修改通道信息】==========【产品信息为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //校验银行信息
        if (ArrayUtil.isEmpty(channelDTO.getBankIdList())) {
            log.info("==========【修改通道信息】==========【银行信息为空】");
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Channel channel = channelMapper.selectByPrimaryKey(channelDTO.getChannelId());
        if (channel == null) {
            log.info("==========【修改通道信息】==========【通道信息不存在】");
            throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
        }
        //校验产品币种与通道币种是否一致
        for (String productId : channelDTO.getProductIdList()) {
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product == null) {
                log.info("==========【修改通道信息】==========【产品信息不存在】");
                throw new BusinessException(EResultEnum.GET_PRODUCT_INFO_ERROR.getCode());
            }
            if (!channelDTO.getCurrency().equals(product.getCurrency())) {
                log.info("==========【修改通道信息】==========【通道与产品币种不一致】");
                throw new BusinessException(EResultEnum.PRODUCT_CHANNEL_CURRENCY_NO_SAME.getCode());
            }
        }
        BeanUtils.copyProperties(channelDTO, channel, ReflexClazzUtils.getNullPropertyNames(channelDTO));
        channel.setUpdateTime(new Date());
        channel.setModifier(username);
        //通道手续费类型为单笔定额时,最大最小费率设为null
  /*      if (channelDTO.getChannelFeeType() != null && TradeConstant.FEE_TYPE_QUOTA.equals(channelDTO.getChannelFeeType())) {
            channel.setChannelMinRate(null);//通道手续费最小值
            channel.setChannelMaxRate(null);//通道手续费最大值
        }
        //通道网关手续费类型为单笔定额时,最大最小费率设为null
        if (channelDTO.getChannelGatewayFeeType() != null && TradeConstant.FEE_TYPE_QUOTA.equals(channelDTO.getChannelGatewayFeeType())) {
            channel.setChannelGatewayMinRate(null);//通道网关手续费最小值
            channel.setChannelGatewayMaxRate(null);//通道网关手续费最大值
        }*/
        //删除产品通道关联关系
        productChannelMapper.deleteByChannelId(channelDTO.getChannelId());
        List<ProductChannel> productChannelList = Lists.newArrayList();
        for (String productId : channelDTO.getProductIdList()) {
            ProductChannel productChannel = new ProductChannel();
            productChannel.setId(IDS.uuid2());
            productChannel.setProductId(productId);
            productChannel.setChannelId(channel.getId());
            productChannel.setCreator(username);
            productChannel.setModifier(username);
            productChannel.setCreateTime(new Date());
            productChannel.setUpdateTime(new Date());
            productChannelList.add(productChannel);
        }
        productChannelMapper.insertList(productChannelList);
        //删除通道银行关联关系
        channelBankMapper.deleteByChannelId(channelDTO.getChannelId());
        List<ChannelBank> channelBankList = new ArrayList<>();
        for (String bankId : channelDTO.getBankIdList()) {
            ChannelBank channelBank = new ChannelBank();
            channelBank.setId(IDS.uuid2());
            channelBank.setBankId(bankId);
            channelBank.setChannelId(channelDTO.getChannelId());
            channelBank.setCreator(username);
            channelBank.setModifier(username);
            channelBank.setCreateTime(new Date());
            channelBank.setUpdateTime(new Date());
            channelBank.setEnabled(true);
            channelBankList.add(channelBank);
        }
        channelBankMapper.insertList(channelBankList);
        //同步Redis
        redisService.set(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channel.getId()), JSON.toJSONString(channel));
        redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channel.getChannelCode()), JSON.toJSONString(channel));
        return channelMapper.updateByPrimaryKeySelective(channel);
    }

    /**
     * 分页查询通道信息
     *
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    @Override
    public PageInfo<Channel> pageFindChannel(ChannelDTO channelDTO) {
        channelDTO.setLanguage(auditorProvider.getLanguage());
        channelDTO.setSort("ch.create_time");
        return new PageInfo<>(channelMapper.pageFindChannel(channelDTO));
    }
}
