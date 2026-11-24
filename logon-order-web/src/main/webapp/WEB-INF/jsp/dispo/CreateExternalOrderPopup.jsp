<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: CreateExternalOrderPopup.jsp,v $ --%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<%@ include file="../includeTLDs.jsp"%>

<c:set var="externalOrderUrl">
	/../order/dispo/ExternalOrder.do
</c:set>

<script type="text/javascript">

var directionsService;

Ext.onReady(function() {	
	
	orderQuantityForExtarnal = new Ext.form.TextField({
		applyTo: 'orderQuantityForExtarnal',
		selectOnFocus: true,
		maskRe: /\d/i 
	});

	var orderStatusTransmittedToDriver = <c:out value='${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}'/> ;
	var orderStatusShipping = <c:out value='${order_dispo_ExternalOrderForm.orderStatusShipping}'/> ;
	
	if(orderStatusTransmittedToDriver){
		loadedQuantityForExtarnal = new Ext.form.TextField({
			applyTo: 'loadedQuantityForExtarnal',
			selectOnFocus: true,
			vtype: 'decNum'
		});
	
		deliveredQuantityForExtarnal = new Ext.form.TextField({
			applyTo: 'deliveredQuantityForExtarnal',
			selectOnFocus: true,
			disabled: !orderStatusShipping,
			vtype: 'decNum'
		});
	}

	<c:if test="${callReloadOrdersJs}">
	refreshOrders();
	</c:if>
	
});


function initDelivFromTo() {

	deliveryFrom = new Ext.form.DateField({
    	applyTo : 'deliveryFromDatePicker',
    	format:  'd.m.Y',
    	disabled: <c:out value="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}"/>,
    	width: 100
	});

	deliveryTo = new Ext.form.DateField({
	    	applyTo : 'deliveryToDatePicker',
	    	format:  'd.m.Y',
	    	disabled: <c:out value="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}"/>,
	    	width: 100
	});

	delFromTime = new Ext.form.TimeField({
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'deliveryFromTimePicker',
    	disabled: <c:out value="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}"/>,
    	width: 100
	});

	delFromTime = new Ext.form.TimeField({
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'deliveryToTimePicker',
    	disabled: <c:out value="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}"/>,
    	width: 100
	});
}


function setSelectedPoolForExternal(){
	a = {'setPool':''};
	addFormElements(a, "ExternalOrderForm");
	showPopup('<c:url value="${externalOrderUrl}"/>', null, a);
}

function selectCarrierForExternal(){
	if(document.getElementById("selectedCarrierForExternal").value != '-'){
		a = {'setCarrier':''};
		addFormElements(a, "ExternalOrderForm");
		showPopup('<c:url value="${externalOrderUrl}"/>', null, a);
	}		
}

function deleteFunction(){
	a = {'delete':''};
	addFormElements(a, "ExternalOrderForm");
	showPopup('<c:url value="${externalOrderUrl}"/>', null, a);
}

function save(param) {
	if(validData()) {
		a = param;
		addFormElements(a, "ExternalOrderForm");
		showPopup('<c:url value="${externalOrderUrl}"/>', null, a);
	}
}

function saveFunction(){
	var param = {'save':''};
	save(param);
}

function saveAndClose(){
	var param = {'saveClose':''};
	save(param);
}

function cancelFunction(){
	<c:if test="${callReloadOrdersJs}">
	if (logonPopupHideCallback) {
		logonPopupHideCallback();
	}
	</c:if>
	hidePopup();
}

function validData(){
	var valid = true;

	if(document.getElementById("selectedPoolForExternal").value == '-1'){
		Logon.message.warning('<fmt:message key="extorder.errors.required.pool"/>');
		valid = false;	
	}
	if(document.getElementById("selectedCarrierForExternal").value == '-'){
		Logon.message.warning('<fmt:message key="extorder.errors.required.carrier"/>');
		valid = false;	
	}
	if(document.getElementById("selectedShippingPointForExternal").value == '-'){
		Logon.message.warning('<fmt:message key="extorder.errors.required.shpoint"/>');
		valid = false;	
	}
	if(document.getElementById("selectedShiptoForExternal").value == '-'){
		Logon.message.warning('<fmt:message key="extorder.errors.required.shipto"/>');
		valid = false;	
	}
	if(document.getElementById("selectedShiptoForExternal").value == document.getElementById("selectedShippingPointForExternal").value){
		Logon.message.warning('<fmt:message key="extorder.error.different.site"/>');
		valid = false;	
	}
	if(document.getElementById("selectedMaterialForExternal").value == '-'){
		Logon.message.warning('<fmt:message key="extorder.errors.required.material"/>');
		valid = false;	
	} 		
	if(document.getElementById("orderQuantityForExtarnal").value == '' || document.getElementById("orderQuantityForExtarnal").value == '0'){
		Logon.message.warning('<fmt:message key="extorder.errors.order.qty.zero"/>');
		valid = false;	
	}
	
	return valid;
}	


function changeOrderStatusTransmittedToDriver(){
	a = {'changeOrderStatusTransmittedToDriver':''};
	addFormElements(a, "ExternalOrderForm");
	showPopup('<c:url value="${externalOrderUrl}"/>', null, a);
}

function selectSitesForExternal(){
	if ((document.getElementById("selectedShippingPointForExternal").value != '-') &&
		(document.getElementById("selectedShiptoForExternal").value != '-')){
		a = {'setSites':''};
		addFormElements(a, "ExternalOrderForm");
		showPopup('<c:url value="${externalOrderUrl}"/>', null, a);
	}
}

/* Transforming old school HTML selects to extjs-generated autocomplete featured combos.
 * Note: filtering works by matching the user typed input for the start of the option's value.
 */
var cmbShiptoForExternal = new Ext.form.ComboBox({
       typeAhead: true,
       triggerAction: 'all',
       transform:'selectedShiptoForExternal',
       width:135,
       forceSelection:true
});

var cmbShippingPointForExternal = new Ext.form.ComboBox({
       typeAhead: true,
       triggerAction: 'all',
       transform:'selectedShippingPointForExternal',
       width:135,
       forceSelection:true
});

var cmbMaterialForExternal = new Ext.form.ComboBox({
       typeAhead: true,
       triggerAction: 'all',
       transform:'selectedMaterialForExternal',
       width:135,
       forceSelection:true
});
/* Extjs-transform ignores or removes some properties of the html:select,
 * so we have to set them up after the transform manually.  
 */
cmbShiptoForExternal.on("select",selectSitesForExternal);
cmbShiptoForExternal.setWidth(200);
cmbShiptoForExternal.setDisabled(<c:out value="${(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal != null) || 
		(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal == null) || 
		(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal != -1) || 
		(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal == -1) ||
		order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}"/>);

cmbShippingPointForExternal.on("select",selectSitesForExternal);
cmbShippingPointForExternal.setWidth(200);
cmbShippingPointForExternal.setDisabled(<c:out value="${(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal != null) || 
		(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal == null) ||
		(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal != -1) || 
		(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal == -1)	|| 
		order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}" />);

cmbMaterialForExternal.setWidth(200);
cmbMaterialForExternal.setDisabled(<c:out value="${(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal != null) || 
		(empty order_dispo_ExternalOrderForm.siteListForExternal && 
		order_dispo_ExternalOrderForm.selectedCarrierForExternal == null) ||
		order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}"/>);		
</script>

<html:form action="dispo/ExternalOrder.do" styleId='ExternalOrderForm'>


<c:if test="${!order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}">			
<table class="classicblue" style="width: 700px; height: 230px; border: 1px">
</c:if>

<c:if test="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}">			
<table class="classicblue" style="width: 700px; height: 260px; border: 1px">
</c:if>

					<c:if test="${!order_dispo_ExternalOrderForm.editOrder}">
							<tr>
								<th height="21" colspan="2">
									<fmt:message key="extorder.title.create" />   
								</th>
							</tr>
					</c:if>		
					<c:if test="${order_dispo_ExternalOrderForm.editOrder}">
							<tr>
								<th height="21" colspan="2">
									<c:out value="${order_dispo_ExternalOrderForm.pupupTitle}" />
								</th>
							</tr>
					</c:if>							
	
	<tr>
		<td>
			<table style="width: 100%;">
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.pool" />
						</span>
					</td>

					<td colspan="2">
						<html:select disabled="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}" size="1" style="width:241px;" styleId='selectedPoolForExternal' property="selectedPoolForExternal" onchange="setSelectedPoolForExternal()">
							<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">
								<fmt:message key="extorder.text.poolchoose" />   
							</html:option>
							<c:forEach var="pool" varStatus="status" items="${order_dispo_ExternalOrderForm.poolsForExternal}">
								<html:option value="${pool.poolTxPK.poolId}"><c:out value="${pool.name}"/></html:option>
							</c:forEach>					
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.carrier" />
						</span>
					</td>
					<td>
						<html:text disabled="true" style="width:80px;" styleId="sapId" property="sapId" />
					</td>

					<td>
						<html:select size="1" style="width:153px;" styleId='selectedCarrierForExternal' property="selectedCarrierForExternal" onchange="selectCarrierForExternal()"
						disabled="${order_dispo_ExternalOrderForm.selectedPoolForExternal == null || order_dispo_ExternalOrderForm.selectedPoolForExternal == 0
						|| empty order_dispo_ExternalOrderForm.carriersForExternal || order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}">
							<html:option value="-">
								<c:if test="${!empty order_dispo_ExternalOrderForm.carriersForExternal}">
									<fmt:message key="extorder.text.carrierchoose" />
								</c:if>
								<c:if test="${empty order_dispo_ExternalOrderForm.carriersForExternal && order_dispo_ExternalOrderForm.selectedPoolForExternal != null
								&& order_dispo_ExternalOrderForm.selectedPoolForExternal != 0 && order_dispo_ExternalOrderForm.selectedPoolForExternal != -1 }">
									<fmt:message key="extorder.text.nocarriers" />
								</c:if>
							</html:option>
							<c:forEach var="carriersForExternal" varStatus="status" items="${order_dispo_ExternalOrderForm.carriersForExternal}">
								<html:option value="${carriersForExternal.carrierId}"><c:out value="${carriersForExternal.carrierName}"/></html:option>
							</c:forEach>					
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.deliverydate" />
						</span>
					</td>
					<td colspan="2">
						<table style="width:100%;">
						<tr>
							<td style='width:100px'>
								
									<html:text disabled="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}" styleClass='textfield' style='width:100px' styleId='deliveryFromDatePicker' property="deliveryFromDate"/>
									<html:text disabled="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}" styleClass='textfield' style='width:100px' styleId="deliveryFromTimePicker" property="deliveryFromTime" />
								
							</td>
							
							<td style='width:5px'>
							-
							</td>
							
							<td>
								
									<html:text disabled="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}" styleClass='textfield' style='width:100px' styleId='deliveryToDatePicker' property="deliveryToDate"/>
									<html:text disabled="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}" styleClass='textfield' style='width:100px' styleId="deliveryToTimePicker" property="deliveryToTime" />
								
							</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.notetruckdriver" />
						</span>
					</td>
					<td colspan="2">
						<html:textarea property="noteTruckDriver" style="height:35px;width: 280px;" disabled="${empty order_dispo_ExternalOrderForm.siteListForExternal}"></html:textarea>
					</td>
				</tr>			
			</table>
		</td>

		<td>
			<table style="width: 100%;">
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.shpoint" />
						</span>
					</td>
					<td  align="left">
						<html:select size="1" styleId='selectedShippingPointForExternal' property="selectedShippingPointForExternal">
							<html:option value="-">
								<c:if test="${!empty order_dispo_ExternalOrderForm.siteListForExternal}">
									<fmt:message key="extorder.text.shippingpointchoose" />
								</c:if>
								<c:if test="${empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal != null  && order_dispo_ExternalOrderForm.selectedCarrierForExternal != -1 && order_dispo_ExternalOrderForm.selectedCarrierForExternal != 0}">
									<fmt:message key="extorder.text.noshippingpoint" />
								</c:if>
							</html:option>
							<c:forEach var="siteListForExternal" varStatus="status" items="${order_dispo_ExternalOrderForm.siteListForExternal}">
								<html:option value="${siteListForExternal.extSiteId}"><c:out value="${siteListForExternal.siteName}"/></html:option>
							</c:forEach>					
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.shipto" />
						</span>
					</td>
					<td  align="left">
						<html:select size="1" styleId='selectedShiptoForExternal' property="selectedShiptoForExternal">
							<html:option value="-">
								<c:if test="${!empty order_dispo_ExternalOrderForm.siteListForExternal}">
									<fmt:message key="extorder.text.shiptochoose" />
								</c:if>
								
								<c:if test="${empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal != null  && order_dispo_ExternalOrderForm.selectedCarrierForExternal != -1 && order_dispo_ExternalOrderForm.selectedCarrierForExternal != 0}">
									<fmt:message key="extorder.text.noshipto" />
								</c:if>
							</html:option>
							<c:forEach var="siteListForExternal" varStatus="status" items="${order_dispo_ExternalOrderForm.siteListForExternal}">
								<html:option value="${siteListForExternal.extSiteId}"><c:out value="${siteListForExternal.siteName}"/></html:option>
							</c:forEach>					
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.produkte" />
						</span>
					</td>
					<td  align="left">
						<html:select size="1" styleId='selectedMaterialForExternal' property="selectedMaterialForExternal">
							<html:option value="-">
								<c:if test="${!empty order_dispo_ExternalOrderForm.materialsForExternal}">
									<fmt:message key="extorder.text.productchoose" />
								</c:if>
								<c:if test="${empty order_dispo_ExternalOrderForm.materialsForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal != null  && order_dispo_ExternalOrderForm.selectedCarrierForExternal != -1 && order_dispo_ExternalOrderForm.selectedCarrierForExternal != 0}">
									<fmt:message key="extorder.text.nomaterial" />
								</c:if>
							</html:option>
							<c:forEach var="materialsForExternal" varStatus="status" items="${order_dispo_ExternalOrderForm.materialsForExternal}">
								<html:option value="${materialsForExternal.extMaterialId}"><c:out value="${materialsForExternal.materialName}"/></html:option>
							</c:forEach>					
						</html:select>
					</td>
				</tr>
				
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.quantity" />/<fmt:message key="extorder.label.uom" />
						</span>
					</td>
					<td align="left">
									<html:text disabled="${ order_dispo_ExternalOrderForm.selectedCarrierForExternal == null }" style="width:50px;" styleId="orderQuantityForExtarnal" property="orderQuantityForExtarnal"/>									
									
									<html:select size="1" style="width:147px;" property="uomCdForExternal"
									disabled="${(empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal != null) || (empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal == null)
						|| (empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal != -1) || (empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal == -1)}">
											<c:if test="${empty order_dispo_ExternalOrderForm.uomListForExternal}">
												<html:option value="-">
												</html:option>	
											</c:if>	
										<c:forEach var="uomForExternal" varStatus="status" items="${order_dispo_ExternalOrderForm.uomListForExternal}">
											<html:option value="${uomForExternal.uomTxPK.uomBs.uomCd}"><c:out value="${uomForExternal.txt}"/></html:option>
										</c:forEach>					
									</html:select>
							
					</td>
				</tr>

	
				
				<c:if test="${order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}">
				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.net.weight" />/<fmt:message key="extorder.label.uom" />
						</span>
					</td>
					<td  align="left">

									<html:text style="width:50px;" styleId="loadedQuantityForExtarnal" property="loadedQuantityForExtarnal"/>

									<html:select size="1" style="width:147px;" property="uomCdLoadedForExternal"
									disabled="${(empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal != null) || (empty order_dispo_ExternalOrderForm.siteListForExternal && order_dispo_ExternalOrderForm.selectedCarrierForExternal == null)}">
											<c:if test="${empty order_dispo_ExternalOrderForm.uomListForExternal}">
												<html:option value="-">
												</html:option>	
											</c:if>	
										<c:forEach var="uomForExternal" varStatus="status" items="${order_dispo_ExternalOrderForm.uomListForExternal}">
											<html:option value="${uomForExternal.uomTxPK.uomBs.uomCd}"><c:out value="${uomForExternal.txt}"/></html:option>
										</c:forEach>					
									</html:select>

					</td>
				</tr>

				<tr>
					<td>
						<span class="label"> 
							<fmt:message key="extorder.label.delivered.quantity" />
						</span>
					</td>
					<td  align="left">
						<html:text style="width:50px;" styleId="deliveredQuantityForExtarnal" property="deliveredQuantityForExtarnal"/>
					</td>
				</tr>
				</c:if>
				
	
			</table>
		</td>
	</tr>
	
	

	<!-- buttons -->
	<tr>
		<td colspan="2">
		<table style="width:100%;">
		<tr>
		
			<td style="width:30%;">
				<c:if test="${order_dispo_ExternalOrderForm.externalOrderId != null && order_dispo_ExternalOrderForm.externalOrderId != 0
							&& !order_dispo_ExternalOrderForm.orderStatusTransmittedToDriver}">
					<div align="left">
					<input onClick="deleteFunction();" type="button"
						class="ViewButtonStyle"
						value=" <fmt:message key="extorder.button.delete"/> ">
					</div>
				</c:if>
			</td>
			
			<td style="width:70%;">
				<div align="right">
				<input onclick="cancelFunction();" type="button"
					class="ViewButtonStyle"
					value=" <fmt:message key="extorder.button.cancel"/> ">
				<input onclick="saveFunction();" type="button"
					class="ViewButtonStyle"
					value=" <fmt:message key="extorder.button.save"/> ">
				<input style="vertical-align: middle" onclick="saveAndClose();" type="button"
					class="ViewButtonStyle"
					value=" <fmt:message key="extorder.button.save.close"/> ">
				</div>	
			</td>
			
		</tr>
		</table>				
		</td>
	</tr>
</table>

</html:form>


<script type="text/javascript">
	initDelivFromTo();
</script>
