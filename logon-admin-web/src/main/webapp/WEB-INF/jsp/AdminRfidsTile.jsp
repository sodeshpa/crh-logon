<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AdminRfidsTile.jsp,v $ --%>
<%@ page import="com.crh.logon.admin.web.forms.AdminRFIDPopupForm" %>
<%@ page import="com.crh.logon.business.services.utils.RFIDBean"%>
 
<%@ include file="includeTLDs.jsp"%>
<script type="text/javascript">
	function removeRFID(rfTagId) {
		var diva = Ext.get('rfids');
		diva.load( {
			url :'<%=request.getContextPath() %>/AdminRFIDPopup.do',
			params : {
				'removeRfid' :'',
				'rfidTagId':rfTagId
			},
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true
		});
	}
</script>


<table class="classic">
<%
Object obj = request.getSession().getAttribute(RFIDBean.SESSION_KEY);
	pageContext.setAttribute("obj", obj);
	%>
		<c:set var="rfList" value="${obj.rfidObject.rfids }"/>
		<c:forEach var="rfid" varStatus="status"
		items="${rfList}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
			<logic:present name="rfid" property="rfidobject">
				<td style="width: 100%; overflow: hidden;"><c:out
					value="${rfid.name}" /></td>
				<td valign="middle" style="width: 1px"><nobr> <a
					href="javascript:removeRFID(<c:out value="${rfid.rfidtagId}"/>)"><img
					src="<crh-common:url value="/images/delete2.png" />"/></a> </nobr></td>
					</logic:present>
			</tr>
	</c:forEach>

	
</table>
