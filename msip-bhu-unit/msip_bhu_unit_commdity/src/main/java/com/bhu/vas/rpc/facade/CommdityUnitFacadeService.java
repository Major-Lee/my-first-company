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
import com.bhu.vas.api.dto.commdity.CommdityPhysicalDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.helper.CommdityHelper;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.CommdityPhysical;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderAmountHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderFinishCountStringService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.commdity.facade.CommdityFacadeService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.CommdityPhysicalService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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
	
	@Resource
	private CommdityPhysicalService commdityPhysicalService;
	@Resource
	private OrderFacadeService orderFacadeService;
	/**
	 * 获取商品列表
	 * @param status 商品状态
	 * @param category 商品分类
	 * @param pageNo 页码
	 * @param pageSize 每页数量
	 * @return
	 */
	public RpcResponseDTO<TailPage<CommdityDTO>> commdityPages(Integer status, Integer category, int pageNo, int pageSize){
		try{
			List<CommdityDTO> retDtos = Collections.emptyList();
			int commdity_count = commdityFacadeService.countCommdityByParam(status, category);
			if(commdity_count > 0){
				List<Commdity> commdityList = commdityFacadeService.findCommdityPageByParam(status, category, pageNo, pageSize);
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
	 * 打赏针对商品的价格区间
	 * 根据设备mac 用户mac 商品id随机金额
	 * 缓存在redis中 以保证金额不变
	 * @param commdityid 商品id
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param umactype 终端类型
	 * @return
	 */
	public RpcResponseDTO<CommdityAmountDTO> rewardIntervalAMount(Integer commdityid, String mac, 
			String umac, Integer umactype){
		try{
			//OrderHelper.supportedAppId(appid);
			OrderHelper.supportedUMacType(umactype);
			
			//验证用户mac umac
			if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(mac) || !StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			
			//获取此上下文的缓存金额数据
			String amount = RewardOrderAmountHashService.getInstance().getRAmount(mac, umac, commdityid, umactype);
			if(StringUtils.isEmpty(amount)){
				//处理商品金额
				amount = CommdityHelper.generateCommdityAmount(chargingFacadeService.fetchAmountRange(mac, umactype));
				//amount = commdityFacadeService.commdityAmount(commdityid);
				//CommdityIntervalAmountService.getInstance().addRAmount(mac, umac, commdityid, amount);
				Long addnx_ret = RewardOrderAmountHashService.getInstance().addNx_RAmount(mac, umac, commdityid, umactype, amount);
				if(addnx_ret == null || addnx_ret <= 0){
					//如果多线程问题，同一个key,setnx有可能未设置成功，则获取目前存在的金额
					amount = RewardOrderAmountHashService.getInstance().getRAmount(mac, umac, commdityid, umactype);
				}
			}
			CommdityAmountDTO commdityAmountDto = new CommdityAmountDTO();
			commdityAmountDto.setAmount(amount);
			commdityAmountDto.setSsid(chargingFacadeService.fetchWifiDeviceSharedNetworkSSID(mac));
			commdityAmountDto.setUsers_rx_rate(chargingFacadeService.fetchWifiDeviceSharedNetworkUsersRxRate(mac));
			commdityAmountDto.setUsers_tx_rate(chargingFacadeService.fetchWifiDeviceSharedNetworkUsersTxRate(mac));
			commdityAmountDto.setForceTime(chargingFacadeService.fetchAccessInternetTime(mac,umactype));
			commdityAmountDto.setUser7d(RewardOrderFinishCountStringService.getInstance().getRecent7daysValue());
			commdityAmountDto.setMonthCardAmount(CommdityHelper.
					generateCommdityAmount(chargingFacadeService.
							fetchAccessInternetCardAmountRange(BusinessRuntimeConfiguration.
									Reward_Month_Internet_Commdity_ID, umactype)));
			
			commdityAmountDto.setWeekCardAmount(CommdityHelper.
					generateCommdityAmount(chargingFacadeService.
							fetchAccessInternetCardAmountRange(BusinessRuntimeConfiguration.
									Reward_Week_Internet_Commdity_ID, umactype)));
			
			commdityAmountDto.setDayCardAmount(CommdityHelper.
					generateCommdityAmount(chargingFacadeService.
							fetchAccessInternetCardAmountRange(BusinessRuntimeConfiguration.
									Reward_Day_Internet_Commdity_ID, umactype)));
			
			logger.info(String.format("intervalAMount success commdityid[%s] "
					+ "mac[%s] umac[%s] umactype[%s] amount[%s] "
					+ "force_time[%s] 7dusers[%s] monthCardAmount[%s] "
					+ "weekCardAmount[%s] dayCardAmount[%s]", 
					commdityid, mac, umac, umactype, amount,
					commdityAmountDto.getForceTime(), 
					commdityAmountDto.getUser7d(),
					commdityAmountDto.getMonthCardAmount(),
					commdityAmountDto.getWeekCardAmount(),
					commdityAmountDto.getDayCardAmount()));
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(commdityAmountDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("RandomInternetLimitAMount Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<CommdityPhysicalDTO> physical_get_address(String umac) {
		try{
			if(StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			CommdityPhysical commdityPhysical = commdityPhysicalService.getById(umac);
			if (commdityPhysical == null){
				commdityPhysical = new CommdityPhysical();
			}
			CommdityPhysicalDTO dto = commdityPhysical.getInnerModel();
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("physical_get_address Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<CommdityPhysicalDTO> physical_set_address(String umac, String uname, String acc,
			String address, boolean needInvoice, String invoiceDetail) {
		try{
			if(StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			
			CommdityPhysical commdityPhysical = commdityPhysicalService.getById(umac);
			CommdityPhysical newcommdityPhysical = orderFacadeService.buildCommdityPhysical(umac, uname, acc, address, needInvoice, invoiceDetail);
			if (commdityPhysical != null){
				commdityFacadeService.updateCommdityPhysical(newcommdityPhysical);
			}else{
				commdityFacadeService.insertCommdityPhysical(newcommdityPhysical);
			}
			CommdityPhysicalDTO dto = newcommdityPhysical.getInnerModel();
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("physical_set_address Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
