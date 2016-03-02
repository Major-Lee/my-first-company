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
	public enum UWalletTransType{
		Recharge2V("R2V","充值购买虎钻"),
		Recharge2C("R2C","充值现金"),
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
	
	//订单基本状态
	public enum OrderStatusType{
		Original(0,"订单原始状态","生成订单最原始的状态"),
		NotPay(1,"未支付状态","其他应用获取此订单的支付url时更新为此状态"),
		PaySuccessed(9,"订单支付成功状态","支付平台支付完成并通知此订单支付成功时更新为此状态"),
		PayFailured(10,"订单支付失败状态","支付平台支付完成并通知此订单支付失败时更新为此状态"),
		;
		private Integer key;
		private String name;
		private String desc;
		
		static Map<Integer, OrderStatusType> allOrderStatusTypes;
		
		private OrderStatusType(Integer key,String name,String desc){
			this.key = key;
			this.name = name;
			this.desc = desc;
		}

		public Integer getKey() {
			return key;
		}
		public void setKey(Integer key) {
			this.key = key;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		
		public static OrderStatusType fromKey(Integer key){
			if(key == null) return null;
			return allOrderStatusTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allOrderStatusTypes.containsKey(key);
		}
		
		static {
			allOrderStatusTypes = new HashMap<Integer, OrderStatusType>();
			OrderStatusType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OrderStatusType type : types){
				allOrderStatusTypes.put(type.getKey(), type);
			}
		}
	}
	
	/**
	 * 订单流程状态
	 * 用于详细标识内部通信步骤的流程状态
	 * 1）订单出现问题时，定位内部流程到达节点
	 * 2）实现重要流程通讯重试，系统通过判断状态为支付成功状态，则重试进行通知应用发货
	 * @author tangzichao
	 */
	public enum OrderProcessStatusType{
		Pending(0,"待处理状态","生成订单最原始的状态"),
		NotPay(1,"未支付状态","其他应用获取此订单的支付url时更新为此状态"),
		Paying(2,"支付中状态","从支付平台获取到此订单的支付url并且通知到用户客户端时更新为此状态"),
		//PayCompleted(3,"支付完成状态","支付平台支付完成并通知此订单支付完成, 无论支付成功还是失败都更新为此状态"),
		PaySuccessed(4,"支付成功状态","支付平台支付完成并通知此订单支付成功时更新为此状态"),
		PayFailured(5,"支付失败状态","支付平台支付完成并通知此订单支付失败时更新为此状态"),
		//Delivering(9,"发货中状态",""),
		DeliverCompleted(10,"发货完成状态","系统通知应用发货成功时更新为此状态"),
		;
		private Integer key;
		private String name;
		private String desc;
		
		static Map<Integer, OrderProcessStatusType> allOrderProcessStatusTypes;
		
		private OrderProcessStatusType(Integer key,String name,String desc){
			this.key = key;
			this.name = name;
			this.desc = desc;
		}

		public Integer getKey() {
			return key;
		}
		public void setKey(Integer key) {
			this.key = key;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		
		public static OrderProcessStatusType fromKey(Integer key){
			if(key == null) return null;
			return allOrderProcessStatusTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allOrderProcessStatusTypes.containsKey(key);
		}
		
		static {
			allOrderProcessStatusTypes = new HashMap<Integer, OrderProcessStatusType>();
			OrderProcessStatusType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OrderProcessStatusType type : types){
				allOrderProcessStatusTypes.put(type.getKey(), type);
			}
		}
	}
}
