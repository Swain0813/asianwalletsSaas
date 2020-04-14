package com.asianwallets.trade.channels.help2pay.impl;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.help2pay.Help2PayRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.RabbitMassage;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.Help2PayCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.utils.HandlerType;
import com.asianwallets.trade.vo.FundChangeVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
@HandlerType(TradeConstant.HELP2PAY)
public class Help2PayServiceImpl extends ChannelsAbstractAdapter implements Help2PayService {

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ClearingService clearingService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    /**
     * 线上收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return 通用响应实体
     */
    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        Help2PayRequestDTO help2PayRequestDTO = new Help2PayRequestDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/help2PayBrowserCallback"), ad3ParamsConfig.getChannelCallbackUrl().concat("/onlinecallback/help2PayServerCallback"), channel.getMd5KeyStr());
        //生成签名
        help2PayRequestDTO.setKey(createHelp2PaySign(help2PayRequestDTO, channel.getMd5KeyStr()));
        //生成签名之后的时间格式要换掉
        help2PayRequestDTO.setDatetime(DateToolUtils.getReqDateI(orders.getCreateTime()));
        log.info("==============【Help2pay网银收单方法】==============【调用Channel服务请求参数】 help2PayRequestDTO: {}", JSON.toJSONString(help2PayRequestDTO));
        BaseResponse response = channelsFeign.help2Pay(help2PayRequestDTO);
        log.info("==============【Help2pay网银收单方法】==============【调用Channel服务响应参数】 response: {}", JSON.toJSONString(response));
        //状态码不为200的时候
        if (!TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
            log.info("==============【Help2pay网银收单方法】==============调用Channels服务【Help2Pay接口】-状态码异常 code: {}", response.getCode());
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        String responseData = (String) response.getData();
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        if (!StringUtils.isEmpty(responseData) && responseData.replaceAll("\\s*", "").matches(".*html.*")) {
            //网银
            onlineTradeVO.setRespCode("T000");
            onlineTradeVO.setCode_url(responseData);
            onlineTradeVO.setType(TradeConstant.ONLINE_BANKING);
            response.setData(onlineTradeVO);
            return response;
        }
        return response;
    }


    /**
     * help2Pay浏览器回调方法
     *
     * @param help2PayCallbackDTO help2Pay浏览器回调实体
     * @return
     */
    @Override
    public void help2PayBrowserCallback(Help2PayCallbackDTO help2PayCallbackDTO, HttpServletResponse response) {
        //校验回调参数
        if (!checkCallback(help2PayCallbackDTO)) {
            return;
        }
        //查询通道md5key
        ChannelsOrder channelsOrder = channelsOrderMapper.selectByPrimaryKey(help2PayCallbackDTO.getReference());
        //校验签名
        if (!createCallbackSign(help2PayCallbackDTO, channelsOrder.getMd5KeyStr())) {
            log.info("-------------help2Pay浏览器回调接口信息记录------------签名不匹配");
            return;
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(help2PayCallbackDTO.getReference());
        if (orders == null) {
            log.info("-------------help2Pay浏览器回调接口信息记录------------回调订单信息不存在");
            return;
        }
        if (orders.getTradeStatus().equals(TradeConstant.ORDER_PAY_SUCCESS)) {
            //支付成功
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("-------------help2Pay浏览器回调接口信息记录------------开始回调商户浏览器");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付成功页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_SUCCESS);
                } catch (IOException e) {
                    log.error("--------------help2Pay浏览器回调接口信息记录--------------调用AW支付成功页面失败", e);
                }
            }
        } else {
            //其他情况
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("-------------help2Pay浏览器回调接口信息记录------------开始回调商户浏览器");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付中页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_PROCESSING);
                } catch (IOException e) {
                    log.error("--------------help2Pay浏览器回调接口信息记录--------------调用AW支付中页面失败", e);
                }
            }
        }
    }

    /**
     * help2Pay服务器回调方法
     *
     * @param help2PayCallbackDTO help2Pay回调实体
     * @return
     */
    @Override
    public void help2PayServerCallback(Help2PayCallbackDTO help2PayCallbackDTO, HttpServletResponse response) {
        //校验回调参数
        if (!checkCallback(help2PayCallbackDTO)) {
            return;
        }
        //查询通道md5key
        ChannelsOrder channelsOrder = channelsOrderMapper.selectByPrimaryKey(help2PayCallbackDTO.getReference());
        //校验签名
        if (!createCallbackSign(help2PayCallbackDTO, channelsOrder.getMd5KeyStr())) {
            log.info("===============【help2Pay服务器回调接口信息记录】===============【签名不匹配】");
            return;
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(help2PayCallbackDTO.getReference());
        if (orders == null) {
            log.info("===============【help2Pay服务器回调接口信息记录】===============【回调订单信息不存在】");
            return;
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【help2Pay服务器回调接口信息记录】=================【订单状态不为支付中】");
            try {
                response.getWriter().write("success");
            } catch (IOException e) {
                log.error("**************help2Pay服务器回调方法发生异常**************", e.getMessage());
            }
            return;
        }
        //校验交易金额与交易币种
        BigDecimal channelAmount = new BigDecimal(help2PayCallbackDTO.getAmount());
        BigDecimal tradeAmount = orders.getTradeAmount();
        if (channelAmount.compareTo(tradeAmount) != 0 || !help2PayCallbackDTO.getCurrency().equals(orders.getTradeCurrency())) {
            log.info("=================【help2Pay服务器回调接口信息记录】=================【订单信息不匹配】");
            return;
        }
        String status = help2PayCallbackDTO.getStatus();//交易状态
        orders.setChannelNumber(help2PayCallbackDTO.getID());//通道流水号
        orders.setChannelCallbackTime(new Date());//通道回调时间
        orders.setUpdateTime(new Date());//修改时间
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orders.getId());
        criteria.andEqualTo("tradeStatus", "2");
        if ("000".equals(status)) {
            log.info("=================【help2Pay服务器回调接口信息记录】=================【订单已支付成功】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(help2PayCallbackDTO.getReference(), help2PayCallbackDTO.getID(), TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【help2Pay服务器回调接口信息记录】=================【更新通道订单异常】", e);
            }
            //修改订单状态
            int i = ordersMapper.updateByExampleSelective(orders, example);
            if (i > 0) {
                log.info("=================【help2Pay服务器回调接口信息记录】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【help2Pay服务器回调接口信息记录】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    FundChangeDTO fundChangeDTO = new FundChangeDTO(orders, TradeConstant.NT);
                    //上报清结算资金变动接口
                    BaseResponse fundChangeResponse = clearingService.fundChange(fundChangeDTO);
                    //请求成功
                    FundChangeVO fundChangeVO = (FundChangeVO) fundChangeResponse.getData();
                    if (fundChangeResponse.getCode().equals(TradeConstant.CLEARING_FAIL)) {
                        //业务处理失败
                        log.info("=================【help2Pay服务器回调接口信息记录】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    } else {
                        log.info("=================【help2Pay服务器回调接口信息记录】=================【上报清结算失败,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】");
                        RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                        rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                    }
                } catch (Exception e) {
                    log.error("=================【help2Pay服务器回调接口信息记录】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【help2Pay服务器回调接口信息记录】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
            try {
                //处理完业务逻辑需要给Help2Pay返回success
                response.getWriter().write("success");
            } catch (IOException e) {
                log.error("****help2Pay服务器回调方法发生异常返回success的场合*****************", e.getMessage());
            }
        } else if ("001".equals(status)) {
            log.info("=================【help2Pay服务器回调接口信息记录】=================【订单已支付失败】 orderId: {}", orders.getId());
            //支付失败
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            orders.setRemark4(status);
            try {
                //更改channelsOrders状态
                channelsOrderMapper.updateStatusById(help2PayCallbackDTO.getReference(), help2PayCallbackDTO.getID(), TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【help2Pay服务器回调接口信息记录】=================【更新通道订单异常】", e);
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【help2Pay服务器回调接口信息记录】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【help2Pay服务器回调接口信息记录】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
            try {
                response.getWriter().write("True");
            } catch (IOException e) {
                log.error("****help2Pay服务器回调方法发生异常返回true的场合*****************", e.getMessage());
            }
        } else {
            log.info("=================【help2Pay服务器回调接口信息记录】=================【订单是其他状态】 status: {}", status);
        }
        try {
            //商户服务器回调地址不为空,回调商户服务器
            if (!StringUtils.isEmpty(orders.getServerUrl())) {
                commonBusinessService.replyReturnUrl(orders);
            }
        } catch (Exception e) {
            log.error("=================【help2Pay服务器回调接口信息记录】=================【回调商户异常】", e);
        }
    }


    /**
     * 校验help2Pay回调参数
     *
     * @param help2PayCallbackDTO help2Pay回调实体
     * @return
     */
    private static boolean checkCallback(Help2PayCallbackDTO help2PayCallbackDTO) {
        //商户上送的商户订单号
        if (StringUtils.isEmpty(help2PayCallbackDTO.getReference())) {
            log.info("-------------help2Pay浏览器回调接口信息记录------------订单id为空");
            return false;
        }
        if (StringUtils.isEmpty(help2PayCallbackDTO.getStatus())) {
            log.info("-------------help2Pay浏览器回调接口信息记录------------订单状态为空");
            return false;
        }
        if (StringUtils.isEmpty(help2PayCallbackDTO.getKey())) {
            log.info("-------------help2Pay浏览器回调接口信息记录------------签名为空");
            return false;
        }
        return true;
    }


    /**
     * help2Pay生成网银收单签名
     *
     * @return sign
     */
    private String createHelp2PaySign(Help2PayRequestDTO help2PayRequestDTO, String md5KeyStr) {
        String origin = "";//签名前的明文字符串
        String key = null;//生成签名后的key
        if (help2PayRequestDTO != null) {
            if (!StringUtils.isEmpty(help2PayRequestDTO.getMerchant())) {
                origin = origin + help2PayRequestDTO.getMerchant();
            }
            if (!StringUtils.isEmpty(help2PayRequestDTO.getReference())) {
                origin = origin + help2PayRequestDTO.getReference();
            }
            if (!StringUtils.isEmpty(help2PayRequestDTO.getCustomer())) {
                origin = origin + help2PayRequestDTO.getCustomer();
            }
            if (!StringUtils.isEmpty(help2PayRequestDTO.getAmount())) {
                origin = origin + help2PayRequestDTO.getAmount();
            }
            if (!StringUtils.isEmpty(help2PayRequestDTO.getCurrency())) {
                origin = origin + help2PayRequestDTO.getCurrency();
            }
            if (!StringUtils.isEmpty(help2PayRequestDTO.getDatetime())) {
                origin = origin + help2PayRequestDTO.getDatetime();
            }
            if (!StringUtils.isEmpty(md5KeyStr)) {
                origin = origin + md5KeyStr;
            }
            if (!StringUtils.isEmpty(help2PayRequestDTO.getClientIP())) {
                origin = origin + help2PayRequestDTO.getClientIP();
            }
            if (!StringUtils.isEmpty(origin)) {
                origin = origin.trim();
            }
            log.info("-------------------------------------help2Pay线上网银收单通道接口信息记录-------------------------------------签名前的明文:{}", origin);
            key = MD5.MD5Encode(origin).toUpperCase();
            log.info("-------------------------------------help2Pay线上网银收单通道接口信息记录-------------------------------------签名后的密文:{}", key);
        }
        return key;
    }

    /**
     * help2Pay生成回调签名
     *
     * @return sign
     */
    private static boolean createCallbackSign(Help2PayCallbackDTO help2PayCallbackDTO, String md5KeyStr) {
        String origin = "";//签名前的明文字符串
        String key = null;//生成签名后的key
        if (help2PayCallbackDTO != null) {
            if (!StringUtils.isEmpty(help2PayCallbackDTO.getMerchant())) {
                origin = origin + help2PayCallbackDTO.getMerchant();
            }
            if (!StringUtils.isEmpty(help2PayCallbackDTO.getReference())) {
                origin = origin + help2PayCallbackDTO.getReference();
            }
            if (!StringUtils.isEmpty(help2PayCallbackDTO.getCustomer())) {
                origin = origin + help2PayCallbackDTO.getCustomer();
            }
            if (!StringUtils.isEmpty(help2PayCallbackDTO.getAmount())) {
                origin = origin + help2PayCallbackDTO.getAmount();
            }
            if (!StringUtils.isEmpty(help2PayCallbackDTO.getCurrency())) {
                origin = origin + help2PayCallbackDTO.getCurrency();
            }
            if (!StringUtils.isEmpty(help2PayCallbackDTO.getStatus())) {
                origin = origin + help2PayCallbackDTO.getStatus();
            }
            if (!StringUtils.isEmpty(md5KeyStr)) {
                origin = origin + md5KeyStr;
            }
            if (!StringUtils.isEmpty(origin)) {
                origin = origin.trim();
            }
            log.info("-------------------------------------help2Pay回调接口信息记录-------------------------------------签名前的明文:{}", origin);
            key = MD5.MD5Encode(origin).toUpperCase();
            log.info("-------------------------------------help2Pay回调接口信息记录-------------------------------------签名后的密文:{}", key);
        }
        return key.equals(help2PayCallbackDTO.getKey());
    }
}
