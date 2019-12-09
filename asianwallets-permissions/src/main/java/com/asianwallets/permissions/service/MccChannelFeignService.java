package com.asianwallets.permissions.service;

import com.asianwallets.common.entity.MccChannel;
import com.asianwallets.common.vo.MChannelVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/6 18:25
 * @Description: 设备信息Feign-Service
 */
public interface MccChannelFeignService {

    List<MccChannel> uploadMccChannel(MultipartFile file, String name);

    /**
     * 查询通道信息
     *
     * @return
     */
    List<MChannelVO> selectAllChannel();
}
