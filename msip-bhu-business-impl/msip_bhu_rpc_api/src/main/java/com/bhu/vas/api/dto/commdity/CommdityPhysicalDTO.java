package com.bhu.vas.api.dto.commdity;

@SuppressWarnings("serial")
public class CommdityPhysicalDTO implements java.io.Serializable{
	//用户mac
	private String umac;
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
	}

	//收货人
	private String uname;
	//手机号
	private String acc;
	//收货地址
	private String address;
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public static CommdityPhysicalDTO buildCommdityPhysicalDTO(String umac, String uname, String acc, String address){
		CommdityPhysicalDTO dto = new CommdityPhysicalDTO();
		dto.setUmac(umac);
		dto.setAcc(acc);
		dto.setUname(uname);
		dto.setAddress(address);
		return dto;
	}
}
