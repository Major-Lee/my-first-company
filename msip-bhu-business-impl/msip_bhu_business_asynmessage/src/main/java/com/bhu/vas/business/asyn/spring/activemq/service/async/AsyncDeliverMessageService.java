package com.bhu.vas.business.asyn.spring.activemq.service.async;

import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.SharedNetworkChangeType;
import com.bhu.vas.business.asyn.spring.activemq.queue.producer.async.AsyncDeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchImportConfirmDTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchImportPreCheckDTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchSharedealModifyBySnDTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchSharedealModifyDTO;
import com.bhu.vas.business.asyn.spring.model.async.device.BatchDeviceApplyAdvertiseDTO;
import com.bhu.vas.business.asyn.spring.model.async.group.BatchGroupDeviceSnkApplyDTO;
import com.bhu.vas.business.asyn.spring.model.async.group.BatchGroupSendSortMessageDTO;
import com.bhu.vas.business.asyn.spring.model.async.group.OperGroupDTO;
import com.bhu.vas.business.asyn.spring.model.async.message.AsyncTimUserAddTagDTO;
import com.bhu.vas.business.asyn.spring.model.async.message.AsyncTimUserRegisterDTO;
import com.bhu.vas.business.asyn.spring.model.async.snk.BatchDeviceSnkApplyDTO;
import com.bhu.vas.business.asyn.spring.model.async.snk.BatchDeviceSnkClearDTO;
import com.bhu.vas.business.asyn.spring.model.async.tag.OperTagDTO;
import com.bhu.vas.business.asyn.spring.model.async.user.UserIdentityRepairDTO;


public class AsyncDeliverMessageService {
	
	@Resource(name="asyncDeliverMessageQueueProducer")
	private AsyncDeliverMessageQueueProducer asyncDeliverMessageQueueProducer;

	public void sendPureText(String message){
		asyncDeliverMessageQueueProducer.sendPureText(message);
	}

	public void sendBatchImportPreCheckMessage(int uid,String batchno){
		BatchImportPreCheckDTO dto = new BatchImportPreCheckDTO();
		dto.setUid(uid);
		dto.setBatchno(batchno);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}

	public void sendBatchImportConfirmActionMessage(int uid,String batchno){
		BatchImportConfirmDTO dto = new BatchImportConfirmDTO();
		dto.setUid(uid);
		dto.setBatchno(batchno);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendBatchSharedealModifyActionMessage(int uid,String message,
			Boolean cbto,Boolean el,boolean customized,String owner_percent,String manufacturer_percent,String distributor_percent,String distributor_l2_percent,
			String rcm,String rcp,String ait,/* String channel_lv1, String channel_lv2, */String fait, boolean needCheckBinding){
		BatchSharedealModifyDTO dto = new BatchSharedealModifyDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setCbto(cbto);
		dto.setEl(el);
		dto.setCustomized(customized);
		dto.setOwner_percent(owner_percent);
		dto.setManufacturer_percent(manufacturer_percent);
		dto.setDistributor_percent(distributor_percent);
		dto.setDistributor_l2_percent(distributor_l2_percent);
		dto.setRcm(rcm);
		dto.setRcp(rcp);
		dto.setAit(ait);
		dto.setFait(fait);
		dto.setNeedCheckBinding(needCheckBinding);
//		dto.setChannel_lv1(channel_lv1);
//		dto.setChannel_lv2(channel_lv2);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}

	public void sendBatchSharedealModifyBySnActionMessage(int uid,String macs,
			String owner_percent,String manufacturer_percent,String distributor_percent,String distributor_l2_percent){
		BatchSharedealModifyBySnDTO dto = new BatchSharedealModifyBySnDTO();
		dto.setUid(uid);
		dto.setMacs(macs);
		dto.setOwner_percent(owner_percent);
		dto.setManufacturer_percent(manufacturer_percent);
		dto.setDistributor_percent(distributor_percent);
		dto.setDistributor_l2_percent(distributor_l2_percent);
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
	
	public void sentBatchGroupCmdsActionMessage(int uid , String message ,String opt, String subopt, String extparams, String channel, String channel_taskid){
		OperGroupDTO dto = new OperGroupDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setOpt(opt);
		dto.setSubopt(subopt);
		dto.setExtparams(extparams);
		dto.setChannel(channel);
		dto.setChannel_taskid(channel_taskid);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	/*public void sendUserSingleDeviceSharedNetworkApplyActionMessage(int uid,String snk_type,String template, String mac,boolean onlyindexupdate,char dtoType){
		List<String> dmacs = new ArrayList<String>();
		dmacs.add(mac);
		this.sendUserDeviceSharedNetworkApplyActionMessage(uid, snk_type,template, dmacs, onlyindexupdate, dtoType);
	}*/
	
	public void sendBatchGroupDeviceSnkApplyActionMessage(int uid,String message,String snk_type,String template,char dtoType){
		BatchGroupDeviceSnkApplyDTO dto = new BatchGroupDeviceSnkApplyDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setSnk_type(snk_type);
		dto.setTemplate(template);
		dto.setDtoType(dtoType);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
//	public void sendBatchGroupModifyDeviceSnkActionMessage(int uid,String message,String ssid, int rate, char dtoType){
//		BatchGroupModifyDeviceSnkDTO dto = new BatchGroupModifyDeviceSnkDTO();
//		dto.setUid(uid);
//		dto.setMessage(message);
//		dto.setSsid(ssid);
//		dto.setRate(rate);
//		dto.setDtoType(dtoType);
//		dto.setTs(System.currentTimeMillis());
//		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
//	}
	
	
	
	public void sendBatchDeviceSnkApplyActionMessage(int uid,String snk_type,String template, List<String> dmacs, boolean onlyindexupdate, SharedNetworkChangeType configChanged, char dtoType){
		BatchDeviceSnkApplyDTO dto = new BatchDeviceSnkApplyDTO();
		dto.setUid(uid);
		dto.setSnk_type(snk_type);
		dto.setTemplate(template);
		dto.setMacs(dmacs);
		dto.setOnlyindexupdate(onlyindexupdate);
		dto.setConfigChanged(configChanged);
		dto.setDtoType(dtoType);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}

//	public void sendBatchModifyDeviceSnkActionMessage(int uid,String ssid, int rate, List<String> dmacs, char dtoType){
//		BatchModifyDeviceSnkDTO dto = new BatchModifyDeviceSnkDTO();
//		dto.setUid(uid);
//		dto.setSsid(ssid);
//		dto.setRate(rate);
//		dto.setMacs(dmacs);
//		dto.setDtoType(dtoType);
//		dto.setTs(System.currentTimeMillis());
//		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
//	}

	public void sendBatchDeviceSnkTemplateClearActionMessage(int uid,String snk_type,String template){
		BatchDeviceSnkClearDTO dto = new BatchDeviceSnkClearDTO();
		dto.setUid(uid);
		dto.setSnk_type(snk_type);
		dto.setTemplate(template);
		dto.setTs(System.currentTimeMillis());
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sentGroupSmsActionMessage(int uid , int taskid,String orderid){
		BatchGroupSendSortMessageDTO dto = new BatchGroupSendSortMessageDTO();
		dto.setUid(uid);
		dto.setTaskid(taskid);
		dto.setOrderid(orderid);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendUserIdentityRepariActionMessage(String hdmac,String acc){
		UserIdentityRepairDTO dto = new UserIdentityRepairDTO();
		dto.setHdmac(hdmac);
		dto.setMobileno(acc);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendBatchDeviceApplyAdvertiseActionMessage(List<String> adIds,char dto_type){
		BatchDeviceApplyAdvertiseDTO dto = new BatchDeviceApplyAdvertiseDTO();
		dto.setDtoType(dto_type);
		dto.setIds(adIds);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendBatchTimUserRegisterActionMessage(Integer uid, String nick){
		AsyncTimUserRegisterDTO dto = new AsyncTimUserRegisterDTO();
		dto.setUid(uid);
		dto.setNick(nick);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendBatchTimUserAddTagActionMessage(String acc, String utype, Integer channel){
		AsyncTimUserAddTagDTO dto = new AsyncTimUserAddTagDTO();
		dto.setAcc(acc);
		dto.setUtype(utype);
		dto.setChannel(channel);
		asyncDeliverMessageQueueProducer.sendPureText(AsyncMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
}
