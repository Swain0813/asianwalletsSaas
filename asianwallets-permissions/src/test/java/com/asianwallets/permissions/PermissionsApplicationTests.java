package com.asianwallets.permissions;

import com.asianwallets.permissions.dao.SysMenuMapper;
import com.asianwallets.permissions.demo.AbstractLogger;
import com.asianwallets.permissions.demo.ResultVO;
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

            //String[] process = new String[]{"SecondProcess"};
            //String[] process = new String[]{"ThreeProcess", "FirstProcess"};
            //String[] process = new String[]{"ThreeProcess", "FirstProcess"};
            //String[] process = new String[]{"ThreeProcess", "FirstProcess" ,"SecondProcess"};
            //String[] process = new String[]{"FirstProcess", "ThreeProcess" ,"SecondProcess"};
            String[] process = new String[]{"FirstProcess", "SecondProcess" ,"SecondProcess"};
            System.out.println("-----------------------------------------");
            try {
                AbstractLogger ab = getAbstractLogger(process);
                if(ab == null) System.out.println("========== AbstractLogger is null =========");
                System.out.println("-----------------------------------------");
                ResultVO resultVO = new ResultVO();
                resultVO.setObject("===== 进来了 ====");
                ab.logMessage(resultVO);
                System.out.println("-----------------------------------------");
            } catch (Exception e) {

            }
    }

    public AbstractLogger getAbstractLogger( String[] process1){
        try {
            //String[] process1 = new String[]{"ThreeProcess", "FirstProcess", "SecondProcess"};
            int num = process1.length - 1;
            AbstractLogger a = (AbstractLogger) Class.forName("com.asianwallets.permissions.demo." + process1[num]).newInstance();
            System.out.println("---------------- return a");
            if(num==0) return a;
            num--;
            AbstractLogger b = (AbstractLogger) Class.forName("com.asianwallets.permissions.demo." + process1[num]).newInstance();
            b.setNextLogger(a);
            System.out.println("---------------- return b");
            if(num==0) return b;
            num--;
            AbstractLogger c = (AbstractLogger) Class.forName("com.asianwallets.permissions.demo." + process1[num]).newInstance();
            c.setNextLogger(b);
            System.out.println("---------------- return c");
            if(num==0) return c;
            num--;
            AbstractLogger d = (AbstractLogger) Class.forName("com.asianwallets.permissions.demo." + process1[num]).newInstance();
            d.setNextLogger(c);
            System.out.println("---------------- return d");
            if(num==0) return d;
            num--;

        }catch (Exception e){

        }
        return null;
    }


}
