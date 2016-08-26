package com.bhu.vas.api.rpc.payment.vto;

/**
 * 提现申请费用消耗
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class PaymentRecordVTO implements java.io.Serializable {
	private String timeD;
	private int amount;
	private int count;
	private int dx_count;
	private int video_count;
	private String info;
	public String getTimeD() {
		return timeD;
	}
	public void setTimeD(String timeD) {
		this.timeD = timeD;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int i) {
		this.amount = i;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getDx_count() {
		return dx_count;
	}
	public void setDx_count(int dx_count) {
		this.dx_count = dx_count;
	}
	public int getVideo_count() {
		return video_count;
	}
	public void setVideo_count(int video_count) {
		this.video_count = video_count;
	}
}
