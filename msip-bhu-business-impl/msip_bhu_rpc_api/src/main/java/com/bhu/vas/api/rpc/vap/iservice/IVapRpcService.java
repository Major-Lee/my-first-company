package com.bhu.vas.api.rpc.vap.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.ModuleStyleVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.api.vto.modulestat.ModuleDefinedItemVTO;
import com.bhu.vas.api.vto.modulestat.ModuleDefinedVTO;
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
    RpcResponseDTO<List<String>> saveMacs2Gray(int uid, String dut, int gl,List<String> macs);
    RpcResponseDTO<Boolean> forceDeviceUpgrade(int uid, boolean fw, String versionid,List<String> macs,String beginTime,String endTime);
    RpcResponseDTO<TailPage<ModuleStyleVTO>> pagesVapStyles(int uid,int pn,int ps);
    RpcResponseDTO<DeviceDetailVTO> deviceDetail(int uid,String mac);
    RpcResponseDTO<List<DeviceDetailVTO>> userDetail(int uid,int countrycode,String acc,int tid);



    RpcResponseDTO<List<ModuleDefinedVTO>> fetchDayStat(int uid);
    RpcResponseDTO<ModuleDefinedItemVTO> fetchStatDetail(int uid,String style);

}
