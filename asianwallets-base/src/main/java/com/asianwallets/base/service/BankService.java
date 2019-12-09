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
     * @param bankDTO  通道输入实体
     * @return 修改条数
     */
    int addBank(String username, BankDTO bankDTO);

    /**
     * 修改银行信息
     *
     * @param username 用户名
     * @param bankDTO  通道输入实体
     * @return 修改条数
     */
    int updateBank(String username, BankDTO bankDTO);

    /**
     * 分页查询银行信息
     *
     * @param bankDTO  通道输入实体
     * @return 修改条数
     */
    PageInfo<Bank> pageFindBank(BankDTO bankDTO);

    /**
     * 导出银行信息
     *
     * @param bankDTO  通道输入实体
     * @return 修改条数
     */
    List<ExportBankVO> exportBank(BankDTO bankDTO);
}
