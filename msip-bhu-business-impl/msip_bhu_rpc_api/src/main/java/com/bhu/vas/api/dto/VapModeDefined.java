package com.bhu.vas.api.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

public class VapModeDefined {
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
	public enum Portal{
		NORMAL("000","normal","1","http://vap.bhunetworks.com/vapfiles/portal/000-normal-1.zip"),
		STYLE001("001","style001","1","http://vap.bhunetworks.com/vapfiles/portal/000-style001-1.zip"),
		;
		
		private String index;
		private String style;
		private String ver;
		private String url;
		
		static Map<String, Portal> allPortalTypes;
		
		Portal(String index,String style,String ver,String url){
			this.index = index;
			this.style = style;
			this.ver = ver;
			this.url = url;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
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
			sb.append(index)
				.append(StringHelper.MINUS_CHAR_GAP).append(style)
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
			Portal p = getPortalByIndex(array[0]);
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
		public Portal getNewVerVap(String indentify,Portal invalidatedThenDefault){
			if(StringUtils.isEmpty(indentify)) return invalidatedThenDefault;
			String[] array = indentify.split(StringHelper.MINUS_STRING_GAP);
			if(array.length != 3) return invalidatedThenDefault;
			Portal p = getPortalByIndex(array[0]);
			if(p == null) return invalidatedThenDefault;
			if(!p.getStyle().equals(array[1]) || !p.getVer().equals(array[2]) ){
				return p;
			}
			return null;
		}
		
		
		static {
			allPortalTypes = new HashMap<String,Portal>();
			Portal[] types = values();
			for (Portal type : types)
				allPortalTypes.put(type.getIndex(), type);
		}
		
		public static Portal getPortalByIndex(String index) {
			return allPortalTypes.get(index);
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
		NORMAL("000","normal","1","http://vap.bhunetworks.com/vapfiles/inject/000-normal-1.zip"),
		STYLE001("001","style001","1","http://vap.bhunetworks.com/vapfiles/inject/000-style001-1.zip"),
		;

		private String index;
		private String style;
		private String ver;
		private String url;
		HtmlInject404(String index,String style,String ver,String url){
			this.index = index;
			this.style = style;
			this.ver = ver;
			this.url = url;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
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
			sb.append(index)
				.append(StringHelper.MINUS_CHAR_GAP).append(style)
				.append(StringHelper.MINUS_CHAR_GAP).append(ver);
			return sb.toString();
		}
	}
	
	
	public enum HtmlInjectAdv{
		NORMAL("000","normal","1","http://auth.wi2o.cn/ad/ad.js"),
		;

		private String index;
		private String style;
		private String ver;
		private String url;
		HtmlInjectAdv(String index,String style,String ver,String url){
			this.index = index;
			this.style = style;
			this.ver = ver;
			this.url = url;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
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
			sb.append(index)
				.append(StringHelper.MINUS_CHAR_GAP).append(style)
				.append(StringHelper.MINUS_CHAR_GAP).append(ver);
			return sb.toString();
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
