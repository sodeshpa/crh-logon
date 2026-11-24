<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ShippingPointTile.jsp,v $ --%>
<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant"%>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ShippingPointAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RFIDAdminAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/masterdata/shipping-point-util.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-validation.js'/>"></script>

<script type="text/javascript">
var constants = {
		WEIGH_SYSTEM_LP_WS: '<%=Constant.WEIGH_SYSTEM_LP_WS %>'
};
</script>	
<body>
<jsp:include page="../SubTab9Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.4"/></th>
      </tr>
      <tr>
    
      <td>
            <!-- Beginn Submenu 2-2-1-1 -->
<jsp:include page="../Sub4Tab9Tile.jsp"/>
   
<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
	style="padding: 0px; margin: 0px; margin-top: 20px;">
	<colgroup>
		<col width="2%">
		<col width="48%">
		<col width="2%">
		<col width="48">
	</colgroup>
	<tr>
		<td valign="bottom"
			style="white-space: nowrap; border: 0px; padding-right: 4px;"
			class="label"><fmt:message key="shippingpoint.field.plants" />:</td>
		<td style="border: 0px;"><input type="text" name="plants"
			value="" id="plants" style="width: 95%"></td>
		<td valign="bottom"
			style="white-space: nowrap; border: 0px; padding-right: 4px;"
			class="label"><fmt:message
			key="shippingpoint.field.shippingpoints" />:</td>
		<td style="border: 0px;"><input type="text" name="shippingPoints"
			value="" class="textfield" id="shippingPoints" style="width: 95%"></td>
	</tr>
</table>

<div id="ShippingPointDiv"></div>

</td>
</tr>
</table>
</body>
</html>
<%-- END $RCSfile: ShippingPointTile.jsp,v $ --%>
