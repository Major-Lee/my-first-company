package com.bhu.vas.api.vto.statistics;

/**
 * 订单统计数据模型
 * 时间段内的订单创建数，订单完成数，完成订单的金额
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderStatisticsVTO implements java.io.Serializable{
	//pc order created count
	private int pc_occ = 0;
	//pc order finish count
	private int pc_ofc = 0;
	//pc order finish amount
	private String pc_ofa = "0.00";
	
	//mobile order created count
	private int mb_occ = 0;
	//mobile order finish count
	private int mb_ofc = 0;
	//mobile order finish amount
	private String mb_ofa = "0.00";
	
	public int getPc_occ() {
		return pc_occ;
	}
	public void setPc_occ(int pc_occ) {
		this.pc_occ = pc_occ;
	}
	public int getPc_ofc() {
		return pc_ofc;
	}
	public void setPc_ofc(int pc_ofc) {
		this.pc_ofc = pc_ofc;
	}
	public String getPc_ofa() {
		return pc_ofa;
	}
	public void setPc_ofa(String pc_ofa) {
		this.pc_ofa = pc_ofa;
	}
	public int getMb_occ() {
		return mb_occ;
	}
	public void setMb_occ(int mb_occ) {
		this.mb_occ = mb_occ;
	}
	public int getMb_ofc() {
		return mb_ofc;
	}
	public void setMb_ofc(int mb_ofc) {
		this.mb_ofc = mb_ofc;
	}
	public String getMb_ofa() {
		return mb_ofa;
	}
	public void setMb_ofa(String mb_ofa) {
		this.mb_ofa = mb_ofa;
	}
}
