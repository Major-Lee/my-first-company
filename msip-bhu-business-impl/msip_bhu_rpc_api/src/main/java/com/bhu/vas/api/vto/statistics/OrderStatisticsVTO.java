package com.bhu.vas.api.vto.statistics;

/**
 * 订单统计数据模型
 * 时间段内的订单创建数，订单完成数，完成订单的金额
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderStatisticsVTO implements java.io.Serializable{
	//order created count
	private int occ = 0;
	//order finish count
	private int ofc = 0;
	//order finish amount
	private String ofa = "0.00";
	
	public int getOcc() {
		return occ;
	}
	public void setOcc(int occ) {
		this.occ = occ;
	}
	public int getOfc() {
		return ofc;
	}
	public void setOfc(int ofc) {
		this.ofc = ofc;
	}
	public String getOfa() {
		return ofa;
	}
	public void setOfa(String ofa) {
		this.ofa = ofa;
	}
}
