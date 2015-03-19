package com.bhu.vas.business.search.constants;
/**
 * 索引常量类
 * 记录各种业务索引的存放库名和type名
 * @author lawliet
 *
 */
public class IndexConstants {
	public static final String UserShards = "5";
	public static final String UserReplicas = "1";
	public static final String UserIndex = "user_index";//用户数据索引库名字
	public static final String UserLocationIndex = "userlocation_index";//用户地理位置数据索引库名字
	
	public static interface Types {
		public static final String UserType = "user";//用户数据索引库类别
		public static final String UserLocationType = "userlocation";//用户地理位置数据索引库类别
	}
}
