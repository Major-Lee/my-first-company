package com.bhu.vas.api.rpc.social.iservice;

import java.util.Map;

import com.bhu.vas.api.vto.WifiActionVTO;

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
    void fetchHandsetList(String bssid, String hd_macs, int pageNo, int pageSize);


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
    boolean comment(long uid, String bssid, String message);
    
    /**
     * 点赞/踩/举报
     * @param uid
     * @param bssid
     * @param type up/down/type
     * @return
     */
    WifiActionVTO clickPraise(long uid, String bssid,String type);
}
