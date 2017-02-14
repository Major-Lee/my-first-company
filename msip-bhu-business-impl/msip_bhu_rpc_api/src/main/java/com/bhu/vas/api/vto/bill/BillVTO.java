package com.bhu.vas.api.vto.bill;

import java.util.List;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class BillVTO implements java.io.Serializable {
	private String startTime;
	private String endTtime;
	private String amountT;
	private String amountC;
	private String amountU;
	
	private List<BillDayVTO> billDay;


	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTtime() {
		return endTtime;
	}

	public void setEndTtime(String endTtime) {
		this.endTtime = endTtime;
	}

	public String getAmountT() {
		return amountT;
	}

	public void setAmountT(String amountT) {
		this.amountT = amountT;
	}

	public String getAmountC() {
		return amountC;
	}

	public void setAmountC(String amountC) {
		this.amountC = amountC;
	}

	public String getAmountU() {
		return amountU;
	}

	public void setAmountU(String amountU) {
		this.amountU = amountU;
	}

	public List<BillDayVTO> getBillDay() {
		return billDay;
	}

	public void setBillDay(List<BillDayVTO> billDay) {
		this.billDay = billDay;
	}
}
