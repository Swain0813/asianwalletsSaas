package com.asianwallets.permissions.demo;

import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:02
 **/
@Component
public class FirstProcess extends AbstractLogger {
    public FirstProcess() {
    }

    public FirstProcess(AbstractLogger abstractLogger) {
        this.nextLogger = abstractLogger;
    }
    @Override
    protected ResultVO write(String message) {
        System.out.println("-------------------------- FirstProcess --------------------------------");
        ResultVO resultVO = new ResultVO();
        message = message +" > a" ;
        System.out.println(" ================= FirstProcess : " + message);
        resultVO.setObject(message);
        //resultVO.setStatus(true);
        resultVO.setStatus(false);
        return resultVO;
    }
}
