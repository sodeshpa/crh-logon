<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ReferenceEditor.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.constants.Constant"%><c:if test="${!admin_RowEditForm.table.fields[valueNr].type.reference.tableMeta.useDropdown}">
<c:set var="textName" value="valueSpan${valueNr}" />
<c:set var="fieldName" value="valueField${valueNr}" />
<table>
<tr>
<td width="100%" id="<c:out value="${textName}"/>">
<c:out value="${value.webValue}"/>&nbsp;
</td>
<td>
<script type="text/javascript">
	function setValue<c:out value="${valueNr}"/>(value, webValue){
		document.getElementById('<c:out value="${textName}"/>').innerHTML = webValue;
		document.getElementById('<c:out value="${fieldName}"/>').value = value;
	}
</script>
<html:hidden property="value[${valueNr}]" styleId="${fieldName}" value="${value.value}"/>
 <!-- <input type="button" onclick="window.open('<html:rewrite page="/tablebrowser/ReferenceSearchEntry.do?refTable=${value.type.reference.name}&setter=setValue${valueNr}"/>')" value="..."/> -->
 <nobr><input type="button" onclick="showPopup('ReferenceSearchEntry.do?refTable=<c:out value="${value.type.reference.name}"/>&setter=setValue<c:out value="${valueNr}"/>','')" value="..."/>
 <c:if test='${admin_RowEditForm.table.fields[valueNr].columnInfo.nullable=="Y"}'>
 	<img src="<crh-common:url value='/images/delete2.png'/>" onclick="setValue<c:out value='${valueNr}'/>('','')" title="Clear reference"/>
 </c:if>
 </nobr>
</td>
</tr>
</table>
</c:if>
<c:if test="${admin_RowEditForm.table.fields[valueNr].type.reference.tableMeta.useDropdown}">
<html:select size="1" property="value[${valueNr}]" value="${value.value}">
	<c:if test='${admin_RowEditForm.table.fields[valueNr].columnInfo.nullable=="Y"}'>
		<html:option value="">-</html:option>	
	</c:if>
	<c:forEach var="row" items="${admin_RowEditForm.optionValues[valueNr]}">
		<html:option value="${row[0]}"><c:out value="${row[1]}"/></html:option>
	</c:forEach>
</html:select>
</c:if>