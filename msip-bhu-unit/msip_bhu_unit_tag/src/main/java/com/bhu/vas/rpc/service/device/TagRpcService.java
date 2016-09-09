package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;
import com.bhu.vas.api.rpc.tag.vto.GroupCountOnlineVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupUsersStatisticsVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupHandsetDetailVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupRankUsersVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.rpc.facade.TagFacadeRpcSerivce;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * Created by xiaowei on 4/13/16.
 */
@Service("tagRpcService")
public class TagRpcService implements ITagRpcService {

	private final Logger logger = LoggerFactory.getLogger(TagRpcService.class);

	@Resource
	private TagFacadeRpcSerivce tagFacadeRpcSerivce;

	@Override
	public RpcResponseDTO<Boolean> bindTag(int uid, String mac, String tag) {
		logger.info(String.format("bindTag uid[%s] mac[%s] tag[%s]", uid, mac, tag));
		try {
			tagFacadeRpcSerivce.bindTag(uid, mac, tag);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<TailPage<TagNameVTO>> fetchTag(int pageNo, int pageSize) {
		logger.info(String.format("fetchTag pageNo[%s] pageSize[%s]", pageNo, pageSize));

		try {
			TailPage<TagNameVTO> tagName = tagFacadeRpcSerivce.fetchTag(pageNo, pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(tagName);
		} catch (Exception e) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Boolean> delTag(int uid, String mac) {
		logger.info(String.format("delTag  uid[%s] mac[%s]", uid, mac));
		try {
			tagFacadeRpcSerivce.delTag(uid, mac);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Boolean> deviceBatchBindTag(int uid, String message, String tag) {
		logger.info(String.format("deviceBatchBindTag uid[%s] message[%s] tag[%s]", uid, message, tag));
		try {
			tagFacadeRpcSerivce.deviceBatchBindTag(uid, message, tag);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Boolean> deviceBatchDelTag(int uid, String message) {
		logger.info(String.format("deviceBatchBindTag uid[%s] message[%s]", uid, message));
		try {
			tagFacadeRpcSerivce.deviceBatchDelTag(uid, message);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 添加或修改分组
	 */
	@Override
	public RpcResponseDTO<TagGroupVTO> saveTreeNode(int uid, int gid, int pid, String name) {

		logger.info(String.format("saveTreeNode uid[%s] gid[%s] pid[%s] name[%s]", uid, gid, pid, name));
		try {
			TagGroupVTO tagGroupVTO = tagFacadeRpcSerivce.saveTreeNode(uid, gid, pid, name);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(tagGroupVTO);
		} catch (BusinessI18nCodeException i18nex) {
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 分组添加设备
	 */
	@Override
	public RpcResponseDTO<Boolean> saveDevices2Group(int uid, int gid, String path, String macs) {
		logger.info(String.format("saveDevices2Group uid[%s] gid[%s] path [%s] macs[%s]", uid, gid, path, macs));
		try {
			tagFacadeRpcSerivce.saveDevices2Group(uid, gid, path, macs);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 删除分组
	 */
	@Override
	public RpcResponseDTO<Boolean> delNode(int uid, String gids) {
		logger.info(String.format("delNode uid[%s] gids[%s]", uid, gids));
		try {
			tagFacadeRpcSerivce.delNode(uid, gids);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 修改设备分组信息
	 */
	@Override
	public RpcResponseDTO<Boolean> modifyDeciceWithNode(int uid, int gid, int newGid,String newPath, String macs) {
		logger.info(
				String.format("modifyDeciceWithNode uid[%s] gid[%s] newGid[%s] newPath[%s] macs[%s]", uid, gid, newGid, newPath,macs));
		try {
			tagFacadeRpcSerivce.modifyDeciceWithNode(uid, gid, newGid, newPath, macs);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 分页查询子节点
	 */
	@Override
	public RpcResponseDTO<TailPage<TagGroupVTO>> fetchChildGroup(int uid, int pid, int pageNo, int pageSize) {
		logger.info(
				String.format("fetchChildGroup uid[%s] pid[%s] pageNo[%s] pageSize[%s]", uid, pid, pageNo, pageSize));
		try {
			TailPage<TagGroupVTO> tagGroup = tagFacadeRpcSerivce.fetchChildGroup(uid, pid, pageNo, pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(tagGroup);
		} catch (Exception e) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 查询当前节点信息
	 */
	@Override
	public TagGroupVTO currentGroupDetail(int uid, int gid) {
		logger.info(String.format("currentGroupDetail uid[%s] gid[%s]", uid, gid));
		return tagFacadeRpcSerivce.currentGroupDetail(uid, gid);
	}

	/**
	 * 添加分组验证
	 */
	@Override
	public RpcResponseDTO<Boolean> CanSaveNode(int uid, int gid, int pid, String name) {
		logger.info(String.format("canSaveNode uid[%s] gid[%s] name[%s]", uid, gid, name));
		try {
			tagFacadeRpcSerivce.CanSaveNode(uid, gid, pid, name);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 分组批量下发指令
	 */
	@Override
	public RpcResponseDTO<Boolean> batchGroupDownCmds(int uid, String message, String opt, String subopt,
			String extparams, String channel, String channel_taskid) {
		logger.info(String.format("batchGroupDownCmds uid[%s] message[%s] opt[%s] subopt[%s] extparams[%s] channel[%s] channel_taskid[%s]", uid,
				message, opt, subopt, extparams, channel, channel_taskid));
		try {
			tagFacadeRpcSerivce.batchGroupDownCmds(uid, message, opt, subopt, extparams,channel,channel_taskid);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Boolean> batchGroupSnkTakeEffectNetworkConf(int uid, String message, boolean on,
			String snk_type, String template) {
		logger.info(
				String.format("batchGroupSnkTakeEffectNetworkConf uid[%s] message[%s] on[%s] snk_type[%s] template[%s]",
						uid, message, on, snk_type, template));
		try {
			boolean ret = tagFacadeRpcSerivce.batchGroupSnkTakeEffectNetworkConf(uid, message, on, snk_type, template);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret ? Boolean.TRUE : Boolean.FALSE);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<List<DeviceGroupPaymentStatisticsVTO>> groupsGainsStatistics(int uid, String gids,
			String paths) {
		logger.info(String.format("groupGainsStats uid[%s] gids[%s] paths[%s]", uid, gids, paths));
		try {
			List<DeviceGroupPaymentStatisticsVTO> ret = tagFacadeRpcSerivce.groupsGainsStatistics(uid, gids, paths);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public List<GroupCountOnlineVTO> groupsStatsOnline(int uid, String gids) {
		logger.info(String.format("groupsStatsOnline uid[%s] gids[%s]", uid, gids));
		List<GroupCountOnlineVTO> list = tagFacadeRpcSerivce.groupsStatsOnline(uid, gids);
		return list;
	}

	@Override
	public RpcResponseDTO<GroupUsersStatisticsVTO> groupUsersStatistics(int gid, long time) {
		logger.info(String.format("groupUsersStatistics gid[%s] time[%s]", gid, time));
		try{
			String timeStr = DateTimeHelper.formatDate(new Date(time), DateTimeHelper.FormatPattern7);
			GroupUsersStatisticsVTO result = tagFacadeRpcSerivce.groupUsersStatistics(gid, timeStr);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("groupUsersStatistics Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<TailPage<TagGroupHandsetDetailVTO>> groupUsersDetail(int gid, long beginTime,long endTime,boolean filter,int count,String mobileno,int pageNo,int pageSize) {
		logger.info(String.format("groupUsersDetail gid[%s] beginTime[%s] endTime[%s] filter[%s] count[%s] mobileno[%s] pageNo[%s] pageSize[%s]", gid, beginTime,endTime,filter,count,mobileno,pageNo,pageSize));
		try{
			String beginTimeStr = DateTimeHelper.formatDate(DateTimeHelper.getDateTime(new Date(beginTime),DateTimeHelper.FormatPattern5));
			String endTimeStr = DateTimeHelper.formatDate(DateTimeHelper.getDateTime(new Date(endTime),DateTimeHelper.FormatPattern5));
			TailPage<TagGroupHandsetDetailVTO> result = tagFacadeRpcSerivce.groupUsersDetail(gid, beginTimeStr, endTimeStr, filter, count,mobileno, pageNo, pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("groupUsersStatistics Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<List<Date>> groupUserDetail(int gid,String hdmac,int pageNo,int pageSize) {
		logger.info(String.format("groupUserDetail gid[%s] hdmac[%s] pageNo[%s] pageSize[%s]", gid,hdmac,pageNo,pageSize));
		try{
			List<Date> result = tagFacadeRpcSerivce.groupUserDetail(gid,hdmac, pageNo, pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("groupUsersStatistics Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<TailPage<TagGroupRankUsersVTO>> groupRankUsers(int uid, int gid, int pageNo, int pageSize) {
		logger.info(String.format("groupRankUsers uid[%s] gid[%s] pageNo[%s] pageSize[%s]", uid,gid, pageNo, pageSize));
		try{
			TailPage<TagGroupRankUsersVTO> result = tagFacadeRpcSerivce.groupRankUsers(uid,gid,pageNo,pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("groupUsersStatistics Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
