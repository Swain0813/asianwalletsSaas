package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.SearchChannelDTO;
import com.asianwallets.common.entity.MerchantChannel;
import com.asianwallets.common.vo.MerChannelVO;
import com.asianwallets.common.vo.MerchantRelevantVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantChannelMapper extends BaseMapper<MerchantChannel> {

}
