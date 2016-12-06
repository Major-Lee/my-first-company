package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchmessage;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.asyn.spring.model.async.message.AsyncTimUserAddTagDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
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
		String utype = userTagdto.getUtype();
		Integer channel = userTagdto.getChannel();
		
		ayscTimeUserAddTag(acc, utype, channel);
		
		logger.info(String.format("BatchTimUserAddTagServiceHandler process message[%s] successful", message));
	}
	private void ayscTimeUserAddTag(String acc, String utype, Integer channel) {
		MessageUser user = messageUserFacadeService.validate(acc);
		String tags = utype+channel.intValue();
		if (utype.equals(BusinessKeyDefine.Message.User)){
			if (user == null){
				user = new MessageUser();
				user.setId(acc);
				user.setExtension_content(tags);
			}else{
				String oldTags = user.getExtension_content();
				user.setExtension_content(replaceTags(oldTags, tags));
			}
		}
		TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimAddTagUrlCommunication(acc, tags);
		if (ret_dto.isExecutedSuccess()){
			user.setSync(1);
		}else{
			user.setSync(0);
			logger.info(String.format("ayscTimeUserAddTag tim add user[%s] tags[%s] failed!", acc, tags));
		}
		messageUserFacadeService.updateMessageUserData(user);
	}
	
	private String replaceTags(String oldTags, String newTag){
		String[] split = oldTags.split(",");
		for (String tag : split){
			if(tag.equals(newTag)){
				return  oldTags;
			}
		}
		return oldTags.concat(",").concat(newTag);
	}
	
}
