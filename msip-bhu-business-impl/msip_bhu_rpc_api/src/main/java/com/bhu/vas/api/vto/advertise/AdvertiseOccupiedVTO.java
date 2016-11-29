package com.bhu.vas.api.vto.advertise;

import java.math.BigDecimal;
import java.util.List;


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
		this.cash = cutDecimal(cash) +"";
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
	
	private float cutDecimal (float f){
		 BigDecimal   b  =   new BigDecimal(f);  
		 return b.setScale(2, BigDecimal.ROUND_DOWN).floatValue();  
	}
}
