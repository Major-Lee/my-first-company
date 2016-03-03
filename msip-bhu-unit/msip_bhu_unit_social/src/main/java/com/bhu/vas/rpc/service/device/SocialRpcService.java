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

    @Override
    public void handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat_lon) {
        
    }

    @Override
    public void fetchHandsetList(String bssid, String hd_macs, int pageNo, int pageSize) {

    }

    @Override
    public void fetchHandsetDetail(String hd_mac) {

    }

    @Override
    public void modifyHandset(Long uid, String hd_mac, String nick) {

    }

    @Override
    public void fetchWifiDetail(Long uid, String bssid) {

    }
}
