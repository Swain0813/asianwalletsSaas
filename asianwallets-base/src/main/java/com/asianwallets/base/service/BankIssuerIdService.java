package com.asianwallets.base.service;

import com.asianwallets.common.dto.BankIssuerIdDTO;
import com.asianwallets.common.vo.BankIssuerIdVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BankIssuerIdService {

    /**
     * 添加银行机构代码映射信息
     *
     * @param username            用户名
     * @param bankIssuerIdDTOList 银行机构代码映射输入实体集合
     * @return 修改条数
     */
    int addBankIssuerId(String username, List<BankIssuerIdDTO> bankIssuerIdDTOList);

    /**
     * 修改银行机构代码映射信息
     *
     * @param username        用户名
     * @param bankIssuerIdDTO 银行机构代码映射输入实体
     * @return 修改条数
     */
    int updateBankIssuerId(String username, BankIssuerIdDTO bankIssuerIdDTO);

    /**
     * 分页查询银行机构代码映射信息
     *
     * @param bankIssuerIdDTO 银行机构代码映射输入实体
     * @return PageInfo<BankIssuerIdVO>
     */
    PageInfo<BankIssuerIdVO> pageFindBankIssuerId(BankIssuerIdDTO bankIssuerIdDTO);

    /**
     * 导出银行机构代码映射信息
     *
     * @param bankIssuerIdDTO 银行机构代码映射输入实体
     * @return List<BankIssuerIdVO>
     */
    List<BankIssuerIdVO> exportBankIssuerId(BankIssuerIdDTO bankIssuerIdDTO);
}
