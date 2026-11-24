<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderSpecialMaintTypeList.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderSpecialForm}" />

<div class="scrollist">
<table class="classic" style="border: none">

<c:set var="counter" value="1" />
<c:forEach var="entity" varStatus="status"
	items="${order_auftrag_OrderSpecialForm.maintTypeTxEntityMap}">
		<tr class="zebra<c:out value="${counter % 2}"/>">
			<td valign="middle" style="width: 1px"><html:checkbox onclick="javascript:updateEmptyMaterial()"
				property="maintTypeBs[${status.count - 1}]"
				value="true" /></td>
			<td><c:out value="${entity.value.maintTypeTxPK.maintTypeBs.maintTypeCd}" /></td>
			<td><c:out value="${entity.value.txt}" /></td>
		</tr>
		<c:set var="counter" value="${counter + 1}" />
	
</c:forEach>
</table>
</div>
<div style="margin-left: 7px; margin-top: 4px;">
<html:checkbox property="maintTypeReturnFlag" value="true" /> <nobr><fmt:message key="orderspecial.text.retrun.from.maint" /></nobr>
</div>
