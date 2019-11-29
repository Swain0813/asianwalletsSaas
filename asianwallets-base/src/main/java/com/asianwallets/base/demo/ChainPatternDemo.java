package com.asianwallets.base.demo;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:04
 **/
public class ChainPatternDemo {


    private static AbstractLogger getChainOfLoggers(){

        String[] process = new String[]{"ThreeProcess","FirstProcess","SecondProcess"};

        AbstractLogger a1 = new ThreeProcess(process[2]);
        AbstractLogger a3 = new SecondProcess(process[1],a1);
        AbstractLogger a2 = new FirstProcess(process[0],a3);

        a1.setNextLogger(a2);
        a2.setNextLogger(a3);


        return a1;
    }

    public static void main(String[] args) {
        AbstractLogger loggerChain = getChainOfLoggers();

        System.out.println("-----------------------------------------");

        //loggerChain.logMessage(AbstractLogger.INFO, "This is an 1.");

        //loggerChain.logMessage(AbstractLogger.DEBUG,
        //        "This is a debug level 2.");

        //loggerChain.logMessage(AbstractLogger.ERROR,
        //        "This is an error 3.");

        System.out.println("-----------------------------------------");
    }
}
