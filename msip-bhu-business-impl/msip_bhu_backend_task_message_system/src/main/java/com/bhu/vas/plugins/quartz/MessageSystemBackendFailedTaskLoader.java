package com.bhu.vas.plugins.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.message.dto.MessageSystemFailTaskDTO;
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.business.bucache.redis.serviceimpl.message.MessageSystemFailedTaskListService;
import com.smartwork.msip.cores.helper.JsonHelper;

public class MessageSystemBackendFailedTaskLoader {
	
	private static Logger logger = LoggerFactory.getLogger(MessageSystemBackendFailedTaskLoader.class);
	
	public void execute(){
		logger.info("MessageSystemBackendFailedTaskLoader start...");
		boolean flag = true;
		while(flag){
			String fail = MessageSystemFailedTaskListService.getInstance().lpopFailedTaskKey();
			MessageSystemFailTaskDTO dto = JsonHelper.getDTO(fail, MessageSystemFailTaskDTO.class);
			if (StringUtils.isEmpty(dto.getUrl()) || StringUtils.isEmpty(dto.getMessage())){
				flag = false;
			}else{
				try {
					TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimUrlCommunication(dto.getUrl(), dto.getMessage());
					if (ret_dto.isExecutedSuccess()){
						logger.info(String.format("FailedTaskBackend exe message[%s] successful", fail));
					}
				} catch (Exception e) {
					logger.error(String.format("FailedTaskBackend exe message[%s] failed",fail));
					System.out.println(e);
				}
			}
		}
		logger.info("MessageSystemBackendFailedTaskLoader end...");
	}
}
