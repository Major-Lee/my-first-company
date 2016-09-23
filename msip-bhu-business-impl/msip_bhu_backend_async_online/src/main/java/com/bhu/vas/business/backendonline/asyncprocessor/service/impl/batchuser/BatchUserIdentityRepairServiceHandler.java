package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchuser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.asyn.spring.model.async.user.UserIdentityRepairDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.tag.service.TagGroupHandsetDetailService;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
public class BatchUserIdentityRepairServiceHandler implements IMsgHandlerService{
	private final Logger logger = LoggerFactory.getLogger(BatchUserIdentityRepairServiceHandler.class);
	
	@Resource
	private TagGroupHandsetDetailService tagGroupHandsetDetailService;
	
	@Resource
	private UserIdentityAuthService userIdentityAuthService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final UserIdentityRepairDTO userIdentityRepairDTO = JsonHelper.getDTO(message, UserIdentityRepairDTO.class);
		String hdmac = userIdentityRepairDTO.getHdmac();
		String mobileno = userIdentityRepairDTO.getMobileno();
		
		//1.修复分组终端详情
		repairGroupHandsetDetail(hdmac,mobileno);
		//2. 修复potal身份认证表
		repairUserPortalAuth(hdmac,mobileno);
		
		logger.info(String.format("process message[%s] successful", message));
	}
	
	
	private void repairGroupHandsetDetail(String hdmac,String mobileno){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("hdmac", hdmac);
		List<TagGroupHandsetDetail> list = tagGroupHandsetDetailService.findModelByModelCriteria(mc);
		List<TagGroupHandsetDetail> updateList = new ArrayList<TagGroupHandsetDetail>();	
		for(TagGroupHandsetDetail entity : list){
			if(entity.getMobileno() == null || entity.getMobileno().isEmpty()){
				entity.setMobileno(mobileno);
				updateList.add(entity);
			}
		}
		if(!updateList.isEmpty()){
			tagGroupHandsetDetailService.updateAll(updateList);
		}
	}
	
	private void repairUserPortalAuth(String hdmac,String mobileno){
		UserIdentityAuth auth = userIdentityAuthService.getById(hdmac);
		if(auth == null){
			userIdentityAuthService.generateIdentityAuth(UserIdentityAuth.countrycode, mobileno, hdmac);
		}
	}
}
