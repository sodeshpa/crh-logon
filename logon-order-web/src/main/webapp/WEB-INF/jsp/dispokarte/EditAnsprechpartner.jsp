<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: EditAnsprechpartner.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>
 
 <%@page import="com.crh.logon.persistence.entities.common.ShiptoPersonEntity"%>
 <%@page import="com.crh.logon.persistence.entities.common.PersonEntity"%>
<c:choose>
	<c:when test="${param.referrer == 'order'}">
		<c:set var="targetForm" value="${order_bestellung_BestellungForm}"/>
		<c:set var="okButtonMsgKey" value="buttons.save"/>
	</c:when>
	<c:otherwise>
		<c:set var="targetForm" value="${order_dispokarte_DispokarteForm}"/>
		<c:set var="okButtonMsgKey" value="buttons.ok"/>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
	onPopupReady(function() {

		var el = document.getElementById('searchAnsprechpartnerName');
		if (!el) {
			el = document.getElementById('fldAnsprechpartnerName');
		}
		if (el) {
			el.focus();
		}
	});
	
	function saveAnsprechpartner(){

		params = {'save': '', 'id': <c:out value="${order_dispokarte_AnsprechpartnerEditForm.id}"/>, 'referrer': '<c:out value="${order_dispokarte_AnsprechpartnerEditForm.referrer}"/>'};
		addFormElements(params, "AnsprchpartnerForm");

		hidePopup();
		var diva = Ext.get('DivAnsprechpartner');
		diva.load({
			url: '../dispokarte/Ansprechpartner.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
		diva.show();
	}
	
	function submitSearch(){
		params = {'search_ansprechPartner': '', 'id': <c:out value="${order_dispokarte_AnsprechpartnerEditForm.id}"/>, 'referrer': '<c:out value="${order_dispokarte_AnsprechpartnerEditForm.referrer}"/>'};
		addFormElements(params, "AnsprchpartnerForm");
		
		//var diva = Ext.get('DivAnsprechpartnerPopup');
		var diva = Ext.get('logonPopup');
		diva.load({
			url: '../dispokarte/Ansprechpartner.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
		diva.show();

	}
	
	function submitAdd(id){
		params = {'add': '', 'id': id, 'referrer': '<c:out value="${order_dispokarte_AnsprechpartnerEditForm.referrer}"/>'};
		addFormElements(params, "AnsprchpartnerForm");
		
		/*divAnsprechpartnerPopup = Ext.get('DivAnsprechpartnerPopup');
		divAnsprechpartnerPopup.hide();
		unGrayoutScreen();*/	

		hidePopup();
		var diva = Ext.get('DivAnsprechpartner');
		diva.load({
			url: '../dispokarte/Ansprechpartner.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
		diva.show();

	}
</script>

<div id="DivAnsprechpartnerPopup">
<html:form action="dispokarte/Ansprechpartner.do" onsubmit="return false;" styleId="AnsprechpartnerForm">
<html:hidden property="referrer"/>
<table class="classicblue" style="border:none; width:auto; border-collapse: collapse">
<tr>
	<th>
		<fmt:message key="dispokarte.texte.ansprechpartner"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
		<c:if test='${order_dispokarte_AnsprechpartnerEditForm.newContact}'>
			<table width="100%">
				<tr>
					<td class="label" colspan="2">
						<fmt:message key="dispokarte.texte.searchAnsprechpartner"/>
					</td>
					<td colspan="4">
					</td>
				</tr>
				<tr>
					<td class="label right"><fmt:message key="fields.ansprechpartner.name"/>:</td>
					<td><html:text style="width:200px;" property="searchName" styleId="searchAnsprechpartnerName"/> </td>
					<td class="label right"><fmt:message key="fields.ansprechpartner.firstname"/>:</td>
					<td><html:text style="width:200px;" property="searchFirstName" styleId="searchAnsprechpartnerFirstName"/> </td>
				</tr>
				<tr>
					<td colspan="6" class="buttons right">
						<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.search"/>" onClick="submitSearch()">   				
					</td>
				</tr>
			</table>
	
			<div class="scrollist" id="DivSilobelegung" style="height: 70px;">
				<table class="classic" style="border:none">
					<th>
						<fmt:message key="fields.ansprechpartner.name"/>
					</th>
					<th>
						<fmt:message key="fields.ansprechpartner.firstname"/>
					</th>
					<th>
						<fmt:message key="fields.ansprechpartner.telefon"/>
					</th>
					<th>
					</th>
					<c:forEach var="searchResult" varStatus="searchStatus" items="${order_dispokarte_AnsprechpartnerEditForm.searchResult}">
					<tr class="zebra<c:out value="${status.count % 2}"/>">
						<td><c:out value="${searchResult.name}"/></td>
						<td><c:out value="${searchResult.firstname}"/></td>
						<td><c:out value="${searchResult.phoneNr}"/></td>
						<td><input type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onClick="submitAdd(<c:out value="${searchStatus.index}"/>)"</td>
					</tr>
					</c:forEach>
				</table>
			</div>
			
			<br>
			<br>
	
	</c:if>
		
		<table width="100%">
			<c:if test='${order_dispokarte_AnsprechpartnerEditForm.newContact}'>
			<tr>
				<td class="label" colspan="2">
						<fmt:message key="dispokarte.texte.createAnsprechpartner"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<td class="label right"><fmt:message key="fields.ansprechpartner.name"/>:</td>
				<td><html:text style="width:200px;" maxlength="<%=PersonEntity.DBSIZE_NAME + ""%>"  property="contactperson.person.name" styleId="fldAnsprechpartnerName"/> </td>
			</tr>
			<tr>
				<td class="label right"><fmt:message key="fields.ansprechpartner.firstname"/>:</td>
				<td><html:text style="width:200px;" maxlength="<%=PersonEntity.DBSIZE_FIRSTNAME + ""%>" property="contactperson.person.firstname" styleId="fldAnsprechpartnerFirstName"/> </td>
			</tr>
			<tr>
				<td class="label right"><fmt:message key="fields.ansprechpartner.funktion"/>:</td>
				<td><html:text style="width:200px;" maxlength="<%=ShiptoPersonEntity.DBSIZE_FUNCTION + ""%>" property="contactperson.function" styleId="fldAnsprechpartnerFunktion"/> </td>
			</tr>
			<tr>
				<td class="label right"><fmt:message key="fields.ansprechpartner.telefon"/>:</td>
				<td><html:text style="width:200px;" maxlength="<%=PersonEntity.DBSIZE_PHONE_NR + ""%>" property="contactperson.person.phoneNr" styleId="fldAnsprechpartnerTelefon"/> </td>
			</tr>
			<tr>
				<td colspan="2" class="buttons right">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<c:if test='${order_dispokarte_AnsprechpartnerEditForm.newContact}'>
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="dispokarte.buttons.addnewcontact"/>" onclick="saveAnsprechpartner()"/>
				</c:if>
				<c:if test='${!order_dispokarte_AnsprechpartnerEditForm.newContact}'>
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="${okButtonMsgKey}"/>" onclick="saveAnsprechpartner()"/>				
				</c:if>									
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
</html:form>
</div>
