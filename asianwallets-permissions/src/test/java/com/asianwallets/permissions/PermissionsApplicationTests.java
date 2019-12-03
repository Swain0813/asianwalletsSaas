package com.asianwallets.permissions;

import com.asianwallets.permissions.dao.SysMenuMapper;
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





}
