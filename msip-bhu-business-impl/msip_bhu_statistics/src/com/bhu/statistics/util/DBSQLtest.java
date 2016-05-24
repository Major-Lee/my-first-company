package com.bhu.statistics.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;


public class DBSQLtest {
	   public static final String DBDRIVER = "com.mysql.jdbc.Driver";  
	    
	    public static final String DBURL = "jdbc:mysql://192.168.66.7:3306/msip_bhu_core";  
	      
	    public static final String DBUSER = "root";  
	      
	    public static final String DBPASS = "1234";  
	    public static String getData() throws Exception{  
	        Connection conn = null;
	        Class.forName(DBDRIVER);
	        conn = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
	        if(conn == null){
	        	System.out.println("数据库连接失败！");
	        }
	      //创建存储过程的对象  
	        CallableStatement c=conn.prepareCall("{call finacial_statistics(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");  
	        String beginTime="2016-05-09 00:00:00";
	        String endTime="2016-05-09 23:59:59";
	        c.setString(1, beginTime);
	        c.setString(2, endTime);
	        c.registerOutParameter(3, java.sql.Types.DOUBLE);
	        c.registerOutParameter(4, java.sql.Types.DOUBLE);
	        c.registerOutParameter(5, java.sql.Types.DOUBLE);
	        c.registerOutParameter(6, java.sql.Types.DOUBLE);
	        c.registerOutParameter(7, java.sql.Types.DOUBLE);
	        c.registerOutParameter(8, java.sql.Types.DOUBLE);
	        c.registerOutParameter(9, java.sql.Types.DOUBLE);
	        c.registerOutParameter(10, java.sql.Types.DOUBLE);
	        c.registerOutParameter(11, java.sql.Types.DOUBLE);
	        c.registerOutParameter(12, java.sql.Types.DOUBLE);
	        c.registerOutParameter(13, java.sql.Types.DOUBLE);
	        c.registerOutParameter(14, java.sql.Types.DOUBLE);
	        c.execute();  
	        System.out.println(c.getDouble(3));
	        System.out.println(c.getDouble(4));
	        System.out.println(c.getDouble(5));
	        System.out.println(c.getDouble(6));
	        System.out.println(c.getDouble(7));
	        System.out.println(c.getDouble(8));
	        System.out.println(c.getDouble(9));
	        System.out.println(c.getDouble(10));
	        System.out.println(c.getDouble(11));
	        System.out.println(c.getDouble(12));
	        System.out.println(c.getDouble(13));
	        System.out.println(c.getDouble(14));
	        conn.close();
	        return "";    
	    } 
	    public static void main(String[] args) {
			try {
				getData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
