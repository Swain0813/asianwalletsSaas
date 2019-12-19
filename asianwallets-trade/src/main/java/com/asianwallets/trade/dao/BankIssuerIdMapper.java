package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.BankIssuerIdDTO;
import com.asianwallets.common.entity.BankIssuerId;
import com.asianwallets.common.vo.BankIssuerIdVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BankIssuerIdMapper extends BaseMapper<BankIssuerId> {

    /**
     * 通过通道编号查询一条银行机构映射(limit 1)
     *
     * @param channelCode 通道编号
     * @return 银行机构映射
     */
    BankIssuerId selectByChannelCode(String channelCode);
}