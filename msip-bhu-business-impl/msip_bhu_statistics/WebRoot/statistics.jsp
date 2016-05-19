<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
  	<div style="text-align: center;width: 100%">
	  	<select class="eventNames" onchange="testStatistics()"></select>
	    <table style="width: 500px;height:100px;margin:auto">
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
  	</div>
  	
  </body>
  <script src="js/jquery-1.9.1.min.js"></script>
  <script type="text/javascript">
  
  function testStatistics(){
	  var event_name=$('.eventNames').val();
	  if(event_name=='0'){
		  return;
	  }
	  var orderJson = '{"event_name":"'+event_name+'","from_date":"2016-05-01","to_date":"2016-05-20","on_condition":"","where_condition":""}';
	  $.post("/msip_bhu_statistics/index.do", {
		  	"data": orderJson,
		  }).success(function(data) {
			  data=eval("(" +data+")");
			  $('.pv').html(data.pv);
			  $('.uv').html(data.uv);
			  $('.ip').html(data.ip);
			  $('.eventName').html(data.eventName);
		  });
  }
  queryEventNames();
  function queryEventNames(){
	  var orderJson = '{"event_name":"pc+赏","from_date":"2016-05-01","to_date":"2016-05-20","on_condition":"","where_condition":""}';
	  $.post("/msip_bhu_statistics/queryEventNames.do", {
		  	"data": orderJson,
		  }).success(function(data) {
			  var resArray=data.split(",");
			  var html="<option value=\"0\">请选择事件</option>";
			  for(var i=0;i<resArray.length;i++){
				  html+="<option value="+resArray[i]+">"+resArray[i]+"</option>";
			  }
			  $('.eventNames').html(html);
		  });
  }
  
  </script>
</html>
