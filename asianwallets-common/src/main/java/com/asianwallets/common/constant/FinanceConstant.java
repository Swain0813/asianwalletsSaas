package com.asianwallets.common.constant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 账务常量
 */
public class FinanceConstant {

    public static Map<String, List<String>> FinanceChannelNameMap = new ConcurrentHashMap<>();

    /************************************  对账状态 ****************************************/
    public static int FINACE_WAIT =1;//1,待对账；
    public static int FINACE_CACUO =2;//2，差错处理
    public static int FINACE_BUDAN =3;//3，补单
    public static int FINACE_SUCCESS =4;//4，对账成功

    public static int AUDIT_WAIT =1;//1,待复核；
    public static int AUDIT_SUCCESS =2;//2，复核成功
    public static int AUDIT_FAIL =3;//3，复核失败


}
