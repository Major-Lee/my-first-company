package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGrayVersionPK;
import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceBaseVTO;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.device.DeviceOperationVTO;
import com.bhu.vas.api.vto.device.DevicePresentVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.ModuleStyleVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.task.service.VasModuleCmdDefinedService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceUnitFacadeRpcService{
	@Resource
	private UserService userService;

	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
    private WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService;
	
	@Resource
	private VasModuleCmdDefinedService vasModuleCmdDefinedService;
	
	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;

	
	@Resource
	private DeliverMessageService deliverMessageService;

	public RpcResponseDTO<List<DeviceUnitTypeVTO>> deviceUnitTypes(int uid) {
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(wifiDeviceGrayFacadeService.deviceUnitTypes());
	}

	public RpcResponseDTO<CurrentGrayUsageVTO> currentGrays(int uid, String dut) {
		try{
			DeviceUnitType unitType = VapEnumType.DeviceUnitType.fromIndex(dut);
			if(unitType != null)
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(wifiDeviceGrayFacadeService.currentGrays(unitType));
			else
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<TailPage<VersionVTO>> pagesDeviceVersions(int uid,String dut,boolean fw,int pn,int ps) {
		try{
			DeviceUnitType unitType = VapEnumType.DeviceUnitType.fromIndex(dut);
			if(unitType != null){
				TailPage<VersionVTO> pages = null;
				if(fw){
					pages = wifiDeviceGrayFacadeService.pagesFW(unitType, pn, ps);
				}else{
					pages = wifiDeviceGrayFacadeService.pagesOM(unitType, pn, ps);
				}
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(pages);
			}else
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<GrayUsageVTO> modifyRelatedVersion4GrayVersion(
			int uid, String dut, int gl, String fwid, String omid) {
		try{
			 GrayUsageVTO grayUsageVTO = wifiDeviceGrayFacadeService.modifyRelatedVersion4GrayVersion(
					 VapEnumType.DeviceUnitType.fromIndex(dut), VapEnumType.GrayLevel.fromIndex(gl), fwid, omid);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(grayUsageVTO);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
		
	}

	public RpcResponseDTO<VersionVTO> addDeviceVersion(int uid, String dut,
			boolean fw, String versionid,byte[] bs,String fileName) {
		try{
			 VersionVTO deviceVersion = wifiDeviceGrayFacadeService.addDeviceVersion(VapEnumType.DeviceUnitType.fromIndex(dut), fw, versionid,bs,fileName);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(deviceVersion);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<VersionVTO> removeDeviceVersion(int uid, String dut,
			boolean fw, String versionid) {
		try{
			 VersionVTO deviceVersion = wifiDeviceGrayFacadeService.removeDeviceVersion(VapEnumType.DeviceUnitType.fromIndex(dut), fw, versionid);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(deviceVersion);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<List<String>> saveMacs2Gray(int uid, String dut, int gl,
			List<String> macs) {
		try{
			 List<String> result_success= wifiDeviceGrayFacadeService.saveMacs2Gray(
					 VapEnumType.DeviceUnitType.fromIndex(dut), VapEnumType.GrayLevel.fromIndex(gl), macs);
			 if(result_success != null && !result_success.isEmpty()){
				 deliverMessageService.sendDevicesGrayChangedNotifyMessage(uid,dut,gl,macs);
			 }
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_success);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<Boolean> forceDeviceUpgrade(int uid, boolean fw, String versionid,List<String> macs,String beginTime,String endTime) {
		try{
			 List<DownCmds> downCmds = wifiDeviceGrayFacadeService.forceDeviceUpgrade(fw,versionid, macs,beginTime,endTime);
			 if(downCmds != null && !downCmds.isEmpty())
				 deliverMessageService.sendWifiMultiCmdsCommingNotifyMessage(uid, downCmds);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	public RpcResponseDTO<TailPage<ModuleStyleVTO>> pagesVapStyles(int uid,int pn,int ps){
		try{
	    	ModelCriteria mc = new ModelCriteria();
	    	mc.createCriteria().andSimpleCaulse(" 1=1 ");
	    	mc.setPageNumber(pn);
	    	mc.setPageSize(ps);
	    	mc.setOrderByClause(" created_at desc ");
			TailPage<VasModuleCmdDefined> pages = vasModuleCmdDefinedService.findModelTailPageByModelCriteria(mc);
			List<ModuleStyleVTO> vtos = new ArrayList<>();
			for(VasModuleCmdDefined mcf:pages.getItems()){
				vtos.add(mcf.toModuleStyleVTO());
			}
			TailPage<ModuleStyleVTO> result_pages = new CommonPage<ModuleStyleVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos);
			{
				//增加一条无的数据，用于关闭增值模板指令
				result_pages.getItems().add(0, VasModuleCmdDefined.BuildEmptyModuleStyleVTO());
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result_pages);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/*public RpcResponseDTO<Boolean> changeVapStyle(int uid,String mac,String style){
		return null;
	}*/
	
	public RpcResponseDTO<List<DeviceDetailVTO>> userDetail(int operationUid,int countrycode,String acc,int tid){
		if(tid <=0 && StringUtils.isEmpty(acc))
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		if(tid <=0){
			Integer ret_uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
			if(ret_uid == null || ret_uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			tid = ret_uid.intValue();
		}
		List<UserDevice> userDevices = userDeviceService.fetchBindDevicesWithLimit(tid, UserUnitFacadeService.WIFI_DEVICE_BIND_LIMIT_NUM);
		
		List<DeviceDetailVTO> resultVTOs = new ArrayList<>();
		for(UserDevice udevice:userDevices){
			RpcResponseDTO<DeviceDetailVTO> vto = this.deviceDetail(operationUid, udevice.getMac());
			if(!vto.hasError())
				resultVTOs.add(vto.getPayload());
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(resultVTOs);
	}
	
	public RpcResponseDTO<DeviceDetailVTO> deviceDetail(int operationUid,String mac){
		try{
			WifiDevice wifiDevice = wifiDeviceService.getById(mac);
			if(wifiDevice == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{"mac"});
			}
			WifiDeviceModule wifiDeviceModule = wifiDeviceModuleService.getById(mac);
			User user = null;
			Integer bindUid = userDeviceService.fetchBindUid(mac);
			if(bindUid != null){
				user = userService.getById(bindUid);
			}
			//基础信息
			DeviceBaseVTO dbv = new DeviceBaseVTO();
			dbv.setMac(wifiDevice.getId());
			dbv.setSn(wifiDevice.getSn());
			dbv.setOrig_swver(wifiDevice.getOrig_swver());
			dbv.setVap_module(wifiDeviceModule != null?wifiDeviceModule.getOrig_vap_module():StringUtils.EMPTY);
			dbv.setOrig_model(wifiDevice.getOrig_model());
			dbv.setOrig_hdver(wifiDevice.getOrig_hdver());
			dbv.setWork_mode(wifiDevice.getWork_mode());
			dbv.setHdtype(wifiDevice.getHdtype());
			DeviceVersion parser = DeviceVersion.parser(wifiDevice.getOrig_swver());
			//String dut = parser.toDeviceUnitTypeIndex();
			dbv.setDut(parser.getDut());
			DeviceUnitType unitType = DeviceUnitType.fromIndex(parser.getDut());
			dbv.setDutn(unitType != null ?unitType.getName():StringHelper.MINUS_STRING_GAP);
			//状态信息
			DevicePresentVTO dpv = new DevicePresentVTO();
			dpv.setMac(wifiDevice.getId());
			dpv.setUid(user != null?user.getId():0);
			dpv.setMobileno(user != null ? user.getMobileno():StringUtils.EMPTY);
			if(user != null){
				dpv.setHandsettype(user.getLastlogindevice());
				dpv.setHandsetn(DeviceEnum.getBySName(user.getLastlogindevice()).getName());
			}else{
				dpv.setHandsettype(StringHelper.MINUS_STRING_GAP);
				dpv.setHandsetn(StringHelper.MINUS_STRING_GAP);
			}
			
			dpv.setAddress(wifiDevice.getFormatted_address());
			dpv.setOnline(wifiDevice.isOnline());
			dpv.setMonline(wifiDeviceModule != null?wifiDeviceModule.isModule_online():false);
			dpv.setFirst_reg_at(DateTimeHelper.formatDate(wifiDevice.getCreated_at(), DateTimeHelper.FormatPattern0));
			if(wifiDevice.getLast_reged_at() != null)
				dpv.setLast_reg_at(DateTimeHelper.formatDate(wifiDevice.getLast_reged_at(), DateTimeHelper.FormatPattern0));
			if(wifiDevice.getLast_logout_at() != null)
				dpv.setLast_logout_at(DateTimeHelper.formatDate(wifiDevice.getLast_logout_at(), DateTimeHelper.FormatPattern0));
			dpv.setDod(wifiDevice.getUptime());
			
			//运营状态信息 灰度、模板
			DeviceOperationVTO dov = new DeviceOperationVTO();
			WifiDeviceGrayVersionPK deviceGray = wifiDeviceGrayFacadeService.determineDeviceGray(wifiDevice.getId(), wifiDevice.getOrig_swver());
			dov.setDut(deviceGray.getDut());
			dov.setGl(deviceGray.getGl());
			dov.setGln(VapEnumType.GrayLevel.fromIndex(deviceGray.getGl()).getName());
			String mstyle = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(wifiDevice.getId());
			if(StringUtils.isNotEmpty(mstyle))
				dov.setMstyle(mstyle);
			else
				dov.setMstyle(StringHelper.MINUS_STRING_GAP);
			DeviceDetailVTO dvto = new DeviceDetailVTO();
			dvto.setDbv(dbv);
			dvto.setDpv(dpv);
			dvto.setDov(dov);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(dvto);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
