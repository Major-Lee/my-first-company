package com.bhu.vas.business.ds.message.facade;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.ds.message.service.MessageUserService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;

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
		MessageUser user = messageUserService.getById(id);
		if (user == null) return null;
		return messageUserService.getById(id);
	}
	
	public List<MessageUser> findMessageUsersByParams(String param, int status,
			int pageNo, int pageSize){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		if (!StringUtils.isEmpty(param))
			criteria.andColumnEqualTo(param, status);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return messageUserService.findModelByModelCriteria(mc);
	}
	
	public int countMessageUsersByParams(String param, int status){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		if (!StringUtils.isEmpty(param))
			criteria.andColumnEqualTo(param, status);
		return messageUserService.countByModelCriteria(mc);
	}
	
	public void updateRegisterStatus(List<String> accs, int register){
		List<MessageUser> ids = messageUserService.findByIds(accs);
		for(MessageUser user : ids){
			user.setRegister(register);
		}
		messageUserService.updateAll(ids);
	}
	
	public void updateSyncStatus(MessageUser user, int sync){
		if (user != null){
			user.setSync(sync);
			messageUserService.update(user);
		}
	}
	
}
