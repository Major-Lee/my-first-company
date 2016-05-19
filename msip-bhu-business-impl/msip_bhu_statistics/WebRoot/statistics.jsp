<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
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

  </head>
  
  <body>
    <table>
    	<thead>
    		<tr>
    			<td>
    				UMPV
    			</td>
    		</tr>
    		<tr>
    			<td>
    				UMUV
    			</td>
    		</tr>
    		<tr>
    			<td>
    				UMIP
    			</td>
    		</tr>
    	</thead>
    	<tbody>
    	
    	</tbody>
    </table>
  </body>
  <script src="js/jquery-1.9.1.min.js"></script>
  <script type="text/javascript">
  //testStatistics();
  function testStatistics(){
	  var orderJson = '{}';
	  $.post("/msip_bhu_statistics/index.do", {
		  	"data": orderJson,
		  }).success(function(data) {
			  alert(data);
		  });
  }
  
  </script>
</html>
