package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.Collections;
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

public class MessageSystemRegisterBackendTaskLoader {
	
	private static Logger logger = LoggerFactory.getLogger(MessageSystemRegisterBackendTaskLoader.class);
	@Resource
	private MessageUserFacadeService messageUserFacadeService;
	
	private ThreadPoolExecutor exec_processes = null;
	private static final int pageSize = 100;
	//腾讯调用接口最大100次/s
	private static final int registerCycle = 120;
	
	public void execute(){
		logger.info("MessageSystemRegisterBackendTaskLoader start...");
		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"导入腾讯im用户处理",10);
		int total = messageUserFacadeService.countMessageUsersByParams(0, null);
		if (total > 0){
			//腾讯im 接口导入用户一次100个
			int pageCount= total / pageSize;
			if (total % pageSize != 0){
				pageCount++;
			}
			List<String> accList = Collections.emptyList();
			for (int page = 1; page <= pageCount; page++ ){
				List<MessageUser> users = messageUserFacadeService.findMessageUsersByParams(0, null, page, pageSize);
				accList = new ArrayList<String>();
				for(MessageUser user : users){
					accList.add(user.getId());
				}
				onProcessor(accList);
			}
		}
		
		logger.info("MessageSystemRegisterBackendTaskLoader end...");
	}
	public void onProcessor(final List<String> accs){
		exec_processes.submit((new Runnable() {
			@Override
			public void run() {
				try{
					TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimMULAccoutImportUrlCommunication(accs);
					messageUserFacadeService.updateRegisterStatus(accs, 1);
					if (!ret_dto.getFailAccounts().isEmpty()){
						messageUserFacadeService.updateRegisterStatus(ret_dto.getFailAccounts(), 0);
					}
					Thread.sleep(registerCycle);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("Import tim onProcessor", ex);
				}
			}
		}));
	}
}
