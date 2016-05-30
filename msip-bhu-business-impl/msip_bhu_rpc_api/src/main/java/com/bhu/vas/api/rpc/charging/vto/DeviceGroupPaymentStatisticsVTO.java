package com.bhu.vas.api.rpc.charging.vto;

@SuppressWarnings("serial")
public class DeviceGroupPaymentStatisticsVTO implements java.io.Serializable{
	//今日收益额
	private String t_incoming_amount= "0";
	//今日打赏次数
	private int t_times = 0;
	//昨日收益额
	private String y_incoming_amount = "0";
	//昨日打赏次数
	private int y_times = 0;
	//收益总额
	private String incoming_amount = "0";
	//打赏总次数
	private int times = 0;
	
	public String getT_incoming_amount() {
		return t_incoming_amount;
	}
	public void setT_incoming_amount(String t_incoming_amount) {
		this.t_incoming_amount = t_incoming_amount;
	}
	public String getY_incoming_amount() {
		return y_incoming_amount;
	}
	public void setY_incoming_amount(String y_incoming_amount) {
		this.y_incoming_amount = y_incoming_amount;
	}
	public String getIncoming_amount() {
		return incoming_amount;
	}
	public void setIncoming_amount(String incoming_amount) {
		this.incoming_amount = incoming_amount;
	}
	public int getT_times() {
		return t_times;
	}
	public void setT_times(int t_times) {
		this.t_times = t_times;
	}
	public int getY_times() {
		return y_times;
	}
	public void setY_times(int y_times) {
		this.y_times = y_times;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
}
