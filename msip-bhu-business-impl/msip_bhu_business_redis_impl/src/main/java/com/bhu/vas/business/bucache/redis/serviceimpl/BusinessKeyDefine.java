package com.bhu.vas.business.bucache.redis.serviceimpl;

public class BusinessKeyDefine {
	
	public static class Default{
		public static final String TileRecentSubject		        = "TRS";
		public static final String TileHotSubject 					= "THS";
		
		public static final String WorldRecentSubject		        = "WRS";
		public static final String WorldHotSubject 					= "WHS";
	}
	
	public static class HandsetPresent{
		public static final String StatisticsPrefixKey 			= "HPS.";
		public static final String PresentPrefixKey 			= "HPP.";
	}
	
	public static class Present{
		public static final String MarkPrefixKey = "MP";
		public static final String UserTokenPrefixKey  			= "WTH";//"TokenH";
		public static final String UserPushNotifyCountPrefixKey = "UPN";
		public static final String UserMarkPrefixKey 			= "BMH.";//"BuMarkH.";
		public static final String SubjectPrefixKey 			= "SP.";//"BuMarkH.";
		public static final String WifiDevicePresentPrefixKey 			= "WDP";
		public static final String WifiDevicePresentCtxPrefixKey 			= "WDPC";
		public static final String WifiDeviceSerialTaskPrefixKey 			= "WDST";
		public static final String WifiDeviceHandsetPresentPrefixKey 			= "WDHP";
		public static final String WifiDeviceModeStatusPrefixKey 			= "WDMS";
		public static final String WifiDeviceMobilePresentPrefixKey 			= "WDMP";
	}
	
	public static class Statistics{
		public static final String FragmentUserOnline  				= "FO.";
		public static final String FragmentDeviceOnline  			= "FD.";
		public static final String FragmentOnlineDailySuffixKey 	= "D.";
		public static final String FragmentOnlineWeeklySuffixKey 	= "W.";
		public static final String FragmentOnlineMonthlySuffixKey 	= "M.";
		public static final String FragmentOnlineQuarterlySuffixKey = "Q.";
		public static final String FragmentOnlineYearlySuffixKey 	= "Y.";
		
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
		public static final String WifiDeviceStatistics_RealtimeRate 		= "RR";
		public static final String WifiDeviceStatistics_LastRate 		= "LR";
		public static final String WifiDeviceStatistics_RateWaiting 		= "RWG";
		public static final String WifiDeviceStatistics_PeakRate 		= "PR";
		public static final String WifiDeviceStatistics_PeakSectionRate 		= "PSR";
		//public static final String WifiDeviceStatistics_PeakRateWaiting 		= "PRWG";
		public static final String WifiDeviceStatistics_HDRateWaiting 		= "HDRWG";
	}
	public static class Unique{
		public static final String EmailCheck = "WUEC";
		public static final String NickCheck = "WUNC";
		public static final String MobilenoCheck = "WUMC";
		//public static final String PermalinkCheck = "UPC";
		//public static final String SnsTokenCheck = "USC";
		public static final String UserSnsCheck  = "WUSC";
		
		public static final String Sequence = "SEQ";
	}

	public static class WifiStasniffer{
		public static final String TerminalRecent = "WSTR";
		public static final String TerminalHot = "WSTH";
		public static final String TerminalDetailRecent = "WSTDR";
		public static final String TerminalDeviceTypeCount = "WSDTC";
		public static final String TerminalLastTime = "WSLT";
		public static final String UserTerminalFocus = "WSUTF";
	}
	public static class VapMode {
		public static final String VapModeCount = "VM.";
	}

	public static class UserWifiDeviceHandset {
		public static final String Nick = "UDHN";
	}

	public static class WifiDeviceGuest {
		public static final String Guest = "WDG";
	}
}
