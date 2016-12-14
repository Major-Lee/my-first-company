package com.bhu.vas.api.rpc.commdity.vto;


@SuppressWarnings("serial")
public class QualityGoodsSharedealListVTO implements java.io.Serializable{
	private String orderid;
	private String commdityid;
	private String commdityname;
	private String amount;
	private String username;
	private String mobileno;
	private String address;
	private String process_status;
	private String created_at;
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getCommdityid() {
		return commdityid;
	}
	public void setCommdityid(String commdityid) {
		this.commdityid = commdityid;
	}
	public String getCommdityname() {
		return commdityname;
	}
	public void setCommdityname(String commdityname) {
		this.commdityname = commdityname;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProcess_status() {
		return process_status;
	}
	public void setProcess_status(String process_status) {
		this.process_status = process_status;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
}
