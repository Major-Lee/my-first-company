package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchgroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.asyn.spring.model.async.group.OperGroupDTO;
import com.bhu.vas.business.asyn.spring.model.async.tag.OperTagDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.smartwork.msip.cores.helper.JsonHelper;

public class BatchGroupCmdsServiceHandler implements IMsgHandlerService{
	private final Logger logger = LoggerFactory.getLogger(BatchGroupCmdsServiceHandler.class);
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final OperGroupDTO operGroupDto = JsonHelper.getDTO(message, OperGroupDTO.class);
		
	}

}
