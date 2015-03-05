/**
 * $RCSfile$
 * $Revision: 1715 $
 * $Date: 2005-07-26 21:05:38 -0300 (Tue, 26 Jul 2005) $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Public License (GPL),
 * a copy of which is included in this distribution.
 */

package com.smartwork.im.utils;

import java.util.ArrayList;
import java.util.List;

import com.smartwork.msip.localunit.RandomPicker;

/**
 * Contains constant values representing various objects in Jive.
 */
public class JingConstants {

    public static final int SYSTEM = 17;
    public static final int ROSTER = 18;
    public static final int OFFLINE = 19;
    public static final int MUC_ROOM = 23;

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;

    /**
     * Date/time format for use by SimpleDateFormat. The format conforms to
     * <a href="http://www.jabber.org/jeps/jep-0082.html">JEP-0082</a>, which defines
     * a unified date/time format for XMPP.
     */
    public static final String XMPP_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    
    //此部分逻辑定义在 msip_core com.smartwork.msip.business.runtimeconf.RuntimeConfiguration也有同样定义 ，修改后，请同步修改
    public static final List<Integer> SystemDefaultSupportedUserIDs = new ArrayList<Integer>();//"1001"
    
    static{
    	SystemDefaultSupportedUserIDs.add(1001);
    }
    public static boolean isSystemSupportedUsers(int uid){
		return uid<=2000 || SystemDefaultSupportedUserIDs.contains(uid);
	}
	public static boolean isSystemSupportedUsers(String uid){
		try{
			return isSystemSupportedUsers(Integer.parseInt(uid));
		}catch(Exception ex){
			return false;
		}
	}
	
	private static String[] randomRobotResponseContents = {"照片拍得不错！","你想干嘛？","想什么呢？","我闪！","我不清楚你想做什么！"};
	
	public static String randomRobotResponseContent(){
		return RandomPicker.pick(randomRobotResponseContents);
	}
}