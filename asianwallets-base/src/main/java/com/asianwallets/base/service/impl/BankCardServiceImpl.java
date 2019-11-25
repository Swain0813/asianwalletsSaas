package com.asianwallets.base.service.impl;

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

            BankCard bankCard = new BankCard();
            BeanUtils.copyProperties(list.get(i), bankCard);
            bankCard.setId(IDS.uuid2());//id
            bankCard.setCreateTime(new Date());
            bankCard.setCreator(name);
            bankCard.setEnabled(true);
            bankCard.setDefaultFlag(true);//设为默认银行卡
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
