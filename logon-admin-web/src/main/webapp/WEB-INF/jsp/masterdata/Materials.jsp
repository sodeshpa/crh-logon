<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: Materials.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp"%>
<!--<html:form action="ShippingPoint.do" onsubmit="return false;" >-->
<table class="classic" style="border: none">

<c:set var="counter" value="1" />
<c:forEach var="material" varStatus="status"
	items="${admin_ShippingPointForm.materialList}">
	<c:if test="${admin_ShippingPointForm.loadingPointWrapperList[admin_ShippingPointForm.currLoadingPointIndex].packageTypeCd == material.logonPackageTypeCd}">
		<tr class="zebra<c:out value="${counter % 2}"/>">
			<td valign="middle" style="width: 1px"><html:checkbox
				property="loadingPointWrapperList[${admin_ShippingPointForm.currLoadingPointIndex}].materialRestriction[${status.count - 1}]"
				value="true" /></td>
			<td><c:out value="${material.display}" /></td>
		</tr>
		<c:set var="counter" value="${counter + 1}" />
	</c:if>
</c:forEach>
</table>
<!--</html:form>-->