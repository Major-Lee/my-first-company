package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchdevice;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionDTO;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.device.BatchDeviceApplyAdvertiseDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseListService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchDeviceApplyAdvertseServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory
			.getLogger(BatchDeviceApplyAdvertseServiceHandler.class);

	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;

	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;

	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;

	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;

	@Resource
	private IDaemonRpcService daemonRpcService;
	
	@Resource
	private AdvertiseService advertiseService;

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchDeviceApplyAdvertiseDTO adDTO = JsonHelper.getDTO(message,
				BatchDeviceApplyAdvertiseDTO.class);
		List<Advertise> adlists = advertiseService.findByIds(adDTO.getIds());
		for (final Advertise ad : adlists) {
			final int batch = 200;
			final List<String> macList = new ArrayList<String>();
			wifiDeviceDataSearchService.iteratorWithPosition(null,ad.getProvince(),
					ad.getCity(), ad.getDistrict(), batch,
					new IteratorNotify<Page<WifiDeviceDocument>>() {

						@Override
						public void notifyComming(Page<WifiDeviceDocument> pages) {
							for (WifiDeviceDocument doc : pages) {
								macList.add(doc.getD_mac());
							}
							test(batch, macList, ad.getDomain(),
									adDTO.getDtoType(), ad);
						}
			});
		}
	}

	public void test(int batch, List<String> macList, String domain,
			char dtotype, Advertise ad) {
		int fromIndex = 0;
		int toIndex = 0;
		List<DownCmds> downCmds = new ArrayList<DownCmds>(batch);
		do {
			toIndex = fromIndex + batch;
			if (toIndex > macList.size())
				toIndex = macList.size();
			List<String> todoList = macList.subList(fromIndex, toIndex);

			for (String mac : todoList) {
				WifiDeviceSharedNetwork sharednetwork = sharedNetworksFacadeService
						.fetchDeviceSharedNetworkConfWhenEmptyThenCreate(mac,
								true, true);
				SharedNetworkSettingDTO snk = sharednetwork.getInnerModel();
				ParamSharedNetworkDTO psn = snk.getPsn();
				psn.setOpen_resource_ad(dtotype == IDTO.ACT_ADD ? domain : null); // 设置或者清空广告白名单
				sharednetwork.putInnerModel(snk);
				sharedNetworksFacadeService.getWifiDeviceSharedNetworkService()
						.update(sharednetwork);

				WifiDevice wifiDevice = wifiDeviceService.getById(mac);
				if (wifiDevice == null)
					continue;
				// 生成下发指令
				String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, mac, -1,
						JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(),wifiDevice.getOrig_swver()),
						deviceCMDGenFacadeService);
				downCmds.add(DownCmds.builderDownCmds(mac, cmd));
			}

			if (!downCmds.isEmpty()) {
				daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
				downCmds.clear();
			}

			fromIndex += batch;
		} while (toIndex < macList.size());

		switch (dtotype) {
			case IDTO.ACT_ADD:
				WifiDeviceAdvertiseListService.getInstance().wifiDevicesAdApply(
						macList, JsonHelper.getJSONString(ad));
				break;
			case IDTO.ACT_DELETE:
				WifiDeviceAdvertiseListService.getInstance().wifiDevicesAdInvalid(
						macList);
			break;
		default:
			break;
		}
	}
}
