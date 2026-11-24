<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.business.ordercreation.dto.DeliverySettingDTO"%>

<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>

<c:set var="DELIVERY_SETTING_BASED_ON_CONTRACT"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT%></c:set>
<c:set var="DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION%></c:set>
<c:set var="DELIVERY_SETTING_BASED_ON_RESTRICTION"><%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_RESTRICTION%></c:set>
<c:set var="DELIVERY_SETTING_NOT_DEFINED"><%=DeliverySettingDTO.DELIVERY_SETTING_NOT_DEFINED%></c:set>

<c:set var="RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL"><%=DeliverySettingDTO.RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL%></c:set>
<c:set var="RESOLUTION_EXPLICIT_IN_CONTRACT"><%=DeliverySettingDTO.RESOLUTION_EXPLICIT_IN_CONTRACT%></c:set>
<c:set var="RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL"><%=DeliverySettingDTO.RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL%></c:set>
<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />
<script type="text/javascript">
function selectService(compositeIdentifier) {
	var diva = Ext.get('TabsDiv');
	diva.load( {
		url :'<c:url value="${orderDetailUrl}"/>',
		params : {
			'formAction' :'<%=OrderDetailAction.FORM_ACTION_ADD_SERVICE_MATERIAL%>',
			'servicesExtraCostBean.selectedServiceCompositeIdentifier' : compositeIdentifier
			},
		text : '<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts : true
	});
}

Ext.onReady(function() {
	var serviceComboTemplate = new Ext.XTemplate(
		'<table class="classicblue" style="width:100%"><thead><tr>',
		'<th><fmt:message key="bestellung.materialcontractcombo.material"/></th>',
		'<th><fmt:message key="bestellung.materialcontractcombo.contract"/></th>',
		'<th><fmt:message key="bestellung.materialcontractcombo.packagetype"/></th>',
		'<th><fmt:message key="bestellung.materialcontractcombo.quantity"/></th>',
		'<th><fmt:message key="bestellung.materialcontractcombo.uom"/></th>',
		'<th><fmt:message key="bestellung.materialcontractcombo.contractMaterial"/></th>',
		'<th><fmt:message key="bestellung.materialcontractcombo.basedOn"/></th>',
		'</tr></thead><tbody>',
		'<tpl for=".">',
		'<tr class="listitem zebra{[xindex % 2]}">',
		'<td><tpl if="!this.isNotAvailable(sapMaterialId)">{txt} ({sapMaterialNo})</tpl></td>',
		'<td><tpl if="!this.isNotAvailable(sapContractId)">{sapContractNo} ({contractItemNo})</tpl></td>',
		'<td><tpl if="!this.isNotAvailable(materialPackageTxt)">{materialPackageTxt}</tpl></td>',
		'<td><tpl if="!this.isNotAvailable(quantity)">{quantity}</tpl></td>',
		'<td><tpl if="!this.isNotAvailable(uomCd)">{uomCd}</tpl></td>',
		'<td>{[this.getResolutionOfMaterialText(values.resolutionType)]}<tpl if="!this.isNotAvailable(contractSapMaterialTxt)"> ({contractSapMaterialTxt})</tpl> </td>',
		'<td>{[this.getBasedOnText(values.deliverySettingType)]}</td>',
		'</tr>',
		'</tpl>',
		'</tbody></table>',
		{
			compiled : true,
			disableFormats : true,
			isNotAvailable : function(val) {
				return val == null;
			},
			getBasedOnText : function(val) {
				if (val == <c:out value="${DELIVERY_SETTING_BASED_ON_CONTRACT}"/>) {
					return '<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedOnContract"/>';
				} else if (val == <c:out value="${DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION}"/>) {
					return '<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedContractCombinedRestriction"/>';
				} else if (val == <c:out value="${DELIVERY_SETTING_BASED_ON_RESTRICTION}"/>) {
					return '<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedOnRestriction"/>';
				} else {
					return '';
				}
			},
			getResolutionOfMaterialText : function(val) {
				if (val == <c:out value="${RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL}"/>) {
					return '<fmt:message key="bestellung.materialcontractcombo.materialResolvedFromPricingGroup"/>';
				} else if (val == <c:out value="${RESOLUTION_EXPLICIT_IN_CONTRACT}"/>) {
					return '<fmt:message key="bestellung.materialcontractcombo.materialExplicitelyDefined"/>';
				} else if (val == <c:out value="${RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL}"/>) {
					return '<fmt:message key="bestellung.materialcontractcombo.materialResolvedFromDivision"/>';
				} else {
					return '';
				}
			}
		}
	);
	serviceCombo = new Ext.ux.MaterialCombo({
		tpl : serviceComboTemplate,
		id : 'serviceComboSelectedCompositeIdentifier',
		name : 'serviceComboSelectedCompositeIdentifier',
		transform : 'serviceComboSelectedCompositeIdentifier',
		emptyText : '<fmt:message key="bestellung.materialcontractcombo.choose"/>',
		width : 200,
		listWidth : 700,
		store : new Ext.data.JsonStore({
			fields : [ 'compositeIdentifier',
				'sapMaterialId', 'txt',
				'quantity', 'uomCd',
				'sapMaterialNo',
				'logonPackageTypeCd',
				'materialPackageCd',
				'sapContractId',
				'sapContractItemId',
				'sapContractNo',
				'contractItemNo',
				'contractSapMaterialTxt',
				'deliverySettingType',
				'resolutionType',
				'searchByText',
				'materialPackageTxt' ],
			data : <c:out value="${form.servicesExtraCostBean.serviceListJSON}" escapeXml="false"/>,
			sortInfo : {
				field : 'txt',
				direction : 'ASC'
			}
		})
	});
	serviceCombo.on('select', function(combo, record) {
		selectService(record.data.compositeIdentifier);
	});
});
</script>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />
	
<div id="serviceMaterialDiv"
	style="height: 300px; margin: 0px; border: 0px" class="subform">
	<span class="labelblue">
		<fmt:message key="orderdetail.services.title" />
	</span>
	<br>
	<jsp:include page="MaterialTabItem.jsp"></jsp:include>
	<input type="text" name="serviceComboSelectedCompositeIdentifier"
		id="serviceComboSelectedCompositeIdentifier" value="" />
</div>

<div style="height: 330px; margin: 0px; border: 0px" class="subform"
	id="extraCostDiv">
	<jsp:include page="OrderExtraCostDiv.jsp"></jsp:include>
</div>
