package com.bhu.vas.api.dto.commdity.internal.portal;

import java.util.Date;

import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.smartwork.msip.cores.helper.DateTimeHelper;


/**
 * 订单支付成功后,数据写入约定的redis中进行发货通知
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class RequestDeliverNotifyDTO  implements java.io.Serializable{
	//订单id
	private String orderid;
	//订单金额
	private String amount;
	//商品id
	private Integer commdityid;
	//应用发货细节(对于限时上网服务 此属性存储的是限时上网的时间 比如4 代表4小时)
	private String app_deliver_detail;
	//设备mac
	private String mac;
	//用户mac
	private String umac;
	//支付时间
	private String paymented_ds;
	//业务上下文
	private String context;
	//bind user mobileno 设备的绑定用户的手机号
	private String bu_mobileno;
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public Integer getCommdityid() {
		return commdityid;
	}
	public void setCommdityid(Integer commdityid) {
		this.commdityid = commdityid;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getApp_deliver_detail() {
		return app_deliver_detail;
	}
	public void setApp_deliver_detail(String app_deliver_detail) {
		this.app_deliver_detail = app_deliver_detail;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
	}
	public String getPaymented_ds() {
		return paymented_ds;
	}
	public void setPaymented_ds(String paymented_ds) {
		this.paymented_ds = paymented_ds;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getBu_mobileno() {
		return bu_mobileno;
	}
	public void setBu_mobileno(String bu_mobileno) {
		this.bu_mobileno = bu_mobileno;
	}
	
	public static RequestDeliverNotifyDTO from(Order order, Commdity commdity, User bindUser){
		if(order == null || commdity == null) return null;
		RequestDeliverNotifyDTO requestDeliverNotifyDto = new RequestDeliverNotifyDTO();
		requestDeliverNotifyDto.setOrderid(order.getId());
		requestDeliverNotifyDto.setMac(order.getMac());
		requestDeliverNotifyDto.setUmac(order.getUmac());
		requestDeliverNotifyDto.setContext(order.getContext());
		requestDeliverNotifyDto.setCommdityid(order.getCommdityid());
		requestDeliverNotifyDto.setAmount(order.getAmount());
		Date paymented_at = order.getPaymented_at();
		if(paymented_at != null){
			requestDeliverNotifyDto.setPaymented_ds(DateTimeHelper.formatDate(paymented_at, DateTimeHelper.DefalutFormatPattern));
		}
		requestDeliverNotifyDto.setApp_deliver_detail(commdity.getApp_deliver_detail());
		if(bindUser != null){
			requestDeliverNotifyDto.setBu_mobileno(bindUser.getMobileno());
		}
		return requestDeliverNotifyDto;
	}
}

