package com.asianwallets.base.service;

import com.asianwallets.common.entity.Mcc;

import java.util.List;

/**
 * mcc
 */
public interface MccService {

    /**
     * 查询所有Mcc
     *
     * @return
     */
    List<Mcc> inquireAllMcc();
}
