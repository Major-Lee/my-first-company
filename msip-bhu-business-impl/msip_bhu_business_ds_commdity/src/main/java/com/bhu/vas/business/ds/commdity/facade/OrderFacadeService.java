package com.bhu.vas.business.ds.commdity.facade;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.CommdityPhysicalDTO;
import com.bhu.vas.api.dto.commdity.OrderRewardNewlyDataVTO;
import com.bhu.vas.api.dto.commdity.OrderSMSPromotionDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseSMSValidateCompletedNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.portal.PhysicalPermissionThroughNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.portal.RewardPermissionThroughNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.portal.SMSPermissionThroughNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.portal.VideoPermissionThroughNotifyDTO;
import com.bhu.vas.api.dto.procedure.DeviceOrderStatisticsProduceDTO;
import com.bhu.vas.api.dto.procedure.RewardOrderNewlyDataProcedureDTO;
import com.bhu.vas.api.dto.procedure.RewardOrderStatisticsProcedureDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.helper.BusinessEnumType.SnkAuthenticateResultType;
import com.bhu.vas.api.helper.PaymentNotifyFactoryBuilder;
import com.bhu.vas.api.helper.PermissionThroughNotifyFactoryBuilder;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.CommdityPhysical;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.notify.IWalletVCurrencySpendCallback;
import com.bhu.vas.api.vto.statistics.DeviceOrderStatisticsVTO;
import com.bhu.vas.api.vto.statistics.RewardOrderStatisticsVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderAmountHashService;
import com.bhu.vas.business.ds.commdity.service.CommdityPhysicalService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class OrderFacadeService {
	private final Logger logger = LoggerFactory.getLogger(OrderFacadeService.class);
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private CommdityService commdityService;
	
	@Resource
	private CommdityPhysicalService commdityPhysicalService;
	
	@Resource
	private CommdityFacadeService commdityFacadeService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	/**
	 * 查询最近的一条满足条件的订单
	 * 主要条件为umac
	 * @param commdityid 商品id
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户终端mac
	 * @return
	 */
	public Order recentNotpayOrderByUmac(Integer commdityid, Integer appid, String mac, String umac){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()
			.andColumnEqualTo("commdityid", commdityid)
			.andColumnEqualTo("appid", appid)
			.andColumnEqualTo("mac", mac)
			.andColumnEqualTo("umac", umac)
			.andColumnLessThanOrEqualTo("status", OrderStatus.NotPay.getKey());
//			.andColumnEqualTo("status", OrderStatus.NotPay.getKey());
		mc.setOrderByClause("created_at desc");
		mc.setSize(1);
		List<Order> orderList = orderService.findModelByModelCriteria(mc);
		if(!orderList.isEmpty()){
			return orderList.get(0);
		}
		return null;
	}
	
	/**
	 * 根据订单参数进行查询订单数量
	 * @param uid
	 * @param mac 设备mac
	 * @param umac 支付订单的用户mac
	 * @param status 订单状态
	 * @param dut 业务线
	 * @param type 订单类型
	 */
	public int countOrderByParams(Integer uid, String mac, String umac, Integer status, String dut, 
			Integer type, long start_created_ts, long end_created_ts){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		criteria
			.andColumnEqualTo("uid", uid)
			.andColumnEqualTo("status", status);
		if(StringUtils.isNotEmpty(mac)){
			criteria.andColumnEqualTo("mac", mac);
		}
		if(StringUtils.isNotEmpty(umac)){
			criteria.andColumnEqualTo("umac", umac);
		}
		if(StringUtils.isNotEmpty(dut)){
			criteria.andColumnEqualTo("mac_dut", dut);
		}
		if(type != null){
			criteria.andColumnEqualTo("type", type);
		}
		if(start_created_ts > 0){
			criteria.andColumnGreaterThanOrEqualTo("created_at", new Date(start_created_ts));
		}
		if(end_created_ts > 0){
			criteria.andColumnLessThanOrEqualTo("created_at", new Date(end_created_ts));
		}
		return orderService.countByModelCriteria(mc);
	}
	
	
	/**
	 * 根据订单参数进行查询订单分页列表
	 * @param uid
	 * @param mac 设备mac
	 * @param umac 支付订单的用户mac
	 * @param status 订单状态
	 * @param dut 订单业务线
	 * @param type 订单类型
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Order> findOrdersByParams(Integer uid, String mac, String umac, Integer status, String dut, 
			Integer type, long start_created_ts, long end_created_ts, int pageNo, int pageSize){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		criteria
			.andColumnEqualTo("uid", uid)
			.andColumnEqualTo("status", status);
		if(StringUtils.isNotEmpty(mac)){
			criteria.andColumnEqualTo("mac", mac);
		}
		if(StringUtils.isNotEmpty(umac)){
			criteria.andColumnEqualTo("umac", umac);
		}
		if(StringUtils.isNotEmpty(dut)){
			criteria.andColumnEqualTo("mac_dut", dut);
		}
		if(type != null){
			criteria.andColumnEqualTo("type", type);
		}
		if(start_created_ts > 0){
			criteria.andColumnGreaterThanOrEqualTo("updated_at", new Date(start_created_ts));
		}
		if(end_created_ts > 0){
			criteria.andColumnLessThanOrEqualTo("updated_at", new Date(end_created_ts));
		}
		mc.setOrderByClause("updated_at desc");
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return orderService.findModelByModelCriteria(mc);
	}
	/**
	 * 查询时间段内所有订单
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Order> findOrdersByTime(String startTime, String endTime){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		//criteria.andColumnEqualTo("type", 0);
		criteria.andColumnBetween("created_at", startTime, endTime);
		return orderService.findModelByModelCriteria(mc);
	}
	/**
	 * 根据日期查询订单数量
	 * @param status
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int countOrderByDateParams(Integer status, Integer type, Date startDate, Date endDate){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		criteria.andColumnEqualTo("status", status);
		if(type != null){
			criteria.andColumnEqualTo("type", type);
		}
		if(startDate != null){
			criteria.andColumnGreaterThanOrEqualTo("created_at", startDate);
		}
		if(endDate != null){
			criteria.andColumnLessThanOrEqualTo("created_at", endDate);
		}
		return orderService.countByModelCriteria(mc);
	}
	
	/**
	 * 更新订单的状态和流程状态
	 * @param order 订单实体
	 * @param status 订单状态
	 * @param process_status 订单流程状态
	 */
	public void orderStatusChanged(Order order, Integer status, Integer process_status){
		if(order != null && status != null && process_status != null){
			order.setStatus(status);
			order.setProcess_status(process_status);
			orderService.update(order);
		}
	}

	

	/*************            打赏             ****************/

	
	/**
	 * 生成打赏订单
	 * @param commdity 商品实体
	 * @param appid 应用id
	 * @param bindUser 
	 * @param mac 设备mac
	 * @param mac_dut 设备业务线
	 * @param umac 用户mac
	 * @param umactype 终端类型
	 * @param payment_type 支付方式
	 * @param context 业务上下文
	 * @param user_agent 
	 * @return
	 */
	public Order createRewardOrder(Integer commdityid, Integer appid, User bindUser, String mac, 
			String mac_dut, String umac, Integer umactype, String payment_type, String context, String user_agent,
			Integer channel){
		//商品信息验证
		//验证商品是否合法
		Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
		//验证商品是否合理
		if(!CommdityCategory.correct(commdity.getCategory(), CommdityCategory.RewardInternetLimit)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_ILLEGAL);
		}
		
		//验证缓存中的商品金额
		String amount = RewardOrderAmountHashService.getInstance().getRAmount(mac, umac, commdityid, umactype);
		if(StringUtils.isEmpty(amount)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_AMOUNT_INVALID);
		}
		
		//订单生成
		Order order = new Order();
		order.setCommdityid(commdityid);
		order.setAppid(appid);
		if(bindUser != null){
			order.setUid(bindUser.getId());
		}
		order.setChannel(channel);
		order.setMac(mac);
		order.setMac_dut(mac_dut);
		order.setUmac(umac);
		order.setUmactype(umactype);
		order.setType(commdity.getCategory());
		order.setPayment_type(payment_type);
		order.setContext(context);
		order.setUser_agent(user_agent);
		order.setStatus(OrderStatus.NotPay.getKey());
		order.setProcess_status(OrderProcessStatus.NotPay.getKey());
		order.setAmount(amount);
		orderService.insert(order);
		return order;
	}
	
	/**
	 * 打赏支付系统支付完成的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param success 支付是否成功
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @param paymented_ds 支付时间 yyyy-MM-dd HH:mm:ss
	 * @param payment_type 支付方式
	 * @param payment_proxy_type 支付代理方式
	 */
	public Order rewardOrderPaymentCompletedNotify(boolean success, Order order, User bindUser, String paymented_ds,
			String payment_type, String payment_proxy_type,String ait_time){
		Integer changed_status = null;
		Integer changed_process_status = null;
		try{
			String orderid = order.getId();
/*			Integer order_status = order.getStatus();
			if(!OrderHelper.lte_notpay(order_status)){
				throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_STATUS_INVALID, new String[]{orderid, String.valueOf(order_status)});
			}*/
			
			if(StringUtils.isNotEmpty(paymented_ds)){
				order.setPayment_type(payment_type);
				order.setPayment_proxy_type(payment_proxy_type);
				order.setPaymented_at(DateTimeHelper.parseDate(paymented_ds, DateTimeHelper.DefalutFormatPattern));
			}
			
/*			if(bindUser != null){
				order.setUid(bindUser.getId());
			}*/
			
			//支付成功
			if(success){
				changed_status = OrderStatus.PaySuccessed.getKey();
				changed_process_status = OrderProcessStatus.PaySuccessed.getKey();

				logger.info(String.format("RewardOrderPaymentCompletedNotify prepare deliver notify: orderid[%s]", orderid));
				//进行发货通知
				boolean deliver_notify_ret = rewardOrderPermissionNotify(order, bindUser, ait_time);
				//判断通知发货成功 更新订单状态
				if(deliver_notify_ret){
					changed_status = OrderStatus.DeliverCompleted.getKey();
					changed_process_status = OrderProcessStatus.DeliverCompleted.getKey();
					logger.info(String.format("RewardOrderPaymentCompletedNotify successed deliver notify: orderid[%s]", orderid));
				}else{
					logger.info(String.format("RewardOrderPaymentCompletedNotify failed deliver notify: orderid[%s]", orderid));
				}
				//清除缓存中的随机金额
				RewardOrderAmountHashService.getInstance().removeRAmount(order.getMac(), order.getUmac(), order.getCommdityid(), order.getUmactype());
			}else{
				changed_status = OrderStatus.PayFailured.getKey();
				changed_process_status = OrderProcessStatus.PayFailured.getKey();
			}
		}catch(Exception ex){
			throw ex; 
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
		return order;
	}
	
	/**
	 * 打赏支付系统支付完成的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param success 支付是否成功
	 * @param orderid 订单id
	 * @param bindUser 设备绑定的用户实体
	 * @param paymented_ds 支付时间 yyyy-MM-dd HH:mm:ss
	 * @param payment_type 支付方式
	 * @param payment_proxy_type 支付代理方式
	 * @return
	 */
	public Order rewardOrderPaymentCompletedNotify(boolean success, String orderid, User bindUser, String paymented_ds,
			String payment_type, String payment_proxy_type,String ait_time){
		Order order = validateOrderId(orderid);
		return rewardOrderPaymentCompletedNotify(success, order, bindUser, paymented_ds, payment_type, payment_proxy_type,ait_time);
	}
	
	/**
	 * 打赏通知应用发货，按照约定的redis写入
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @return
	 */
	public boolean rewardOrderPermissionNotify(Order order, User bindUser, String ait_time){
		try{
			if(order == null) {
				logger.error("rewardOrderPermissionNotify order data not exist");
				return false;
			}
			
			RewardPermissionThroughNotifyDTO rewardPermissionNotifyDto = RewardPermissionThroughNotifyDTO.from(order, ait_time, bindUser);
			if(rewardPermissionNotifyDto != null){
				//String requestDeliverNotifyMessage = JsonHelper.getJSONString(rewardPermissionNotifyDto);
				String rewardPermissionNotifyMessage = PermissionThroughNotifyFactoryBuilder.toJsonHasPrefix(rewardPermissionNotifyDto);
				Long notify_ret = CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotify(rewardPermissionNotifyMessage);
				//判断通知发货成功
				if(notify_ret != null && notify_ret > 0){
					logger.info(String.format("rewardOrderPermissionNotify success deliver notify: message[%s] rpush_ret[%s]", rewardPermissionNotifyMessage, notify_ret));
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("rewardOrderPermissionNotify exception", ex);
		}
		return false;
	}
	
	public RewardOrderStatisticsVTO rewardOrderStatisticsWithProcedure(String start_date, String end_date){
		RewardOrderStatisticsProcedureDTO procedureDTO = new RewardOrderStatisticsProcedureDTO();
		procedureDTO.setStart_date(start_date);
		procedureDTO.setEnd_date(end_date);
		int executeRet = orderService.executeProcedure(procedureDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{procedureDTO.getName()});
		}
		return procedureDTO.toVTO();
	}
	
	public OrderRewardNewlyDataVTO rewardOrderNewlyDataWithProcedure(Integer uid, Date date){
		RewardOrderNewlyDataProcedureDTO procedureDTO = new RewardOrderNewlyDataProcedureDTO();
		procedureDTO.setUid(uid);
		procedureDTO.setStart_date(DateTimeHelper.formatDate(date, DateTimeHelper.DefalutFormatPattern));
		int executeRet = orderService.executeProcedure(procedureDTO);
		logger.info("uid: "+procedureDTO.getUid()+",get_start_date: "+procedureDTO.getStart_date());
		if(executeRet == 0){
			logger.info("newcount: "+procedureDTO.getNewly_count());
			logger.info("newamount_count: "+procedureDTO.getNewly_amount_count());
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{procedureDTO.getName()});
		}
		return procedureDTO.toVTO();
	}
	
	/**
	 * 清除某个或多个设备中的所有终端产生的打赏随机金额记录
	 * @param macs
	 */
	public void clearRewardAMounts(String... macs){
		RewardOrderAmountHashService.getInstance().removeAllRAmountByMacs(macs);
	}
	
	/*************            充值虎钻             ****************/
	
	
	/**
	 * 生成充值虎钻订单
	 * @param uid
	 * @param commdity 商品实体
	 * @param appid 应用id
	 * @param payment_type 支付方式
	 * @param user_agent
	 * @return
	 */
	public Order createRechargeVCurrencyOrder(Integer uid, Integer commdityid, Integer appid, 
			String payment_type, Integer umactype, String user_agent){
		//商品信息验证
		Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
		//验证商品是否合理
		if(!CommdityCategory.correct(commdity.getCategory(), CommdityCategory.RechargeVCurrency)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_ILLEGAL);
		}
		
		String amount = commdity.getPrice();
		long vcurrency = commdity.getVcurrency();
		//订单生成
		Order order = new Order();
		order.setUid(uid);
		order.setCommdityid(commdityid);
		order.setAppid(appid);
		order.setType(commdity.getCategory());
		order.setPayment_type(payment_type);
		order.setUser_agent(user_agent);
		order.setStatus(OrderStatus.NotPay.getKey());
		order.setProcess_status(OrderProcessStatus.NotPay.getKey());
		order.setAmount(amount);
		order.setUmactype(umactype);
		order.setVcurrency(vcurrency);
		orderService.insert(order);
		return order;
	}
	
	
	/**
	 * 充值虎钻支付系统支付完成的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param success 支付是否成功
	 * @param order 订单实体
	 * @param paymented_ds 支付时间 yyyy-MM-dd HH:mm:ss
	 * @param payment_type 支付方式
	 * @param payment_proxy_type 支付代理方式
	 */
	public Order rechargeVCurrencyOrderPaymentCompletedNotify(boolean success, Order order, String paymented_ds,
			String payment_type, String payment_proxy_type){
		Integer changed_status = null;
		Integer changed_process_status = null;
		try{
			String orderid = order.getId();
			Integer order_status = order.getStatus();
			if(!OrderHelper.lte_notpay(order_status)){
				throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_STATUS_INVALID, new String[]{orderid, String.valueOf(order_status)});
			}
			
			if(StringUtils.isNotEmpty(paymented_ds)){
				order.setPayment_type(payment_type);
				order.setPayment_proxy_type(payment_proxy_type);
				order.setPaymented_at(DateTimeHelper.parseDate(paymented_ds, DateTimeHelper.DefalutFormatPattern));
			}

			//支付成功
			if(success){
				changed_status = OrderStatus.DeliverCompleted.getKey();
				changed_process_status = OrderProcessStatus.DeliverCompleted.getKey();
				logger.info(String.format("RechargeVCurrencyOrderPaymentCompletedNotify successed deliver notify: orderid[%s]", orderid));
			}else{
				changed_status = OrderStatus.PayFailured.getKey();
				changed_process_status = OrderProcessStatus.PayFailured.getKey();
			}
		}catch(Exception ex){
			throw ex; 
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
		return order;
	}
	
	/*************            短信认证             ****************/
	
	public static final int SMS_VALIDATE_COMMDITY_ID = 9;
	/**
	 * 生成短信认证订单
	 * @param mac 设备mac
	 * @param umac 用户终端mac
	 * @param umactype 用户终端类型
	 * @param bindUser
	 * @param context 验证的手机号
	 * @param spendvcurrency 是否花费虎钻
	 * @return
	 */
	public Order createSMSOrder(String mac, String mac_dut, String umac, Integer umactype, User bindUser,
			String context, String user_agent, boolean spendvcurrency){
		//商品信息验证
		Commdity commdity = commdityFacadeService.validateCommdity(SMS_VALIDATE_COMMDITY_ID);
		//验证商品是否合理
		if(!CommdityCategory.correct(commdity.getCategory(), CommdityCategory.SMSInternetLimit)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_ILLEGAL);
		}
		
		long vcurrency = 0l;
		//订单生成
		Order order = new Order();
		order.setCommdityid(commdity.getId());
		order.setAppid(CommdityApplication.DEFAULT.getKey());
		order.setType(commdity.getCategory());
		order.setStatus(OrderStatus.PaySuccessed.getKey());
		order.setProcess_status(OrderProcessStatus.PaySuccessed.getKey());
		order.setMac(mac);
		order.setMac_dut(mac_dut);
		order.setUmac(umac);
		order.setUmactype(umactype);
		if(spendvcurrency){
			vcurrency = commdity.getVcurrency();
		}
		order.setVcurrency(vcurrency);
		if(bindUser != null){
			order.setUid(bindUser.getId());
		}
		order.setContext(context);
		order.setUser_agent(user_agent);
		order.setPaymented_at(new Date());
		orderService.insert(order);
		
		//短信认证订单生成时候已经验证通过 直接进行放行数据通知
		String notify_message = PaymentNotifyFactoryBuilder.toJsonHasPrefix(ResponseSMSValidateCompletedNotifyDTO.
				builder(order));
		CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(notify_message);
		return order;
	}
	
	/**
	 * 短信认证完成的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param success 支付是否成功
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @param paymented_ds 支付时间 yyyy-MM-dd HH:mm:ss
	 */
	public Order smsOrderPaymentCompletedNotify(boolean success, Order order, User bindUser, Date paymented_ds,
			String ait_time){
		Integer changed_status = null;
		Integer changed_process_status = null;
		try{
			String orderid = order.getId();
			order.setPaymented_at(paymented_ds);

//			if(bindUser != null){
//				order.setUid(bindUser.getId());
//			}
			
			//支付成功
			if(success){
				logger.info(String.format("SmsOrderPaymentCompletedNotify prepare deliver notify: orderid[%s]", orderid));
				//进行发货通知
				boolean deliver_notify_ret = smsOrderPermissionNotify(order, bindUser, ait_time);
				//判断通知发货成功 更新订单状态
				if(deliver_notify_ret){
					changed_status = OrderStatus.DeliverCompleted.getKey();
					changed_process_status = OrderProcessStatus.SharedealCompleted.getKey();
					logger.info(String.format("SmsOrderPaymentCompletedNotify successed deliver notify: orderid[%s]", orderid));
				}else{
					logger.info(String.format("SmsOrderPaymentCompletedNotify failed deliver notify: orderid[%s]", orderid));
				}
			}else{
				changed_status = OrderStatus.PayFailured.getKey();
				changed_process_status = OrderProcessStatus.PayFailured.getKey();
			}
		}catch(Exception ex){
			throw ex; 
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
		return order;
	}
	
	/**
	 * 短信认证通知应用发货，按照约定的redis写入
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @return
	 */
	public boolean smsOrderPermissionNotify(Order order, User bindUser, String ait_time){
		try{
			if(order == null) {
				logger.error("smsOrderPermissionNotify order data not exist");
				return false;
			}
			
			SMSPermissionThroughNotifyDTO smsPermissionNotifyDto = SMSPermissionThroughNotifyDTO.from(order, ait_time, bindUser);
			if(smsPermissionNotifyDto != null){
				//String requestDeliverNotifyMessage = JsonHelper.getJSONString(rewardPermissionNotifyDto);
				String smsPermissionNotifyMessage = PermissionThroughNotifyFactoryBuilder.toJsonHasPrefix(smsPermissionNotifyDto);
				Long notify_ret = CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotify(smsPermissionNotifyMessage);
				//判断通知发货成功
				if(notify_ret != null && notify_ret > 0){
					logger.info(String.format("smsOrderPermissionNotify success deliver notify: message[%s] rpush_ret[%s]", smsPermissionNotifyMessage, notify_ret));
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("smsOrderPermissionNotify exception", ex);
		}
		return false;
	}
	
	/*************            validate             ****************/
	
	public void supportedAppId(Integer appid){
		//认证appid
		if(!CommdityApplication.supported(appid)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_APPID_INVALID, new String[]{String.valueOf(appid)});
		}
	}
	/**
	 * 认证应用id
	 * @param appId 应用id
	 * @param appSecret 应用密钥
	 */
	public void verifyAppId(Integer appid, String appsecret){
		//认证appid
		if(!CommdityApplication.verifyed(appid, appsecret)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_APPID_INVALID, new String[]{String.valueOf(appid)});
		}
	}
	/**
	 * 验证订单id
	 * @param orderId
	 * @return
	 */
	public Order validateOrderId(String orderid){
		Order order = orderService.getById(orderid);
		if(order == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST, new String[]{orderid});
		return order;
	}
	
	/**
	 * 验证应用id 订单id 订单与应用是否匹配
	 * @param orderId
	 * @param appId
	 * @return
	 */
/*	public Order validateOrder(String orderid, Integer appid){
		supportedAppId(appid);
		Order order = validateOrderId(orderid);
		if(!appid.equals(order.getAppid())){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_APPID_INVALID);
		}
		return order;
	}*/
	
	//add by Jason 2016-07-22 Start
	public DeviceOrderStatisticsVTO deviceOrderStatisticsWithProcedure(String start_date, String end_date,String device_mac){
		DeviceOrderStatisticsProduceDTO procedureDTO = new DeviceOrderStatisticsProduceDTO();
		procedureDTO.setStart_date(start_date);
		procedureDTO.setEnd_date(end_date);
		procedureDTO.setDevice_mac(device_mac);
		int executeRet = orderService.executeProcedure(procedureDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{procedureDTO.getName()});
		}
		return procedureDTO.toVTO();
	}
	//add by Jason 2016-07-22 E N D
	
	
/*************            视频放行             ****************/
	
	/**
	 * 生成视频认证订单
	 * @param mac 设备mac
	 * @param umac 用户终端mac
	 * @param umactype 用户终端类型
	 * @param bindUser
	 * @param context 
	 * @param channel 订单渠道 3为utool 
	 * @return
	 */
	public Order createVideoOrder(Integer commdityid,String mac, String mac_dut, String umac, Integer umactype, User bindUser,
			String context, Integer channel,String user_agent){
		//商品信息验证
		Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
		//验证商品是否合理
		if(!CommdityCategory.correct(commdity.getCategory(), CommdityCategory.VideoInternetLimit)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_ILLEGAL);
		}
		
		//订单生成
		Order order = new Order();
		order.setCommdityid(commdity.getId());
		order.setAppid(CommdityApplication.DEFAULT.getKey());
		order.setType(commdity.getCategory());
		order.setChannel(channel);
		order.setStatus(OrderStatus.NotPay.getKey());
		order.setProcess_status(OrderProcessStatus.NotPay.getKey());
		order.setMac(mac);
		order.setMac_dut(mac_dut);
		order.setUmac(umac);
		order.setUmactype(umactype);
		if(bindUser != null){
			order.setUid(bindUser.getId());
		}
		order.setContext(context);
		order.setUser_agent(user_agent);
		orderService.insert(order);
		
		return order;
	}
	
	/**
	 * 视频上网完成的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param success 支付是否成功
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @param paymented_ds 支付时间 yyyy-MM-dd HH:mm:ss
	 */
	public Order videoOrderPaymentCompletedNotify(boolean success, Order order, User bindUser, Date paymented_ds,
			String ait_time){
		Integer changed_status = null;
		Integer changed_process_status = null;
		try{
			String orderid = order.getId();
			order.setPaymented_at(paymented_ds);

			//支付成功
			if(success){
				logger.info(String.format("VideoOrderPaymentCompletedNotify prepare deliver notify: orderid[%s]", orderid));
				//进行发货通知
				boolean deliver_notify_ret = videoOrderPermissionNotify(order, bindUser, ait_time);
				//判断通知发货成功 更新订单状态
				if(deliver_notify_ret){
					changed_status = OrderStatus.DeliverCompleted.getKey();
					changed_process_status = OrderProcessStatus.SharedealCompleted.getKey();
					logger.info(String.format("VideoOrderPaymentCompletedNotify successed deliver notify: orderid[%s]", orderid));
				}else{
					logger.info(String.format("VideoOrderPaymentCompletedNotify failed deliver notify: orderid[%s]", orderid));
				}
			}else{
				changed_status = OrderStatus.PayFailured.getKey();
				changed_process_status = OrderProcessStatus.PayFailured.getKey();
			}
		}catch(Exception ex){
			throw ex; 
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
		return order;
	}
	
	/**
	 * 视频上网通知应用发货，按照约定的redis写入
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @return
	 */
	public boolean videoOrderPermissionNotify(Order order, User bindUser, String ait_time){
		try{
			if(order == null) {
				logger.error("videoOrderPermissionNotify order data not exist");
				return false;
			}
			
			VideoPermissionThroughNotifyDTO videoPermissionNotifyDto = VideoPermissionThroughNotifyDTO.from(order, ait_time, bindUser);
			if(videoPermissionNotifyDto != null){
				String videoPermissionNotifyMessage = PermissionThroughNotifyFactoryBuilder.toJsonHasPrefix(videoPermissionNotifyDto);
				Long notify_ret = CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotify(videoPermissionNotifyMessage);
				//判断通知发货成功
				if(notify_ret != null && notify_ret > 0){
					logger.info(String.format("videoOrderPermissionNotify success deliver notify: message[%s] rpush_ret[%s]", videoPermissionNotifyMessage, notify_ret));
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("videoOrderPermissionNotify exception", ex);
		}
		return false;
	}
	
	
	/**
	 * 生成短信营销订单
	 * @param commdityid 
	 * @param bindUser User 
	 * @param cost_vcurrency 花费多少虎钻
	 * @return
	 */
	public Order createSMSPromotionOrder(Integer commdityid, User bindUser,long cost_vcurrency){
		//商品信息验证
		Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
		//验证商品是否合理
		if(!CommdityCategory.correct(commdity.getCategory(), CommdityCategory.SMSPromotionLimit)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_ILLEGAL);
		}
		
		//订单生成
		Order order = new Order();
		order.setCommdityid(commdity.getId());
		order.setAppid(CommdityApplication.DEFAULT.getKey());
		order.setType(commdity.getCategory());
		order.setVcurrency(cost_vcurrency);
		order.setStatus(OrderStatus.NotPay.getKey());
		order.setProcess_status(OrderProcessStatus.NotPay.getKey());
		if(bindUser != null){
			order.setUid(bindUser.getId());
		}
		orderService.insert(order);
		
		return order;
	}
	/***
	 * @param uid 用户id
	 * @param count 短信数目
	 * 
	 * */
	public long getSMSPromotionSpendvcurrency(int uid,int count){
		long cost_vcurrency = 20*count;
		return cost_vcurrency;
		
	}
	
	public OrderSMSPromotionDTO vcurrencyFromUserWalletForSMSPromotion(int uid, int commdityid, int count,String desc){
		logger.info(String.format("vcurrencyFromUserWalletForSMSPromotion uid[%s] commdityid[%s] count[%s] desc[%s]",
				uid,commdityid,count,desc));
		long cost_vcurrency = getSMSPromotionSpendvcurrency(uid, count);
		User bindUser = userService.getById(uid);
		Order order = createSMSPromotionOrder(commdityid, bindUser, cost_vcurrency);
		if (order == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST);
		final AtomicLong vcurrency_current_leave = new AtomicLong(0l);
		SnkAuthenticateResultType ret = userWalletFacadeService.deductVcurrencyForSMSPromotion(uid,order.getId(),order.getVcurrency(),desc,new IWalletVCurrencySpendCallback(){

			@Override
			public boolean beforeCheck(int uid, long vcurrency_cost, long vcurrency_has) {
				if(vcurrency_has < vcurrency_cost){
					return false;
				}else{
					return true;
				}
			}

			@Override
			public String after(int uid, long vcurrency_leave) {
				vcurrency_current_leave.addAndGet(vcurrency_leave);
				return null;
			}
		});
		if (ret == SnkAuthenticateResultType.Success){
			order.setPaymented_at(new Date());
			order.setStatus(OrderStatus.DeliverCompleted.getKey());
			order.setProcess_status(OrderProcessStatus.SharedealCompleted.getKey());
			orderService.update(order);
			
			OrderSMSPromotionDTO dto = new OrderSMSPromotionDTO();
			dto.setId(order.getId());
			dto.setUid(order.getUid().intValue());
			dto.setVcurrency_cost(order.getVcurrency());
			logger.info(String.format("SMSPromotion successfully [%s]", dto.toString()));
			return dto;
		}else if(ret == SnkAuthenticateResultType.FailedThresholdVcurrencyNotsufficient){
			logger.info(String.format("vcurrencyFromUserWalletForSMSPromotion failed ErrInfo[Vcurrency Not sufficient]"));
			throw new BusinessI18nCodeException(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT,new String[]{""});
		}else if (ret == SnkAuthenticateResultType.Failed){
			logger.info(String.format("vcurrencyFromUserWalletForSMSPromotion failed ErrInfo[Vcurrency deduct failed]"));
			throw new BusinessI18nCodeException(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_DEDUCT_FAILED,new String[]{""});
		}
		
		return null;
		
	}

	public Order createMonthlyServiceOrder(Integer commdityid, String mac, String mac_dut, String umac,
			Integer umactype, User bindUser, String context, Integer channel, 
			String user_agent, int count, String acc, String uname, String address) {
		//商品信息验证
		Commdity commdity = commdityFacadeService.validateCommdity(commdityid);
		//验证商品是否合理
		if(!CommdityCategory.correct(commdity.getCategory(), CommdityCategory.RewardMonthlyServiceLimit)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_ILLEGAL);
		}
		String amount = ArithHelper.getCuttedCurrency(ArithHelper.mul(Double.parseDouble(count+""), Double.parseDouble(commdity.getPrice()))+"");
		String userInfo = String.format("%s,%s,%s,%s",count,acc,uname,address);
		//订单生成
		Order order = new Order();
		order.setCommdityid(commdity.getId());
		order.setAppid(CommdityApplication.DEFAULT.getKey());
		order.setType(commdity.getCategory());
		order.setChannel(channel);
		order.setAmount(amount);
		if (userInfo.length() > 255){
			order.setContext(userInfo.substring(0, 255));
		}else{
			order.setContext(userInfo);
		}
		order.setStatus(OrderStatus.NotPay.getKey());
		order.setProcess_status(OrderProcessStatus.NotPay.getKey());
		order.setMac(mac);
		order.setMac_dut(mac_dut);
		order.setUmac(umac);
		order.setUmactype(umactype);
		if(bindUser != null){
			order.setUid(bindUser.getId());
		}
		order.setUser_agent(user_agent);
		orderService.insert(order);
				
		return order;
	}
	
	public CommdityPhysical buildCommdityPhysical(String umac,String uname, String acc, String address){
		CommdityPhysical order = new CommdityPhysical();
		order.setId(umac);
		CommdityPhysicalDTO dto = CommdityPhysicalDTO.buildCommdityPhysicalDTO(umac,uname, acc, address);
		order.setExtension_content(JsonHelper.getJSONString(dto));
		return order;
	}
	
	
	/**
	 * 购买实体商品支付完成的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param success 支付是否成功
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @param paymented_ds 支付时间 yyyy-MM-dd HH:mm:ss
	 * @param payment_type 支付方式
	 * @param payment_proxy_type 支付代理方式
	 */
	public Order CommdityPhysicalOrderPaymentCompletedNotify(boolean success, Order order, User bindUser, String paymented_ds,
			String payment_type, String payment_proxy_type,String ait_time){
		Integer changed_status = null;
		Integer changed_process_status = null;
		try{
			String orderid = order.getId();
			
			if(StringUtils.isNotEmpty(paymented_ds)){
				order.setPayment_type(payment_type);
				order.setPayment_proxy_type(payment_proxy_type);
				order.setPaymented_at(DateTimeHelper.parseDate(paymented_ds, DateTimeHelper.DefalutFormatPattern));
			}
			
			//支付成功
			if(success){
				changed_status = OrderStatus.PaySuccessed.getKey();
				changed_process_status = OrderProcessStatus.PaySuccessed.getKey();

				logger.info(String.format("CommdityPhysicalOrderPaymentCompletedNotify prepare deliver notify: orderid[%s]", orderid));
				//进行发货通知
				boolean deliver_notify_ret = commdityPhysicalOrderPermissionNotify(order, bindUser, ait_time);
				//判断通知发货成功 更新订单状态
				if(deliver_notify_ret){
					changed_status = OrderStatus.DeliverCompleted.getKey();
					changed_process_status = OrderProcessStatus.DeliverCompleted.getKey();
					logger.info(String.format("CommdityPhysicalOrderPaymentCompletedNotify successed deliver notify: orderid[%s]", orderid));
				}else{
					logger.info(String.format("CommdityPhysicalOrderPaymentCompletedNotify failed deliver notify: orderid[%s]", orderid));
				}
			}else{
				changed_status = OrderStatus.PayFailured.getKey();
				changed_process_status = OrderProcessStatus.PayFailured.getKey();
			}
		}catch(Exception ex){
			throw ex; 
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
		return order;
	}
	
	
	public boolean commdityPhysicalOrderPermissionNotify(Order order, User bindUser, String ait_time){
		try{
			if(order == null) {
				logger.error("commdityPhysicalOrderPermissionNotify order data not exist");
				return false;
			}
			
			PhysicalPermissionThroughNotifyDTO physicalPermissionNotifyDto = PhysicalPermissionThroughNotifyDTO.from(order, ait_time, bindUser);
			if(physicalPermissionNotifyDto != null){
				String physicalPermissionNotifyMessage = PermissionThroughNotifyFactoryBuilder.toJsonHasPrefix(physicalPermissionNotifyDto);
				Long notify_ret = CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotify(physicalPermissionNotifyMessage);
				//判断通知发货成功
				if(notify_ret != null && notify_ret > 0){
					logger.info(String.format("commdityPhysicalOrderPermissionNotify success deliver notify: message[%s] rpush_ret[%s]", physicalPermissionNotifyMessage, notify_ret));
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("commdityPhysicalOrderPermissionNotify exception", ex);
		}
		return false;
	}
}
