<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.order.web.forms.dispokarte.DispokarteForm"%>

<%@page import="com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity"%>
<%@page import="com.crh.logon.business.ordercreation.dto.DeliverySettingDTO"%>
<%@page import="com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity"%>
<%@page import="java.util.List"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.persistence.entities.sap.SapMaterialEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.PlantBsEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.UomTxEntity"%>
<%@page import="com.crh.logon.order.taglib.DispokarteInvalidAttributeMarkerTag"%>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>

<script type="text/javascript">
var constants = {
	DELIVERY_SETTING_BASED_ON_CONTRACT:<%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT%>,
	DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION:<%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT_COMBINED_RESTRICTION%>,
	DELIVERY_SETTING_BASED_ON_RESTRICTION:<%=DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_RESTRICTION%>,
	DELIVERY_SETTING_NOT_DEFINED:<%=DeliverySettingDTO.DELIVERY_SETTING_NOT_DEFINED%>,
	RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL:<%=DeliverySettingDTO.RESOLUTION_FROM_PRICING_GROUP_TYPE_GENERIC_MATERIAL%>,
	RESOLUTION_EXPLICIT_IN_CONTRACT:<%=DeliverySettingDTO.RESOLUTION_EXPLICIT_IN_CONTRACT%>,
	RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL:<%=DeliverySettingDTO.RESOLUTION_FROM_DIVISION_TYPE_GENERIC_MATERIAL%>
};
var texts = {
	bestellung_materialcontractcombo_material:
		'<fmt:message key="bestellung.materialcontractcombo.material"/>',
	bestellung_materialcontractcombo_contract:
		'<fmt:message key="bestellung.materialcontractcombo.contract"/>',
	bestellung_materialcontractcombo_salesarea:
		'<fmt:message key="bestellung.materialcontractcombo.salesarea"/>',
	bestellung_materialcontractcombo_productionPlant:
		'<fmt:message key="bestellung.materialcontractcombo.productionPlant"/>',
	bestellung_materialcontractcombo_packagetype:
		'<fmt:message key="bestellung.materialcontractcombo.packagetype"/>',
	bestellung_materialcontractcombo_quantity:
		'<fmt:message key="bestellung.materialcontractcombo.quantity"/>',
	bestellung_materialcontractcombo_uom:
		'<fmt:message key="bestellung.materialcontractcombo.uom"/>',
	bestellung_materialcontractcombo_deliveryType:
		'<fmt:message key="bestellung.materialcontractcombo.deliveryType"/>',
	bestellung_materialcontractcombo_supplyPlant:
		'<fmt:message key="bestellung.materialcontractcombo.supplyPlant"/>',
	bestellung_materialcontractcombo_billto:
		'<fmt:message key="bestellung.materialcontractcombo.billto"/>',
	bestellung_materialcontractcombo_payer:
		'<fmt:message key="bestellung.materialcontractcombo.payer"/>',
	bestellung_materialcontractcombo_contractMaterial:
		'<fmt:message key="bestellung.materialcontractcombo.contractMaterial"/>',
	bestellung_materialcontractcombo_basedOn:
		'<fmt:message key="bestellung.materialcontractcombo.basedOn"/>',
	bestellung_materialcontractcombo_deliverySettingBasedOnContract:
		'<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedOnContract"/>',
	bestellung_materialcontractcombo_deliverySettingBasedContractCombinedRestriction:
		'<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedContractCombinedRestriction"/>',
	bestellung_materialcontractcombo_deliverySettingBasedOnRestriction:
		'<fmt:message key="bestellung.materialcontractcombo.deliverySettingBasedOnRestriction"/>',
	bestellung_materialcontractcombo_materialResolvedFromPricingGroup:
		'<fmt:message key="bestellung.materialcontractcombo.materialResolvedFromPricingGroup"/>',
	bestellung_materialcontractcombo_materialExplicitelyDefined:
		'<fmt:message key="bestellung.materialcontractcombo.materialExplicitelyDefined"/>',
	bestellung_materialcontractcombo_materialResolvedFromDivision:
		'<fmt:message key="bestellung.materialcontractcombo.materialResolvedFromDivision"/>',
	bestellung_materialcontractcombo_choose:
		'<fmt:message key="bestellung.materialcontractcombo.choose"/>',
	extjs_select: '<fmt:message key="extjs.select"/>',
	dispokarte_texte_freeOfCharge: '<fmt:message key="dispokarte.texte.freeOfCharge"/>',
	dispokarte_texte_releaseSAP: '<fmt:message key="dispokarte.texte.releaseSAP"/>',
	dispokarte_texte_onHoldOrderAndLockedFlag:
		'<fmt:message key="dispokarte.texte.onHoldOrderAndLockedFlag"/>',
	dispokarte_texte_suppressMinChargeSapFlag:
		'<fmt:message key="dispokarte.texte.suppressMinChargeSapFlag"/>',
	dispokarte_texte_openCustorderFlag:
		'<fmt:message key="dispokarte.texte.openCustorderFlag"/>',
	dispokarte_texte_autoReleaseFlag:
			'<fmt:message key="dispokarte.texte.title.automaticRelease"/>',		
	dispokarte_texte_trucktype_type: '<fmt:message key="dispokarte.texte.trucktype.type"/>',
	dispokarte_texte_trucktype_prio: '<fmt:message key="dispokarte.texte.trucktype.prio"/>',
	dispokarte_texte_trucktype_maxgrossweight:
		'<fmt:message key="dispokarte.texte.trucktype.maxgrossweight"/>'
};


var productList = <c:out value="${order_dispokarte_DispokarteForm.currSalesAreaBean.productListJSON}" escapeXml="false"/>;
var serviceOnlyProductList = <c:out value="${order_dispokarte_DispokarteForm.currSalesAreaBean.serviceOnlyProductListJSON}" escapeXml="false"/>;
var restrictionDeliverySettings = <c:out value="${order_dispokarte_DispokarteForm.currSalesAreaBean.restrictionDeliverySettingsJSON}" escapeXml="false"/>;
var combosettings = [];

<%--TODO should not be global this currentIndex the callback needs to which row it needs to apply--%>
var currentIndex;

<%-- FIXME Necessary override as otherwise if the combobox is expanded while loading the store, at return time it will expand again -> 
Will not be possible to actually select. It should be solved differently. At other places loadData is overridden, that is why it is working --%>

Ext.onReady(function() {
	
	initSalesAreaTabs();
});
	
</script>	    
<!--  <%-- Struts needs this, fond no better way to do it --%> 
<html:form action="dispokarte/Dispokarte.do">
-->
<table id="restrictionTable" width="1200" class="classicblue" style="margin-top:0px">
	<tr>
		<th rowspan="2">&nbsp;</th>
		<th rowspan="2">
			<fmt:message key="fields.defaults.enabled"/>
		</th>
		<th rowspan="2">
		<nobr>
			<fmt:message key="fields.produkt.produkt"/>
			(<fmt:message key="fields.produkt.produktnummer"/>)<br>
			<fmt:message key="fields.konditionen.produktionswerk"/><br>
			<fmt:message key="fields.order.mixdesignid"/><br>
			<fmt:message key="fields.konditionen.contract"/>
		</nobr>
		</th>
		<th rowspan="2">
		<nobr>
			<fmt:message key="fields.konditionen.liefertyp"/><br>
			<fmt:message key="fields.konditionen.vehicletype"/><br>			
			<fmt:message key="fields.defaults.kundensilo"/>
		</nobr>
		</th>
 		<th rowspan="2">
			<fmt:message key="dispokarte.texte.quantity"/><br>
			<fmt:message key="dispokarte.texte.perSdorderQuantity"/>
			<fmt:message key="dispokarte.texte.deliveryFrequency"/>
		</th>
		<th rowspan="2">
		<nobr>
			<fmt:message key="fields.konditionen.lieferwerk"/><br>
			<fmt:message key="fields.defaults.versandstelle"/><br>
			<fmt:message key="fields.defaults.ladestrasse"/>
		</nobr>
		</th>
		<th>
		<nobr>
			<fmt:message key="fields.pool.pool"/><br>
			<fmt:message key="fields.carrier.commercial"/><br>
			<fmt:message key="fields.carrier.executing"/><br>
		</nobr>
		</th>
		<th>
		<nobr>
			<fmt:message key="dispokarte.texte.rechnungsemp"/>
			<br/>
			<fmt:message key="fields.payer.payer"/>
		</nobr>
		</th>
		<th rowspan="2">
			<fmt:message key="dispokarte.texte.restrictionAvailability"/>
		</th>		
	</tr>
	<tr>
		<th colspan="2" style="text-align: center">
	 		<fmt:message key="fields.order.specorderindicators"/>
	 	</th>
	</tr>
	<c:set var="zebracount" value="0"/>
	<c:set var="displayCounter" value="-1"/>
	<% DispokarteForm form = (DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm"); %>
	<c:forEach var="deliverySetting" varStatus="status" items="${order_dispokarte_DispokarteForm.currSalesAreaBean.restrictionDeliverySettings}">
	<c:if test='${deliverySetting.shiptoSareaRestr.sapMaterial != null}'>
		<c:set var="displayCounter" value="${(displayCounter + 1)}"/>
		<c:set target="${order_dispokarte_DispokarteForm}" property="activeIdx" value="${status.count-1}"/>
		<c:set var="activeIdx" value="${status.count-1}"/>
		<% int activeIdx = ((Long)pageContext.getAttribute("activeIdx")).intValue(); %>
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		<% String zebracount = "zebra" + pageContext.getAttribute("zebracount").toString(); %>
		<% String restrictionTr = "restrictionTr" + activeIdx; %>
				
		<crh-order:dispokarteInvalidAttributeTrTag id="<%=restrictionTr%>" clazz="<%=zebracount%>" screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=form.getDeliverySettingForActiveIndex()%>">
			<td rowspan="2">
				<table>
					<tr>
						<td title='<fmt:message key="dispokarte.texte.bemerkungen"/>'>
							<a href="javascript:showPopup('ProduktBemerkungenEntryAction.do?id=<c:out value="${status.count-1}"/>','')" ><img src="<crh-common:url value='/images/silk/user_comment.png'/>" style="margin-top: 5px"/></a>
						</td>
					</tr>
					<tr>
						<td title='<fmt:message key="dispokarte.texte.activebemerkungen"/>'>
							<c:if test="${ not empty deliverySetting.shiptoSareaRestr.noteOrderMgmt || not empty deliverySetting.shiptoSareaRestr.noteDelivery || not empty deliverySetting.shiptoSareaRestr.noteDispatching || not empty deliverySetting.shiptoSareaRestr.noteOrderMgmt || not empty deliverySetting.shiptoSareaRestr.noteRailwayDisp}" >
								<img src="<crh-common:url value='/images/error.png'/>"/>
							</c:if>
						</td>
					</tr>
				</table>
			</td>
			<td rowspan="2">
				<table>
					<tr>
						<td>
							<html:checkbox property="currSalesAreaBean.restrictionDeliverySettings[${status.count-1}].shiptoSareaRestr.enabledFlag" 
								value="true"/>
							<%--struts trick, set the value here to false otherwise not possibe to uncheck the checkbox--%>
							<c:set target="${deliverySetting.shiptoSareaRestr}" property="enabledFlag" value="false"/> 
						</td>
					</tr>
					<tr>
						<td title='<fmt:message key="dispokarte.texte.delete"/>'>
								<a href="javascript:modifyRestrictions({'deleteRestriction': '','restrToRemove':'<c:out value="${status.count-1}"/>'})" >
									<img src="<crh-common:url value='/images/delete2.png'/>"/>
								</a>
						</td>
					</tr>
				</table>
			</td>
			<td rowspan="2">
			<nobr>
				<c:out value="${order_dispokarte_DispokarteForm.wrappedDeliverySettingForActiveIdx.txt}"/>
				(<c:out value="${deliverySetting.shiptoSareaRestr.sapMaterial.sapMaterialNo}"/> - <c:out value="${order_dispokarte_DispokarteForm.wrappedDeliverySettingForActiveIdx.materialPackageTxt}"/>)
				&nbsp;
				<input type="hidden" id="productionPlantCd<c:out value="${status.count-1}"/>" name="productionPlantCd<c:out value="${status.count-1}"/>"/>
				<input type="hidden" id="mixDesignId<c:out value="${status.count-1}"/>" name="mixDesignId<c:out value="${status.count-1}"/>"/>
				<br>
				<c:out value="${deliverySetting.shiptoSareaRestr.sapContractItem.sapContract.sapContractNo}"/>&nbsp;(<c:out value="${deliverySetting.shiptoSareaRestr.sapContractItem.contractItemNo}"/>) <c:out value="${deliverySetting.shiptoSareaRestr.sapContractItem.sapContract.incotermsCd}"/><br>
				<fmt:message key="dispokarte.texte.contract.valid"/>: <c:out value="${order_dispokarte_DispokarteForm.contractValidity}"/>						
				</nobr>
			</td>
			<td rowspan="2">
			<nobr>
				<input type="hidden" id="deliveryTypeCd<c:out value="${status.count-1}"/>"  name="deliveryTypeCd<c:out value="${status.count-1}"/>"/>
				<input type="hidden" id="vehicleTypeCombo<c:out value="${status.count-1}"/>" name="vehicleTypeCombo<c:out value="${status.count-1}"/>"/>	
				<input type="hidden" id="siloId<c:out value="${status.count-1}"/>" name="siloId<c:out value="${status.count-1}"/>"/>											
			</nobr>
			</td>
			<td style="padding-bottom: 5px;">
			<table class="noPadding" style="margin-left: 66px;">
			<tr><td>
				<input id="quantity<c:out value="${status.count-1}"/>" name="quantity<c:out value="${status.count-1}"/>"/>
				</td>
				<td>			
				<input type="hidden" id="uomCd<c:out value="${status.count-1}"/>" name="uomCd<c:out value="${status.count-1}"/>"/>
				</td>
				<td title="<fmt:message key="dispokarte.texte.exactCustorderQtyFlag"/>">				
				<input type="hidden" id="exactCustorderQtyFlag<c:out value="${status.count-1}"/>" name="exactCustorderQtyFlag<c:out value="${status.count-1}"/>"/>
			</td></tr>
			<tr>
				<td title="<fmt:message key="dispokarte.texte.perSdorderQuantity"/>">													
				<input id="perSdorderQuantity<c:out value="${status.count-1}"/>" name="perSdorderQuantity<c:out value="${status.count-1}"/>"/>
				</td>
				<td style="text-align: left; vertical-align: middle;">
					<span id="perSdorderUomLabel<c:out value="${status.count-1}"/>" name="perSdorderUomLabel<c:out value="${status.count-1}"/>"/>
				</td>
				<td title="<fmt:message key="dispokarte.texte.exactSdorderQtyFlag"/>">				
				<input type="hidden" id="exactSdorderQtyFlag<c:out value="${status.count-1}"/>" name="exactSdorderQtyFlag<c:out value="${status.count-1}"/>"/>																							
			</td></tr>
			</table>		
			</td>
			<td rowspan="2">
			<nobr>
				<input type="hidden" id="supplyPlantCd<c:out value="${status.count-1}"/>" name="supplyPlantCd<c:out value="${status.count-1}"/>"/>
				<input type="hidden" id="shippingpointId<c:out value="${status.count-1}"/>" name="shippingpointId<c:out value="${status.count-1}"/>"/>				
				<input type="hidden" id="loadingpointId<c:out value="${status.count-1}"/>" name="loadingpointId<c:out value="${status.count-1}"/>"/>				
			</nobr>
			</td>

			<td>
				<table class="noPadding">
					<tr>
						<td title="<fmt:message key="dispokarte.texte.autoPool"/>">
							<input type="hidden" id="autoPool<c:out value="${status.count-1}"/>" name="autoPool<c:out value="${status.count-1}"/>" />							
						</td>
						<td>
							<input type="hidden" id="poolId<c:out value="${status.count-1}"/>" name="poolId<c:out value="${status.count-1}"/>"/>							
						</td>
					</tr>
					<tr>
						<td title="<fmt:message key="dispokarte.texte.autoCarrier"/>">
							<input type="hidden" id="autoCarrier<c:out value="${status.count-1}"/>" name="autoCarrier<c:out value="${status.count-1}"/>" />						</td>
						<td>
							<input type="hidden" id="commercialCarrierId<c:out value="${status.count-1}"/>" name="commercialCarrierId<c:out value="${status.count-1}"/>"/>
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						<td>
							<input type="hidden" id="executingCarrierId<c:out value="${status.count-1}"/>" name="executingCarrierId<c:out value="${status.count-1}"/>"/>
						</td>
					</tr>
				</table>
			</td>
			<td>
			<nobr>
			<table class="noPadding">
			<tr>
				<td>				
				<input type="hidden" id="billtoSapBpId<c:out value="${status.count-1}"/>" name="billtoSapBpId<c:out value="${status.count-1}"/>"/>
				</td>
				<td style="padding-top: 5px">
				<c:if test="${!empty order_dispokarte_DispokarteForm.currSalesAreaBean.shiptoSarea.billtos }">
					<a href="javascript:showPopup('BilltoSelectionRhythmEntryAction.do?id=<c:out value="${status.count-1}"/>','')" ><img src="<crh-common:url value='/images/silk/clock.png'/>"/></a>
				</c:if>
				</td>
				</tr>
			</table>				
			</nobr>								
				<input type="hidden" id="payerSapBpId<c:out value="${status.count-1}"/>" name="payerSapBpId<c:out value="${status.count-1}"/>"/>				
			</td>
			<td rowspan="2" align="left">
			<html:checkbox styleId="availableInPickupAppFlag${status.count-1}" 
				property="currSalesAreaBean.restrictionDeliverySettings[${status.count-1}].shiptoSareaRestr.availableInPickupAppFlag" 
								value="true"/>&nbsp;<fmt:message key="dispokarte.texte.availableinTerminalApp"/>	<br>
			<%--struts trick, set the value here to false otherwise not possibe to uncheck the checkbox--%>								
			<c:set target="${deliverySetting.shiptoSareaRestr}" property="availableInPickupAppFlag" value="false"/>
			<html:checkbox styleId="availableInCustWebFlag${status.count-1}"
				property="currSalesAreaBean.restrictionDeliverySettings[${status.count-1}].shiptoSareaRestr.availableInCustWebFlag" 
								value="true"/>&nbsp;<fmt:message key="dispokarte.texte.availableinCustomerWeb"/>
			<%--struts trick, set the value here to false otherwise not possibe to uncheck the checkbox--%>
			<c:set target="${deliverySetting.shiptoSareaRestr}" property="availableInCustWebFlag" value="false"/>								 			
			</td>			
		</crh-order:dispokarteInvalidAttributeTrTag>
		<crh-order:dispokarteInvalidAttributeTrTag id="<%=restrictionTr%>" clazz="<%=zebracount%>" screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=form.getDeliverySettingForActiveIndex()%>">


			<td style="padding-bottom: 5px"> 
			<table class="noPadding">
				<tr>
				<td>&nbsp;</td>
				<td style="vertical-align: middle; padding-top: 5px;">			
					<span id="deliveryFreqEveryTimeSpan"><fmt:message key="dispokarte.texte.title.every"/></span>
				</td>
				<td title="<fmt:message key="dispokarte.texte.deliveryFrequency"/>">
					<input id="deliveryFreqEveryTime<c:out value="${status.count-1}"/>" name="deliveryFreqEveryTime<c:out value="${status.count-1}"/>"/>
				</td>
				<td>				
					<input type="hidden" id="deliveryFreqEveryTimeUomCd<c:out value="${status.count-1}"/>" name="deliveryFreqEveryTimeUomCd<c:out value="${status.count-1}"/>"/>
				</td>
				<td rowspan="2" style="vertical-align: middle">
					&plusmn;
				</td>
				<td  rowspan="2" style="vertical-align: middle" title="<fmt:message key="dispokarte.texte.title.deliveryTimeTolerance"/>">
					<input id="deliveryTimeTolerance<c:out value="${status.count-1}"/>" name="deliveryTimeTolerance<c:out value="${status.count-1}"/>"/>
				</td>
				<td rowspan="2" style="vertical-align: middle"> 				
					<input type="hidden" id="deliveryTimeToleranceUomCd<c:out value="${status.count-1}"/>" name="deliveryTimeToleranceUomCd<c:out value="${status.count-1}"/>"/>
				</td>
				</tr>
				<tr>
				<td style="vertical-align: middle">
					<a title="<fmt:message key="dispokarte.texte.switchCustomerOrderMode"/>" href="javascript:switchCustomerOrderMode('<c:out value="${status.count-1}"/>')" id="switchCustomerOrderModeButton<c:out value="${status.count-1}"/>" name="switchCustomerOrderModeButton<c:out value="${status.count-1}"/>"><img src="<crh-common:url value='/images/special_customer_order_button.png'/>"/></a>
				</td>
				<td title="<fmt:message key="dispokarte.texte.title.deliveryFreqQtyperhre"/>">
					<input id="deliveryFreqQtyperhre<c:out value="${status.count-1}"/>" name="deliveryFreqQtyperhre<c:out value="${status.count-1}"/>"/>
				</td>
				<td title="<fmt:message key="dispokarte.texte.title.deliveryFreqQtyperhreUom"/>" >				
					<input type="hidden" id="deliveryFreqQtyperhreUomCd<c:out  value="${status.count-1}"/>" name="deliveryFreqQtyperhreUomCd<c:out value="${status.count-1}"/>"/>					
				</td>
				<td style="vertical-align: middle; padding-left: 2px">
					<span id="deliveryFreqQtyperhreSpan"><fmt:message key="dispokarte.texte.title.perhour"/></span>
				</td>
				</tr>
				</table>
			</td>


		
			<td style="vertical-align: middle">
			<input type="hidden" id="checkboxCombo<c:out value="${status.count-1}"/>" name="checkboxCombo<c:out value="${status.count-1}"/>"/>					
			</td>
			<td style="vertical-align: middle">
			<input type="hidden" id="serviceProcessIndicatorList<c:out value="${status.count-1}"/>" name="serviceProcessIndicatorList<c:out value="${status.count-1}"/>"/>					
			</td>
		</crh-order:dispokarteInvalidAttributeTrTag>
			
	</c:if>	
	</c:forEach>
</table>
<br/>
<table>
	<tr>
		<td>
			<input type="hidden" name="materialComboSelectedCompositeIdentifier" id="materialComboSelectedCompositeIdentifier"/>
		</td>
		<td width="20px" style="vertical-align: middle" title="<fmt:message key="dispokarte.texte.title.serviceonlyorder"/>">
			<input type="checkbox" value="true" name="serviceOnlyOrderFlag" id="serviceOnlyOrderFlag"/>
		</td>
</table>

<%--<script type="text/javascript">
	setSAPcheckboxes();
	setFreeOfChargeFlags();
</script>--%>
<!--  
</html:form>
-->
