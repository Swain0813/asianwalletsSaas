package com.asianwallets.clearing.service;

import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import com.asianwallets.common.response.BaseResponse;

public interface TCSFrozenFundsService {

    BaseResponse frozenFundsLogs(TcsFrozenFundsLogs ffl);
}
