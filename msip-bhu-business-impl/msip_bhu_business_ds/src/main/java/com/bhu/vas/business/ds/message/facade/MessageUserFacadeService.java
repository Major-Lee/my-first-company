package com.bhu.vas.business.ds.message.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.ds.message.service.MessageUserService;

@Service
public class MessageUserFacadeService {
	@Resource
    private MessageUserService messageUserService;
	
	public void updateMessageUserData(MessageUser user){
		MessageUser orgUser = messageUserService.getById(user.getId());
		if (orgUser == null){
			messageUserService.insert(user);
		}else{
			messageUserService.update(user);
		}
	}
	
	public MessageUser validate(String id){
		return messageUserService.getById(id);
	}
	
}
