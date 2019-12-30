package com.asianwallets.trade.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.dto.UndoDTO;
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
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(description = "退款和撤销接口")
@RequestMapping("/trade")
@Slf4j
public class RefundTradeController extends BaseController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RefundTradeService refundTradeService;

    @Autowired
    private CommonService commonService;


    @ApiOperation(value = "退款接口")
    @PostMapping("/refund")
    @CrossOrigin
    public BaseResponse refundOrder(RefundDTO refundDTO) {
        //线下判断交易密码
        if (TradeConstant.TRADE_UPLINE.equals(refundDTO.getTradeDirection())) {
            if (StringUtils.isEmpty(refundDTO.getToken())) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            SysUserVO sysUserVO = JSON.parseObject(redisService.get(refundDTO.getToken()), SysUserVO.class);
            if (sysUserVO == null) {
                throw new BusinessException(EResultEnum.USER_IS_NOT_LOGIN.getCode());
            }
            if (!commonService.checkPassword(refundDTO.getTradePassword(), sysUserVO.getTradePassword())) {
                throw new BusinessException(EResultEnum.TRADE_PASSWORD_ERROR.getCode());
            }
        }
        BaseResponse baseResponse = refundTradeService.refundOrder(refundDTO, this.getReqIp());
        return ResultUtil.success(baseResponse.getCode(), this.getErrorMsgMap(baseResponse.getCode()));
    }


    @ApiOperation(value = "撤销接口")
    @PostMapping("/reverse")
    @CrossOrigin
    public BaseResponse reverse(UndoDTO undoDTO) {
        BaseResponse baseResponse = refundTradeService.undo(undoDTO, this.getReqIp());
        return ResultUtil.success(baseResponse.getCode(), this.getErrorMsgMap(baseResponse.getCode()));
    }

    @ApiOperation(value = "人工退款接口")
    @GetMapping("artificialRefund")
    public BaseResponse artificialRefund(@RequestParam @ApiParam String refundOrderId, Boolean enabled, String remark) {
        BaseResponse baseResponse = refundTradeService.artificialRefund(this.getSysUserVO().getUsername(), refundOrderId, enabled, remark);
        return ResultUtil.success(baseResponse);
    }


}
