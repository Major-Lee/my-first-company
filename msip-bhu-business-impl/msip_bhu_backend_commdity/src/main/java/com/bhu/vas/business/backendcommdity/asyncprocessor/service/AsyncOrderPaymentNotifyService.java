package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;

import com.bhu.vas.api.dto.commdity.id.StructuredExtSegment;
import com.bhu.vas.api.dto.commdity.id.StructuredId;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseSMSValidateCompletedNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseVideoValidateCompletedNotifyDTO;
import com.bhu.vas.api.dto.push.SharedealNotifyPushDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.OrderPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderUmacType;
import com.bhu.vas.api.helper.BusinessEnumType.SnkAuthenticateResultType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.PaymentNotifyFactoryBuilder;
import com.bhu.vas.api.helper.PaymentNotifyType;
import com.bhu.vas.api.helper.UPortalHttpHelper;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.helper.StructuredIdHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.notify.IWalletSharedealNotifyCallback;
import com.bhu.vas.api.rpc.user.notify.IWalletVCurrencySpendCallback;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderFinishCountStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.SnkChargingMarkerService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.commdity.facade.CommdityFacadeService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.push.business.PushService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class AsyncOrderPaymentNotifyService{
	private final Logger logger = LoggerFactory.getLogger(AsyncOrderPaymentNotifyService.class);
	//private ExecutorService exec_remote_portalexchange = null;//Executors.newFixedThreadPool(10);
	@Resource
	private OrderService orderService;
	
/*	@Resource
	private CommdityService commdityService;*/
	
	@Resource
	private OrderFacadeService orderFacadeService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
/*	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;*/
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private PushService pushService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private CommdityFacadeService commdityFacadeService;
	
	@PostConstruct
	public void initialize() {
		logger.info("AsyncOrderPaymentNotifyService initialize...");
		//exec_remote_portalexchange = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"AsyncOrderPaymentNotify processes消息处理",ProcessesThreadCount);
		//exec_remote_portalexchange = ExecObserverManager.buildExecutorService(this.getClass(),"uPortalRemoteNotify消息处理",10);
	}
	
	/**
	 * 支付完成的通知处理
	 * 1) 支付系统支付的成功通知
	 * 2) 应用内部的成功通知
	 * @param message
	 */
	public void notifyHandle(String message){
		logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyHandle: message[%s]", message));
		try{
			PaymentNotifyType paymentNotifyType = PaymentNotifyFactoryBuilder.determineActionType(message);
			if(paymentNotifyType == null){
				throw new RuntimeException(String.format("PaymentNotifyType unsupport message[%s]", message));
			}
			String messageNoPrefix = PaymentNotifyFactoryBuilder.determineActionMessage(message);
			
			switch(paymentNotifyType){
				case NormalPaymentNotify:
					orderPaymentNotifyCompletedHandle(messageNoPrefix);
					break;
				case SMSPaymentNotify:
					orderSMSNotifyCompletedHandle(messageNoPrefix);
					break;
				case AdvertiseNotify:
					orderVideoNotifyCompletedHandle(messageNoPrefix);
					break;
				default:
					throw new RuntimeException(String.format("PaymentNotifyType unsupport message[%s]", message));
			}

			logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyHandle: message[%s] successful", message));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("AsyncOrderPaymentNotifyProcessor notifyHandle Exception", ex);
		}
	}
	
	/**
	 * 订单通过支付系统通知
	 * @param message
	 */
	public void orderPaymentNotifyCompletedHandle(String message){
		ResponsePaymentCompletedNotifyDTO rpcn_dto = PaymentNotifyFactoryBuilder.fromJson(message, ResponsePaymentCompletedNotifyDTO.class);
		//ResponsePaymentCompletedNotifyDTO rpcn_dto = JsonHelper.getDTO(message, ResponsePaymentCompletedNotifyDTO.class);
		if(rpcn_dto != null){
			String orderid = rpcn_dto.getOrderid();

			if(StringUtils.isEmpty(orderid)){
				throw new RuntimeException(String.format("AsyncOrderPaymentNotifyProcessor orderPaymentNotifyCompletedHandle "
						+ "param illegal orderid[%s]", orderid));
			}
			StructuredId structuredId = StructuredIdHelper.generateStructuredId(orderid);
			StructuredExtSegment structuredExtSegment = structuredId.getExtSegment();
			//判断支付模式为收入模式(商品订单支付 如出现其他情况可扩展下一位业务占位)
			if(StructuredIdHelper.isPaymodeReceipt(structuredExtSegment)){
				orderPaymentNotifyPaymodeReceiptHandle(rpcn_dto);
			}
			//判断支付模式为支出模式(提现 如出现其他情况可扩展下一位业务占位)
			else if(StructuredIdHelper.isPaymodeExpend(structuredExtSegment)){
				orderPaymentNotifyPaymodeExpendHandle(rpcn_dto);
			}
		}
	}
	
	
	/********************          Receipt           ********************/
	
	
	/**
	 * 支付模式为收入模式(商品订单支付 如出现其他情况可扩展下一位业务占位)
	 * @param rpcn_dto
	 */
	public void orderPaymentNotifyPaymodeReceiptHandle(ResponsePaymentCompletedNotifyDTO rpcn_dto){
		String orderid = rpcn_dto.getOrderid();
		//验证订单 
		Order order = orderFacadeService.validateOrderId(orderid);
		//验证订单合理
		if(!OrderHelper.lte_notpay(order.getStatus())){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_STATUS_INVALID, new String[]{orderid, String.valueOf(order.getStatus())});
		}
		//验证appid
		CommdityApplication commdityApplication = OrderHelper.supportedAppId(order.getAppid());
		switch(commdityApplication){
			case DEFAULT:
				rewardOrderReceiptHandle(order, rpcn_dto);
				break;
			case BHU_PREPAID_BUSINESS:
				rechargeVCurrencyOrderReceiptHandle(order, rpcn_dto);
				break;
			default:
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
	}
	
	
	/**
	 * 打赏订单支付结束处理
	 * @param order
	 * @param rpcn_dto
	 */
	public void rewardOrderReceiptHandle(Order order, ResponsePaymentCompletedNotifyDTO rpcn_dto){
		String orderid = rpcn_dto.getOrderid();
		boolean success = rpcn_dto.isSuccess();
		String paymented_ds = rpcn_dto.getPaymented_ds();
		String payment_type = rpcn_dto.getPayment_type();
		String payment_proxy_type = rpcn_dto.getPayment_proxy_type();
		//订单处理逻辑 
		//Order order = orderFacadeService.validateOrderId(orderid);
		//支付完成时进行设备的uid获取并设置订单
		//User bindUser = userDeviceFacadeService.getBindUserByMac(order.getMac());
		//User bindUser = userWifiDeviceFacadeService.findUserById(order.getMac());
		Commdity commdity = commdityFacadeService.validateCommdity(order.getCommdityid());
		User bindUser = null;
		if(order.getUid() != null){
			bindUser = userService.getById(order.getUid());
		}
		String accessInternetTime = null;
		//根据商品id判断是打赏还是购买实体商品
		if (CommdityCategory.correct(commdity.getCategory(), CommdityCategory.RewardInternetLimit)){
			
			accessInternetTime = chargingFacadeService.fetchAccessInternetTime(order.getMac(), order.getUmactype());
			
			order = orderFacadeService.rewardOrderPaymentCompletedNotify(success, order, bindUser, paymented_ds, 
					payment_type, payment_proxy_type, accessInternetTime);
			
			//判断订单状态为支付成功或发货成功
			Integer order_status = order.getStatus();
			if(OrderStatus.isPaySuccessed(order_status) || OrderStatus.isDeliverCompleted(order_status)){
				//判断是否为限时上网商品
	/*			Integer commdityid = order.getCommdityid();
				Commdity commdity = commdityService.getById(commdityid);
				if(commdity != null && CommdityCategory.isInternetLimit(commdity.getCategory())){*/
					//进行订单分成处理逻辑
					//String dmac = order.getMac();
					double amount = Double.parseDouble(order.getAmount());
					//userWalletFacadeService.sharedealCashToUserWallet(order.getUid(), amount, orderid);
					OrderUmacType uMacType = OrderUmacType.fromKey(order.getUmactype());
					if(uMacType == null){
						uMacType = OrderUmacType.Terminal;
					}
					if(StringUtils.isEmpty(order.getPayment_type())){
						order.setPayment_type(BusinessEnumType.unknownPaymentType);
					}
					/*StringBuilder sb_description = new StringBuilder();
					if(uMacType != null){
						sb_description.append(uMacType.getName());
					}
					if(StringUtils.isNotEmpty(order.getPayment_type())){
						if(sb_description.length()>0)	
							sb_description.append(StringHelper.MINUS_CHAR_GAP);
						sb_description.append(order.getPayment_type());
					}*/
					final String order_payment_type = order.getPayment_type();
					final Integer order_umac_type = order.getUmactype();
					final String mac = order.getMac();
					final String umac = order.getUmac();
					OrderPaymentType orderPaymentType = OrderPaymentType.fromKey(order.getPayment_type());
					userWalletFacadeService.sharedealCashToUserWalletWithProcedure(order.getMac(), order.getUmac(), amount, orderid, order.getPaymented_at(),
							String.format(BusinessEnumType.templateRedpacketPaymentDesc, uMacType.getDesc(), 
									orderPaymentType != null ? orderPaymentType.getDesc() : StringHelper.EMPTY_STRING_GAP),
									new IWalletSharedealNotifyCallback(){
										@Override
										public String notifyCashSharedealOper(int uid, double cash) {
											logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyCashSharedealOper: uid[%s] "
													+ "cash[%s] order_payment_type[%s] order_umac_type[%s] mac[%s] umac[%s]", uid, cash, order_payment_type, order_umac_type, mac, umac));
											if(uid > 0 && cash >= 0.01){
												SharedealNotifyPushDTO sharedeal_push_dto = new SharedealNotifyPushDTO();
												sharedeal_push_dto.setMac(mac);
												sharedeal_push_dto.setUid(uid);
												sharedeal_push_dto.setCash(ArithHelper.getCuttedCurrency(String.valueOf(cash)));
												sharedeal_push_dto.setHd_mac(umac);
												sharedeal_push_dto.setPayment_type(order_payment_type);
												sharedeal_push_dto.setUmac_type(order_umac_type);
												pushService.pushSharedealNotify(sharedeal_push_dto);
											}
											logger.info(String.format("AsyncOrderPaymentNotifyProcessor notifyCashSharedealOper successful: uid[%s] "
													+ "cash[%s] order_payment_type[%s] order_umac_type[%s] mac[%s] umac[%s]", uid, cash, order_payment_type, order_umac_type, mac, umac));
											return null;
										}
					});
					
					/*userWalletFacadeService.sharedealCashToUserWallet(order.getMac(), amount, orderid, 
							String.format(BusinessEnumType.templateRedpacketPaymentDesc, uMacType.getDesc(), 
									orderPaymentType != null ? orderPaymentType.getDesc() : StringHelper.EMPTY_STRING_GAP));*/
					/*userWalletFacadeService.sharedealCashToUserWalletWithBindUid(order.getUid(), amount, orderid,
							String.format(BusinessEnumType.templateRedpacketPaymentDesc, uMacType.getDesc(), 
									orderPaymentType != null ? orderPaymentType.getDesc() : StringHelper.EMPTY_STRING_GAP));*/
				//}
			}else if (CommdityCategory.correct(commdity.getCategory(), CommdityCategory.RewardMonthlyServiceLimit)){
				
				accessInternetTime = commdity.getApp_deliver_detail();
				order = orderFacadeService.CommdityPhysicalOrderPaymentCompletedNotify(success, order, bindUser, paymented_ds, 
						payment_type, payment_proxy_type, accessInternetTime);
				String acc = commdityFacadeService.getCommdityPhysicalDTO(order.getUmac()).getAcc();
				if (PhoneHelper.isValidPhoneCharacter(86, acc)){
					String smsg_snk_stop = String.format(BusinessRuntimeConfiguration.Internal_CommdityPhysical_Payment_Template,
							RewardOrderFinishCountStringService.getInstance().getRecent7daysValue());
	   				String response_snk_stop = SmsSenderFactory.buildSender(
							BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg_snk_stop, acc);
					logger.info(String.format("send CommdityPhysical acc[%s] msg[%s] response[%s]",acc,smsg_snk_stop,response_snk_stop));
				}else{
					logger.info(String.format("send CommdityPhysical acc  invalid "));
				}
				
			}
		}
	}
	
	/**
	 * 充值虎钻订单支付结束处理
	 * @param order
	 * @param rpcn_dto
	 */
	public void rechargeVCurrencyOrderReceiptHandle(Order order, ResponsePaymentCompletedNotifyDTO rpcn_dto){
		//String orderid = rpcn_dto.getOrderid();
		boolean success = rpcn_dto.isSuccess();
		String paymented_ds = rpcn_dto.getPaymented_ds();
		String payment_type = rpcn_dto.getPayment_type();
		String payment_proxy_type = rpcn_dto.getPayment_proxy_type();
		
		order = orderFacadeService.rechargeVCurrencyOrderPaymentCompletedNotify(success, order, paymented_ds, 
				payment_type, payment_proxy_type);
		
		Integer order_status = order.getStatus();
		if(OrderStatus.isPaySuccessed(order_status) || OrderStatus.isDeliverCompleted(order_status)){
			OrderPaymentType orderPaymentType = OrderPaymentType.fromKey(order.getPayment_type());
			OrderUmacType uMacType = OrderUmacType.fromKey(order.getUmactype());
			if(uMacType == null){
				uMacType = OrderUmacType.Terminal;
			}
			String desc = String.format(BusinessEnumType.templateReChargingRealmoney2VCurrencyPaymentDesc, uMacType.getDesc(), 
					orderPaymentType != null ? orderPaymentType.getDesc() : StringHelper.EMPTY_STRING_GAP);
			userWalletFacadeService.vcurrencyToUserWallet(order.getUid(), order.getId(), UWalletTransMode.RealMoneyPayment,
					Double.parseDouble(order.getAmount()), order.getVcurrency(), desc);
			{
				UserWallet uwallet = userWalletFacadeService.getUserWalletService().getById(order.getUid());
				if(uwallet != null){
					UserWalletDetailVTO walletDetail = uwallet.toUserWalletDetailVTO();
					if(walletDetail.getVcurrency_total() >= BusinessRuntimeConfiguration.Sharednetwork_Auth_Threshold_Notsufficient){
						logger.info(String.format("rechargeVCurrency user[%s] vcurrency_total[%s]",order.getUid(),walletDetail.getVcurrency_total()));
						//清除标记
						SnkChargingMarkerService.getInstance().clear(order.getUid());
						//通知uportal清除标记位
						UPortalHttpHelper.uPortalChargingStatusNotify(order.getUid().intValue(),UPortalHttpHelper.ServiceOK);
					}
				}
				/*if(BusinessRuntimeConfiguration.Sharednetwork_Auth_Threshold_Notsufficient < order.getVcurrency()){
					//清除标记
					SnkChargingMarkerService.getInstance().clear(order.getUid());
					//通知uportal清除标记位
					UPortalHttpHelper.uPortalChargingStatusNotify(order.getUid().intValue(),UPortalHttpHelper.ServiceOK);
				}*/
			}
			
		}
	}
	
	
	/********************          Expend           ********************/
	
	
	/**
	 * 支付模式为支出模式(提现 如出现其他情况可扩展下一位业务占位)
	 * @param rpcn_dto
	 */
	public void orderPaymentNotifyPaymodeExpendHandle(ResponsePaymentCompletedNotifyDTO rpcn_dto){
		String orderId = rpcn_dto.getOrderid();
		boolean successed = rpcn_dto.isSuccess();
		userWalletFacadeService.doWithdrawNotifyFromRemote(orderId, successed);
	}
	
	
	/********************          非支付系统handle           ********************/
	
	/**
	 * 短信认证通过通知
	 * @param message
	 */
	public void orderSMSNotifyCompletedHandle(String message){
		ResponseSMSValidateCompletedNotifyDTO smsv_dto = PaymentNotifyFactoryBuilder.fromJson(message, ResponseSMSValidateCompletedNotifyDTO.class);
		String orderid = smsv_dto.getOrderid();
		boolean success = smsv_dto.isSuccess();
		Date paymented_ds = smsv_dto.getPaymented_ds();
		
		//订单处理逻辑 
		Order order = orderFacadeService.validateOrderId(orderid);
		User bindUser = null;
		if(order.getUid() != null){
			bindUser = userService.getById(order.getUid());
		}
		if(order.getVcurrency() > 0){
			//扣除虎钻
			final AtomicLong vcurrency_current_leave = new AtomicLong(0l);

			//User bindUser = userWifiDeviceFacadeService.findUserById(order.getMac());
			if(bindUser != null && bindUser.getId().intValue() >0){
				int uid = bindUser.getId().intValue();
				String mobileno = bindUser.getMobileno();
				SnkAuthenticateResultType ret = userWalletFacadeService.vcurrencyFromUserWalletForSnkAuthenticate(uid,orderid, order.getVcurrency(), "通过虎钻支付 虚拟币购买道具",new IWalletVCurrencySpendCallback(){
					@Override
					public boolean beforeCheck(int uid, long vcurrency_cost,long vcurrency_has) {
						//业务需求 如果短信验证通过则直接扣款，负数也扣款
						/*if(vcurrency_has < vcurrency_cost){
							return false;
						}else{
							return true;
						}*/
						return true;
					}
					@Override
					public String after(int uid,long vcurrency_leave) {
						vcurrency_current_leave.addAndGet(vcurrency_leave);
						return null;
					}
		   		});
		   		//都放行
		   		System.out.println(ret);
		   		switch(ret){
		   			case Success:
		   				//通知uportal可以放行
		   				break;
		   			case SuccessButThresholdNeedCharging:
		   				//通知uportal可以放行
		   				//判定是否存在标记位 进行短消息充值提醒通知
		   				if(SnkChargingMarkerService.getInstance().level1marker(uid) == 1 && StringUtils.isNotEmpty(mobileno)){
			   				String smsg_snk_needcharging = String.format(BusinessRuntimeConfiguration.Internal_SNK_NeedCharging_Template,DateTimeHelper.formatDate(DateTimeHelper.FormatPattern13), vcurrency_current_leave);
			   				String response_snk_needcharging = SmsSenderFactory.buildSender(
									BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg_snk_needcharging, mobileno);
							logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",mobileno,smsg_snk_needcharging,response_snk_needcharging));
							UPortalHttpHelper.uPortalChargingStatusNotify(uid,UPortalHttpHelper.ServiceNeedCharging);
		   				}
		   				break;
		   			case FailedThresholdVcurrencyNotsufficient:
		   				//通知uportal可以放行
		   				//远程通知uportal 静态页 关闭访客网络
		   				//判定是否存在标记位 进行短消息关闭通知
		   				if(SnkChargingMarkerService.getInstance().level2marker(uid) == 1 && StringUtils.isNotEmpty(mobileno)){
			   				String smsg_snk_stop = String.format(BusinessRuntimeConfiguration.Internal_SNK_Stop_Template,DateTimeHelper.formatDate(DateTimeHelper.FormatPattern13), vcurrency_current_leave);
			   				String response_snk_stop = SmsSenderFactory.buildSender(
									BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg_snk_stop, mobileno);
							logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",mobileno,smsg_snk_stop,response_snk_stop));
							UPortalHttpHelper.uPortalChargingStatusNotify(uid,UPortalHttpHelper.ServiceInsufficient);
		   				}
		   				break;
		   			case Failed:
		   				//扣款失败，原因不明，依然通知uportal可以放行
		   				break;
		   		}
			}else{
				logger.info(String.format("order[%s] mac[%s] devices unbinded",order.getId(),order.getMac()));
			}
		}

		String accessInternetTime = chargingFacadeService.fetchAccessInternetTime(order.getMac(), order.getUmactype());
		orderFacadeService.smsOrderPaymentCompletedNotify(success, order, bindUser, paymented_ds, accessInternetTime);
	}
	
	
	/**
	 * 视频上网通过通知
	 * @param message
	 */
	public void orderVideoNotifyCompletedHandle(String message){
		ResponseVideoValidateCompletedNotifyDTO videov_dto = PaymentNotifyFactoryBuilder.fromJson(message, ResponseVideoValidateCompletedNotifyDTO.class);
		String orderid = videov_dto.getOrderid();
		boolean success = videov_dto.isSuccess();
		Date paymented_ds = videov_dto.getPaymented_ds();
		
		//订单处理逻辑 
		Order order = orderFacadeService.validateOrderId(orderid);
		User bindUser = null;
		if(order.getUid() != null){
			bindUser = userService.getById(order.getUid());
		}
		String accessInternetTime = chargingFacadeService.fetchFreeAccessInternetTime(order.getMac(), order.getUmactype());
		orderFacadeService.videoOrderPaymentCompletedNotify(success, order, bindUser, paymented_ds, accessInternetTime);
	}
}
