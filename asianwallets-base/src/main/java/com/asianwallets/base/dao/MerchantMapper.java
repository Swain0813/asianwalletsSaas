package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.MerchantDTO;
import com.asianwallets.common.entity.Merchant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Repository
public interface MerchantMapper extends BaseMapper<Merchant> {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 分页查询商户信息列表
     **/
    List<Merchant> pageFindMerchant(MerchantDTO merchantDTO);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据商户Id查询商户信息详情
     **/
    Merchant getMerchantInfo(@Param("id") String id, @Param("language") String language);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/11/25
     * @Descripate 根据用户名查询名称是否存在
     **/
    @Select("select count(1) from merchant where cn_name = #{cnName} or en_name = #{cnName}")
    int selectCountByInsName(@Param("cnName") String cnName);


    /**
     * @Author YangXu
     * @Date 2019/11/28
     * @Descripate 代理商下拉框
     * @return
     **/
    List<Merchant> getAllAgent(@Param("merchantType") String merchantType,@Param("agentType")String agentType,@Param("institutionCode")String institutionCode);

   /**
    * @Author YangXu
    * @Date 2019/11/28
    * @Descripate 导出商户
    * @return
    **/
   List<Merchant> exportMerchant(MerchantDTO merchantDTO);

    /**
     * 根据商户编号获取商户信息
     *
     * @param code
     * @return
     */
    Merchant getMerchant(@Param("code") String code);

    /**
     * 通过集团商户号查询子商户
     *
     * @param merchantId
     * @return
     */
    List<String> selectByGroupMasterAccount(@Param("merchantId") String merchantId);

    /**
     * 商户报备时查询商户信息方法
     * @param merchantId
     * @return
     */
    Merchant getMerchantReportInfo(@Param("merchantId") String merchantId,@Param("channelCode") String channelCode);
}
