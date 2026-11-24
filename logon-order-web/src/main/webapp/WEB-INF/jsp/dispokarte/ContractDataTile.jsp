<%@ include file="../includeTLDs.jsp" %>
<html:form action="dispokarte/Dispokarte.do" styleId="ShipToForm" onsubmit="return false;">
<html:hidden property="formAction" value=""/>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>
<%@page import="com.crh.logon.order.web.forms.dispokarte.DispokarteForm"%>

<script type="text/javascript">
var texts = {
	loading: '<fmt:message key="extjs.loading"/>',
	select: '<fmt:message key="extjs.select"/>',
	pagerDisplayMsg: '<fmt:message key="extjsgrid.pager.displayMsg"/>',
	pagerEmptyMsg: '<fmt:message key="salesarea.pager.emptyMsg"/>',
	functions: '<fmt:message key="salesarea.grid.functions"/>',
	
	sapContractNo: '<fmt:message key="sapContract.grid.sapContractNo"/>',
	sapContractTypeCd: '<fmt:message key="sapContract.grid.sapContractTypeCd"/>',
	sareaDisplayLongname: '<fmt:message key="sapContract.grid.sareaDisplayLongname"/>',	
	soldtoSapId: '<fmt:message key="sapContract.grid.soldtoSapId"/>',
	soldtoName: '<fmt:message key="sapContract.grid.soldtoName"/>',
	shiptoSapId: '<fmt:message key="sapContract.grid.shiptoSapId"/>',
	shiptoName: '<fmt:message key="sapContract.grid.shiptoName"/>',
	billtoSapId: '<fmt:message key="sapContract.grid.billtoSapId"/>',
	billtoName: '<fmt:message key="sapContract.grid.billtoName"/>',
	payerSapId: '<fmt:message key="sapContract.grid.payerSapId"/>',
	payerName: '<fmt:message key="sapContract.grid.payerName"/>',	
	validFromDat: '<fmt:message key="sapContract.grid.validFromDat"/>',
	validUntilDat: '<fmt:message key="sapContract.grid.validUntilDat"/>',
	validUntilIgnoreFlag: '<fmt:message key="sapContract.grid.validUntilIgnoreFlag"/>',
	sapShippingCondCd: '<fmt:message key="sapContract.grid.sapShippingCondCd"/>',
	custPurchaseOrderNo: '<fmt:message key="sapContract.grid.custPurchaseOrderNo"/>',
	quantityControlFlag: '<fmt:message key="sapContract.grid.quantityControlFlag"/>',
	currencyCd: '<fmt:message key="sapContract.grid.currencyCd"/>',
	creditControlAreaCd: '<fmt:message key="sapContract.grid.creditControlAreaCd"/>',
	creditLimitAccountNo: '<fmt:message key="sapContract.grid.creditLimitAccountNo"/>',
	customerCreditGroup: '<fmt:message key="sapContract.grid.customerCreditGroup"/>',
	creditManagementGroup: '<fmt:message key="sapContract.grid.creditManagementGroup"/>',
	creditRiskCategory: '<fmt:message key="sapContract.grid.creditRiskCategory"/>',
	creditCurrencyCd: '<fmt:message key="sapContract.grid.creditCurrencyCd"/>',
	salesDistrictCd: '<fmt:message key="sapContract.grid.salesDistrictCd"/>',
	incotermsCd: '<fmt:message key="sapContract.grid.incotermsCd"/>',
	ctlCreTs: '<fmt:message key="sapContract.grid.ctlCreTs"/>',
	ctlModTs: '<fmt:message key="sapContract.grid.ctlModTs"/>',
	validationCode: '<fmt:message key="sapContract.grid.validationCode"/>',
 	contractDataGridTitle: '<fmt:message key="sapContractItem.grid.contractDataGridTitle"/>',

 	contractItemNo: '<fmt:message key="sapContractItem.grid.contractItemNo"/>',
 	materialTxt: '<fmt:message key="sapContractItem.grid.materialTxt"/>',
 	prodPlantName: '<fmt:message key="sapContractItem.grid.prodPlantName"/>',
 	supplyPlantName: '<fmt:message key="sapContractItem.grid.supplyPlantName"/>',
 	materialGroup1: '<fmt:message key="sapContractItem.grid.materialGroup1"/>',
 	materialGroup2: '<fmt:message key="sapContractItem.grid.materialGroup2"/>',
 	materialGroup3: '<fmt:message key="sapContractItem.grid.materialGroup3"/>',
 	materialGroup4: '<fmt:message key="sapContractItem.grid.materialGroup4"/>',
 	materialGroup5: '<fmt:message key="sapContractItem.grid.materialGroup5"/>',
 	itemCategory: '<fmt:message key="sapContractItem.grid.itemCategory"/>',
 	targetQuantity: '<fmt:message key="sapContractItem.grid.targetQuantity"/>',
 	targetQuantityUomCd: '<fmt:message key="sapContractItem.grid.targetQuantityUomCd"/>',
 	netValue: '<fmt:message key="sapContractItem.grid.netValue"/>',
 	ctlCreTs: '<fmt:message key="sapContractItem.grid.ctlCreTs"/>',
 	ctlModTs: '<fmt:message key="sapContractItem.grid.ctlModTs"/>',
 	contractItemGridTitle: '<fmt:message key="sapContractItem.grid.contractItemGridTitle"/>'

};

	var selectedSoldTo = <c:out value="${order_dispokarte_DispokarteForm.soldTo.sapBpId}"/>;
	var selectedShipTo = <c:out value="${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.sapBpId}"/>;
</script>
<script type="text/javascript" src="<crh-common:url value='/js/contractData.js'/>"></script>
<div id="divContracts" style="padding: 0px; margin: 20px 0px 0px;">
		<input onclick="changeOnlyActiveContractCheckboxState()" value="on"	id="showOnlyActiveContracts" type="checkbox" checked />
		<fmt:message key="dispokarte.texte.contract.data.show.only.active.contracts"/>
</div>

<br/>
<div id="divContractItems" style="padding: 0px; margin: 20px 0px 0px;"></div>
<br/>
<div id="divContractItemData" style="padding: 0px; margin: 20px 0px 0px;"></div>
</html:form>
