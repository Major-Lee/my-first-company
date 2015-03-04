
package com.bhu.was.business.asyn.web.handler;import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.statistics.service.UserStatisticsService;

/**
 * 用户注册（/account/create）后调用
 * @author Edmond
 *
 */public class UserSubjectEstimateActHandler {	private final Logger logger = LoggerFactory.getLogger(UserSubjectEstimateActHandler.class);
	
	@Resource
	private UserStatisticsService userStatisticsService;
	
	private UserSubjectEstimateActHandler(){			}		private static class ActHandlerHolder{ 		private static UserSubjectEstimateActHandler instance =new UserSubjectEstimateActHandler(); 	} 	/**	 * 获取工厂单例	 * @return	 */	public static UserSubjectEstimateActHandler getInstance() { 		return ActHandlerHolder.instance; 	}		public void handlerMessage(String message){		logger.info("handle message:"+message);
		/*UserSubjectEstimateDTO dto = null;
		try{
			dto = JsonHelper.getDTO(message, UserSubjectEstimateDTO.class);
			userStatisticsService.increaseUpdate(dto.getUid(), UserStatisticsService.Action_Estimate);
			subjectIndexFacadeService.createSubjectIndex(dto.getSid());
			logger.info("handle message:"+message+" successfully!");
		}catch(Exception ex){
			ex.printStackTrace();
			logger.info("handle message:"+message+" fail!");
		}finally{
			dto = null;
		}*/	}}


