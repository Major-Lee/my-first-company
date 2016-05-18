package com.bhu.vas.business.asyn.spring.activemq.service.async;

import javax.annotation.Resource;

import com.bhu.vas.business.asyn.spring.activemq.queue.producer.async.AsyncDeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchImportConfirmDTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchSharedealModifyDTO;
import com.bhu.vas.business.asyn.spring.model.async.group.OperGroupDTO;
import com.bhu.vas.business.asyn.spring.model.async.tag.OperTagDTO;


public class AsyncDeliverMessageService {
	
	@Resource(name="asyncDeliverMessageQueueProducer")
	private AsyncDeliverMessageQueueProducer asyncDeliverMessageQueueProducer;

	public void sendPureText(String message){
		asyncDeliverMessageQueueProducer.sendPureText(message);
	}
	public void sendBatchImportConfirmActionMessage(int uid,String batchno){
		BatchImportConfirmDTO dto = new BatchImportConfirmDTO();
		dto.setUid(uid);
		dto.setBatchno(batchno);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendBatchSharedealModifyActionMessage(int uid,String message,
			Boolean cbto,Boolean el,boolean customized,String owner_percent,
			String rcm,String rcp,String ait){
		BatchSharedealModifyDTO dto = new BatchSharedealModifyDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setCbto(cbto);
		dto.setEl(el);
		dto.setCustomized(customized);
		dto.setOwner_percent(owner_percent);
		dto.setRcm(rcm);
		dto.setRcp(rcp);
		dto.setAit(ait);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sentDeviceBatchBindTagActionMessage(int uid,String message ,String tag){
		OperTagDTO dto = new OperTagDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setTag(tag);
		dto.setDtoType(IDTO.ACT_ADD);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}

	public void sentDeviceBatchDelTagActionMessage(int uid,String message){
		OperTagDTO dto = new OperTagDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setDtoType(IDTO.ACT_DELETE);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sentBatchGroupCmdsActionMessage(int uid , String message ,String opt, String subopt, String extparams){
		OperGroupDTO dto = new OperGroupDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setOpt(opt);
		dto.setSubopt(subopt);
		dto.setExtparams(extparams);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
}
