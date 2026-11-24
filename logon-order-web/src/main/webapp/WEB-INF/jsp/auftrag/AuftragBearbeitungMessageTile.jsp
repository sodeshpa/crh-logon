<%@ page session="true"%>
	
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<crh-common:authorize function="<%= Authorization.SHIPPING_W %>">
<script>
Ext.onReady(function(){
	<logic:notPresent name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
	<logic:notPresent  name="org.apache.struts.action.ERROR" scope="request">
	loadOrders(true);
	</logic:notPresent>
	</logic:notPresent>
});
</script>
</crh-common:authorize>
