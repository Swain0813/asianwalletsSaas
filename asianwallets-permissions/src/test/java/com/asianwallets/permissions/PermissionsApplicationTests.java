package com.asianwallets.permissions;

import com.asianwallets.permissions.dao.SysMenuMapper;
import com.asianwallets.permissions.demo.AbstractLogger;
import com.asianwallets.permissions.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PermissionsApplication.class)
public class PermissionsApplicationTests {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Test
    public void contextLoads() {
        List<String> list = new ArrayList<>();
         FirstMenuVO firstMenuVO = sysMenuMapper.selectAllMenuById("00dcb121a4cc4baf8b62c7bae0663688");
        for (SecondMenuVO secondMenuVO : firstMenuVO.getSecondMenuVOS()) {
            list.add(secondMenuVO.getId());
            for (ThreeMenuVO threeMenuVO : secondMenuVO.getThreeMenuVOS()) {
                list.add(threeMenuVO.getId());
            }
        }
        list.add(firstMenuVO.getId());
        System.out.println(list);
    }

    @Test
    public void contextLoad1s() {

            String[] process = new String[]{"ThreeProcess", "FirstProcess", "SecondProcess"};
            try {

                AbstractLogger a3 = (AbstractLogger) Class.forName("com.asianwallets.permissions.demo."+process[2]).newInstance();
                AbstractLogger a2 = (AbstractLogger) Class.forName("com.asianwallets.permissions.demo."+process[1]).newInstance();
                AbstractLogger a1 = (AbstractLogger) Class.forName("com.asianwallets.permissions.demo."+process[0]).newInstance();

                a1.setNextLogger(a2);
                a2.setNextLogger(a3);

                System.out.println("-----------------------------------------");
                a1.logMessage("This is an 1.");
                System.out.println("-----------------------------------------");
            } catch (Exception e) {

            }


    }

}
