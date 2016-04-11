package com.bhu.vas.business.ds.commdity.facade;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.internal.portal.RequestDeliverNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.helper.CommdityHelper;
import com.bhu.vas.api.rpc.commdity.helper.OrderHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityIntervalAmountService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
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
	 */
	public int countOrderByParams(Integer uid, String mac, String umac, Integer status, String dut){
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
		return orderService.countByModelCriteria(mc);
	}
	
	/**
	 * 根据订单参数进行查询订单分页列表
	 * @param uid
	 * @param mac 设备mac
	 * @param umac 支付订单的用户mac
	 * @param status 订单状态
	 * @param dut 订单业务线
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Order> findOrdersByParams(Integer uid, String mac, String umac, Integer status, String dut, 
			int pageNo, int pageSize){
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
		mc.setOrderByClause("created_at desc");
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return orderService.findModelByModelCriteria(mc);
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
	
	/*************            status             ****************/
/*	*//**
	 * 生成订单
	 * @param commdity 商品实体
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param uid 用户id
	 * @param context 业务上下文
	 * @return
	 *//*
	public Order createOrder(Integer commdityid, Integer appid, String mac, String umac, Integer uid, String context){
		//商品信息验证
		Commdity commdity = commdityService.getById(commdityid);
		if(commdity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST);
		}
		if(!CommdityHelper.onsale(commdity.getStatus())){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_NOT_ONSALE);
		}
		//订单金额处理
		String amount = CommdityHelper.generateCommdityAmount(commdity.getPrice());
		if(StringUtils.isEmpty(amount)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_AMOUNT_INVALID);
		}
		
		Order order = null;
		//如果商品分类是限时上网 防止商品金额重新随机 检测是否有旧的未支付订单存在
		Integer commdity_category = commdity.getCategory();
		if(CommdityCategory.InternetLimit.getCategory().equals(commdity_category)){
			order = recentNotpayOrderByUmac(commdity.getId(), appid, mac, umac);
		}
		
		if(order == null){
			//订单生成
			order = new Order();
			order.setCommdityid(commdity.getId());
			order.setAppid(appid);
			order.setMac(mac);
			order.setUmac(umac);
			order.setUid(uid);
			order.setContext(context);
			order.setStatus(OrderStatus.Pending.getKey());
			order.setProcess_status(OrderProcessStatus.Pending.getKey());
			order.setAmount(amount);
			orderService.insert(order);
		}
		return order;
	}*/
	
/*	*//**
	 * 调用支付系统获取支付url信息完成后的订单处理逻辑
	 * 
	 * @param orderId 订单id
	 * @param rcp_dto 支付系统返回的支付url信息DTO
	 * @return
	 *//*
	public String orderPaymentUrlCreated(String orderid, ResponseCreatePaymentUrlDTO rcp_dto) {
		Integer changed_status = OrderStatus.NotPay.getKey();
		Integer changed_process_status = OrderProcessStatus.NotPay.getKey();
		Order order = null;
		try{
			if(!rcp_dto.isSuccess()){
				String errorcode = rcp_dto.getErrorcode();
				//如果订单已经支付成功 则返回订单已经支付的状态码
				if(PaymentInternalHelper.ERRORCODE_PAYMENT_STATUS_PAYSUCCESSED.equals(errorcode)){
					throw new BusinessI18nCodeException(ResponseErrorCode.ORDER_PAYMENT_STATUS_PAYSUCCESSED);
				}
				throw new BusinessI18nCodeException(ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE, new String[]{rcp_dto.getMsg()});
			}
		
			order = validateOrderId(orderid);
			
			changed_process_status = OrderProcessStatus.Paying.getKey();
			return rcp_dto.getParams();
		}catch(Exception ex){
			throw ex;
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
	}*/
	
	/**
	 * 生成订单
	 * @param commdity 商品实体
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param mac_dut 设备业务线
	 * @param umac 用户mac
	 * @param umactype 终端类型
	 * @param payment_type 支付方式
	 * @param context 业务上下文
	 * @return
	 */
	public Order createOrder(Integer commdityid, Integer appid, String mac, String mac_dut, String umac, 
			Integer umactype, String payment_type, String context){
		//商品信息验证
		Commdity commdity = commdityService.getById(commdityid);
		if(commdity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST);
		}
		if(!CommdityHelper.onsale(commdity.getStatus())){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_NOT_ONSALE);
		}
		//验证缓存中的商品金额
		String amount = CommdityIntervalAmountService.getInstance().getRAmount(mac, umac, commdityid);
		if(StringUtils.isEmpty(amount)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_AMOUNT_INVALID);
		}
		
		//订单生成
		Order order = new Order();
		order.setCommdityid(commdity.getId());
		order.setAppid(appid);
		order.setMac(mac);
		order.setMac_dut(mac_dut);
		order.setUmac(umac);
		order.setUmactype(umactype);
		//order.setUid(uid);
		//order.setPayment_type(payment_type);
		order.setContext(context);
		order.setStatus(OrderStatus.NotPay.getKey());
		
		order.setProcess_status(OrderProcessStatus.NotPay.getKey());
		order.setAmount(amount);
		orderService.insert(order);
		return order;
	}
	
	/**
	 * 支付系统支付完成的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param success 支付是否成功
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @param paymented_ds 支付时间 yyyy-MM-dd HH:mm:ss
	 * @param payment_type 支付方式
	 * @param payment_proxy_type 支付代理方式
	 */
	public Order orderPaymentCompletedNotify(boolean success, Order order, User bindUser, String paymented_ds,
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
			
			if(bindUser != null){
				order.setUid(bindUser.getId());
			}
			
			//支付成功
			if(success){
				changed_status = OrderStatus.PaySuccessed.getKey();
				changed_process_status = OrderProcessStatus.PaySuccessed.getKey();

				logger.info(String.format("OrderPaymentCompletedNotify prepare deliver notify: orderid[%s]", orderid));
				//进行发货通知
				boolean deliver_notify_ret = orderDeliverNotify(order, bindUser);
				//判断通知发货成功 更新订单状态
				if(deliver_notify_ret){
					changed_status = OrderStatus.DeliverCompleted.getKey();
					changed_process_status = OrderProcessStatus.DeliverCompleted.getKey();
					logger.info(String.format("OrderPaymentCompletedNotify successed deliver notify: orderid[%s]", orderid));
				}else{
					logger.info(String.format("OrderPaymentCompletedNotify failed deliver notify: orderid[%s]", orderid));
				}
				//清除缓存中的随机金额
				CommdityIntervalAmountService.getInstance().removeRAmount(order.getMac(), order.getUmac(), order.getCommdityid());
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
	 * 支付系统支付完成的订单处理逻辑
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
	public Order orderPaymentCompletedNotify(boolean success, String orderid, User bindUser, String paymented_ds,
			String payment_type, String payment_proxy_type){
		Order order = validateOrderId(orderid);
		return orderPaymentCompletedNotify(success, order, bindUser, paymented_ds, payment_type, payment_proxy_type);
	}
	
	/**
	 * 通知应用发货，按照约定的redis写入
	 * @param order 订单实体
	 * @param bindUser 设备绑定的用户实体
	 * @return
	 */
	public boolean orderDeliverNotify(Order order, User bindUser){
		try{
			if(order == null) {
				logger.error("orderDeliverNotify order data not exist");
				return false;
			}
			Integer commdityid = order.getCommdityid();
			Commdity commdity = commdityService.getById(commdityid);
			if(commdity == null){
				logger.error("orderDeliverNotify order commdity data not exist");
				return false;
			}
			RequestDeliverNotifyDTO requestDeliverNotifyDto = RequestDeliverNotifyDTO.from(order, commdity, bindUser);
			if(requestDeliverNotifyDto != null){
				String requestDeliverNotifyMessage = JsonHelper.getJSONString(requestDeliverNotifyDto);
				Long notify_ret = CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotify(requestDeliverNotifyMessage);
				//判断通知发货成功
				if(notify_ret != null && notify_ret > 0){
					logger.info(String.format("OrderDeliverNotify success deliver notify: message[%s] rpush_ret[%s]", requestDeliverNotifyMessage, notify_ret));
					return true;
				}
/*				List<Object> notify_ret = CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotifyTransaction(requestDeliverNotifyMessage);
				//判断通知发货成功
				if(notify_ret != null && notify_ret.size() == 3){
					logger.info(String.format("OrderDeliverNotify success deliver notify: message[%s] slen[%s] rpush_ret[%s] elen[%s]", 
							requestDeliverNotifyMessage, notify_ret.get(0), notify_ret.get(1), notify_ret.get(2)));
					return true;
				}*/
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("orderDeliverNotify exception", ex);
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
	public Order validateOrder(String orderid, Integer appid){
		supportedAppId(appid);
		Order order = validateOrderId(orderid);
		if(!appid.equals(order.getAppid())){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_APPID_INVALID);
		}
		return order;
	}
}
