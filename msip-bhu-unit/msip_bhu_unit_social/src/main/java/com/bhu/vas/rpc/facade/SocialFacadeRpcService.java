package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.social.WifiActionDTO;
import com.bhu.vas.api.rpc.social.model.Wifi;
import com.bhu.vas.api.rpc.social.vto.*;
import com.bhu.vas.business.asyn.spring.activemq.service.SocialMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.*;
import com.bhu.vas.business.ds.social.service.WifiService;
import com.bhu.vas.api.vto.SocialFetchFollowListVTO;
import com.smartwork.msip.jdo.ResponseErrorCode;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.social.HandsetMeetDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.model.HandsetUser;
import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.social.service.HandsetUserService;
import com.bhu.vas.business.ds.social.service.WifiCommentService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.util.StringUtils;

/**
 * Created by bluesand on 3/2/16.
 */
@Service
public class SocialFacadeRpcService {

    @Resource
    private WifiCommentService wifiCommentService;

    @Resource
    private HandsetUserService handsetUserService;

    @Resource
    private UserService userService;

    @Resource
    private WifiService wifiService;

    @Resource
    private SocialMessageService socialMessageService;

    public WifiComment comment(long uid, String bssid, String hd_mac, String message) {

        WifiComment wifiComment = new WifiComment();
        wifiComment.setUid(uid);
        wifiComment.setMessage(message);
        wifiComment.setBssid(bssid);
        wifiComment.setHd_mac(hd_mac);
        Date currentDate = new Date();
        double currentTime = currentDate.getTime();
        wifiComment.setCreated_at(currentDate);
        WifiCommentSortedSetService.getInstance().addUserWifi(Long.toString(uid), currentTime, bssid);
        return wifiCommentService.insert(wifiComment);
    }

    /**
     * 点赞/踩/举报
     *
     * @param bssid
     * @param type
     * @return
     */
    public RpcResponseDTO<Boolean> clickPraise(String bssid, String type) {
        WifiActionHashService wifiActionHashService = WifiActionHashService.getInstance();
        if (wifiActionHashService.isNoExist(bssid)) {
            wifiActionHashService.hadd(bssid);
        }

        wifiActionHashService.hincrease(bssid, type);
        wifiActionHashService.counts(bssid);
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }

    /**
     * 关注
     *
     * @param uid
     * @param hd_mac
     * @return
     */
    public RpcResponseDTO<Boolean> follow(long uid, String hd_mac) {

        HandsetUser handsetUser = handsetUserService.getById(hd_mac);
        if (handsetUser != null && handsetUser.getUid() != uid) {
            long index = SocialFollowSortedSetService.getInstance().follow(uid, hd_mac);
            if (index == 1){
                return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
            }else{
                return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.SOCIAL_FOLLOW_ERROR);
            }
        } else {
            return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.SOCIAL_FOLLOW_ERROR);
        }
    }

    /**
     * 取消关注
     *
     * @param uid
     * @param hd_mac
     * @return
     */
    public RpcResponseDTO<Boolean> unFollow(long uid, String hd_mac) {

        SocialFollowSortedSetService.getInstance().unFollow(uid, hd_mac);
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }

    /**
     * 获取关注列表
     *
     * @param uid
     * @param hd_mac_self
     * @return
     */
    public TailPage<SocialFetchFollowListVTO> fetchFollowList(long uid, String hd_mac_self, int pageNo, int pageSize) {
        Set<String> set = SocialFollowSortedSetService.getInstance().fetchFollowList(uid, pageNo, pageSize);
        int total = set.size();
        List<SocialFetchFollowListVTO> result = new ArrayList<SocialFetchFollowListVTO>();
        List<String> hds = new ArrayList<>();

        if (set != null && set.size() > 0) {
            for (String hd_mac : set) {
                SocialFetchFollowListVTO vto = new SocialFetchFollowListVTO();
                hds.add(hd_mac);
                vto.setHd_mac(hd_mac);
                vto.setLast_meet(SocialHandsetMeetHashService.getInstance().getLasthandsetMeet(hd_mac_self, hd_mac));
                result.add(vto);
            }
        }
        List<HandsetUser> handsetList = handsetUserService.findByIds(hds,true,true);
        List<Integer> ids = new ArrayList<>();

        if (handsetList != null && handsetList.size() >0) {
            int index = 0;
            for (HandsetUser handSerUser : handsetList) {
                SocialFetchFollowListVTO vtos = result.get(index);
                if (handSerUser != null) {
                    vtos.setNick(handSerUser.getNick());
                    vtos.setUid(handSerUser.getUid());
                    ids.add((int) handSerUser.getUid());
                }
                index++;
            }
        }

        List<User> users = userService.findByIds(ids,true,true);

        if (users !=null && users.size() > 0){
            int index = 0;
            for (User user : users){
                SocialFetchFollowListVTO vtos = result.get(index);
                if (vtos != null ){
                    vtos.setAvatar(user.getAvatar());
                    vtos.setType(SocialFetchFollowListVTO.TYPE);
                    vtos.setUid(user.getId());
                    if (StringUtils.isEmpty(vtos.getNick())){
                        vtos.setNick(user.getNick());
                    }
                }
                index++;
            }
        }
        return new CommonPage<SocialFetchFollowListVTO>(pageNo, pageSize, total, result);
    }

    /**
     * 终端遇见
     *
     * @param uid
     * @param hd_mac
     * @param hd_macs
     * @param bssid
     * @param ssid
     * @param lat
     * @param lon
     * @return
     */
    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat, String lon) {

        try {
            if (uid != null && uid > 0) {

                HandsetUser handsetUser = handsetUserService.getById(hd_mac);

                if (handsetUser == null) {
                    handsetUser = new HandsetUser();
                    handsetUser.setId(hd_mac);
                    handsetUser.setUid(uid);
                    handsetUser.setCreated_at(new Date());
                    handsetUserService.insert(handsetUser);
                }

                WifiSortedSetService.getInstance().addWifiVistor(bssid, uid);
            }

            HandsetMeetDTO dto = new HandsetMeetDTO();
            dto.setBssid(bssid);
            dto.setSsid(ssid);
            dto.setTs(System.currentTimeMillis());
            dto.setLat(lat);
            dto.setLon(lon);


            socialMessageService.sendHandsetMeetMessage(hd_mac, hd_macs, bssid, JsonHelper.getJSONString(dto));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 获取wifi详情
     *
     * @param uid
     * @param bssid
     * @return
     */
    public WifiVTO fetchWifiDetail(Long uid, String bssid) {

        Wifi wifi = wifiService.getById(bssid);

        WifiVTO wifiVTO = new WifiVTO();

        wifiVTO.setBssid(bssid);
        wifiVTO.setMax_rate(wifi.getMax_rate());
        wifiVTO.setM(wifi.getManufacturer());
        wifiVTO.setSsid(wifi.getSsid());
        wifiVTO.setLat(wifi.getLat());
        wifiVTO.setLon(wifi.getLon());

        WifiActionDTO action = WifiActionHashService.getInstance().counts(bssid);
        wifiVTO.setAction(action);

        Set<String> visitors = WifiSortedSetService.getInstance().getWifiVistors(bssid);

        List<WifiVisitorVTO> vtos = new ArrayList<WifiVisitorVTO>();

        List<Integer> ids = new ArrayList<>();
        for (String visitor : visitors) {
            ids.add(Integer.parseInt(visitor));
        }

        List<User> users = userService.findByIds(ids);

        for (User user : users) {
            WifiVisitorVTO vto = new WifiVisitorVTO();
            vto.setUid(user.getId());
            vto.setAvatar(user.getAvatar());
            vto.setN(user.getNick());
            vtos.add(vto);
        }

        wifiVTO.setVisitors(vtos);

        return wifiVTO;

    }

    /**
     * 修改wifi信息,最高速率
     *
     * @param uid
     * @param bssid
     * @param rate
     * @return
     */
    public boolean modifyWifi(Long uid, String bssid, String rate) {
        Wifi wifi = wifiService.getById(bssid);
        try {
            String max_rate = wifi.getMax_rate();
            if (Double.parseDouble(rate) > Double.parseDouble(max_rate)) {
                wifi.setMax_rate(rate);
            }
            wifiService.update(wifi);
        } catch (Exception e) {
        }

        return true;
    }


    /**
     * 获取终端列表
     *
     * @param bssid
     * @param hd_macs
     * @return
     */
    public WifiHandsetUserVTO fetchHandsetList(Long uid, String bssid, String hd_macs) {
        WifiHandsetUserVTO vto = new WifiHandsetUserVTO();

        Wifi wifi = wifiService.getById(bssid);

        vto.setBssid(bssid);
        vto.setSsid(wifi.getSsid());

        List<HandsetUserVTO> hdVTOs = new ArrayList<HandsetUserVTO>();
        List<String> hds = new ArrayList<String>();
        String[] list = hd_macs.split(",");
        if (list != null && list.length > 0) {
            for (String hd_mac : list) {
                HandsetUserVTO handsetVTO = new HandsetUserVTO();
                handsetVTO.setHd_mac(hd_mac);
                hds.add(hd_mac);
                hdVTOs.add(handsetVTO);
            }
        }

        List<HandsetUser> handsetUsers = handsetUserService.findByIds(hds, true, true);
        List<Integer> ids = new ArrayList<Integer>();

        int index = 0;
        if (handsetUsers != null) {
            for (HandsetUser handsetUser : handsetUsers) {
                if (handsetUser != null) {
                    HandsetUserVTO hdVTO = hdVTOs.get(index);
                    hdVTO.setUid(handsetUser.getUid());
                    hdVTO.setNick(handsetUser.getNick());
                    //Todo(bluesand): 用户的头像
                    //hdVTO.setAvatar();
                }
                index++;
            }
        }

        List<User> users = userService.findByIds(ids, true, true);

        index = 0;
        for (User user : users) {
            if (user != null) {
                HandsetUserVTO hdVTO = hdVTOs.get(index);
                hdVTO.setAvatar(user.getAvatar());
                if (StringUtils.isEmpty(user.getNick())) {
                    hdVTO.setNick(user.getNick());
                }
                hdVTO.setMemo(user.getMemo());
            }
        }

        vto.setHandsets(hdVTOs);

        return vto;
    }

    /**
     * 修改终端信息
     *
     * @param uid
     * @param hd_mac
     * @param nick
     * @return
     */
    public boolean modifyHandset(long uid, String hd_mac, String nick) {
        HandsetUser handsetUser = handsetUserService.getById(hd_mac);
        handsetUser.setNick(nick);
        return true;
    }

    public HandsetUserDetailVTO fetchHandsetUserDetai(Long uid, String hd_mac_self, String hd_mac, String bssid) {

        List<HandsetMeetDTO> meets = SocialHandsetMeetHashService.getInstance().
                getHandsetMeetList(hd_mac_self, hd_mac, bssid);

        HandsetUserVTO handsetUserVTO = new HandsetUserVTO();
        handsetUserVTO.setHd_mac(hd_mac);

        if (uid != null && uid > 0) {
            HandsetUser handsetUser = handsetUserService.getById(hd_mac);
            if (handsetUser != null) {
                User user = userService.getById((int) handsetUser.getUid());
                if (user != null) {
                    handsetUserVTO.setNick(user.getNick());
                    handsetUserVTO.setAvatar(user.getAvatar());
                    handsetUserVTO.setMemo(user.getMemo());
                    handsetUserVTO.setUid(uid);
                }
            }
        }

        HandsetUserDetailVTO vto = new HandsetUserDetailVTO();

        vto.setHandset(handsetUserVTO);
        vto.setMeets(meets);

        return vto;

    }


    /**
     * 分页获取评论列表
     *
     * @param uid
     * @param bssid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public TailPage<WifiCommentVTO> pageWifiCommentVTO(int uid, String bssid, int pageNo, int pageSize) {

        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("bssid", bssid);
        int total = wifiCommentService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        mc.setOrderByClause(" created_at desc");

        List<WifiComment> wifiComments = wifiCommentService.findModelByCommonCriteria(mc);
        List<Integer> uids = new ArrayList<Integer>();
        for (WifiComment wifiComment : wifiComments) {

            uids.add((int) wifiComment.getUid());
        }
        List<User> users = userService.findByIds(uids);
        List<WifiCommentVTO> vtos = new ArrayList<WifiCommentVTO>();
        if (wifiComments != null) {
            WifiCommentVTO vto = null;
            User user = null;
            int i = 0;
            for (WifiComment wifiComment : wifiComments) {
                vto = new WifiCommentVTO();
                vto.setBssid(wifiComment.getBssid());
                vto.setUid(wifiComment.getUid());
                vto.setMessage(wifiComment.getMessage());
                vto.setCreated_at(DateTimeHelper.formatDate(wifiComment.getCreated_at(), DateTimeHelper.FormatPattern1));
                //vto.setUpdate_at(DateTimeHelper.formatDate(wifiComment.getUpdated_at(),DateTimeHelper.FormatPattern1));
                // if(users.size()>0){
                user = users.get(i);
                if (user.getNick() != null) {
                    vto.setNick(user.getNick());
                } else {
                    vto.setNick("");
                }
                if (user.getAvatar() != null) {
                    vto.setAvatar(user.getAvatar());
                } else {
                    vto.setAvatar("");
                }
                // }
                vtos.add(vto);
                i++;
            }
        }
        return new CommonPage<WifiCommentVTO>(pageNo, pageSize, total, vtos);
    }


    /**
     * 获取用户评论列表
     *
     * @param uid
     * @param hd_mac
     * @return
     */
    public List<CommentedWifiVTO> fetchUserCommentWifiList(String uid, String hd_mac) {

        Set<String> wifiSet = WifiCommentSortedSetService.getInstance().fetchUserWifiList(uid);
        List<String> bssidList = new ArrayList<String>(wifiSet);
        List<Wifi> wifiList = wifiService.findByIds(bssidList);
        List<CommentedWifiVTO> vtos = new ArrayList<CommentedWifiVTO>();
        if (wifiList != null) {
            CommentedWifiVTO commentedWifiVTO = null;
            for (Wifi wifi : wifiList) {
                commentedWifiVTO = new CommentedWifiVTO();
                commentedWifiVTO.setBssid(wifi.getId());
                commentedWifiVTO.setMax_rate(wifi.getMax_rate());
                commentedWifiVTO.setLat(Double.toString(wifi.getLat()));
                commentedWifiVTO.setLon(Double.toString(wifi.getLon()));
                vtos.add(commentedWifiVTO);
            }
        }
        return vtos;

    }

}

