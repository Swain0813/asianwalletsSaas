package com.asianwallets.task.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.entity.SettleCheckAccount;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
  * 机构结算单表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2020-01-14
 */
@Repository
public interface SettleCheckAccountMapper extends  BaseMapper<SettleCheckAccount> {
    /**
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 统计结算记录
     * @return
     **/
    List<SettleCheckAccount> statistical(Date time);


    /**
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 根据机构id与币种查询记录
     * @return
     **/
    SettleCheckAccount selectByCurrencyAndInstitutionCode(@Param("currency") String currency, @Param("merchantId") String merchantId);
}
