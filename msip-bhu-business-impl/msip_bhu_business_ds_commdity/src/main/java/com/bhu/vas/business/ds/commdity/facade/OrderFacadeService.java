package com.bhu.vas.business.ds.commdity.facade;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.commdity.internal.pay.OrderPaymentNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
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
	
	/**
	 * 支付系统支付成功的订单处理逻辑
	 * @param order
	 * @param opn_dto
	 */
	public void orderPaymentNotify(Order order, OrderPaymentNotifyDTO opn_dto){
		if(opn_dto == null || order == null) return;
		
		Integer changed_status = OrderStatus.PaySuccessed.getKey();
		Integer changed_process_status = OrderProcessStatus.PaySuccessed.getKey();
		try{
			//TODO:通知应用发货 如果通知成功 更新status为PaySuccessed 更新支付时间
			order.setPaymented_at(new Date(opn_dto.getPayment_ts()));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{
			orderStatusChanged(order, changed_status, changed_process_status);
		}
	}
}
