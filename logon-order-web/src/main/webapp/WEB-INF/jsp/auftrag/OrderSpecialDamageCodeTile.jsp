<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<table style="border:0px;margin:0 px;" class="classic">

		<c:forEach var="damageCdEntity" varStatus="status"
		items="${order_auftrag_OrderSpecialForm.damageCodeTxList}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
				<td style="width: 100%; overflow: hidden;">
				<c:out value="${damageCdEntity.damagecodeTxt}"/>&nbsp;<c:out value="${damageCdEntity.txt}"/></td>
				<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
				<td valign="middle" style="width: 1px"><nobr><a
					href="javascript:removeDamageCd(<c:out value="${damageCdEntity.damagecodeTxPK.damagecodeBs.damagecodeCd}"/>)"><img
					src="<crh-common:url value="/images/delete2.png" />"/></a> </nobr></td>
				</crh-common:authorize>
			</tr>
	</c:forEach>

	
</table>    

