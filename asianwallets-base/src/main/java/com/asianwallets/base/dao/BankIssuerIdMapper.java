package com.asianwallets.base.dao;

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
     * 根据银行名称与银行币种查询银行机构代码映射关系
     *
     * @param bankName 银行名称
     * @param currency 银行币种
     * @return 银行机构代码映射集合
     */
    List<BankIssuerId> selectByBankName(@Param("bankName") String bankName, @Param("currency") String currency);

    /**
     * 根据通道编号,银行名称,币种查询银行机构代码映射信息
     *
     * @param channelCode 银行名称
     * @param bankName    银行币种
     * @param currency    币种
     * @return 银行机构代码映射信息
     */
    BankIssuerId selectByChannelCodeAndBankNameAndCurrency(@Param("channelCode") String channelCode, @Param("bankName") String bankName, @Param("currency") String currency);

    /**
     * 分页查询银行机构代码映射信息
     *
     * @param bankIssuerIdDTO 银行机构映射输入实体
     * @return 银行机构代码映射信息集合
     */
    List<BankIssuerIdVO> pageFindBankIssuerId(BankIssuerIdDTO bankIssuerIdDTO);

    /**
     * 导出查询银行机构代码映射信息
     *
     * @param bankIssuerIdDTO 银行机构映射输入实体
     * @return 银行机构代码映射信息集合
     */
    List<BankIssuerIdVO> exportBankIssuerId(BankIssuerIdDTO bankIssuerIdDTO);

    /**
     * 根据条件查询银行机构映射信息
     *
     * @param bankIssuerIdDTO 银行机构代码映射输入实体
     * @return BankIssuerId
     */
    BankIssuerId selectByTerm(BankIssuerIdDTO bankIssuerIdDTO);
}