package com.bhu.vas.web.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bhu.vas.business.helper.JsonUtil;
import com.bhu.vas.web.http.GetRequestUtil;
import com.bhu.vas.web.http.PostRequestUtil;
import com.bhu.vas.web.service.PayHttpService;
import com.bhu.vas.web.service.WechatProcess;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年5月25日 上午10:26:53
 */
@Controller
public class BindWeixinController extends BaseController {
	
	@Autowired
	PayHttpService payHttpService;

	/**
	    * 接收微信服务器推送的用户消息接口
	    * @param request
	    * @param response
	    * @throws IOException
	    */
		@RequestMapping(value = "/pay/wxNotifyUserInfo")
	    public void wxPayNotifySuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
	        System.out.println(String.format("******[%s]********[%s]*******[%s]********","微信通知",System.currentTimeMillis(),"Starting"));
	        request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");

			/** 读取接收到的xml消息 */
			StringBuffer sb = new StringBuffer();
			InputStream is = request.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			String xml = sb.toString();	//次即为接收到微信端发送过来的xml数据
			//String xml = "<xml><ToUserName><![CDATA[gh_9a839b3089cd]]></ToUserName><FromUserName><![CDATA[ohdupuGI3vkfnQSMFsPMlnQPdQvg]]></FromUserName><CreateTime>1467866932</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[哈哈哈提现]]></Content><MsgId>6304440468277117909</MsgId></xml>";

			System.out.println(xml);
			String result = "";
			/** 判断是否是微信接入激活验证，只有首次接入验证时才会收到echostr参数，此时需要把它直接返回 */
//			String echostr = request.getParameter("echostr");
//			if (echostr != null && echostr.length() > 1) {
//				result = echostr;
//			} else {
//				//正常的微信处理流程
//				result = new WechatProcess().processWechatMag(xml);
//				System.out.println(result);
//			}
			result = new WechatProcess().processWechatMag(xml);
			System.out.println(result);
			OutputStream os = null;
			try {
				 os = response.getOutputStream();
				os.write(result.getBytes("UTF-8"));
				os.flush();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				os.close();
			}
	    }
	    @RequestMapping(value = "/pay/submitOrder")
	    public ModelAndView index(ModelAndView mv, HttpServletRequest request) throws UnsupportedEncodingException {
	        mv.addObject("appid", payHttpService.getAppId());

	        String basePath = request.getScheme() + "://" + request.getServerName()
	                + "/";
	        System.out.println("********basePath:"+basePath);
	        //String redirect_uri = basePath + "pay/conformOrder";
	        String redirect_uri = "http://upays.bhuwifi.com/weixin-test/pay/conformOrder";
	        System.out.println("********redirect_uri:"+redirect_uri);
	        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ payHttpService.getAppId();
	        url += "&redirect_uri=" + URLEncoder.encode(redirect_uri, "UTF-8");
	        url += "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
	        System.out.println("请求用户授权url:"+url);
	        mv.addObject("redirect_uri", url);
	        mv.setViewName("/pay/conformOrder");
	        return mv;
	    }
	    
	    @RequestMapping(value = "/pay/conformOrder")
	    public String conformOrder( HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
	    	System.out.println("/pay/conformOrder======starting");
	    	String code = request.getParameter("code");
	        String state = request.getParameter("state");
	        System.out.println("code-======"+code+"===========state======"+state);
	        String reqAccessTokenUrl = "appid="+ payHttpService.getAppId()+"&secret=" +payHttpService.getAppSecret()+"&code=" +code+"&grant_type=authorization_code";
	        String accessTokenJsonStr = GetRequestUtil.sendGet("https://api.weixin.qq.com/sns/oauth2/access_token", reqAccessTokenUrl);
	        Map accessTokenMap = JsonUtil.fromJson(accessTokenJsonStr);
	        //获取access_token
	        String access_token = (String)accessTokenMap.get("access_token");
	        String openid = (String)accessTokenMap.get("openid");
	        String unionid = (String)accessTokenMap.get("unionid");
	        System.out.println("OPENID:"+openid+"access_token"+access_token);
	        //获取用户基本信息
	        
			String notifyUrlParam = "sk=PzdfTFJSUEBHG0dcWFcLew==&identify=weixin&auid="+unionid+"&openid="+openid;
			String notifyJsonStr = PostRequestUtil.sendPost("http://111.207.194.20/bhu_api/v1/dashboard/oauth/fullfill", notifyUrlParam);
		    Map notifyMap = JsonUtil.fromJson(notifyJsonStr);
		    System.out.println(notifyMap);
		    boolean success = (boolean)notifyMap.get("success");
		    if(success){
		    	return "success";
		    }else{
		    	
		    	String codemsg = (String)notifyMap.get("codemsg");
		    	System.out.println(success+codemsg);
		    	return "fail";
		    }
		   
	    }
	
}