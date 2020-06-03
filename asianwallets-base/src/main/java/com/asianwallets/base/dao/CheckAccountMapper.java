package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.entity.CheckAccount;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

/**
 * 对账表
 */
@Repository
public interface CheckAccountMapper extends BaseMapper<CheckAccount> {


    /**
     * @Author YangXu
     * @Date 2019/3/29
     * @Descripate 查询所有待对账的记录
     * @return
     **/
    List<CheckAccount> getDataByType(@Param("errorType") int errorType, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 分页查询对账管理详情
     * @param searchAccountCheckDTO
     * @return
     */
    List<CheckAccount> pageAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * ad3通道对账
     *
     * @param searchAccountCheckDTO 导出对账管理详情
     * @return
     */
    List<CheckAccount> exportAccountCheck(SearchAccountCheckDTO searchAccountCheckDTO);

    /**
     * @Author YangXu
     * @Date 2019/4/10
     * @Descripate 系统补单更新状态
     * @return
     **/
    @Update("update check_account set error_type = 4 ,u_status = 3 ,remark1 = #{remark},update_time=NOW() where u_order_id = #{orderId} and error_type = 3")
    int upateErrorType(@Param("orderId") String orderId, @Param("remark") String remark);

    /**
     * @Author YangXu
     * @Date 2019/4/10
     * @Descripate 查询通道流水号
     * @return
     **/
    @Select("select c_channel_number from check_account where c_order_id = #{orderId}")
    String selectByOrderId(@Param("orderId") String orderId);

    /**
     * 查询差错处理的记录数
     * @param date
     * @return
     */
    @Select("select count(1) from check_account where error_type = 2 and date_format(#{date}, '%Y-%c-%d' ) = date_format(create_time, '%Y-%c-%d')")
    int getErrorCount(Date date);
}