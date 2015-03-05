package com.smartwork.im.utils;

import com.smartwork.msip.cores.helper.StringHelper;

public class IMHelper {
	
	/*public static boolean isPeer2Peer(String to){
		return to.indexOf('@') == -1;
	}*/
	public static ToDto parserTo(String to){
		int index = to.indexOf('@');
		if(index == -1){
			return new ToDto(to,null);
		}else{
			return new ToDto(to.substring(0, index),to.substring(index+1));
		}
	}
	
	public static String builderGroupTo(String to,String group){
		StringBuilder sb = new StringBuilder();
		sb.append(to).append(StringHelper.AT_STRING_GAP).append(group);
		return sb.toString();
	}
	
	//public static String builderGroup
}
