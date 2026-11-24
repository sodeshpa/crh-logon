<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page
    session="false"
%>
<html> 
	<head>
		<title>Crh Ltd - Global supplier of cement, aggregates and concrete</title>
		<meta name="robots" content="all">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
		<meta http-equiv="PRAGMA" content="NO-CACHE">
		<meta name="keywords" content="Cement, Aggregates, Concrete, World Leading Supplier, Building Industry, Construction Materials,">
		<style type="text/css">
	 		body { 
	 			font-family: Arial, Helvetica, sans-serif;
	 			margin: 0px;
				background-color: #F0F0F0;
				color: #000000; 			 
	 		}
			h1 {
				font-size: 20px;
				color:#333333;
				margin-bottom: 18px;
			}
			h2 {
				font-size: 16px;
				color:#333333;
				margin-bottom: 18px;
			}
			p, ul, ol, li, dl, dt, dd, div, td, pre, address, blockquote {
				font-size: 11px;
			}
			.customer_max {
				margin: 0px 0px 0px 0px;
				border: 0px solid #CCC;
				padding: 5px 0px 5px 0px;
				background-color: #E8E8E8;
				width: 100%;
			}
			table.navigation {
				border-bottom: 6px solid #6699ff;
			}
			.applicationheader {
				background-color: #FFFFFF;
			}
			html,
			body{
				height:100%;
				margin-left:0px;
				margin-right:0px;
				margin-top:0px;	
				margin-bottom:0px;	
				padding-top:0px;	
				padding-bottom:0px;	
				padding-left:0px;
				padding-right:0px;
				color:#000000;
			}
			img{
				border: 0px black none;
				margin-left:0px;
				margin-right:0px;
				margin-top:0px;	
				margin-bottom:0px;	
				padding-top:0px;	
				padding-bottom:0px;	
				padding-left:0px;
				padding-right:0px;
				vertical-align:bottom;
			}
		</style>
	</head>
	
	<body>
		<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
			<tbody>
				<tr>
					<td height="55" align="right" valign="top" class="applicationheader" style="width:100%;"><img src="<%=request.getContextPath() %>/images/img_logo_logon.gif" alt="LOGON" style="margin: 10px 0px 0px 0px;"></td>
					<td width="10" align="right" valign="top" class="applicationheader" style="width:10px;"><div style="width:10px;">&nbsp;</div></td>
				</tr>
				<tr>
					<td height="18" valign="top" class="applicationheader">&nbsp;</td>
					<td colspan="2" valign="top" class="applicationheader">&nbsp;</td>
				</tr>
				<tr>
					<td height="15" colspan="3" class="applicationheader">
					<table width="100%" border="0" cellpadding="0" cellspacing="0" class="navigation">
				<tr>
					<td width="100%" style="padding-left:10px">&nbsp;
					</td>
				</tr>
		</table>
        
		<div align="center" class="customer_max" style="background-color:#f0f0f0;">
			<table align="center" cellpadding="0" cellspacing="0" border="0" style="padding:0px; margin:0px;">
				<tr>
					<td align="center" valign="top" height="100%">
						<h1><%= request.getParameter("message") %></h1>
					</td>
				</tr>
			</table>
			<br>
		</div>
	</body>
</html>