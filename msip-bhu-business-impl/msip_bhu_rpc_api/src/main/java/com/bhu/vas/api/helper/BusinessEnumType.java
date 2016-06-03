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
		Weichat("腾讯微信", "weixin",true,"提现至微信"),
		Alipay("支付宝", "alipay",true,"提现至支付宝"),
		QQ("腾讯QQ", "qq"),
		Weibo("新浪微博", "weibo"),
		;
		private String name;
		private String type;
		private boolean payment;
		private String description;
		static Map<String, OAuthType> allOAuthTypes;
		static Map<String, OAuthType> allOAuthPaymentTypes;
		private OAuthType(String name, String type){
			this.name = name;
			this.type = type;
		}
		private OAuthType(String name, String type,boolean payment,String description){
			this.name = name;
			this.type = type;
			this.payment = payment;
			this.description = description;
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
		
		public boolean isPayment() {
			return payment;
		}
		public void setPayment(boolean payment) {
			this.payment = payment;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public static OAuthType fromType(String type){
			return allOAuthTypes.get(type);
		}
		
		public static boolean supported(String type){
			return allOAuthTypes.containsKey(type);
		}
		
		public static boolean paymentSupported(String type){
			return allOAuthPaymentTypes.containsKey(type);
		}
		
		static {
			allOAuthTypes = new HashMap<String, OAuthType>();
			allOAuthPaymentTypes = new HashMap<String, OAuthType>();
			OAuthType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OAuthType type : types){
				allOAuthTypes.put(type.getType(), type);
				if(type.isPayment()){
					allOAuthPaymentTypes.put(type.getType(), type);
				}
			}
		}
	}
	
/*	public enum ThirdpartiesPaymentType{
		Weichat("腾讯微信", "weixin","提现至微信"),
		Alipay("支付宝", "alipay","提现至支付宝"),
		;
		private String name;
		private String type;
		private String description;
		static Map<String, ThirdpartiesPaymentType> allPaymentTypes;
		
		private ThirdpartiesPaymentType(String name, String type,String description){
			this.name = name;
			this.type = type;
			this.description = description;
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
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public static ThirdpartiesPaymentType fromType(String type){
			return allPaymentTypes.get(type);
		}
		
		public static boolean supported(String type){
			return allPaymentTypes.containsKey(type);
		}
		
		static {
			allPaymentTypes = new HashMap<String, ThirdpartiesPaymentType>();
			ThirdpartiesPaymentType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (ThirdpartiesPaymentType type : types){
				allPaymentTypes.put(type.getType(), type);
			}
		}
	}*/
	
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
		//Init("IN","提现申请（初始）"),
		Apply("AP","提现申请"),
		VerifyPassed("VP","提现申请审核通过"),
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
	
	/**
	 * 对于虎钻和钱包里的现金进行消费或者充值的类型定义
	 * 只定义内部虚拟货币之间的结转方式
	 * @author Edmond
	 * Recharge 充值
	 * Realmoney Readymoney现金（真实的）
	 * Cash 零钱（系统内定义的，可以当现金使用）
	 * VCurrency 虎钻
	 */
	public enum UWalletTransType{
		Recharge2V("R2V","虎钻充值"),
		Recharge2C("R2C","零钱充值"),
		Rollback2C("B2C","零钱返还"),
		ReadPacketSettle2C("P2C",	 "红包打赏"),
		Authenticate2Snk("A2S",	 "短信/微信认证网络"),
		PurchaseGoodsUsedV("PGV","虚拟币购买道具"),
		PurchaseGoodsUsedC("PGC","零钱购买道具"),
		Cash2Realmoney("C2M","零钱提现"),
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
	
	//充值购买虎钻 充值零钱 系统/活动赠送虎钻 消费虎钻 提现(withdraw)
	/**
	 * 交易类型定义
	 * 和第三方支付相关的操作
	 * 支付、充值、提现
	 * @author Edmond
	 *
	 */
	public enum UWalletTransMode{
		RealMoneyPayment("RMP","现金支付"),//用于充值
		DrawPresent("DWP","抽奖馈赠"),//用于抽奖
		CashPayment("CAP","零钱支付"),
		CashRollbackPayment("CRP","零钱支付（Rollback）"),
		VCurrencyPayment("VCP","虎钻支付"),
		SharedealPayment("SDP","收益分成"),
		/*Recharge2V("R2V","充值购买虎钻"),
		Recharge2C("R2C","充值现金"),
		Cash2C("C2C","现金购买虎钻"),
		Sharedeal2C("S2C","分成现金"),
		Giveaway2V("G2V","系统/活动赠送虎钻"),
		Consume4V("C4V","消费虎钻"),
		Withdraw("WDA","提现(withdraw)"),
		WithdrawRollback("WDR","提现(withdraw rollback)"),*/
		;
		private String key;
		private String name;
		static Map<String, UWalletTransMode> allWalletTransModes;
		
		private UWalletTransMode(String key,String name){
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
		public static UWalletTransMode fromKey(String key){
			return allWalletTransModes.get(key);
		}
		
		public static boolean supported(String key){
			return allWalletTransModes.containsKey(key);
		}
		
		static {
			allWalletTransModes = new HashMap<String, UWalletTransMode>();
			UWalletTransMode[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (UWalletTransMode type : types){
				allWalletTransModes.put(type.getKey(), type);
			}
		}
	}
	
	//订单基本状态
	public enum OrderStatus{
		//Pending(0,"订单原始状态","生成订单最原始的状态"),
		NotPay(1,"未支付状态","其他应用获取此订单的支付url时更新为此状态"),
		PayFailured(8,"订单支付失败状态","支付平台支付完成并通知此订单支付失败时更新为此状态"),
		PaySuccessed(9,"订单支付成功状态","支付平台支付完成并通知此订单支付成功时更新为此状态"),
		DeliverCompleted(10,"发货完成状态","系统通知应用发货成功时更新为此状态"),
		;
		private Integer key;
		private String name;
		private String desc;
		
		static Map<Integer, OrderStatus> allOrderStatusTypes;
		
		private OrderStatus(Integer key,String name,String desc){
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
		
		public static OrderStatus fromKey(Integer key){
			if(key == null) return null;
			return allOrderStatusTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allOrderStatusTypes.containsKey(key);
		}
		
		public static boolean isPaySuccessed(Integer key){
			if(key == null) return false;
			if(OrderStatus.PaySuccessed.getKey().equals(key)) return true;
			return false;
		}
		
		public static boolean isDeliverCompleted(Integer key){
			if(key == null) return false;
			if(OrderStatus.DeliverCompleted.getKey().equals(key)) return true;
			return false;
		}
		
		public static boolean isNotPay(Integer key){
			if(key == null) return false;
			if(OrderStatus.NotPay.getKey().equals(key)) return true;
			return false;
		}
		
		static {
			allOrderStatusTypes = new HashMap<Integer, OrderStatus>();
			OrderStatus[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OrderStatus type : types){
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
	public enum OrderProcessStatus{
		//Pending(0,"待处理状态","生成订单最原始的状态"),
		NotPay(1,"未支付状态","其他应用获取此订单的支付url时更新为此状态"),
		//Paying(2,"支付中状态","从支付平台获取到此订单的支付url并且通知到用户客户端时更新为此状态"),
		//PayCompleted(3,"支付完成状态","支付平台支付完成并通知此订单支付完成, 无论支付成功还是失败都更新为此状态"),
		PayFailured(8,"支付失败状态","支付平台支付完成并通知此订单支付失败时更新为此状态"),
		PaySuccessed(9,"支付成功状态","支付平台支付完成并通知此订单支付成功时更新为此状态"),
		//DeliverPrepared(6,"准备发货状态","系统通知应用发货失败时更新为此状态"),
		//Delivering(9,"发货中状态",""),
		DeliverCompleted(10,"发货完成状态","系统通知应用发货成功时更新为此状态"),
		SharedealCompleted(100,"分成完成状态","系统分成完成时更新为此状态"),
		;
		private Integer key;
		private String name;
		private String desc;
		
		static Map<Integer, OrderProcessStatus> allOrderProcessStatusTypes;
		
		private OrderProcessStatus(Integer key,String name,String desc){
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
		
		public static OrderProcessStatus fromKey(Integer key){
			if(key == null) return null;
			return allOrderProcessStatusTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allOrderProcessStatusTypes.containsKey(key);
		}
		
		static {
			allOrderProcessStatusTypes = new HashMap<Integer, OrderProcessStatus>();
			OrderProcessStatus[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OrderProcessStatus type : types){
				allOrderProcessStatusTypes.put(type.getKey(), type);
			}
		}
	}
	
	/**
	 * 商品分类
	 * @author tangzichao
	 */
	public enum CommdityCategory{
		InternetLimit(99,"限时上网分类","限时上网分类"),
		;
		private Integer category;
		private String name;
		private String desc;
		
		static Map<Integer, CommdityCategory> allCommdityCategoryTypes;
		
		private CommdityCategory(Integer category,String name,String desc){
			this.category = category;
			this.name = name;
			this.desc = desc;
		}

		public Integer getCategory() {
			return category;
		}
		public void setCategory(Integer category) {
			this.category = category;
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
		
		public static CommdityCategory fromKey(Integer category){
			if(category == null) return null;
			return allCommdityCategoryTypes.get(category);
		}
		
		public static boolean supported(Integer category){
			return allCommdityCategoryTypes.containsKey(category);
		}
		
		public static boolean isInternetLimit(Integer category){
			if(category == null) return false;
			if(InternetLimit.getCategory().equals(category)) return true;
			return false;
		}
		
		static {
			allCommdityCategoryTypes = new HashMap<Integer, CommdityCategory>();
			CommdityCategory[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (CommdityCategory type : types){
				allCommdityCategoryTypes.put(type.getCategory(), type);
			}
		}
	}
	
	/**
	 * 商品状态
	 * @author tangzichao
	 */
	public enum CommdityStatus{
		OffSale(0,"下架状态","下架状态"),
		OnSale(1,"正常售卖状态","正常售卖状态"),
		;
		private Integer key;
		private String name;
		private String desc;
		
		static Map<Integer, CommdityStatus> allCommdityStatusTypes;
		
		private CommdityStatus(Integer key,String name,String desc){
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
		
		public static CommdityStatus fromKey(Integer key){
			if(key == null) return null;
			return allCommdityStatusTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allCommdityStatusTypes.containsKey(key);
		}
		
		public static boolean onsale(Integer key){
			if(key == null) return false;
			if(OnSale.getKey().equals(key)) return true;
			return false;
		}
		
		static {
			allCommdityStatusTypes = new HashMap<Integer, CommdityStatus>();
			CommdityStatus[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (CommdityStatus type : types){
				allCommdityStatusTypes.put(type.getKey(), type);
			}
		}
	}
	
	/**
	 * 应用定义
	 * @author tangzichao
	 */
	public enum CommdityApplication{
		Portal(1001,"3BD80FEBC9CC48E99EA2ABBE214E5957","portal id","uportal id"),
		Default(1000,"1F915A8DA370422582CBAC1DB6A806DD","默认应用id","默认应用id"),
		;
		private Integer key;
		private String secret;
		private String name;
		private String desc;
		
		static Map<Integer, CommdityApplication> allCommdityApplicationTypes;
		
		private CommdityApplication(Integer key, String secret, String name,String desc){
			this.key = key;
			this.secret = secret;
			this.name = name;
			this.desc = desc;
		}

		public Integer getKey() {
			return key;
		}
		public void setKey(Integer key) {
			this.key = key;
		}
		public String getSecret() {
			return secret;
		}
		public void setSecret(String secret) {
			this.secret = secret;
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
		
		public static CommdityApplication fromKey(Integer key){
			if(key == null) return null;
			return allCommdityApplicationTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allCommdityApplicationTypes.containsKey(key);
		}
		
		public static boolean verifyed(Integer key, String secret){
			CommdityApplication app = allCommdityApplicationTypes.get(key);
			if(app == null) return false;
			if(app.getSecret().equals(secret)) return true;
			return false;
		}
		
		static {
			allCommdityApplicationTypes = new HashMap<Integer, CommdityApplication>();
			CommdityApplication[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (CommdityApplication type : types){
				allCommdityApplicationTypes.put(type.getKey(), type);
			}
		}
	}
	
	/**
	 * 生成订单id的扩展的第8位
	 * 订单支付模式
	 * @author tangzichao
	 */
	public enum OrderExtSegmentPayMode{
		Receipt(0,"收入","支付模式为进账"),
		Expend(1,"支出","支付模式为出账"),
		;
		private Integer key;
		private String name;
		private String desc;
		
		static Map<Integer, OrderExtSegmentPayMode> allExtSegmentPayModeTypes;
		
		private OrderExtSegmentPayMode(Integer key,String name,String desc){
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
		
		public static OrderExtSegmentPayMode fromKey(Integer key){
			if(key == null) return null;
			return allExtSegmentPayModeTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allExtSegmentPayModeTypes.containsKey(key);
		}
		
		public static boolean isReceipt(Integer key){
			if(key == null) return false;
			if(Receipt.getKey().equals(key)) return true;
			return false;
		}
		
		public static boolean isExpend(Integer key){
			if(key == null) return false;
			if(Expend.getKey().equals(key)) return true;
			return false;
		}
		
		static {
			allExtSegmentPayModeTypes = new HashMap<Integer, OrderExtSegmentPayMode>();
			OrderExtSegmentPayMode[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OrderExtSegmentPayMode type : types){
				allExtSegmentPayModeTypes.put(type.getKey(), type);
			}
		}
	}
	
	public static String unknownPaymentType = "未知支付";
	public static String templateRedpacketPaymentDesc = "通过%s%s打赏";
	/**
	 * 订单的用户持有的设备类型
	 * @author tangzichao
	 *
	 */
	public enum OrderUmacType{
		Pc(1,"Pc类型","PC"),
		Terminal(2,"终端类型","手机"),
		Unknown(10,"未知终端类型", "未知终端类型"),
		;
		private Integer key;
		private String name;
		private String desc;
		
		
		static Map<Integer, OrderUmacType> allOrderUmacTypes;
		
		private OrderUmacType(Integer key,String name,String desc){
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
		
		public static OrderUmacType fromKey(Integer key){
			if(key == null) return null;
			return allOrderUmacTypes.get(key);
		}
		
		public static boolean supported(Integer key){
			return allOrderUmacTypes.containsKey(key);
		}
		
		static {
			allOrderUmacTypes = new HashMap<Integer, OrderUmacType>();
			OrderUmacType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OrderUmacType type : types){
				allOrderUmacTypes.put(type.getKey(), type);
			}
		}
	}
	
	/**
	 * 支付方式对应
	 * @author tangzichao
	 *
	 */
	public enum OrderPaymentType{
		Weixin("Weixin","微信","微信"),
		Alipay("Alipay","支付宝","支付宝"),
		PcWeixin("PcWeixin","微信","微信"),
		PcAlipay("PcAlipay","支付宝","支付宝"),
		WapWeixin("WapWeixin","微信","微信"),
		WapAlipay("WapAlipay","支付宝","支付宝"),
		Midas("Midas","米大师","米大师"),
		Hee("Hee","汇元宝","汇元宝"),
		Unknown("unknown","未知支付方式", "未知支付方式"),
		;
		private String key;
		private String name;
		private String desc;
		
		
		static Map<String, OrderPaymentType> allOrderPaymentTypes;
		
		private OrderPaymentType(String key,String name,String desc){
			this.key = key;
			this.name = name;
			this.desc = desc;
		}

		public String getKey() {
			return key;
		}
		public void setKey(String key) {
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
		
		public static OrderPaymentType fromKey(String key){
			if(key == null) return null;
			return allOrderPaymentTypes.get(key);
		}
		
		public static boolean supported(String key){
			return allOrderPaymentTypes.containsKey(key);
		}
		
		static {
			allOrderPaymentTypes = new HashMap<String, OrderPaymentType>();
			OrderPaymentType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OrderPaymentType type : types){
				allOrderPaymentTypes.put(type.getKey(), type);
			}
		}
	}
	
	public static void main(String [] args){
		OrderPaymentType payment_type = OrderPaymentType.fromKey("Alipay");
		System.out.println(payment_type.getDesc());
	}
}
