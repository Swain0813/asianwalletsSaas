package com.asianwallets.task;

import com.asianwallets.common.entity.Courier;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.vo.ExchangeRateVO;
import com.asianwallets.task.dao.ExchangeRateMapper;
import com.asianwallets.task.scheduled.OpenExchangeRateTask;
import com.asianwallets.task.utils.JsoupUtill;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AsianwalletTaskApplicationTests {

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    @Autowired
    private CourierMapper courierMapper;

    @Autowired
    private FinanceFeign financeFeign;

    @Autowired
    private OpenExchangeRateTask task;

    @Test
    public void settleAccountCheck() {
        financeFeign.selectTcsStFlow(DateToolUtils.getReqDateG(new Date()));
    }


    @Test
    public void contextLoads() {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<ExchangeRateVO> lists = exchangeRateMapper.selectByCreateTimeAndCreator(sdf.format(nowDate), "爬虫");
        System.out.println(lists);
    }

    @Test
    public void TT() {
        List<Element> exRateList = JsoupUtill.getCourierList(JsoupUtill.getPageDocument("https://www.trackingmore.com/help_article-25-31-en.html"));
        List<Courier> list = new ArrayList<>();
        List<String> lll = new ArrayList<>();
        for (Element element : exRateList) {
            lll.add(element.html());
        }
        List<List<String>> lists = groupListByQuantity(lll, 3);
        for (List<String> str : lists) {
            Courier courier = new Courier();
            courier.setCourierCode(str.get(0));
            courier.setCourierEnName(str.get(1));
            courier.setCourierCnName(str.get(2));
            list.add(courier);
        }

        System.out.println(courierMapper.insertList(list));
    }

    /**
     * 将集合按指定数量分组
     *
     * @param list     数据集合
     * @param quantity 分组数量
     * @return 分组结果
     */
    private static List<List<String>> groupListByQuantity(List<String> list, int quantity) {
        List<List<String>> wrapList = new ArrayList<>();
        int count = 0;
        while (count < list.size()) {
            wrapList.add(new ArrayList<>(list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity)));
            count += quantity;
        }
        return wrapList;
    }

    @Test
    public void testwww() throws ParseException {
        task.getOpenRate();
    }
}
