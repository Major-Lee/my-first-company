package com.bhu.vas.api.rpc.user.dto;

@SuppressWarnings("serial")
public class UserManageDTO implements java.io.Serializable{
	//必虎Id
	private int uid;
	//昵称
	private String nickName;
	//性别
	private String sex;
	//签名
	private String signature; 
	//用户类型
	private String userType;
	//手机号
	private String mobileNo;
	//客户端类型
	private String regdevice;
	//标签
	private String userLabel;
	//零钱
	private double walletMoney = 0.00d;;
	//虎钻
	private double vcurrency = 0l;
	//设备在线数
	private long doc;
	//设备总数
	private long dc;
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
	
	public double getWalletMoney() {
		return walletMoney;
	}
	public void setWalletMoney(double walletMoney) {
		this.walletMoney = walletMoney;
	}
	public double getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(double vcurrency) {
		this.vcurrency = vcurrency;
	}
	
	public long getDoc() {
		return doc;
	}
	public void setDoc(long doc) {
		this.doc = doc;
	}
	public long getDc() {
		return dc;
	}
	public void setDc(long dc) {
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
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
}
