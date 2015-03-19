package com.bhu.vas.business.bucache.redis.serviceimpl;

public class BusinessKeyDefine {
	
	public static class Default{
		public static final String TileRecentSubject		        = "TRS";
		public static final String TileHotSubject 					= "THS";
		
		public static final String WorldRecentSubject		        = "WRS";
		public static final String WorldHotSubject 					= "WHS";
	}
	
	
	public static class Present{
		public static final String UserTokenPrefixKey  			= "WTH";//"TokenH";
		public static final String UserPushNotifyCountPrefixKey = "UPN";
		public static final String UserMarkPrefixKey 			= "BMH.";//"BuMarkH.";
		public static final String SubjectPrefixKey 			= "SP.";//"BuMarkH.";
		public static final String WifiDevicePresentPrefixKey 			= "WDP";
		public static final String WifiDeviceHandsetPresentPrefixKey 			= "WDHP";
	}
	
	public static class Statistics{
		public static final String SubjectFragment  				= "SF";
		/*public static final String SubjectRelaton  					= "SR";
		public static final String CommentRelation  				= "CR";
		
		public static final String TileRelation  					= "TR";
		public static final String WorldRelation  					= "WR";
		public static final String TileHourFragmentRelation  		= "THFR";*/
		public static final String SystemStatistics		= "SS.";
		public static final String DailyStatistics		= "DS.";
		public static final String DailyStatisticsHandsetInnerPrefixKey	= "H.";
		public static final String DailyStatisticsDeviceInnerPrefixKey	= "D.";
		//UserPrefixKey
		
		public static final String WifiDeviceStatistics 		= "WDS";
	}
	
}
