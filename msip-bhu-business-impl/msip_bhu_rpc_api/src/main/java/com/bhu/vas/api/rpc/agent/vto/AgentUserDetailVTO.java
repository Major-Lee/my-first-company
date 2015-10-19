package com.bhu.vas.api.rpc.agent.vto;

@SuppressWarnings("serial")
public class AgentUserDetailVTO implements java.io.Serializable{
	private int id;
	private int countrycode;
	private String mobileno;
	private String nick;
	//是否是注册  true 注册  false 登录
	private boolean reg = false;
	private int utype;
	private String org;
	private String bln;
	private String addr1;
	private String addr2;
	private String memo;
	public AgentUserDetailVTO() {
	}
	public AgentUserDetailVTO(int id, 
			int countrycode, 
			String mobileno, 
			String nick,
			String org,
			String bln,
			String addr1,
			String addr2,
			String memo,
			int utype,
			boolean reg) {
		super();
		this.id = id;
		this.countrycode = countrycode;
		this.mobileno = mobileno;
		this.nick = nick;
		
		this.org = org;
		this.bln = bln;
		this.addr1 = addr1;
		this.addr2 = addr2;
		this.memo = memo;
		
		this.utype = utype;
		this.reg = reg;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public boolean isReg() {
		return reg;
	}
	public void setReg(boolean reg) {
		this.reg = reg;
	}
	public int getUtype() {
		return utype;
	}
	public void setUtype(int utype) {
		this.utype = utype;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	public String getAddr2() {
		return addr2;
	}
	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getBln() {
		return bln;
	}
	public void setBln(String bln) {
		this.bln = bln;
	}
	
}
