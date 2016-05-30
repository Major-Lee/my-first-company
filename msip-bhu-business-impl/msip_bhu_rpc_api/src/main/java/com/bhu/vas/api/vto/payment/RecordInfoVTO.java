package com.bhu.vas.api.vto.payment;

import java.io.Serializable;
import com.bhu.vas.api.rpc.payment.vto.PaymentRecordVTO;
import java.util.List;
/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年5月25日 下午5:25:52
 */
@SuppressWarnings("serial")
public class RecordInfoVTO implements Serializable{
	private List<PaymentRecordVTO> info;
	private int history_order_count;
	private int history_order_user;

	public List<PaymentRecordVTO> getInfo() {
		return info;
	}
	public void setInfo(List<PaymentRecordVTO> info) {
		this.info = info;
	}
	public int getHistory_order_count() {
		return history_order_count;
	}
	public void setHistory_order_count(int history_order_count) {
		this.history_order_count = history_order_count;
	}
	public int getHistory_order_user() {
		return history_order_user;
	}
	public void setHistory_order_user(int history_order_user) {
		this.history_order_user = history_order_user;
	}
	public int getPay_again_user_count() {
		return pay_again_user_count;
	}
	public void setPay_again_user_count(int pay_again_user_count) {
		this.pay_again_user_count = pay_again_user_count;
	}
	private int pay_again_user_count;
	
}

