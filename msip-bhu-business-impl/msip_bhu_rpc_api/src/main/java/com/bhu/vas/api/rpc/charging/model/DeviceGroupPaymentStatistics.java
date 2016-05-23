package com.bhu.vas.api.rpc.charging.model;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class DeviceGroupPaymentStatistics extends BaseStringModel{
	public static final String DEFAULT_GROUP = "default";
	public static final String TOTAL_DATE_STR = "00000000";
	
	private Integer uid;
	//分组id
	private String groupid;
	//总打赏金额
	private String total_payment_amount;
	//总收益金额
	private String total_incoming_amount;
	//总打赏次数
	private int total_times;
	
	private Date created_at;

	/**
	 * 组合id 
	 * @param groupid 分组id
	 * @param datestr 日期格式 20160519
	 */
	public static String combineid(String groupid, int uid, String datestr){
		if(StringUtils.isEmpty(groupid) || StringUtils.isEmpty(datestr)) return null;
		StringBuffer id = new StringBuffer();
		id.append(groupid);
		id.append(StringHelper.MINUS_CHAR_GAP);
		id.append(uid);
		id.append(StringHelper.MINUS_CHAR_GAP);
		id.append(datestr);
//		this.setId(id.toString());
		return id.toString();
	}
	
	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getTotal_payment_amount() {
		return total_payment_amount;
	}

	public void setTotal_payment_amount(String total_payment_amount) {
		this.total_payment_amount = total_payment_amount;
	}

	public String getTotal_incoming_amount() {
		return total_incoming_amount;
	}

	public void setTotal_incoming_amount(String total_incoming_amount) {
		this.total_incoming_amount = total_incoming_amount;
	}

	public int getTotal_times() {
		return total_times;
	}

	public void setTotal_times(int total_times) {
		this.total_times = total_times;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}
