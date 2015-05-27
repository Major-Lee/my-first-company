package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.DeviceGroupDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceGroupUnitFacadeRpcService{

	private final Logger logger = LoggerFactory.getLogger(DeviceGroupUnitFacadeRpcService.class);
	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;
	
	
	/**
	 * 通过pid取得pid=pid的节点
	 * @param uid
	 * @param pid
	 * @return
	 */
	public RpcResponseDTO<List<DeviceGroupDTO>> birthTree(Integer uid, Integer pid) {
		if(pid == null) pid = 0;
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("pid", pid);
    	//mc.setPageNumber(1);
    	//mc.setPageSize(400);
    	List<WifiDeviceGroup> groups = wifiDeviceGroupService.findModelByModelCriteria(mc);
    	List<DeviceGroupDTO> result = new ArrayList<DeviceGroupDTO>();
    	for(WifiDeviceGroup group:groups){
    		result.add(fromWifiDeviceGroup(group));
    	}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
	}
	
	
	public RpcResponseDTO<DeviceGroupDTO> save(Integer uid, Integer gid,Integer pid, String name) {
		WifiDeviceGroup dgroup= null;
		if(gid == null || gid.intValue() == 0){//新建一个组
			dgroup = new WifiDeviceGroup();
			dgroup.setPid(pid);
			dgroup.setName(name);
			dgroup = wifiDeviceGroupService.insert(dgroup);
		}else{
			dgroup = wifiDeviceGroupService.getById(gid);
			if(pid != null && pid.intValue() > 0)
				dgroup.setPid(pid);
			dgroup.setName(name);
			dgroup = wifiDeviceGroupService.update(dgroup);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(fromWifiDeviceGroup(dgroup));
	}
	public RpcResponseDTO<DeviceGroupDTO> detail(Integer uid, Integer gid) {
		WifiDeviceGroup dgroup = wifiDeviceGroupService.getById(gid);
		if(dgroup != null){
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(fromWifiDeviceGroup(dgroup));
		}else{
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST);
		}
	}
	
	public RpcResponseDTO<Boolean> remove(Integer uid, String gids) {
		List<Integer> gidList = new ArrayList<Integer>();
		String[] arrayresids = gids.split(StringHelper.COMMA_STRING_GAP);
		for(String residstr:arrayresids){
			Integer resid = new Integer(residstr);
			gidList.add(resid);
		}
		if(!gidList.isEmpty())
			wifiDeviceGroupService.deleteByIds(gidList);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}
	
	public RpcResponseDTO<Boolean> grant(Integer uid, Integer gid, String wifi_ids) {
		if(StringUtils.isEmpty(wifi_ids)) return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		String[] arrayresids = wifi_ids.split(StringHelper.COMMA_STRING_GAP);
		if(arrayresids.length > 0){
			WifiDeviceGroup dgroup = wifiDeviceGroupService.getById(gid);
			if(dgroup == null)
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST);
			
			for(String residstr:arrayresids){
				dgroup.putInnerModel(residstr, true, true);
			}
			wifiDeviceGroupService.update(dgroup);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}
	
	public RpcResponseDTO<Boolean> ungrant(Integer uid, Integer gid, String wifi_ids) {
		if(StringUtils.isEmpty(wifi_ids)) return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		String[] arrayresids = wifi_ids.split(StringHelper.COMMA_STRING_GAP);
		if(arrayresids.length > 0){
			WifiDeviceGroup dgroup = wifiDeviceGroupService.getById(gid);
			if(dgroup == null)
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST);
			
			for(String residstr:arrayresids){
				dgroup.removeInnerModel(residstr);//.putInnerModel(residstr, true, true);
			}
			wifiDeviceGroupService.update(dgroup);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}

	private DeviceGroupDTO fromWifiDeviceGroup(WifiDeviceGroup dgroup){
		DeviceGroupDTO dto = new DeviceGroupDTO();
		dto.setGid(dgroup.getId());
		dto.setName(dgroup.getName());
		dto.setPid(dgroup.getPid());
		if(dgroup.getPid() == null || dgroup.getPid().intValue() == 0){
			dto.setPname("根节点");
		}else{
			WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(dgroup.getPid());
			dto.setPname( (parent_group!=null)?parent_group.getName():null);
		}
		dto.setPath(dgroup.getPath());
		return dto;
	}
}
