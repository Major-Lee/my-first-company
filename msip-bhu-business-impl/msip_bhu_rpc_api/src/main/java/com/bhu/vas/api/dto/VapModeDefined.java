package com.bhu.vas.api.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.StringHelper;

public class VapModeDefined {
	
	private static final String Vap_Supported_Work_Mode1 = "router-ap";
	private static final String Vap_Supported_Work_Mode2 = "wwan_router-ap";
	private static final String Vap_Supported_Work_Mode3 = "bridge-ap";
	
	public static boolean supported(String device_workmode){
		return Vap_Supported_Work_Mode1.equals(device_workmode) 
				|| Vap_Supported_Work_Mode2.equals(device_workmode) || Vap_Supported_Work_Mode3.equals(device_workmode);
	}
	
	//<img src="http://192.168.66.7/vap/ad/001/js/../images/hot_1.png" alt="Hot">
	//private final static String url_prefix = "http://vap.bhunetworks.com/vap/";
	//private final static String url_prefix = "http://192.168.66.7/vap/";
	//private final static String url_res_prefix = RuntimeConfiguration.Vap_Http_Res_UrlPrefix;
	//private final static String url_api_prefix = RuntimeConfiguration.Vap_Http_Api_UrlPrefix;
	public enum VapMode {
		HtmlInjectAdv("htmlinjectadv"),
		HtmlInject404("htmlinject404"),
		HtmlPortal("htmlportal"),
		HtmlRedirect("htmlredirect");

		private String key;
		VapMode(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}


	public enum HtmlInjectAdv{
		STYLE000("style000","00.00.01","1000000","http://auth.wi2o.cn/ad/ad.js"),
		STYLE001("style001","00.00.01","1000001",RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("ad/001/js/ad.js")),
		STYLE002("style002","00.00.01","1000002",RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("ad/002/js/ad.js")),
		STYLE003("style003","00.00.01","1000003",RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("ad/003/js/ad.js")),
		;
		//private String index;
		private String style;
		private String ver;
		//bhu_id 应该是广告厂商的id
		private String bid;
		private String url;
		
		static Map<String, HtmlInjectAdv> allInjectAdvTypes;
		
		HtmlInjectAdv(/*String index,*/String style,String ver,String bid,String url){
			//this.index = index;
			this.style = style;
			this.ver = ver;
			this.bid = bid;
			this.url = url;
		}
		/*public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}*/
		public String getStyle() {
			return style;
		}
		public void setStyle(String style) {
			this.style = style;
		}
		public String getVer() {
			return ver;
		}
		public void setVer(String ver) {
			this.ver = ver;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		
		public String getBid() {
			return bid;
		}
		public void setBid(String bid) {
			this.bid = bid;
		}
		public String toIndentify(){
			StringBuilder sb = new StringBuilder();
			sb/*.append(index)
				.append(StringHelper.MINUS_CHAR_GAP)*/.append(style)
				.append(StringHelper.MINUS_CHAR_GAP).append(ver);
			return sb.toString();
		}
		
		/**
		 * 当前的设备是否需要升级
		 * @param indentify
		 * @return
		 */
		public boolean needUpdate(String indentify){
			if(StringUtils.isEmpty(indentify)) return true;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 2) return true;
			HtmlInjectAdv p = getByStyle(array[0]);
			if(p == null) return true;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ) return true;
			return false;
		}
		
		/**
		 * 获取新的版本
		 * @param indentify
		 * @param invalidatedThenDefault indentify验证错误后返回的缺省portal值
		 * @return 为null则代表目前是最新版本
		 */
		public HtmlInjectAdv getNewVerVap(String indentify,HtmlInjectAdv invalidatedThenDefault){
			if(StringUtils.isEmpty(indentify)) return invalidatedThenDefault;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 2) return invalidatedThenDefault;
			HtmlInjectAdv p = getByStyle(array[0]);
			if(p == null) return invalidatedThenDefault;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ){
				return p;
			}
			return null;
		}
		
		
		static {
			allInjectAdvTypes = new HashMap<String,HtmlInjectAdv>();
			HtmlInjectAdv[] types = values();
			for (HtmlInjectAdv type : types)
				allInjectAdvTypes.put(type.style, type);
		}
		
		public static HtmlInjectAdv getByStyle(String style) {
			HtmlInjectAdv adv = allInjectAdvTypes.get(style);
			if(adv != null)
				return adv;
			else
				return HtmlInjectAdv.STYLE000;
		}
	}
	
	
	
	
	/**
	 * HtmlInject404 定义
	 * 编号 样式 版本号 资源包url
	 * 
	 * 编号 样式 版本号 组合串为资源包识别号
	 * 
	 * 版本号 修改后为递增修改
	 * @author Edmond
	 *
	 *进入目录style001 执行tar -czf style001.tar.gz *
	 *tar -xvf style001.tar.gz
	 *
	 *http://192.168.66.7/vap/404/style001/index.html
	 *
	 *404页面 内容系统实现思路
	 *A、实现规则
	 *	1、所有访问的404页面都是静态的，事先生成好的
	 *  2、提供n中页面模板，对于每个客户可以同时选择多个模板，根据每个客户、每个模板生成独立的静态内容，每个客户的内容可能存在多个不同的静态内容
	 *  3、静态内容的生成频次默认为30分钟重新生成，时间可以配置，也可以手动触发马上生成
	 *B、生成规则
	 *C、内容规则
	 *	1、每套模板都存在静态的tpl文件，模板中的内容版块可以事先定义，不通的模板的内容版块数量可以不一致
	 */
	public enum HtmlInject404{
		STYLE000("style000","00.00.01","404,500",
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("rw404?bid=10001")),
		STYLE001("style001","00.00.03","40*,50*,10*",
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("rw404?bid=10002")),
				
		/*STYLE002("style002","00.00.03","40*,50*,10*",
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("rw404?bid=10002")),
		STYLE003("style003","00.00.03","40*,50*,10*",
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("rw404?bid=10002")),*/
				
				//RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("404/style001/index.html?bid=10002")),
/*		STYLE000("style000","00.00.01",
				RuntimeConfiguration.Vap_Http_Api_UrlPrefix.concat("v1/noauth/vap/url404"),
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("404/rawfiles/style000.tar.gz")),
		STYLE001("style001","00.00.03",
				RuntimeConfiguration.Vap_Http_Api_UrlPrefix.concat("v1/noauth/vap/url404"),
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("404/rawfiles/style001.tar.gz")),*/
		;
		private String style;
		private String ver;
		private String codes;//在什么codes的情况下进行HtmlInject404，例如404，403，40*，50*等
		private String dynaurl;
		//private String packurl;
		static Map<String, HtmlInject404> allInject404Types;
		HtmlInject404(String style,String ver,String codes,String dynaurl/*,String packurl*/){
			this.style = style;
			this.ver = ver;
			this.codes = codes;
			this.dynaurl = dynaurl;
		}
		public String getStyle() {
			return style;
		}
		public void setStyle(String style) {
			this.style = style;
		}
		public String getVer() {
			return ver;
		}
		public void setVer(String ver) {
			this.ver = ver;
		}
		
		
		/*public String getPackurl() {
			return packurl;
		}
		public void setPackurl(String packurl) {
			this.packurl = packurl;
		}*/
		
		public String getCodes() {
			return codes;
		}
		public void setCodes(String codes) {
			this.codes = codes;
		}
		public String getDynaurl() {
			return dynaurl;
		}
		public void setDynaurl(String dynaurl) {
			this.dynaurl = dynaurl;
		}
		public String toIndentify(){
			StringBuilder sb = new StringBuilder();
			sb/*.append(index)
				.append(StringHelper.MINUS_CHAR_GAP)*/.append(style)
				.append(StringHelper.MINUS_CHAR_GAP).append(ver);
			return sb.toString();
		}
		
		/**
		 * 当前的设备是否需要升级
		 * @param indentify
		 * @return
		 */
		public static boolean needUpdate(String indentify){
			if(StringUtils.isEmpty(indentify)) return true;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 2) return true;
			HtmlInject404 p = getByStyle(array[0]);
			if(p == null) return true;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ) return true;
			return false;
		}
		
		/**
		 * 获取新的版本
		 * @param indentify
		 * @param invalidatedThenDefault indentify验证错误后返回的缺省portal值
		 * @return 为null则代表目前是最新版本
		 */
		public static HtmlInject404 getNewVerVap(String indentify,HtmlInject404 invalidatedThenDefault){
			if(StringUtils.isEmpty(indentify)) return invalidatedThenDefault;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 2) return invalidatedThenDefault;
			HtmlInject404 p = getByStyle(array[0]);
			if(p == null) return invalidatedThenDefault;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ){
				return p;
			}
			return null;
		}
		
		
		static {
			allInject404Types = new HashMap<String,HtmlInject404>();
			HtmlInject404[] types = values();
			for (HtmlInject404 type : types)
				allInject404Types.put(type.style, type);
		}
		
		public static HtmlInject404 getByStyle(String stype) {
			HtmlInject404 adv = allInject404Types.get(stype);
			if(adv != null)
				return adv;
			else
				return HtmlInject404.STYLE000;
		}
	}
	
	/**
	 * portal 定义
	 * 编号 样式 版本号 资源包url
	 * 
	 * 编号 样式 版本号 组合串为资源包识别号
	 * 
	 * 版本号 修改后为递增修改
	 * @author Edmond
	 *
	 */
	public enum HtmlPortal{
		STYLE000("style000","00.00.01",
				RuntimeConfiguration.Vap_Http_Api_UrlPrefix.concat("v1/noauth/vap/urlportal"),
				"192.168.66.7,bhunetworks.com",
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("portal/rawfiles/style001.tar.gz")),
		STYLE001("style001","00.00.02",
				RuntimeConfiguration.Vap_Http_Api_UrlPrefix.concat("v1/noauth/vap/urlportal"),
				"192.168.66.7,bhunetworks.com",
				RuntimeConfiguration.Vap_Http_Res_UrlPrefix.concat("portal/rawfiles/style001.tar.gz")),
		;
		
		private String style;
		private String ver;
		//private String dynaurl;
		//认证成功后跳转url after logined in then redirect
		private String redirect_url;
		//开放的资源链接
		private String open_resource;
		private String packurl;
		
		static Map<String, HtmlPortal> allPortalTypes;
		
		HtmlPortal(String style,String ver,String redirect_url,String open_resource,String packurl){
			this.style = style;
			this.ver = ver;
			this.redirect_url = redirect_url;
			this.open_resource = open_resource;
			this.packurl = packurl;
		}
		public String getStyle() {
			return style;
		}
		public void setStyle(String style) {
			this.style = style;
		}
		public String getVer() {
			return ver;
		}
		public void setVer(String ver) {
			this.ver = ver;
		}
		
		public String getRedirect_url() {
			return redirect_url;
		}
		public void setRedirect_url(String redirect_url) {
			this.redirect_url = redirect_url;
		}
		public String getOpen_resource() {
			return open_resource;
		}
		public void setOpen_resource(String open_resource) {
			this.open_resource = open_resource;
		}
		public String getPackurl() {
			return packurl;
		}
		public void setPackurl(String packurl) {
			this.packurl = packurl;
		}
		public String toIndentify(){
			StringBuilder sb = new StringBuilder();
			sb.append(style)
				.append(StringHelper.MINUS_CHAR_GAP).append(ver);
			return sb.toString();
		}
		
		/**
		 * 当前的设备是否需要升级portal
		 * @param indentify
		 * @return
		 */
		public boolean needUpdate(String indentify){
			if(StringUtils.isEmpty(indentify)) return true;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 3) return true;
			HtmlPortal p = getByStyle(array[0]);
			if(p == null) return true;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ) return true;
			return false;
		}
		
		/**
		 * 获取新的版本
		 * @param indentify
		 * @param invalidatedThenDefault indentify验证错误后返回的缺省portal值
		 * @return 为null则代表目前是最新版本
		 */
		public static HtmlPortal getNewVerVap(String indentify,HtmlPortal invalidatedThenDefault){
			if(StringUtils.isEmpty(indentify)) return invalidatedThenDefault;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 3) return invalidatedThenDefault;
			HtmlPortal p = getByStyle(array[0]);
			if(p == null) return invalidatedThenDefault;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ){
				return p;
			}
			return null;
		}
		
		
		static {
			allPortalTypes = new HashMap<String,HtmlPortal>();
			HtmlPortal[] types = values();
			for (HtmlPortal type : types)
				allPortalTypes.put(type.getStyle(), type);
		}
		
		public static HtmlPortal getByStyle(String stype) {
			return allPortalTypes.get(stype);
		}
	}
	
	public enum HtmlRedirect{
		STYLE000("style000","00.00.01","http://www.sina.com.cn,http://www.bhunetworks.com,http://www.chinaren.com,http://www.bhunetworks.com"),
		STYLE001("style001","00.00.01","http://www.csdn.net,http://wap.sogou.com/web/sl?keyword=&amp;bid=sogou-waps-34adeb8e32428240"),
		STYLE002("style002","00.00.01","http://baidu.com,http://google.com.hk"),
		STYLE003("style003","00.00.01","http://www.bhunetworks.com,http://www.csdn.net"),
		STYLE004("style004","00.00.01","http://sina.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c"
				+ ",http://m.sohu.com,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c"
				+ ",http://h5.mse.360.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c"
				+ ",http://hao.360.cn,http://www.hao123.com/?tn=99801877_s_hao_pg"),
		;
		private String style;
		private String ver;
		private String urls;
		
		static Map<String, HtmlRedirect> allRedirectTypes;
		
		HtmlRedirect(String style,String ver,String urls){
			this.style = style;
			this.ver = ver;
			this.urls = urls;
		}
		public String getStyle() {
			return style;
		}
		public void setStyle(String style) {
			this.style = style;
		}
		public String getVer() {
			return ver;
		}
		public void setVer(String ver) {
			this.ver = ver;
		}

		
		public String getUrls() {
			return urls;
		}
		public void setUrls(String urls) {
			this.urls = urls;
		}
		public String toIndentify(){
			StringBuilder sb = new StringBuilder();
			sb.append(style)
				.append(StringHelper.MINUS_CHAR_GAP).append(ver);
			return sb.toString();
		}
		
		/**
		 * 当前的设备是否需要升级
		 * @param indentify
		 * @return
		 */
		public boolean needUpdate(String indentify){
			if(StringUtils.isEmpty(indentify)) return true;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 2) return true;
			HtmlRedirect p = getByStyle(array[0]);
			if(p == null) return true;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ) return true;
			return false;
		}
		
		/**
		 * 获取新的版本
		 * @param indentify
		 * @param invalidatedThenDefault indentify验证错误后返回的缺省portal值
		 * @return 为null则代表目前是最新版本
		 */
		public HtmlRedirect getNewVerVap(String indentify,HtmlRedirect invalidatedThenDefault){
			if(StringUtils.isEmpty(indentify)) return invalidatedThenDefault;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 2) return invalidatedThenDefault;
			HtmlRedirect p = getByStyle(array[0]);
			if(p == null) return invalidatedThenDefault;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ){
				return p;
			}
			return null;
		}
		
		
		static {
			allRedirectTypes = new HashMap<String,HtmlRedirect>();
			HtmlRedirect[] types = values();
			for (HtmlRedirect type : types)
				allRedirectTypes.put(type.style, type);
		}
		
		public static HtmlRedirect getByStyle(String stype) {
			HtmlRedirect adv = allRedirectTypes.get(stype);
			if(adv != null)
				return adv;
			else
				return HtmlRedirect.STYLE000;
		}
	}
	
	
	public static void main(String[] argv){
		System.out.println(String.format("%s %s", 1,2,3));
		/*String aa = "000-001-sdf";
		String[] ss = aa.split(StringHelper.MINUS_STRING_GAP);
		for(String s:ss){
			System.out.println(s);
		}*/
	}
}
