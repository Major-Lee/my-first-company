package com.bhu.vas.business.asyn.spring.activemq.service.async;

import javax.annotation.Resource;

import com.bhu.vas.business.asyn.spring.activemq.queue.producer.DeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.BatchImportConfirmDTO;
import com.bhu.vas.business.asyn.spring.model.BatchSharedealModifyDTO;


public class AsyncDeliverMessageService {
	
	@Resource(name="deliverAsyncMessageQueueProducer")
	private DeliverMessageQueueProducer deliverMessageQueueProducer;

	public void sendPureText(String message){
		deliverMessageQueueProducer.sendPureText(message);
	}
	public void sendBatchImportConfirmActionMessage(int uid,String batchno){
		BatchImportConfirmDTO dto = new BatchImportConfirmDTO();
		dto.setUid(uid);
		dto.setBatchno(batchno);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendBatchSharedealModifyActionMessage(int uid,String message,
			Boolean cbto,Boolean el,double owner_percent,
			String rcm,String rcp,String ait){
		BatchSharedealModifyDTO dto = new BatchSharedealModifyDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setCbto(cbto);
		dto.setEl(el);
		dto.setOwner_percent(owner_percent);
		dto.setRcm(rcm);
		dto.setRcp(rcp);
		dto.setAit(ait);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
}
