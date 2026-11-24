<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: Ansprechpartner.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<c:choose>
	<c:when test="${param.referrer == 'order'}">
		<c:set var="mode" value="order"/>
		<c:set var="targetForm" value="${order_bestellung_BestellungForm}"/>
	</c:when>
	<c:otherwise>
		<c:set var="mode" value="dispokarte"/>
		<c:set var="targetForm" value="${order_dispokarte_DispokarteForm}"/>
	</c:otherwise>
</c:choose>

<script type="text/javascript">

	function deleteAnsprechpartner(id){
		var diva = Ext.get('DivAnsprechpartner');
		diva.load({
			url: '../dispokarte/Ansprechpartner.do', // <-- change if necessary
			params: {'delete': '', 'id': id, 'referrer': '<c:out value="${param.referrer}"/>'},
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
		diva.show();
	}
</script>

<table class="classic" style="border:none">

<c:set var="selectedPerson" value="${targetForm.selectedContactPersonId}"/>

<c:forEach var="partner" varStatus="status" items="${targetForm.shipTo.shiptoPersonList}">
<tr class="zebra<c:out value="${status.count % 2}"/>">
<td>
	<c:choose>
		<c:when test='${ (selectedPerson != null) && (selectedPerson != -1) && (selectedPerson == partner.person.personId)}'>
			<c:set var="checkedAttr" value=" checked "/>
		</c:when>
		<c:otherwise>
			<c:set var="checkedAttr" value=""/>
		</c:otherwise>
	</c:choose>
	<c:if test="${mode == 'order'}">
		<input type="radio" name="selectedContactPersonId" value="<c:out value="${partner.person.personId}"/>" onClick='switchContactPerson("<c:out value="${partner.person.name}"/>", "<c:out value="${partner.person.firstname}"/>")' <c:out value="${checkedAttr}"/>/>
	</c:if>
	<c:if test="${mode == 'dispokarte'}">
		<input type="radio" name="selectedContactPersonId" value="<c:out value="${partner.person.personId}"/>" <c:out value="${checkedAttr}"/>/>
	</c:if>
</td>
<td width="250" style='table-layout:fixed'><c:out value="${partner.person.firstname}"/> <c:out value="${partner.person.name}"/></td>
<td width="100" style='table-layout:fixed'><c:out value="${partner.function}"/></td>
<td width="100" style='table-layout:fixed'><c:out value="${partner.person.phoneNr}"/></td>
<td valign="middle" style="width:1px"><nobr>
	<a href="javascript:deleteAnsprechpartner(<c:out value="${status.count-1}"/>)" title="<fmt:message key="buttons.delete"/>"><img src="<crh-common:url value='/images/silk/user_delete.png'/>"/></a>
	<a href="javascript:showPopup('../dispokarte/AnsprechpartnerEntry.do?referrer=<c:out value="${param.referrer}"/>&action=edit&id=<c:out value="${status.count-1}"/>','')" title="<fmt:message key="buttons.edit"/>"><img src="<crh-common:url value='/images/silk/user_edit.png'/>" style="margin-left: 3px"/></a>
	</nobr>
</td>
</tr>
</c:forEach>
</table>