package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
import com.bhu.vas.rpc.facade.VapFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 
 * @author Edmond
 *
 */
@Service("vapRpcService")
public class VapRpcService  implements IVapRpcService{
    private final Logger logger = LoggerFactory.getLogger(VapRpcService.class);

    @Resource
    private VapFacadeService vapFacadeService;
    
    @Resource
    private WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService;
	@Resource
	private DeliverMessageService deliverMessageService;
    @Override
    public RpcResponseDTO<VapModeUrlViewCountDTO> urlView(String key, String field) {
        logger.info(String.format("checkAcc with key[%s] field[%s]", key, field));
        return vapFacadeService.urlView(key.toLowerCase(), field);
    }

	@Override
	public RpcResponseDTO<List<DeviceUnitTypeVTO>> deviceUnitTypes(int uid) {
		logger.info(String.format("deviceUnitTypes uid[%s]",uid));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(wifiDeviceGrayFacadeService.deviceUnitTypes());
	}

	@Override
	public RpcResponseDTO<CurrentGrayUsageVTO> currentGrays(int uid, String dut) {
		logger.info(String.format("currentGrays uid[%s] dut[%s]",uid,dut));
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

	@Override
	public RpcResponseDTO<TailPage<VersionVTO>> pagesDeviceVersions(int uid,String dut,boolean fw,int pn,int ps) {
		logger.info(String.format("pagesDeviceVersions uid[%s] dut[%s] fw[%s] pn[%s] ps[%s]",uid,dut,fw,pn,ps));
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

	@Override
	public RpcResponseDTO<GrayUsageVTO> modifyRelatedVersion4GrayVersion(
			int uid, String dut, int gl, String fwid, String omid) {
		logger.info(String.format("modifyRelatedVersion4GrayVersion uid[%s] dut[%s] gl[%s] fwid[%s] omid[%s]",uid,dut,gl,fwid,omid));
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

	@Override
	public RpcResponseDTO<VersionVTO> addDeviceVersion(int uid, String dut,
			boolean fw, String versionid, String upgrade_url) {
		logger.info(String.format("addDeviceVersion uid[%s] dut[%s] fw[%s] versionid[%s] upgrade_url[%s]",uid,dut,fw,versionid,upgrade_url));
		try{
			 VersionVTO deviceVersion = wifiDeviceGrayFacadeService.addDeviceVersion(VapEnumType.DeviceUnitType.fromIndex(dut), fw, versionid, upgrade_url);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(deviceVersion);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<VersionVTO> removeDeviceVersion(int uid, String dut,
			boolean fw, String versionid) {
		logger.info(String.format("removeDeviceVersion uid[%s] dut[%s] fw[%s] versionid[%s]",uid,dut,fw,versionid));
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
	@Override
	public RpcResponseDTO<Boolean> saveMacs2Gray(int uid, String dut, int gl,
			List<String> macs) {
		logger.info(String.format("saveMacs2Gray uid[%s] dut[%s] gl[%s] macs[%s]",uid,dut,gl,macs));
		try{
			 wifiDeviceGrayFacadeService.saveMacs2Gray(
					 VapEnumType.DeviceUnitType.fromIndex(dut), VapEnumType.GrayLevel.fromIndex(gl), macs);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	@Override
	public RpcResponseDTO<Boolean> forceDeviceUpgrade(int uid, boolean fw, String versionid,List<String> macs,String beginTime,String endTime) {
		logger.info(String.format("forceDeviceUpgrade uid[%s] fw[%s] versionid[%s] macs[%s]",uid,fw,versionid,macs));
		try{
			 List<DownCmds> downCmds = wifiDeviceGrayFacadeService.forceDeviceUpgrade(fw,versionid, macs,beginTime,endTime);
			 if(downCmds != null && !downCmds.isEmpty())
				 deliverMessageService.sendWifiCmdsCommingNotifyMessage(uid, downCmds);
			 return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
}
