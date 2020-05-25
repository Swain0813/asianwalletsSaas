package com.asianwallets.trade.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.MerchantReport;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantReportMapper extends BaseMapper<MerchantReport> {

    /**
     * 根据商户编号和通道编号查询商户报备信息
     * @param channelCode
     * @param merchantId
     * @return
     */
    MerchantReport selectByChannelCodeAndMerchantId(@Param("merchantId") String merchantId,@Param("channelCode") String channelCode);
}