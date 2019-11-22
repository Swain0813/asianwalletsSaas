package com.asianwallets.task.scheduled;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.ExchangeRate;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.HttpClientUtils;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.task.dao.ExchangeRateMapper;
import com.asianwallets.task.feign.MessageFeign;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author: XuWenQi
 * @create: 2019-08-01 14:20
 **/
@Component
@Slf4j
@Api(value = "OPEN汇率API调用定时任务")
public class OpenExchangeRateTask {

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    @Autowired
    private MessageFeign messageFeign;

    @Autowired
    private RedisService redisService;

    @Value("${custom.developer.mobile}")
    private String developerMobile;

    @Value("${custom.developer.email}")
    private String developerEmail;

    @Value("${custom.open.appId}")
    private String appId;


    /**
     * Open汇率查询定时任务
     * 每天早上9点开始执行,执行一次
     */
    @Scheduled(cron = "0 0 9 ? * *")
    //@Scheduled(cron = "0/10 * * * * ? ")//每10秒执行一次 测试用
    @Transactional(rollbackFor = Exception.class)
    public void getOpenRate() {
        log.info("=============【OPEN汇率调用定时任务】=============【开始执行】");
        List<String> currencyList = new ArrayList<>(13);
        currencyList.add("USD");
        currencyList.add("SGD");
        currencyList.add("MYR");
        currencyList.add("IDR");
        currencyList.add("VND");
        currencyList.add("AUD");
        currencyList.add("HKD");
        currencyList.add("THB");
        currencyList.add("JPY");
        currencyList.add("KHR");
        currencyList.add("PHP");
        currencyList.add("CNY");
        currencyList.add("KRW");
        Map<String, List<String>> currencyMap = getCurrencyMap(currencyList);
        //Open汇率查询接口URL
        String openBaseQueryUrl = "https://openexchangerates.org/api/latest.json";
        //请求头Map
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("Authorization", " Token " + appId);
        try {
            for (String base : currencyMap.keySet()) {
                StringBuilder sb = new StringBuilder();
                //拼接XE汇率查询接口的URL
                sb.append(openBaseQueryUrl).append("?base=").append(base).append("&symbols=");
                List<String> toList = currencyMap.get(base);
                for (int i = 0; i < toList.size(); i++) {
                    if (i == toList.size() - 1) {
                        sb.append(toList.get(i));
                    } else {
                        sb.append(toList.get(i)).append(",");
                    }
                }
                //查询接口URL
                String queryUrl = sb.toString();
                //调用OPEN汇率查询接口
                String openResponse = HttpClientUtils.reqGetString(queryUrl, null, headerMap);
                JSONObject jsonResult = JSONObject.fromObject(openResponse);
                if (jsonResult == null) {
                    log.info("=============【OPEN汇率调用定时任务】=============【响应结果为空】");
                    messageFeign.sendSimple(developerMobile, "OPEN汇率定时任务获取数据异常!【本位币种】: " + base);
                    messageFeign.sendSimpleMail(developerEmail, "OPEN汇率定时任务获取数据异常! ", "OPEN汇率定时任务获取数据异常!【本位币种】: " + base);
                    break;
                }
                log.info("=============【OPEN汇率调用定时任务】=============JSON解析后的【响应参数记录】 jsonResult: {}", JSON.toJSONString(jsonResult));
                //汇率集合
                List<ExchangeRate> exchangeRates = new ArrayList<>();
                //时间戳
                int timestamp = jsonResult.getInt("timestamp");
                //本位币种
                String resultBaseCurrency = jsonResult.getString("base");
                //目标币种:汇率
                JSONObject rates = jsonResult.getJSONObject("rates");
                Set set = rates.keySet();
                for (Object targetCurrency : set) {
                    if (!StringUtils.isEmpty(rates.get(targetCurrency))) {
                        //汇率不为空才添加
                        ExchangeRate exchangeRate = new ExchangeRate();
                        exchangeRate.setId(IDS.uuid2());
                        exchangeRate.setLocalCurrency(resultBaseCurrency);
                        exchangeRate.setForeignCurrency(String.valueOf(targetCurrency));
                        exchangeRate.setBuyRate(new BigDecimal(String.valueOf(rates.get(targetCurrency))));
                        exchangeRate.setUsingTime(new Date(timestamp * 1000L));
                        exchangeRate.setCreateTime(new Date());
                        exchangeRate.setCreator("OPEN定时任务");
                        exchangeRate.setEnabled(true);
                        exchangeRates.add(exchangeRate);
                        //禁用已启用的对应币种的汇率信息
                        exchangeRateMapper.updateStatusByLocalCurrencyAndForeignCurrency(resultBaseCurrency, String.valueOf(targetCurrency), "OPEN定时任务");
                    }
                }
                //批量插入
                exchangeRateMapper.insertList(exchangeRates);
                //将汇率信息同步到redis
                try {
                    for (ExchangeRate exchangeRate : exchangeRates) {
                        redisService.set(AsianWalletConstant.EXCHANGERATE_CACHE_KEY.concat("_").concat(exchangeRate.getLocalCurrency()).concat("_")
                                .concat(exchangeRate.getForeignCurrency()), JSON.toJSONString(exchangeRate));
                    }
                } catch (Exception e) {
                    log.error("=============【OPEN汇率调用定时任务】=============【同步Redis发生错误】", e);
                }
            }
        } catch (Exception e) {
            log.error("=============【OPEN汇率调用定时任务】=============【定时任务发生异常】", e);
            messageFeign.sendSimple(developerMobile, "OPEN汇率定时任务获取数据异常!");
            messageFeign.sendSimpleMail(developerEmail, "OPEN汇率定时任务获取数据异常!", "OPEN汇率定时任务获取数据异常!");
            throw new BusinessException(EResultEnum.ERROR.getCode());
        }
        log.info("=============【OPEN汇率调用定时任务】=============【结束执行】");
    }


    /**
     * 获取币种对应关系
     *
     * @return 币种对应关系Map
     */
    private static Map<String, List<String>> getCurrencyMap(List<String> currencyList) {
        Map<String, List<String>> currencyMap = new HashMap<>();
        for (String code : currencyList) {
            List<String> tempList = new ArrayList<>();
            for (String currency : currencyList) {
                if (!code.equals(currency)) {
                    tempList.add(currency);
                }
            }
            currencyMap.put(code, tempList);
        }
        return currencyMap;
    }

}