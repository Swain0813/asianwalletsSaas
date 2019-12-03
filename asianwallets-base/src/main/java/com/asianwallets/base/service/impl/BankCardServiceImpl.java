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
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 添加银行卡信息
     **/
    @Override
    public int addBankCard(String name, List<BankCardDTO> list) {
        List<BankCard> bankCardList = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            //判断该机构下的银行账户下的该银行卡币种是不是已经存在
            List<BankCard> bankCards = bankCardMapper.getBankCards(list.get(i).getMerchantId(), list.get(i).getBankAccountCode());
            if (bankCards != null && bankCards.size() > 0) {
                for (BankCard bc : bankCards) {
                    //结算币种
                    if (bc.getBankCurrency().equals(list.get(i).getBankCurrency())) {
                        //信息已存在
                        throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                    }
                }
            }
            for (int j = 0; j < list.size(); j++) {
                //入参中有两条同币种的银行卡
                if (list.get(j).getBankCurrency().equals(list.get(i).getBankCurrency())
                        && list.get(j).getMerchantId().equals(list.get(i).getMerchantId())
                        && list.get(j).getBankAccountCode().equals(list.get(i).getBankAccountCode())
                        && i != j) {
                    throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
                }
                if (list.get(j).getMerchantId().equals(list.get(i).getMerchantId())
                        && list.get(j).getDefaultFlag() == list.get(i).getDefaultFlag()
                        && list.get(i).getDefaultFlag()
                        && i != j) {
                    throw new BusinessException(EResultEnum.INFORMATION_IS_ILLEGAL.getCode());
                }
            }

            //根据机构code，银行卡币种和启用禁用状态和是否设为默认银行卡查询银行卡信息
            if (list.get(i).getDefaultFlag()) {
                List<BankCard> lists = bankCardMapper.checkDefaultBankCard(list.get(i).getMerchantId());
                if (lists != null && !lists.isEmpty()) {//存在的场合
                    for (BankCard bankCard : lists) {
                        bankCard.setDefaultFlag(false);
                        bankCardMapper.updateByPrimaryKeySelective(bankCard);//将存在的更新为不为默认的银行卡
                    }
                }
            }
            BankCard bankCard = new BankCard();
            BeanUtils.copyProperties(list.get(i), bankCard);
            bankCard.setId(IDS.uuid2());//id
            bankCard.setCreateTime(new Date());
            bankCard.setCreator(name);
            bankCard.setEnabled(true);
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

    /**
     * @return
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 启用禁用银行卡
     **/
    @Override
    public int banBankCard(String name, String bankCardId, Boolean enabled) {
        //查询银行卡信息是不是存在
        BankCard bankCardInfo = bankCardMapper.getBankCard(bankCardId);
        if (bankCardInfo == null) {//银行卡信息不存在
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        BankCard bankCard = new BankCard();
        bankCard.setId(bankCardId);
        bankCard.setEnabled(enabled);
        if (!enabled) {//禁用的场合，取消默认银行卡
            bankCard.setDefaultFlag(false);
        }
        bankCard.setUpdateTime(new Date());
        bankCard.setModifier(name);
        return bankCardMapper.updateByPrimaryKeySelective(bankCard);
    }

    /**
     * 设置默认银行卡
     *
     * @param name
     * @param bankCardId
     * @param defaultFlag
     * @return
     */
    @Override
    public int defaultBankCard(String name, String bankCardId, Boolean defaultFlag) {
        //查询银行卡信息是不是存在
        BankCard bankCardInfo = bankCardMapper.getBankCard(bankCardId);
        if (bankCardInfo == null) {//银行卡信息不存在
            throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
        }
        //已经禁用的银行卡信息不能设置为默认银行卡
        if (!bankCardInfo.getEnabled()) {
            //已经禁用的银行卡信息不能设置成默认银行卡
            throw new BusinessException(EResultEnum.ENABLED_BANK_ACCOUT_CODE_IS_ERROR.getCode());
        }
        //根据是否设为默认银行卡判断
        if (defaultFlag) {//默认银行卡是否已经存在判断是不是已经存在
            List<BankCard> bankCards = bankCardMapper.checkDefaultBankCard(bankCardInfo.getMerchantId());
            //若默认银行卡已存在设为非默认
            for (BankCard b : bankCards) {
                BankCard bankCard = new BankCard();
                bankCard.setId(b.getId());
                bankCard.setDefaultFlag(false);
                bankCard.setUpdateTime(new Date());
                bankCard.setModifier(name);
                bankCardMapper.updateByPrimaryKeySelective(bankCard);
            }
        }
        BankCard bankCard = new BankCard();
        bankCard.setId(bankCardId);
        bankCard.setDefaultFlag(defaultFlag);
        bankCard.setUpdateTime(new Date());
        bankCard.setModifier(name);
        return bankCardMapper.updateByPrimaryKeySelective(bankCard);
    }


}
