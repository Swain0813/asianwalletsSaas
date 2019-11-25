package com.asianwallets.base.dao;

import com.asianwallets.common.base. BaseMapper;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.entity.BankCard;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
  * 银行卡表 Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Repository
public interface BankCardMapper extends  BaseMapper<BankCard> {


    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 分页查询银行卡
     **/
    List<BankCard> pageBankCard(BankCardSearchDTO bankCardSearchDTO);


    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户id查询银行卡
     * @return
     **/
    List<BankCard> selectBankCardByMerId(@Param("merchantId") String merchantId);
}
