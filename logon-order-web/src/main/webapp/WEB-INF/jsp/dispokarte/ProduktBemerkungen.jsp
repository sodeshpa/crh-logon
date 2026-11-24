<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ProduktBemerkungen.jsp,v $ --%>
<%@page import="com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity"%>
<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">

var texts = {		
		productOrderNote: '<fmt:message key="dispokarte.texte.auftragsmanagement"/>',
		productDeliveryNote: '<fmt:message key="dispokarte.texte.zusatzlieferschein"/>',
		productDispatchingNote: '<fmt:message key="dispokarte.texte.poolspediteur"/>',
		productTruckNote: '<fmt:message key="dispokarte.texte.chauffeur"/>',
		productRailwayNote: '<fmt:message key="dispokarte.texte.bahndisposition"/>'
};
	onPopupReady(function() {
	
		var el = document.getElementById('fldproductOrderNote');
		if (el) {
			el.focus();
		}
	});
	
	function saveBemerkungen(){
		if (!checkComments()){
			return false;
		}
		
			a = {"save": ""};
			addFormElements(a, "DispokarteForm");
	
			showPopup('ProduktBemerkungenAction.do', null, a);
	}

	function checkComments(){
		
		var confirmCommentTrim = checkCommentLength("fldproductOrderNote",'<fmt:message key="confirm.trim.textarealength"/>',texts.productOrderNote,<%=ShiptoSareaRestrEntity.DBSIZE_NOTE_ORDER_MGMT + ""%>);
		if (!confirmCommentTrim) {
			return false;
		}
		confirmCommentTrim = checkCommentLength("fldProductDeliveryNote",'<fmt:message key="confirm.trim.textarealength"/>',texts.productDeliveryNote,<%=ShiptoSareaRestrEntity.DBSIZE_NOTE_DELIVERY + ""%>);
		if (!confirmCommentTrim) {
			return;
		}	
		confirmCommentTrim = checkCommentLength("fldProductDispatchingNote",'<fmt:message key="confirm.trim.textarealength"/>',texts.productDispatchingNote,<%=ShiptoSareaRestrEntity.DBSIZE_NOTE_DISPATCHING + ""%>);
		if (!confirmCommentTrim) {
			return;
		}
		confirmCommentTrim = checkCommentLength("fldProductTruckNote",'<fmt:message key="confirm.trim.textarealength"/>',texts.productTruckNote,<%=ShiptoSareaRestrEntity.DBSIZE_NOTE_TRUCK_DRIVER + ""%>);
		if (!confirmCommentTrim) {
			return false;
		}
		confirmCommentTrim = checkCommentLength("fldProductRailwayNote",'<fmt:message key="confirm.trim.textarealength"/>',texts.productRailwayNote,<%=ShiptoSareaRestrEntity.DBSIZE_NOTE_RAILWAY_DISP + ""%>);
		if (!confirmCommentTrim) {
			return false;
		}
		return true;
	}
</script>

<div id="DivProduktBemerkungenPopup">
<html:form action="dispokarte/ProduktBemerkungenAction.do" onsubmit="return false;" styleId="DispokarteForm">
<table class="classicblue" style="border:none; width:auto; border-collapse: collapse">
<tr>
	<th>
		<fmt:message key="dispokarte.texte.bemerkungenFull"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
		<table width="100%">
			<tr>
				<td class="label right"><fmt:message key="dispokarte.texte.auftragsmanagement"/>:</td>
				<c:set var="textarealength"><%=ShiptoSareaRestrEntity.DBSIZE_NOTE_ORDER_MGMT + ""%></c:set>
				<td><html:textarea cols="30" onkeypress="return checkMaxLength(event, this, ${textarealength})" rows="5" property="productOrderNote" styleId="fldproductOrderNote"></html:textarea></td>
			</tr>
			<tr>
				<td class="label right"><fmt:message key="dispokarte.texte.zusatzlieferschein"/>:</td>
				<c:set var="textarealength"><%=ShiptoSareaRestrEntity.DBSIZE_NOTE_DELIVERY + ""%></c:set>
				<td><html:textarea cols="30" onkeypress="return checkMaxLength(event, this, ${textarealength})" rows="5" property="productDeliveryNote" styleId="fldProductDeliveryNote"></html:textarea> </td>
			</tr>
			<tr>
				<td class="label right"><fmt:message key="dispokarte.texte.poolspediteur"/>:</td>
				<c:set var="textarealength"><%=ShiptoSareaRestrEntity.DBSIZE_NOTE_DISPATCHING + ""%></c:set>
				<td><html:textarea cols="30" onkeypress="return checkMaxLength(event, this, ${textarealength})" rows="5" property="productDispatchingNote" styleId="fldProductDispatchingNote"></html:textarea> </td>
			</tr>
			<tr>
				<td class="label right"><fmt:message key="dispokarte.texte.chauffeur"/>:</td>
				<c:set var="textarealength"><%=ShiptoSareaRestrEntity.DBSIZE_NOTE_TRUCK_DRIVER + ""%></c:set>
				<td><html:textarea cols="30" onkeypress="return checkMaxLength(event, this, ${textarealength})" rows="5" property="productTruckNote" styleId="fldProductTruckNote"></html:textarea> </td>
			</tr>
			<tr>
				<td class="label right"><fmt:message key="dispokarte.texte.bahndisposition"/>:</td>
				<c:set var="textarealength"><%=ShiptoSareaRestrEntity.DBSIZE_NOTE_RAILWAY_DISP + ""%></c:set>
				<td><html:textarea cols="30" onkeypress="return checkMaxLength(event, this, ${textarealength})" rows="5" property="productRailwayNote" styleId="fldProductRailwayNote"></html:textarea> </td>
			</tr>
			<tr>
				<td colspan="2" class="buttons right">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>   				
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.ok"/>" onclick="saveBemerkungen()"/>   				
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
</html:form>
</div>
