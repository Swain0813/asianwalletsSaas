package com.asianwallets.permissions.service;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.entity.BankIssuerId;
import com.asianwallets.common.entity.MerchantReport;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImportService {


    /**
     * 导入银行信息
     *
     * @param username 用户名
     * @param file     文件
     * @return 银行集合
     */
    List<Bank> importBank(String username, MultipartFile file);

    /**
     * 导入银行机构号映射信息
     *
     * @param username 用户名
     * @param file     文件
     * @return 银行机构号映射信息集合
     */
    List<BankIssuerId> importBankIssuerId(String username, MultipartFile file);

    /**
     * 导入商户报备信息
     * @param username
     * @param file
     * @return
     */
    List<MerchantReport> importMerchantReport(String username, MultipartFile file);
}
