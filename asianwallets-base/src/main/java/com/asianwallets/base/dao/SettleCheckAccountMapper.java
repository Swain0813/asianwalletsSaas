package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
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
     * @Descripate 分页查询机构结算对账
     * @return
     **/
    List<SettleCheckAccount> pageSettleAccountCheck(TradeCheckAccountDTO tradeCheckAccountDTO);


    /**
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 导出机构结算对账
     * @return
     **/
    List<SettleCheckAccount> exportSettleAccountCheck(TradeCheckAccountSettleExportDTO tradeCheckAccountDTO);
    /**
     * @Author YangXu
     * @Date 2019/4/16
     * @Descripate 根据机构id与币种查询记录
     * @return
     **/
    SettleCheckAccount selectByCurrencyAndInstitutionCode(@Param("currency") String currency, @Param("merchantId") String merchantId);

    /**
     * @Author YangXu
     * @Date 2019/8/5
     * @Descripate 根据时间和币种查询期末余额
     * @return
     **/
    @Select("select final_amount from settle_check_account where merchant_id = #{merchantId} and currency =#{currency} and  DATE_FORMAT(check_time,'%Y-%m-%d') <  DATE_FORMAT(#{date},'%Y-%m-%d') order by check_time DESC limit 1")
    BigDecimal getBalanceByTimeAndCurrencyAndInstitutionCode(@Param("date") Date date, @Param("currency")String currency, @Param("merchantId")String merchantId);
}
