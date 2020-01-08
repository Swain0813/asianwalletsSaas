package com.asianwallets.task;
import com.asianwallets.task.scheduled.InsDailyTradeTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@SpringBootTest
@RunWith(SpringRunner.class)
public class AsianwalletTaskApplicationTests {


    @Autowired
    private InsDailyTradeTask insDailyTradeTask;

    @Test
    public void test1() {

        insDailyTradeTask.insDailyTrade();
    }
}
