package com.asianwallets.rights.service;
import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.entity.RightsOrders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.RightsOrdersApiVO;
import com.asianwallets.common.vo.RightsOrdersVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yx
 * @since 2019-12-30
 */
public interface RightsOrdersService extends BaseService<RightsOrders> {

    /**
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 权益核销
     * @return
     **/
    BaseResponse verificationCancel(VerificationCancleDTO verificationCancleDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 权益核销分页查询
     * @return
     **/
    PageInfo<RightsOrdersVO> pageRightsOrders(RightsOrdersDTO rightsOrdersDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/30
     * @Descripate 导出权益核销
     * @return
     **/
    List<RightsOrdersVO> exportRightsOrders(RightsOrdersExportDTO rightsOrdersDTO);


    /**
     * @Author YangXu
     * @Date 2019/12/31
     * @Descripate
     * @return
     **/
    List<RightsOrdersApiVO> selectRightsOrders(RightsOrdersOutDTO rightsOrdersDTO);

    /**
     * @Author YangXu
     * @Date 2020/1/2
     * @Descripate 权益退款(发放平台)API
     * @return
     **/
    BaseResponse sysRightsRefund(RightsRefundDTO rightsRefundDTO);


    /**
     * @return
     * @Author YangXu
     * @Date 2020/1/2
     * @Descripate 权益退款(机构)API
     **/
    BaseResponse insRightsRefund(RightsRefundDTO rightsRefundDTO);

    /**
     * 查询核销详情
     *
     * @param rightsOrdersDTO
     */
    RightsOrders getRightsOrdersInfo(RightsOrdersDTO rightsOrdersDTO);
}
