package com.asianwallets.trade.channels.megaPay.impl;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.constant.AD3MQConstant;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.megapay.MegaPayIDRRequestDTO;
import com.asianwallets.common.dto.megapay.MegaPayRequestDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.MD5;
import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.megaPay.MegaPayService;
import com.asianwallets.trade.config.AD3ParamsConfig;
import com.asianwallets.trade.dao.ChannelsOrderMapper;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.MegaPayBrowserCallbackDTO;
import com.asianwallets.trade.dto.MegaPayIDRBrowserCallbackDTO;
import com.asianwallets.trade.dto.MegaPayIDRServerCallbackDTO;
import com.asianwallets.trade.dto.MegaPayServerCallbackDTO;
import com.asianwallets.trade.feign.ChannelsFeign;
import com.asianwallets.trade.rabbitmq.RabbitMQSender;
import com.asianwallets.trade.service.ClearingService;
import com.asianwallets.trade.service.CommonBusinessService;
import com.asianwallets.trade.service.CommonRedisDataService;
import com.asianwallets.trade.service.CommonService;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
@HandlerType(TradeConstant.MEGAPAY)
public class MegaPayServiceImpl extends ChannelsAbstractAdapter implements MegaPayService {

    @Autowired
    @Qualifier(value = "ad3ParamsConfig")
    private AD3ParamsConfig ad3ParamsConfig;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private OrdersMapper ordersMapper;


    @Autowired
    private ChannelsFeign channelsFeign;

    @Autowired
    private ChannelsOrderMapper channelsOrderMapper;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private CommonRedisDataService commonRedisDataService;

    @Autowired
    private CommonService commonService;

    /**
     * MegaPay网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        BaseResponse response = new BaseResponse();
        if (orders.getTradeCurrency().equalsIgnoreCase("THB")) {//megaPay THB通道
            MegaPayRequestDTO megaPayRequestDTO = new MegaPayRequestDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/onlineCallback/megaPayThbBrowserCallback"));
            log.info("===============【MegaPay-THB网银收单】===============【调用Channels服务-请求参数】 megaPayRequestDTO: {}", JSON.toJSONString(megaPayRequestDTO));
            response = channelsFeign.megaPayTHB(megaPayRequestDTO);
            log.info("===============【MegaPay-THB网银收单】===============【调用Channels服务-响应参数】 response: {}", JSON.toJSONString(response));
            //状态码不为200的时候
            if (!TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
                log.info("==============【MegaPay-THB网银收单】==============调用Channels服务【Help2Pay接口】-状态码异常 code: {}", response.getCode());
                throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
            }
            JSONObject param = new JSONObject();
            param.put("orderId", orders.getId());
            param.put("channelMerchantId", channel.getChannelMerchantId());
            log.info("===============【MegaPay-THB网银收单】===============【上报MegaPay-THB查询订单队列1】");
            RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(param));
            rabbitMQSender.send(AD3MQConstant.E_MQ_MEGAPAY_THB_CHECK_ORDER, JSON.toJSONString(rabbitMassage));
        } else if (orders.getTradeCurrency().equalsIgnoreCase("IDR")) {//megaPay IDR通道
            MegaPayIDRRequestDTO megaPayIDRRequestDTO = new MegaPayIDRRequestDTO(orders, channel, ad3ParamsConfig.getChannelCallbackUrl().concat("/onlineCallback/megaPayIdrBrowserCallback"));
            log.info("===============【MegaPay-IDR网银收单】===============【调用Channels服务-请求参数】 megaPayIDRRequestDTO: {}", JSON.toJSONString(megaPayIDRRequestDTO));
            response = channelsFeign.megaPayIDR(megaPayIDRRequestDTO);
            log.info("===============【MegaPay-IDR网银收单】===============【调用Channels服务-响应参数】 response: {}", JSON.toJSONString(response));
            //状态码不为200的时候
            if (!TradeConstant.HTTP_SUCCESS.equals(response.getCode())) {
                log.info("==============【MegaPay-IDR网银收单】==============用Channels服务【Help2Pay接口】-状态码异常 code: {}", response.getCode());
                throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
            }
        }
        if (StringUtils.isEmpty(response.getData())) {
            throw new BusinessException(EResultEnum.ORDER_CREATION_FAILED.getCode());
        }
        String megaInfo = (String) response.getData();
        OnlineTradeVO onlineTradeVO = new OnlineTradeVO();
        if (megaInfo.replaceAll("\\s*", "").matches(".*html.*")) {
            //网银
            onlineTradeVO.setRespCode("T000");
            if (megaInfo.contains("href=\"")) {
                onlineTradeVO.setCode_url(TradeConstant.START + megaInfo.substring(megaInfo.indexOf("href=\"") + 6, megaInfo.indexOf("\">here</a>")) + TradeConstant.END);
            } else {
                onlineTradeVO.setCode_url(megaInfo);
            }
            onlineTradeVO.setType(TradeConstant.ONLINE_BANKING);
            response.setData(onlineTradeVO);
            return response;
        }
        return response;
    }


    /**
     * MegaPayTHB服务器回调方法
     *
     * @param megaPayServerCallbackDTO megaPayTHB回调参数
     * @return
     */
    @Override
    public void megaPayThbServerCallback(MegaPayServerCallbackDTO megaPayServerCallbackDTO, HttpServletRequest request, HttpServletResponse response) {
        //校验回调参数
        if (!checkTHBCallback(megaPayServerCallbackDTO)) {
            return;
        }
        //查询通道MD5Key
        ChannelsOrder channelsOrder = channelsOrderMapper.selectByPrimaryKey(megaPayServerCallbackDTO.getInv());
        //根据通道md5KeyStr获取交易状态
        String result = request.getParameter(channelsOrder.getMd5KeyStr());
        megaPayServerCallbackDTO.setResult(result);
        megaPayServerCallbackDTO.setMd5KeyStr(channelsOrder.getMd5KeyStr());
        log.info("===========【megaPayTHB服务器回调方法信息记录】==============【交易状态】: {}", result);
        if (megaPayServerCallbackDTO.getInv().startsWith("CBO")) {
            log.info("===========【megaPayTHB服务器回调方法信息记录】==============这笔回调订单信息属于AD3 inv:{}", megaPayServerCallbackDTO.getInv());
            //分发给AD3
            megaTHBCallbackAD3(megaPayServerCallbackDTO, "megaPayReturn.do");
            return;
        }
        //设置金额显示
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(3);//设置数值的小数部分允许的最小位数。
        numberFormat.setMaximumFractionDigits(2);//设置数值的小数部分允许的最大位数。
        numberFormat.setMaximumIntegerDigits(15);//设置数值的整数部分允许的最大位数。
        numberFormat.setMinimumIntegerDigits(1);//设置数值的整数部分允许的最小位数
        String sign = megaPayServerCallbackDTO.getRefCode() + megaPayServerCallbackDTO.getInv() + channelsOrder.getMd5KeyStr() + result + numberFormat.format(Double.valueOf(megaPayServerCallbackDTO.getAmt()));
        log.info("===========【megaPayTHB服务器回调方法信息记录】==============签名前的明文: {}", sign);
        String signMsg = MD5.MD5Encode(sign).toUpperCase();
        log.info("===========【megaPayTHB服务器回调方法信息记录】==============签名后的密文: {}", signMsg);
        //验签
        if (!org.apache.commons.lang.StringUtils.equals(sign, signMsg)) {
            log.info("===========【megaPayTHB服务器回调方法信息记录】==============签名不匹配");
            return;
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(megaPayServerCallbackDTO.getInv());
        if (orders == null) {
            log.info("===========【megaPayTHB服务器回调方法信息记录】==============【回调订单信息不存在】");
            return;
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【megaPayTHB服务器回调方法信息记录】=================【订单状态不为支付中】");
            return;
        }
        //校验订单信息
        if (new BigDecimal(megaPayServerCallbackDTO.getAmt()).compareTo(orders.getTradeAmount()) != 0) {
            log.info("=================【megaPayTHB服务器回调方法信息记录】=================【订单信息不匹配】");
            return;
        }
        orders.setUpdateTime(new Date());//修改时间
        orders.setChannelCallbackTime(new Date());//通道回调时间
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orders.getId());
        criteria.andEqualTo("tradeStatus", "2");
        if ("000".equals(megaPayServerCallbackDTO.getResult())) {
            log.info("===========【megaPayTHB服务器回调方法信息记录】==============【订单已支付成功】 orderId: {}", orders.getId());
            //支付成功
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), null, TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("===========【megaPayTHB服务器回调方法信息记录】==============【更新通道订单异常】");
            }
            //修改原订单状态
            int i = ordersMapper.updateByExampleSelective(orders, example);
            if (i > 0) {
                log.info("=================【megaPayTHB服务器回调方法信息记录】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【megaPayTHB服务器回调方法信息记录】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【megaPayTHB服务器回调方法信息记录】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【megaPayTHB服务器回调方法信息记录】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("===========【megaPayTHB服务器回调方法信息记录】==============【订单已支付失败】 orderId: {}", orders.getId());
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            //上游返回的错误code
            orders.setRemark5(megaPayServerCallbackDTO.getResult());
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), null, TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("===========【megaPayTHB服务器回调方法信息记录】==============【更新通道订单异常】");
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            log.info("-------------megaPayTHB服务器回调方法信息记录------------订单已支付失败");
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【megaPayTHB服务器回调方法信息记录】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【megaPayTHB服务器回调方法信息记录】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        //商户服务器回调地址不为空,回调商户服务器
        if (!StringUtils.isEmpty(orders.getServerUrl())) {
            try {
                commonBusinessService.replyReturnUrl(orders);
            } catch (Exception e) {
                log.error("===========【megaPayTHB服务器回调方法信息记录】==============回调商户服务器异常", e);
            }
        }
    }


    /**
     * MegaPayIDR浏览器回调方法
     *
     * @param megaPayIDRBrowserCallbackDTO megaPayIDR回调参数
     * @return
     */
    /**
     * MegaPayTHB浏览器回调方法
     *
     * @param megaPayBrowserCallbackDTO megaPay回调参数
     * @return
     */
    @Override
    public void megaPayThbBrowserCallback(MegaPayBrowserCallbackDTO megaPayBrowserCallbackDTO, HttpServletResponse response) {
        if (!StringUtils.isEmpty(megaPayBrowserCallbackDTO.getOrderID())) {
            if (megaPayBrowserCallbackDTO.getOrderID().startsWith("CBO")) {
                log.info("===========【megaPayTHB浏览器回调方法信息记录】==============这笔回调订单信息属于AD3 orderId:{}", megaPayBrowserCallbackDTO.getOrderID());
                String ad3Url = ad3ParamsConfig.getAd3ItsUrl().concat("megaPayToMerchant.do");
                log.info("----------------------回调信息分发AD3方法记录----------------------分发AD3URL:{},参数:{}", ad3Url, JSON.toJSONString(megaPayBrowserCallbackDTO));
                //分发给AD3
                cn.hutool.http.HttpResponse execute = HttpRequest.get(ad3Url)
                        .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                        .form(BeanToMapUtil.beanToMap(megaPayBrowserCallbackDTO))
                        .timeout(20000)
                        .execute();
                String body = execute.body();
                log.info("----------------------回调信息分发AD3方法记录----------------------回调返回 status:{}, body:{}", execute.getStatus(), body);
                //判断HTTP状态码
                if (execute.getStatus() == AsianWalletConstant.HTTP_SUCCESS_STATUS) {
                    log.info("----------------------回调信息分发AD3方法记录----------------------分发AD3成功 http状态码:{}", execute.getStatus());
                    return;
                }
                log.info("----------------------回调信息分发AD3方法记录----------------------分发AD3失败 http状态码:{}", execute.getStatus());
                return;
            }
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(megaPayBrowserCallbackDTO.getOrderID());
        if (orders == null) {
            log.info("-------------megaPayTHB浏览器回调接口信息记录------------回调订单信息不存在");
            return;
        }
        if (orders.getTradeStatus().equals(TradeConstant.ORDER_PAY_SUCCESS)) {
            //支付成功
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("-------------megaPayTHB浏览器回调接口信息记录------------开始回调商户");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付成功页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_SUCCESS);
                } catch (IOException e) {
                    log.info("--------------megaPayTHB浏览器回调接口信息记录--------------调用AW支付成功页面失败", e);
                }
            }
        } else {
            //其他情况
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("-------------megaPayTHB浏览器回调接口信息记录------------开始回调商户");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付中页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_PROCESSING);
                } catch (IOException e) {
                    log.info("--------------megaPayTHB浏览器回调接口信息记录--------------调用AW支付中页面失败", e);
                }
            }
        }
    }


    /**
     * MegaPayIDR服务器回调方法
     *
     * @param megaPayIDRServerCallbackDTO megaPayIDR回调参数
     * @return
     */
    @Override
    public void megaPayIdrServerCallback(MegaPayIDRServerCallbackDTO megaPayIDRServerCallbackDTO, HttpServletRequest request, HttpServletResponse response) {
        //校验订单参数
        if (!checkIDRCallback(megaPayIDRServerCallbackDTO)) {
            return;
        }
        //查询通道MD5Key
        ChannelsOrder channelsOrder = channelsOrderMapper.selectByPrimaryKey(megaPayIDRServerCallbackDTO.getNp_inv());
        if (channelsOrder == null) {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================通道订单信息不存在");
            return;
        }
        //根据通道md5KeyStr获取交易状态
        String result = request.getParameter(channelsOrder.getMd5KeyStr());
        megaPayIDRServerCallbackDTO.setResult(result);//交易结果
        megaPayIDRServerCallbackDTO.setMd5KeyStr(channelsOrder.getMd5KeyStr());//md5Key
        if (megaPayIDRServerCallbackDTO.getNp_inv().startsWith("CBO")) {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================这笔回调订单信息属于AD3 orderId:{}", megaPayIDRServerCallbackDTO.getNp_inv());
            megaIDRCallbackAD3(megaPayIDRServerCallbackDTO, "nextPayRemitReturn.do");
            return;
        }
        //设置金额显示
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(3);//设置数值的小数部分允许的最小位数。
        numberFormat.setMaximumFractionDigits(2);//设置数值的小数部分允许的最大位数。
        numberFormat.setMaximumIntegerDigits(15);//设置数值的整数部分允许的最大位数。
        numberFormat.setMinimumIntegerDigits(1);//设置数值的整数部分允许的最小位数
        String responsePasswordKey = "111233";
        String sign = megaPayIDRServerCallbackDTO.getNp_refCode() + megaPayIDRServerCallbackDTO.getNp_inv() + responsePasswordKey + channelsOrder.getMd5KeyStr() + result + numberFormat.format(Double.valueOf(megaPayIDRServerCallbackDTO.getNp_amt()));
        log.info("=================【megaPayIDR服务器回调方法信息记录】=================签名前的明文: {}", sign);
        String signMsg = MD5.MD5Encode(sign);
        log.info("=================【megaPayIDR服务器回调方法信息记录】=================签名后的密文: {}", signMsg);
        //验签
        if (!org.apache.commons.lang.StringUtils.equals(sign, signMsg)) {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================签名不匹配");
            return;
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(megaPayIDRServerCallbackDTO.getNp_inv());
        if (orders == null) {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================回调订单信息不存在");
            return;
        }
        //校验订单状态
        if (!TradeConstant.ORDER_PAYING.equals(orders.getTradeStatus())) {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================【订单状态不为支付中】");
            return;
        }
        //校验订单信息
        if (new BigDecimal(megaPayIDRServerCallbackDTO.getNp_amt()).compareTo(orders.getTradeAmount()) != 0) {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================【订单信息不匹配】");
            return;
        }
        orders.setUpdateTime(new Date());//修改时间
        orders.setChannelCallbackTime(new Date());//通道回调时间
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tradeStatus", "2");
        criteria.andEqualTo("id", orders.getId());
        if ("000".equals(megaPayIDRServerCallbackDTO.getResult())) {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================【订单已支付成功】 orderId: {}", orders.getId());
            //支付成功
            orders.setTradeStatus(TradeConstant.ORDER_PAY_SUCCESS);
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), null, TradeConstant.TRADE_SUCCESS);
            } catch (Exception e) {
                log.error("=================【megaPayIDR服务器回调方法信息记录】=================【更新通道订单异常】", e);
            }
            //修改原订单状态
            int i = ordersMapper.updateByExampleSelective(orders, example);
            if (i > 0) {
                log.info("=================【megaPayIDR服务器回调方法信息记录】=================【订单支付成功后更新数据库成功】 orderId: {}", orders.getId());
                //计算支付成功时的通道网关手续费
                commonBusinessService.calcCallBackGatewayFeeSuccess(orders);
                //TODO 添加日交易限额与日交易笔数
                //commonBusinessService.quota(orders.getMerchantId(), orders.getProductCode(), orders.getTradeAmount());
                //支付成功后向用户发送邮件
                commonBusinessService.sendEmail(orders);
                try {
                    //账户信息不存在的场合创建对应的账户信息
                    if (commonRedisDataService.getAccountByMerchantIdAndCurrency(orders.getMerchantId(), orders.getOrderCurrency()) == null) {
                        log.info("=================【megaPayIDR服务器回调方法信息记录】=================【上报清结算前线下下单创建账户信息】");
                        commonBusinessService.createAccount(orders);
                    }
                    //分润
                    if (!StringUtils.isEmpty(orders.getAgentCode()) || !StringUtils.isEmpty(orders.getRemark8())) {
                        rabbitMQSender.send(AD3MQConstant.SAAS_FR_DL, orders.getId());
                    }
                    //更新成功,上报清结算
                    commonService.fundChangePlaceOrderSuccess(orders);
                } catch (Exception e) {
                    log.error("=================【megaPayIDR服务器回调方法信息记录】=================【上报清结算异常,上报队列】 【MQ_PLACE_ORDER_FUND_CHANGE_FAIL】", e);
                    RabbitMassage rabbitMassage = new RabbitMassage(AsianWalletConstant.THREE, JSON.toJSONString(orders));
                    rabbitMQSender.send(AD3MQConstant.MQ_PLACE_ORDER_FUND_CHANGE_FAIL, JSON.toJSONString(rabbitMassage));
                }
            } else {
                log.info("=================【megaPayIDR服务器回调方法信息记录】=================【订单支付成功后更新数据库失败】 orderId: {}", orders.getId());
            }
        } else {
            log.info("=================【megaPayIDR服务器回调方法信息记录】=================订单已支付失败");
            //支付失败
            orders.setTradeStatus(TradeConstant.ORDER_PAY_FAILD);
            //更改channelsOrders状态
            try {
                channelsOrderMapper.updateStatusById(orders.getId(), null, TradeConstant.TRADE_FALID);
            } catch (Exception e) {
                log.error("=================【megaPayIDR服务器回调方法信息记录】=================【更新通道订单异常】", e);
            }
            //计算支付失败时通道网关手续费
            commonBusinessService.calcCallBackGatewayFeeFailed(orders);
            if (ordersMapper.updateByExampleSelective(orders, example) == 1) {
                log.info("=================【megaPayIDR服务器回调方法信息记录】=================【订单支付失败后更新数据库成功】 orderId: {}", orders.getId());
            } else {
                log.info("=================【megaPayIDR服务器回调方法信息记录】=================【订单支付失败后更新数据库失败】 orderId: {}", orders.getId());
            }
        }
        //商户服务器回调地址不为空,回调商户服务器
        if (!StringUtils.isEmpty(orders.getServerUrl())) {
            try {
                commonBusinessService.replyReturnUrl(orders);
            } catch (Exception e) {
                log.error("=================【megaPayIDR服务器回调方法信息记录】=================回调商户服务器异常", e);
            }
        }
    }

    @Override
    public void megaPayIdrBrowserCallback(MegaPayIDRBrowserCallbackDTO megaPayIDRBrowserCallbackDTO, HttpServletResponse response) {
        if (!StringUtils.isEmpty(megaPayIDRBrowserCallbackDTO.getE_inv())) {
            if (megaPayIDRBrowserCallbackDTO.getE_inv().startsWith("CBO")) {
                log.info("-------------megaPayIDR浏览器回调接口信息记录------------这笔回调订单信息属于AD3 orderId:{}", megaPayIDRBrowserCallbackDTO.getE_inv());
                callbackAD3(megaPayIDRBrowserCallbackDTO, "nextPayRemitToMerchant.do");
                return;
            }
        }
        //查询原订单信息
        Orders orders = ordersMapper.selectByPrimaryKey(megaPayIDRBrowserCallbackDTO.getE_inv());
        if (orders == null) {
            log.info("-------------megaPayIDR浏览器回调接口信息记录------------回调订单信息不存在");
            return;
        }
        if (orders.getTradeStatus().equals(TradeConstant.ORDER_PAY_SUCCESS)) {
            //支付成功
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("-------------megaPayIDR浏览器回调接口信息记录------------开始回调商户");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付成功页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_SUCCESS);
                } catch (IOException e) {
                    log.info("--------------megaPayIDR浏览器回调接口信息记录--------------调用AW支付成功页面失败", e);
                }
            }
        } else {
            //其他情况
            if (!StringUtils.isEmpty(orders.getBrowserUrl())) {
                log.info("-------------megaPayIDR浏览器回调接口信息记录------------开始回调商户");
                commonBusinessService.replyJumpUrl(orders, response);
            } else {
                try {
                    //返回支付中页面
                    response.sendRedirect(ad3ParamsConfig.getPaySuccessUrl() + "?page=" + TradeConstant.PAGE_PROCESSING);
                } catch (IOException e) {
                    log.info("--------------megaPayIDR浏览器回调接口信息记录--------------调用AW支付中页面失败", e);
                }
            }
        }
    }

    /**
     * megaPay-THB通道分发AD3
     *
     * @param megaPayServerCallbackDTO 参数
     * @param url                      url
     */
    public String megaTHBCallbackAD3(MegaPayServerCallbackDTO megaPayServerCallbackDTO, String url) {
        String ad3Url = ad3ParamsConfig.getAd3ItsUrl().concat(url);
        log.info("----------------------megaPay-THB通道回调AD3信息记录----------------------分发AD3URL:{}", ad3Url);
        Map<String, Object> map = new HashMap<>();
        map.put("inv", megaPayServerCallbackDTO.getInv());
        map.put("amt", megaPayServerCallbackDTO.getAmt());
        map.put("merID", megaPayServerCallbackDTO.getMerID());
        map.put("refCode", megaPayServerCallbackDTO.getRefCode());
        map.put("mark", megaPayServerCallbackDTO.getMark());
        map.put(megaPayServerCallbackDTO.getMd5KeyStr(), megaPayServerCallbackDTO.getResult());
        log.info("----------------------megaPay-THB通道回调AD3信息记录----------------------分发AD3参数:{}", JSON.toJSONString(map));
        //分发给AD3
        cn.hutool.http.HttpResponse execute = HttpRequest.post(ad3Url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(map)
                .timeout(20000)
                .execute();
        String body = execute.body();
        log.info("----------------------megaPay-THB通道回调AD3信息记录----------------------回调返回 http状态码:{}, body:{}", execute.getStatus(), body);
        return body;
    }

    /**
     * 分发AD3
     *
     * @param obj 参数
     * @param url url
     */
    public String callbackAD3(Object obj, String url) {
        String ad3Url = ad3ParamsConfig.getAd3ItsUrl().concat(url);
        log.info("----------------------回调信息分发AD3方法记录----------------------分发AD3URL:{}", ad3Url);
        log.info("----------------------回调信息分发AD3方法记录----------------------分发AD3参数:{}", JSON.toJSONString(obj));
        //分发给AD3
        cn.hutool.http.HttpResponse execute = HttpRequest.post(ad3Url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(BeanToMapUtil.beanToMap(obj))
                .timeout(20000)
                .execute();
        String body = execute.body();
        log.info("----------------------回调信息分发AD3方法记录----------------------回调返回 http状态码:{}, body:{}", execute.getStatus(), body);
        return body;
    }

    /**
     * megaPay-IDR通道分发AD3
     *
     * @param megaPayIDRServerCallbackDTO 参数
     * @param url                         url
     */
    public String megaIDRCallbackAD3(MegaPayIDRServerCallbackDTO megaPayIDRServerCallbackDTO, String url) {
        String ad3Url = ad3ParamsConfig.getAd3ItsUrl().concat(url);
        log.info("----------------------megaPay-IDR通道回调AD3信息记录----------------------分发AD3URL:{}", ad3Url);
        Map<String, Object> map = new HashMap<>();
        map.put("np_inv", megaPayIDRServerCallbackDTO.getNp_inv());
        map.put("np_amt", megaPayIDRServerCallbackDTO.getNp_amt());
        map.put("np_merID", megaPayIDRServerCallbackDTO.getNp_merID());
        map.put("np_refCode", megaPayIDRServerCallbackDTO.getNp_refCode());
        map.put("np_mark", megaPayIDRServerCallbackDTO.getNp_mark());
        map.put(megaPayIDRServerCallbackDTO.getMd5KeyStr(), megaPayIDRServerCallbackDTO.getResult());
        log.info("----------------------megaPay-IDR通道回调AD3信息记录----------------------分发AD3参数:{}", JSON.toJSONString(map));
        //分发给AD3
        cn.hutool.http.HttpResponse execute = HttpRequest.post(ad3Url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(map)
                .timeout(20000)
                .execute();
        String body = execute.body();
        log.info("----------------------megaPay-IDR通道回调AD3信息记录----------------------回调返回 http状态码:{}, body:{}", execute.getStatus(), body);
        return body;
    }

    /**
     * 校验megaPayTHB服务回调参数
     *
     * @param megaPayServerCallbackDTO
     * @return
     */
    private boolean checkTHBCallback(MegaPayServerCallbackDTO megaPayServerCallbackDTO) {
        if (StringUtils.isEmpty(megaPayServerCallbackDTO.getInv())) {
            log.info("-------------megaPay回调方法信息记录------------订单id为空");
            return false;
        }
        return true;
    }

    /**
     * 校验megaPayIDR服务回调参数
     *
     * @param megaPayIDRServerCallbackDTO
     * @return
     */
    private boolean checkIDRCallback(MegaPayIDRServerCallbackDTO megaPayIDRServerCallbackDTO) {
        if (StringUtils.isEmpty(megaPayIDRServerCallbackDTO.getNp_inv())) {
            log.info("-------------megaPay回调方法信息记录------------订单id为空");
            return false;
        }
        return true;
    }
}
