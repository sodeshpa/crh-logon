<%@ page session="true"%>
	
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%-- This is a tile, which is called each time after save. Reloads the page if no warnings were present, but just renders
the messages in case there was any. Thus the input fields remain as they were.--%>
<crh-common:authorize function="<%= Authorization.WEB_ORDER %>">
<script>
//# sourceURL=WebBestellungCallbackTile.jsp.js
Ext.onReady(function(){
<logic:present  name="action" scope="request">

		
<%---------------------------------------------------------------------------------------------------%>
<%-------------------------------------- IF NOT WARNINGS PRESENT-------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
<logic:notPresent name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
<logic:notPresent  name="org.apache.struts.action.ERROR" scope="request">

<%--------------------------------------SAVE --------------------------------------------------------%>
<c:if test="${action eq 'save'}">
	refreshPage();
	return false;
</c:if>

<%--------------------------------------FORWARD TO ADJUST DELIVERIES------------------------------%>
<c:if test="${prepareAdjustDeliveries1Success}">

showAdjustDeliveriesPopUp();

</c:if>
function showAdjustDeliveriesPopUp(){
	var action = 'prepareAdjustDeliveries2';
	document.getElementById("formAction").value=action;
	var params = {};
	params.action = action;
	addFormElements(params, "cust_WebBestellungForm");
	disableSave();
	var loadingMessage = '<fmt:message key="extjs.processing"/>';
	showPopup('WebBestellung.do', loadingMessage, params);
}

<%--------------------------------------ADD BIMODAL RAIL PART----------------------------------------%>
<c:if test="${action eq 'createBimodalRailpart'}">
	enableSave();
	return false;
</c:if>

</logic:notPresent>
</logic:notPresent>

<c:if test="${action eq 'save'}">
enableSave();
return false;
</c:if>
});
</logic:present>
</script>
</crh-common:authorize>
