package com.bhu.vas.api.rpc.tag.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupCountOnlineVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * 
 * @author xiaowei by 16/04/12
 *
 */
public interface ITagRpcService {
	
	/**
	 * 给设备绑定标签
	 * @param mac
	 * @param tag
	 * @return
	 */
    RpcResponseDTO<Boolean> bindTag(int uid, String mac, String tag);
    
    RpcResponseDTO<TailPage<TagNameVTO>> fetchTag(int pageNo, int pageSize);

	RpcResponseDTO<Boolean> deviceBatchBindTag(int uid, String message, String tag);
    
    /**
     * 单个设备删除标签
     * @param uid
     * @param mac
     * @return
     */
    RpcResponseDTO<Boolean> delTag(int uid, String mac);
    
	RpcResponseDTO<Boolean> deviceBatchDelTag(int uid, String message);

	RpcResponseDTO<TagGroupVTO> saveTreeNode(int uid, int gid, int pid, String name);

	RpcResponseDTO<Boolean> saveDevices2Group(int uid, int gid, String path, String macs);

	RpcResponseDTO<Boolean> delNode(int uid, String gids);

	RpcResponseDTO<TailPage<TagGroupVTO>> fetchChildGroup(int uid, int pid, int pageNo, int pageSize);

	TagGroupVTO currentGroupDetail(int uid, int gid);

	RpcResponseDTO<Boolean> modifyDeciceWithNode(int uid, int gid, int newGid, String macs);

	RpcResponseDTO<Boolean> CanSaveNode(int uid, int gid, int pid, String name);

	RpcResponseDTO<Boolean> batchGroupDownCmds(int uid, String message, String opt, String subopt, String extparams);
	RpcResponseDTO<Boolean> batchGroupSnkTakeEffectNetworkConf(int uid, String message, boolean on, String snk_type,String template);

	RpcResponseDTO<List<DeviceGroupPaymentStatisticsVTO>> groupsGainsStatistics(int uid, String gids, String paths);

	List<GroupCountOnlineVTO> groupsStatsOnline(int uid, String gids);
}	
