package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.dto.BankIssuerIdDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.entity.BankIssuerId;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.feign.base.BankFeign;
import com.asianwallets.permissions.feign.base.BankIssuerIdFeign;
import com.asianwallets.permissions.feign.base.ChannelFeign;
import com.asianwallets.permissions.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImportServiceImpl implements ImportService {

    @Autowired
    private BankFeign bankFeign;

    @Autowired
    private ChannelFeign channelFeign;

    @Autowired
    private BankIssuerIdFeign bankIssuerIdFeign;

    @Value("${file.tmpfile}")
    private String tmpFile;

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 导入银行信息
     *
     * @param username 用户名
     * @param file     文件
     * @return 银行集合
     */
    @Override
    public List<Bank> importBank(String username, MultipartFile file) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //指定临时文件路径,这个路径可以随便写
        factory.setLocation(tmpFile);
        factory.createMultipartConfig();
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            log.info("==========【导入银行信息】==========【文件名为空】");
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        //判断格式
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            log.info("==========【导入银行信息】==========【文件名不正确】");
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        ExcelReader reader;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (Exception e) {
            log.info("==========【导入银行信息】==========【Excel读取异常】", e);
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        List<List<Object>> read = reader.read();
        if (read.size() == 0) {
            log.info("==========【导入银行信息】==========【Excel文件内容为空】");
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        if (read.size() - 1 > AsianWalletConstant.UPLOAD_LIMIT) {
            log.info("==========【导入银行信息】==========【超过最大导入条数300】");
            throw new BusinessException(EResultEnum.EXCEEDING_UPLOAD_LIMIT.getCode());
        }
        //判断传入的excel的格式是否符合约定
        for (int i = 1; i < read.size(); i++) {
            if (StringUtils.isEmpty(read.get(i).get(0)) || StringUtils.isEmpty(read.get(i).get(1)) || StringUtils.isEmpty(read.get(i).get(2))
                    || read.get(i).size() != 4 || StringUtils.isEmpty(read.get(i).get(3))) {
                log.info("==========【导入银行信息】==========【Excel文件内格式不正确】");
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
        }
        List<Bank> bankList = new ArrayList<>();
        //解析Excel内容
        for (int i = 1; i < read.size(); i++) {
            try {
                List<Object> objects = read.get(i);
                String bankName = objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
                String bankCurrency = objects.get(1).toString().replaceAll("\\s*", "");
                BankDTO bankDTO = new BankDTO();
                bankDTO.setBankName(bankName);
                bankDTO.setBankCurrency(bankCurrency);
                //校验是否有重复记录
                if (bankFeign.getByBankNameAndCurrency(bankDTO) != null) {
                    continue;
                }
                String bankCountry = objects.get(2).toString().replaceAll("\\s*", "");
                String issuerId = objects.get(3).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
                Bank bank = new Bank();
                bank.setId(IDS.uuid2());
                bank.setBankCode(IDS.uniqueID().toString());
                bank.setBankName(bankName);
                bank.setBankCurrency(bankCurrency);
                bank.setIssuerId(issuerId);
                bank.setBankCountry(bankCountry);
                bank.setCreator(username);
                bank.setCreateTime(new Date());
                bank.setEnabled(true);
                bankList.add(bank);
            } catch (Exception e) {
                log.info("==========【导入银行信息】==========【解析异常】", e);
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
        }
        //去除银行名与币种相同的数据
        List<Bank> banks = bankList.stream().filter(distinctByKey(b -> b.getBankName() + b.getBankCurrency())).collect(Collectors.toList());
        if (banks.size() == 0) {
            log.info("==========【导入银行信息】==========【导入信息重复】");
            throw new BusinessException(EResultEnum.IMPORT_REPEAT_ERROR.getCode());
        }
        return banks;
    }


    /**
     * 导入银行机构号映射信息
     *
     * @param username 用户名
     * @param file     文件
     * @return 银行机构号映射信息集合
     */
    @Override
    public List<BankIssuerId> importBankIssuerId(String username, MultipartFile file) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //指定临时文件路径,这个路径可以随便写
        factory.setLocation(tmpFile);
        factory.createMultipartConfig();
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            log.info("==========【导入银行机构映射信息】==========【文件名为空】");
            throw new BusinessException(EResultEnum.NAME_ERROR.getCode());
        }
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            log.info("==========【导入银行机构映射信息】==========【文件名不正确】");
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        ExcelReader reader;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (Exception e) {
            log.info("==========【导入银行机构映射信息】==========【Excel读取异常】", e);
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }

        List<List<Object>> read = reader.read();
        if (read.size() == 0) {
            log.info("==========【导入银行机构映射信息】==========【Excel文件内容为空】");
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        if (read.size() - 1 > AsianWalletConstant.UPLOAD_LIMIT) {
            log.info("==========【导入银行机构映射信息】==========【超过最大导入条数300】");
            throw new BusinessException(EResultEnum.EXCEEDING_UPLOAD_LIMIT.getCode());
        }
        List<String> channelCodeList = channelFeign.getAllChannelCode();
        List<String> bankNameList = bankFeign.getAllBankName();
        //判断传入的Excel的格式是否符合约定
        for (int i = 1; i < read.size(); i++) {
            if (StringUtils.isEmpty(read.get(i).get(0)) || StringUtils.isEmpty(read.get(i).get(1)) || StringUtils.isEmpty(read.get(i).get(2))
                    || read.get(i).size() != 4 || StringUtils.isEmpty(read.get(i).get(3))) {
                log.info("==========【导入银行机构映射信息】==========【Excel文件内格式不正确】");
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
            String bankName = read.get(i).get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
            String channelCode = read.get(i).get(2).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
            if (!channelCodeList.contains(channelCode) || !bankNameList.contains(bankName)) {
                log.info("==========【导入银行机构映射信息】==========【导入数据不存在与通道表或银行表】");
                throw new BusinessException(EResultEnum.CHANNEL_OR_BANK_DOES_NOT_EXIST.getCode());
            }
        }
        List<BankIssuerId> bankIssuerIdList = new ArrayList<>();
        for (int i = 1; i < read.size(); i++) {
            try {
                List<Object> objects = read.get(i);
                String bankName = objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
                String currency = objects.get(1).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
                String channelCode = objects.get(2).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
                String issuerId = objects.get(3).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
                BankIssuerIdDTO bankIssuerIdDTO = new BankIssuerIdDTO(bankName, currency, channelCode, issuerId);
                if (bankIssuerIdFeign.getByTerm(bankIssuerIdDTO) != null) {
                    continue;
                }
                BankIssuerId bankIssuerId = new BankIssuerId();
                bankIssuerId.setId(IDS.uuid2());
                bankIssuerId.setBankName(bankName);
                bankIssuerId.setChannelCode(channelCode);
                bankIssuerId.setCurrency(currency);
                bankIssuerId.setIssuerId(issuerId);
                bankIssuerId.setCreator(username);
                bankIssuerId.setCreateTime(new Date());
                bankIssuerId.setEnabled(true);
                bankIssuerIdList.add(bankIssuerId);
            } catch (Exception e) {
                log.info("==========【导入银行机构映射信息】==========【解析异常】", e);
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
        }
        //去除相同信息的数据
        List<BankIssuerId> bankIssuerIds = bankIssuerIdList.stream().filter(distinctByKey(b -> b.getBankName() + b.getChannelCode() + b.getCurrency() + b.getIssuerId())).collect(Collectors.toList());
        if (bankIssuerIds.size() == 0) {
            log.info("==========【导入银行机构映射信息】==========【导入信息重复】");
            throw new BusinessException(EResultEnum.IMPORT_REPEAT_ERROR.getCode());
        }
        return bankIssuerIds;
    }
}
