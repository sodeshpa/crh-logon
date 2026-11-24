<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WagonDetailCopyWagonTile.jsp,v $ --%>


<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@ include file="../../includeTLDs.jsp"%>
<script>
	function saveNewWagon() {
		var diva = Ext.get('logonPopup');
		var wagonNo = document.getElementById("popupWagonNo").value;
		var params = {"formAction":"<%=WagonDetailAction.FORM_ACTION_SAVE_NEW%>","inputWagonNo":wagonNo};
		diva.load({
			url: 'WagonDetail.do', 
			params: params,
			text:'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true
		});
	} 
</script>
<% if (request.getAttribute("reload")!= null){%>
<script>
	window.location.href = '<html:rewrite action="/WagonDetailEntry.do?formAction=copied"/>'; 
</script>
<%}

else{
%>

<logic:present name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
<script>
	Ext.onReady(function(){
		document.getElementById("popupWagonNo").value = "<c:out value="${admin_WagonDetailForm.inputWagonNo}"/>";
	});
</script>
</logic:present>

<script>
	onPopupReady(function() {
		var el = document.getElementById('popupWagonNo');
		el.focus();
	});
</script>

<table class="classicblue" style="border:none; width:auto">
<tr>
	<th>
		<fmt:message key="wagondetail.text.savenewwagon"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%">
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="wagon.wagonno"/></td>
                              <td class="content"  style="white-space:nowrap;border:0px;">
                              	<input type="text" name="popupWagonNo" id="popupWagonNo" value="<c:out value="${admin_WagonDetailForm.inputWagonNo}"/>" maxlength="30"/>
                              </td>
                            </tr>

				<tr>
			<td colspan="2" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveNewWagon()"/>
			</td>
		</tr>
	</table>
	</td>
</table>
<%} %>