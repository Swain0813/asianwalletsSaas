package com.asianwallets.institution.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.BankIssueridDTO;
import com.asianwallets.common.dto.BankIssueridExportDTO;
import com.asianwallets.common.entity.BankIssuerid;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankIssueridMapper  extends BaseMapper<BankIssuerid> {

    /**
     * @Author YangXu
     * @Date 2019/6/11
     * @Descripate  查询银行issureid对照信息
     * @return
     **/
    List<BankIssuerid> pageFindBankIssuerid(BankIssueridDTO bankIssueridDTO);

    /**
     * 根据银行名称查询银行关联表的信息
     *
     * @param bankName
     * @return
     */
    List<BankIssuerid> selectByBankName(@Param("bankName") String bankName,@Param("currency") String currency);


    /**
     * @Author YangXu
     * @Date 2019/7/3
     * @Descripate 根据通道银行币种查询
     * @return
     **/
    @Select("select count(1) from bank_issuerid where channel_code = #{channelCode} and bank_name = #{bankName} and currency = #{currency}")
    int getBankIssuerIdByCI(@Param("channelCode") String channelCode, @Param("bankName") String bankName, @Param("currency") String currency);

    /**
     * @param bankIssueridDTO
     * @return
     */
    List<BankIssuerid> exportBankIssuerid(BankIssueridExportDTO bankIssueridDTO);
}