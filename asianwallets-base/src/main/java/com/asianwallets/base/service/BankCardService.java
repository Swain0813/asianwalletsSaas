package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.BankCardDTO;
import com.asianwallets.common.dto.BankCardSearchDTO;
import com.asianwallets.common.entity.BankCard;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * <p>
 * 银行卡表 服务类
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
public interface BankCardService extends BaseService<BankCard> {


    /**
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 添加银行卡信息
     * @return
     **/
    int addBankCard(String username, List<BankCardDTO> bankCardDTO);

    /**
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 修改银行卡信息
     * @return
     **/
    int updateBankCard(String name, BankCardDTO bankCardDTO);

    /**
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 根据商户id查询银行卡
     * @return
     **/
    List<BankCard> selectBankCardByMerId(String institutionId);

    /**
     * @Author YangXu
     * @Date 2019/2/27
     * @Descripate 分页查询银行卡
     * @return
     **/
    PageInfo<BankCard> pageBankCard(BankCardSearchDTO bankCardSearchDTO);

}
