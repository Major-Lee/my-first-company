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
	//是否需要发票
	boolean	needInvoice;
	//发票详细信息
	String invoiceDetail;
	public boolean isNeedInvoice() {
		return needInvoice;
	}
	public void setNeedInvoice(boolean needInvoice) {
		this.needInvoice = needInvoice;
	}
	public String getInvoiceDetail() {
		return invoiceDetail;
	}
	public void setInvoiceDetail(String invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
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
	
	public static CommdityPhysicalDTO buildCommdityPhysicalDTO(String umac, String uname, String acc, 
			String address, boolean needInvoice, String invoiceDetail){
		CommdityPhysicalDTO dto = new CommdityPhysicalDTO();
		dto.setUmac(umac);
		dto.setAcc(acc);
		dto.setUname(uname);
		dto.setAddress(address);
		dto.setNeedInvoice(needInvoice);
		dto.setInvoiceDetail(invoiceDetail);
		return dto;
	}
}
