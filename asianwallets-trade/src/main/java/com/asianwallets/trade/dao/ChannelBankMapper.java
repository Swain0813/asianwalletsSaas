package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ChannelBank;
import com.asianwallets.common.vo.ChaBankRelVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ChannelBankMapper extends BaseMapper<ChannelBank> {

}
