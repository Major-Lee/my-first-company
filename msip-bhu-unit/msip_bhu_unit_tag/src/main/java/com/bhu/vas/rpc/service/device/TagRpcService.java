package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.api.vto.BackendTaskVTO;
import com.bhu.vas.rpc.facade.TagFacadeRpcSerivce;
import com.smartwork.msip.cores.orm.support.page.TailPage;
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
		logger.info(
				String.format("bindTag uid[%s] mac[%s] tag[%s]",uid, mac,tag));
		try {
			tagFacadeRpcSerivce.bindTag(uid, mac, tag);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}

	}

	@Override
	public RpcResponseDTO<TailPage<TagNameVTO>> fetchTag(int pageNo, int pageSize) {
		logger.info(String.format("fetchTag pageNo[%s] pageSize[%s]",pageNo,pageSize));
		
		TailPage<TagNameVTO> tagName = tagFacadeRpcSerivce.fetchTag(pageNo, pageSize);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(tagName);
		
	}
	

}
