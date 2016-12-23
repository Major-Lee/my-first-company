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
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.message.MessageSystemHashService;
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
		if (acc == null || utype == null || channel == null) return;
		String tags = utype+channel.intValue();
		int sync = 0;
		MessageUser user = null;
		TimResponseBasicDTO ret_dto = null;
		try{
			ret_dto = MessageTimHelper.CreateTimAddTagUrlCommunication(acc, tags);
			if(ret_dto.isExecutedSuccess()){
				sync = 1;
				logger.info(String.format("BatchTimUserAddTagServiceHandler add tag user[%s]"
						+ " tag[%s] successful", acc, tags));
			}else{
				logger.info(String.format("BatchTimUserAddTagServiceHandler add tag user[%s]"
						+ " tag[%s] failed[%s]!", acc, tags, ret_dto.getErrorInfo()));
			}
			if (utype.equals(BusinessKeyDefine.Message.User)){
				user = messageUserFacadeService.validate(acc);
				if (user == null){
					user = new MessageUser();
					user.setId(acc);
				}
				user.setSync(sync);
				user.setExtension_content(MessageTimHelper.replaceTags(user.getExtension_content(),tags));
				messageUserFacadeService.updateMessageUserData(user);
			}
			//将用户tag数据放入redis
			String oldTags = MessageSystemHashService.getInstance().fetchMessageUserTag(acc, utype);
			MessageSystemHashService.getInstance().setMessageUserTag(acc, utype, MessageTimHelper.replaceTags(oldTags, tags));
			
		}catch(Exception ex){
			System.out.println(ex);
		}
	}
	

	
}
