package com.bhu.vas.business.search.test.user.index;

public class IndexBusinessStructureConstants {
	
	public static final String PerformanceIndex = "performanceindex";
	
	public static interface PerformanceIndexTypes{
		public static final String PrefixType = "prefix";
		public static final String SuggestType = "suggest";
		public static final String MultiMatchType = "multimatch";
	}
}
