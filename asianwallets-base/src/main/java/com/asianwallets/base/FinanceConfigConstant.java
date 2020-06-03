package com.asianwallets.base;
import com.asianwallets.base.dao.ChannelMapper;
import com.asianwallets.common.constant.FinanceConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Wu, Hua-Zheng
 * @version v1.0.0
 * @classDesc: 功能描述: 项目配置文件初始化
 * @createTime 2018年8月2日 下午9:21:14
 * @copyright: 上海众哈网络技术有限公司
 */
@Slf4j
@Component
@Order(value = 0)
public class FinanceConfigConstant implements CommandLineRunner {

    @Autowired
    private ChannelMapper channelMapper;

    /**
     * 加载通道编号
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        List<String> list = channelMapper.getChannelCodeByName("AD3");
        List<String> help2PAYlist = channelMapper.getChannelCodeByName("HELP2PAY");
        for (String s : list) {
            log.info(">>>>>>>>>>>>>>>加载AD3通道编号 : 【{}】 <<<<<<<<<<<<<", s);
        }
        for (String s : help2PAYlist) {
            log.info(">>>>>>>>>>>>>>>加载HELP2PAY通道编号 : 【{}】 <<<<<<<<<<<<<", s);
        }
        FinanceConstant.FinanceChannelNameMap.put("AD3", list);
        FinanceConstant.FinanceChannelNameMap.put("HELP2PAY", help2PAYlist);
        log.info(">>>>>>>>>>>>>>>加载通道编号;FinanceChannelNameMap : 【{}】 <<<<<<<<<<<<<", FinanceConstant.FinanceChannelNameMap.size());
    }

}
