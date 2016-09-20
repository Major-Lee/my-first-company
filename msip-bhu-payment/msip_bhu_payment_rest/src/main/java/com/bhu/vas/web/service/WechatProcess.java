package com.bhu.vas.web.service;


import com.bhu.vas.web.model.FormatXmlProcess;
import com.bhu.vas.web.model.ReceiveXmlEntity;
import com.bhu.vas.web.model.ReceiveXmlProcess;



/**
 * 微信xml消息处理流程逻辑类
 * @author pamchen-1
 *
 */
public class WechatProcess {
	/**
	 * 解析处理xml、获取智能回复结果（通过图灵机器人api接口）
	 * @param xml 接收到的微信数据
	 * @return	最终的解析结果（xml格式数据）
	 */
	public String processWechatMag(String xml){
		/** 解析xml数据 */
		ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);
		
		/** 以文本消息为例，调用图灵机器人api接口，获取回复内容 */
		String result = "";
		if("text".endsWith(xmlEntity.getMsgType())){
			String userMsg = xmlEntity.getContent();
			if(userMsg.contains("提现")){
				result = "提现绑定<a href=\"http://upays.bhuwifi.com/msip_bhu_payment_rest/pay/submitOrder\">【点击这里】</a>！";
			}else{
				//result = new TulingApiProcess().getTulingResult(xmlEntity.getContent());
				result = "Hello";
			}
		}
		
		/** 此时，如果用户输入的是“你好”，在经过上面的过程之后，result为“你也好”类似的内容 
		 *  因为最终回复给微信的也是xml格式的数据，所有需要将其封装为文本类型返回消息
		 * */
		result = new FormatXmlProcess().formatXmlAnswer(xmlEntity.getFromUserName(), xmlEntity.getToUserName(), result);
		
		return result;
	}
}
