package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.ModuleStyleVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.task.service.VasModuleCmdDefinedService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
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
	private UserDeviceService userDeviceService;
	
	@Resource
    private WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService;
	
	@Resource
	private VasModuleCmdDefinedService vasModuleCmdDefinedService;
	
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
			boolean fw, String versionid, String upgrade_url,String upgrade_slaver_urls) {
		try{
			 VersionVTO deviceVersion = wifiDeviceGrayFacadeService.addDeviceVersion(VapEnumType.DeviceUnitType.fromIndex(dut), fw, versionid, upgrade_url,upgrade_slaver_urls);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(deviceVersion);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<VersionVTO> addDeviceVersionUploadFailCallback(int uid,boolean fw,String versionid) {
		try{
			 VersionVTO deviceVersion = wifiDeviceGrayFacadeService.addDeviceVersionUploadFailCallback(fw,versionid);
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
			boolean fw, String fileName) {
		try{
			 VersionVTO deviceVersion = wifiDeviceGrayFacadeService.removeDeviceVersion(VapEnumType.DeviceUnitType.fromIndex(dut), fw, fileName);
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
	    	mc.createCriteria().andColumnNotEqualTo("style", StringHelper.MINUS_STRING_GAP).andSimpleCaulse(" 1=1 ");
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
				result_pages.getItems().add(0, VasModuleCmdDefined.BuildStopModuleStyleVTO());
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
	
}
