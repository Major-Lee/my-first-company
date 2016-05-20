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
  	<div style="text-align: center;width: 100%;height:100%">
  		<div style="margin-top: 50px">
	  		<select class="eventNames" onchange="testStatistics()"></select>
			开始时间:<input type="text" id="startTime" name="startTime" onclick="new Calendar().show(this);"/>
			结束时间:<input type="text" id="endTime" name="endTime" onclick="new Calendar().show(this);"/>
  			<select id="type">
  				<option value="1">今日</option>
  				<option value="2">昨日</option>
  				<option value="3">本月</option>
  			</select>
  		</div>
	    <table style="width: 500px;height:100px;margin:auto;margin-top: 50px">
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
  <script type="text/javascript" src="js/Calendar.js"></script>
  <script type="text/javascript">
  
  function testStatistics(){
	  var event_name=$('.eventNames').val();
	  var type=$('#type').val();
	  /* var startTime=$('#startTime').val();
	  var endTime=$('#endTime').val();
	  if(event_name=='0'){
		  return;
	  }
	  if(startTime == null || startTime == ""){
			alert("开始时间不能为空！");
			return;
		}
		if(endTime == null || endTime == ""){
			alert("结束时间不能为空！");
			return;
		}
		var d1 = new Date(startTime.replace(/\-/g, "\/"));  
		var d2 = new Date(endTime.replace(/\-/g, "\/"));  
		if(startTime!=""&&endTime!=""&&d1 >d2){  
		  	alert("开始时间不能大于结束时间！");  
		  	return false;  
		} */
	  var orderJson = '{"event_name":"'+event_name+'","from_date":"","to_date":"","on_condition":"","where_condition":"","type":"'+type+'"}';
	  $.post("/bhu_api/v1/msip_bhu_statistics/index.do", {
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
