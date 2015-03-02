
package com.wecite.toplines.business.asyn.web.handler;import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.statistics.service.UserStatisticsService;
import com.bhu.vas.business.subject.service.DailySubjectClickService;
import com.bhu.vas.business.subject.service.SubjectClickService;
import com.bhu.vas.business.subject.service.SubjectService;
import com.bhu.vas.business.subject.service.SubjectTagStateService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.wecite.toplines.business.asyn.web.model.UserSubjectClickDTO;

/**
 * 用户注册（/account/create）后调用
 * @author Edmond
 *
 */public class UserSubjectClickActHandler {	private final Logger logger = LoggerFactory.getLogger(UserSubjectClickActHandler.class);
	@Resource
	private DailySubjectClickService dailySubjectClickService;
	
	@Resource
	private UserStatisticsService userStatisticsService;
	
	@Resource
	private SubjectTagStateService subjectTagStateService;
	
	@Resource
	private SubjectService subjectService;
	
	@Resource
	private SubjectClickService subjectClickService;
		private UserSubjectClickActHandler(){			}		private static class ActHandlerHolder{ 		private static UserSubjectClickActHandler instance =new UserSubjectClickActHandler(); 	} 	/**	 * 获取工厂单例	 * @return	 */	public static UserSubjectClickActHandler getInstance() { 		return ActHandlerHolder.instance; 	}		public void handlerMessage(String message){		logger.info("handle message:"+message);
		UserSubjectClickDTO dto = null;
		try{
			dto = JsonHelper.getDTO(message, UserSubjectClickDTO.class);
			this.handler(dto);
			logger.info("handle message:"+message+" successfully!");
		}catch(Exception ex){
			ex.printStackTrace();
			logger.info("handle message:"+message+" fail!");
		}finally{
			dto = null;
		}	}
	
	public void handler(UserSubjectClickDTO dto){
		/*int incr = dto.getIncr();
		if(ActLoggerDTO.DOWN.equals(dto.getAct())){
			//incr = -1;
			dailySubjectClickService.click(new TimeFragmentPK(dto.getSid()), DailySubjectClick.Field_Down);
			userStatisticsService.increaseUpdate(dto.getUid(), UserStatisticsService.Action_SubjectDown);
		}else{
			//incr = 1;
			dailySubjectClickService.click(new TimeFragmentPK(dto.getSid()), DailySubjectClick.Field_Up);
			userStatisticsService.increaseUpdate(dto.getUid(), UserStatisticsService.Action_SubjectUp);
		}
		List<String> fragments = DateTimeHelper.generateServalDateFormat(new Date());
		SubjectTagState tagState = subjectTagStateService.getById(dto.getSid());
		for(String fragment:fragments){
			SubjectStatisticsFragmentService.getInstance().subjectClickComming(fragment, String.valueOf(dto.getSid()), incr,
					tagState==null?null:tagState.getInnerModels());	
		}*/
		//System.out.println("step 1");
		/*Subject entity = subjectService.getById(dto.getSid());
		if(entity != null){		
			System.out.println("step 1-1");
			long incr = dto.getIncr();
			if(ActLoggerDTO.DOWN.equals(dto.getAct())){
				//incr = -1;
				//subjectClickService.click(dto.getSid(), SubjectClick.Field_Down, incr);
				dailySubjectClickService.click(new TimeFragmentPK(dto.getSid()), DailySubjectClick.Field_Down,incr);
				SubjectBusinessSortedSetService.getInstance().removeSubjectUpUser(String.valueOf(dto.getSid()), String.valueOf(dto.getUid()));
				userStatisticsService.increaseUpdate(dto.getUid(), UserStatisticsService.Action_SubjectDown,(int)incr);
			}else{
				//incr = 1;
				//subjectClickService.click(dto.getSid(), SubjectClick.Field_Up, incr);
				dailySubjectClickService.click(new TimeFragmentPK(dto.getSid()), DailySubjectClick.Field_Up,incr);
				SubjectBusinessSortedSetService.getInstance().addSubjectUpUser(String.valueOf(dto.getSid()), String.valueOf(dto.getUid()));
				userStatisticsService.increaseUpdate(dto.getUid(), UserStatisticsService.Action_SubjectUp,(int)incr);
			}
			System.out.println("step 1-2");
			List<String> fragments = DateTimeHelper.generateServalDateFormat(new Date());
			//System.out.println("step 1-2-1");
			SubjectTagState tagState = subjectTagStateService.getById(dto.getSid());
			//System.out.println("step 1-2-2");
			try{
				for(String fragment:fragments){
					SubjectStatisticsFragmentService.getInstance().subjectClickComming(fragment, String.valueOf(dto.getSid()), incr,
							tagState==null?null:tagState.getInnerModels(), HttpHelper.parseAllDomains(entity.getUrl()));	
				}
			}catch(Exception ex){
				ex.printStackTrace(System.out);
			}
			
			System.out.println("step 1-3");
			//更新文章索引
			subjectIndexFacadeService.createSubjectIndex(entity);
			System.out.println("step 1-4");
		}*/
		
		System.out.println("handler UserSubjectClickDTO successfully!");
	}}


