package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchtag;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.increment.IncrementBulkDocumentDTO;
import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.tag.OperTagDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.increment.KafkaMessageIncrementProducer;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchDeviceTagServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchDeviceTagServiceHandler.class);
	
/*	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;*/
	
	//@Resource
	//private BackendBusinessService backendBusinessService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
//	@Resource
//	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;
	@Resource
	private KafkaMessageIncrementProducer incrementMessageTopicProducer;
	
	@Resource
	private TagDevicesService tagDevicesService;
	
	/*@Resource
	private IDaemonRpcService daemonRpcService;*/

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final OperTagDTO operTagDto = JsonHelper.getDTO(message, OperTagDTO.class);
		//final BindTagDTO bindTagDto = JsonHelper.getDTO(message, BindTagDTO.class);
		final String newTags = operTagDto.getTag();
		final int uid = operTagDto.getUid();
		
		if(IDTO.ACT_ADD == operTagDto.getDtoType()){
			wifiDeviceDataSearchService.iteratorAll(operTagDto.getMessage(),
					new IteratorNotify<Page<WifiDeviceDocument>>() {
						@Override
						public void notifyComming(Page<WifiDeviceDocument> pages) {

							List<String> macList = new ArrayList<String>();
							List<String> tagNameList = new ArrayList<String>();

							List<TagDevices> insertList = new ArrayList<TagDevices>();
							List<TagDevices> upDateList = new ArrayList<TagDevices>();

							for (WifiDeviceDocument doc : pages) {
								macList.add(doc.getD_mac());
							}
							List<TagDevices> tagList = tagDevicesService.findByIds(macList, true, true);

							String[] arrTemp = newTags.split(",");
							int index = 0;
							for (TagDevices tagDevices : tagList) {
								if (tagDevices == null) {
									TagDevices entity = new TagDevices();
									entity.setId(macList.get(index));
									entity.setLast_operator(uid);

									entity.replaceInnerModels(ArrayHelper.toSet(arrTemp));

									tagNameList.add(entity.getTag2ES());
									insertList.add(entity);
								} else {
									tagDevices.setLast_operator(uid);
									tagDevices.replaceInnerModels(ArrayHelper.toSet(arrTemp));
									upDateList.add(tagDevices);
									tagNameList.add(tagDevices.getTag2ES());
								}
								index++;
							}
							tagDevicesService.insertAll(insertList);
							tagDevicesService.updateAll(upDateList);

							//wifiDeviceStatusIndexIncrementService.bindDTagsMultiUpdIncrement(macList, tagNameList);
							incrementMessageTopicProducer.incrementDocument(IncrementBulkDocumentDTO.builder(macList, 
									IncrementActionEnum.WD_DTagsChanged, BusinessIndexDefine.WifiDevice.IndexUniqueId));
						}
					});
		}else{
			wifiDeviceDataSearchService.iteratorAll(operTagDto.getMessage(),
					new IteratorNotify<Page<WifiDeviceDocument>>() {
						@Override
						public void notifyComming(Page<WifiDeviceDocument> pages) {
							List<String> macList = new ArrayList<String>();
							for (WifiDeviceDocument doc : pages) {
								macList.add(doc.getD_mac());
							}
							List<TagDevices> entities = tagDevicesService.findByIds(macList, true, true);
							tagDevicesService.deleteAll(entities);
							//wifiDeviceStatusIndexIncrementService.bindDTagsMultiUpdIncrement(macList, "");
							
							incrementMessageTopicProducer.incrementDocument(IncrementBulkDocumentDTO.builder(macList, 
									IncrementActionEnum.WD_DTagsChanged, BusinessIndexDefine.WifiDevice.IndexUniqueId));
						}
					});
		}

		logger.info(String.format("process message[%s] successful", message));
	}
	
	
/*	public void deviceBatchBindTag(String message) {
		logger.info(String.format("deviceBatchBindTag message[%s]", message));
		final OperTagDTO operTagDto = JsonHelper.getDTO(message, OperTagDTO.class);
		//final BindTagDTO bindTagDto = JsonHelper.getDTO(message, BindTagDTO.class);
		final String newTags = operTagDto.getTag();
		final int uid = operTagDto.getUid();

		
		
		wifiDeviceDataSearchService.iteratorAll(operTagDto.getMessage(),
				new IteratorNotify<Page<WifiDeviceDocument>>() {
					@Override
					public void notifyComming(Page<WifiDeviceDocument> pages) {

						List<String> macList = new ArrayList<String>();
						List<String> tagNameList = new ArrayList<String>();

						List<TagDevices> insertList = new ArrayList<TagDevices>();
						List<TagDevices> upDateList = new ArrayList<TagDevices>();

						for (WifiDeviceDocument doc : pages) {
							macList.add(doc.getD_mac());
						}

						List<TagDevices> tagList = tagDevicesService.findByIds(macList, true, true);

						String[] arrTemp = newTags.split(",");

						int index = 0;
						for (TagDevices tagDevices : tagList) {
							if (tagDevices == null) {
								TagDevices entity = new TagDevices();
								entity.setId(macList.get(index));
								entity.setLast_operator(uid);

								entity.replaceInnerModels(ArrayHelper.toSet(arrTemp));

								tagNameList.add(entity.getTag2ES());

								insertList.add(entity);

							} else {
								tagDevices.setLast_operator(uid);

								tagDevices.replaceInnerModels(ArrayHelper.toSet(arrTemp));

								upDateList.add(tagDevices);
								tagNameList.add(tagDevices.getTag2ES());
							}
							index++;
						}
						tagDevicesService.insertAll(insertList);
						tagDevicesService.updateAll(upDateList);

						wifiDeviceStatusIndexIncrementService.bindDTagsMultiUpdIncrement(macList, tagNameList);
					}
				});
	}*/

	/*public void deviceBatchDelTag(String message) {
		logger.info(String.format("deviceBatchDelTag message[%s]", message));
		final DelTagDTO delTagDto = JsonHelper.getDTO(message, DelTagDTO.class);

		wifiDeviceDataSearchService.iteratorAll(delTagDto.getMessage(),
				new IteratorNotify<Page<WifiDeviceDocument>>() {
					@Override
					public void notifyComming(Page<WifiDeviceDocument> pages) {
						
						List<String> macList = new ArrayList<String>();
						for (WifiDeviceDocument doc : pages) {
							macList.add(doc.getD_mac());
						}
						
						List<TagDevices> entities = tagDevicesService.findByIds(macList, true, true);
						tagDevicesService.deleteAll(entities);
						wifiDeviceStatusIndexIncrementService.bindDTagsMultiUpdIncrement(macList, "");
					}
				});
	}*/
}
