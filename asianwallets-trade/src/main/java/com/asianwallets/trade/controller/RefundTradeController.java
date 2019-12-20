package com.asianwallets.trade.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.SysUserVO;
import com.asianwallets.trade.service.CommonService;
import com.asianwallets.trade.service.RefundTradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 退款
 * @author: YangXu
 * @create: 2019-12-18 11:27
 **/
@RestController
@Api(description = "退款接口")
@RequestMapping("/refund")
public class RefundTradeController extends BaseController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RefundTradeService refundTradeService;

    @Autowired
    private CommonService commonService;

    @ApiOperation(value = "退款接口")
    @PostMapping("/refundOrder")
    @CrossOrigin
    public BaseResponse refundOrder(RefundDTO refundDTO) {
        //线下判断交易密码
        if (TradeConstant.TRADE_UPLINE.equals(refundDTO.getTradeDirection())) {
            if(StringUtils.isEmpty(refundDTO.getToken())){
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            SysUserVO sysUserVO = JSON.parseObject(redisService.get(refundDTO.getToken()), SysUserVO.class);
            if(sysUserVO==null){//获取不到用户信息
                throw new BusinessException(EResultEnum.USER_IS_NOT_LOGIN.getCode());
            }
            if (!commonService.checkPassword(refundDTO.getTradePassword(), sysUserVO.getTradePassword())) {
                throw new BusinessException(EResultEnum.TRADE_PASSWORD_ERROR.getCode());
            }
        }
        BaseResponse baseResponse = refundTradeService.refundOrder(refundDTO, this.getReqIp());
        if (StringUtils.isEmpty(baseResponse.getMsg())) {
            baseResponse.setCode(EResultEnum.SUCCESS.getCode());
            baseResponse.setMsg("SUCCESS");
            return baseResponse;
        } else {
            return ResultUtil.error(baseResponse.getMsg(), this.getErrorMsgMap(baseResponse.getMsg()));
        }
    }


}
