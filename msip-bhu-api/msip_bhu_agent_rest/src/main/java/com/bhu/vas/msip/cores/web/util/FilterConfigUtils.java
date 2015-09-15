package com.bhu.vas.msip.cores.web.util;

import javax.servlet.FilterConfig;

import org.apache.commons.lang.StringUtils;

public class FilterConfigUtils {

	public static String getParameter(FilterConfig config,String key,String defaultValue) {
		String v = config.getInitParameter(key);
		return StringUtils.isEmpty(v) ? defaultValue : v;
	}

	public static int getIntParameter(FilterConfig config,String key,int defaultValue) {
		String v = getParameter(config,key,Integer.toString(defaultValue));
		return Integer.parseInt(v);
	}

	public static int getIntegerParameter(FilterConfig config,String key,Integer defaultValue) {
		String v = config.getInitParameter(key);
		if(v == null)
			return defaultValue;
		else
			return new Integer(v);
	}
}
