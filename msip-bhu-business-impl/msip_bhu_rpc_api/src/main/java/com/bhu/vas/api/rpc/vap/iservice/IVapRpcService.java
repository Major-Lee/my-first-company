package com.bhu.vas.api.rpc.vap.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;

/**
 * Created by bluesand on 5/26/15.
 */
public interface IVapRpcService {
    RpcResponseDTO<VapModeUrlViewCountDTO> urlView(String key, String field);
}
