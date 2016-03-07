package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.social.WifiActionDTO;
import com.bhu.vas.api.rpc.social.model.Wifi;
import com.bhu.vas.api.rpc.social.vto.*;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.*;
import com.bhu.vas.business.ds.social.service.WifiService;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.social.SocialHandsetMeetDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.model.HandsetUser;
import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.api.rpc.social.vto.UserHandsetVTO;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.bhu.vas.api.rpc.social.vto.WifiUserHandsetVTO;
import com.bhu.vas.api.rpc.social.vto.CommentedWifiVTO;
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
     * @param bssid
     * @param type
     * @return
     */
    public RpcResponseDTO<Boolean> clickPraise(String bssid, String type) {

        if (WifiActionHashService.getInstance().isNoExist(bssid)) {
            WifiActionHashService.getInstance().hadd(bssid);
        }

        WifiActionHashService.getInstance().hincrease(bssid, type);
        WifiActionHashService.getInstance().counts(bssid);
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }

    /**
     * 关注
     * @param uid
     * @param hd_mac
     * @return
     */
    public RpcResponseDTO<Boolean> follow(long uid, String hd_mac) {
        SocialFollowSortedSetService.getInstance().follow(uid, hd_mac);
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }

    /**
     * 取消关注
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
     * @param uid
     * @param hd_mac
     */
    public Set<String> fetchFollowList(long uid,String hd_mac){
        return SocialFollowSortedSetService.getInstance().fetchFollowList(uid);
    }

    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat, String lon) {

        if (uid != null || uid > 0) {
            HandsetUser handsetUser = new HandsetUser();
            handsetUser.setId(hd_mac);
            handsetUser.setUid(uid);
            handsetUser.setCreated_at(new Date());
            handsetUserService.insert(handsetUser);

            WifiSortedSetService.getInstance().addWifiVistor(bssid, uid);
        }

        SocialHandsetMeetDTO dto = new SocialHandsetMeetDTO();
        dto.setBssid(bssid);
        dto.setSsid(ssid);
        dto.setTs(System.currentTimeMillis());
        dto.setLat(lat);
        dto.setLon(lon);

        String[] list = hd_macs.split(",");
        if (list != null && list.length > 0) {
            for (String mac : list) {
                //TODO:(bluesand): backend操作
                SocialHandsetMeetHashService.getInstance().handsetMeet(hd_mac, mac, bssid, JsonHelper.getJSONString(dto));
            }
        }
        return false;
    }


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


    public WifiUserHandsetVTO fetchHandsetList(String bssid, String hd_macs) {
        WifiUserHandsetVTO vto = new WifiUserHandsetVTO();

        List<UserHandsetVTO> hdVTOs = new ArrayList<UserHandsetVTO>();
        List<String> hds = new ArrayList<String>();
        String[] list = hd_macs.split(",");
        if (list != null && list.length > 0) {
            for (String hd_mac : list) {
                UserHandsetVTO handsetVTO = new UserHandsetVTO();
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
                    UserHandsetVTO hdVTO = hdVTOs.get(index);
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
        for (User user: users) {
            if (user != null) {
                UserHandsetVTO hdVTO = hdVTOs.get(index);
                hdVTO.setAvatar(user.getAvatar());
                if (StringUtils.isEmpty(user.getNick())) {
                    hdVTO.setNick(user.getNick());
                }
            }
        }



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
                vto.setNick(user.getNick());
                vto.setAvatar(user.getAvatar());
                // }
                vtos.add(vto);
                i++;
            }
        }
        return new CommonPage<WifiCommentVTO>(pageNo, pageSize, total, vtos);
    }

 
    public List<CommentedWifiVTO> fetchUserCommentWifiList(String uid,String hd_mac){
    	
    	Set<String>wifiSet= WifiCommentSortedSetService.getInstance().fetchUserWifiList(uid);
    	List<String> bssidList = new ArrayList<String>(wifiSet);
    	List<Wifi> wifiList=wifiService.findByIds(bssidList);
    	List<CommentedWifiVTO> vtos = new ArrayList<CommentedWifiVTO>();
    	if(wifiList != null){
    		CommentedWifiVTO commentedWifiVTO=null;
    	for(Wifi wifi:wifiList){
    		commentedWifiVTO=new CommentedWifiVTO();
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

