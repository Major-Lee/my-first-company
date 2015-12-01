package com.bhu.vas.api.rpc.vap.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * 
 * @author Edmond
 *
 */
public interface IVapRpcService {
    RpcResponseDTO<VapModeUrlViewCountDTO> urlView(String key, String field);
    
    RpcResponseDTO<List<DeviceUnitTypeVTO>> deviceUnitTypes(int uid);
    RpcResponseDTO<CurrentGrayUsageVTO> currentGrays(int uid,String dut);
    RpcResponseDTO<TailPage<VersionVTO>> pagesDeviceVersions(int uid,String dut,boolean fw,int pn,int ps);
    
    RpcResponseDTO<GrayUsageVTO> modifyRelatedVersion4GrayVersion(int uid,String dut,int gl,String fwid,String omid);
    RpcResponseDTO<VersionVTO> addDeviceVersion(int uid,String dut,boolean fw,String versionid,String upgrade_url);
    RpcResponseDTO<VersionVTO> removeDeviceVersion(int uid,String dut,boolean fw,String versionid);
    RpcResponseDTO<Boolean> saveMacs2Gray(int uid, String dut, int gl,List<String> macs);
}
