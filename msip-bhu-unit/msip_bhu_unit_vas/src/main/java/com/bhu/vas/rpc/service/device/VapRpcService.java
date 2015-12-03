package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.vto.modulestat.ModuleDefinedItemVTO;
import com.bhu.vas.api.vto.modulestat.ModuleDefinedVTO;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.ModuleStyleVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.rpc.facade.DeviceUnitFacadeRpcService;
import com.bhu.vas.rpc.facade.VapFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

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
    private DeviceUnitFacadeRpcService deviceUnitFacadeRpcService;

    @Override
    public RpcResponseDTO<VapModeUrlViewCountDTO> urlView(String key, String field) {
        logger.info(String.format("checkAcc with key[%s] field[%s]", key, field));
        return vapFacadeService.urlView(key.toLowerCase(), field);
    }

	@Override
	public RpcResponseDTO<List<DeviceUnitTypeVTO>> deviceUnitTypes(int uid) {
		logger.info(String.format("deviceUnitTypes uid[%s]",uid));
		return deviceUnitFacadeRpcService.deviceUnitTypes(uid);
	}

	@Override
	public RpcResponseDTO<CurrentGrayUsageVTO> currentGrays(int uid, String dut) {
		logger.info(String.format("currentGrays uid[%s] dut[%s]",uid,dut));
		return deviceUnitFacadeRpcService.currentGrays(uid, dut);
	}

	@Override
	public RpcResponseDTO<TailPage<VersionVTO>> pagesDeviceVersions(int uid,String dut,boolean fw,int pn,int ps) {
		logger.info(String.format("pagesDeviceVersions uid[%s] dut[%s] fw[%s] pn[%s] ps[%s]",uid,dut,fw,pn,ps));
		return deviceUnitFacadeRpcService.pagesDeviceVersions(uid, dut, fw, pn, ps);
	}

	@Override
	public RpcResponseDTO<GrayUsageVTO> modifyRelatedVersion4GrayVersion(
			int uid, String dut, int gl, String fwid, String omid) {
		logger.info(String.format("modifyRelatedVersion4GrayVersion uid[%s] dut[%s] gl[%s] fwid[%s] omid[%s]",uid,dut,gl,fwid,omid));
		return deviceUnitFacadeRpcService.modifyRelatedVersion4GrayVersion(uid, dut, gl, fwid, omid);
	}

	@Override
	public RpcResponseDTO<VersionVTO> addDeviceVersion(int uid, String dut,
			boolean fw, String versionid, String upgrade_url) {
		logger.info(String.format("addDeviceVersion uid[%s] dut[%s] fw[%s] versionid[%s] upgrade_url[%s]",uid,dut,fw,versionid,upgrade_url));
		return deviceUnitFacadeRpcService.addDeviceVersion(uid, dut, fw, versionid, upgrade_url);
	}

	@Override
	public RpcResponseDTO<VersionVTO> removeDeviceVersion(int uid, String dut,
			boolean fw, String versionid) {
		logger.info(String.format("removeDeviceVersion uid[%s] dut[%s] fw[%s] versionid[%s]",uid,dut,fw,versionid));
		return deviceUnitFacadeRpcService.removeDeviceVersion(uid, dut, fw, versionid);
	}
	@Override
	public RpcResponseDTO<Boolean> saveMacs2Gray(int uid, String dut, int gl,
			List<String> macs) {
		logger.info(String.format("saveMacs2Gray uid[%s] dut[%s] gl[%s] macs[%s]",uid,dut,gl,macs));
		return deviceUnitFacadeRpcService.saveMacs2Gray(uid, dut, gl, macs);
	}
	
	
	@Override
	public RpcResponseDTO<Boolean> forceDeviceUpgrade(int uid, boolean fw, String versionid,List<String> macs,String beginTime,String endTime) {
		logger.info(String.format("forceDeviceUpgrade uid[%s] fw[%s] versionid[%s] macs[%s]",uid,fw,versionid,macs));
		return deviceUnitFacadeRpcService.forceDeviceUpgrade(uid, fw, versionid, macs, beginTime, endTime);
	}
	
	@Override
	public RpcResponseDTO<TailPage<ModuleStyleVTO>> pagesVapStyles(int uid,int pn,int ps){
		logger.info(String.format("pagesStyles uid[%s] pn[%s] ps[%s]",uid,pn,ps));
		return deviceUnitFacadeRpcService.pagesVapStyles(uid, pn, ps);
	}
	
	@Override
	public RpcResponseDTO<DeviceDetailVTO> deviceDetail(int uid,String mac){
		logger.info(String.format("deviceDetail uid[%s] mac[%s]",uid,mac));
		return deviceUnitFacadeRpcService.deviceDetail(uid, mac);
	}

	@Override
	public RpcResponseDTO<List<ModuleDefinedVTO>> fetchDayStat(int uid) {
		logger.info(String.format("fetchDayStat uid[%s]",uid));
		return vapFacadeService.fetchDayStat(uid);
	}

	@Override
	public RpcResponseDTO<ModuleDefinedItemVTO> fetchStatDetail(int uid, String style) {
		logger.info(String.format("fetchStatDetail uid[%s] style[%s]",uid, style));
		return vapFacadeService.fetchStatDetail(uid, style);
	}
}
