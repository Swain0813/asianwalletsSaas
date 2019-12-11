package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.BankDTO;
import com.asianwallets.common.entity.Bank;
import com.asianwallets.common.entity.BankIssuerId;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.feign.base.BankFeign;
import com.asianwallets.permissions.feign.base.BankIssuerIdFeign;
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
        //指定临时文件路径，这个路径可以随便写
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
        List<Bank> bankList = new ArrayList<>();
        //解析Excel内容
        for (int i = 1; i < read.size(); i++) {
            List<Object> objects = read.get(i);
            //判断传入的excel的格式是否符合约定
            if (StringUtils.isEmpty(objects.get(0)) || StringUtils.isEmpty(objects.get(1)) || StringUtils.isEmpty(objects.get(2))
                    || objects.size() != 4 || StringUtils.isEmpty(objects.get(3))) {
                log.info("==========【导入银行信息】==========【Excel文件内格式不正确】");
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
            try {
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
        //指定临时文件路径，这个路径可以随便写
        factory.setLocation(tmpFile);
        factory.createMultipartConfig();
//        List<BankIssuerId> h = new ArrayList<>();
//        String fileName = file.getOriginalFilename();
//        // 判断格式0
//        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
//            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
//        }
//        ExcelReader reader;
//        try {
//            reader = ExcelUtil.getReader(file.getInputStream());
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 当excel内的格式不正确时
//            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
//        }
//
//        List<List<Object>> read = reader.read();
//        //判断是否超过上传限制
//        if (read.size() - 1 > AsianWalletConstant.UPLOAD_LIMIT) {
//            throw new BusinessException(EResultEnum.EXCEEDING_UPLOAD_LIMIT.getCode());
//        }
//        if (read.size() <= 0) {
//            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
//        }
//        List<String> channelCode = channelMapper.selectAllChannelCode();
//        List<String> bankName = bankMapper.selectAllBankName();
//        for (int i = 1; i < read.size(); i++) {
//            List<Object> objects = read.get(i);
//            BankIssuerId ol = new BankIssuerId();
//            //判断传入的excel的格式是否符合约定
//            if (StringUtils.isEmpty(objects.get(0))
//                    || StringUtils.isEmpty(objects.get(1))
//                    || StringUtils.isEmpty(objects.get(2))
//                    || objects.size() != 4
//                    || StringUtils.isEmpty(objects.get(3))) {
//                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
//            }
//            String code = objects.get(2).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
//            String bName = objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", "");
//            if (!channelCode.contains(code) || !bankName.contains(bName)) {
//                log.info("-------导入映射表信息错误--------通道CODE:{},银行名:{}", JSON.toJSONString(code), JSON.toJSONString(bName));
//                throw new BusinessException(EResultEnum.CHANNEL_OR_BANK_DOES_NOT_EXIST.getCode());
//            }
//            try {
//                ol.setBankName(bName);
//                ol.setCurrency(objects.get(1).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
//                ol.setChannelCode(code);
//                ol.setIssuerId(objects.get(3).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
//            } catch (Exception e) {
//                // 当excel内的格式不正确时
//                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
//            }
//            ol.setId(IDS.uuid2());
//            ol.setCreator(name);
//            ol.setCreateTime(new Date());
//            ol.setEnabled(true);
//            if (bankIssueridMapper.findDuplicatesCount(ol) > 0) {
//                continue;
//            }
//            h.add(ol);
//        }
//        if (h.size() == 0) {
//            throw new BusinessException(EResultEnum.IMPORT_REPEAT_ERROR.getCode());
//        }
//        return h;
        return null;
    }
}
