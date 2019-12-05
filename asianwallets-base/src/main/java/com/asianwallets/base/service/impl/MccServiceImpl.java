package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.MccMapper;
import com.asianwallets.base.service.MccService;
import com.asianwallets.common.entity.Mcc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * mcc
 */

@Service
@Transactional
public class MccServiceImpl implements MccService {

    @Autowired
    private MccMapper mccMapper;


    /**
     * 查询所有Mcc
     *
     * @return
     */
    @Override
    public List<Mcc> inquireAllMcc() {
        return mccMapper.selectAll();
    }
}
