package com.asianwallets.base.service.impl;
import com.asianwallets.base.dao.PreOrdersMapper;
import com.asianwallets.base.service.PreOrdersService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.PreOrdersDTO;
import com.asianwallets.common.entity.PreOrders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.ExportPreOrdersVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@Slf4j
public class PreOrdersServiceImpl implements PreOrdersService {

    @Autowired
    private PreOrdersMapper preOrdersMapper;


    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * 分页查询预授权订单信息
     * @param preOrdersDTO
     * @return
     */
    @Override
    public PageInfo<PreOrders> pageFindPreOrders(PreOrdersDTO preOrdersDTO) {
        preOrdersDTO.setLanguage(auditorProvider.getLanguage());
        return new PageInfo<>(preOrdersMapper.pageFindPreOrders(preOrdersDTO));
    }

    /**
     * 查询预授权订单详情信息
     * @param preOrdersDTO
     * @return
     */
    @Override
    public PreOrders getPreOrdersDetail(PreOrdersDTO preOrdersDTO) {
        //id不能为空
        if(StringUtils.isEmpty(preOrdersDTO.getId())){
            throw new BusinessException(EResultEnum.NOTICE_ID_IS_NOT_NULL.getCode());
        }
        PreOrders preOrders = preOrdersMapper.getPreOrdersDetail(preOrdersDTO);
        return preOrders;
    }

    /**
     * 分页查询预授权订单信息
     * @param preOrdersDTO
     * @return
     */
    @Override
    public List<ExportPreOrdersVO> exportPreOrders(PreOrdersDTO preOrdersDTO) {
        preOrdersDTO.setLanguage(auditorProvider.getLanguage());
        return preOrdersMapper.exportPreOrders(preOrdersDTO);
    }

}
