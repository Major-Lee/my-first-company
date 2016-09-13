package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchgroup;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.tag.model.TagGroupSortMessage;
import com.bhu.vas.business.asyn.spring.model.async.group.BatchGroupSendSortMessageDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.tag.service.TagGroupSortMessageService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

@Service
public class BatchGroupSendSortMessageServiceHandler implements IMsgHandlerService{
	private final Logger logger = LoggerFactory.getLogger(BatchGroupSendSortMessageServiceHandler.class);
	
	@Resource
	private TagGroupSortMessageService tagGroupSortMessageService;
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchGroupSendSortMessageDTO dto = JsonHelper.getDTO(message, BatchGroupSendSortMessageDTO.class);
		int taskid = dto.getTaskid();
		String orderid = dto.getOrderid();
		
		TagGroupSortMessage entity = tagGroupSortMessageService.getById(taskid);
		entity.setGoodsid(orderid);
		entity.setState(TagGroupSortMessage.doing);
		
		System.out.println(JsonHelper.getJSONString(entity));
		tagGroupSortMessageService.update(entity);
		System.out.println("222222222");
		
		String smsg = String.format(BusinessRuntimeConfiguration.Internal_group_Template, entity.getContext());
		
		Set<String> accSet = entity.getInnerModels();
		String[] accs = new String[accSet.size()];

		int i = 0;
		for(String acc : accSet){
			accs[i] = acc;
			i++;
		}
		
		if(StringUtils.isNotEmpty(smsg)){
			String response = SmsSenderFactory.buildSender(
				BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, accs);
			logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",accs.toString(),smsg,response));
		}
		entity.setState(TagGroupSortMessage.done);
		tagGroupSortMessageService.update(entity);
		logger.info(String.format("process message[%s] successful", message));
	}
}
