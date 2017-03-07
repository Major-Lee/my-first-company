package com.bhu.vas.rpc.facade;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.dto.commdity.CommdityOrderCommonVTO;
import com.bhu.vas.api.dto.commdity.HotPlayOrderVTO;
import com.bhu.vas.api.dto.commdity.OrderDetailDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardNewlyDataVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardVTO;
import com.bhu.vas.api.dto.commdity.OrderSMSVTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.OrderVideoVTO;
import com.bhu.vas.api.dto.commdity.OrderWhiteListVTO;
import com.bhu.vas.api.dto.commdity.QueryBalanceLogsVTO;
import com.bhu.vas.api.dto.commdity.RewardCreateMonthlyServiceVTO;
import com.bhu.vas.api.dto.commdity.RewardQueryExportRecordVTO;
import com.bhu.vas.api.dto.commdity.RewardQueryPagesDetailVTO;
import com.bhu.vas.api.dto.commdity.TechServiceDataDTO;
import com.bhu.vas.api.dto.commdity.UserValidateCaptchaDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseVideoValidateCompletedNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.OrderPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.helper.BusinessEnumType.PaymentChannelType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.PaymentNotifyFactoryBuilder;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.commdity.vto.QualityGoodsSharedealListVTO;
import com.bhu.vas.api.rpc.commdity.vto.QualityGoodsSharedealVTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.dto.UserInnerExchangeDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.api.vto.advertise.AdCommdityVTO;
import com.bhu.vas.api.vto.statistics.RewardOrderStatisticsVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.CommdityMessageService;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.OrdersFinishCountStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderFinishCountStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.UserQueryDateHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.advertise.facade.AdvertiseFacadeService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.commdity.facade.CommdityFacadeService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserConsumptiveWalletFacadeService;
import com.bhu.vas.business.ds.user.facade.UserIdentityAuthFacadeService;
import com.bhu.vas.business.ds.user.facade.UserSignInOrOnFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.JNIRsaHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.DevicesSet;
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
	
	@Resource
	private UserWalletLogService userWalletLogService;
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	
	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;
	
	@Resource
	private UserIdentityAuthService userIdentityAuthService;
	
	@Resource
	private UserIdentityAuthFacadeService userIdentityAuthFacadeService;
	
	@Resource
	private AdvertiseFacadeService advertiseFacadeService;
	
	@Resource 
	private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;
	
	@Resource
	private UserConsumptiveWalletFacadeService userConsumptiveWalletFacadeService;
	
	@Resource
	private UserSignInOrOnFacadeService userSignInOrOnFacadeService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
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
			Integer umactype, String payment_type, String context, String user_agent, Integer channel){
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
			String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
			//商品信息验证
			//验证商品是否合法
			Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
			
			Order order = orderFacadeService.createRewardOrder(commdity, BusinessEnumType.CommdityApplication.DEFAULT.getKey(), 
					bindUser, mac_lower, mac_dut, umac_lower, umactype, payment_type, context, user_agent, channel);
				
			commdityMessageService.sendOrderCreatedMessage(order.getId());

			OrderRewardVTO orderVto = new OrderRewardVTO();
			BeanUtils.copyProperties(order, orderVto);
			orderVto.setGoods_name(commdity.getName());
			orderVto.setName_key(commdity.getName_key());
			
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
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize) {
		try{
			
			String start_time = null;
			String end_time = null;
			if (start_created_ts != 0)
				start_time = DateTimeHelper.formatDate(new Date(start_created_ts), DateTimeHelper.DefalutFormatPattern);
			if (end_created_ts != 0)
				end_time = DateTimeHelper.formatDate(new Date(end_created_ts), DateTimeHelper.DefalutFormatPattern);
			Map<String, Object> map = userWalletLogService.getEntityDao().fetchCashSumAndCountByUid(uid, start_time, 
					end_time, mac,umac,status,dut,UWalletTransMode.SharedealPayment.getKey(),null);
			Long total = (Long)map.get("count");
			TailPage<OrderRewardVTO> returnRet = queryOrdersByUid(total, uid, mac, umac, 
					status, dut, start_created_ts, end_created_ts, pageNo, pageSize);
			
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
			
			//commdityMessageService.sendOrderCreatedMessage(order.getId());
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
					null, CommdityCategory.RechargeVCurrency.getCategory(), 0,0);
			if(order_count > 0){
				List<Order> orderList = orderFacadeService.findOrdersByParams(uid, null, null, status, null,
						CommdityCategory.RechargeVCurrency.getCategory(), 0,0, pageNo, pageSize);
				
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
				WifiDeviceSharedealConfigs sharedeal = wifiDeviceSharedealConfigsService.getById(order.getMac());
				if (sharedeal != null){
					if(!uid.equals(sharedeal.getDistributor()) && !uid.equals(sharedeal.getDistributor_l2())){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_UID_INVALID);
					}
				}
			}
			
			//验证商品是否合法
			Commdity commdity = commdityFacadeService.validateCommdity(order.getCommdityid());
			
			UserWifiDevice userWifiDevice= null;
			if (order.getMac() != null) {
				 userWifiDevice = userWifiDeviceService.getById(order.getMac());
			}
			OrderDetailDTO orderStatusDto = OrderHelper.buildOrderDetailDTO(order, commdity, userWifiDevice);
			if (order.getUmac() != null) {
				orderStatusDto.setUmac_mf(MacDictParserFilterHelper.prefixMactch(order.getUmac(),true,false));
			}
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
			String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
			Order order = orderFacadeService.createSMSOrder(mac_lower, mac_dut, umac_lower, umactype, bindUser,
					context, user_agent, spendvcurrency);
			
			commdityMessageService.sendOrderCreatedMessage(order.getId());
			asyncDeliverMessageService.sendUserIdentityRepariActionMessage(umac_lower,context.substring(3));
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
					dut, CommdityCategory.SMSInternetLimit.getCategory(), 0,0);
			if(order_count > 0){
				List<Order> orderList = orderFacadeService.findOrdersByParams(uid, mac, umac, status, dut, 
						CommdityCategory.SMSInternetLimit.getCategory(), 0,0, pageNo, pageSize);
				
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
	
	public RpcResponseDTO<OrderRewardNewlyDataVTO> rewardOrderNewlyDataByUid(Integer uid) {
		try{
			
			String str = UserQueryDateHashService.getInstance().fetchLastQueryData(uid);
			long timestamp = 0L;
			if (str != null) {
				timestamp = Long.parseLong(str);
			}
			
			if (timestamp == 0) {
				timestamp = DateTimeHelper.getDateDaysAgo(1).getTime();
			}
			
			OrderRewardNewlyDataVTO vto = null;
			if(timestamp > 0){
				vto = orderFacadeService.rewardOrderNewlyDataWithProcedure(uid,new Date(timestamp));
			}else{
				vto = new OrderRewardNewlyDataVTO();
			}
			
			UserQueryDateHashService.getInstance().addQueryData(uid, System.currentTimeMillis());
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("OrderStatusByUmac Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<RewardQueryPagesDetailVTO> rewardOrderPagesDetail(Integer uid, String mac, String umac,
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize){
		try{
			RewardQueryPagesDetailVTO vto = new RewardQueryPagesDetailVTO();
			String start_time = null;
			String end_time = null;
			if (start_created_ts != 0)
				start_time = DateTimeHelper.formatDate(new Date(start_created_ts), DateTimeHelper.DefalutFormatPattern);
			if (end_created_ts != 0)
				end_time = DateTimeHelper.formatDate(new Date(end_created_ts), DateTimeHelper.DefalutFormatPattern);
			Map<String, Object> map = userWalletLogService.getEntityDao().fetchCashSumAndCountByUid(uid, start_time, 
					end_time, mac,umac,status,dut,UWalletTransMode.SharedealPayment.getKey(),null);
			vto.setCashSum((Double)map.get("cashSum"));
			Long vto_count = (Long)map.get("count");
			vto.setCount(vto_count);
			logger.info("rewardOrderPagesDetail CashSum: "+vto.getCashSum()+" Count: "+vto.getCount());
			
			TailPage<OrderRewardVTO> returnRet = queryOrdersByUid(vto_count, uid, mac, umac,
				 status, dut, start_created_ts, end_created_ts, pageNo, pageSize);
			
			vto.setTailPages(returnRet);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("rewardOrderPagesDetail Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	private TailPage<OrderRewardVTO> queryOrdersByUid(Long total, Integer uid, String mac, String umac,
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize){
		
		List<OrderRewardVTO> retDtos = Collections.emptyList();
		if (total.intValue() > 0){
			List<Map<String, Object>> logs = userWalletLogService.getEntityDao().queryRewardOrderpages(uid, mac, 
					umac, status, dut, UWalletTransMode.SharedealPayment.getKey(), null,
					start_created_ts, end_created_ts, pageNo, pageSize);
			OrderRewardVTO orderRewardVto = null;
			retDtos = new ArrayList<OrderRewardVTO>();
			for(Map<String, Object> log : logs){
				orderRewardVto = new OrderRewardVTO();
				orderRewardVto.setId((String)log.get("orderid"));
				orderRewardVto.setCommdityid((Integer)log.get("commdityid"));
				orderRewardVto.setAppid((Integer)log.get("appid"));
				orderRewardVto.setChannel((Integer)log.get("channel"));
				orderRewardVto.setUid((Integer)log.get("uid"));
				orderRewardVto.setType((Integer)log.get("type"));
				orderRewardVto.setContext((String)log.get("context"));
				orderRewardVto.setStatus((Integer)log.get("status"));
				
				orderRewardVto.setRole((String)log.get("role"));
				orderRewardVto.setTransmode((String)log.get("transmode"));
				orderRewardVto.setTranstype((String)log.get("transtype"));
				
				if (StringHelper.isValidMac((String)log.get("mac")))
					orderRewardVto.setMac((String)log.get("mac"));
				else
					orderRewardVto.setMac(StringHelper.MINUS_STRING_GAP);
				String logUmac = (String)log.get("umac");
				if (StringHelper.isValidMac(logUmac))
					orderRewardVto.setUmac(logUmac);
				else
					orderRewardVto.setUmac(StringHelper.MINUS_STRING_GAP);
				if (logUmac != null){
					orderRewardVto.setUmac_mf(MacDictParserFilterHelper.prefixMactch((String)log.get("umac"),true,false));
				}else{
					orderRewardVto.setUmac_mf(DevicesSet.Unknown.getScn());
				}
				orderRewardVto.setPayment_type((String)log.get("payment_type"));
				OrderPaymentType orderPaymentType = OrderPaymentType.fromKey((String)log.get("payment_type"));
				if(orderPaymentType != null){
					orderRewardVto.setPayment_type_name(orderPaymentType.getDesc());
				}
				orderRewardVto.setAmount((String)log.get("amount"));
				orderRewardVto.setShare_amount((String)log.get("cash"));
				long paymented_ts = DateTimeHelper.parseDate((String)log.get("paymented_at"), DateTimeHelper.DefalutFormatPattern).getTime();
				orderRewardVto.setPaymented_ts(paymented_ts);
				String logCreatedTs = (String)log.get("created_at");
				long created_ts = paymented_ts;
				if (logCreatedTs != null){
					created_ts = DateTimeHelper.parseDate(logCreatedTs, DateTimeHelper.DefalutFormatPattern).getTime();
				}
				orderRewardVto.setCreated_ts(created_ts);
				orderRewardVto.setUmactype((Integer)log.get("umactype"));
				retDtos.add(orderRewardVto);
			}
		}
		return new CommonPage<OrderRewardVTO>(pageNo, pageSize, total.intValue(), retDtos);
	}
	
	public	RpcResponseDTO<RewardQueryExportRecordVTO> rewardQueryExportRecord(Integer uid, String mac, String umac, 
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize){
		try{
			RewardQueryExportRecordVTO vto = new RewardQueryExportRecordVTO();
			if (start_created_ts == 0 || end_created_ts == 0){
				Date dateYesterday = DateTimeHelper.getDateDaysAgo(1);
				String yesterdayStr = DateTimeHelper.formatDate(dateYesterday, "yyyy-MM-dd 00:00:00");
				Date parse = DateTimeHelper.longDateFormat.parse(yesterdayStr);
				start_created_ts = parse.getTime();
				end_created_ts = System.currentTimeMillis();
				logger.info(String.format("rewardQueryExportRecord default time start_ts[%s] end_ts[%s]", start_created_ts,end_created_ts));
			}
			List<Map<String, Object>> logs = userWalletLogService.getEntityDao().queryRewardOrderpages(uid, mac, 
					umac, status, dut, UWalletTransMode.SharedealPayment.getKey(), null,
					start_created_ts, end_created_ts, pageNo, pageSize);
			List<String> recordList = Collections.emptyList();
			if(logs != null && !logs.isEmpty()){
				recordList = outputOrderStringByItem(logs);
			}
			String filename = String.format("%s_%s.csv",DateTimeHelper.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd_HH_mm_ss"),uid);
			vto.setFilename(filename);
			String url = String.format("%s%s",EXPORT_REWARD_RECORD_URL,filename);
			vto.setUrl(url);
			File file = searchResultExportFile(uid,filename,RewardOrderResultExportColumns,recordList);
			byte[] bs = getFileBytes(file);
			vto.setBs(bs);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("rewardQueryExportRecord Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	public File searchResultExportFile(Integer uid,String filename,String[] columns, List<String> lines) {
		String export_filepath = String.format("/%s/%s/%s/%s", "BHUData","rewardexport",uid,filename);
		BufferedWriter fw = null;
		FileHelper.makeDirectory(export_filepath);
		File file = new File(export_filepath);
		try {
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(0xEF);
			fos.write(0xBB);
			fos.write(0xBF);
			fos.flush();
			fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			int columns_length = columns.length;
			for(int i = 0;i<columns_length;i++){
				if((i+1) == columns_length){
					fw.append(formatStr(columns[i], false));
				}else{
					fw.append(formatStr(columns[i]));
				}
			}
			fw.newLine();
			for(String item : lines){
				fw.append(item);
				fw.newLine();
			}
			fw.flush(); // 全部写入缓存中的内容
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
	
	private List<String> outputOrderStringByItem(List<Map<String, Object>> orderList){
		List<String> recordList = new ArrayList<String>();
		for(Map<String, Object> order : orderList){
			StringBuffer bw = new StringBuffer();
			String umac = (String)order.get("umac");
			String paymented_at = (String)order.get("paymented_at");
			String mac = (String)order.get("mac");
			String cash = (String)order.get("cash");
			String payment_type = (String)order.get("payment_type");
			//厂家
			if (umac != null){
				bw.append(formatStr(MacDictParserFilterHelper.prefixMactch(umac,true,false)));
			}else{
				bw.append(formatStr(DevicesSet.Unknown.getScn()));
			}
			//打赏时间
			if (paymented_at != null) {
				Date parseDate = DateTimeHelper.parseDate(paymented_at, DateTimeHelper.DefalutFormatPattern);
				bw.append(formatStr(DateTimeHelper.formatDate(parseDate, DateTimeHelper.DefalutFormatPattern)));
			}
			else
				bw.append(formatStr(""));
			//打赏收益
			bw.append(formatStr(cash));
			//打赏方式
			OrderPaymentType orderPaymentType = OrderPaymentType.fromKey(payment_type);
			if(orderPaymentType == null){
				orderPaymentType = OrderPaymentType.Unknown;
			}
			bw.append(formatStr(orderPaymentType.getDesc()));
			//设备mac
			bw.append(formatStr(mac));
			//终端mac
			bw.append(formatStr(umac,false));
			recordList.add(bw.toString());
		}
		return recordList;
	}
	
	private static String formatStr(String str, boolean split) {
		if(str == null) str = StringHelper.EMPTY_STRING_GAP;
		
		StringBuffer formatStr = new StringBuffer();
		formatStr.append(str);
		if(split)
			formatStr.append(StringHelper.COMMA_STRING_GAP);
		return formatStr.toString();
	}
	
	private static String formatStr(String str) {
		return formatStr(str, true);
	}
	public static final String[] RewardOrderResultExportColumns = new String[]{"厂家","打赏时间","打赏收益(元)","打赏方式","打赏设备","终端MAC"};
	public byte[] getFileBytes(File file){
		byte[] buffer = null;
		try{
			FileInputStream fis = new FileInputStream(file);  
	        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
	        byte[] b = new byte[1000];  
	        int n;  
	        while ((n = fis.read(b)) != -1) {  
	            bos.write(b, 0, n);  
	        }  
	        fis.close();  
	        bos.close();  
	        buffer = bos.toByteArray();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
    	return buffer;  
	}
	public static final String EXPORT_REWARD_RECORD_URL = "http://obklbhh9z.bkt.clouddn.com/";
	
	public RpcResponseDTO<OrderVideoVTO> createVideoOrder(Integer commdityid,String mac, String umac, Integer umactype, 
			String context, Integer channel,String user_agent){
		try{
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
			
			ParamSharedNetworkDTO psn = chargingFacadeService.getParamSharedNetwork(mac_lower);
			if (!chargingFacadeService.fetchDeviceIsOpenFreeMode(psn)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_DEVICE_ISFREE_STATUS_INVALID);
			}
			User bindUser = userWifiDeviceFacadeService.findUserById(mac_lower);
			//生成订单
			String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
			PaymentChannelType channelType = BusinessEnumType.PaymentChannelType.fromKey(channel);
			Integer newChannel = channel;
			switch (channelType) {
			case CARDREWARD:
				newChannel = PaymentChannelType.BHUWIFIWEB.getChannel();
				break;
			case OTHERS:
				newChannel = PaymentChannelType.UTOOL.getChannel();
				break;
			default:
				break;
			}
			
			Order order = orderFacadeService.createVideoOrder(commdityid,mac_lower, mac_dut, umac_lower, umactype, bindUser,
					context, newChannel,user_agent);
			
			commdityMessageService.sendOrderCreatedMessage(order.getId());
			
			OrderVideoVTO orderVto = new OrderVideoVTO();
			orderVto.setId(order.getId());
			orderVto.setForceTime(chargingFacadeService.fetchFreeAccessInternetTime(psn,umactype));
			orderVto.setUser7d(OrdersFinishCountStringService.getInstance().fetchOrdersFinishRecent7Days()+"");
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(orderVto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("CreateVideoOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> authorizeVideoOrder(String token, String context) {
		try{
			boolean result = false;
			String orderid = JNIRsaHelper.jniRsaDecryptHexStr(token);
			logger.info(String.format("authorizeVideoOrder Decrypt orderid[%s]", orderid));
			Order order = orderService.getById(orderid);
			if (order == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST);
			}
			//验证不是免费上网类型订单,返回错误
			if (!order.getType().equals(BusinessEnumType.CommdityCategory.VideoInternetLimit.getCategory())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST);
			}
			if (order.getStatus().intValue() == OrderStatus.DeliverCompleted.getKey().intValue()){
				logger.info(String.format("authorizeVideoOrder orderid[%s] timeout", orderid));
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_ORDER_TIMEOUT);
			}
//			2016-10-12按需求取消时间最短15秒的限制
//			long isExpire = System.currentTimeMillis() - order.getCreated_at().getTime();
//			if (isExpire < 15000){
//				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_STATUS_INVALID);
//			}
			order.setStatus(OrderStatus.PaySuccessed.getKey());
			order.setProcess_status(OrderProcessStatus.PaySuccessed.getKey());
			order.setPaymented_at(new Date());
			String notify_message = PaymentNotifyFactoryBuilder.toJsonHasPrefix(ResponseVideoValidateCompletedNotifyDTO.
					builder(order));
			CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
			result = true;
			logger.info(String.format("authorizeVideoOrder successful orderid[%s]", orderid));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
		}catch(Exception ex){
			logger.error("authorizeVideoOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<RewardCreateMonthlyServiceVTO> rewardCreateMonthlyService(Integer commdityid, String mac,
			String umac, String context, Integer umactype, Integer channel, 
			String user_agent, String uname, String acc, String address, int count, boolean needInvoice, String invoiceDetail) {
		try{
			//验证mac umac
			if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(mac) || !StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
//			if (!PhoneHelper.isValidPhoneCharacter(86, acc)){
//				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_INVALID_FORMAT);
//			}
			if (count < 0 ){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"count"});
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
			String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
			Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
			Order order = orderFacadeService.createMonthlyServiceOrder(commdity,mac_lower, mac_dut, umac_lower, umactype, bindUser,
					context, channel, user_agent, count, acc, uname, address, needInvoice, invoiceDetail);
			
			RewardCreateMonthlyServiceVTO vto = new RewardCreateMonthlyServiceVTO();
			vto.setOrderid(order.getId());
			vto.setAmount(order.getAmount());
			vto.setAppid(order.getAppid());
			vto.setGoods_name(commdity.getName());
			vto.setName_key(commdity.getName_key());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(Exception ex){
			logger.error("rewardCreateMonthlyService Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<OrderWhiteListVTO> createWhiteListOrder(Integer commdityid, String mac, String umac,
			Integer umactype, String context, String user_agent, Integer channel) {
		
		try{
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
			String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
			Order order = orderFacadeService.createWhiteListOrder(commdityid, mac_lower, mac_dut, umac_lower, 
					umactype, bindUser, context, user_agent);
			
			OrderWhiteListVTO vto = new OrderWhiteListVTO();
			vto.setOrderid(order.getId());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
			
		}catch(Exception ex){
			logger.error("createWhiteListOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> check_user_in_whiteList(String mac, String umac, String acc, String context,
			Integer umactype, Integer commdityid, String user_agent, Integer channel) {
		try{
			if (!BusinessRuntimeConfiguration.isCommdityWhiteList(acc)){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.FALSE);
			}else{
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
				String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
				Order order = orderFacadeService.createWhiteListOrder(commdityid, mac_lower, mac_dut, umac_lower, 
						umactype, bindUser, context, user_agent);
				logger.info(String.format("check_user_in_whiteList create order[%s]",order.getId()));
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
			}
			
		}catch(Exception ex){
			logger.error("check_user_in_whiteList Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<UserValidateCaptchaDTO> validate_code_check_authorize(String mac, String umac,
			int countrycode, String acc, String captcha, String context, Integer umactype, Integer commdityid,
			Integer channel, String user_agent,String remateIp) {
		try{
			UserValidateCaptchaDTO dto = new UserValidateCaptchaDTO();
			String accWithContryCode = PhoneHelper.format(countrycode, acc);
			ResponseErrorCode errorCode = userCaptchaCodeService.validCaptchaCode(accWithContryCode, captcha);
			if (errorCode == null){
				dto.setValidate_captcha(true);
				userIdentityAuthService.generateIdentityAuth(countrycode, acc, umac);
				asyncDeliverMessageService.sendUserIdentityRepariActionMessage(umac,acc);
				
				
				Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
				if(uid == null){
					UserInnerExchangeDTO userExchange = userSignInOrOnFacadeService.commonUserCreate(countrycode, acc, null, null, null, DeviceEnum.Portal.getSname(), remateIp, null, UserType.Normal, null);
					deliverMessageService.sendUserRegisteredActionMessage(userExchange.getUser().getId(),acc, null, DeviceEnum.Portal.getSname(),remateIp);
					deliverMessageService.sendPortalUpdateUserChangedActionMessage(userExchange.getUser().getId(), null, acc, userExchange.getUser().getAvatar());
					dto.setUser(userExchange.getUser());
					uid = dto.getUser().getId();
				}else{
					dto.setUser(RpcResponseDTOBuilder.builderUserDTOFromUser(userService.getById(uid), false));
				}
				userIdentityAuthFacadeService.updateLoginDevice(uid, countrycode, acc, umac);
				
				//是否在白名单中
				dto.setAuthorize(BusinessRuntimeConfiguration.isCommdityWhiteList(acc));
				if(dto.isAuthorize()){
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
					String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
					Order order = orderFacadeService.createWhiteListOrder(commdityid, mac_lower, mac_dut, umac_lower, 
							umactype, bindUser, context, user_agent);
					logger.info(String.format("validate_code_check_authorize create order[%s]",order.getId()));
				}else{
					logger.info(String.format("validate_code_check_authorize user not in whitelist umac[%s],acc[%s]", umac,acc));
				}
			}else{
				logger.info(String.format("validate_code_check_authorize captcha error umac[%s],acc[%s]", umac,acc));
			}
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
		}catch(Exception ex){
			logger.error("validate_code_check_authorize Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<HotPlayOrderVTO> createHotPlayOrder(Integer commdityid, String hpid, Integer umactype,
			String payment_type, Integer channel, String user_agent) {
		try{
			AdCommdityVTO advertisePayment = advertiseFacadeService.advertisePayment(hpid);
			String amount = advertisePayment.getCash();
			long orderExpire = 20;
			long restMin = 20;
			OrderPaymentType pType = BusinessEnumType.OrderPaymentType.fromKey(payment_type);
			switch (pType) {
			case PcWeixin:
			case WapWeixin:
			case Weixin:
			case AppWeixin:
				if (Double.parseDouble(amount) > 20000){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_AMOUNT_LARGE);
				}
				break;
			case WapAlipay:
			case AppAlipay:
			case PcAlipay:
			case Alipay:
				restMin = orderExpire - getBetweenTimeCouse(advertisePayment.getCreated_at(),
						advertisePayment.getNowDate());
				if (restMin < 0){
					restMin = 0;
				}
				break;
			default:
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_PAYMENT_TYPE_ERROR);
			}
			//商品信息验证
			//验证商品是否合法
			Commdity commdity = null;
			AdvertiseType hpType = AdvertiseType.fromKey(advertisePayment.getType());
			switch (hpType) {
			
			case HomeImage_SmallArea:
				commdity = commdityFacadeService.
				validateCommdity(BusinessRuntimeConfiguration.
						HotPlay_HomeImage_SmallArea_Commdity_ID);
				break;
			case HomeImage:
			case SortMessage:
			default:
				commdity = commdityFacadeService.validateCommdity(commdityid);
				break;
			}
			
			Order order = orderFacadeService.createHotPlayOrder(commdity, hpid, amount,
					umactype, payment_type, channel, user_agent);
			HotPlayOrderVTO vto = new HotPlayOrderVTO();
			vto.setOrderid(order.getId());
			vto.setAmount(order.getAmount());
			vto.setAppid(order.getAppid());
			vto.setAdCommdityVTO(advertisePayment);
			vto.setRestMin(restMin);
			vto.setName_key(commdity.getName_key());
			vto.setGoods_name(commdity.getName());
			
			logger.info("createHotPlayOrder successfully!");
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(Exception ex){
			logger.error("createHotPlayOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public static long getBetweenTimeCouse(Date createdTime, Date payTime) {
        long min=0;  
        long time1 = createdTime.getTime();  
		long time2 = payTime.getTime();  
		long diff ;  
		if(time1<time2) {  
		    diff = time2 - time1;  
		} else {  
		    diff = time1 - time2;  
		}  
		min = diff /60000 ;  
        return min;  
	}

	public RpcResponseDTO<OrderVideoVTO> clickAuthorize(Integer commdityid, String mac, String umac, Integer umactype,
			Integer channel, String user_agent) {
		try{
			//验证mac umac
			if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_ORDER_MAC_UMAC_ILLEGAL);
			}
			if(!StringHelper.isValidMac(mac) || !StringHelper.isValidMac(umac)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MAC_INVALID_FORMAT);
			}
			String mac_lower = mac.toLowerCase();
			String umac_lower = umac.toLowerCase();
			
			WifiDevice wifiDevice = wifiDeviceService.getById(mac_lower);
			if(wifiDevice == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
			}
			ParamSharedNetworkDTO psn = chargingFacadeService.getParamSharedNetwork(mac_lower);

			if (!chargingFacadeService.fetchDeviceIsOpenFreeMode(psn))
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_DEVICE_ISFREE_STATUS_INVALID);
				
			OrderVideoVTO vto = new OrderVideoVTO();
			if (chargingFacadeService.fetchDeviceIsNoappdl(mac_lower)){
				User bindUser = userWifiDeviceFacadeService.findUserById(mac_lower);
				//生成订单
				String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
				Order order = orderFacadeService.createVideoOrder(commdityid,mac_lower, mac_dut, umac_lower, umactype, bindUser,
						null, channel,user_agent);
				//直接放行用户
				order.setStatus(OrderStatus.PaySuccessed.getKey());
				order.setProcess_status(OrderProcessStatus.PaySuccessed.getKey());
				order.setPaymented_at(new Date());
				String notify_message = PaymentNotifyFactoryBuilder.toJsonHasPrefix(ResponseVideoValidateCompletedNotifyDTO.
						builder(order));
				CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
				vto.setNoappdl(Boolean.TRUE);
				vto.setId(order.getId());
				vto.setForceTime(chargingFacadeService.fetchFreeAccessInternetTime(psn,umactype));
				vto.setUser7d(OrdersFinishCountStringService.getInstance().fetchOrdersFinishRecent7Days()+"");
			}
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			logger.error("CreateVideoOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	
	public RpcResponseDTO<QualityGoodsSharedealVTO> qualityGoodsSharedealPages(int uid, int pageNo, int pageSize){
		try{
			QualityGoodsSharedealVTO ret = new QualityGoodsSharedealVTO();
			List<Map<String, Object>> counts = orderService.getEntityDao().countQualityGoodsSharedeal();
			List<Map<String, Object>> items = orderService.getEntityDao().qualityGoodsSharedealPages(pageNo, pageSize);
			
			for(Map<String, Object> countMap:counts){
				String key = String.valueOf(countMap.get("process_status"));
				String cnt = String.valueOf(countMap.get("count"));
				
				if(String.valueOf(BusinessEnumType.OrderProcessStatus.DeliverCompleted.getKey()).equals(key))
					ret.setWaitCount(Integer.parseInt(cnt));
				else if(String.valueOf(BusinessEnumType.OrderProcessStatus.SharedealCanceled.getKey()).equals(key))
					ret.setCancelCount(Integer.parseInt(cnt));
				else if(String.valueOf(BusinessEnumType.OrderProcessStatus.SharedealCompleted.getKey()).equals(key))
					ret.setDoneCount(Integer.parseInt(cnt));
			}
			if(items != null && items.size() > 0){
				List<QualityGoodsSharedealListVTO> vtos = new ArrayList<QualityGoodsSharedealListVTO>();
				for(Map<String, Object> item:items){
					QualityGoodsSharedealListVTO vto = new QualityGoodsSharedealListVTO();
					vto.setOrderid((String)item.get("orderid"));
					vto.setCommdityid((String)item.get("commdityid"));
					vto.setCommdityname((String)item.get("commdityname"));
					vto.setAmount((String)item.get("amount"));
					vto.setCreated_at((String)item.get("created_at"));
					vto.setProcess_status((String)item.get("process_status"));
					vto.setRemark((String)item.get("remark"));
					
					String context = (String)item.get("context");
					if(StringUtils.isNotEmpty(context)){
						String[] arr = context.split(StringHelper.COMMA_STRING_GAP);
						if(arr.length >= 4){
							vto.setMobileno(arr[1]);
							vto.setUsername(arr[2]);
							vto.setAddress(arr[3]);
						}
					}
					vtos.add(vto);
				}
				TailPage<QualityGoodsSharedealListVTO> tailPages = new CommonPage<QualityGoodsSharedealListVTO>(pageNo, pageSize, items.size(), vtos);
				ret.setTailPages(tailPages);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(Exception ex){
			logger.error("createHotPlayOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	
	public RpcResponseDTO<Boolean> doOrderSharedealCancel(int uid, String orderid, String remark){
		try{
			//存储过程中可能修改过分润状态
			orderService.dropCache(orderid);

			Order order = orderService.getById(orderid);
			if(order == null){
				logger.info(String.format("doOrderSharedealCancel no such order [%s]" , orderid));
				throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST,new String[]{"remark"});
			} else if(!BusinessEnumType.OrderStatus.DeliverCompleted.getKey().equals(order.getStatus()) || !
					BusinessEnumType.OrderProcessStatus.DeliverCompleted.getKey().equals(order.getProcess_status())){
				logger.info(String.format("doOrderSharedealCancel invalide order status [%s] process_status[%s]", order.getStatus(), order.getProcess_status()));
				throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_STATUS_INVALID,new String[]{String.valueOf(order.getStatus()), String.valueOf(order.getProcess_status())});
			} else {
				order.setProcess_status(BusinessEnumType.OrderProcessStatus.SharedealCanceled.getKey());
				order.setRemark(remark);
				orderFacadeService.cancelGoodsOrderPermissionNotify(order);
				orderService.update(order);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException be){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(be.getErrorCode());
		}catch(Exception ex){
			logger.error("createHotPlayOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<CommdityOrderCommonVTO> createTechServiceOrder(Integer commdityid, Integer uid, String macs,
			String payment_type, Integer channel, String user_agent) {
		try{
			if (StringHelper.isEmpty(macs)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"macs"});
			}
			List<String> macsList = Arrays.asList(macs.split(StringHelper.COMMA_STRING_GAP));
			if (!StringHelper.isValidMacs(macsList) || macsList.size() <= 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"macs"});
			}
			Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
			String amount = ArithHelper.getCuttedCurrency(String.valueOf(Double.parseDouble(commdity.getPrice()) * macsList.size()));
			String context = JsonHelper.getJSONString(TechServiceDataDTO.builder(macsList));
			Order order = orderFacadeService.createTechServiceOrder(uid, commdity, null, WifiDeviceSharedealConfigs.Default_ConfigsWifiID,amount,
					payment_type, channel, user_agent, context);
			CommdityOrderCommonVTO vto = new CommdityOrderCommonVTO();
			vto.setAmount(amount);
			vto.setAppid(order.getAppid());
			vto.setOrderid(order.getId());
			vto.setGoods_name(commdity.getName());
			vto.setName_key(commdity.getName_key());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException be){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(be.getErrorCode());
		}catch(Exception ex){
			logger.error("createTechServiceOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<CommdityOrderCommonVTO> createRechargeCashOrder(Integer commdityid, Integer uid,
			String payment_type, String amount, Integer channel, String context, String user_agent) {
		try{
			Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
			if (commdity.getId().intValue() == BusinessRuntimeConfiguration.RechargeBalance_OtherAmount_Commdity_ID){
				if (StringHelper.isEmpty(amount)){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,
							new String[]{"amount"});
				}else{
					if (Double.parseDouble(amount) < BusinessRuntimeConfiguration.RechargeBalance_Min_Amount){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_AMOUNT_LITTLE,
								new String[]{BusinessRuntimeConfiguration.RechargeBalance_Min_Amount+""});
					}
				}
				commdity.setPrice(amount);
			}
			Order order = orderFacadeService.createRechargeCashOrder(commdity, uid, payment_type,
					channel, context, user_agent);
			CommdityOrderCommonVTO vto = new CommdityOrderCommonVTO();
			vto.setAmount(order.getAmount());
			vto.setAppid(order.getAppid());
			vto.setOrderid(order.getId());
			vto.setGoods_name(commdity.getName());
			vto.setName_key(commdity.getName_key());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException be){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(be.getErrorCode());
		}catch(Exception ex){
			logger.error("createRechargeCashOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<CommdityOrderCommonVTO> spendBalanceOrder(Integer commdityid, Integer uid, String mac, String umac,
			Integer umactype, String payment_type, String context, String user_agent, Integer channel) {
		try{
			if(!BusinessEnumType.OrderPaymentType.BalancePay.getKey().equals(payment_type)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.VALIDATE_COMMDITY_PAYMENT_TYPE_ERROR);
			}
			if(!BusinessEnumType.PaymentChannelType.supported(channel)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"channel"});
			}
			Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
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
			String mac_dut = WifiDeviceHelper.stDevice(wifiDevice.getOrig_swver());
			Order order = orderFacadeService.spendBalanceOrder(commdity, uid, mac_lower, mac_dut, umac_lower, umactype, payment_type, context, user_agent, channel);
			int result = userConsumptiveWalletFacadeService.userPurchaseGoods(uid, order.getId(), 
					Double.parseDouble(order.getAmount()), BusinessEnumType.UConsumptiveWalletTransType.PurchaseInternetService,
					null, null, "spend balance:"+order.getAmount(), "", null);
			if(result == 0){
				CommdityOrderCommonVTO vto = new CommdityOrderCommonVTO();
				vto.setAmount(order.getAmount());
				vto.setAppid(order.getAppid());
				vto.setOrderid(order.getId());
				vto.setGoods_name(commdity.getName());
				vto.setName_key(commdity.getName_key());
				String notify_message = PaymentNotifyFactoryBuilder.toJsonHasPrefix(ResponsePaymentCompletedNotifyDTO.
						builder(order));
				CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
			}else if(result == 1){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
			}else{
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			}
		}catch(BusinessI18nCodeException be){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(be.getErrorCode());
		}catch(Exception ex){
			logger.error("spendBalanceOrder Exception:", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<QueryBalanceLogsVTO> queryBalanceLogs(Integer uid, long start_created_ts,
			long end_created_ts, int pageNo, int pageSize) {
	try{
		QueryBalanceLogsVTO vto = new QueryBalanceLogsVTO();
		int count = userConsumptiveWalletFacadeService.countByParams(uid, start_created_ts, end_created_ts);
		if (count > 0){
			List<UserConsumptiveWalletLog> logs = userConsumptiveWalletFacadeService.findByParams(uid, 
					start_created_ts, end_created_ts, pageNo, pageSize);
			for(UserConsumptiveWalletLog log : logs){
				log.setCash(ArithHelper.getCuttedCurrency(ArithHelper.longCurrencyToDouble(Math.abs(Long.parseLong(log.getCash())), 
						BusinessRuntimeConfiguration.WalletDataBaseDegree)+""));
			}
			vto.setLogs(new CommonPage<UserConsumptiveWalletLog>(pageNo, pageSize, count, logs));
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
	}catch(BusinessI18nCodeException be){
		return RpcResponseDTOBuilder.builderErrorRpcResponse(be.getErrorCode());
	}catch(Exception ex){
		logger.error("queryBalanceLogs Exception:", ex);
		return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
	}
	}
}
