package com.bhu.vas.plugins.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 被Spring的Quartz MethodInvokingJobDetailFactoryBean定时执行的普通Spring Bean.
 */
public class RetryInitChecker {
	private static Logger logger = LoggerFactory.getLogger(RetryInitChecker.class);
	
	//@Resource
	//private RetryLogService retryLogService;
	
	public void execute() {
		logger.info("RetryInitChecker starting...");
		logger.info("RetryInitChecker ended");
		/*EntityBatchIterator<Integer, RetryLog> it = new KeyBasedEntityBatchIterator<Integer, RetryLog>(
				Integer.class, RetryLog.class, retryLogService.getEntityDao(),
				1000, new String[]{"status"},new String[]{RetryLogHelper.unsolved()});
		
        while (it.hasNext()) {
            List<RetryLog> list = it.next();
            for(RetryLog log : list){
            	try{
            		this.processLog(log);
            		this.updateRetryStatus(log);
            	}catch(Exception ex){
            		//ex.printStackTrace();
            		logger.error(String.format("[%s] exception catched when access for retrylog id [%s]",
            				this.getClass().getSimpleName(), log.getId()), ex);
            		continue;
            	}
            }
        }*/
	}
/*	
	public void processLog(RetryLog log){
		//以后可以修改为动态或配置,目前就只有SNS有RETRY，先暂时不做判断
		//if(RetryLogHelper.getActionClassName(log.getType()).equals("AbstractSNS")){	
		//}
		this.processSNSRetry(log);
	}
	
	public void processSNSRetry(RetryLog log){
		String actiontype = RetryLogHelper.getActionTypeName(log.getType());
		AbstractSNS message = SNSFactoryBuilder.fromJsonHasActionType(log.getMessage(), actiontype);
		
		
    	SNSQueueAction action = new SNSQueueAction(applicationSupport, 
    			bindManager, snsFriendService, userSnsStateService, 
    			snsMuiscService, userFriendsStateService, userFriendsService){
    	    public void fail(final AbstractSNS message, Exception e){
    	    	super.fail(message, e);
    	    	logger.error(String.format("exception catched when access [%s] for message [%s]", 
    	    			this.getClass().getName(), e.getMessage()), e);
    	    	
    	    	retryFail();
    	    }
    	};
    	//同步分享
    	if(SNSActionType.TC.toString().equals(actiontype)){
    		action.syncshare(message);
    	}
    	//SNS好友存储并入驻好友
    	else if(SNSActionType.SF.toString().equals(actiontype)){
    		action.snsFriends(message);
    	}
    	//SMS音乐相关搜索
    	else if(SNSActionType.MP.toString().equals(actiontype)){
    		action.snsMusicInfo(message);
    	}
	}*/
	
	
	/*protected void updateRetryStatus(RetryLog log){
		log.setStatus(RetryLogHelper.success());
		retryLogService.update(log);
	}
	
	protected void retryFail(){
		throw new RuntimeException();
	}*/
	
	
}
