package com.asianwallets.clearing.service;

/**
 * 自动提款批处理
 */
public interface DrawService {
    /**
     * 按商户分组自动提款批处理
     */
    void DrawForBatch();
}
