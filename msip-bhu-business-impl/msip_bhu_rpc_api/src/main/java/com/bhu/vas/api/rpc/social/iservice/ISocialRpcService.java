package com.bhu.vas.api.rpc.social.iservice;


import java.util.List;
import java.util.Set;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.social.vto.CommentedWifiVTO;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.bhu.vas.api.rpc.social.vto.WifiUserHandsetVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


/**
 * Created by bluesand on 3/2/16.
 */
public interface ISocialRpcService {
    /**
     * 终端扫描后相遇
     * @param uid
     * @param hd_mac
     * @param hd_macs
     * @param bssid
     * @param ssid
     * @param lat
     * @param lon
     */
    boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat, String lon);


    /**
     * 获取终端列表
     * @param bssid
     * @param hd_macs
     */
    WifiUserHandsetVTO fetchHandsetList(String bssid, String hd_macs);


    /**
     * 获取终端详情
     * @param hd_mac
     */
    void fetchHandsetDetail(String hd_mac);

    /**
     * 修改终端备注
     * @param hd_mac
     * @param nick
     */
    void modifyHandset(Long uid, String hd_mac, String nick);

    /**
     * 获取wifi详情
     * @param uid
     * @param bssid
     */
    void fetchWifiDetail(Long uid, String bssid);

    /**
     * 评论
     * @param uid
     * @param bssid
     * @param message
     * @return
     */

    public boolean comment(long uid, String bssid,String hd_mac, String message);
    
    /**
     * 查看wifi评论列表
     * @param uid
     * @param bssid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public RpcResponseDTO<TailPage<WifiCommentVTO>> pageWifiCommentVTO(int uid,String bssid, int pageNo, int pageSize);

    
    /**
     * 点赞/踩/举报
     * @param uid
     * @param bssid
     * @param type up/down/type
     * @return
     */
    public RpcResponseDTO<Boolean> clickPraise(long uid, String bssid,String type);
    
    /**
     * 关注
     * @param uid
     * @param hd_mac
     */
    public RpcResponseDTO<Boolean> follow (long uid,String hd_mac);
    	
    /**
     * 取消关注
     * @param uid
     * @param hd_mac
     * @return 
     */

    public RpcResponseDTO<Boolean>  unFollow (long uid,String hd_mac);

    /**
     * 获取关注列表
     * @param uid
     * @param hd_mac
     */
    public Set<String> fetchFollowList(long uid,String hd_mac);

    /**
     * 获取用户评论的wifi
     * @param uid
     * @return
     */
    public RpcResponseDTO<List<CommentedWifiVTO>> fetchUserCommentWifiList(String uid,String hd_mac);



}
