package com.asianwallets.baseinfo;

import com.asianwallets.base.BaseApplication;
import com.asianwallets.base.service.impl.InstitutionServiceImpl;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BaseApplication.class)
public class BaseinfoApplicationTests {

    @Autowired
    private InstitutionServiceImpl institutionServiceimpl;

  /*  @Test
    void contextLoads() {
    }
*/
   /* @Test
    public void Testa() {
        System.out.println(institutionServiceimpl.getMerchantByInId());
    }*/
}
