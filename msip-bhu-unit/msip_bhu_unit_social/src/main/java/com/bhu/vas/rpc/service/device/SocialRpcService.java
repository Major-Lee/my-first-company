package com.bhu.vas.rpc.service.device;

import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.rpc.facade.SocialFacadeRpcService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 3/2/16.
 */
@Service("socialRpcService")
public class SocialRpcService implements ISocialRpcService {

    @Resource
    private SocialFacadeRpcService socialFacadeRpcService;

    @Override
    public boolean comment(long uid, String bssid, String message) {
        socialFacadeRpcService.comment(uid, bssid, message);
        return false;
    }
}
