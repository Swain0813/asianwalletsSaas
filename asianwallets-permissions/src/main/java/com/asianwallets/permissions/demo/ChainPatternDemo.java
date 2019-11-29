package com.asianwallets.permissions.demo;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-29 14:04
 **/
public class ChainPatternDemo {


    private static AbstractLogger getChainOfLoggers() {

        AbstractLogger a3 = new SecondProcess();
        AbstractLogger a2 = new FirstProcess(a3);
        AbstractLogger a1 = new ThreeProcess(a2);
        return a1;
    }

    public static void main(String[] args) {
        AbstractLogger loggerChain = getChainOfLoggers();

        System.out.println("-----------------------------------------");

        loggerChain.logMessage("This is an 1.");


        System.out.println("-----------------------------------------");
    }


    //public AbstractLogger getAbstractLogger() {
    //    AbstractLogger a = null;
    //    String[] process = new String[]{"ThreeProcess", "FirstProcess", "SecondProcess"};
    //    try {
    //        AbstractLogger a3 = (AbstractLogger) Class.forName(process[2]).newInstance();
    //        AbstractLogger a2 = (AbstractLogger) Class.forName(process[1]).newInstance();
    //        a = (AbstractLogger) Class.forName(process[0]).newInstance();
    //
    //        a.setNextLogger(a2);
    //        a2.setNextLogger(a3);
    //        return a;
    //    } catch (Exception e) {
    //
    //    }
    //    return a;
    //}
}
