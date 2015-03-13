package com.whisper.web.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.business.token.service.TokenServiceHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.encrypt.AccessTokenExpirableHelper;
import com.smartwork.msip.cores.helper.json.DateConvertType;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.statics.dto.AUStaticsActiveUserDTO;
import com.whisper.api.statics.dto.AUStaticsContentDTO;
import com.whisper.api.statics.dto.AUStaticsMainDTO;
import com.whisper.api.statics.dto.AUStaticsUserDTO;
import com.whisper.api.statics.model.UserDailyCallback;
import com.whisper.business.facade.ucenter.UserAUStaticsFacadeService;
import com.whisper.business.statics.service.UserDailyCallbackService;
import com.whisper.business.ucenter.service.UserExtensionService;
import com.whisper.business.ucenter.service.UserService;
import com.whisper.msip.cores.web.mvc.spring.BaseController;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;

@Controller
@RequestMapping("/dashboard")
public class DashboardController extends BaseController{
	
	private final String SECRETKEY = "ZiF@XD#yaP9WdF6wT9#}TjtqhceGFmLL";
	/*@Resource
	private OnlineService onlineService;*/
	@Resource
	private UserService userService;

	
	@Resource
	private UserExtensionService userExtensionService;
	
	@Resource
	private UserDailyCallbackService userDailyCallbackService;
	
	@Resource
	private UserAUStaticsFacadeService userAUStaticsFacadeService;
	
	/*@Resource
	private UserEmptyResultSearchStateService userEmptyResultSearchStateService;
	
	
	@Resource
	private UserOnlineTimeDailyStatisicsService userOnlineTimeDailyStatisicsService;
	
	@Resource
	private BusinessHeartbeatCachedService businessHeartbeatCachedService;*/
	
	
	/**
	1 用户:当天新注册用户 本周的上一周的注册用户数 本月的上一月的注册用户数 总注册用户数
	2 DAU：当天的
			DAU：当天日期ID取得dailycallback的newly+callback（非缓存） 
			WAU：通过日志分析本周的上一周生成WAU 
			MAU：通过日志分析本日的上一月生成MAU
	3 缓存半个小时
	4 
	*/
	
	@ResponseBody()
	@RequestMapping(value="/fetch_statics",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_statics(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String secretkey) {
		Map<String,Long> retMap = null;
		try{
			if(!SECRETKEY.equals(secretkey)){
				 SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
				 return;
			}
			retMap = new HashMap<String,Long>();
			int totalusercount = userService.countAllRecords();
			
			/*long nusercount = UserOnlineFacadeService.userOnlinedCount();
			retMap.put("NOnlineCount", nusercount);
			Iterator<Entry<String, DeviceEnum>> iter = DeviceEnum.getMapSName().entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, DeviceEnum> entry = iter.next();
				DeviceEnum device = entry.getValue();
				if(device.getName().startsWith(DeviceEnum.Prefix_Guest_App_Name)){
					long gusercount = businessHeartbeatCachedService.distillUserGuestHeartbeatCount(device.getSname());//.userOnlinedCount();
					retMap.put(device.getName(), gusercount);
					System.out.println("dashboard fetch_online_count:"+device.getName()+" count:"+gusercount);
				}
			}
			System.out.println("dashboard fetch_online_count:"+retMap);*/
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retMap));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			if(retMap != null){
				retMap.clear();
				retMap=null;
			}
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/fetch_user_count",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_user_count(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String secretkey) {
		
			if(!SECRETKEY.equals(secretkey)){
				 SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
				 return;
			}
			try{
				
				//mc.createCriteria().andSimpleCaulse(" 1=1 ");
				int totalusercount = userService.countAllRecords();//userService.countByModelCriteria(mc);
				StringBuilder sbret = new StringBuilder();
				/*//mc = new ModelCriteria();
				//mc.createCriteria().andColumnEqualTo("newbie", 0);//.andColumnIsNull("truename");
				int validusercount = spliterUserService.countAllNewBie0Records();//.countByModelCriteria(mc);
				
				ModelCriteria mc = new ModelCriteria();
				mc = new ModelCriteria();
				mc.createCriteria().andColumnGreaterThan("regist_type", 0);//.andColumnEqualTo("newbie", 0).andColumnIsNull("truename");
				int snsusercount = userExtensionService.countByModelCriteria(mc);
				
				
				mc = new ModelCriteria();
				mc.createCriteria().andColumnNotEqualTo("lastlogindevice", DeviceEnum.APP360.getSname()).andColumnEqualTo("transfer", 0);
				int appusercountwithout360 = appUserService.countByModelCriteria(mc);
				
				
				sbret.append(totalusercount).append(StringHelper.COMMA_STRING_GAP).append(validusercount)
				.append(StringHelper.COMMA_STRING_GAP).append(snsusercount)
				.append(StringHelper.COMMA_STRING_GAP).append(appusercountwithout360);*/
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(sbret.toString()));
				
			}catch(Exception ex){
				ex.printStackTrace();
				SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
			}finally{
			}
			
	}
	
	private static final String[] callbackfields = {"id",
				"newly","web_newly","iphone_newly","ipad_newly","itouch_newly","android_newly","app360_newly","a_iphone_newly","a_ipad_newly","a_itouch_newly","a_android_newly",
				"callback","web_callback","iphone_callback","ipad_callback","itouch_callback","android_callback","app360_callback","a_iphone_callback","a_ipad_callback","a_itouch_callback","a_android_callback",
				"updated_at"};

	/**
	 * curl -d "secretkey=ZiF@XD#yaP9WdF6wT9#}TjtqhceGFmLL" http://jing.fm/api/v1/dashboard/fetch_user_callback
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_user_callback",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_user_callback(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String secretkey,
			@RequestParam(required = false) String date,
			@RequestParam(required = false, defaultValue = "0", value = "st") int start,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int size) {
		
			if(!SECRETKEY.equals(secretkey)){
				 SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
				 return;
			}
			List<UserDailyCallback> callbacks = null;
			List<Map<String,Object>> resultlist = null;
			int count = 0;
			try{
				if(StringUtils.isEmpty(date)){
					CommonCriteria mc = new CommonCriteria();
					mc.setOrderByClause(" id desc");
					mc.setStartRow(start);
					mc.setSize(size);
					count = userDailyCallbackService.countByCommonCriteria(mc);
					if(count == 0){
						SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(Collections.emptyList(), start, size, count)));
						return;
					}
					callbacks = userDailyCallbackService.findModelByCommonCriteria(mc);
					if(callbacks.isEmpty()){
						SpringMVCHelper.renderJson(response, ResponseSuccess.embed(PageHelper.partialAllList(callbacks, 0, start, size)));
						return;
					}
				}else{
					UserDailyCallback callback = userDailyCallbackService.getById(date);
					callbacks = new ArrayList<UserDailyCallback>();
					if(callback != null){
						count = 1;
						callbacks.add(callback);
					}
				}
				resultlist = JsonHelper.filterListData(callbacks,DateConvertType.TOSTRING_EN_COMMON_YMDHMS, false, callbackfields);
				//SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialDataWithNoJsonConfig(resultlist, start, size, count)));
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(resultlist, start, size, count)));//.getPartialDataWithNoJsonConfig(resultlist, start, size, count)));
			}catch(Exception ex){
				ex.printStackTrace();
				SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
			}finally{
				if(callbacks != null){
					callbacks.clear();
					callbacks = null;
				}
			}
	}
	/* blink data start*/
	public static final String DashboardPwdEndString = "simba";
	
	@ResponseBody()
	@RequestMapping(value="/validate",method={RequestMethod.GET,RequestMethod.POST})
	public void validate(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String pwd) {
		try{
			String currentPwd = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern7).concat(DashboardPwdEndString);
			if(!currentPwd.equals(pwd)){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
			}
			String uToken = TokenServiceHelper.generateAccessToken4User(Integer.parseInt(RuntimeConfiguration.SystemAdminUserID));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(uToken));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{

		}
	}
	/**
	 * 验证dashboard token
	 * @param uToken
	 * @return
	 */
	public boolean validateDashboardToken(String uToken){
		return TokenServiceHelper.isNotExpiredAccessToken4User(uToken, AccessTokenExpirableHelper.TimeGap4Validate_30M);
	}
	
	@ResponseBody()
	@RequestMapping(value="/fetch_main_analytics",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_main_analytics(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String token) {
		
		if(!this.validateDashboardToken(token)){
			 SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
			 return;
		}
		
		try{
			AUStaticsMainDTO dto = userAUStaticsFacadeService.getMainAnalytics();
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dto));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{

		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/fetch_user_analytics",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_user_analytics(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String token) {
		
		if(!this.validateDashboardToken(token)){
			 SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
			 return;
		}
		
		try{
			AUStaticsUserDTO dto = userAUStaticsFacadeService.getUserAnalytics();
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dto));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{

		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/fetch_active_user_analytics",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_active_user_analytics(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String token) {
		
		if(!this.validateDashboardToken(token)){
			 SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
			 return;
		}
		
		try{
			AUStaticsActiveUserDTO dto = userAUStaticsFacadeService.getActiveUserAnalytics();
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dto));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{

		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/fetch_content_analytics",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_content_analytics(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) String token) {
		
		if(!this.validateDashboardToken(token)){
			 SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
			 return;
		}
		
		try{
			AUStaticsContentDTO dto = userAUStaticsFacadeService.getContentMessageAnalytics();
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dto));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{

		}
	}
	
	/* blink data end*/
}
