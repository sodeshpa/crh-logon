<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<table style="border:0px;margin:0 px;" class="classic">

		<c:forEach var="damageCdEntity" varStatus="status"
		items="${admin_WagonDetailForm.wagonDetailMaintenance.damageCodeTxList}">
			<tr class="zebra<c:out value="${status.count % 2}"/>">
				<td style="width: 100%; overflow: hidden;">
				<c:out value="${damageCdEntity.damagecodeTxt}"/>&nbsp;<c:out value="${damageCdEntity.txt}"/></td>
				<td valign="middle" style="width: 1px"><nobr><a
					href="javascript:removeDamageCd(<c:out value="${damageCdEntity.damagecodeTxPK.damagecodeBs.damagecodeCd}"/>)"><img
					src="<crh-common:url value="/images/delete2.png" />"/></a> </nobr></td>
			</tr>
	</c:forEach>

	
</table>    
