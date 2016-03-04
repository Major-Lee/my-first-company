package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.social.SocialHandsetMeetDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.social.model.HandsetUser;
import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.api.rpc.social.vto.UserHandsetVTO;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.bhu.vas.api.rpc.social.vto.WifiUserHandsetVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialFollowSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialHandsetMeetHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.WifiActionHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.WifiCommentSortedSetService;
import com.bhu.vas.business.ds.social.service.HandsetUserService;
import com.bhu.vas.business.ds.social.service.WifiCommentService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;

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

    public WifiComment comment(long uid, String bssid, String message) {

        WifiComment wifiComment = new WifiComment();
        wifiComment.setUid(uid);
        wifiComment.setMessage(message);
        wifiComment.setBssid(bssid);
        Date currentDate=new Date();
        double currentTime=currentDate.getTime();
        wifiComment.setCreated_at(currentDate);
        WifiCommentSortedSetService.getInstance().addUserWifi(Long.toString(uid), currentTime, bssid);
        return wifiCommentService.insert(wifiComment);
    }

    public RpcResponseDTO<WifiActionVTO> clickPraise(String bssid, String type) {

	if (WifiActionHashService.getInstance().isNoExist(bssid)) {
	    WifiActionHashService.getInstance().hadd(bssid);
	}
	
	WifiActionHashService.getInstance().hincrease(bssid, type);
	WifiActionVTO result =  WifiActionHashService.getInstance().counts(bssid);
	return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
    }

    public RpcResponseDTO<Boolean> follow(long uid, String hd_mac) {

	SocialFollowSortedSetService.getInstance().follow(uid, hd_mac);
	return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }
    
    public RpcResponseDTO<Boolean> unFollow(long uid, String hd_mac) {
	
	SocialFollowSortedSetService.getInstance().unFollow(uid, hd_mac);
	return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }



    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat, String lon) {

        if (uid != null || uid >0) {
            HandsetUser handsetUser = new HandsetUser();
            handsetUser.setId(hd_mac);
            handsetUser.setUid(uid);
            handsetUser.setCreated_at(new Date());
            handsetUserService.insert(handsetUser);
        }

        SocialHandsetMeetDTO dto = new SocialHandsetMeetDTO();
        dto.setBssid(bssid);
        dto.setSsid(ssid);
        dto.setTs(System.currentTimeMillis());
        dto.setLat(lat);
        dto.setLon(lon);

        String[] list = hd_macs.split(",");
        if (list != null && list.length >0) {
            for (String mac : list) {
                //TODO:(bluesand): backend操作
                SocialHandsetMeetHashService.getInstance().handsetMeet(hd_mac, mac, bssid, JsonHelper.getJSONString(dto));
            }
        }
        return false;
    }


    public WifiUserHandsetVTO fetchHandsetList(String bssid, String hd_macs) {
        WifiUserHandsetVTO vto = new WifiUserHandsetVTO();

        List<UserHandsetVTO> hdVTOs = new ArrayList<UserHandsetVTO>();
        List<String> hds = new ArrayList<String>();
        String[] list = hd_macs.split(",");
        if (list != null && list.length >0) {
            for (String hd_mac : list) {
                UserHandsetVTO handsetVTO = new UserHandsetVTO();
                handsetVTO.setHd_mac(hd_mac);
                hds.add(hd_mac);
                hdVTOs.add(handsetVTO);
            }
        }

        List<HandsetUser> handsetUsers = handsetUserService.findByIds(hds, true, true);

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

        return vto;
    }

    
    
    /**
     * 分页获取评论列表
     * @param uid
     * @param bssid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public TailPage<WifiCommentVTO> pageWifiCommentVTO(int uid,String bssid, int pageNo, int pageSize){
    	
    	 ModelCriteria mc = new ModelCriteria();
         mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("bssid", bssid);
         int total = wifiCommentService.countByCommonCriteria(mc);
         mc.setPageNumber(pageNo);
         mc.setPageSize(pageSize);
         mc.setOrderByClause(" created_at desc");

         List<WifiComment> wifiComments = wifiCommentService.findModelByCommonCriteria(mc);
         List<Integer> uids=new ArrayList<Integer>();
         for (WifiComment wifiComment : wifiComments) {
        	 
        	 uids.add((int)wifiComment.getUid());
         }
         List<User> users=userService.findByIds(uids);  
         List<WifiCommentVTO> vtos = new ArrayList<WifiCommentVTO>();
         if (wifiComments != null) {
        	 WifiCommentVTO vto = null;
        	 User user=null;
        	 int i=0;
             for (WifiComment wifiComment : wifiComments) {
                 vto = new WifiCommentVTO();
                 
                 
                 vto.setBssid(wifiComment.getBssid());
                 vto.setUid(wifiComment.getUid());
                 vto.setMessage(wifiComment.getMessage());
                 vto.setCreated_at(DateTimeHelper.formatDate(wifiComment.getCreated_at(),DateTimeHelper.FormatPattern1));
                 //vto.setUpdate_at(DateTimeHelper.formatDate(wifiComment.getUpdated_at(),DateTimeHelper.FormatPattern1));
                // if(users.size()>0){
                 user=users.get(i);
                 vto.setNick(user.getNick());
                 vto.setAvatar(user.getAvatar());
                // }
                 vtos.add(vto);
                 i++;
             }
         }
         return new CommonPage<WifiCommentVTO>(pageNo, pageSize, total, vtos);
    }

    
    public Set<String> fetchUserCommentWifiList(String uid){
    	
    	return WifiCommentSortedSetService.getInstance().fetchUserWifiList(uid);
    	
    }
    
}

