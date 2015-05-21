package com.bhu.vas.api.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

public class VapModeDefined {
	//<img src="http://192.168.66.7/vap/ad/001/js/../images/hot_1.png" alt="Hot">
	//private final static String url_prefix = "http://vap.bhunetworks.com/vap/";
	private final static String url_prefix = "http://192.168.66.7/vap/";
	
	public enum HtmlInjectAdv{
		STYLE000("STYLE000","00.00.01","http://auth.wi2o.cn/ad/ad.js"),
		STYLE001("STYLE001","00.00.01",url_prefix.concat("ad/001/js/ad.js")),
		;
		//private String index;
		private String style;
		private String ver;
		private String url;
		
		static Map<String, HtmlInjectAdv> allInjectAdvTypes;
		
		HtmlInjectAdv(/*String index,*/String style,String ver,String url){
			//this.index = index;
			this.style = style;
			this.ver = ver;
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
		
		public static HtmlInjectAdv getByStyle(String stype) {
			HtmlInjectAdv adv = allInjectAdvTypes.get(stype);
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
	 */
	public enum HtmlInject404{
		STYLE000("style000","00.00.01",url_prefix.concat("404/rawfiles/inject/style000.zip")),
		STYLE001("style001","00.00.01",url_prefix.concat("404/rawfiles/inject/style001.zip")),
		;

		private String style;
		private String ver;
		private String url;
		static Map<String, HtmlInject404> allInject404Types;
		HtmlInject404(String style,String ver,String url){
			this.style = style;
			this.ver = ver;
			this.url = url;
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
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
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
		STYLE000("style000","00.00.01","http://vap.bhunetworks.com/vapfiles/portal/000-normal-1.zip"),
		STYLE001("style001","00.00.01","http://vap.bhunetworks.com/vapfiles/portal/000-style001-1.zip"),
		;
		
		private String style;
		private String ver;
		private String url;
		
		static Map<String, HtmlPortal> allPortalTypes;
		
		HtmlPortal(String style,String ver,String url){
			this.style = style;
			this.ver = ver;
			this.url = url;
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
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
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
	
	
	/*public static void main(String[] argv){
		String aa = "000-001-sdf";
		String[] ss = aa.split(StringHelper.MINUS_STRING_GAP);
		for(String s:ss){
			System.out.println(s);
		}
		
	}*/
}
