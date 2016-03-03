package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.CommdityDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.ICommdityRpcService;
import com.bhu.vas.rpc.facade.CommdityUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("commdityRpcService")
public class CommdityRpcService implements ICommdityRpcService{
	private final Logger logger = LoggerFactory.getLogger(CommdityRpcService.class);
	
	@Resource
	private CommdityUnitFacadeService commdityUnitFacadeService;

	@Override
	public RpcResponseDTO<TailPage<CommdityDTO>> commdityPages(Integer status, int pageNo, int pageSize) {
		logger.info(String.format("commdityPages with status[%s] pageNo[%s] pageSize[%s]", status, pageNo, pageSize));
		return commdityUnitFacadeService.commdityPages(status, pageNo, pageSize);
	}
}
