package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.social.SocialRemarkDTO;
import com.bhu.vas.api.dto.social.WifiActionDTO;
import com.bhu.vas.api.helper.SocialActionType;
import com.bhu.vas.api.rpc.social.dto.SocialStatisManufatorDTO;
import com.bhu.vas.api.rpc.social.dto.SocialStatisManufatorItemDTO;
import com.bhu.vas.api.rpc.social.model.Wifi;
import com.bhu.vas.api.rpc.social.vto.*;
import com.bhu.vas.business.asyn.spring.activemq.service.SocialMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.*;
import com.bhu.vas.business.ds.social.service.WifiService;
import com.bhu.vas.api.vto.SocialFetchFollowListVTO;
import com.smartwork.msip.exception.BusinessI18nCodeException;
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


    /**
     * 点赞/踩/举报
     * @param bssid
     * @param type
     * @return
     */
    public void clickPraise(String bssid, String type) {

        if (WifiActionHashService.getInstance().isNoExist(bssid)) {
            WifiActionHashService.getInstance().init(bssid);
        }

        if (SocialActionType.isActionType(type)){
            WifiActionHashService.getInstance().hincrease(bssid, type);
        }else
            throw  new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
    }

    /**
     * 关注
     *
     * @param uid
     * @param hd_mac
     * @return
     */
    public void follow(long uid, String hd_mac) {

        HandsetUser handsetUser = handsetUserService.getById(hd_mac);
            if (handsetUser == null || handsetUser.getUid() != uid) {
                if (SocialFollowSortedSetService.getInstance().isFollowMax(uid)){
                    SocialFollowSortedSetService.getInstance().follow(uid, hd_mac);
                }else {
                    throw new BusinessI18nCodeException(ResponseErrorCode.SOCIAL_FOLLOW_MAX);
                }
            }
            else {
                throw  new BusinessI18nCodeException(ResponseErrorCode.SOCIAL_FOLLOW_ERROR);
            }
    }

    /**
     * 取消关注
     *
     * @param uid
     * @param hd_mac
     * @return
     */
    public void unFollow(long uid, String hd_mac) {
        SocialFollowSortedSetService.getInstance().unFollow(uid, hd_mac);
        SocialCareHashService.getInstance().unCare(uid,hd_mac);
    }

    /**
     * 获取关注列表
     *
     * @param uid
     * @return
     */
    public TailPage<SocialFetchFollowListVTO> fetchFollowList(long uid, int pageNo, int pageSize) {
        Set<String> set = SocialFollowSortedSetService.getInstance().fetchFollowList(uid, pageNo, pageSize);
        int total = set.size();
        List<SocialFetchFollowListVTO> result = new ArrayList<SocialFetchFollowListVTO>();
        List<String> hds = new ArrayList<>();

        if (set != null && set.size() > 0) {

            ModelCriteria mc = new ModelCriteria();
            mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("uid", uid);
            List<HandsetUser> list= handsetUserService.findModelByModelCriteria(mc);
            String hd_mac_self = list.get(0).getId();

            for (String hd_mac : set) {
                SocialFetchFollowListVTO vto = new SocialFetchFollowListVTO();
                hds.add(hd_mac);
                vto.setHd_mac(hd_mac);

                HandsetMeetDTO meetDto = SocialStorageFacadeService.getLastHandsetMeet(hd_mac_self, hd_mac);
                vto.setLast_meet(meetDto);
                result.add(vto);
            }
        }
        List<HandsetUser> handsetList = handsetUserService.findByIds(hds,true,true);
        List<Integer> ids = new ArrayList<>();

        if (handsetList != null && handsetList.size() >0) {
            int index = 0;
            for (HandsetUser handSerUser : handsetList) {
                SocialFetchFollowListVTO vtos = result.get(index);

                SocialUserVTO SUser = new SocialUserVTO();
                vtos.setNick(SocialCareFacadeService.getNick(uid,vtos.getHd_mac()));
                if (handSerUser != null) {
                    SUser.setUid(handSerUser.getUid());
                    ids.add((int) handSerUser.getUid());
                }
                vtos.setUser(SUser);
                index++;
            }
        }

        List<User> users = userService.findByIds(ids,true,true);

        if (users !=null && users.size() > 0){
            int index = 0;
            for (User user : users){
                SocialFetchFollowListVTO vtos = result.get(index);
                if (user != null ){
                    vtos.getUser().setAvatar(user.getAvatar());
                    vtos.setType(SocialFetchFollowListVTO.TYPE);
                    vtos.getUser().setNick(user.getNick());
                }
                index++;
            }
        }
        return new CommonPage<SocialFetchFollowListVTO>(pageNo, pageSize, total, result);
    }

    /**
     * 终端遇见
     * 1. 终端遇见现在只计算,当前扫描的终端和其他终端为一次遇见,其他终端之间不算遇见.
     * 2. 构建Wifi实体  (Wifi)
     * 3. 构建终端和用户的关系 (HandsetUser)
     * 4. 异步队列处理终端遇见
     *
     * @param uid
     * @param hd_mac
     * @param hd_macs
     * @param bssid
     * @param ssid
     * @param lat
     * @param lon
     * @param addr
     * @return
     */
    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid,
                               String lat, String lon, String addr) {

        try {

            Wifi wifi = wifiService.getById(bssid);
            if (wifi == null) {
                wifi = new Wifi();
                wifi.setId(bssid);
                wifi.setSsid(ssid);
                wifi.setLat(lat);
                wifi.setLon(lon);
                wifi.setAddr(addr);
                wifi.setCreated_at(new Date());
                wifiService.insert(wifi);
            } else {
                wifi.setSsid(ssid);
                wifi.setLat(lat);
                wifi.setLon(lon);
                wifi.setAddr(addr);
                wifiService.update(wifi);
            }

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
            dto.setAddr(addr);

            socialMessageService.sendHandsetMeetMessage(hd_mac, hd_macs, bssid, JsonHelper.getJSONString(dto));

        } catch (Exception e) {
            e.printStackTrace();
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
    public WifiHandsetUserVTO fetchHandsetList(Long uid, String hd_mac, String bssid, String hd_macs) {


        WifiHandsetUserVTO vto = new WifiHandsetUserVTO();
        Wifi wifi = wifiService.getById(bssid);

        vto.setBssid(bssid);
        vto.setSsid(wifi.getSsid());

        List<HandsetUserVTO> hdVTOs = new ArrayList<HandsetUserVTO>();
        List<String> hds = new ArrayList<String>();
        String[] list = hd_macs.split(",");
        if (list != null && list.length > 0) {
            for (String mac : list) {
                HandsetUserVTO handsetVTO = new HandsetUserVTO();
                handsetVTO.setHd_mac(mac);
                if (uid != null && uid >0) {
                    handsetVTO.setNick(SocialCareFacadeService.getNick(uid, mac));
                }

                handsetVTO.setLast(SocialStorageFacadeService.getLastHandsetMeet(hd_mac, mac));
                hds.add(mac);
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

                    if (uid != null && uid >0) {
                        hdVTO.setFollowed(SocialFollowSortedSetService.getInstance().isFollowed(uid,handsetUser.getId()));
                    }
                    ids.add((int)handsetUser.getUid());
                } else {
                    ids.add(0);
                }
                index++;
            }
        }

        List<User> users = userService.findByIds(ids, true, true);

        index = 0;
        for (User user : users) {
            if (user != null) {
                HandsetUserVTO hdVTO = hdVTOs.get(index);
                SocialUserVTO userVTO = new SocialUserVTO();
                userVTO.setUid((long)user.getId());
                userVTO.setAvatar(user.getAvatar());
                if (StringUtils.isEmpty(user.getNick())) {
                    userVTO.setNick(user.getNick());
                }
                userVTO.setMemo(user.getMemo());
                hdVTO.setUser(userVTO);
            }

            index ++;
        }

        vto.setHandsets(hdVTOs);

        return vto;
    }

    /**
     * 修改终端信息,暂时只修改终端用户昵称
     *
     * 如果用户修改备注,算关注,业务理解.
     *
     * @param uid
     * @param hd_mac
     * @param nick
     * @return
     */
    public boolean modifyHandset(long uid, String hd_mac, String nick) {


        SocialCareFacadeService.moidfyNick(uid, hd_mac, nick);

        this.follow(uid, hd_mac);

        return true;
    }

    public HandsetUserDetailVTO fetchHandsetUserDetail(Long uid, String hd_mac_self, String hd_mac, String bssid) {

        HandsetUserVTO handsetUserVTO = new HandsetUserVTO();

        if (uid != null && uid >0) {
            handsetUserVTO.setNick(SocialCareFacadeService.getNick(uid, hd_mac));
        }
        handsetUserVTO.setHd_mac(hd_mac);
        handsetUserVTO.setLast(SocialStorageFacadeService.getLastHandsetMeet(hd_mac_self, hd_mac));

        if (uid != null && uid > 0) {
            HandsetUser handsetUser = handsetUserService.getById(hd_mac);
            if (handsetUser != null) {
                User user = userService.getById((int) handsetUser.getUid());
                if (user != null) {

                    SocialUserVTO socialUserVTO = new SocialUserVTO();
                    socialUserVTO.setUid((long)user.getId());
                    socialUserVTO.setAvatar(user.getAvatar());
                    socialUserVTO.setMemo(user.getMemo());
                    socialUserVTO.setNick(user.getNick());
                    handsetUserVTO.setUser(socialUserVTO);

                }
            }
        }

        HandsetUserDetailVTO vto = new HandsetUserDetailVTO();

        vto.setHandset(handsetUserVTO);

        List<HandsetMeetDTO> meets = SocialStorageFacadeService.getHandsetMeets(hd_mac_self, hd_mac, bssid);
        vto.setMeets(meets);

        return vto;

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
        wifiVTO.setManu(wifi.getManufacturer());
        wifiVTO.setSsid(wifi.getSsid());
        wifiVTO.setLat(wifi.getLat());
        wifiVTO.setLon(wifi.getLon());
        wifiVTO.setAddr(wifi.getAddr());

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
            vto.setNick(user.getNick());
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
            if (max_rate == null || (Double.parseDouble(rate) > Double.parseDouble(max_rate))) {
                wifi.setMax_rate(rate);
            }
            wifiService.update(wifi);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean statis(Long uid, String bssid, String manu) {

        SocialStatisManufatorDTO dto = JsonHelper.getDTO(manu, SocialStatisManufatorDTO.class);

        Wifi wifi = wifiService.getById(bssid);

        SocialStatisManufatorDTO old = JsonHelper.getDTO(wifi.getManufacturer(), SocialStatisManufatorDTO.class);


        /**
         * 两个for循环,客户端目前只定义10-15个厂商
         */
        if (dto != null && !dto.getItems().isEmpty()) {
            for (SocialStatisManufatorItemDTO item : dto.getItems()) {
                String name  = item.getName().trim();
                int count = item.getCount();
                if (dto != null && !old.getItems().isEmpty()) {
                    for (SocialStatisManufatorItemDTO olditem : old.getItems()) {
                        if (name.equals(olditem.getName())) {
                            olditem.setCount(count + olditem.getCount());
                        } else {
                            olditem.setCount(count);
                        }
                    }

                    wifi.setManufacturer(JsonHelper.getJSONString(old));

                } else {
                    wifi.setManufacturer(JsonHelper.getJSONString(dto));
                }

            }
        }
        wifiService.update(wifi);
        return true;
    }






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
        List<Wifi> wifiList = wifiService.findByIds(bssidList,true,true);
        List<CommentedWifiVTO> vtos = new ArrayList<CommentedWifiVTO>();
        if (wifiList != null) {
        	int i=0;
            CommentedWifiVTO commentedWifiVTO = null;
            for (Wifi wifi : wifiList) {
                commentedWifiVTO = new CommentedWifiVTO();
                commentedWifiVTO.setBssid(bssidList.get(i));
                if(wifi!=null){
                commentedWifiVTO.setMax_rate(wifi.getMax_rate());
                commentedWifiVTO.setLat(wifi.getLat());
                commentedWifiVTO.setLon(wifi.getLon());
                commentedWifiVTO.setSsid(wifi.getSsid());
                commentedWifiVTO.setAddr(wifi.getAddr());
                }
                vtos.add(commentedWifiVTO);
                i++;
            }
        }
        return vtos;

    }

}

