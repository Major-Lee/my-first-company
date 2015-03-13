package com.whisper.web.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;

@Controller
@RequestMapping("/ping")
public class PingController {
	
/*	@Resource
	private UserFriendOnlineZSetService userFriendOnlineZSetService;*/
	
	//@Resource
	//private SpliterUserService spliterUserService;
	
//	@ResponseBody()
//	@RequestMapping(value="/v1",method={RequestMethod.GET,RequestMethod.POST})
//	public void ping(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@RequestParam(required = false,defaultValue="") String uid,
//			@RequestParam(required = false,defaultValue="false") boolean isStore) {
//		IpModel model = new IpModel();
//		model.setX_forwarded_for(request.getHeader("x-forwarded-for"));
//		model.setProxy_Client_IP(request.getHeader("Proxy-Client-IP"));
//		model.setWl_Proxy_Client_IP(request.getHeader("WL-Proxy-Client-IP"));
//		model.setRemoteAddr(request.getRemoteAddr());
//		model.setServerAddr(WebHelper.getHostName()+"-"+WebHelper.getAddress());
//		
//		String test = "我是你爸爸";
//		String dtest = StringNormalTokenHelper.generateStringToken(test);
//		
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("encode", dtest);
//		map.put("decode", StringNormalTokenHelper.parseStringFromToken(dtest));
//		map.put("default.locale", Locale.getDefault().toString());
//		map.put("default.charset", Charset.defaultCharset().displayName());
//		Charset.availableCharsets();
//		if(StringUtils.isNotEmpty(uid)){
//			if(isStore){
//				userFriendOnlineZSetService.doInterFrdAndStore(Integer.parseInt(uid));
//			}else{
//				long count = userFriendOnlineZSetService.doGetOnlineFrdCount(Integer.parseInt(uid));
//				Set<String> set = userFriendOnlineZSetService.doGetOnlineFrds(Integer.parseInt(uid),0,(int)count);
//				map.put("count", count);
//				map.put("frds", set);
//			}
//		}
//		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
//		
//		/*if (username == null || username.equals(oldusername)) {
//			//return true;
//			SpringMVCHelper.renderText(response, "true");//renderHtml(response, html, headers)
//			return;
//		}
//		Integer userid = this.userService.findUniqueIdByProperty("username", username);
//		if (userid == null) {
//			SpringMVCHelper.renderText(response, "true");
//		} else {
//			SpringMVCHelper.renderText(response, "false");
//		}*/
//	}
	

	
	@ResponseBody()
	@RequestMapping(value="/v1",method={RequestMethod.GET,RequestMethod.POST})
	public void bson(
			HttpServletRequest request,
			HttpServletResponse response/*,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String uids*/) {

		try{
			//String[] uidarray = StringHelper.split(uids, StringHelper.COMMA_STRING_GAP);
			//List<Object> ret = UserPlayDurationService.getInstance().hget_pipeline_playDurations(ArrayHelper.toList(uidarray));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed("ping ok.."));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
}
