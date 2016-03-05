package com.bhu.vas.business.ds.commdity.facade;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.internal.pay.OrderPaymentNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponseCreatePaymentUrlDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.ds.commdity.helper.CommdityHelper;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class OrderFacadeService {
	
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
			.andColumnEqualTo("status", OrderStatus.NotPay.getKey());
		mc.setOrderByClause("created_at desc");
		mc.setSize(1);
		List<Order> orderList = orderService.findModelByModelCriteria(mc);
		if(!orderList.isEmpty()){
			return orderList.get(0);
		}
		return null;
	}
	
	/**
	 * 更新订单的状态和流程状态
	 * @param order 订单实体
	 * @param status 订单状态
	 * @param process_status 订单流程状态
	 */
	public void orderStatusChanged(Order order, Integer status, Integer process_status){
		if(order != null){
			order.setStatus(status);
			order.setProcess_status(process_status);
			orderService.update(order);
		}
	}
	
	/*************            status             ****************/
	/**
	 * 生成订单
	 * @param commdity 商品实体
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @param uid 用户id
	 * @param context 业务上下文
	 * @return
	 */
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
	}
	
	/**
	 * 调用支付系统获取支付url信息完成后的订单处理逻辑
	 * 
	 * @param orderid 订单id
	 * @param rcp_dto 支付系统返回的支付url信息DTO
	 * @return
	 */
	public String orderPaymentUrlCreated(String orderid, ResponseCreatePaymentUrlDTO rcp_dto) {
		Integer changed_status = OrderStatus.NotPay.getKey();
		Integer changed_process_status = OrderProcessStatus.NotPay.getKey();
		Order order = null;
		try{
			if(rcp_dto == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID);
			}
			
			if(!rcp_dto.isSuccess()){
				throw new BusinessI18nCodeException(ResponseErrorCode.INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE, new String[]{rcp_dto.getMsg()});
			}
		
			order = orderService.getById(orderid);
			if(order == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST, new String[]{orderid});
			}
			
			changed_process_status = OrderProcessStatus.Paying.getKey();
			return rcp_dto.getParams();
		}catch(Exception ex){
			throw ex;
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
	}
	
	/**
	 * 支付系统支付成功的订单处理逻辑
	 * 更新订单状态为支付成功
	 * 通知应用发货成功以后 更新支付状态为发货完成
	 * @param opn_dto 支付成功通知dto
	 */
	public Order orderPaymentNotify(OrderPaymentNotifyDTO opn_dto){
		Integer changed_status = OrderStatus.PaySuccessed.getKey();
		Integer changed_process_status = OrderProcessStatus.PaySuccessed.getKey();
		Order order = null;
		try{
			if(opn_dto == null) {
				throw new RuntimeException(String.format("orderPaymentNotify param illegal opn_dto[%s]", opn_dto));
			}
			String orderid = opn_dto.getOrderid();
			if(StringUtils.isEmpty(orderid)){
				throw new RuntimeException(String.format("orderPaymentNotify param illegal orderid[%s]", orderid));
			}
			
			order = orderService.getById(orderid);
			if(order == null)
				throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_ORDER_DATA_NOTEXIST, new String[]{orderid});

			order.setPaymented_at(new Date(opn_dto.getPayment_ts()));
			//通知应用发货
			CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNofity("test");
			
			changed_status = OrderStatus.DeliverCompleted.getKey();
			changed_process_status = OrderProcessStatus.DeliverCompleted.getKey();
		}catch(Exception ex){
			throw ex; 
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
		return order;
	}
}
