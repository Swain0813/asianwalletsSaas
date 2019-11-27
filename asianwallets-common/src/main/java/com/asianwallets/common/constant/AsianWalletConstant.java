package com.asianwallets.common.constant;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: 常量类
 * @createTime 2018年7月3日 上午9:33:11
 * @copyright: 上海众哈网络技术有限公司
 */
public class AsianWalletConstant {
    //http 200 状态码
    public static final int HTTP_SUCCESS_STATUS = 200;

    // 商户返回的回调状态
    public static final String CALLBACK_SUCCESS = "success";

    // ID 参数名
    public static final String FIELD_ID_PARAM = "id";
    // isAvailable 参数名
    public static final String FIELD_IS_AVAILABLE_PARAM = "isAvailable";

    public static String server_port;// 项目端口号
    public static String project_name;// 项目名称

    public static String tokenHeader = "x-access-token";
    public static String languageHeader = "Content-Language";

    public static final int THREE = 3;
    public static final int TWO = 2;
    public static final int FOUR = 4;
    public static final int ZERO = 0;

    //操作日志表中的操作类型
    public static final Byte ADD = 1; //增
    public static final Byte DELETE = 2; //删
    public static final Byte UPDATE = 3; //改
    public static final Byte SELECT = 4; //查

    //数据字典中类型code的前半部分拼接用
    public static final String DIC = "dic_";//类型code拼接用

    //字典类型的初始CODE
    public static final String DICTIONARY_TYPE_CODE = "dic_1";//类型名称

    //币种类型dic_code的默认值
    public static final String CURRENCY_CODE = "dic_2";//币种

    //支付方式dic_code的默认值
    public static final String PAY_METHOD_CODE = "dic_3";//支付方式

    //结算周期dic_code的默认值
    public static final String SETTLEMENT_CODE = "dic_4";//结算周期

    //节假日批量上传数目限制值
    public static final int UPLOAD_LIMIT = 300;

    //币种默认值
    public static final String CURRENCY_DEFAULT = "currencyDefault";

    //导出excel
    //注释信息
    public static final String EXCEL_TITLES = "titles";
    //属性名信息
    public static final String EXCEL_ATTRS = "attrs";

    //亚洲钱包的目前支持的语言
    public static final String ZH_CN = "zh-cn";//中文
    public static final String EN_US = "en-us";//英文

    /************************************** 机构 产品 机构产品 机构通道 通道*******************************************************************************************/

    // institutionCacheKey_886079481626
    public static final String INSTITUTION_CACHE_KEY = "institutionCacheKey";//机构表的缓存key

    // institutionCacheKey_886079481626
    public static final String MERCHANT_CACHE_KEY = "merchantCacheKey";//商户表的缓存key

    // productCacheKey_1      key+产品code
    public static final String PRODUCT_CACHE_CODE_KEY = "productCodeCacheKey";//产品表的缓存key

    // productCacheKey_dic_3_3_USD_1        key+支付方式+币种+交易方向
    public static final String PRODUCT_CACHE_TYPE_KEY = "productTypeCacheKey";//产品表的缓存key

    // institutionProductCacheKey_fce0ebc7afd64224b2357b993be95dbf_ca05e0cb1791433386824cd1c603fe76          key+机构id+产品id
    public static final String INSTITUTIONPRODUCT_CACHE_KEY = "institutionProductCacheKey";//机构产品中间表的缓存key

    // institutionChannelCacheKey_242e7c2f738f4ebd9a6f91399bdeafef          key+机构产品ID
    public static final String INSTITUTIONCHANNEL_CACHE_KEY = "institutionChannelCacheKey";//机构通道中间表的缓存key

    //channelCacheKey_165dd9669f064da98645fecfb70afc3d      key+通道id
    public static final String CHANNEL_CACHE_KEY = "channelCacheKey";//通道表的缓存key

    //channelCacheCodeKey_880239009516445696      key+通道Code
    public static final String CHANNEL_CACHE_CODE_KEY = "channelCacheCodeKey";//通道表的缓存key

    /************************************** 机构 产品 机构产品 机构通道 通道*******************************************************************************************/


    public static final String EXCHANGERATE_CACHE_KEY = "exchangeRateCacheKey";//汇率表的缓存key

    public static final String ATTESTATION_CACHE_KEY = "attestationCacheKey";//秘钥管理表的缓存key

    public static final String ATTESTATION_CACHE_PLATFORM_KEY = "attestationCachePlatformKey";//密钥管理表的平台私钥缓存

    /************************************** 账户 *******************************************************************************************/
    public static final String ACCOUNT_CACHE_KEY = "accountCacheKey";//key + 机构编号 + 币种

    /************************************** 账户 *******************************************************************************************/
    public static final String CURRENCY_CACHE_KEY = "currencyCacheKey";//key + 币种

    public static final String PAYOUT_BALANCE_KEY = "payoutBalanceKey";//付款校检余额缓存key





    //清结算接口url的key值
    public static final String CSAPI_URL_FROZENFUNDSJSON = "CSAPI_URL_FrozenFundsJSON";//冻结资金接口

    public static final String CSAPI_URL_INTOACCOUNTJSON = "CSAPI_URL_IntoAccountJSON";//账户变动接口

    public static final String CSAPI_URL_TRANSFERACCOUNTJSON = "CSAPI_URL_TransferAccountJSON";//转账接口

    public static final String CSAPI_MD5KEY = "CSAPI_MD5key";//MD5 key

    //调账类型
    public static final int RECONCILIATION_IN = 1; //调入
    public static final int RECONCILIATION_OUT = 2;//调出
    public static final int FREEZE = 3;//冻结
    public static final int UNFREEZE = 4;//解冻

    //机构结算表的结算状态
    public static final Byte SETTLING = 1; //结算中

    //结算类型
    public static final Byte SETTLE_AUTO = 1;//自动结算
    public static final Byte SETTLE_ACCORD = 2;//手动结算

    //登录类型
    public static final Integer OPERATION = 1;//运维系统
    public static final Integer INSTITUTION = 2;//机构系统
    public static final Integer MERCHANT = 3;//商户系统
    public static final Integer AGENCY = 4;//代理
    public static final Integer POS = 5;//Pos机

    //账户类型
    public static final Integer OPERATION_USER = 1;//运维
    public static final Integer INSTITUTION_USER = 2;//机构
    public static final Integer MERCHANT_USER = 3;//普通商户
    public static final Integer AGENCY_USER= 4;//代理商户
    public static final Integer GROUP_USER = 5;//集团商户

    //前端发布版本控制字段Key
    public static final String VERSION_CONTROL = "VERSION_CONTROL";

    //限额限次分布式锁前缀
    public static final String QUOTA = "QUOTA";

    //毛利计算的币种
    public static final String SGD = "SGD";
    public static final String HKD = "HKD";

    //国家地区类型
    //国家
    public static final Byte COUNTRY = 0;
    //地区
    public static final Byte AREA = 1;
}
