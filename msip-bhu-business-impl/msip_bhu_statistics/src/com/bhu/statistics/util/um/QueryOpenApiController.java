package com.bhu.statistics.util.um;

public class QueryOpenApiController {
	public String queryStatistic(){
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		String result=apiCnzzImpl.queryCnzzStatistic("测试页面浏览", "2016-05-13", "2016-05-13", "name", "");
		System.out.println(result);
		return "";
	}
	public static void main(String[] args) {
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		String result=apiCnzzImpl.queryCnzzStatistic("$Pv", "2016-05-01", "2016-05-14", "", "");
		//String result=apiCnzzImpl.queryCnzzStatistic("测试页面浏览", "2016-05-01", "2016-05-13", "", "");
		//System.out.println(result);
	}
}
