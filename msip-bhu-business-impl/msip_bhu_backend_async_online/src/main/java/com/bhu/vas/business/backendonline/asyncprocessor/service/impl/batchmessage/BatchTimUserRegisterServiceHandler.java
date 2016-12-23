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
import com.bhu.vas.business.bucache.redis.serviceimpl.message.MessageSystemHashService;
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
		String sig = timUserRegisterDTO.getSig();
		Integer channel = timUserRegisterDTO.getChannel();
		ayscTimeUserImport(user, utype, sig, channel);
		logger.info(String.format("BatchTimUserRegisterServiceHandler process message[%s] successful", message));
	}
	
	private void ayscTimeUserImport(String acc, String utype, String sig, Integer channel) {
		if (acc == null || utype == null || sig == null || channel == null ) return;
		String tags = utype+channel.intValue();
		int sync = 0, register = 0;
		MessageUser user = null;
		TimResponseBasicDTO ret_dto = null;
		try{
			ret_dto = MessageTimHelper.CreateTimAccoutImportUrlCommunication(acc);
			if(ret_dto.isExecutedSuccess()){
				register = 1;
				logger.info(String.format("BatchTimUserRegisterServiceHandler import user[%s]"
						+ " successful", acc));
			}else{
				logger.info(String.format("BatchTimUserRegisterServiceHandler import user[%s]"
						+ " failed[%s]！", acc, ret_dto.getErrorInfo()));
			}
			
			ret_dto = MessageTimHelper.CreateTimAddTagUrlCommunication(acc, tags);
			if(ret_dto.isExecutedSuccess()){
				sync = 1;
				logger.info(String.format("BatchTimUserRegisterServiceHandler add tag user[%s]"
						+ " tag[%s] successful", acc, tags));
			}else{
				logger.info(String.format("BatchTimUserRegisterServiceHandler add tag user[%s]"
						+ " tag[%s] failed[%s]!", acc, tags, ret_dto.getErrorInfo()));
			}
			
			if (utype.equals(BusinessKeyDefine.Message.User)){
				user = messageUserFacadeService.validate(acc);
				if (user == null){
					user = new MessageUser();
					user.setId(acc);
				}
				user.setRegister(register);
				user.setSig(sig);
				user.setSync(sync);
				user.setExtension_content(MessageTimHelper.replaceTags(user.getExtension_content(),tags));
				messageUserFacadeService.updateMessageUserData(user);
				logger.info(String.format("MessageUser update user[%s] tags[%s] "
						+ "sync[%s] register[%s]",user.getId(), user.getExtension_content(),
						user.getSync(), user.getRegister()));
			}
			//将用户tag数据放入redis
			String oldTags = MessageSystemHashService.getInstance().fetchMessageUserTag(acc, utype);
			MessageSystemHashService.getInstance().setMessageUserTag(acc, utype, MessageTimHelper.replaceTags(oldTags, tags));
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
