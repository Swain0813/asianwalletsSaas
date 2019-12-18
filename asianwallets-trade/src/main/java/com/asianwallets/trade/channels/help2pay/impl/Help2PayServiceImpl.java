package com.asianwallets.trade.channels.help2pay.impl;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Help2PayServiceImpl  extends ChannelsAbstract implements Help2PayService{


    /**
     * 线下CSB处理方法
     *
     * @param orders       订单
     * @param channel      通道
     */
    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel){
        return null;
    };
    /**
     * 线下BSC处理方法
     *
     * @param orders       订单
     * @param channel      通道
     * @param authCode     支付条码
     * @return 通用响应实体
     */
    @Override
    public  BaseResponse offlineBSC(Orders orders, Channel channel, String authCode){
        System.out.println("-------------------- offlineBSC ----------------------");
          return null;
      };



    /**
     * 线上收单方法
     *
     * @param orders       订单
     * @param channel      通道
     * @return 通用响应实体
     */
    @Override
    public  BaseResponse onlinePay(Orders orders, Channel channel){
        System.out.println("--------  onlinePay ---------");
          return null;
      };


    /**
     * 退款方法
     *
     * @param orders       订单
     * @param channel      通道
     * @return 通用响应实体
     */
    @Override
    public BaseResponse refund(Orders orders, Channel channel){
          return null;
      };
    /**
     * 撤销方法
     *
     * @param orders       订单
     * @param channel      通道
     * @return 通用响应实体
     */
    @Override
    public  BaseResponse cancle(Orders orders, Channel channel){
          return null;
      };





}
