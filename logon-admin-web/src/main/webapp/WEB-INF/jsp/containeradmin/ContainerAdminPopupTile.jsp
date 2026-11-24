<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ContainerAdminPopupTile.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.persistence.entities.common.ContainerEntity"%>

<% if (request.getAttribute("reload")!= null){%>
<script>
	hidePopup();
	resetContainer('<c:out value="${admin_ContainerAdminForm.containerId }" />');
</script>
<%}

else{
%>

<logic:present name="org.apache.struts.action.ACTION_WARNINGS" scope="request">
<script>
	Ext.onReady(function(){

	});
</script>
</logic:present>

<script>
	onPopupReady(function() {
		var el = document.getElementById('inputContNameIdPopup');
		el.focus();
	});
</script>	


<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${admin_ContainerAdminForm}" />
	
<table class="classicblue" style="border:none; width:auto">
<tr>
	<th>
		<fmt:message key="contadmin.popup.createnew"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%">

			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.name" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px; width:100%;">
				<html:text styleId="inputContNameIdPopup" style="width:100%;" property="inputContainerName"
					maxlength="<%=ContainerEntity.DBSIZE_CONTAINER_NAME + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.no" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px; width:100%;">
				<html:text styleId="inputContNoIdPopup" style="width:100%;" 
					property="inputContainerNo"
					maxlength="<%=ContainerEntity.DBSIZE_CONTAINER_NO + ""%>" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.trsp.cat" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px; width:100%;"><html:select
					styleId="inputContTrspCatIdPopup" property="inputContainerTrspCat"
					style="width:100%; margin:0px; padding:0px;">
					<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
					<c:forEach var="entry"
						items="${admin_ContainerAdminForm.containerTrspCatMap}">
						<html:option value="${entry.key}">
							<c:out value="${entry.value.txt}" />
						</html:option>
					</c:forEach>
				</html:select></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;"><fmt:message
					key="contadmin.label.trsp.type" />:</td>
				<td class="content" style="white-space: nowrap; border: 0px; width:100%;"><html:select
					styleId="inputContTypeIdPopup" property="inputContainerType"
					style="width:100%; margin:0px; padding:0px;">
					<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
					<c:forEach var="entry"
						items="${admin_ContainerAdminForm.containerTypeMap}">
						<html:option value="${entry.key}">
							<c:out value="${entry.value.txt}" />
						</html:option>
					</c:forEach>
				</html:select></td>
			</tr>                          

				<tr>
			<td colspan="2" class="buttons right" style="white-space: nowrap;">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveNewContainer()"/>
			</td>
		</tr>
	</table>
	</td>
</table>
<%}%>