package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.CommdityAmountDTO;
import com.bhu.vas.api.dto.commdity.CommdityDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.CommdityHelper;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityIntervalAmountService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.commdity.facade.CommdityFacadeService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class CommdityUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(CommdityUnitFacadeService.class);
	
//	@Resource
//	private CommdityService commdityService;
	
	@Resource
	private CommdityFacadeService commdityFacadeService;

	@Resource
	private ChargingFacadeService chargingFacadeService;
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
			logger.error("CommdityPages Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 针对商品的价格区间
	 * 根据设备mac 用户mac 商品id随机金额
	 * 缓存在redis中 以保证金额不变
	 * @param commdityid 商品id
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param umactype 终端类型
	 * @return
	 */
	public RpcResponseDTO<CommdityAmountDTO> intervalAMount(Integer commdityid, Integer appid, String mac, 
			String umac, Integer umactype){
		try{
			OrderHelper.supportedAppId(appid);
			
			//验证用户mac umac
			if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(mac) || !StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			
			//获取此上下文的缓存金额数据
			String amount = CommdityIntervalAmountService.getInstance().getRAmount(mac, umac, commdityid);
			if(StringUtils.isEmpty(amount)){
				//处理商品金额
				amount = CommdityHelper.generateCommdityAmount(chargingFacadeService.fetchAmountRange(mac, umactype));
				//amount = commdityFacadeService.commdityAmount(commdityid);
				//CommdityIntervalAmountService.getInstance().addRAmount(mac, umac, commdityid, amount);
				Long addnx_ret = CommdityIntervalAmountService.getInstance().addNx_RAmount(mac, umac, commdityid, amount);
				if(addnx_ret == null || addnx_ret <= 0){
					//如果多线程问题，同一个key,setnx有可能未设置成功，则获取目前存在的金额
					amount = CommdityIntervalAmountService.getInstance().getRAmount(mac, umac, commdityid);
				}
			}
			logger.info(String.format("intervalAMount success commdityid[%s] appid[%s] mac[%s] umac[%s] amount[%s]", commdityid, appid, mac, umac, amount));
			CommdityAmountDTO commdityAmountDto = new CommdityAmountDTO();
			commdityAmountDto.setAmount(amount);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(commdityAmountDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("RandomInternetLimitAMount Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
