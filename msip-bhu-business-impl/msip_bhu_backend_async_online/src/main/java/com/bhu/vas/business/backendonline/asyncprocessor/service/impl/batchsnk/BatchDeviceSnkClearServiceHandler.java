package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.SharedNetworksHelper;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.SnkTurnStateEnum;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.business.asyn.spring.model.async.snk.BatchDeviceSnkClearDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchDeviceSnkClearServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchDeviceSnkClearServiceHandler.class);
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			BatchDeviceSnkClearDTO clearDto = JsonHelper.getDTO(message, BatchDeviceSnkClearDTO.class);
			final int userid = clearDto.getUid();
			final String snk_type = clearDto.getSnk_type();
			final String template = clearDto.getTemplate();
			final SharedNetworkType sharedNetworkType = VapEnumType.SharedNetworkType.fromKey(snk_type);
			if(userid <=0 
					||StringUtils.isEmpty(snk_type) || sharedNetworkType == null 
					|| SharedNetworksHelper.wasDefaultTemplate(template) || !SharedNetworksHelper.validTemplateFormat(template)){
				logger.info(String.format("process message[%s] param error!", message));
				return;
			}
			final List<String> dmacs = new ArrayList<String>();
			wifiDeviceDataSearchService.iteratorWithSharedNetwork(userid, snk_type,template, null, 200, new IteratorNotify<Page<WifiDeviceDocument>>() {
			    @Override
			    public void notifyComming(Page<WifiDeviceDocument> pages) {
			    	for (WifiDeviceDocument doc : pages) {
			    		//对应的设备会自动关联相同类型的默认模板（原来是打赏的就关联打赏默认，原来是短信的就关联短信默认）
			    		String mac = doc.getD_mac();
			    		dmacs.add(mac);
			    	}
			    	if(dmacs.isEmpty()) return;
			    	logger.info(String.format("clear uid[%s] dmacs[%s] snk[%s] from tpl[%s] to tpl[%s]", userid,dmacs,snk_type,template,SharedNetworksHelper.DefaultTemplate));
			    	sharedNetworksFacadeService.closeAndApplyDevicesFromSharedNetwork(userid,sharedNetworkType,SharedNetworksHelper.DefaultTemplate,dmacs,
							new ISharedNetworkNotifyCallback(){
								@Override
								public void notify(ParamSharedNetworkDTO current,List<String> rdmacs) {
									logger.info(String.format("turnoff notify callback uid[%s] rdmacs[%s] sharednetwork conf[%s]", userid,rdmacs,JsonHelper.getJSONString(current)));
									if(rdmacs == null || rdmacs.isEmpty()){
										return;
									}
									wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(rdmacs, current.getNtype(),current.getTemplate(),SnkTurnStateEnum.Off.getType());
								}
							});
			    }
			});
			logger.info(String.format("start clear uid[%s] snk[%s] tpl[%s]", userid,snk_type,template));
			UserDevicesSharedNetworks configs = sharedNetworksFacadeService.getUserDevicesSharedNetworksService().getById(userid);
			List<ParamSharedNetworkDTO> models = configs.get(snk_type,new ArrayList<ParamSharedNetworkDTO>(),true);
			if(!models.isEmpty()){
				ParamSharedNetworkDTO temp = new ParamSharedNetworkDTO();
				temp.setTemplate(template);
				models.remove(temp);
				configs.put(snk_type, models);
				sharedNetworksFacadeService.getUserDevicesSharedNetworksService().update(configs);
				logger.info(String.format("clear ok uid[%s] snk[%s] tpl[%s]", userid,snk_type,template));
			}
			
		}finally{
		}
		logger.info(String.format("process message[%s] successful", message));
	}


/*	@Override
	public void createDeviceGroupIndex(String message) {

		logger.info(String.format("WifiDeviceGroupServiceHandler createDeviceGroupIndex message[%s]", message))
		WifiDeviceGroupAsynCreateIndexDTO dto = JsonHelper.getDTO(message, WifiDeviceGroupAsynCreateIndexDTO.class);
		String wifiIdsStr = dto.getWifiIds();
		String type = dto.getType();
		Long gid = dto.getGid();

		List<String> wifiIds = new ArrayList<>();
		List<List<Long>> groupIdList = new ArrayList<List<Long>>();
		String[] wifiIdArray = wifiIdsStr.split(StringHelper.COMMA_STRING_GAP);
		for (String wifiId : wifiIdArray) {
			wifiIds.add(wifiId);

			List<Long> gids = wifiDeviceGroupRelationService.getDeviceGroupIds(wifiId);
			if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_GRANT)) {
				gids.add(gid);
			} else if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_UNGRANT)) {
				gids.remove(gid);
			}
			groupIdList.add(gids);
		}
		List<WifiDevice> wifiDeviceList = wifiDeviceService.findByIds(wifiIds);
		wifiDeviceIndexIncrementService.wifiDeviceIndexBlukIncrement(wifiDeviceList, groupIdList);

	}*/
}
