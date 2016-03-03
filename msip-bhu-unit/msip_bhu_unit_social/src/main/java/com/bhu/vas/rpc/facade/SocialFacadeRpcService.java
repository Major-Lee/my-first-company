package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.social.SocialHandsetMeetDTO;
import com.bhu.vas.api.rpc.social.model.UserHandset;
import com.bhu.vas.api.rpc.social.model.WifiComment;
import com.bhu.vas.api.rpc.social.model.pk.UserHandsetPK;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.WifiActionVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialHandsetMeetHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.WifiActionService;
import com.bhu.vas.business.ds.social.service.UserHandsetService;
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
    private UserHandsetService userHandsetService;
    
   @Resource 
   private UserService userService;

    public WifiComment comment(long uid, String bssid, String message) {

        WifiComment wifiComment = new WifiComment();
        wifiComment.setUid(uid);
        wifiComment.setMessage(message);
        wifiComment.setBssid(bssid);
        wifiComment.setCreated_at(new Date());
        return wifiCommentService.insert(wifiComment);
    }

    public WifiActionVTO clickPraise(String bssid, String type) {

	if (WifiActionService.getInstance().isNoExist(bssid)) {
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("up", "0");
	    map.put("down", "0");
	    map.put("report", "0");
	    WifiActionService.getInstance().hadd(bssid, map);
	}
	WifiActionService.getInstance().hincrease(bssid, type);
	return WifiActionService.getInstance().counts(bssid);
    }


    public boolean handsetMeet(Long uid, String hd_mac, String hd_macs, String bssid, String ssid, String lat, String lon) {

        if (uid != null || uid >0) {
            UserHandset userHandset = new UserHandset();
            userHandset.setId(new UserHandsetPK(uid, hd_mac));
            userHandset.setCreated_at(new Date());
            userHandsetService.insert(userHandset);
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
}
