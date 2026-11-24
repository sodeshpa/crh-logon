<%@page import="com.crh.logon.web.utils.Authorization"%>

<%@page import="com.crh.logon.order.web.forms.auftrag.OrderDetailForm"%>
<%@ include file="../../includeTLDs.jsp"%>
<script type="text/javascript">

function getAuthParams(){
		<%------------------------------------------------------------------------------------------------%>
		<%--------------------PARAMS SENT IN CASE USER HAS AUTHORIZATION TO SOME FUNCTIONALITY -----------%>
		<%-------------ORDER ATTRIBUTES SHOULD BE MAPPED ONLY IN CASE USER HAS AUTHORIZED TO CHANGE---------%>
		var aParams = new Array();
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_W %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_W %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_W %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_W %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_DELIVERY_DATE_TIME %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_DELIVERY_DATE_TIME %>');
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_BILLTO_PARTY %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_BILLTO_PARTY %>');
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_PAYER_PARTY %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_PAYER_PARTY %>');
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_ONETIMESOLDTO_ADDR %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_ONETIMESOLDTO_ADDR %>');
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_FORWARDING_AGENT %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_FORWARDING_AGENT %>');
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO %>');
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_QUANTITY_UOM %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_QUANTITY_UOM %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_DELIVERY_NOTE %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_MATERIAL_DELIVERY_NOTE %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_W %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_W %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CRH %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_RATING_COMMNT_CRH %>');
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CARR %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_RATING_COMMNT_CARR %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_DISPATCHING %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_COMMENT_DISPATCHING %>');	
</crh-common:authorize>
		<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_DRIVER%>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_COMMENT_DRIVER %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_SHIPPING %>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_REPORT_COMMENT_SHIPPING %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_ORDER_MANAGEMENT%>">
		aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_ORDER_MANAGEMENT %>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT%>">
	aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_WAGON_SPECIAL_ORDER_CREATE_EDIT%>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_SPI %>">
	aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_SPI%>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_ON_HOLD_FUNCTIONALITY %>">
	aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_ON_HOLD_W%>');	
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_LDS_ORDERNO%>">
	aParams.push('<%= OrderDetailForm.PARAM_ORDER_DETAIL_EDIT_LDS_NO%>');	
</crh-common:authorize>
	return aParams;
}
</script>
