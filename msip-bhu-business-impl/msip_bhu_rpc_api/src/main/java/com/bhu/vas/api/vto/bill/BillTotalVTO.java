package com.bhu.vas.api.vto.bill;

import java.util.List;

/**
 * 
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class BillTotalVTO implements java.io.Serializable {
	private String amountT;
	private String amountC;
	private String amountU;
	private String amountPaid;
	private String amountUnPaid;
	

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

	public String getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getAmountUnPaid() {
		return amountUnPaid;
	}

	public void setAmountUnPaid(String amountUnPaid) {
		this.amountUnPaid = amountUnPaid;
	}
}
