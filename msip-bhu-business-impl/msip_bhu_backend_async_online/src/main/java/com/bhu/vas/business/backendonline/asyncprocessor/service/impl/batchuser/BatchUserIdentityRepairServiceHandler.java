package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchuser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.business.asyn.spring.model.async.user.UserIdentityRepairDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.tag.service.TagGroupHandsetDetailService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
public class BatchUserIdentityRepairServiceHandler implements IMsgHandlerService{
	private final Logger logger = LoggerFactory.getLogger(BatchUserIdentityRepairServiceHandler.class);
	
	@Resource
	private TagGroupHandsetDetailService tagGroupHandsetDetailService;
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final UserIdentityRepairDTO userIdentityRepairDTO = JsonHelper.getDTO(message, UserIdentityRepairDTO.class);
		String hdmac = userIdentityRepairDTO.getHdmac();
		String mobileno = userIdentityRepairDTO.getMobileno();
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("hdmac", hdmac);
		List<TagGroupHandsetDetail> list = tagGroupHandsetDetailService.findModelByModelCriteria(mc);
		List<TagGroupHandsetDetail> updateList = new ArrayList<TagGroupHandsetDetail>();	
		for(TagGroupHandsetDetail entity : list){
			if(entity.getMobileno() == null){
				entity.setMobileno(mobileno);
				updateList.add(entity);
			}
		}
		if(!updateList.isEmpty()){
			tagGroupHandsetDetailService.updateAll(updateList);
		}
		
		logger.info(String.format("process message[%s] successful", message));
	}

}
