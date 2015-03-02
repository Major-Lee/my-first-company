
package com.wecite.toplines.business.asyn.web.handler;import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.logger.dto.ActLoggerDTO;
import com.bhu.vas.business.statistics.service.UserStatisticsService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.wecite.toplines.business.asyn.web.model.UserSubjectAbstractClickDTO;

/**
 * 用户注册（/account/create）后调用
 * @author Edmond
 *
 */public class UserSubjectAbstractClickActHandler {	private final Logger logger = LoggerFactory.getLogger(UserSubjectAbstractClickActHandler.class);
	
	@Resource
	private UserStatisticsService userStatisticsService;
		private UserSubjectAbstractClickActHandler(){			}		private static class ActHandlerHolder{ 		private static UserSubjectAbstractClickActHandler instance =new UserSubjectAbstractClickActHandler(); 	} 	/**	 * 获取工厂单例	 * @return	 */	public static UserSubjectAbstractClickActHandler getInstance() { 		return ActHandlerHolder.instance; 	}		public void handlerMessage(String message){		logger.info("handle message:"+message);
		UserSubjectAbstractClickDTO dto = null;
		try{
			dto = JsonHelper.getDTO(message, UserSubjectAbstractClickDTO.class);
			if(ActLoggerDTO.DOWN.equals(dto.getAct())){
				userStatisticsService.increaseUpdate(dto.getUid(), UserStatisticsService.Action_SubjectAbstractDown);
			}else{
				userStatisticsService.increaseUpdate(dto.getUid(), UserStatisticsService.Action_SubjectAbstractUp);
			}
			logger.info("handle message:"+message+" successfully!");
		}catch(Exception ex){
			ex.printStackTrace();
			logger.info("handle message:"+message+" fail!");
		}finally{
			dto = null;
		}	}}


