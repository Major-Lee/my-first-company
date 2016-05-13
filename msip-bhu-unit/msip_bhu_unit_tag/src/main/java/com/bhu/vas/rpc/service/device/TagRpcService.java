package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.rpc.facade.TagFacadeRpcSerivce;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import org.springframework.stereotype.Service;

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
	public TagGroupVTO saveTreeNode(int uid, int gid, int pid, String name) {
		logger.info(String.format("saveTreeNode uid[%s] gid[%s] pid[%s] name[%s]", uid, gid, pid, name));
		return tagFacadeRpcSerivce.saveTreeNode(uid, gid, pid, name);
	}

	/**
	 * 分组绑定设备
	 */
	@Override
	public RpcResponseDTO<Boolean> saveDevices2Group(int uid, int gid, int pid, String path, String macs) {
		logger.info(String.format("saveDevices2Group uid[%s] gid[%s] pid[%s] path [%s] macs[%s]", uid, gid, pid, path,
				macs));
		try {
			tagFacadeRpcSerivce.saveDevices2Group(uid, gid, pid, path, macs);
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
	public boolean delNode(int uid, String gids) {
		logger.info(String.format("delNode uid[%s] gids[%s]", uid, gids));
		return tagFacadeRpcSerivce.delNode(uid, gids);
	}

	/**
	 * 修改设备分组信息
	 */
	@Override
	public RpcResponseDTO<Boolean> modifyDeciceWithNode(int uid, int gid, int newGid, String macs) {
		logger.info(
				String.format("modifyDeciceWithNode uid[%s] message[%s] newGid[%s] macs[%s]", uid, gid, newGid, macs));
		try {
			tagFacadeRpcSerivce.modifyDeciceWithNode(uid, gid, newGid, macs);
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
	public TailPage<TagGroupVTO> fetchChildGroup(int uid, int pid, int pageNo, int pageSize) {
		logger.info(
				String.format("fetchChildGroup uid[%s] pid[%s] pageNo[%s] pageSize[%s]", uid, pid, pageNo, pageSize));
		return tagFacadeRpcSerivce.fetchChildGroup(uid, pid, pageNo, pageSize);
	}

	/**
	 * 查询当前节点信息
	 */
	@Override
	public TagGroupVTO currentGroupDetail(int uid, int gid) {
		logger.info(String.format("currentGroupDetail uid[%s] gid[%s]", uid, gid));
		return tagFacadeRpcSerivce.currentGroupDetail(uid, gid);
	}
}
