package com.bhu.vas.api.rpc.tag.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
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

}	
