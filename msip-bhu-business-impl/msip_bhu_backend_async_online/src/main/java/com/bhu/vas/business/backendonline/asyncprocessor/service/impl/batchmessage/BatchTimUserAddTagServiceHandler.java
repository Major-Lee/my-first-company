package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchmessage;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
		String sig = userTagdto.getSig();
		String utype = userTagdto.getUtype();
		Integer channel = userTagdto.getChannel();
		boolean newly = userTagdto.isNewly();
		
		ayscTimeUserAddTag(acc, sig, utype, channel, newly);
		
		logger.info(String.format("BatchTimUserAddTagServiceHandler process message[%s] successful", message));
	}
	private void ayscTimeUserAddTag(String acc, String sig, String utype, Integer channel, boolean newly) {
		if (acc == null || utype == null || sig == null) return;
		String tags = utype+channel.intValue();
		int sync = 0, register = 0;
		MessageUser user = null;
		TimResponseBasicDTO ret_dto = null;
		try{
			if (newly){
				ret_dto = MessageTimHelper.CreateTimAccoutImportUrlCommunication(acc);
				if(ret_dto.isExecutedSuccess()){
					register = 1;
					logger.info(String.format("BatchTimUserAddTagServiceHandler import user[%s]"
							+ " successful", acc));
				}else{
					logger.info(String.format("BatchTimUserAddTagServiceHandler import user[%s]"
							+ " failed[%s]！", acc, ret_dto.getErrorInfo()));
				}
			}
			
			ret_dto = MessageTimHelper.CreateTimAddTagUrlCommunication(acc, tags);
			if(ret_dto.isExecutedSuccess()){
				sync = 1;
				logger.info(String.format("BatchTimUserAddTagServiceHandler add tag user[%s]"
						+ " tag[%s] successful", acc, tags));
			}else{
				logger.info(String.format("BatchTimUserAddTagServiceHandler add tag user[%s]"
						+ " tag[%s] failed[%s]!", acc, tags, ret_dto.getErrorInfo()));
			}
		}catch(Exception e){
			System.out.println(e);
		}finally {
			if (utype.equals(BusinessKeyDefine.Message.User)){
				user = messageUserFacadeService.validate(acc);
				if (user == null){
					user = new MessageUser();
					user.setId(acc);
				}
				if (newly)
					user.setRegister(register);
				user.setSig(sig);
				user.setSync(sync);
				user.setExtension_content(replaceTags(user.getExtension_content(),tags));
				messageUserFacadeService.updateMessageUserData(user);
			}
		}
		
	}
	
	private String replaceTags(String oldTags, String newTag){
		if (StringUtils.isEmpty(oldTags))
			return newTag;
		String[] split = oldTags.split(",");
		for (String tag : split){
			if(tag.equals(newTag)){
				return  oldTags;
			}
		}
		return oldTags.concat(",").concat(newTag);
	}
	
}
