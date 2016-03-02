package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public class BusinessEnumType {
	/**
	 * sns 类别定义
	 * @author lawliet
	 *
	 */
	public enum OAuthType{
		Local("本地", "local"),
		Weichat("腾讯微信", "weichat"),
		QQ("腾讯QQ", "qq"),
		Weibo("新浪微博", "weibo"),
		;
		private String name;
		private String type;
		static Map<String, OAuthType> allOAuthTypeTypes;
		
		private OAuthType(String name, String type){
			this.name = name;
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public static OAuthType fromType(String type){
			return allOAuthTypeTypes.get(type);
		}
		
		public static boolean supported(String type){
			return allOAuthTypeTypes.containsKey(type);
		}
		
		static {
			allOAuthTypeTypes = new HashMap<String, OAuthType>();
			OAuthType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OAuthType type : types){
				allOAuthTypeTypes.put(type.getType(), type);
			}
		}
	}
	
	public enum UserSex{
		Male("男"),
		Female("女"),
		Neutral("中性"),
		;
		static Map<String, UserSex> allUserSexs;
		private String name;
		private UserSex(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public static UserSex fromType(String name){
			UserSex userSex = allUserSexs.get(name); 
			if(userSex == null){
				userSex = Female;
			}
			return userSex;
		}
		
		public static boolean isMale(String name){
			UserSex userSex = fromType(name);
			if(userSex == Male){
				return true;
			}
			return false;
		}
		
		static {
			allUserSexs = new HashMap<String, UserSex>();
			UserSex[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (UserSex type : types){
				allUserSexs.put(type.getName(), type);
			}
		}
	}
	
	//充值购买虎钻 充值零钱 系统/活动赠送虎钻 消费虎钻 提现(withdraw)
	public enum UWithdrawStatus{
		Apply("AP","提现申请"),
		VerifySucceed("VS","提现申请审核通过"),
		VerifyFailed("VF","提现申请审核失败"),
		WithdrawDoing("WD","uPay正在提现处理中"),
		WithdrawSucceed("WS","uPay提现成功"),
		WithdrawFailed("WF","uPay提现失败"),
		;
		private String key;
		private String name;
		static Map<String, UWithdrawStatus> allWithdrawStatusTypes;
		
		private UWithdrawStatus(String key,String name){
			this.key = key;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public static UWithdrawStatus fromKey(String key){
			return allWithdrawStatusTypes.get(key);
		}
		
		public static boolean supported(String key){
			return allWithdrawStatusTypes.containsKey(key);
		}
		
		static {
			allWithdrawStatusTypes = new HashMap<String, UWithdrawStatus>();
			UWithdrawStatus[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (UWithdrawStatus type : types){
				allWithdrawStatusTypes.put(type.getKey(), type);
			}
		}
	}
	
	//充值购买虎钻 充值零钱 系统/活动赠送虎钻 消费虎钻 提现(withdraw)
	public enum UWalletTransType{
		Recharge2V("R2V","充值购买虎钻"),
		Recharge2C("R2C","充值现金"),
		Sharedeal2C("S2C","分成现金"),
		Giveaway2V("G2V","系统/活动赠送虎钻"),
		Consume4V("C4V","消费虎钻"),
		Withdraw("WDR","提现(withdraw)"),
		;
		private String key;
		private String name;
		static Map<String, UWalletTransType> allWalletTransTypes;
		
		private UWalletTransType(String key,String name){
			this.key = key;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public static UWalletTransType fromKey(String key){
			return allWalletTransTypes.get(key);
		}
		
		public static boolean supported(String key){
			return allWalletTransTypes.containsKey(key);
		}
		
		static {
			allWalletTransTypes = new HashMap<String, UWalletTransType>();
			UWalletTransType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (UWalletTransType type : types){
				allWalletTransTypes.put(type.getKey(), type);
			}
		}
	}
}
