package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.rpc.facade.VapFacadeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 5/26/15.
 */
@Service("vapRpcService")
public class VapRpcService  implements IVapRpcService{
    private final Logger logger = LoggerFactory.getLogger(VapRpcService.class);

    @Resource
    private VapFacadeService vapFacadeService;

    @Override
    public void urlView(String key, String field) {
        logger.info(String.format("checkAcc with key[%s] field[%s]", key, field));
        vapFacadeService.urlView(key.toLowerCase(), field);
    }


}
