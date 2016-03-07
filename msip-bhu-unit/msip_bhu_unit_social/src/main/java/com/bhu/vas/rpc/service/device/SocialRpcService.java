package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.CommentedWifiVTO;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.bhu.vas.api.rpc.social.vto.WifiUserHandsetVTO;
import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.rpc.facade.SocialFacadeRpcService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 3/2/16.
 */
@Service("socialRpcService")
public class SocialRpcService implements ISocialRpcService {

    private final Logger logger = LoggerFactory
	    .getLogger(SocialRpcService.class);

    @Resource
    private SocialFacadeRpcService socialFacadeRpcService;

    @Override
    public boolean comment(long uid,String bssid,String hd_mac, String message) {
	try {
	    socialFacadeRpcService.comment(uid, bssid,hd_mac, message);
	    return true;
	} catch (Exception ex) {
	    return false;
	}
    }

    @Override
    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs,
	    String bssid, String ssid, String lat, String lon) {
//	logger.info(String.format(
//		"handsetMeet uid[%s] hd_mac[%s] hd_macs[%s] bssid[%s], ssid[%s] lat[%] lon[%s]",
//		uid, hd_mac, hd_macs, bssid, ssid, lat, lon));
	return socialFacadeRpcService.handsetMeet(uid, hd_mac, hd_macs, bssid,
		ssid, lat, lon);
    }

    @Override
    public WifiUserHandsetVTO fetchHandsetList(String bssid, String hd_macs) {
	    logger.info(String.format("fetchHandsetList bssid[%s] hd_macs[%s] ", bssid, hd_macs));
        return socialFacadeRpcService.fetchHandsetList(bssid, hd_macs);
    }

    @Override
    public void fetchHandsetDetail(String hd_mac) {
	logger.info(String.format("fetchHandsetDetail hd_mac[%s]", hd_mac));
    }

    @Override
    public void modifyHandset(Long uid, String hd_mac, String nick) {
	logger.info(String.format("modifyHandset uid[%s] hd_mac[%s] nick[%s]",
		uid, hd_mac, nick));
    }

    @Override
    public void fetchWifiDetail(Long uid, String bssid) {
	logger.info(
		String.format("handsetMeet uid[%s]  bssid[%s] ", uid, bssid));
    }

    @Override
    public RpcResponseDTO<WifiActionVTO> clickPraise(long uid, String bssid, String type) {
	logger.info(String.format("clickPraise uid[%s]  bssid[%s] type[%s]",
		uid, bssid, type));
	return socialFacadeRpcService.clickPraise(bssid, type);
    }

    @Override
    public RpcResponseDTO<TailPage<WifiCommentVTO>> pageWifiCommentVTO(int uid,
	    String bssid, int pageNo, int pageSize) {
	logger.info(String.format("pageWifiCommentVTO pageNo[%s] pageSize",
		pageNo, pageSize));
	return RpcResponseDTOBuilder
		.builderSuccessRpcResponse(socialFacadeRpcService
			.pageWifiCommentVTO(uid, bssid, pageNo, pageSize));

    }

    @Override
    public RpcResponseDTO<Boolean> follow(long uid, String hd_mac) {
	logger.info(String.format("follow uid[%s] hd_mac[%s]", uid,
		hd_mac));
	return socialFacadeRpcService.follow(uid, hd_mac);
    }

    @Override
    public RpcResponseDTO<Boolean> unFollow(long uid, String hd_mac) {
	logger.info(String.format("unFollow uid[%s] hd_mac[%s]", uid, hd_mac));
	return socialFacadeRpcService.unFollow(uid, hd_mac);
    }
    
	public RpcResponseDTO<List<CommentedWifiVTO>> fetchUserCommentWifiList(String uid,String hd_mac) {

		return RpcResponseDTOBuilder
				.builderSuccessRpcResponse(socialFacadeRpcService.fetchUserCommentWifiList(uid,hd_mac));

	}
}
