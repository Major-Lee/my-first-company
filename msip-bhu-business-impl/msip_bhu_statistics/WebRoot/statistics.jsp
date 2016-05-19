<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'statistics.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<style type="text/css">
		td{border:solid #add9c0; border-width:0px 1px 1px 0px;}
 		table{border:solid #add9c0; border-width:1px 0px 0px 1px;}
	</style>
  </head>
  
  <body>
    <table >
    	<thead>
    		<tr>
    			<td>
    				UMPV
    			</td>
    			<td>
    				UMUV
    			</td>
    			<td>
    				UMIP
    			</td>
    		</tr>
    	</thead>
    	<tbody>
    		<tr>
    			<td class="pv">0</td>
    			<td class="uv">0</td>
    			<td class="ip">0</td>
    		</tr>
    	</tbody>
    </table>
  </body>
  <script src="js/jquery-1.9.1.min.js"></script>
  <script type="text/javascript">
  testStatistics();
  function testStatistics(){
	  var orderJson = '{"event_name":"pc+打赏","from_date":"2016-05-01","to_date":"2016-05-20","on_condition":"","where_condition":""}';
	  $.post("/msip_bhu_statistics/index.do", {
		  	"data": orderJson,
		  }).success(function(data) {
			  data=eval("(" +data+")");
			  $('.pv').html(data.pv);
			  $('.uv').html(data.uv);
			  $('.ip').html(data.ip);
			 // alert(data);
		  });
  }
  
  </script>
</html>
