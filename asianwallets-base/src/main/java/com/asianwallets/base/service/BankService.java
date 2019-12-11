package com.asianwallets.base.service;


import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.vo.ExportBankVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BankService {

    /**
     * 新增银行信息
     *
     * @param username 用户名
     * @param bankDTO  银行输入实体
     * @return 修改条数
     */
    int addBank(String username, BankDTO bankDTO);

    /**
     * 修改银行信息
     *
     * @param username 用户名
     * @param bankDTO  银行输入实体
     * @return 修改条数
     */
    int updateBank(String username, BankDTO bankDTO);

    /**
     * 分页查询银行信息
     *
     * @param bankDTO 银行输入实体
     * @return 修改条数
     */
    PageInfo<Bank> pageFindBank(BankDTO bankDTO);

    /**
     * 导出银行信息
     *
     * @param bankDTO 银行输入实体
     * @return 修改条数
     */
    List<ExportBankVO> exportBank(BankDTO bankDTO);

    /**
     * 根据银行名称与币种查询启用银行
     *
     * @param bankDTO 银行输入实体
     * @return 银行
     */
    Bank getByBankNameAndCurrency(BankDTO bankDTO);

    /**
     * 导入银行
     *
     * @param bankList 银行集合
     * @return 修改条数
     */
    int importBank(List<Bank> bankList);
}
