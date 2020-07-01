package com.asianwallets.base.controller;
import com.asianwallets.base.service.CommonService;
import com.asianwallets.common.entity.MerchantCardCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(description = "静态码跳转功能")
@RequestMapping("/qr")
@Slf4j
public class QrCodeForwardController {
    /**
     * 跳转到前端url
     */
    @Value("${file.http.frontPage}")
    private String frontPage;

    @Autowired
    private CommonService commonService;

    @ApiOperation(value = "静态码跳转功能")
    @GetMapping("/forward")
    @CrossOrigin
    public void forward(@RequestParam @ApiParam String id, HttpServletResponse response, HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        log.info("*******调用静态码支付跳转的输入金额的页面带过去的输入参数**************** 静态码编号:{},扫码的标志:{}",id,userAgent);
        ModelAndView mv=new ModelAndView();
        mv.addObject("id",id);
        mv.addObject("userAgent",userAgent);
        MerchantCardCode merchantCardCode = commonService.getMerchantCardCode(id);
        mv.addObject("merchantLogo",commonService.getMerchant(merchantCardCode.getMerchantId()).getMerchantLogo());
        mv.addObject("merchantName",merchantCardCode.getMerchantName());
        if(userAgent!=null){
            try {
                response.sendRedirect(frontPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
