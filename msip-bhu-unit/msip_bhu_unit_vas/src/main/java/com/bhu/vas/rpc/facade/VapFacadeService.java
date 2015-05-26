package com.bhu.vas.rpc.facade;

import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.VapModeHashService;

/**
 * Created by bluesand on 5/26/15.
 */
public class VapFacadeService {

    public void urlView(String key, String field) {
        VapModeHashService.getInstance().incrStatistics(key, field, 1);
    }
}
