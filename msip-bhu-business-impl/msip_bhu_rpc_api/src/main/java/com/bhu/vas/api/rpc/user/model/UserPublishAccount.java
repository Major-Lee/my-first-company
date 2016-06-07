package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 提现对公账号绑定
 * @author Jason
 *
 */
@SuppressWarnings("serial")
public class UserPublishAccount extends BaseIntModel{
	
	//用户Id
	private int uid;
	//公司名称
	private String companyName;
	//营业执照号
	private String business_license_number;
	//营业执照号所在地
	private String business_license_address;
	//联系地址
	private String address;
	//联系电话
	private String mobile;
	//营业执照号扫描副本
	private String business_license_pic;
	//法人名称
	private String legal_person;
	//法人证件号
	private String legal_person_certificate;
	//开户名
	private String account_name;
	//对公账号
	private String publish_account_number;
	//开户银行
	private String opening_bank;
	//所在城市
	private String city;
	//开户银行支行名称
	private String bank_branch_name;
	//创建时间
	private String createTime;
	//修改时间
	private String updateTime;
	//状态
	private int status;
	
	
	
	public int getUid() {
		return uid;
	}



	public void setUid(int uid) {
		this.uid = uid;
	}



	public String getCompanyName() {
		return companyName;
	}



	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}



	public String getBusiness_license_number() {
		return business_license_number;
	}



	public void setBusiness_license_number(String business_license_number) {
		this.business_license_number = business_license_number;
	}



	public String getBusiness_license_address() {
		return business_license_address;
	}



	public void setBusiness_license_address(String business_license_address) {
		this.business_license_address = business_license_address;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



	public String getBusiness_license_pic() {
		return business_license_pic;
	}



	public void setBusiness_license_pic(String business_license_pic) {
		this.business_license_pic = business_license_pic;
	}



	public String getLegal_person() {
		return legal_person;
	}



	public void setLegal_person(String legal_person) {
		this.legal_person = legal_person;
	}



	public String getLegal_person_certificate() {
		return legal_person_certificate;
	}



	public void setLegal_person_certificate(String legal_person_certificate) {
		this.legal_person_certificate = legal_person_certificate;
	}



	public String getAccount_name() {
		return account_name;
	}



	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}



	public String getPublish_account_number() {
		return publish_account_number;
	}



	public void setPublish_account_number(String publish_account_number) {
		this.publish_account_number = publish_account_number;
	}



	public String getOpening_bank() {
		return opening_bank;
	}



	public void setOpening_bank(String opening_bank) {
		this.opening_bank = opening_bank;
	}



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getBank_branch_name() {
		return bank_branch_name;
	}



	public void setBank_branch_name(String bank_branch_name) {
		this.bank_branch_name = bank_branch_name;
	}



	public String getCreateTime() {
		return createTime;
	}



	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}



	public String getUpdateTime() {
		return updateTime;
	}



	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}



	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}



	public UserPublishAccountDetailVTO toUserPulishAccountDetailVTO(){
		UserPublishAccountDetailVTO detail = new UserPublishAccountDetailVTO();
		detail.setUid(uid);
		detail.setCompanyName(companyName);
		detail.setBusiness_license_number(business_license_number);
		detail.setBusiness_license_address(business_license_address);
		detail.setAddress(address);
		detail.setMobile(mobile);
		detail.setBusiness_license_pic(business_license_pic);
		detail.setLegal_person(legal_person);
		detail.setLegal_person_certificate(legal_person_certificate);
		detail.setAccount_name(account_name);
		detail.setPublish_account_number(publish_account_number);
		detail.setOpening_bank(opening_bank);
		detail.setCity(city);
		detail.setBank_branch_name(bank_branch_name);
		detail.setCreateTime(createTime);
		detail.setUpdateTime(updateTime);
		detail.setStatus(status);
		return detail;
	}
}
