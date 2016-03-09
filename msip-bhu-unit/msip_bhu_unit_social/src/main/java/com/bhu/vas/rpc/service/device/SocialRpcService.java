package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.HandsetUserDetailVTO;
import com.bhu.vas.api.rpc.social.vto.CommentedWifiVTO;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.bhu.vas.api.rpc.social.vto.WifiVTO;
import com.bhu.vas.api.rpc.social.vto.WifiHandsetUserVTO;
import com.bhu.vas.api.vto.SocialFetchFollowListVTO;
import com.bhu.vas.rpc.facade.SocialFacadeRpcService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by bluesand on 3/2/16.
 */
@Service("socialRpcService")
public class SocialRpcService implements ISocialRpcService {

    private final Logger logger = LoggerFactory.getLogger(SocialRpcService.class);

    @Resource
    private SocialFacadeRpcService socialFacadeRpcService;

    @Override
    public boolean comment(long uid, String bssid, String hd_mac, String message) {
        try {
            socialFacadeRpcService.comment(uid, bssid, hd_mac, message);
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
    public WifiHandsetUserVTO fetchHandsetList(Long uid, String bssid, String hd_macs) {
	    logger.info(String.format("fetchHandsetList uid[%s] bssid[%s] hd_macs[%s] ", uid, bssid, hd_macs));
        return socialFacadeRpcService.fetchHandsetList(uid, bssid, hd_macs);
    }

    @Override
    public HandsetUserDetailVTO fetchHandsetDetail(Long uid, String hd_mac_self, String hd_mac, String bssid) {
        logger.info(String.format("fetchHandsetDetail uid[%s], hd_mac_self[%s], hd_mac[%s] bssid[%s]",
                uid, hd_mac_self, hd_mac, bssid));
        return socialFacadeRpcService.fetchHandsetUserDetai(uid, hd_mac_self, hd_mac, bssid);
    }

    @Override
    public boolean modifyHandset(long uid, String hd_mac, String nick) {
        logger.info(String.format("modifyHandset uid[%s] hd_mac[%s] nick[%s]",
                uid, hd_mac, nick));
        return socialFacadeRpcService.modifyHandset(uid, hd_mac, nick);

    }

    @Override
    public WifiVTO fetchWifiDetail(Long uid, String bssid) {
        logger.info(
                String.format("handsetMeet uid[%s]  bssid[%s] ", uid, bssid));
        return socialFacadeRpcService.fetchWifiDetail(uid, bssid);
    }

    @Override
    public boolean modifyWifi(Long uid, String bssid, String rate) {
        logger.info(String.format("modifyWifi uid[%s]  bssid[%s] rate[%s]", uid, bssid, rate));
        return socialFacadeRpcService.modifyWifi(uid, bssid, rate);
    }

    @Override
    public RpcResponseDTO<Boolean> clickPraise(long uid, String bssid, String type) {
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

    @Override
    public RpcResponseDTO<TailPage<SocialFetchFollowListVTO>> fetchFollowList(long uid, String hd_mac,int pageNo,int pageSize) {
        logger.info(String.format("fetchFollowList uid[%s] hd_mac[%s] pn[%s] ps[%s]", uid, hd_mac,pageNo,pageSize));
        return RpcResponseDTOBuilder
                .builderSuccessRpcResponse(socialFacadeRpcService
                        .fetchFollowList(uid, hd_mac,pageNo,pageSize));
    }


    public RpcResponseDTO<List<CommentedWifiVTO>> fetchUserCommentWifiList(String uid, String hd_mac) {
        return RpcResponseDTOBuilder
                .builderSuccessRpcResponse(socialFacadeRpcService.fetchUserCommentWifiList(uid, hd_mac));

    }
}
