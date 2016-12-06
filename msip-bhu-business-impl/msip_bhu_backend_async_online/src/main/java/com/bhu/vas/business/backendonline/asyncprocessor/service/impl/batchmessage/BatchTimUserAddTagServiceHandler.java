package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchmessage;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.asyn.spring.model.async.message.AsyncTimUserAddTagDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.message.facade.MessageUserFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class BatchTimUserAddTagServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchTimUserAddTagServiceHandler.class);
	
	@Resource
	private MessageUserFacadeService messageUserFacadeService;
	@Override
	public void process(String message) {
		logger.info(String.format("BatchTimUserAddTagServiceHandler process message[%s]", message));
		final AsyncTimUserAddTagDTO userTagdto = JsonHelper.getDTO(message, AsyncTimUserAddTagDTO.class);
		String acc = userTagdto.getAcc();
		String tags = userTagdto.getTags();
		String sig = userTagdto.getSig();
		ayscTimeUserAddTag(acc, tags, sig);
		
		logger.info(String.format("BatchTimUserAddTagServiceHandler process message[%s] successful", message));
	}
	private void ayscTimeUserAddTag(String acc, String tags, String sig) {
		MessageUser user = messageUserFacadeService.validate(acc);
		if (user == null){
			user = new MessageUser();
			user.setId(acc);
			user.setExtension_content(tags);
		}
		user.setSig(sig);
		TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimAddTagUrlCommunication(acc, tags);
		if (ret_dto.isExecutedSuccess()){
			user.setSync(1);
		}else{
			user.setSync(0);
		}
		messageUserFacadeService.updateMessageUserData(user);
	}
	
	
	
}
