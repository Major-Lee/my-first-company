package com.bhu.commdity.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.CommdityDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.business.ds.commdity.facade.CommdityFacadeService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class CommdityUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(CommdityUnitFacadeService.class);
	
	@Resource
	private CommdityService commdityService;
	
	@Resource
	private CommdityFacadeService commdityFacadeService;

	/**
	 * 获取商品列表
	 * @param status 商品状态
	 * @param pageNo 页码
	 * @param pageSize 每页数量
	 * @return
	 */
	public RpcResponseDTO<TailPage<CommdityDTO>> commdityPages(Integer status, int pageNo, int pageSize){
		try{
			List<CommdityDTO> retDtos = Collections.emptyList();
			int commdity_count = commdityFacadeService.countCommdityByStatus(status);
			if(commdity_count > 0){
				List<Commdity> commdityList = commdityFacadeService.findCommditysByStatus(status, pageNo, pageSize);
				if(commdityList != null && !commdityList.isEmpty()){
					retDtos = new ArrayList<CommdityDTO>();
					CommdityDTO commdityDto = null;
					for(Commdity commdity : commdityList){
						commdityDto = new CommdityDTO();
						BeanUtils.copyProperties(commdity, commdityDto);
						retDtos.add(commdityDto);
					}
				}
			}
			TailPage<CommdityDTO> returnRet = new CommonPage<CommdityDTO>(pageNo, pageSize, commdity_count, retDtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
