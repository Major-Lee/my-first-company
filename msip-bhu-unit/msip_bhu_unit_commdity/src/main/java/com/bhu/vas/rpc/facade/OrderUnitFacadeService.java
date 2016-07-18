package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderDetailDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardNewlyDataVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardVTO;
import com.bhu.vas.api.dto.commdity.OrderSMSVTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.OrderPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.api.vto.statistics.RewardOrderStatisticsVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.CommdityMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderFinishCountStringService;
import com.bhu.vas.business.ds.commdity.facade.CommdityFacadeService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class OrderUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(OrderUnitFacadeService.class);
	
	@Resource
	private UserService userService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	
	@Resource
	private CommdityFacadeService commdityFacadeService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;

	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private CommdityMessageService commdityMessageService;
	
	@Resource
	private UserWifiDeviceService userWifiDeviceService;
	
	/**
	 * 生成打赏订单
	 * @param commdityid 商品id
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param payment_type 支付方式
	 * @param context 业务上下文
	 * @param user_agent
	 * @return
	 */
	public RpcResponseDTO<OrderRewardVTO> createRewardOrder(Integer commdityid, String mac, String umac, 
			Integer umactype, String payment_type, String context, String user_agent){
		try{
			//orderFacadeService.supportedAppId(appid);
			//验证mac umac
			if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(mac) || !StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			
			String mac_lower = mac.toLowerCase();
			String umac_lower = umac.toLowerCase();
			//检查设备是否接入过
			WifiDevice wifiDevice = wifiDeviceService.getById(mac_lower);
			if(wifiDevice == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
			}
			
			User bindUser = userWifiDeviceFacadeService.findUserById(mac_lower);
			
			//生成订单
			String mac_dut = WifiDeviceHelper.dutDevice(wifiDevice.getOrig_swver());
			Order order = orderFacadeService.createRewardOrder(commdityid, BusinessEnumType.CommdityApplication.DEFAULT.getKey(), 
					bindUser, mac_lower, mac_dut, umac_lower, umactype, payment_type, context, user_agent);
			
			commdityMessageService.sendOrderCreatedMessage(order.getId());
			OrderRewardVTO orderVto = new OrderRewardVTO();
			BeanUtils.copyProperties(order, orderVto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderVto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("CreateRewardOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据用户终端mac地址进行订单的状态查询
	 * @param umac 用户mac
	 * @param orderId 订单id
	 * @return
	 */
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUmac(String umac, String orderid) {
		try{
			//验证订单是否合法
			Order order = orderFacadeService.validateOrderId(orderid);
			if(!umac.equals(order.getUmac())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UMAC_INVALID);
			}
			//验证商品是否合法
			Commdity commdity = commdityFacadeService.validateCommdity(order.getCommdityid());
			
			OrderStatusDTO orderStatusDto = OrderHelper.buildOrderStatusDTO(order, commdity);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderStatusDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderStatusByUmac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据设备mac查询打赏订单分页列表
	 * @param uid 用户id
	 * @param mac 用户绑定的设备mac
	 * @param umac 订单支付用户的终端mac
	 * @param status 订单状态
	 * @param dut 设备业务线
	 * @param pageNo 页码
	 * @param pageSize 分页数量
	 * @return
	 */
	public RpcResponseDTO<TailPage<OrderRewardVTO>> rewardOrderPages(Integer uid, String mac, String umac, 
			Integer status, String dut, int pageNo, int pageSize) {
		try{
			List<OrderRewardVTO> retDtos = Collections.emptyList();
			int order_count = orderFacadeService.countOrderByParams(uid, mac, umac, status, dut, 
					CommdityCategory.RewardInternetLimit.getCategory(), 0);
			if(order_count > 0){
				List<Order> orderList = orderFacadeService.findOrdersByParams(uid, mac, umac, status, dut, 
						CommdityCategory.RewardInternetLimit.getCategory(), pageNo, pageSize);
				
				if(orderList != null && !orderList.isEmpty()){
					List<String> orderids = new ArrayList<String>();
					for(Order order : orderList){
						orderids.add(order.getId());
					}
					List<UserWalletLog> walletLogs = null;
					{
						ModelCriteria mc_wallet_log = new ModelCriteria();
						mc_wallet_log.createCriteria().andColumnNotEqualTo("uid", WifiDeviceSharedealConfigs.Default_Manufacturer).andColumnIn("orderid", orderids).andSimpleCaulse(" 1=1 ");
						walletLogs = userWalletFacadeService.getUserWalletLogService().findModelByModelCriteria(mc_wallet_log);
					}
					retDtos = new ArrayList<OrderRewardVTO>();
					OrderRewardVTO orderRewardVto = null;
					for(Order order : orderList){
						orderRewardVto = new OrderRewardVTO();
						BeanUtils.copyProperties(order, orderRewardVto);
						orderRewardVto.setUmac_mf(MacDictParserFilterHelper.prefixMactch(order.getUmac(),true,false));
						OrderPaymentType orderPaymentType = OrderPaymentType.fromKey(order.getPayment_type());
						if(orderPaymentType != null){
							orderRewardVto.setPayment_type_name(orderPaymentType.getDesc());
						}
						orderRewardVto.setShare_amount(distillOwnercash(order.getId(),walletLogs));
						if(order.getCreated_at() != null){
							orderRewardVto.setCreated_ts(order.getCreated_at().getTime());
						}
						if(order.getPaymented_at() != null){
							orderRewardVto.setPaymented_ts(order.getPaymented_at().getTime());
						}
						retDtos.add(orderRewardVto);
					}
				}
			}
			TailPage<OrderRewardVTO> returnRet = new CommonPage<OrderRewardVTO>(pageNo, pageSize, order_count, retDtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("RewardOrderPagesByUid Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<RewardOrderStatisticsVTO> rewardOrderStatisticsBetweenDate(String start_date, String end_date) {
		try{
			RewardOrderStatisticsVTO vto = orderFacadeService.rewardOrderStatisticsWithProcedure(start_date, end_date);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("RewardOrderStatisticsBetweenDate Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	
	/**
	 * 生成充值虎钻订单
	 * @param uid 用户id
	 * @param commdityid 商品id
	 * @param payment_type 支付方式
	 * @param umactype
	 * @param user_agent
	 * @return
	 */
	public RpcResponseDTO<OrderRechargeVCurrencyVTO> createRechargeVCurrencyOrder(Integer uid, Integer commdityid,
			String payment_type, Integer umactype, String user_agent){
		try{
			//orderFacadeService.supportedAppId(appid);
			User user = userService.getById(uid);
			if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_NOTEXIST);
			}
/*			//验证商品是否合法
			Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
			//验证商品是否合理
			if(!CommdityCategory.correct(commdity.getCategory(), CommdityCategory.RechargeVCurrency)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_DATA_ILLEGAL);
			}*/
			
			Order order = orderFacadeService.createRechargeVCurrencyOrder(uid, commdityid, 
					BusinessEnumType.CommdityApplication.BHU_PREPAID_BUSINESS.getKey(), payment_type, umactype, user_agent);
			
			commdityMessageService.sendOrderCreatedMessage(order.getId());
			OrderRechargeVCurrencyVTO orderVto = new OrderRechargeVCurrencyVTO();
			BeanUtils.copyProperties(order, orderVto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderVto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("CreateRechargeVCurrencyOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Integer> rewardOrderFinishCountRecent7Days() {
		try{
			Date Ago7Date = DateTimeHelper.getDateDaysAgo(7);
			int count = orderFacadeService.countOrderByDateParams(OrderStatus.DeliverCompleted.getKey(), 
					CommdityCategory.RewardInternetLimit.getCategory(), Ago7Date, null);
			RewardOrderFinishCountStringService.getInstance().refreshRecent7daysValue(count);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(count);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("rewardOrderFinishCountRecent7Days Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据设备条件查询充值虎钻订单分页列表
	 * @param uid 用户id
	 * @param status 订单状态
	 * @param type 订单类型
	 * @param pageNo 页码
	 * @param pageSize 分页数量
	 * @return
	 */
	public RpcResponseDTO<TailPage<OrderRechargeVCurrencyVTO>> rechargeVCurrencyOrderPages(Integer uid, 
			Integer status,  int pageNo, int pageSize) {
		try{
			List<OrderRechargeVCurrencyVTO> retDtos = Collections.emptyList();
			int order_count = orderFacadeService.countOrderByParams(uid, null, null, status, 
					null, CommdityCategory.RechargeVCurrency.getCategory(), 0);
			if(order_count > 0){
				List<Order> orderList = orderFacadeService.findOrdersByParams(uid, null, null, status, null,
						CommdityCategory.RechargeVCurrency.getCategory(), pageNo, pageSize);
				
				if(orderList != null && !orderList.isEmpty()){
					retDtos = new ArrayList<OrderRechargeVCurrencyVTO>();
					OrderRechargeVCurrencyVTO rvOrderVto = null;
					for(Order order : orderList){
						rvOrderVto = new OrderRechargeVCurrencyVTO();
						BeanUtils.copyProperties(order, rvOrderVto);
						OrderPaymentType orderPaymentType = OrderPaymentType.fromKey(order.getPayment_type());
						if(orderPaymentType != null){
							rvOrderVto.setPayment_type_name(orderPaymentType.getDesc());
						}
						if(order.getCreated_at() != null){
							rvOrderVto.setCreated_ts(order.getCreated_at().getTime());
						}
						if(order.getPaymented_at() != null){
							rvOrderVto.setPaymented_ts(order.getPaymented_at().getTime());
						}
						retDtos.add(rvOrderVto);
					}
				}
			}
			TailPage<OrderRechargeVCurrencyVTO> returnRet = new CommonPage<OrderRechargeVCurrencyVTO>(pageNo, pageSize, order_count, retDtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("RechargeVCurrencyOrderPagesByUid Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据用户id地址进行订单的状态查询
	 * @param uid 用户id
	 * @param orderId 订单id
	 * @return
	 */
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUid(Integer uid, String orderid) {
		try{
			Order order = orderFacadeService.validateOrderId(orderid);
			
			if(!uid.equals(order.getUid())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UID_INVALID);
			}
			//验证商品是否合法
			Commdity commdity = commdityFacadeService.validateCommdity(order.getCommdityid());
			
			OrderStatusDTO orderStatusDto = OrderHelper.buildOrderStatusDTO(order, commdity);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderStatusDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderStatusByUmac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据用户id地址进行订单的细节
	 * @param uid
	 * @param orderid
	 * @return
	 */
	public RpcResponseDTO<OrderDetailDTO> orderDetailByUid(Integer uid, String orderid) {
		try{
			Order order = orderFacadeService.validateOrderId(orderid);
			
			if(!uid.equals(order.getUid())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UID_INVALID);
			}
			//验证商品是否合法
			Commdity commdity = commdityFacadeService.validateCommdity(order.getCommdityid());
			
			UserWifiDevice userWifiDevice= null;
			if (order.getMac() != null) {
				 userWifiDevice = userWifiDeviceService.getById(order.getMac());
			}
			OrderDetailDTO orderStatusDto = OrderHelper.buildOrderDetailDTO(order, commdity, userWifiDevice);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderStatusDto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderStatusByUmac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 生成短信认证订单
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param umactype 用户终端类型
	 * @param context 验证的手机号
	 * @param user_agent
	 * @param spendvcurrency 是否花费虎钻
	 * @return
	 */
	public RpcResponseDTO<OrderSMSVTO> createSMSOrder(String mac, String umac, Integer umactype, 
			String context, String user_agent, boolean spendvcurrency){
		try{
			//orderFacadeService.supportedAppId(appid);
			//验证mac umac
			if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(mac) || !StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			
			String mac_lower = mac.toLowerCase();
			String umac_lower = umac.toLowerCase();
			//检查设备是否接入过
			WifiDevice wifiDevice = wifiDeviceService.getById(mac_lower);
			if(wifiDevice == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
			}
			User bindUser = userWifiDeviceFacadeService.findUserById(mac_lower);
			//生成订单
			String mac_dut = WifiDeviceHelper.dutDevice(wifiDevice.getOrig_swver());
			Order order = orderFacadeService.createSMSOrder(mac_lower, mac_dut, umac_lower, umactype, bindUser,
					context, user_agent, spendvcurrency);
			
			commdityMessageService.sendOrderCreatedMessage(order.getId());
			
			OrderSMSVTO orderVto = new OrderSMSVTO();
			BeanUtils.copyProperties(order, orderVto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderVto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("CreateSMSOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 根据设备mac查询打赏订单分页列表
	 * @param uid 用户id
	 * @param mac 用户绑定的设备mac
	 * @param umac 订单支付用户的终端mac
	 * @param status 订单状态
	 * @param dut 设备业务线
	 * @param pageNo 页码
	 * @param pageSize 分页数量
	 * @return
	 */
	public RpcResponseDTO<TailPage<OrderSMSVTO>> smsOrderPages(Integer uid, String mac, String umac, 
			Integer status, String dut, int pageNo, int pageSize) {
		try{
			List<OrderSMSVTO> retDtos = Collections.emptyList();
			int order_count = orderFacadeService.countOrderByParams(uid, mac, umac, status, 
					dut, CommdityCategory.SMSInternetLimit.getCategory(), 0);
			if(order_count > 0){
				List<Order> orderList = orderFacadeService.findOrdersByParams(uid, mac, umac, status, dut, 
						CommdityCategory.SMSInternetLimit.getCategory(), pageNo, pageSize);
				
				if(orderList != null && !orderList.isEmpty()){
					retDtos = new ArrayList<OrderSMSVTO>();
					OrderSMSVTO orderSMSVto = null;
					for(Order order : orderList){
						orderSMSVto = new OrderSMSVTO();
						BeanUtils.copyProperties(order, orderSMSVto);
						orderSMSVto.setUmac_mf(MacDictParserFilterHelper.prefixMactch(order.getUmac(),true,false));
						if(order.getCreated_at() != null){
							orderSMSVto.setCreated_ts(order.getCreated_at().getTime());
						}
						if(order.getPaymented_at() != null){
							orderSMSVto.setPaymented_ts(order.getPaymented_at().getTime());
						}
						retDtos.add(orderSMSVto);
					}
				}
			}
			TailPage<OrderSMSVTO> returnRet = new CommonPage<OrderSMSVTO>(pageNo, pageSize, order_count, retDtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("RewardOrderPagesByUid Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<OrderRewardNewlyDataVTO> rewardOrderNewlyDataByUid(Integer uid, long start_created_ts) {
		try{
			OrderRewardNewlyDataVTO vto = null;
			if(start_created_ts > 0){
				vto = orderFacadeService.rewardOrderNewlyDataWithProcedure(uid, new Date(start_created_ts));
			}else{
				vto = new OrderRewardNewlyDataVTO();
			}
//			int count = orderFacadeService.countOrderByParams(uid, null, null, status, 
//					null, CommdityCategory.RewardInternetLimit.getCategory(), start_created_ts);
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderStatusByUmac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	private String distillOwnercash(String orderid,List<UserWalletLog> walletLogs){
		if(walletLogs != null && !walletLogs.isEmpty()){
			for(UserWalletLog log:walletLogs){
				if(orderid.equals(log.getOrderid())){
					String cash = log.getCash();
					if(StringUtils.isNotEmpty(cash) && cash.startsWith(StringHelper.PLUS_STRING_GAP) && cash.length()>=1){
						return cash.substring(1);
					}else
						return log.getCash();
				}
			}
		}
		//return StringHelper.MINUS_STRING_GAP;
		return "0";
	}
	
	/*public static void main(String[] argv){
		System.out.println("+0.84".substring(1));
	}*/
}
