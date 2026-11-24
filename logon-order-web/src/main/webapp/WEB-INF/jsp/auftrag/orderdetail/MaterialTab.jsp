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

<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>

<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />

<script type="text/javascript">
	
	var sentToSapIsNullFlag = <c:out value="${form.materialBean.sentToSapIsNullFlag}" />;
	var mixDesignAggRmxMap = <c:out value="${form.materialBean.mixDesignAggRmxMapJson}"
			escapeXml="false"/>;
	var convFactors = <c:out value="${form.materialBean.convFactorsMapJson}" escapeXml="false"/>
	
	<%--If the row is selected according to the rules determined when loading the tab input fields have to be updated--%>
	function onClickMaterialRadio(orderItemId, disableOrdered, disableLoaded, disableReturned, 
			disableCreditNote, creditNoteDBVlaue, returnQuantHandling) {
		
		document.getElementById(orderItemId+'_OrderedQuantity').disabled = disableOrdered;
		document.getElementById(orderItemId+'_OrderedUom').disabled = disableOrdered;

		var creditNoteUIVlaue = creditNoteDBVlaue;
		
		//on the services and extra cost tab there is no creditNote
		if (document.getElementById(orderItemId+'_CreditNoteFlag') != undefined) {
			document.getElementById(orderItemId+'_CreditNoteFlag').disabled = disableCreditNote;
			creditNoteUIVlaue = document.getElementById(orderItemId+'_CreditNoteFlag').checked;
		}
		
		// in case problematic use same logic as for loaded qty and uom which
		// checks for rejected / approved
		if (creditNoteDBVlaue && !creditNoteUIVlaue) {
			document.getElementById(orderItemId+'_ReturnedQuantity').disabled = true;
			document.getElementById(orderItemId+'_ReturnedUom').disabled = true;
		} 
		else {
			document.getElementById(orderItemId+'_ReturnedQuantity').disabled = disableReturned;
			document.getElementById(orderItemId+'_ReturnedUom').disabled = disableReturned;
		} 
		
		// if rejected or already approved
		if (returnQuantHandling == 0 || returnQuantHandling == 4) {
			document.getElementById(orderItemId+'_LoadedQuantity').disabled = true;
			document.getElementById(orderItemId+'_LoadedUom').disabled = true;
		}
		else {
			document.getElementById(orderItemId+'_LoadedQuantity').disabled = disableLoaded;
			document.getElementById(orderItemId+'_LoadedUom').disabled = disableLoaded;
		}
		
	}

	<%--Before radio button selection is refreshed, the last selection must be disabled--%>
	
	function onChangeMaterialRadio() {
		var radios = document.getElementsByName('radio_material');
		for (var i=0; i< radios.length; i++) {
			if (!radios[i].checked) {
				orderItemId = radios[i].value;
				document.getElementById(orderItemId+'_OrderedQuantity').disabled = true;
				document.getElementById(orderItemId+'_OrderedUom').disabled = true;
				document.getElementById(orderItemId+'_LoadedQuantity').disabled = true;
				document.getElementById(orderItemId+'_LoadedUom').disabled = true;
				document.getElementById(orderItemId+'_ReturnedQuantity').disabled = true;
				document.getElementById(orderItemId+'_ReturnedUom').disabled = true;

				//on the services and extra cost tab there is no creditNote
				if (document.getElementById(orderItemId+'_CreditNoteFlag') != undefined) {
					document.getElementById(orderItemId+'_CreditNoteFlag').disabled = true;
				}
			}
		}
	};
	
	//called when material is replaced by another material. this can occur only in case of planning material
	function onReplacePlanningMaterial() {
		var radios = document.getElementsByName('radio_material');
		for (var i = 0; i < radios.length; i++) {
			if (radios[i].checked) {
				orderItemId = radios[i].value;
				var materialContractItemEl = document.getElementById(orderItemId+'_SapMaterialId');
				var sapContractItemId = materialContractItemEl.value;
				document.getElementById(orderItemId+'_OrderedQuantity').disabled = false;
				document.getElementById(orderItemId+'_OrderedUom').disabled = false;
				
				loadMaterialInfo(orderItemId, sapContractItemId);
				
				break;
			}
		}
	};
	
	//calculations with the new material, reloading Material delivery note info part of material tab
	function loadMaterialInfo(orderItemId, sapContractItemId) {
		form = Ext.getDom('OrderDetailForm');
		p = {
			'formAction' :'<%=OrderDetailAction.FORWARD_REPLACE_MATERIAL%>',
			'orderItemId' :orderItemId,
			'sapContractItemId': sapContractItemId
		};
		addFormElements(p, form);
		diva = Ext.get('delNoteDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : p,
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true,
			callback: function() {
				reloadUomList();
			}
			});
	}
	
	//reloading uomlist
	// TODO (orsika Jul 25, 2013): currently reloads the whole div containing all items. only the current item's uomlist should be reloaded
	function reloadUomList() {
		p = {
			'formAction' :'<%=OrderDetailAction.FORWARD_LOAD_UOMLIST%>'
		};
		diva = Ext.get("materialTabItem");
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : p,
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true
			});
	}
	
	function loadOrderItem(matIndex) {
		if (currentTab == '<%=OrderDetailAction.FORWARD_MATERIAL_TAB%>') {
			loadDelNote(matIndex) 
		}
		else if (currentTab == '<%=OrderDetailAction.FORWARD_SERVICES_EXTRA_COST_TAB%>') {
			callLoadOrderItem(matIndex);
		}
	};

	function callLoadOrderItem(matIndex) {
		form = Ext.getDom('OrderDetailForm');
		p = {
				'formAction' :'<%=OrderDetailAction.FORWARD_DELIVERY_NOTE%>',
				'matIndex' :matIndex
		};
		addFormElements(p, form);

		Ext.Ajax.request({
			url :'<c:url value="${orderDetailUrl}"/>',
			params : p
		});
	};

	function loadDelNote(matIndex) {
		form = Ext.getDom('OrderDetailForm');
		p = {'formAction' :'<%=OrderDetailAction.FORWARD_DELIVERY_NOTE%>',
			'matIndex' :matIndex
		};
		addFormElements(p, form);
		
		var diva = Ext.get('delNoteDiv');
		diva.load( {
				url :'<c:url value="${orderDetailUrl}"/>',
				params : p,
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true
				});
	};
	
	function generatePdf(){
	    form = Ext.getDom('OrderDetailForm');
		p = {'formAction' :'<%=OrderDetailAction.FORWARD_GENERATE_PDF%>'};
		addFormElements(p, form); 
		
		var diva = Ext.get('delNoteDiv');
		diva.load( {
				url :'<c:url value="${orderDetailUrl}"/>',
				params : p,
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true
				});
	};
	
	function loadBillto(billtoSapBpId) {
		var diva = Ext.get('billtoDiv');		
		diva.load({
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {
					'formAction' :'<%=OrderDetailAction.FORWARD_BILLTO%>',
					'billtoSapBpId' : billtoSapBpId
			},
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true,
			callback: function() { 
				document.getElementById('billtoSapIdDiv').innerHTML = document.getElementById('billtoSapId').value;}
		});
	};

	function loadPayer(payerId) {
		var diva = Ext.get('payerDiv');

		diva.load({
			url: '<c:url value="${orderDetailUrl}"/>',
			params: {
				'formAction': '<%=OrderDetailAction.FORWARD_PAYER%>',
				'payerId': payerId
			},
			text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true,
			callback: function() {
				document.getElementById('payerSapIdDiv').innerHTML =
					document.getElementById('payerSapId').value;
			}
		});
	};
	
	//when CN checkbox selected
	function changeCN(orderItemId, checked) {
		
		document.getElementById(orderItemId+'_ReturnedQuantity').disabled = !checked;
		document.getElementById(orderItemId+'_ReturnedUom').disabled = !checked;
		
		if (checked) {
			Ext.get(orderItemId+'_ReturnReasonButton').show();
		}
		else {
			Ext.get(orderItemId+'_ReturnReasonButton').hide();
		}
		
		if (!checked) {
			document.getElementById(orderItemId+'_ReturnedQuantity').value = "";
			document.getElementById(orderItemId+'_ReturnedUom').selectedIndex = 0;
			
			//update Deilvered quantity
			var loadedUom = document.getElementById(orderItemId+'_LoadedUom').value;
			if (loadedUom == '-1') {
				document.getElementById(orderItemId+'_DeliveredQuantity').value = document.getElementById(orderItemId+'_LoadedQuantity').value;
				document.getElementById(orderItemId+'_DeliveredUom').value = ""; 
			}
			else {
				updateDeliveredQty(orderItemId);
			}
		}
	};
	
	/**
	 * Similar to UomConverterServiceBean#convert(BigDecimal, Long, Long, boolean).
	 *
	 * @param quantity a number or a string convertible to number, or an empty string
	 * @param numerator a number or a string convertable to number
	 * @param denominator a number or a string convertable to number
	 * @return if the input quantity is an empty string or zero, or numerator equals to
	 *     denominator, then the input quantity, if conversion is not possible, then an empty
	 *     string, otherwise the converted quantity as a number
	 */
	function convert(quantity, numeratorStr, denominatorStr, forward) {
		if (!quantity) {
			return quantity;
		}
		
		var numerator = parseInt(numeratorStr);
		var denominator = parseInt(denominatorStr);
		
		if (isNaN(numerator) || isNaN(denominator) || !numerator || !denominator) {
			// throw 'Invalid conversion factors: (' + numeratorStr + ', ' + denominatorStr + ')';
			return '';
		}
		
		if (numerator !== denominator) {
			if (!forward) {
				var tmp = numerator;
				numerator = denominator;
				denominator = tmp;
			}
			
			quantity *= numerator / denominator;
		}
		
		return quantity;
	}
	
	// ****************** Material listing / Loaded/Returned Qty event handling *******************
	
	function onLoadedQtyChange(value, orderItemId) {
		updateDeliveredQty(orderItemId);
	}
	
	function onLoadedUomChange(value, orderItemId) {
		// Update Returned UoM.
		var loadedUom = value;
		document.getElementById(orderItemId + '_ReturnedUom'),value = loadedUom;
		
		// Update Delivered Quantity.
		updateDeliveredQty(orderItemId);
	}
	
	function onReturnedQtyChange(value, orderItemId) {
		updateDeliveredQty(orderItemId);
	}
	
	
	// When loaded or returned quantity is changed
	function updateDeliveredQty(orderItemId) {
		var loadedQty = document.getElementById(orderItemId + '_LoadedQuantity').value;
		var returnedQty = document.getElementById(orderItemId + '_ReturnedQuantity').value;
		var loadedUom = document.getElementById(orderItemId + '_LoadedUom').value;
		
		// D = L - R
		var deliveredQty = '';
		
		loadedQty = parseFloat(loadedQty);
		
		if (!isNaN(loadedQty) && loadedUom !== '<%= Constant.INVALID_OBJECT_ID_STR %>') {
			var fromLoadedUomToDelivUom =
					convFactors[orderItemId].fromLoadedUomToDelivUom[loadedUom];
			
			if (fromLoadedUomToDelivUom) {
				var fromLoadedUomToDelivUomNum = fromLoadedUomToDelivUom.numerator;
				var fromLoadedUomToDelivUomDen = fromLoadedUomToDelivUom.denominator;
				
				var loadedQtyInDelivUom = convert(loadedQty, fromLoadedUomToDelivUomNum,
						fromLoadedUomToDelivUomDen, true);
				
				deliveredQty = loadedQtyInDelivUom;
				
				returnedQty = parseFloat(returnedQty);
				
				if (!isNaN(returnedQty)) {
					var returnedQtyInDelivUom = convert(returnedQty, fromLoadedUomToDelivUomNum,
							fromLoadedUomToDelivUomDen, true);
					
					deliveredQty -= returnedQtyInDelivUom;
				}
				
				deliveredQty = roundToDecimalAndTrimEndZeros(deliveredQty, 4);
			}
		}
		
		document.getElementById(orderItemId + '_DeliveredQuantity').value = deliveredQty;
	};
	
	// ********************************************************************************************
	
	// functions related to MaterialTabDelNote.jsp
	function changeProdPlant(plantCd) {
		if (plantCd == '-1') {
			document.getElementById('prodPlantCodeId').value = "";
		} else {
			document.getElementById('prodPlantCodeId').value = plantCd;
		}
	};

	function updateAggRmxCodeField(mixDesignId) {
		if (mixDesignId == '-1') {
			document.getElementById('aggRmxCodeField').value = "";
		} else {
			document.getElementById('aggRmxCodeField').value = mixDesignAggRmxMap[mixDesignId];
		}
	};
	
	// ********************* Weighing / Net/Gross/Tare fields event handling **********************
	
	function warnIfAlreadySentToSap() {
		if (!sentToSapIsNullFlag) {
			Logon.message.warning('<fmt:message key="order.warning.already.sent.to.sap"/>');
		}
	}
	
	//when one of gross/net/tare is changed, update the other ones
	
	function onInputTareMeasureChange(source, value, orderItemId) {
		
		warnIfAlreadySentToSap();
		
		var inputNetMeasure = document.getElementById('inputNetMeasure');
		var inputGrossMeasure = document.getElementById('inputGrossMeasure');
		
		// Update net weight
		var tareT = parseFloat(value);
		var grossT = parseFloat(inputGrossMeasure.value);
		var netStr;
		
		if (isNaN(tareT) || isNaN(grossT)) {
			netStr = '';
		}
		else {
			var fromNetQtyToTonsNum = convFactors[orderItemId].fromNetQtyToTons.numerator;
			var fromNetQtyToTonsDen = convFactors[orderItemId].fromNetQtyToTons.denominator;
			
			var netT = grossT - tareT;
			var net = convert(netT, fromNetQtyToTonsNum, fromNetQtyToTonsDen, false);
			netStr = roundToDecimalAndTrimEndZeros(net, 4);
		}
		
		inputNetMeasure.value = netStr;
		onInputNetMeasureChange(source, inputNetMeasure.value, orderItemId);
	}
	
	function onInputGrossMeasureChange(source, value, orderItemId) {
		
		var inputGrossMeasure = document.getElementById('inputGrossMeasure');
		
		if (source.id === inputGrossMeasure.id) {
			warnIfAlreadySentToSap();
			
			var inputTareMeasure = document.getElementById('inputTareMeasure');
			var inputNetMeasure = document.getElementById('inputNetMeasure');
			
			// Update net weight
			var grossT = parseFloat(value);
			var tareT = parseFloat(inputTareMeasure.value);
			var netStr;
			
			if (isNaN(tareT) || isNaN(grossT)) {
				netStr = '';
			}
			else {
				var fromNetQtyToTonsNum = convFactors[orderItemId].fromNetQtyToTons.numerator;
				var fromNetQtyToTonsDen = convFactors[orderItemId].fromNetQtyToTons.denominator;
				
				var netT = grossT - tareT;
				var net = convert(netT, fromNetQtyToTonsNum, fromNetQtyToTonsDen, false);
				netStr = roundToDecimalAndTrimEndZeros(net, 4);
			}
			
			inputNetMeasure.value = netStr;
			onInputNetMeasureChange(source, inputNetMeasure.value, orderItemId);
		}
	}
	
	function onInputNetMeasureChange(source, value, orderItemId) {
		
		var inputNetMeasure = document.getElementById('inputNetMeasure');
		var net = parseFloat(value);
		
		if (source.id === inputNetMeasure.id) {
			warnIfAlreadySentToSap();
			
			var inputTareMeasure = document.getElementById('inputTareMeasure');
			var inputGrossMeasure = document.getElementById('inputGrossMeasure');
			
			// Update gross weight
			var tareT = parseFloat(inputTareMeasure.value);
			var grossTStr;
			
			if (isNaN(net)) {
				grossTStr = '';
			}
			else {
				var fromNetQtyToTonsNum = convFactors[orderItemId].fromNetQtyToTons.numerator;
				var fromNetQtyToTonsDen = convFactors[orderItemId].fromNetQtyToTons.denominator;
				
				var netT = convert(net, fromNetQtyToTonsNum, fromNetQtyToTonsDen, true);
				var grossT = tareT + netT;
				grossTStr = roundToDecimalAndTrimEndZeros(grossT, 4);
			}
			
			inputGrossMeasure.value = grossTStr;
			onInputGrossMeasureChange(source, inputGrossMeasure.value, orderItemId);
		}
		
		// Update loaded quantity (in material listing)
		var inputLoadedQuantity = document.getElementById(orderItemId + '_LoadedQuantity');
		var netInLoadedUomStr;
		
		if (isNaN(net)) {
			netInLoadedUomStr = '';
		}
		else {
			var fromNetQtyToLoadedUomNum =
					convFactors[orderItemId].fromNetQtyToLoadedUom.numerator;
			var fromNetQtyToLoadedUomDen =
					convFactors[orderItemId].fromNetQtyToLoadedUom.denominator;
			
			var netInLoadedUom = convert(net, fromNetQtyToLoadedUomNum, fromNetQtyToLoadedUomDen,
					true);
			netInLoadedUomStr = roundToDecimalAndTrimEndZeros(netInLoadedUom, 4);
		}
		
		inputLoadedQuantity.value = netInLoadedUomStr;
		onLoadedQtyChange(inputLoadedQuantity.value, orderItemId);
	}
	
	// ********************************************************************************************
	
	var billtoSameAsOneTimeSoldto = <c:out value="${form.billtoSameAsOneTimeSoldto}" />;
	var payerSameAsOneTimeSoldto = <c:out value="${form.payerSameAsOneTimeSoldto}" />;

	function updateOnetimeSoldtoAddress(name) {
		if (billtoSameAsOneTimeSoldto) {
			document.getElementById('billtoStreet').innerHTML = name;
		}
		if (payerSameAsOneTimeSoldto) {
			document.getElementById('payerStreet').innerHTML = name;
		}
	};

	function updateOnetimeSoldtoCountry(country) {
		if (billtoSameAsOneTimeSoldto) {
			document.getElementById('billtoCountry').innerHTML = country;
		}
		if (payerSameAsOneTimeSoldto) {
			document.getElementById('payerCountry').innerHTML = country;
		}
	};

	function updateOnetimeSoldtoPostalCode(postalcode) {
		if (billtoSameAsOneTimeSoldto) {
			document.getElementById('billtoPostalCode').innerHTML = postalcode;
		}
		if (payerSameAsOneTimeSoldto) {
			document.getElementById('payerPostalCode').innerHTML = postalcode;
		}
	};

	function updateOnetimeSoldtoCity(city) {
		if (billtoSameAsOneTimeSoldto) {
			document.getElementById('billtoCity').innerHTML = city;
		}
		if (payerSameAsOneTimeSoldto) {
			document.getElementById('payerCity').innerHTML = city;
		}
	};


	function showGoodsMovementPopup() {
		var diva = Ext.get('logonPopup2');
		p = {
			'formAction' :'<%=OrderDetailAction.FORWARD_GOODS_MOVEMENT_POPUP%>'
		};
		
        diva.hide();
        diva.load( {
        	url :'<c:url value="${orderDetailUrl}"/>',
        	params : p,
            text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
            scripts :true
        });
        
        var greyLayer = Ext.get('grayout2');
        grayoutScreen(greyLayer);
	};
	
</script>

<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />

<c:set var="isDispatched">
	<c:out value="${form.dispatchedFlag}" />
</c:set>
<c:set var="isBillToAndPayerDisabled" value="${form.materialBean.disabledBillToAndPayer}" />

<c:set var="DELIVERY_TYPE_ROAD_DIRECT"><%=Constant.DELIVERY_TYPE_ROAD_DIRECT%></c:set>
<c:set var="DELIVERY_TYPE_RAIL_DIRECT"><%=Constant.DELIVERY_TYPE_RAIL_DIRECT%></c:set>
<c:set var="DELIVERY_TYPE_BIMODAL_ROAD"><%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%></c:set>
<c:set var="DELIVERY_TYPE_BIMODAL_RAIL"><%=Constant.DELIVERY_TYPE_BIMODAL_RAIL%></c:set>
<c:set var="DELIVERY_TYPE_PICKUP"><%=Constant.DELIVERY_TYPE_PICKUP%></c:set>
<c:set var="SHIPTO_TYPE_LOCATION_NOT_FIXED"><%=Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED%></c:set>


<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>

<c:set var="isBilltoChangeDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_BILLTO_PARTY %>">
	<c:set var="isBilltoChangeDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isPayerChangeDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_PAYER_PARTY %>">
	<c:set var="isPayerChangeDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isOneTimeSoldtoAddressDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_ONETIMESOLDTO_ADDR %>">
	<c:set var="isOneTimeSoldtoAddressDisabled" value="${not form.oneTimeSoldtoEditable}"/>
</crh-common:authorize>

<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%>

<div id="materialTabItem">
<jsp:include page="MaterialTabItem.jsp"></jsp:include>
</div>

<table style="width: 100%;">
	<colgroup>
		<col width="50%" />
		<col width="50%" />
	</colgroup>
	<tbody>
		<tr>
			<!-- SOLD TO -->
			<td>
			<div class="subform" style="width:305px; margin-top:4px; margin-left:4px;"><!-- beginn table Kunde -->
			<span class="label"><fmt:message
				key="orderdetail.material.label.soldto" /> <c:out
				value="${form.materialBean.soldtoSapId}" /></span>
			<table cellspacing="0" cellpadding="0" border="0" width="100%"
				style="border: 0px none;" class="classic">
				<tbody>
					<c:choose>
						<c:when test="${not form.oneTimeSoldto}">
							<%-- Regular soldto address: not editable. --%>
							<tr>
								<td><span class="textfielddisabled_h" style="width: 295px;"
									title="<c:out value="${form.materialBean.soldtoName}" />">
								<nobr> <c:out
									value="${form.materialBean.soldtoName}" />&nbsp;
								</nobr> </span></td>
							</tr>
							<tr>
								<td>
								<div class="textfielddisabled_h"
									style="float: left; width: 250px;" 
									title="<c:out value="${form.materialBean.soldtoStreet}" />"><c:out
									value="${form.materialBean.soldtoStreet}" />&nbsp;
								</div>
								<div class="textfielddisabled_h"
									style="float: right; margin-left: 4px; width: 35px;"
									title="<c:out value="${form.materialBean.soldtoHouseNumber}" />"><c:out
									value="${form.materialBean.soldtoHouseNumber}" />&nbsp;
								</div>
								</td>
							</tr>
							<tr>
								<td>
								<div class="textfielddisabled_h" style="float: left; width: 35px;"
									title="<c:out value="${form.materialBean.soldtoCountry}" />">
								<c:out
									value="${form.materialBean.soldtoCountry}" />&nbsp;
								</div>
								<div class="textfielddisabled_h"
									style="float: left; margin-left: 4px; width: 50px;"
									title="<c:out value="${form.materialBean.soldtoPostalCode}" />">
								<c:out
									value="${form.materialBean.soldtoPostalCode}" />&nbsp;</div>
								<div class="textfielddisabled_h"
									style="float: right; margin-left: 4px; width: 195px;"
									title="<c:out value="${form.materialBean.soldtoCity}" />">
								<c:out
									value="${form.materialBean.soldtoCity}" />&nbsp;
								</div>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<%-- One time soldto address can be editable. --%>
							<tr>
								<td><nobr>
									<c:set var="maxlength">
										<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_NAME + "" %>
									</c:set>
									<html:text styleClass='textfield' style='width: 295px'
											property="materialBean.soldtoName" maxlength="${maxlength}" 
											disabled="${isOneTimeSoldtoAddressDisabled}" />
											
								</nobr></td>
							</tr>
							<tr>
								<td>
									<c:set var="maxlength">
										<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_STREET_HOUSENO + "" %>
									</c:set>
									<html:text styleClass='textfield' style='width: 295px'
											onkeyup="javascript:updateOnetimeSoldtoAddress(this.value)"
											property="materialBean.soldToStreetHouseNumber" maxlength="${maxlength}" 
											disabled="${isOneTimeSoldtoAddressDisabled}" />
								</td>
							</tr>
							<tr>
								<td>
									<c:set var="maxlength">
										<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_COUNTRY + "" %>
									</c:set>
									<html:text styleClass='textfield' style='float: left; width: 35px'
											onkeyup="javascript:updateOnetimeSoldtoCountry(this.value)"
											property="materialBean.soldtoCountry" maxlength="${maxlength}"
											disabled="${isOneTimeSoldtoAddressDisabled}" />
									<c:set var="maxlength">
										<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_POSTAL_CODE + "" %>
									</c:set>
									<html:text styleClass='textfield' style='float: left; margin-left: 4px; width: 50px;'
											onkeyup="javascript:updateOnetimeSoldtoPostalCode(this.value)"
											property="materialBean.soldtoPostalCode" maxlength="${maxlength}"
											disabled="${isOneTimeSoldtoAddressDisabled}" />
									<c:set var="maxlength">
										<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_CITY + "" %>
									</c:set>
									<html:text styleClass='textfield' style='float: right; margin-right: 4px; width: 195px;'
											onkeyup="javascript:updateOnetimeSoldtoCity(this.value)"
											property="materialBean.soldtoCity" maxlength="${maxlength}"
											disabled="${isOneTimeSoldtoAddressDisabled}" />
								</td>
							</tr>
							<tr>
								<td>
									<c:set var="maxlength">
										<%= OrderEntity.DBSIZE_ONE_TIME_SOLDTO_VAT_NO + "" %>
									</c:set>
									<html:text styleClass='textfield' style='width: 295px'
											property="materialBean.soldToVatNumber" maxlength="${maxlength}" 
											disabled="${isOneTimeSoldtoAddressDisabled}" />
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
			</div>
			</td>
			
			<!-- SHIP TO -->
			<td>
			<div style="width:305px; margin-top:4px; margin-left:4px;" class="subform"><span
				class="label"><fmt:message
				key="orderdetail.material.label.shipto" /> <c:out
				value="${form.materialBean.shiptoSapId}" /> - <c:out
				value="${form.materialBean.shiptoType}" /></span><br />
			<table cellspacing="0" cellpadding="0" border="0" width="100%"
				style="border: 0px none;" class="classic">
				<tbody>
					<tr>
						<td><span class="textfielddisabled_h" style="width: 295px;" 
							title="<c:out value="${form.materialBean.shiptoName}" />">
						<nobr> <c:out
							value="${form.materialBean.shiptoName}" />&nbsp;
						</nobr> </span></td>
					</tr>
					<tr>
						<td>
						<c:choose>
						<c:when test="${not empty form.materialBean.shiptoHouseNumber}">
						<div class="textfielddisabled_h"
							style="float: left; width: 250px;" 
							title="<c:out value="${form.materialBean.shiptoStreet}" />"><c:out
							value="${form.materialBean.shiptoStreet}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: right; margin-left: 4px; width: 35px;"
							title="<c:out value="${form.materialBean.shiptoHouseNumber}" />"><c:out
							value="${form.materialBean.shiptoHouseNumber}" />&nbsp;
						</div>													
						</c:when>
						<c:otherwise>
						<div class="textfielddisabled_h" 
							style="float: left; width: 250px;" 
							title="<c:out value="${form.materialBean.shipToStreetHouseNumber}" />"><c:out
							value="${form.materialBean.shipToStreetHouseNumber}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: right; margin-left: 4px; width: 35px;">&nbsp;
						</div>
						</c:otherwise>
						</c:choose>
						</td>
					</tr>
					<tr>
						<td>
						<div class="textfielddisabled_h" style="float: left; width: 35px;"
							title="<c:out value="${form.materialBean.shiptoCountry}" />">
						<c:out
							value="${form.materialBean.shiptoCountry}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: left; margin-left: 4px; width: 50px;"
							title="<c:out value="${form.materialBean.shiptoPostalCode}" />">
						<c:out
							value="${form.materialBean.shiptoPostalCode}" />&nbsp;</div>
						<div class="textfielddisabled_h"
							style="float: right; margin-left: 4px; width: 195px;"
							title="<c:out value="${form.materialBean.shiptoCity}" />">
						<c:out
							value="${form.materialBean.shiptoCity}" />&nbsp;
						</div>
						</td>
					</tr>
					<c:if test="${(order_auftrag_OrderDetailForm.materialBean.shiptoTypeCd == SHIPTO_TYPE_LOCATION_NOT_FIXED)}">
					<tr>
						<td><span class="textfielddisabled_h" style="width: 295px;" 
							title="<c:out value="${form.materialBean.locationNotFixed}" />">
						<nobr> <c:out
							value="${form.materialBean.locationNotFixed}" />&nbsp;
						</nobr> </span></td>
					</tr>
					<tr>
						<td><span class="textfielddisabled_h" style="width: 295px;" 
							title="<c:out value="${form.materialBean.shipToVatNumber}" />">
						<nobr> <c:out
							value="${form.materialBean.shipToVatNumber}" />&nbsp;
						</nobr> </span></td>
					</tr>
					</c:if>
				</tbody>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			

			<!-- BILL TO -->
			<td>
		<div style="width:305px; margin-top:4px; margin-left:4px;" class="subform"><span
				class="label" style="float: left;"><fmt:message
				key="orderdetail.material.label.billto" />&nbsp; </span> <div id="billtoSapIdDiv" class="label" >
				<c:out value="${form.materialBean.billtoSapId}" /></div>
			<table cellspacing="0" cellpadding="0" border="0" width="100%"
				style="border: 0px none;" class="classic">
				<tbody>
					<tr>
						<td ><html:select
							onchange="javascript:loadBillto(this.value)" size="1"
							styleClass="textfield" style="width: 295px; " property="materialBean.billtoSapBpId"
							disabled="${isBillToAndPayerDisabled || isBilltoChangeDisabled}">
							<html:option value="">-</html:option>
							<c:forEach var="entity" varStatus="status" 
								items="${form.billtoMap}">
								<html:option value="${entity.key}">
									<c:out value="${entity.value.name}" />
								</html:option>
							</c:forEach>
						</html:select></td>
					</tr>
					<tr>
						<td>
			<div id="billtoDiv"
				style="border: 0px; padding: 0px; spacing: 0px; width: 100%;"><jsp:include
				page="MaterialTabBillto.jsp"></jsp:include></div>						
						</td>
					</tr>
				</tbody>
			</table>			
		</div>
			</td>
			
			
			<!-- PAYER -->
			<td>
				<div style="width: 305px; margin-top: 4px; margin-left: 4px;" class="subform">
					<span class="label" style="float: left;">
						<fmt:message key="fields.payer.payer" />&nbsp;
					</span>
					<div id="payerSapIdDiv" class="label" >
						<c:out value="${form.materialBean.payerSapId}" />
					</div>
					<table cellspacing="0" cellpadding="0" border="0" width="100%"
							style="border: 0px none;" class="classic">
						<tbody>
							<tr>
								<td>
									<html:select onchange="javascript:loadPayer(this.value)"
											size="1" styleClass="textfield" style="width: 295px; "
											property="materialBean.payerId"
											disabled="${order_auftrag_OrderDetailForm.payerIgnored}">
											<html:option value="">-</html:option>
										<c:forEach var="entry" varStatus="status" 
												items="${form.payerMap}">
											<html:option value="${entry.key}">
												<c:out value="${entry.value.name}" />
											</html:option>
										</c:forEach>
									</html:select>
								</td>
							</tr>
							<tr>
								<td>
									<div id="payerDiv"
											style="border: 0px; padding: 0px; spacing: 0px; width: 100%;">
										<jsp:include page="MaterialTabPayer.jsp"></jsp:include>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</td>
			
		</tr>
		<tr>
			<!-- CARRIER or RAIL DEPARTURE -->
			<td>
			<div class="subform" style="width:305px; margin-top:4px; margin-left:4px;"><!-- beginn table comm. Spediteur -->
			<span class="label"><fmt:message
				key="orderdetail.material.label.comm.carrier" /> <c:out
				value="${form.materialBean.commCarrierVendorSapId}" /></span><br />

			<c:if
				test="${order_auftrag_OrderDetailForm.displayCarrierInfo}">
				<table cellspacing="0" cellpadding="0" border="0" width="100%"
					style="border: 0px none;" class="classic">
				<tbody>
					<tr>
						<td><span class="textfielddisabled_h" style="width: 295px;"
							title="<c:out value="${form.materialBean.commCarrierName}" />">
						<nobr> <c:out
							value="${form.materialBean.commCarrierName}" />&nbsp;
						</nobr> </span></td>
					</tr>
					<tr>
						<td>
						<div class="textfielddisabled_h"
							style="float: left; width: 250px;" 
							title="<c:out value="${form.materialBean.commCarrierStreet}" />"><c:out
							value="${form.materialBean.commCarrierStreet}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: right; margin-left: 4px; width: 35px;"
							title="<c:out value="${form.materialBean.commCarrierHouseNumber}" />"><c:out
							value="${form.materialBean.commCarrierHouseNumber}" />&nbsp;
						</div>
						</td>
					</tr>
					<tr>
						<td>
						<div class="textfielddisabled_h" style="float: left; width: 35px;"
							title="<c:out value="${form.materialBean.commCarrierCountry}" />">
						<c:out
							value="${form.materialBean.commCarrierCountry}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: left; margin-left: 4px; width: 50px;"
							title="<c:out value="${form.materialBean.commCarrierPostalCode}" />">
						<c:out
							value="${form.materialBean.commCarrierPostalCode}" />&nbsp;</div>
						<div class="textfielddisabled_h"
							style="float: right; margin-left: 4px; width: 195px;"
							title="<c:out value="${form.materialBean.commCarrierCity}" />">
						<c:out
							value="${form.materialBean.commCarrierCity}" />&nbsp;
						</div>
						</td>
					</tr>
				</tbody>
				</table>
			</c:if>

			<c:if
				test="${((order_auftrag_OrderDetailForm.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_RAIL_DIRECT) || 
				(order_auftrag_OrderDetailForm.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL))}">
				<table cellspacing="0" cellpadding="0" border="0" width="100%" style="table-layout:fixed;border: 0px none;" class="classic">
					<colgroup>
						<col width="25%" />
						<col width="75%" />
					</colgroup>
					<tbody>
						<tr>
							<td valign="bottom" class="formtitle"
								style="border: 0px;"><fmt:message
								key="orderdetail.material.field.departure" /></td>
							<td>
							<div class="textfielddisabled_h" style="float: left; width: 25px;"
								title="<c:out value="${form.materialBean.railDepUicCountryCd}" />">
								<c:out value="${form.materialBean.railDepUicCountryCd}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 50px;"
								title="<c:out value="${form.materialBean.railDepStationNo}" />">
								<c:out value="${form.materialBean.railDepStationNo}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 100px;"
								title="<c:out value="${form.materialBean.railDepStationName}" />">
								<c:out value="${form.materialBean.railDepStationName}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 25px;"
								title="<c:out value="${form.materialBean.railDepStationLP}" />">
								<c:out value="${form.materialBean.railDepStationLP}" />&nbsp;
							</div>
							</td>
						</tr>
						<tr>
							<td valign="bottom" class="formtitle"
								style="border: 0px;"><fmt:message
								key="orderdetail.material.field.arrival" /></td>
							<td>
							<div class="textfielddisabled_h" style="float: left; width: 25px;"
								title="<c:out value="${form.materialBean.railArrUicCountryCd}" />">
								<c:out value="${form.materialBean.railArrUicCountryCd}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left;  margin-left: 4px; width: 50px"
								title="<c:out value="${form.materialBean.railArrStationNo}" />">
								<c:out value="${form.materialBean.railArrStationNo}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left;  margin-left: 4px; width: 100px"
								title="<c:out value="${form.materialBean.railArrStationName}" />">
								<c:out value="${form.materialBean.railArrStationName}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left;  margin-left: 4px; width: 25px"
								title="<c:out value="${form.materialBean.railArrStationUP}" />">
								<c:out value="${form.materialBean.railArrStationUP}" />&nbsp;
							</div>
							</td>
						</tr>
	
					</tbody>
				</table>
			</c:if></div>
			</td>

			<!-- Executing Carrier -->
			<td>
			<div class="subform" style="width:305px; margin-top:4px; margin-left:4px;"><!-- beginn table exec Spediteur -->
			<span class="label"><fmt:message
				key="orderdetail.material.label.exec.carrier" /> <c:out
				value="${form.materialBean.execCarrierVendorSapId}" /></span><br />

			<c:if
				test="${order_auftrag_OrderDetailForm.displayCarrierInfo}">
				<table cellspacing="0" cellpadding="0" border="0" width="100%"
					style="border: 0px none;" class="classic">
				<tbody>
					<tr>
						<td><span class="textfielddisabled_h" style="width: 295px;"
							title="<c:out value="${form.materialBean.execCarrierName}" />">
						<nobr> <c:out
							value="${form.materialBean.execCarrierName}" />&nbsp;
						</nobr> </span></td>
					</tr>
					<tr>
						<td>
						<div class="textfielddisabled_h"
							style="float: left; width: 250px;" 
							title="<c:out value="${form.materialBean.execCarrierStreet}" />"><c:out
							value="${form.materialBean.execCarrierStreet}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: right; margin-left: 4px; width: 35px;"
							title="<c:out value="${form.materialBean.execCarrierHouseNumber}" />"><c:out
							value="${form.materialBean.execCarrierHouseNumber}" />&nbsp;
						</div>
						</td>
					</tr>
					<tr>
						<td>
						<div class="textfielddisabled_h" style="float: left; width: 35px;"
							title="<c:out value="${form.materialBean.execCarrierCountry}" />">
						<c:out
							value="${form.materialBean.execCarrierCountry}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: left; margin-left: 4px; width: 50px;"
							title="<c:out value="${form.materialBean.execCarrierPostalCode}" />">
						<c:out
							value="${form.materialBean.execCarrierPostalCode}" />&nbsp;</div>
						<div class="textfielddisabled_h"
							style="float: right; margin-left: 4px; width: 195px;"
							title="<c:out value="${form.materialBean.execCarrierCity}" />">
						<c:out
							value="${form.materialBean.execCarrierCity}" />&nbsp;
						</div>
						</td>
					</tr>
				</tbody>
				</table>
			</c:if>

			<c:if
				test="${((order_auftrag_OrderDetailForm.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_RAIL_DIRECT) || 
				(order_auftrag_OrderDetailForm.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL))}">
				<table cellspacing="0" cellpadding="0" border="0" width="100%" style="table-layout:fixed;border: 0px none;" class="classic">
					<colgroup>
						<col width="25%" />
						<col width="75%" />
					</colgroup>
					<tbody>
						<tr>
							<td valign="bottom" class="formtitle"
								style="border: 0px;"><fmt:message
								key="orderdetail.material.field.departure" /></td>
							<td>
							<div class="textfielddisabled_h" style="float: left; width: 25px;"
								title="<c:out value="${form.materialBean.railDepUicCountryCd}" />">
								<c:out value="${form.materialBean.railDepUicCountryCd}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 50px;"
								title="<c:out value="${form.materialBean.railDepStationNo}" />">
								<c:out value="${form.materialBean.railDepStationNo}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 100px;"
								title="<c:out value="${form.materialBean.railDepStationName}" />">
								<c:out value="${form.materialBean.railDepStationName}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 25px;"
								title="<c:out value="${form.materialBean.railDepStationLP}" />">
								<c:out value="${form.materialBean.railDepStationLP}" />&nbsp;
							</div>
							</td>
						</tr>
						<tr>
							<td valign="bottom" class="formtitle"
								style="border: 0px;"><fmt:message
								key="orderdetail.material.field.arrival" /></td>
							<td>
							<div class="textfielddisabled_h" style="float: left; width: 25px;"
								title="<c:out value="${form.materialBean.railArrUicCountryCd}" />">
								<c:out value="${form.materialBean.railArrUicCountryCd}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left;  margin-left: 4px; width: 50px"
								title="<c:out value="${form.materialBean.railArrStationNo}" />">
								<c:out value="${form.materialBean.railArrStationNo}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left;  margin-left: 4px; width: 100px"
								title="<c:out value="${form.materialBean.railArrStationName}" />">
								<c:out value="${form.materialBean.railArrStationName}" />&nbsp;
							</div>
							<div class="textfielddisabled_h" style="float: left;  margin-left: 4px; width: 25px"
								title="<c:out value="${form.materialBean.railArrStationUP}" />">
								<c:out value="${form.materialBean.railArrStationUP}" />&nbsp;
							</div>
							</td>
						</tr>
	
					</tbody>
				</table>
			</c:if></div>
			</td>
			
			
		</tr>
	</tbody>
</table>

<!-- End split table 50/50  part 1 -->
<hr
	style="border: 0px none; height: 1px; background-color: rgb(204, 204, 204);" />

<!-- height: 250; -->
<div id="delNoteDiv"
	style="border: 0px; padding: 0px; width: 100%;"><jsp:include
	page="MaterialTabDelNote.jsp"></jsp:include>
</div>






<!--</html:form>-->
