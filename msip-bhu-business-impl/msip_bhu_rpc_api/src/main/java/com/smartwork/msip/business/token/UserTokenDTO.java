package com.smartwork.msip.business.token;

@SuppressWarnings("serial")
public class UserTokenDTO implements java.io.Serializable{
	private int id;
	private String atoken;
	private String rtoken;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAtoken() {
		return atoken;
	}
	public void setAtoken(String atoken) {
		this.atoken = atoken;
	}
	public String getRtoken() {
		return rtoken;
	}
	public void setRtoken(String rtoken) {
		this.rtoken = rtoken;
	}
	public UserTokenDTO() {
	}
	public UserTokenDTO(int id, String atoken, String rtoken) {
		super();
		this.id = id;
		this.atoken = atoken;
		this.rtoken = rtoken;
	}
	
}
