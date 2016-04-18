package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkDeviceDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceSharedNetworkRpcService;
import com.bhu.vas.api.vto.device.DeviceProfileVTO;
import com.bhu.vas.api.vto.device.UserSnkPortalVTO;
import com.bhu.vas.rpc.facade.DeviceSharedNetworkUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * device sharednetwork rpc组件服务service 对外暴露接口
 * 处理web rest业务
 * @author Edmond Lee
 *
 */
@Service("deviceSharedNetworkRpcService")
public class DeviceSharedNetworkRpcService implements IDeviceSharedNetworkRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceSharedNetworkRpcService.class);
	@Resource
	private DeviceSharedNetworkUnitFacadeService deviceSharedNetworkUnitFacadeService;

	@Override
	public RpcResponseDTO<ParamSharedNetworkDTO> fetchUserNetworkConf(int uid, String sharenetwork_type,String template) {
		logger.info(String.format("fetchUserNetworkConf with uid[%s] sharenetwork_type[%s] template[%s]", uid,sharenetwork_type,template));
		return deviceSharedNetworkUnitFacadeService.fetchUserNetworkConf(uid, sharenetwork_type,template);
	}

	@Override
	public RpcResponseDTO<List<ParamSharedNetworkDTO>> fetchUserNetworksConf(int uid, String sharenetwork_type) {
		logger.info(String.format("fetchUserNetworksConf with uid[%s] sharenetwork_type[%s]", uid,sharenetwork_type));
		return deviceSharedNetworkUnitFacadeService.fetchAllUserNetworkConf(uid, sharenetwork_type);
	}
	
	@Override
	public RpcResponseDTO<SharedNetworkSettingDTO> fetchDeviceNetworkConf(int uid, String mac) {
		logger.info(String.format("fetchDeviceNetworkConf with uid[%s] mac[%s] ", uid,mac));
		return deviceSharedNetworkUnitFacadeService.fetchDeviceNetworkConf(uid, mac);
	}
	
	
	@Override
	public RpcResponseDTO<ParamSharedNetworkDTO> applyNetworkConf(int uid,
			String sharenetwork_type,String template, String extparams) {
		logger.info(String.format("applyNetworkConf with uid[%s] sharenetwork_type[%s] template[%s] extparams[%s]", uid,sharenetwork_type,template,extparams));
		return deviceSharedNetworkUnitFacadeService.applyNetworkConf(uid, sharenetwork_type,template, extparams);
	}

	@Override
	public RpcResponseDTO<Boolean> takeEffectNetworkConf(int uid,boolean on,
			String sharenetwork_type,String template, List<String> macs) {
		logger.info(String.format("takeEffectNetworkConf with uid[%s] on[%s] sharenetwork_type[%s] template[%s] macs[%s]", uid,on,sharenetwork_type,template,macs));
		return deviceSharedNetworkUnitFacadeService.takeEffectNetworkConf(uid,on, sharenetwork_type,template, macs);
	}

	/*@Override
	public RpcResponseDTO<SharedNetworkSettingDTO> takeEffectiveNetworkConf(
			int uid, boolean on, String sharenetwork_type, String template,
			List<String> macs) {
		logger.info(String.format("takeEffectNetworkConf with uid[%s] on[%s] sharenetwork_type[%s] template[%s] macs[%s]", uid,on,sharenetwork_type,template,macs));
		return deviceSharedNetworkUnitFacadeService.takeEffectiveNetworkConf(uid,on, sharenetwork_type,template, macs);;
	}*/
	@Override
	public RpcResponseDTO<TailPage<SharedNetworkDeviceDTO>> pages(int uid, String sharedNetwork_type, String template, String d_dut, int pageNo, int pageSize) {
		logger.info(String.format("pages with uid[%s] sharedNetwork_type[%s] template[%s] d_dut[%s]", uid, sharedNetwork_type, template, d_dut));
		return deviceSharedNetworkUnitFacadeService.pages(uid, sharedNetwork_type, template, d_dut, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<UserSnkPortalVTO> fetchUserSnks4Portal(int uid) {
		logger.info(String.format("fetchUserSnks4Portal with uid[%s]", uid));
		return deviceSharedNetworkUnitFacadeService.fetchUserSnks4Portal(uid);
	}

	@Override
	public RpcResponseDTO<DeviceProfileVTO> fetchDeviceSnks4Portal(String mac) {
		logger.info(String.format("fetchUserSnks4Portal with uid[%s]", mac));
		return deviceSharedNetworkUnitFacadeService.fetchDeviceSnks4Portal(mac);
	}

}
