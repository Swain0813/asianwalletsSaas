package com.asianwallets.permissions.demo;

import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:03
 **/
@Component
public class SecondProcess extends AbstractLogger {
    public SecondProcess(){

    }

    public SecondProcess(AbstractLogger abstractLogger) {
        this.nextLogger = abstractLogger;
    }

    @Override
    protected ResultVO write(String message) {
        System.out.println("-------------------------- SecondProcess --------------------------------");
        ResultVO resultVO = new ResultVO();
        message = message +" > b" ;
        System.out.println(" ================= SecondProcess : " + message );
        resultVO.setObject(message);
        resultVO.setStatus(true);
        return resultVO;

    }
}
