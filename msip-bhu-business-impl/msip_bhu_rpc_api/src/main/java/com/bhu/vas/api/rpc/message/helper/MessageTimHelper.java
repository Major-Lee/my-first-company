package com.bhu.vas.api.rpc.message.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.PaymentChannelType;
import com.bhu.vas.api.helper.BusinessEnumType.TimPushChannel;
import com.bhu.vas.api.helper.BusinessEnumType.TimPushMsgType;
import com.bhu.vas.api.rpc.message.dto.TimPushConditionDTO;
import com.bhu.vas.api.rpc.message.dto.TimPushDTO;
import com.bhu.vas.api.rpc.message.dto.TimMsgBodyDTO;
import com.bhu.vas.api.rpc.message.dto.TimTextMsgContentDTO;
import com.bhu.vas.api.rpc.message.dto.TimCustomMsgContentDTO;
import com.bhu.vas.api.rpc.message.dto.TimCustomMsgDefaultDataDTO;
import com.bhu.vas.api.rpc.message.dto.TimGetPushReportDTO;
import com.bhu.vas.api.rpc.message.dto.TimMulImportAccountDTO;
import com.bhu.vas.api.rpc.message.dto.TimOfflinePushInfoDTO;
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.dto.TimSendMsgDTO;
import com.bhu.vas.api.rpc.message.dto.TimUserBasicDTO;
import com.bhu.vas.api.rpc.message.dto.TimUserTagDTO;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.tls.sigcheck.tls_sigcheck;

public class MessageTimHelper {
	private final static Logger logger = LoggerFactory.getLogger(MessageTimHelper.class);
	public static final String timLibPath = "/BHUData/apps/tim_msg_lib/jnisigcheck_mt_x64.so";
	public static final String privateKeyPath = "/BHUData/apps/tim_msg_lib/private_key";
	public static String priKeyContent = null;
	public static String visitorExpire = "604800";
	public static String defaultExpire = "315360000";
	public static final int MAXMSGRANDOM = 999999999;
	public static final int DefaultMsgLifeTime = 604800;
	static{
		try {
			priKeyContent = getTimKey(privateKeyPath);
		} catch (Exception e) {
			System.out.println("getTimKey error");
			e.printStackTrace();
		}
	}
	
	public static String createUserSig(String user, String expire){
		tls_sigcheck tls = new tls_sigcheck();
		tls.loadJniLib(timLibPath);
		int result = tls.tls_gen_signature_ex2_with_expire(BusinessRuntimeConfiguration.TimSdkAppid,
				user, priKeyContent, expire);
		if (result != 0){
			return null;
		}
		return tls.getSig();
	}
	
	private static String getTimKey(String filePath) throws Exception{
		File priKeyFile = new File(filePath);
        StringBuilder strBuilder = new StringBuilder();
        String s = "";
        BufferedReader br = new BufferedReader(new FileReader(priKeyFile));
        while ((s = br.readLine()) != null) {
            strBuilder.append(s + '\n');
        }
        br.close();
		return strBuilder.toString();
	}
	
	public static final String Tim_Url = "https://console.tim.qq.com/v4";
	public static final String Account_Manage = "/im_open_login_svc";
	public static final String OpenIM = "/openim";
	public static final String Action_Account_Import = "/account_import";
	public static final String Action_MUL_Import = "/multiaccount_import";
	public static final String Action_IM_Push = "/im_push";
	public static final String Action_IM_Get_Push_Report = "/im_get_push_report";
	public static final String Action_Add_Tag = "/im_add_tag";
	public static final String Action_Get_Tag = "/im_get_tag";
	public static final String Action_Remove_Tag = "/im_remove_tag";
	public static final String Action_Remove_All_Tags = "/im_remove_all_tags";
	public static final String Action_Send_Msg = "/sendmsg";
	
	public static final int Response_Tim_Server_Error = 91000;
	public static Map<String, String> generateTimApiParamMap(){
		Map<String, String> api_params = new HashMap<String, String>();
		api_params.put("identifier", BusinessRuntimeConfiguration.TimManager);
		api_params.put("sdkappid", BusinessRuntimeConfiguration.TimSdkAppid);
		api_params.put("usersig", BusinessRuntimeConfiguration.TimManagerSig);
		api_params.put("contenttype", "json");
		return api_params;
	}
	
	/**
	 * 腾讯云im 导入用户接口
	 * @param account 用户名
	 * @return TimResponseBasicDTO
	 */
	
	public static TimResponseBasicDTO CreateTimAccoutImportUrlCommunication(String account){
		Map<String, String> api_params = generateTimApiParamMap();
		TimUserBasicDTO user = new TimUserBasicDTO();
		user.setIdentifier(account);
		TimResponseBasicDTO rcp_dto = null;
		try {
			String response = TimHttpHelper.postUrlAsString(Tim_Url+Account_Manage+Action_Account_Import,
					api_params, JsonHelper.getJSONString(user));
			if(StringUtils.isNotEmpty(response)){
				logger.info(String.format("CreateTimAccoutImportUrlCommunication response[%s]", response));
				return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateTimAccoutImportUrlCommunication error account[%s]", account); 
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	
	/**
	 * 腾讯云im 批量导入用户接口
	 * @param accounts 账户名用户,隔开
	 * @return TimResponseBasicDTO
	 */
	
	public static TimResponseBasicDTO CreateTimMULAccoutImportUrlCommunication(String accounts){
		Map<String, String> api_params = generateTimApiParamMap();
		TimMulImportAccountDTO dto = TimMulImportAccountDTO.buildTimMULImportAccountDTO(accounts);
		TimResponseBasicDTO rcp_dto = null;
		try {
			String response = TimHttpHelper.postUrlAsString(Tim_Url+Account_Manage+Action_MUL_Import,
					api_params, JsonHelper.getJSONString(dto));
			if(StringUtils.isNotEmpty(response)){
				logger.info(String.format("CreateTimMULAccoutImportUrlCommunication response[%s]", response));
				return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateTimMULAccoutImportUrlCommunication error accounts[%s]", accounts); 
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	/**
	 * 腾讯云im 批量导入用户接口
	 * @param accounts 账户名用户list
	 * @return TimResponseBasicDTO
	 */
	
	public static TimResponseBasicDTO CreateTimMULAccoutImportUrlCommunication(List<String> accounts){
		Map<String, String> api_params = generateTimApiParamMap();
		TimMulImportAccountDTO dto = TimMulImportAccountDTO.buildTimMULImportAccountDTO(accounts);
		TimResponseBasicDTO rcp_dto = null;
		try {
			String response = TimHttpHelper.postUrlAsString(Tim_Url+Account_Manage+Action_MUL_Import,
					api_params, JsonHelper.getJSONString(dto));
			if(StringUtils.isNotEmpty(response)){
				logger.info(String.format("CreateTimMULAccoutImportUrlCommunication response[%s]", response));
				return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateTimMULAccoutImportUrlCommunication error accounts[%s]", accounts); 
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	/**
	 * 腾讯云IM 添加标签接口,每次只能给500个用户添加标签
	 * @param accounts 用户列表
	 * @param tags 
	 * @return
	 */
	
	public static TimResponseBasicDTO CreateTimAddTagUrlCommunication(String accounts, String tags){
		Map<String, String> api_params = generateTimApiParamMap();
		TimUserTagDTO utagDto = TimUserTagDTO.buildTimUserTagDTO(accounts, tags);
		TimResponseBasicDTO rcp_dto = null;
		try {
			String response = TimHttpHelper.postUrlAsString(Tim_Url+OpenIM+Action_Add_Tag,
					api_params, JsonHelper.getJSONString(utagDto));
			System.out.println(JsonHelper.getJSONString(utagDto));
			if(StringUtils.isNotEmpty(response)){
				logger.info(String.format("CreateTimAddTagUrlCommunication response[%s]", response));
				return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateTimAddTagUrlCommunication error accounts[%s] tags[%s]", accounts, tags); 
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	/**
	 * 
	 * @param fromAcc 推送频道
	 * @param msgLifeTime 离线消息保存时间默认7天
	 * @param tags 目标用户,null为全员推送
	 * @param pushMsgType 推送消息类型
	 * @param content 推送内容
	 * @return
	 */
	
	public static <T> TimResponseBasicDTO CreateTimPushUrlCommunication(TimPushChannel  pushChannel, int msgLifeTime, String tags, TimPushMsgType msgType, String content){
		Map<String, String> api_params = generateTimApiParamMap();
		TimPushDTO<T> pushDTO = new TimPushDTO<T>();
		if (msgLifeTime != 0){
			pushDTO.setMsgLifeTime(msgLifeTime);
		}
		pushDTO.setFromAccount(pushChannel.getName());
		pushDTO.setCondition(TimPushConditionDTO.builder(tags));
		pushDTO.setMsgBodyList(builderMsgBody(msgType, content));
		pushDTO.setOfflinePushInfo(TimOfflinePushInfoDTO.builder("{\"pushType\":\"IMPush\"}"));
		TimResponseBasicDTO rcp_dto = null;
		try {
			String response = TimHttpHelper.postUrlAsString(Tim_Url+OpenIM+Action_IM_Push,
					api_params, JsonHelper.getJSONString(pushDTO));
			if(StringUtils.isNotEmpty(response)){
				logger.info(String.format("CreateTimPushUrlCommunication response[%s]", response));
				return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateTimPushUrlCommunication error pushChannel[%s] msgLifeTime[%s]"
					+ "tags[%s] msgType[%s] content[%s]", pushChannel.getName(), msgLifeTime, 
					tags, msgType.getName(), content);
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	
	/**
	 * 腾讯云im 批量导入用户接口
	 * @param accounts 账户名用户,隔开
	 * @return TimResponseBasicDTO
	 */
	
	public static TimResponseBasicDTO CreateTimGetPushReportUrlCommunication(String taskids){
		Map<String, String> api_params = generateTimApiParamMap();
		TimGetPushReportDTO getReportDto = TimGetPushReportDTO.buildTimGetPushReportDTO(taskids);
		TimResponseBasicDTO rcp_dto = null;
		try {
			String response = TimHttpHelper.postUrlAsString(Tim_Url+OpenIM+Action_IM_Get_Push_Report,
					api_params, JsonHelper.getJSONString(getReportDto));
			if(StringUtils.isNotEmpty(response)){
				logger.info(String.format("CreateTimGetPushReportUrlCommunication response[%s]", response));
				return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateTimGetPushReportUrlCommunication error accounts[%s]", taskids); 
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	public static <T> TimResponseBasicDTO CreateTimSendMsgUrlCommunication(TimPushChannel  pushChannel, String toAcc, TimPushMsgType msgType, String content){
		
		Map<String, String> api_params = generateTimApiParamMap();
		TimSendMsgDTO<T> sendMsgDto = new TimSendMsgDTO<T>();
		sendMsgDto.setFrom_Account(pushChannel.getName());
		sendMsgDto.setTo_Account(toAcc);
		sendMsgDto.setMsgBodyList(builderMsgBody(msgType, content));
		sendMsgDto.setOfflinePushInfo(TimOfflinePushInfoDTO.builder("{\"pushType\":\"IMPush\"}"));
		TimResponseBasicDTO rcp_dto = null;
		try {
			String response = TimHttpHelper.postUrlAsString(Tim_Url+OpenIM+Action_Send_Msg,
					api_params, JsonHelper.getJSONString(sendMsgDto));
			if(StringUtils.isNotEmpty(response)){
				logger.info(String.format("CreateTimSendMsgUrlCommunication response[%s]", response));
				return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
			}
		} catch (Exception ex) {
			logger.error("CreateTimSendMsgUrlCommunication error toAcc[%s] content", toAcc, content); 
			ex.printStackTrace(System.out);
		}
		return rcp_dto;
	}
	
	public static TimPushChannel validateTimPushChannel(Integer channel){
		if (channel == null) 
			return null;
		return BusinessEnumType.TimPushChannel.fromKey(channel);
	}
	
	public static TimPushMsgType validateTimPushMsgType(Integer type){
		if (type == null) 
			return null;
		return BusinessEnumType.TimPushMsgType.fromKey(type);
	}
	
	public static boolean isSupportChannel(Integer channel){
		PaymentChannelType type = BusinessEnumType.PaymentChannelType.fromKey(channel);
		if (type == null){
			return false;
		}else{
			return true;
		}
	}
	
	public static <T> TimPushDTO<T> builderPush(TimPushChannel  pushChannel, int msgLifeTime, String tags, TimPushMsgType msgType, String content){
		TimPushDTO<T> pushDTO = new TimPushDTO<T>();
		if (msgLifeTime != 0){
			pushDTO.setMsgLifeTime(msgLifeTime);
		}
		pushDTO.setFromAccount(pushChannel.getName());
		pushDTO.setCondition(TimPushConditionDTO.builder(tags));
		pushDTO.setMsgBodyList(builderMsgBody(msgType, content));
		pushDTO.setOfflinePushInfo(TimOfflinePushInfoDTO.builder("{\"pushType\":\"IMPush\"}"));
		return pushDTO;
	}
	
	public static <T> TimSendMsgDTO<T> builderSendMsg(TimPushChannel  pushChannel, String toAcc, TimPushMsgType msgType, String content){
		TimSendMsgDTO<T> sendMsgDto = new TimSendMsgDTO<T>();
		sendMsgDto.setFrom_Account(pushChannel.getName());
		sendMsgDto.setTo_Account(toAcc);
		sendMsgDto.setMsgBodyList(builderMsgBody(msgType, content));
		sendMsgDto.setOfflinePushInfo(TimOfflinePushInfoDTO.builder("{\"pushType\":\"IMPush\"}"));
		return sendMsgDto;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<TimMsgBodyDTO<T>> builderMsgBody(TimPushMsgType msgType, String content){
		List<TimMsgBodyDTO<T>> msgBodyList = new ArrayList<TimMsgBodyDTO<T>>();
		switch (msgType) {
		case TIMTextElem:
			msgBodyList.add((TimMsgBodyDTO<T>) TimMsgBodyDTO.buildTimMsgBodyDTO(msgType.getName(), 
					TimTextMsgContentDTO.builder(content)));
			break;
		case TIMCustomElem:
			TimCustomMsgDefaultDataDTO msgContentDTO = JsonHelper.getDTO(content, TimCustomMsgDefaultDataDTO.class);
			
			msgBodyList.add((TimMsgBodyDTO<T>) TimMsgBodyDTO.buildTimMsgBodyDTO(msgType.getName(), 
					TimCustomMsgContentDTO.builder(msgContentDTO)));
			break;
		default:
			logger.info(String.format("msgType[%s] is not support", msgType));
			break;
		}
		return msgBodyList;
	}
	
	public static TimResponseBasicDTO CreateTimUrlCommunication(String url, String message) throws Exception{
		logger.info(String.format("CreateTimUrlCommunication url[%s] message[%s]", url, message));
		Map<String, String> api_params = generateTimApiParamMap();
		String response = TimHttpHelper.postUrlAsString(url, api_params, message);
		TimResponseBasicDTO rcp_dto = null;
		if(StringUtils.isNotEmpty(response)){
			return JsonHelper.getDTO(response, TimResponseBasicDTO.class);
		}
		return rcp_dto;
	}
	
	
	public static boolean isNewUserTag(String oldTags, String newTag){
		if (StringUtils.isEmpty(oldTags))
			return true;
		String[] split = oldTags.split(",");
		for (String tag : split){
			if(tag.equals(newTag)){
				return  false;
			}
		}
		return true;
	}
	
	public static String replaceTags(String oldTags, String newTag){
		if (StringUtils.isEmpty(oldTags))
			return newTag;
		String[] split = oldTags.split(",");
		for (String tag : split){
			if(tag.equals(newTag)){
				return  oldTags;
			}
		}
		return oldTags.concat(",").concat(newTag);
	}
}
