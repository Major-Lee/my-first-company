package com.bhu.vas.plugins.quartz;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.ds.message.facade.MessageUserFacadeService;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

public class MessageSystemTagBackendTaskLoader {
	
	private static Logger logger = LoggerFactory.getLogger(MessageSystemTagBackendTaskLoader.class);
	@Resource
	private MessageUserFacadeService messageUserFacadeService;
	
	private ThreadPoolExecutor exec_processes = null;
	private static final int pageSize = 100;
	
	public void execute(){
		logger.info("MessageSystemTagBackendTaskLoader start...");
		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"导入腾讯im用户处理",10);
		int total = messageUserFacadeService.countMessageUsersByParams("sync", 0);
		if (total > 0){
			//腾讯im 一次给100个用户添加标签
			int pageCount= total / pageSize;
			if (total % pageSize != 0){
				pageCount++;
			}
			for (int page = 1; page <= pageCount; page++ ){
				List<MessageUser> users = messageUserFacadeService.findMessageUsersByParams("sync", 0, page, pageSize);
				onProcessor(users);
			}
		}
		
		logger.info("MessageSystemTagBackendTaskLoader end...");
	}
	public void onProcessor(final List<MessageUser> users){
		exec_processes.submit((new Runnable() {
			@Override
			public void run() {
				try{
					for (MessageUser user : users){
						TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimAddTagUrlCommunication(user.getId(), user.getExtension_content());
						if (ret_dto.getErrorCode() == 0){
							messageUserFacadeService.updateSyncStatus(user, 1);
							logger.info(String.format("onProcessor messageUser[%s] addtag[%s] successful.", 
									user.getId(), user.getExtension_content()));
						}else{
							logger.info(String.format("onProcessor messageUser[%s] addtag[%s] failed[%s].", 
									user.getId(), user.getExtension_content(), ret_dto.getErrorInfo()));
						}
					}
						
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("add tag onProcessor", ex);
				}
			}
		}));
	}
}
