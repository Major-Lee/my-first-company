package com.bhu.vas.api.vto.advertise;

import java.util.List;

import com.smartwork.msip.cores.helper.ArithHelper;


@SuppressWarnings("serial")
public class AdvertiseOccupiedVTO implements java.io.Serializable{
	private String date;
	private int count;
	private String cash;
	private List<AdvertiseTrashPositionVTO> trashs;
	
	public int getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = (int) (count*1.1);
	}
	
	public String getCash() {
		return cash;
	}
	public void setCash(float cash) {
		this.cash = ArithHelper.getCuttedCurrency(cash+"");
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<AdvertiseTrashPositionVTO> getTrashs() {
		return trashs;
	}
	public void setTrashs(List<AdvertiseTrashPositionVTO> trashs) {
		this.trashs = trashs;
	}
	
}
