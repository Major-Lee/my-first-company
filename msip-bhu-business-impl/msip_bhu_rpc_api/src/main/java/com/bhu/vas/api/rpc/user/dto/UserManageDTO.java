package com.bhu.vas.api.rpc.user.dto;

@SuppressWarnings("serial")
public class UserManageDTO implements java.io.Serializable{
	//必虎Id
	private int uid;
	//用户类型
	private String userType;
	//手机号
	private String mobileNo;
	//客户端类型
	private String regdevice;
	//标签
	private String userLabel;
	//零钱
	private String walletMoney;
	//虎钻
	private String vcurrency;
	//设备在线数
	private int doc;
	//设备总数
	private int dc;
	//注册时间
	private String createTime;
	//打赏样式
	private String rewardStyle; 
	//返现
	private String isCashBack;
	//用户总数
	private int userNum;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getRegdevice() {
		return regdevice;
	}
	public void setRegdevice(String regdevice) {
		this.regdevice = regdevice;
	}
	public String getUserLabel() {
		return userLabel;
	}
	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}
	public String getWalletMoney() {
		return walletMoney;
	}
	public void setWalletMoney(String walletMoney) {
		this.walletMoney = walletMoney;
	}
	public String getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(String vcurrency) {
		this.vcurrency = vcurrency;
	}
	public int getDoc() {
		return doc;
	}
	public void setDoc(int doc) {
		this.doc = doc;
	}
	public int getDc() {
		return dc;
	}
	public void setDc(int dc) {
		this.dc = dc;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getRewardStyle() {
		return rewardStyle;
	}
	public void setRewardStyle(String rewardStyle) {
		this.rewardStyle = rewardStyle;
	}
	public String getIsCashBack() {
		return isCashBack;
	}
	public void setIsCashBack(String isCashBack) {
		this.isCashBack = isCashBack;
	}
	public int getUserNum() {
		return userNum;
	}
	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}
	
}
