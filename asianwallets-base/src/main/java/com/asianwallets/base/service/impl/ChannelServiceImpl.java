package com.asianwallets.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.ChannelBankMapper;
import com.asianwallets.base.dao.ChannelMapper;
import com.asianwallets.base.dao.ProductChannelMapper;
import com.asianwallets.base.dao.ProductMapper;
import com.asianwallets.base.service.ChannelService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.AgentChannelsDTO;
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
import com.asianwallets.common.vo.AgentChannelsVO;
import com.asianwallets.common.vo.ChannelDetailVO;
import com.asianwallets.common.vo.ChannelExportVO;
import com.asianwallets.common.vo.ChannelVO;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        for (ChannelBank channelBank : channelBankList) {
            redisService.set(AsianWalletConstant.CHANNEL_BANK_CACHE_KEY.concat("_").concat(channelBank.getId()), JSON.toJSONString(channelBank));
        }
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
    @Transactional
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
        //原数据库银行关联数据
        List<ChannelBank> originalList = channelBankMapper.selectByChannelId(channelDTO.getChannelId());
        //需要添加数据
        List<ChannelBank> channelBankList = new ArrayList<>();
        for (String bankId : channelDTO.getBankIdList()) {
            boolean flag = true;
            for (ChannelBank channelBank : originalList) {
                if (bankId.equals(channelBank.getBankId())) {
                    flag = false;
                    channelBankList.add(channelBank);
                }
            }
            if (flag) {
                ChannelBank channelBank = new ChannelBank();
                channelBank.setId(IDS.uuid2());
                channelBank.setBankId(bankId);
                channelBank.setChannelId(channelDTO.getChannelId());
                channelBank.setCreator(username);
                channelBank.setEnabled(true);
                channelBank.setCreateTime(new Date());
                channelBankList.add(channelBank);
            }
        }
        channelBankMapper.deleteByChannelId(channelDTO.getChannelId());
        channelBankMapper.insertList(channelBankList);
        //同步Redis
        redisService.set(AsianWalletConstant.CHANNEL_CACHE_KEY.concat("_").concat(channel.getId()), JSON.toJSONString(channel));
        redisService.set(AsianWalletConstant.CHANNEL_CACHE_CODE_KEY.concat("_").concat(channel.getChannelCode()), JSON.toJSONString(channel));
        for (ChannelBank channelBank : channelBankList) {
            redisService.set(AsianWalletConstant.CHANNEL_BANK_CACHE_KEY.concat("_").concat(channelBank.getId()), JSON.toJSONString(channelBank));
        }
        return channelMapper.updateByPrimaryKeySelective(channel);
    }

    /**
     * 分页查询通道信息
     *
     * @param channelDTO 通道输入实体
     * @return 修改条数
     */
    @Override
    public PageInfo<ChannelVO> pageFindChannel(ChannelDTO channelDTO) {
        channelDTO.setLanguage(auditorProvider.getLanguage());
        channelDTO.setSort("ch.create_time");
        return new PageInfo<>(channelMapper.pageFindChannel(channelDTO));
    }

    /**
     * 根据通道ID查询通道详情
     *
     * @param channelId 通道ID
     * @return 通道详情输出实体
     */
    @Override
    public ChannelDetailVO getChannelById(String channelId) {
        return channelMapper.selectByChannelId(channelId, auditorProvider.getLanguage());
    }

    /**
     * 导出通道信息
     *
     * @param channelDTO 通道输入实体
     * @return List<ChannelExportVO>
     */
    @Override
    public List<ChannelExportVO> exportChannel(ChannelDTO channelDTO) {
        channelDTO.setLanguage(auditorProvider.getLanguage());
        List<ChannelExportVO> channelExportVOList = channelMapper.exportChannel(channelDTO);
        for (ChannelExportVO channelExportVO : channelExportVOList) {
            if ("1".equals(channelExportVO.getRefundingIsReturnFee())) {
                channelExportVO.setRefundingIsReturnFee("退还");
            } else if ("2".equals(channelExportVO.getRefundingIsReturnFee())) {
                channelExportVO.setRefundingIsReturnFee("不退还");
            } else if ("3".equals(channelExportVO.getRefundingIsReturnFee())) {
                channelExportVO.setRefundingIsReturnFee("仅限当日退还");
            }
        }
        return channelExportVOList;
    }

    /**
     * 查询所有通道编号
     *
     * @return 通道编号集合
     */
    @Override
    public List<String> getAllChannelCode() {
        return channelMapper.selectAllChannelCode();
    }

    /**
     * 代理商渠道查询
     *
     * @param agentChannelsDTO
     * @return
     */
    @Override
    public PageInfo<AgentChannelsVO> pageAgentChannels(AgentChannelsDTO agentChannelsDTO) {
        return new PageInfo<>(channelMapper.pageAgentChannels(agentChannelsDTO));
    }
}
