package com.asianwallets.permissions.demo;

import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:03
 **/
@Component
public class ThreeProcess extends AbstractLogger{
    public ThreeProcess(){

    }

    public ThreeProcess(AbstractLogger abstractLogger){
        this.nextLogger = abstractLogger;
    }

    @Override
    protected ResultVO write(String message) {
        System.out.println("-------------------------- ThreeProcess --------------------------------");
        ResultVO resultVO = new ResultVO();

        message = message +" > c" ;
        System.out.println(" ================= ThreeProcess : " + message);
        resultVO.setObject(message);
        resultVO.setStatus(true);
        return resultVO;
    }
}
