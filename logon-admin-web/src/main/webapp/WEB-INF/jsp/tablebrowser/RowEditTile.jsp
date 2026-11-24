<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RowEditTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.actions.tablebrowser.RowEditAction"%>

<script type="text/javascript"> 
document.onkeydown = function(e) {
	var targ;
	var code;
	if (!e) var e = window.event;

	if (e.keyCode) code = e.keyCode;
	else if (e.which) code = e.which;


	if (code == 13) {// 13 = the code for pressing ENTER
		if (e.target) targ = e.target;
		else if (e.srcElement) targ = e.srcElement;
		if (targ.nodeType == 3) // defeat Safari bug
			targ = targ.parentNode;

		if (targ != null) {
				if(targ.type == "text") {
					if (targ.className != null && targ.className.substr(0,2) == 'x-') {
						//do nothing for ext text boxes
					}
					else {
						return false;
					}
				}
				if (targ.type == "checkbox") {
					return false;
				}
		}
	}
}
</script>
<c:if test="${admin_RowEditForm.row != null}">
<jsp:include page="../SubTab10Tile.jsp"/>  
	<div class="labelblue"><c:out value="${admin_RowEditForm.table.name}"/></div>
	<html:form action="/tablebrowser/RowEdit" enctype="multipart/form-data">

	<table width="100%">
	<tr><td>
	<table class="big_form2">
	<th colspan="2"></th>
	<th class=formtitle><bean:message key="tablebrowser.type"/></th>
	<th class=formtitle><bean:message key="tablebrowser.nullable"/></th>
	<th class=formtitle><bean:message key="tablebrowser.comments"/></th>
	<c:forEach var="field" items="${admin_RowEditForm.table.fields}" varStatus="status">
		<c:set var="value" value="${admin_RowEditForm.row[status.count-1]}"/>
		<tr>
			<td class=formtitle nowrap="nowrap">
				<c:out value="${field.name}"/>
				<c:if test='${field.columnInfo.nullable=="N"}'>&nbsp*</c:if>
			</td>
			<td width="20%">
				<c:if test="${value.editable}">
					<c:if test="${value.type.jspEditor == null}">
						<c:set var="displayValue" value="${value.webValue}"/>
						<c:if test="${!admin_RowEditForm.table.refTable and field.key and field.sequenceName != null}">
							<c:set var="displayValue"><%=RowEditAction.GENERATED_VALUE%></c:set>
						</c:if>
							<c:if test="${field.columnInfo.charLength==0}">
								<html:text property="value[${status.count-1}]" value="${displayValue}" styleClass="textfield"/>
							</c:if>
							<c:if test="${field.columnInfo.charLength!=0}">
								<html:text property="value[${status.count-1}]" value="${displayValue}" styleClass="textfield" maxlength="${field.columnInfo.charLength}"/>
							</c:if>
					</c:if>
					<c:if test="${value.type.jspEditor != null}">
						<c:set var="table" scope="request" value="${admin_RowEditForm.table}"/>
						<c:set var="valueNr" scope="request" value="${status.count - 1}"/>
						<c:set var="value" scope="request" value="${value}"/>
						<c:set var="row" scope="request" value="${admin_RowEditForm.row}"/>
						<c:set var="nullable" scope="request" value="${field.columnInfo.nullable=='Y'}"/>
						<c:set var="mimeMap" scope="request" value="${admin_RowEditForm.mimeMap}"/>
						<jsp:include page="<%= ((com.crh.logon.admin.web.beans.ValueBean)pageContext.getAttribute("value")).getType().getJspEditor() %>"/>
					</c:if>
				</c:if>
				<c:if test="${!value.editable}">
					<c:out value="${value.webValue}"/>
				</c:if>
			</td>
			<td>
				<c:out value="${field.columnInfo.dataType}"/>
			</td>
			<td>
				<c:out value="${field.columnInfo.nullable}"/>
			</td>
			<td width="80%">
				<c:out value="${field.columnInfo.comments}"/>
			</td>
		</tr>

	</c:forEach>		
	</table>
	</td></tr>
	<tr><td class="buttons">
		<html:submit value="Abbrechen" property="cancel" styleClass="ViewButtonStyle" ></html:submit>
		<html:submit value="Speichern" property="save" styleClass="ViewButtonStyle" ></html:submit>
	</td></tr>
	</table>
	
	</html:form>
		
</c:if>

<c:if test="${admin_RowEditForm.row == null}">
	<bean:message key="tablebrowser.noTableSelected"/>
</c:if>

<%-- END $RCSfile: RowEditTile.jsp,v $ --%>
