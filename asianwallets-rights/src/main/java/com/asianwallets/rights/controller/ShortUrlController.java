package com.asianwallets.rights.controller;
import com.asianwallets.rights.dao.ShortUrlMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Api(description = "获取长连接")
@RestController
@RequestMapping("/c")
public class ShortUrlController {

    @Autowired
    private ShortUrlMapper shortUrlMapper;

    @ApiOperation(value = "返回长连接")
    @GetMapping("/s")
    @CrossOrigin
    public String getTradeDetail(@RequestParam @ApiParam String shortUrl) {
        if(!StringUtils.isEmpty(shortUrl)){
            String originUrl = shortUrlMapper.getUrl(shortUrl);
            if(originUrl!=null){
                return "redirect:" + originUrl;
            }
        }
        return "";
    }
}
