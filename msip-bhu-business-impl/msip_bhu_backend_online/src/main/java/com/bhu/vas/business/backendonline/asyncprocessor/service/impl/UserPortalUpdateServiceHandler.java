package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.model.UserPortalUpdateDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class UserPortalUpdateServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(UserPortalUpdateServiceHandler.class);
	private ExecutorService exec = Executors.newFixedThreadPool(10);
	@Resource
	private UserService userService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	private static Map<String,String> Common_Headers = new HashMap<String,String>();
	static{
		//headers.put(RuntimeConfiguration.Param_ATokenHeader, "OjJWVFJXAUEXT0UNCVFf");
		Common_Headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		Common_Headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
				/*headers.put("X-Requested-With", "XMLHttpRequest");
				headers.put("Accept-Encoding", "gzip, deflate");
				headers.put("Accept-Language", "zh-CN,zh;q=0.8");
				headers.put("Connection", "keep-alive");*/
	}
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
			final UserPortalUpdateDTO dto = JsonHelper.getDTO(message, UserPortalUpdateDTO.class);
			final int uid = dto.getUid();
			final String type = dto.getType();
			if(uid >0 && StringUtils.isNotEmpty(type)){
				exec.submit((new Runnable() {
					@Override
					public void run() {
						try{
							Map<String, String> api_params = generateCommonApiParamMap(String.valueOf(uid));
							if(UserPortalUpdateDTO.PortalUpdate_User.equals(type)){
								api_params.put("title", dto.getNick());
								api_params.put("mobileno", dto.getMobileno());
								api_params.put("avatar", dto.getAvatar());
							}else{
								api_params.put("name", dto.getTpl());
								api_params.put("snk", dto.getSnk());
								api_params.put("rate", String.valueOf(dto.getUsers_rate()));
							}
							logger.info(String.format("UserPortalUpdate2UPortalApi request url[%s] params[%s]", 
									BusinessRuntimeConfiguration.UserPortalUpdate2UPortalApi, api_params));
							String response = HttpHelper.postUrlAsString(BusinessRuntimeConfiguration.UserPortalUpdate2UPortalApi, api_params,Common_Headers);
							logger.info(String.format("UserPortalUpdate2UPortalApi Response url[%s] params[%s] response[%s]", 
									BusinessRuntimeConfiguration.UserPortalUpdate2UPortalApi, api_params, response));
						}catch(Exception ex){
							ex.printStackTrace(System.out);
						}
					}
				}));
			}
		logger.info(String.format("process message[%s] successful", message));
	}
	public static Map<String, String> generateCommonApiParamMap(String uid){
		Map<String, String> api_params = new HashMap<String, String>();
		api_params.put("uid", uid);
		return api_params;
	}
/*	@Override
	public void createDeviceGroupIndex(String message) {

		logger.info(String.format("WifiDeviceGroupServiceHandler createDeviceGroupIndex message[%s]", message))
		WifiDeviceGroupAsynCreateIndexDTO dto = JsonHelper.getDTO(message, WifiDeviceGroupAsynCreateIndexDTO.class);
		String wifiIdsStr = dto.getWifiIds();
		String type = dto.getType();
		Long gid = dto.getGid();

		List<String> wifiIds = new ArrayList<>();
		List<List<Long>> groupIdList = new ArrayList<List<Long>>();
		String[] wifiIdArray = wifiIdsStr.split(StringHelper.COMMA_STRING_GAP);
		for (String wifiId : wifiIdArray) {
			wifiIds.add(wifiId);

			List<Long> gids = wifiDeviceGroupRelationService.getDeviceGroupIds(wifiId);
			if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_GRANT)) {
				gids.add(gid);
			} else if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_UNGRANT)) {
				gids.remove(gid);
			}
			groupIdList.add(gids);
		}
		List<WifiDevice> wifiDeviceList = wifiDeviceService.findByIds(wifiIds);
		wifiDeviceIndexIncrementService.wifiDeviceIndexBlukIncrement(wifiDeviceList, groupIdList);

	}*/
	
	
	public static void main(String[] argv) throws Exception{
		String jsonMessage = "{\"uid\":3,\"ts\":1460627328403,\"type\":\"User\",\"nick\":\"熊出没了d\",\"mobileno\":\"18612272825\",\"avatar\":\"http://profile.icon\",\"snk\":null,\"tpl\":null,\"users_rate\":0,\"actionType\":\"UU\"}";
		final UserPortalUpdateDTO dto = JsonHelper.getDTO(jsonMessage, UserPortalUpdateDTO.class);
		final int uid = dto.getUid();
		final String type = dto.getType();
		Map<String, String> api_params = generateCommonApiParamMap(String.valueOf(uid));
		/*if(UserPortalUpdateDTO.PortalUpdate_User.equals(type)){
			api_params.put("title", dto.getNick());
			api_params.put("mobileno", dto.getMobileno());
			api_params.put("avatar", dto.getAvatar());
		}else{
			api_params.put("name", dto.getTpl());
			api_params.put("snk", dto.getSnk());
			api_params.put("rate", String.valueOf(dto.getUsers_rate()));
		}*/
		
		Map<String,String> headers = new HashMap<String,String>();
		//headers.put(RuntimeConfiguration.Param_ATokenHeader, "OjJWVFJXAUEXT0UNCVFf");
		//headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
		/*headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8");
		headers.put("Connection", "keep-alive");*/
		
		System.out.println(String.format("UserPortalUpdate2UPortalApi request url[%s] params[%s]", 
				BusinessRuntimeConfiguration.UserPortalUpdate2UPortalApi, api_params));
		String response = HttpHelper.postUrlAsString("http://192.168.66.91:8550/portal/set/recreate?uid=123456", api_params,headers,"UTF-8");
		System.out.println(String.format("UserPortalUpdate2UPortalApi Response url[%s] params[%s] response[%s]", 
				BusinessRuntimeConfiguration.UserPortalUpdate2UPortalApi, api_params, response));
	}
}
