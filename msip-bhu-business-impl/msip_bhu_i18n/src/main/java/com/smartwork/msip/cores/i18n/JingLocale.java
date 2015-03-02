package com.smartwork.msip.cores.i18n;

import java.util.Locale;
import java.util.Map;
/**
 * 
 * @author Edmond
 *
 *
 *	
   Locale.CHINA　　　　　　　　　　 zh_CN
　　Locale.CHINESE　　　　　　　　　 zh
　　Locale.SIMPLIFIED_CHINESE　　　　zh_CN
　　Locale.TRADITIONAL_CHINESE　　　 zh_TW
　　Locale.PRC　　　　　　　　　　　 zh_CN
　　Locale.TAIWAN　　　　　　　　　　zh_TW
　　Locale.ENGLISH　　　　　　　　　 en
　　Locale.UK　　　　　　　　　　　　en_GB
　　Locale.US　　　　　　　　　　　　en_US
　　Locale.FRANCE　　　　　　　　　　fr_FR
　　Locale.FRENCH　　　　　　　　　　fr
 */
public enum JingLocale {
	zh_CN(new String[] {"中国"},Locale.CHINA,"zh_CN"),
	zh_TW(new String[] {"台湾"},Locale.TAIWAN,"zh_TW"),
	en_GB(new String[] {"英国"},Locale.UK,"en_GB"),
	en_US(new String[] {"美国"},Locale.US,"en_US");
	private String[] regions;
	private Locale locale;
	private String langContry;
	private JingLocale(String[] regions,Locale locale,String langContry){
		this.regions = regions;
		this.locale = locale;
		this.langContry = langContry;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
		new Locale("","");
	}
	public String[] getRegions() {
		return regions;
	}
	public void setRegions(String[] regions) {
		this.regions = regions;
	}
	
	public String getLangContry() {
		return langContry;
	}
	public void setLangContry(String langContry) {
		this.langContry = langContry;
	}

	private static Map<String, JingLocale> mapRegionLocale;
	private static Map<String, JingLocale> maplangContryLocale;
	
}
