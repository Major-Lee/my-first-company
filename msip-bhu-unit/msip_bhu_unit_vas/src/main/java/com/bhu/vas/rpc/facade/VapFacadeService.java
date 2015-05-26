package com.bhu.vas.rpc.facade;

import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.VapModeHashService;
import org.springframework.stereotype.Service;

/**
 * Created by bluesand on 5/26/15.
 */
@Service
public class VapFacadeService {

    public void urlView(String key, String field) {
        VapModeHashService.getInstance().incrStatistics(key, field, 1);
    }
}
