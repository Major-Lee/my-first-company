package com.bhu.vas.rpc.service.device;

<<<<<<< fb6651a0baa25dc69a1d574ef4a29b964bd04ee8
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.rpc.facade.SocialFacadeRpcService;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;
=======
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.bhu.vas.rpc.facade.SocialFacadeRpcService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
>>>>>>> 社交--评论; 社交---wifi评论

/**
 * Created by bluesand on 3/2/16.
 */
@Service("socialRpcService")
public class SocialRpcService implements ISocialRpcService {
	
	 private final Logger logger = LoggerFactory.getLogger(SocialRpcService.class);

    private final Logger logger = LoggerFactory.getLogger(SocialRpcService.class);

    @Resource
    private SocialFacadeRpcService socialFacadeRpcService;

    @Override
    public boolean comment(long uid, String bssid, String message) {
    	try{
    	socialFacadeRpcService.comment(uid, bssid, message);
        return true;
	}catch(Exception ex){		
		return false;
	}
    }

<<<<<<< fb6651a0baa25dc69a1d574ef4a29b964bd04ee8
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
	@Override
	public RpcResponseDTO<TailPage<WifiCommentVTO>> pageWifiCommentVTO(int uid,String bssid, int pageNo, int pageSize) {
		logger.info(String.format("pageWifiCommentVTO pageNo[%s] pageSize", pageNo, pageSize));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(socialFacadeRpcService.pageWifiCommentVTO(uid, bssid, pageNo, pageSize));
		
	}
}
