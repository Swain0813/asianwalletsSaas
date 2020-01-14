package com.asianwallets.permissions;

import com.asianwallets.permissions.dao.SysMenuMapper;
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
        list.add("1022e7f14c9a4bb48fcd7771df5485b5");
        list.add("41f4afc97603429687712c89f7b4f772");
        sysMenuMapper.updateEnabledById(list, "admin", false);
    }


}
