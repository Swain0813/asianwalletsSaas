package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Mcc;
import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.MChannelVO;
import com.asianwallets.permissions.dao.ChannelMapper;
import com.asianwallets.permissions.dao.MccChannelMapper;
import com.asianwallets.permissions.dao.MccMapper;
import com.asianwallets.permissions.service.MccChannelFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author shenxinran
 * @Date: 2019/3/6 18:26
 * @Description: 设备信息
 */
@Service
@Transactional
public class MccChannelFeignServiceImpl implements MccChannelFeignService {

    @Autowired
    private MccMapper mccMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private MccChannelMapper mccChannelMapper;

    @Override
    public List<MccChannel> uploadMccChannel(MultipartFile file, String createName) {
        ArrayList<MccChannel> h = new ArrayList<>();
        // 判断格式0
        if (!file.getOriginalFilename().matches("^.+\\.(?i)(xls)$") && !file.getOriginalFilename().matches("^.+\\.(?i)(xlsx)$")) {
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        ExcelReader reader;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        List<List<Object>> read = reader.read();
        //判断是否超过上传限制
        if (read.size() - 1 > AsianWalletConstant.UPLOAD_LIMIT) {
            throw new BusinessException(EResultEnum.EXCEEDING_UPLOAD_LIMIT.getCode());
        }
        if (read.size() <= 0) {
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        for (int i = 1; i < read.size(); i++) {
            List<Object> objects = read.get(i);
            //判断传入的excel的格式是否符合约定
            if (StringUtils.isEmpty(objects.get(0))
                    || StringUtils.isEmpty(objects.get(1))
                    || StringUtils.isEmpty(objects.get(2))
                    || objects.size() != 3
            ) {
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
            MccChannel mcc = new MccChannel();
            Mcc mc = checkMccCode(objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            Channel channel = checkChannelCode(objects.get(1).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            if (mccChannelMapper.selectByCidAndMid(channel.getChannelCode(), mc.getExtend1()) != null) {
                throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
            }
            mcc.setId(IDS.uniqueID().toString());
            mcc.setCid(channel.getChannelCode());
            mcc.setMid(mc.getExtend1());
            mcc.setCode(objects.get(2).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            mcc.setCreateTime(new Date());
            mcc.setCreator(createName);
            mcc.setEnabled(true);
            h.add(mcc);
        }
        if (h.size() == 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        Set<MccChannel> set = new HashSet<>(h);
        if (set.size() != h.size()) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        return h;
    }

    /**
     * 校验 MCC CODE
     *
     * @param code
     * @return
     */
    private Mcc checkMccCode(String code) {
        Mcc mcc = mccMapper.selectByCode(code);
        if (mcc == null) {
            throw new BusinessException(EResultEnum.MCC_DOES_NOT_EXIST.getCode());
        }
        return mcc;
    }

    /**
     * 校验 CHANNEL CODE
     *
     * @param code
     * @return
     */
    private Channel checkChannelCode(String code) {
        Channel channel = channelMapper.selectByChannelCode(code);
        if (channel == null) {
            throw new BusinessException(EResultEnum.CHANNEL_IS_NOT_EXISTS.getCode());
        }
        return channel;
    }


    /**
     * 查询通道信息
     *
     * @return MChannelVO 这里面的CID指的是channel_code
     */
    @Override
    public List<MChannelVO> selectAllChannel() {
        return channelMapper.selectAllChannel();
    }
}
