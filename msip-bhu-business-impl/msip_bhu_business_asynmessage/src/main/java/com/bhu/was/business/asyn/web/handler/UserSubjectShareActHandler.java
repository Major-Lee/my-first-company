
package com.bhu.was.business.asyn.web.handler;import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.logger.dto.ActLoggerDTO;
import com.bhu.vas.api.subject.dto.SubjectNotifyDTO;
import com.bhu.vas.api.subject.dto.SubjectShareResponseDTO;
import com.bhu.vas.api.subject.model.Subject;
import com.bhu.vas.business.subject.service.SubjectService;
import com.bhu.vas.business.subject.service.SubjectUserAbstractService;
import com.bhu.was.business.asyn.web.model.UserSubjectClickDTO;
import com.bhu.was.business.asyn.web.model.UserSubjectShareDTO;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * @author Edmond
 *
 */public class UserSubjectShareActHandler {	private final Logger logger = LoggerFactory.getLogger(UserSubjectShareActHandler.class);
	@Resource
	private SubjectService subjectService;
	
	@Resource
	private SubjectUserAbstractService subjectUserAbstractService;
		private UserSubjectShareActHandler(){			}		private static class ActHandlerHolder{ 		private static UserSubjectShareActHandler instance =new UserSubjectShareActHandler(); 	} 	/**	 * 获取工厂单例	 * @return	 */	public static UserSubjectShareActHandler getInstance() { 		return ActHandlerHolder.instance; 	}		public void handlerMessage(String message){		logger.info("handle message:"+message);
		UserSubjectShareDTO dto = null;
		try{
			dto = JsonHelper.getDTO(message, UserSubjectShareDTO.class);
			Subject subject = subjectService.getById(dto.getSid());
			if(subject == null){
				logger.info("subject entity not exist!");
				return;
			}
			//logger.info("handle message:"+subject.getVisible_state());
			if(subject.getVisible_state() == Subject.VisibleState_Normal){
				notifyAutoSubjectUpClick(dto.getUid(),dto.getSid(),dto.getTs());
				SubjectNotifyDTO notifydto = SubjectNotifyDTO.fromSubject(subject);
				notifydto.setCustom_abstract(dto.getCustom_abstract());
				Map<String,String> header = new HashMap<String,String>();
				header.put(RuntimeConfiguration.Param_ATokenHeader, "atoken");
				header.put(RuntimeConfiguration.Param_RTokenHeader,"rtoken");
				Map<String,String> params  = new HashMap<String,String>();
				params.put("data", JsonHelper.getJSONString(notifydto));
				params.put("app", "headline");
				params.put("mod", "Index");
				params.put("act", "share");
				String response = null;
				if(!RuntimeConfiguration.SecretInnerTest)
					response = HttpHelper.getUrlAsString("http://wecite.cn/index.php", params,"UTF-8");//, header);
					//response = HttpHelper.postUrlAsString("http://wecite.cn/whisper_api/v1/sessions/validates", params, header);
				else
					response = HttpHelper.getUrlAsString("http://192.168.0.188/index.php", params,"UTF-8");//, header);
					//response = HttpHelper.postUrlAsString("http://192.168.1.106/whisper_api/v1/sessions/validates", params, header);
				System.out.println("~~~~~~~~response:"+response);
				if(response!=null){
					SubjectShareResponseDTO resDto = JsonHelper.getDTO(response, SubjectShareResponseDTO.class);
					if(resDto.isSuccess())
						System.out.println("~~~~~~~~response Success:"+resDto.getFid());
					else
						System.out.println("~~~~~~~~response Fail:"+resDto.getFid());
				}
			}
			logger.info("handle message:"+message+" successfully!");
		}catch(Exception ex){
			ex.printStackTrace();
			logger.info("handle message:"+message+" fail!");
		}finally{
			dto = null;
		}	}
	
	private void notifyAutoSubjectUpClick(int uid,int sid,long ts){
		UserSubjectClickDTO dto = new UserSubjectClickDTO();
		dto.setSid(sid);
		dto.setTs(ts);
		dto.setIncr(1);
		dto.setUid(uid);
		dto.setAct(ActLoggerDTO.UP);
		UserSubjectClickActHandler.getInstance().handler(dto);
	}}


