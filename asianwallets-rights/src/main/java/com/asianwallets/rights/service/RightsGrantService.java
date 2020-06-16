package com.asianwallets.rights.service;
import com.alibaba.fastjson.JSONObject;
import com.asianwallets.common.dto.RightsGrantDTO;
import com.asianwallets.common.dto.RightsGrantInsertDTO;
import com.asianwallets.common.dto.SendReceiptDTO;
import com.asianwallets.common.entity.RightsGrant;
import com.asianwallets.common.entity.RightsUserGrant;
import com.asianwallets.common.vo.ExportRightsGrantVO;
import com.asianwallets.common.vo.ExportRightsUserGrantVO;
import com.asianwallets.common.vo.RightsUserGrantDetailVO;
import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * 权益发放管理模块业务层
 */
public interface RightsGrantService {

    /**
     * 分页查询权益发放管理信息
     *
     * @param rightsGrantDTO
     * @return
     */
    PageInfo<RightsGrant> pageFindRightsGrant(RightsGrantDTO rightsGrantDTO);

    /**
     * 查询权益发放管理信息详情
     * @param rightsGrantDTO
     * @return
     */
    RightsGrant selectRightsGrantInfo(RightsGrantDTO rightsGrantDTO);

    /**
     * 导出权益发放管理信息
     *
     * @param rightsGrantDTO
     * @return
     */
    List<ExportRightsGrantVO> exportRightsGrants(RightsGrantDTO rightsGrantDTO);

    /**
     * 新增权益发放管理信息
     *
     * @param username
     * @param rightsGrantInsertDTO
     * @return
     */
    int addRightsGrant(String username, RightsGrantInsertDTO rightsGrantInsertDTO);

    /**
     * 发券接口【对外API】
     *
     * @param sendReceiptDTO 发券DTO
     * @return
     */
    JSONObject sendReceipt(SendReceiptDTO sendReceiptDTO);

    /**
     * 分页查询权益票券信息
     *
     * @param rightsGrantDTO 输入DTO
     * @return
     */
    PageInfo<RightsUserGrant> pageFindRightsUserGrant(RightsGrantDTO rightsGrantDTO);

    /**
     * 查询权益票券详情
     *
     * @param ticketId 票券编号
     * @return
     */
    RightsUserGrantDetailVO getRightsUserGrantDetail(String ticketId);

    /**
     * 导出权益票券信息
     *
     * @param rightsGrantDTO 输入DTO
     * @return
     */
    List<ExportRightsUserGrantVO> exportRightsUserGrant(RightsGrantDTO rightsGrantDTO);
}
