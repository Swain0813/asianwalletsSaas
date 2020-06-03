package com.asianwallets.base.dao;
import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.SearchAccountCheckDTO;
import com.asianwallets.common.entity.CheckAccountLog;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CheckAccountLogMapper extends BaseMapper<CheckAccountLog> {

    /**
     * 分页查询对账管理
     * @param searchAccountCheckDTO
     * @return
     */
    List<CheckAccountLog> pageAccountCheckLog(SearchAccountCheckDTO searchAccountCheckDTO);
}