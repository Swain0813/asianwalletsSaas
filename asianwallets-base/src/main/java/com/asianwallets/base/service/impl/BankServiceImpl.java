package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.BankIssuerIdMapper;
import com.asianwallets.base.dao.BankMapper;
import com.asianwallets.base.service.BankService;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.entity.BankIssuerId;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.ExportBankVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BankServiceImpl implements BankService {

    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private BankIssuerIdMapper bankIssuerIdMapper;

    /**
     * 新增银行信息
     *
     * @param username 用户名
     * @param bankDTO  通道输入实体
     * @return 修改条数
     */
    @Override
    public int addBank(String username, BankDTO bankDTO) {
        Bank dbBank = bankMapper.selectByBankNameAndCurrency(bankDTO);
        if (dbBank != null) {
            log.info("==========【新增银行信息】==========【银行信息重复】");
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        Bank bank = new Bank();
        bank.setId(IDS.uuid2());
        bank.setBankCode(IDS.uniqueID().toString());
        bank.setBankName(bankDTO.getBankName());
        bank.setBankCountry(bankDTO.getBankCountry());
        bank.setBankCurrency(bankDTO.getBankCurrency());
        bank.setIssuerId(bankDTO.getIssuerId());
        bank.setBankImg(bankDTO.getBankImg());
        bank.setCreator(username);
        bank.setCreateTime(new Date());
        bank.setEnabled(bankDTO.getEnabled());
        return bankMapper.insert(bank);
    }

    /**
     * 修改银行信息
     *
     * @param username 用户名
     * @param bankDTO  通道输入实体
     * @return 修改条数
     */
    @Override
    public int updateBank(String username, BankDTO bankDTO) {
        Bank bank = bankMapper.selectByPrimaryKey(bankDTO.getBankId());
        if (bank == null) {
            log.info("==========【修改银行信息】==========【银行信息不存在】");
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        bank.setBankName(bankDTO.getBankName());
        bank.setBankCountry(bankDTO.getBankCountry());
        bank.setBankCurrency(bankDTO.getBankCurrency());
        bank.setIssuerId(bankDTO.getIssuerId());
        bank.setBankImg(bankDTO.getBankImg());
        bank.setModifier(username);
        bank.setUpdateTime(new Date());
        bank.setEnabled(bankDTO.getEnabled());
        //修改银行机构代码映射表信息
        List<BankIssuerId> bankIssuerIdList = bankIssuerIdMapper.selectByBankName(bank.getBankName(), bankDTO.getBankCurrency());
        for (BankIssuerId bankIssuerid : bankIssuerIdList) {
            bankIssuerid.setBankName(bank.getBankName());
            bankIssuerid.setModifier(username);
            bankIssuerid.setUpdateTime(new Date());
            bankIssuerIdMapper.updateByPrimaryKeySelective(bankIssuerid);
        }
        return bankMapper.updateByPrimaryKeySelective(bank);
    }

    /**
     * 分页查询银行信息
     *
     * @param bankDTO 通道输入实体
     * @return 修改条数
     */
    @Override
    public PageInfo<Bank> pageFindBank(BankDTO bankDTO) {
        return new PageInfo<>(bankMapper.pageFindBank(bankDTO));
    }

    /**
     * 导出银行信息
     *
     * @param bankDTO 通道输入实体
     * @return 修改条数
     */
    @Override
    public List<ExportBankVO> exportBank(BankDTO bankDTO) {
        return bankMapper.exportBank(bankDTO);
    }
}
