package com.bhu.vas.rpc.service.advertise;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.rpc.facade.AdvertiseUnitFacadeService;

@Service("advertiseRpcService")
public class advertiseRpcService implements IAdvertiseRpcService{
	private final Logger logger = LoggerFactory.getLogger(advertiseRpcService.class);
	@Resource
	private AdvertiseUnitFacadeService advertiseUnitFacadeService;
	@Override
	public RpcResponseDTO<Map<String, Object>> createNewAdvertise(int uid,
			String image, String url, String province, String city,
			String district, String start, String end) {
		logger.info(String.format("createNewAdvertise with uid[%s] image[%s] url[%s] province[%s] city[%s] district[%s] start[%s] start[%s]",
				uid, image, url, province, city, district, start, end));
		return advertiseUnitFacadeService.createNewAdvertise(uid, image, url, province, city, district, start, end);
	}
	
}
