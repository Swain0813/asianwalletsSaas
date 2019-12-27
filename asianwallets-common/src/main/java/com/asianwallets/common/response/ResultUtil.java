package com.asianwallets.common.response;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: 返回值工具类
 * @createTime 2018年7月2日 下午4:12:38
 * @copyright: 上海众哈网络技术有限公司
 */
@Slf4j
public class ResultUtil {

    /**
     * @param object
     * @methodDesc: 功能描述: 操作成功返回信息
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月2日 下午4:13:40
     * @version v1.0.0
     */
    public static BaseResponse success(Object object) {
        BaseResponse response = new BaseResponse();
        response.setCode(EResultEnum.SUCCESS.getCode());
        response.setMsg("SUCCESS");
        response.setData(object);
        return response;
    }

    public static BaseResponse success(String code, String msg) {
        BaseResponse response = new BaseResponse();
        response.setCode(code);
        response.setMsg(msg);
        if (code.equals("200")) {response.setMsg("SUCCESS"); } ;
        return response;
    }

    /**
     * @methodDesc: 功能描述: 操作成功不返回消息
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月2日 下午4:13:40
     * @version v1.0.0
     */
    public static BaseResponse success() {
        return ResultUtil.success(null);
    }

    /**
     * @param code
     * @param msg
     * @methodDesc: 功能描述: 操作失败返回的消息
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月2日 下午4:14:42
     * @version v1.0.0
     */
    public static BaseResponse error(String code, String msg) {
        BaseResponse response = new BaseResponse();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }

    public static BaseResponse error(String code, String msg, Object object) {
        BaseResponse response = new BaseResponse();
        response.setCode(code);
        response.setMsg(msg);
        response.setData(object);
        return response;
    }

    /**
     * @param resultEnum
     * @methodDesc: 功能描述: 操作失败返回消息，对error的重载
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月2日 下午4:15:01
     * @version v1.0.0
     */
    //    public static BaseResponse error(EResultEnum resultEnum) {
    //        BaseResponse response = new BaseResponse();
    //        response.setCode(resultEnum.getCode());
    //        response.setMsg(resultEnum.getMsg());
    //        return response;
    //    }

    /**
     * @param response
     * @throws IOException
     * @methodDesc: 功能描述: 包装返回对象
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月8日 下午5:47:11
     * @version v1.0.0
     */
    //    public static void unauthorizedResponse(ServletResponse response) {
    //        PrintWriter writer = null;
    //        OutputStreamWriter osw = null;
    //        try {
    //            osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
    //            writer = new PrintWriter(osw, true);
    //            String jsonStr = JSON.toJSONString(ResultUtil.error(EResultEnum.UNAUTHORIZED));
    //            writer.write(jsonStr);
    //            writer.flush();
    //            writer.close();
    //            osw.close();
    //        } catch (IOException e) {
    ////            log.error("过滤器返回信息失败:" + e.getMessage(), e);
    //        } finally {
    //            if (null != writer) {
    //                writer.close();
    //            }
    //            if (null != osw) {
    //                try {
    //                    osw.close();
    //                } catch (IOException e) {
    ////                    log.error("过滤器返回信息失败:" + e.getMessage(), e);
    //                }
    //            }
    //        }
    //    }
}
