package com.whisper.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.user.dto.MissedItemDTO;
import com.whisper.api.user.model.User;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.whisper.business.bucache.redis.serviceimpl.im.msgstorespace.UserMsgFacadeService;
import com.whisper.business.bucache.redis.serviceimpl.im.msgstorespace.UserMsgMissedCountService;
import com.whisper.business.bucache.redis.serviceimpl.offline.notify.UserOfflineMessageService;
import com.whisper.business.bucache.redis.serviceimpl.push.UserPushNotifyCountService;
import com.whisper.business.ucenter.service.UserService;
import com.whisper.msip.cores.web.mvc.WebHelper;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.ValidateUserCheckController;

@Controller
@RequestMapping("/user/offline")
public class UserOfflineController  extends ValidateUserCheckController{
	@Resource
	private UserService userService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@ResponseBody()
	@RequestMapping(value="/fetch_recents",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_offline_recents(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String type){
			//supportReceipt
			//@RequestParam(required = false,defaultValue="false") boolean sr){
			
		User user = userService.getById(uid);
		validateUserNotNull(user);
		List<String> result_chat = null;
		Map<String,Object> retMap = null;
		try{
			//判定用户最后登录时间是不是超过0.5天，超过的话发送用户登录消息
			if((System.currentTimeMillis() - user.getLastlogin_at().getTime()) > 0.5*1*24*3600*1000l){
				String remoteIp = WebHelper.getRemoteAddr(request);
				deliverMessageService.sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp,user.getLastlogindevice());
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		try{
			retMap = new HashMap<String,Object>();
			if(StringUtils.isEmpty(type)){//如果type为空则全取
				String userid = String.valueOf(uid);
				msgMissedBuilder(userid,retMap);
				//Map<String, String> allMissed = UserMsgMissedCountService.getInstance().fetchAllMissed(userid);
				/*UserMsgMissedDTO missedDto = msgMissedBuilder(uid);
				if(missedDto != null){
					retMap.put("M", JsonHelper.getJSONString(missedDto));
				}*/
				//sr=true fetchAndclear=false,	sr=false fetchAndclear=true
				result_chat = UserMsgFacadeService.builderOfflineChatMsg(userid);//,!sr);
				System.out.println(userid+":~~~new 1:"+result_chat);
				//List<String> old_chat = UserOfflineMessageService.getInstance().userOfflineAll(userid, BusinessKeyDefine.Present.UserOfflineType_ChatPrefixKey);
				//System.out.println("~~~new 2:"+old_chat);
				//result_chat.addAll(old_chat);
				retMap.put(BusinessKeyDefine.Present.UserOfflineType_ChatPrefixKey, result_chat);//new ArrayList<String>());//
				//result_chat.addAll(UserOfflineMessageService.getInstance().userOfflineAll(userid, BusinessKeyDefine.Present.UserOfflineType_UserPrefixKey));
				retMap.put(BusinessKeyDefine.Present.UserOfflineType_UserPrefixKey,UserOfflineMessageService.getInstance().userOfflineAll(userid, BusinessKeyDefine.Present.UserOfflineType_UserPrefixKey));//new ArrayList<String>());//
				retMap.put(BusinessKeyDefine.Present.UserOfflineType_NotifyPrefixKey, new ArrayList<String>());//UserOfflineMessageService.getInstance().userOfflineAll(userid, BusinessKeyDefine.Present.UserOfflineType_NotifyPrefixKey));//new ArrayList<String>());//
				//retMap.put(BusinessKeyDefine.Present.UserErrorType_DeliverPrefixKey, UserOfflineMessageService.getInstance().userOfflineAll(userid, BusinessKeyDefine.Present.UserErrorType_DeliverPrefixKey));
				
				UserOfflineMessageService.getInstance().deleteOfflineBy(String.valueOf(uid),
						BusinessKeyDefine.Present.UserOfflineType_ChatPrefixKey,
						BusinessKeyDefine.Present.UserOfflineType_UserPrefixKey);
						//BusinessKeyDefine.Present.UserOfflineType_NotifyPrefixKey);
						//BusinessKeyDefine.Present.UserErrorType_DeliverPrefixKey
				
				UserPushNotifyCountService.getInstance().del(uid);
				
				//清理消息的过期提醒数据
				if(!result_chat.isEmpty()){
					deliverMessageService.sendUserOfflineMsgFetchActionMessage(DeliverMessageType.AC.getPrefix(), uid);
				}
			}else{
				retMap.put(type, UserOfflineMessageService.getInstance().userOfflineAll(String.valueOf(uid), type));
				UserOfflineMessageService.getInstance().deleteOfflineBy(String.valueOf(uid), type);
			}
			//long count = UserOfflineMessageService.getInstance().userOfflineSize(uid, type);
			//List<String> ret = UserOfflineMessageService.getInstance().userOfflineAll(String.valueOf(uid), type);//.userOfflineLrange(uid, type, start, size);//.tickerLrangeJson(TickerFacadeService.MusicLove_Ticker, String.valueOf(uid), start, size);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retMap));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			if(retMap != null){
				retMap.clear();
				retMap = null;
			}
			if(result_chat != null){
				result_chat.clear();
				result_chat = null;
			}
		}
	}
	
	private void msgMissedBuilder(String user,Map<String,Object> retMap){
		Map<String, String> allMissed = UserMsgMissedCountService.getInstance().fetchAllMissed(user);
		if(allMissed.isEmpty()){
			return;
		}else{
			int total = 0;
			//MissedItemDTO item = null;
			List<MissedItemDTO> items = new ArrayList<MissedItemDTO>();
			Iterator<Entry<String, String>> iter = allMissed.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				try{
					int u = Integer.parseInt(entry.getKey());
					int val = Integer.parseInt(entry.getValue());
					//item = new MissedItemDTO(u,val);
					items.add(new MissedItemDTO(u,val));
					total+=val;
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
			retMap.put("MT", String.valueOf(total));
			retMap.put("MD", JsonHelper.getJSONString(items));
			//System.out.println("~~~~~~~~~Missed:"+retMap);
			/*UserMsgMissedDTO result = new UserMsgMissedDTO();
			result.setOwner(user);
			result.setTotal(total);
			result.setItems(items);*/
			//return result;
			UserMsgMissedCountService.getInstance().resetMissed(user);
		}
		
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/fetch_recent_counts",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_offline_recent_counts(
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid){
		
		User user = userService.getById(uid);
		validateUserNotNull(user);
		try{
			long chat_count = UserOfflineMessageService.getInstance().userOfflineSize(String.valueOf(uid), BusinessKeyDefine.Present.UserOfflineType_ChatPrefixKey);
			long user_count = UserOfflineMessageService.getInstance().userOfflineSize(String.valueOf(uid), BusinessKeyDefine.Present.UserOfflineType_UserPrefixKey);
			long topic_count = UserOfflineMessageService.getInstance().userOfflineSize(String.valueOf(uid), BusinessKeyDefine.Present.UserOfflineType_NotifyPrefixKey);
			//long error_count = UserOfflineMessageService.getInstance().userOfflineSize(String.valueOf(uid), BusinessKeyDefine.Present.UserErrorType_DeliverPrefixKey);
			Map<String,Long> retMap = new HashMap<String,Long>();
			retMap.put(BusinessKeyDefine.Present.UserOfflineType_ChatPrefixKey, chat_count);
			retMap.put(BusinessKeyDefine.Present.UserOfflineType_UserPrefixKey, user_count);
			retMap.put(BusinessKeyDefine.Present.UserOfflineType_NotifyPrefixKey, topic_count);
			//retMap.put(BusinessKeyDefine.Present.UserErrorType_DeliverPrefixKey, error_count);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retMap));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
