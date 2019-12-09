package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.vo.ExportBankVO;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BankMapper extends BaseMapper<Bank> {

    /**
     * 根据银行名称与币种查询启用的银行
     *
     * @param bankDTO 银行输入实体
     * @return 银行信息
     */
    Bank selectByBankNameAndCurrency(BankDTO bankDTO);

    /**
     * 分页查询银行信息
     *
     * @param bankDTO 银行输入实体
     * @return 银行集合
     */
    List<Bank> pageFindBank(BankDTO bankDTO);

    /**
     * 导出银行信息
     *
     * @param bankDTO 银行输入实体
     * @return 银行集合
     */
    List<ExportBankVO> exportBank(BankDTO bankDTO);
}