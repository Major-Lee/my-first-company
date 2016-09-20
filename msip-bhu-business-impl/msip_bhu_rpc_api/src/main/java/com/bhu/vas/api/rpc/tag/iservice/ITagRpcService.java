package com.bhu.vas.api.rpc.tag.iservice;

import java.util.Date;
import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupCountOnlineVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupStatDetailVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupUsersStatisticsVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupHandsetDetailVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupSendSortMessageVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupSortMessageVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupUserStatisticsConnectVTO;
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

	RpcResponseDTO<Boolean> modifyDeciceWithNode(int uid, int gid, int newGid,String newPath, String macs);

	RpcResponseDTO<Boolean> CanSaveNode(int uid, int gid, int pid, String name);

	RpcResponseDTO<Boolean> batchGroupDownCmds(int uid, String message, String opt, String subopt, String extparams, String channel, String channel_taskid);
	
	RpcResponseDTO<Boolean> batchGroupSnkTakeEffectNetworkConf(int uid, String message, boolean on, String snk_type,String template);

	RpcResponseDTO<List<DeviceGroupPaymentStatisticsVTO>> groupsGainsStatistics(int uid, String gids, String paths);

	List<GroupCountOnlineVTO> groupsStatsOnline(int uid, String gids);
	
	RpcResponseDTO<GroupUsersStatisticsVTO> groupUsersStatistics(int gid,long time);

	RpcResponseDTO<TailPage<TagGroupHandsetDetailVTO>> groupUsersDetail(
			int uid,int gid, Long beginTime, Long endTime, boolean filter, String match,int count,String mobileno, int pageNo, int pageSize);

	RpcResponseDTO<List<Date>> groupUserDetail(int uid ,int gid, String hdmac,
			int pageNo, int pageSize);

	RpcResponseDTO<GroupStatDetailVTO> groupUsersCount(int uid ,int gid, Long beginTime,
			Long endTime);

	RpcResponseDTO<TagGroupUserStatisticsConnectVTO> groupUserStatisticsConnect(int uid, int gid, long startTime,
			long endTime, int pageNo, int pageSize);

	RpcResponseDTO<TagGroupSendSortMessageVTO> generateGroupSendSMSTask(int uid,
			int gid, int count, String context, Long beginTime, Long endTime);

	RpcResponseDTO<Boolean> executeSendTask(int uid, int taskid);

	RpcResponseDTO<TailPage<TagGroupSortMessageVTO>> sendMessageDetail(int uid,
			int gid, int pageNo, int pageSize);

}
