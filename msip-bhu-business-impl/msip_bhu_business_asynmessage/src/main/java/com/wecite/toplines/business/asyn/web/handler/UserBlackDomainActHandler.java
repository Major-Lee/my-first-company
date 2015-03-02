
package com.wecite.toplines.business.asyn.web.handler;import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.subject.model.Subject;
import com.bhu.vas.api.subject.model.SubjectTagState;
import com.bhu.vas.api.subject.model.SubjectWeixinShareClick;
import com.bhu.vas.business.subject.service.SubjectService;
import com.bhu.vas.business.subject.service.SubjectTagStateService;
import com.bhu.vas.business.subject.service.SubjectWeixinShareClickService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.IdHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.encrypt.MD5Helper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.wecite.toplines.business.asyn.web.model.UserBlackDomainDTO;
import com.wecite.toplines.business.bucache.redis.serviceimpl.statistics.SubjectStatisticsFragmentService;

/**
 * @author Edmond
 *
 */public class UserBlackDomainActHandler {	private final Logger logger = LoggerFactory.getLogger(UserBlackDomainActHandler.class);
	@Resource
	private SubjectService subjectService;
	@Resource
	private SubjectTagStateService subjectTagStateService;
	
	@Resource
	private SubjectWeixinShareClickService subjectWeixinShareClickService;
	
	private UserBlackDomainActHandler(){			}		private static class ActHandlerHolder{ 		private static UserBlackDomainActHandler instance =new UserBlackDomainActHandler(); 	} 	/**	 * 获取工厂单例	 * @return	 */	public static UserBlackDomainActHandler getInstance() { 		return ActHandlerHolder.instance; 	}		public void handlerMessage(String message){		logger.info("handle message:"+message);
		UserBlackDomainDTO dto = null;
		try{
			dto = JsonHelper.getDTO(message, UserBlackDomainDTO.class);
			if(dto.isAdd()){
				this.handlerMessageByAddBlackList(dto);
			}else{
				this.handlerMessageByRemoveBlackList(dto);
			}
			logger.info("handle message:"+message+" successfully!");
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.info("handle message:"+message+" fail!");
		}finally{
			dto = null;
		}	}
	/**
	 * 增加黑名单的handle
	 * @param dto
	 */
	protected void handlerMessageByAddBlackList(UserBlackDomainDTO dto){
		/*if(dto.isSingle()){
			Subject entity = subjectService.getByUrlMD5(MD5Helper.md5(dto.getUrl()));
			if(entity != null){
				this.addBlackBySubject(entity);
				subjectIndexFacadeService.updateVisibleState(entity.getId(),Subject.VisibleState_Inblack);
			}
		}else{
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("domain", dto.getDomain()).andColumnNotEqualTo("visible_state", Subject.VisibleState_Inblack);
			mc.setPageNumber(1);
			mc.setPageSize(200);
			mc.setOrderByClause("id desc");
			
			do{   
			    List<Subject> ops = subjectService.findModelByModelCriteria(mc);
				if(ops.isEmpty()) break;
				for(Subject op:ops){
					this.addBlackBySubject(op);
				}
				//更新索引
				subjectIndexFacadeService.updateVisibleStates(IdHelper.getIntIds(ops),Subject.VisibleState_Inblack);
			}while(true);
		}*/
	}
	/**
	 * 取消黑名单的handle
	 * @param dto
	 */
	protected void handlerMessageByRemoveBlackList(UserBlackDomainDTO dto){
		/*if(dto.isSingle()){
			Subject entity = subjectService.getByUrlMD5(MD5Helper.md5(dto.getUrl()));
			if(entity != null){
				this.removeBlackBySubject(entity);
				subjectIndexFacadeService.updateVisibleState(entity.getId(),Subject.VisibleState_Normal);
			}
		}else{
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("domain", dto.getDomain()).andColumnEqualTo("visible_state", Subject.VisibleState_Inblack);
			mc.setPageNumber(1);
			mc.setPageSize(200);
			mc.setOrderByClause("id desc");
			
			do{   
			    List<Subject> ops = subjectService.findModelByModelCriteria(mc);
				if(ops.isEmpty()) break;
				for(Subject op:ops){
					this.removeBlackBySubject(op);
				}
				//更新索引
				subjectIndexFacadeService.updateVisibleStates(IdHelper.getIntIds(ops),Subject.VisibleState_Normal);
			}while(true);
		}*/
	}
	/**
	 * 把文章处理成黑名单的相关逻辑操作
	 * @param entity
	 */
	protected void addBlackBySubject(Subject entity){
		try{
			entity.setVisible_state(Subject.VisibleState_Inblack);
			subjectService.update(entity);
			//微信分享的url如果存在 也设置为黑名单
			SubjectWeixinShareClick subject_wx_click_entity = subjectWeixinShareClickService.getById(entity.getUrl_md5());
			if(subject_wx_click_entity != null){
				subject_wx_click_entity.setVisible_state(SubjectWeixinShareClick.VisibleState_Inblack);
				subjectWeixinShareClickService.update(subject_wx_click_entity);
			}
			
			Date current = new Date();
			List<String> fragments = DateTimeHelper.generateServalDateFormat(current);
			//System.out.println("step 1-2-1");
			SubjectTagState tagState = subjectTagStateService.getById(entity.getId());
			String sid_str = String.valueOf(entity.getId());
			//本日，周，月，年,域名，子域名的相关数据 redis删除
			for(String fragment:fragments){
				SubjectStatisticsFragmentService.getInstance().clearAllSubjectClick(fragment, sid_str,
						tagState==null?null:tagState.getInnerModels(),HttpHelper.parseAllDomains(entity.getUrl()));
			}
			//昨日，上周，上月，去年的相关数据 redis删除
			{
				List<String> previous_fragment = new ArrayList<String>();
				String yesterday = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1), DateTimeHelper.FormatPattern5);
				String previousweek = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(7), DateTimeHelper.FormatPattern12);
				String previousmonth = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfPreviousMonth(), DateTimeHelper.FormatPattern11);
				Calendar calendar = Calendar.getInstance();    
				calendar.set(Calendar.YEAR, -1);	//减一个月，变为上月的1号
		        calendar.set(Calendar.DATE, 1);    	// 设为当前月的1号    
		        calendar.set(Calendar.MONTH, 1);  	// 设为当前年的1号
		        String previousyear = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfPreviousMonth(), DateTimeHelper.FormatPattern10);
		        previous_fragment.add(yesterday);
		        previous_fragment.add(previousweek);
		        previous_fragment.add(previousmonth);
		        previous_fragment.add(previousyear);
		        for(String fragment:previous_fragment){
					SubjectStatisticsFragmentService.getInstance().clearAllSubjectClick(fragment, sid_str,
							tagState==null?null:tagState.getInnerModels(),HttpHelper.parseAllDomains(entity.getUrl()));
				}
			}
			//TODO:TBD日，周，月，年的相关数据库click数据的状态位 mysql，暂时用不上,
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	/**
	 * 把文章从黑名单中排除的相关逻辑操作
	 * @param entity
	 */
	protected void removeBlackBySubject(Subject entity){
		try{
			entity.setVisible_state(Subject.VisibleState_Normal);
			subjectService.update(entity);
			
			//微信分享的url如果存在 也取消黑名单
			SubjectWeixinShareClick subject_wx_click_entity = subjectWeixinShareClickService.getById(entity.getUrl_md5());
			if(subject_wx_click_entity != null){
				subject_wx_click_entity.setVisible_state(SubjectWeixinShareClick.VisibleState_Normal);
				subjectWeixinShareClickService.update(subject_wx_click_entity);
			}
			
			
			List<String> fragments = DateTimeHelper.generateServalDateFormat(new Date());
			//System.out.println("step 1-2-1");
			SubjectTagState tagState = subjectTagStateService.getById(entity.getId());
			//System.out.println("step 1-2-2");
		
			for(String fragment:fragments){
				SubjectStatisticsFragmentService.getInstance().subjectClickComming(fragment, String.valueOf(entity.getId()), 1,
						tagState==null?null:tagState.getInnerModels(), HttpHelper.parseAllDomains(entity.getUrl()));	
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
}


