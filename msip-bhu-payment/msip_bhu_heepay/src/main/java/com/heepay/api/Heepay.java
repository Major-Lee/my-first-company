package com.heepay.api;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.heepay.commons.Config;
import com.heepay.commons.GatewayHelper;
import com.heepay.commons.HeepaySubmit;
import com.heepay.commons.Md5Tools;
import com.heepay.model.GatewayModel;
import com.heepay.submitreturn.SubmitReturn;

public class Heepay {
	private static String agent_id = Config.agent_id; // 商户编号 如1001 替换商户的商户号
	private static String version = "1"; // 当前接口版本号
	private static String pay_type = "30"; // 微信支付
	private static String pay_code = ""; // 空字符串
	private static String agent_bill_id;// 商户系统内部的定单号（要保证唯一）。长度最长50字符
	private static String pay_amt; // 订单总金额
									// 不可为空，取值范围（0.01到10000000.00），单位：元，小数点后保留两位。
	private static String notify_url = Config.notify_url; // 支付后返回的商户处理页面，URL参数是以http://或https://开头的完整URL地址(后台处理)
															// 提交的url地址必须外网能访问到,否则无法通知商户。值可以为空，但不可以为null。
	private static String return_url = Config.return_url; // 支付后返回的商户显示页面，URL参数是以http://
															// 或https://开头的完整URL地址(前台显示)，原则上：该参数与notify_url提交的参数不一致。值可以为空，但不可以为null。
	private static String user_ip;// "192_168_564_123";
									// //用户所在客户端的真实IP其中的“.”替换为“_” 。如
									// 127_127_12_12。因为近期我司发现用户在提交数据时，user_ip在网络层被篡改，导致签名错误，所以我们规定使用这种格式。
	private static String agent_bill_time; // 提交单据的时间yyyyMMddHHmmss
											// 如：20100225102000该参数共计14位，当时不满14位时，在后面加0补足14位
	private static String goods_name = "打赏认证"; // 商品名称, 长度最长50字符，不能为空（不参加签名）
	private static String goods_num = "1"; // 产品数量,长度最长20字符（不参加签名）
	private static String remark = ""; // 商户自定义 原样返回,长度最长50字符，可以为空。（不参加签名）
	// private static String is_test = "1";
	private static String is_phone = "1"; // 是否使用手机端微信支付，1=是，微信扫码支付不用传本参数
	private static String is_frame = "0"; // 1（默认值）=使用微信公众号支付，0=使用WAP微信支付
	private static String goods_note = "note"; // 支付说明, 长度50字符（不参加签名）

	public static String order(String billno, String price, String ip) {
		agent_bill_id = billno;
		pay_amt = price;
		user_ip = ip.replaceAll("\\.", "_");
		String url = submitOrder();
		return url;
	}

	public static String order(String billno, String price, String ip, String notifyUrl, String returnUrl) {
		agent_bill_id = billno;
		pay_amt = price;
		user_ip = ip.replaceAll("\\.", "_");
		notify_url = notifyUrl;
		return_url = returnUrl;

		String url = submitOrder();
		return url;
	}

	public static String query(String bill_no) {
		agent_bill_id = bill_no;
		String result = submitQuery();
		return result;
	}

	public static String sign(String jnet_bill_no, String agent_bill_no, String price) {
		String str = "";
		str += "result=1";
		str += "&agent_id=" + Config.agent_id;
		str += "&jnet_bill_no=" + jnet_bill_no;
		str += "&agent_bill_id=" + agent_bill_no;
		str += "&pay_type=30";
		str += "&pay_amt=" + price;
		str += "&remark=";
		str += "&key=" + Config.sign_key;
		System.out.println(str);
		String sign = Md5Tools.MD5(str).toLowerCase();
		return sign;
	}

	private static String submitOrder() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String time = df.format(new Date());
		agent_bill_time = time;

		GatewayModel gatewayModel = new GatewayModel();
		gatewayModel.setagent_id(agent_id);
		gatewayModel.setversion(version);
		gatewayModel.setpay_type(pay_type);
		gatewayModel.setpay_code(pay_code);
		gatewayModel.setagent_bill_id(agent_bill_id);
		gatewayModel.setpay_amt(pay_amt);
		gatewayModel.setnotify_url(notify_url);
		gatewayModel.setreturn_url(return_url);
		gatewayModel.setuser_ip(user_ip);
		gatewayModel.setagent_bill_time(agent_bill_time);
		gatewayModel.setgoods_name(goods_name);
		gatewayModel.setgoods_num(goods_num);
		gatewayModel.setremark(remark);
		gatewayModel.setgoods_note(goods_note);
		gatewayModel.setis_phone(is_phone);
		gatewayModel.setis_frame(is_frame);

		HeepaySubmit heepaySubmit = new HeepaySubmit();

		SubmitReturn submitReturn = null;

		try {
			submitReturn = heepaySubmit.SubmitUrl(gatewayModel);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} // 提交组织URL参数
		if (submitReturn.is_success()) {
			System.out.println(submitReturn.get_error_message());// 出现错误打印出错误信息
			return "error";
		} else {
			return submitReturn.get_error_message();
		}
	}

	private static String submitQuery() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String time = df.format(new Date());
		String agent_bill_time = time; // 必填 单据的时间yyyyMMddHHmmss 20091112102000
		GatewayModel gatewayModel = new GatewayModel();
		gatewayModel.setagent_id(agent_id);
		gatewayModel.setversion(version);
		gatewayModel.setagent_bill_id(agent_bill_id);
		gatewayModel.setagent_bill_time(agent_bill_time);
		gatewayModel.setremark(remark);

		HeepaySubmit heepaySubmit = new HeepaySubmit();
		SubmitReturn submitReturn = null;

		try {
			submitReturn = heepaySubmit.SubmitQueryString(gatewayModel);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} // 提交组织url参数
		if (submitReturn.is_success()) {
			return submitReturn.get_error_message();// 出现错误打印出错误信息

		} else {
			// 验证签名结果
			System.out.println("验证签名结果：" + GatewayHelper.VerifiSign(submitReturn.get_error_message()));
			return submitReturn.get_error_message();
		}
	}
}
