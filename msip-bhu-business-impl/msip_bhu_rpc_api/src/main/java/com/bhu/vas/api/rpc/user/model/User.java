package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.UserType;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.model.BaseIntModel;
@SuppressWarnings("serial")
public class User extends BaseIntModel{// implements ISequenceGenable,TableSplitable<Integer>{
	
	/*public static final int Normal_User = 1;
	public static final int Agent_User = 10;
	public static final int Finance_User = 15;
	public static final int WarehouseManager_User = 20;
	public static final int Sellor_User = 30;*/
	//电话号码国家区号
	private int countrycode = 86;
	private String mobileno;
	//private String email;
	//不唯一的nick
	private String nick;
	private String password;
	private String plainpwd = RuntimeConfiguration.Default_Pwd;
	
	/**
	 * 标识用户是否绑定sns，email，
	 * 手机号等 000代表什么都没有绑定 
	 * 第一位为1代表绑定邮箱 第二位为1 代表绑定了手机号 第三位为1代表绑定了sns 
	 * 参考SafetyBitMarkHelper
	 */
	//private int safety_mark = SafetyBitMarkHelper.None;
	
	private String sex;
	private String birthday;
	
	//是否有效用户
	private boolean validated = false;
	//是否锁住用户 ，例如用户尝试几次都登录失败可能锁住此用户，每天解锁一次
	private boolean locked = false;
	/*private String avatar;
	private String cover;*/
	//公司名称
	private String org;
	//营业执照号Business license number
	private String bln;
	//注册地址
	private String addr1;
	private String addr2;
	private String memo;
	
	//注册ip
	private String regip;
	//通过那种设备注册
	private String regdevice;
	
/*	//用户所处地域
	private String region;
	//用户选择的语言
	private String lang;*/
	
	//用户最后登录设备
	private String lastlogindevice;
	

	private String lastlogindevice_uuid;
	private int utype = UserType.Normal.getIndex();//用户类型
	
	/*private Date lastlogin_at;
	private String channel;
	
*/	//private Integer inviteuid;
	
	private Date created_at;
	public User() {
		super();
	}
	public User(Integer userid){//,String email){
		super();
		this.id = userid;
		//this.email = email;
	}
	/*public User(String email, String plainpwd) {
		super();
		this.email = email;
		this.plainpwd = plainpwd;
	}*/	

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		if(StringUtils.isNotEmpty(this.plainpwd) && StringUtils.isEmpty(this.password))
			this.password = BCryptHelper.hashpw(plainpwd, BCryptHelper.gensalt());//DigestHelper.md5ToHex(this.plainpwd);
		//if(StringUtils.isNotEmpty(this.email)) this.email = this.email.toLowerCase();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		if(StringUtils.isNotEmpty(this.plainpwd) && StringUtils.isEmpty(this.password))
			this.password = BCryptHelper.hashpw(plainpwd, BCryptHelper.gensalt());//DigestHelper.md5ToHex(this.plainpwd);
		//if(StringUtils.isNotEmpty(this.email)) this.email = this.email.toLowerCase();
		super.preUpdate();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	/*public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}*/
	
	
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
	
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public String getPlainpwd() {
		return plainpwd;
	}
	public void setPlainpwd(String plainpwd) {
		this.plainpwd = plainpwd;
	}

	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	
	public String getRegip() {
		return regip;
	}
	public void setRegip(String regip) {
		this.regip = regip;
	}
	
	public String getLastlogindevice() {
		if(StringUtils.isEmpty(lastlogindevice)) return DeviceEnum.PC.getSname();
		return lastlogindevice;
	}
	public void setLastlogindevice(String lastlogindevice) {
		this.lastlogindevice = lastlogindevice;
	}
	
	public String getRegdevice() {
		if(StringUtils.isEmpty(regdevice)) return DeviceEnum.PC.getSname();
		return regdevice;
	}
	public void setRegdevice(String regdevice) {
		this.regdevice = regdevice;
	}
	
	public boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public int getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}
	public String getLastlogindevice_uuid() {
		return lastlogindevice_uuid;
	}
	public void setLastlogindevice_uuid(String lastlogindevice_uuid) {
		this.lastlogindevice_uuid = lastlogindevice_uuid;
	}
	public int getUtype() {
		return utype;
	}
	public void setUtype(int utype) {
		this.utype = utype;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
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
	public String getBln() {
		return bln;
	}
	public void setBln(String bln) {
		this.bln = bln;
	}
}
