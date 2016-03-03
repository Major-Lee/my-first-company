package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.rpc.facade.SocialFacadeRpcService;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by bluesand on 3/2/16.
 */
@Service("socialRpcService")
public class SocialRpcService implements ISocialRpcService {

    private final Logger logger = LoggerFactory.getLogger(SocialRpcService.class);

    @Resource
    private SocialFacadeRpcService socialFacadeRpcService;

    @Override
    public boolean comment(long uid, String bssid, String message) {
        socialFacadeRpcService.comment(uid, bssid, message);
        return false;
    }

    @Override
    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat, String lon) {
        logger.info(String.format("handsetMeet uid[%s] hd_mac[%s] hd_macs[%s] bssid[%s], ssid[%s] lat[%] lon[%s]",
                uid, hd_mac, hd_macs, bssid, ssid, lat, lon));
        return socialFacadeRpcService.handsetMeet(uid, hd_mac, hd_macs, bssid, ssid, lat, lon);
    }

    @Override
    public void fetchHandsetList(String bssid, String hd_macs, int pageNo, int pageSize) {
        logger.info(String.format("fetchHandsetList bssid[%s] hd_macs[%s] pageNo[%s] pageSize[%s]",
                bssid, hd_macs, pageNo, pageSize));
    }

    @Override
    public void fetchHandsetDetail(String hd_mac) {
        logger.info(String.format("fetchHandsetDetail hd_mac[%s]", hd_mac));
    }

    @Override
    public void modifyHandset(Long uid, String hd_mac, String nick) {
        logger.info(String.format("modifyHandset uid[%s] hd_mac[%s] nick[%s]", uid, hd_mac, nick));
    }

    @Override
    public void fetchWifiDetail(Long uid, String bssid) {
        logger.info(String.format("handsetMeet uid[%s]  bssid[%s] ",uid, bssid));
    }

    @Override
    public WifiActionVTO clickPraise(long uid, String bssid, String type) {
	return socialFacadeRpcService.clickPraise(bssid, type);
    }
}
