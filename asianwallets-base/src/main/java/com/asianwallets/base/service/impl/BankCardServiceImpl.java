package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.AccountMapper;
import com.asianwallets.base.dao.BankCardMapper;
import com.asianwallets.base.service.BankCardService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.BankCardDTO;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.entity.BankCard;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 银行卡表 服务实现类
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Service
public class BankCardServiceImpl extends BaseServiceImpl<BankCard> implements BankCardService {

    @Autowired
    private BankCardMapper bankCardMapper;

    @Autowired
    private AccountMapper accountMapper;
    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 添加银行卡信息
     * @return
     **/
    @Override
    public int addBankCard(String name, List<BankCardDTO> list) {
        List<BankCard> bankCardList = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            //根据机构code和结算币种获取账户信息
            String accountCode = accountMapper.getAccountCode(list.get(i).getMerchantId(), list.get(i).getSettleCurrency());
            if (StringUtils.isEmpty(accountCode)) {//账户信息不存在
                throw new BusinessException(EResultEnum.ACCOUNT_IS_NOT_EXIST.getCode());
            }
            //判断该机构下的银行账户下的该银行卡币种是不是已经存在
            List<BankCard> bankCards = bankCardMapper.getBankCards(list.get(i).getMerchantId(), list.get(i).getBankAccountCode());
            if (bankCards != null && bankCards.size() > 0) {
                for (BankCard bc : bankCards) {
                    //结算币种
                    if (bc.getSettleCurrency().equals(list.get(i).getSettleCurrency())) {
                        //信息已存在
                        throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                    }
                }
            }
            boolean flag = true;
            //不为第一条时
            if (i != 0) {
                for (int j = 0; j < list.size(); j++) {
                    //同一条时
                    if (i == j) {
                        continue;
                    }
                    if (list.get(i).getSettleCurrency().equals(list.get(j).getSettleCurrency())) {
                        //有相同的结算币种就不设置为默认银行卡
                        flag = false;
                        break;
                    }
                }
            }
            //根据机构code，银行卡币种以及结算币种和启用禁用状态和是否设为默认银行卡查询银行卡信息
            List<BankCard> lists = bankCardMapper.selectUpdateBankCard(list.get(i).getMerchantId(), list.get(i).getSettleCurrency());
            if (lists != null && !lists.isEmpty()) {//存在的场合
                for (BankCard bankCard : lists) {
                    bankCard.setDefaultFlag(false);
                    bankCardMapper.updateByPrimaryKeySelective(bankCard);//将存在的更新为不为默认的银行卡
                }
            }
            BankCard bankCard = new BankCard();
            BeanUtils.copyProperties(list.get(i), bankCard);
            bankCard.setAccountCode(accountCode);//账户编号
            bankCard.setId(IDS.uuid2());//id
            bankCard.setCreateTime(new Date());
            bankCard.setCreator(name);
            bankCard.setEnabled(true);
            bankCard.setDefaultFlag(flag);//设为默认银行卡
            bankCardList.add(bankCard);
        }
        return bankCardMapper.insertList(bankCardList);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 修改银行卡信息
     **/
    @Override
    public int updateBankCard(String name, BankCardDTO bankCardDTO) {
        BankCard bankCard = new BankCard();
        BeanUtils.copyProperties(bankCardDTO, bankCard);
        bankCard.setId(bankCardDTO.getBankCardId());
        bankCard.setModifier(name);
        bankCard.setUpdateTime(new Date());
        return bankCardMapper.updateByPrimaryKeySelective(bankCard);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 根据商户id查询银行卡
     **/
    @Override
    public List<BankCard> selectBankCardByMerId(String merchantId) {
        return bankCardMapper.selectBankCardByMerId(merchantId);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 分页查询银行卡
     **/
    @Override
    public PageInfo<BankCard> pageBankCard(BankCardSearchDTO bankCardSearchDTO) {
        return new PageInfo<BankCard>(bankCardMapper.pageBankCard(bankCardSearchDTO));
    }
}
