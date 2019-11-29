package com.asianwallets.permissions.demo;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:03
 **/
@Component
public class SecondProcess extends AbstractLogger {
    public SecondProcess() {

    }

    public SecondProcess(AbstractLogger abstractLogger) {
        this.nextLogger = abstractLogger;
    }

    @Override
    protected ResultVO write(ResultVO resultVO) {
        System.out.println("-------------------------- SecondProcess --------------------------------");
        String message = resultVO.getObject().toString() + " > b";
        System.out.println(" ================= SecondProcess : " + message);
        resultVO.setObject(message);
        resultVO.setStatus(true);
        //resultVO.setStatus(false);
        //if (true){
        //    System.out.println("----------------------------false");
        //    throw new BusinessException(EResultEnum.ROLE_EXIST.getCode());
        //}
        return resultVO;

    }
}
