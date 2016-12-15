package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchmessage;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.asyn.spring.model.async.message.AsyncTimUserRegisterDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.ds.message.facade.MessageUserFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class BatchTimUserRegisterServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchTimUserRegisterServiceHandler.class);
	
	@Resource
	private MessageUserFacadeService messageUserFacadeService;
	@Override
	public void process(String message) {
		logger.info(String.format("BatchTimUserRegisterServiceHandler process message[%s]", message));
		final AsyncTimUserRegisterDTO timUserRegisterDTO = JsonHelper.getDTO(message, AsyncTimUserRegisterDTO.class);
		String user = timUserRegisterDTO.getUser();
		String utype = timUserRegisterDTO.getUtype();
		ayscTimeUserImport(user, utype);
		logger.info(String.format("BatchTimUserRegisterServiceHandler process message[%s] successful", message));
	}
	
	private void ayscTimeUserImport(String uname, String utype) {
		if (uname == null || utype == null) return;
		TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimAccoutImportUrlCommunication(uname);
		int register = 0;
		if (ret_dto.isExecutedSuccess()){
			register = 1;
			logger.info(String.format("ayscTimeUserImport user[%s] successful", uname));
		}else{
			logger.info(String.format("ayscTimeUserImport user[%s] failed[%s]", uname, ret_dto.getErrorInfo()));
		}
		if (utype.equals(BusinessKeyDefine.Message.User)){
			MessageUser user = messageUserFacadeService.validate(uname);
			if (user == null){
				user = new MessageUser();
				user.setId(uname);
			}
			user.setRegister(register);
			messageUserFacadeService.updateMessageUserData(user);
		}
	}
}
