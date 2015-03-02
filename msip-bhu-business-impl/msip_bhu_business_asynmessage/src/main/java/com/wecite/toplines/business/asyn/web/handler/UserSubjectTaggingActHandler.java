
package com.wecite.toplines.business.asyn.web.handler;import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.statistics.service.UserStatisticsService;
import com.bhu.vas.business.subject.service.SubjectService;

/**
 * @author Edmond
 *
 */public class UserSubjectTaggingActHandler {	private final Logger logger = LoggerFactory.getLogger(UserSubjectTaggingActHandler.class);
	
	@Resource
	private UserStatisticsService userStatisticsService;
	
	@Resource
	private SubjectService subjectService;
	
	private UserSubjectTaggingActHandler(){			}		private static class ActHandlerHolder{ 		private static UserSubjectTaggingActHandler instance =new UserSubjectTaggingActHandler(); 	} 	/**	 * 获取工厂单例	 * @return	 */	public static UserSubjectTaggingActHandler getInstance() { 		return ActHandlerHolder.instance; 	}		public void handlerMessage(String message){		logger.info("handle message:"+message);
			
		logger.info("handle message:"+message+" successfully!");
	}
}


