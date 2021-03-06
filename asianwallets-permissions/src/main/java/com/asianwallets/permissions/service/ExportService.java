package com.asianwallets.permissions.service;
import cn.hutool.poi.excel.ExcelWriter;
import com.asianwallets.common.entity.SettleCheckAccount;
import com.asianwallets.common.vo.*;

import java.util.ArrayList;
import java.util.List;

public interface ExportService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 导出商户
     **/
    ExcelWriter getMerchantExcel(ArrayList<MerchantExportVO> merchantExportVOS, Class clazz);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 导出商户产品
     **/
    ExcelWriter getMerchantProductExcel(ArrayList<MerchantProductExportVO> merchantProExportVOS, Class clazz);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/10
     * @Descripate 导出商户通道
     **/
    ExcelWriter getMerchantChannelExcel(ArrayList<MerChannelExportVO> merchantChannelExportVOS, Class clazz);

    /**
     * 导出订单信息
     *
     * @param exportOrdersVOList  订单集合
     * @param exportOrdersVOClass 订单导出class
     * @param writer
     * @return
     */
    ExcelWriter exportOrders(List<ExportOrdersVO> exportOrdersVOList, Class<ExportOrdersVO> exportOrdersVOClass, ExcelWriter writer);

    /**
     * 导出结算户余额流水详情
     * @param institutions
     * @param clazz
     * @return
     */
    ExcelWriter getTmMerChTvAcctBalanceWriter(List<TmMerChTvAcctBalanceVO> institutions, Class clazz);

    /**
     * 机构后台分润导出
     * @param queryAgencyShareBenefitVOS
     * @param clazz
     * @return
     */
    ExcelWriter exportAgencyShareBenefit(List<QueryAgencyShareBenefitVO> queryAgencyShareBenefitVOS, Class clazz);


    /**
     * 导出账户信息
     * @param accountListVOS
     * @param clazz
     * @return
     */
    ExcelWriter exportAccount(List<AccountListVO> accountListVOS, Class clazz);

    /**
     * Excel 导出结算单1
     *
     * @param insPros 对象集合
     * @param clazz   类名Class对象
     * @return ExcelWriter writer
     */
    ExcelWriter getSettleCheckAccountsWriter(ExcelWriter write, String language, List<SettleCheckAccount> insPros, Class clazz);


    /**
     * Excel 导出结算单2
     *
     * @param write
     * @param insPros
     * @param clazz
     * @return
     */
    ExcelWriter getSettleCheckAccountDetailWriter(ExcelWriter write, List<ExportSettleCheckAccountDetailVO> insPros, Class clazz);


    /**
     * 导出商户交易对账单
     */
    ExcelWriter exportTradeCheckAccount(ExportTradeAccountVO exportTradeAccountVO, String language, Class clazz1, Class clazz2);

    /**
     * Excel 导出通道对账详情
     *
     * @param insPros 对象集合
     * @param clazz   类名Class对象
     * @return ExcelWriter writer
     */
    ExcelWriter getCheckAccountWriter(List<CheckAccountVO> insPros, Class clazz);

    /**
     * Excel 导出通道对账复核详情
     *
     * @param insPros 对象集合
     * @param clazz   类名Class对象
     * @return ExcelWriter writer
     */
    ExcelWriter getCheckAccountAuditWriter(List<CheckAccountAuditVO> insPros, Class clazz);

    /**
     * 预授权订单导出
     * @param exportOrdersVOList
     * @param clazz
     * @return
     */
    ExcelWriter exportPreOrders(List<ExportPreOrdersVO> exportOrdersVOList, Class clazz);


    /**
     * 导出权益发放管理信息
     * @param exportRightsGrantVOs
     * @param clazz
     * @return
     */
    ExcelWriter getRightsGrantExcel(List<ExportRightsGrantVO> exportRightsGrantVOs, Class clazz);

    /**
     * 导出权益票券信息
     * @param exportRightsUserGrantVOList
     * @param clazz
     * @return
     */
    ExcelWriter getRightsUserGrantExcel(List<ExportRightsUserGrantVO> exportRightsUserGrantVOList, Class clazz);

    /**
     * 导出权益核销
     * @param language
     * @param list
     * @return
     */
    ExcelWriter exportRightsOrders(String language, List<RightsOrdersVO> list);
}
