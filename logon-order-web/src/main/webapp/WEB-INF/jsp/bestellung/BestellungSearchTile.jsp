<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BestellungSearchTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx" %>
<%@page import="com.crh.logon.order.web.ajax.OrderAjaxService"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.actions.bestellung.BestellungSearchEntryAction"%>
<%@page import="java.util.HashMap"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<c:choose>
	<c:when test="${ApplicationSubTabs2Bean == 'ordermonitoring'}">
		<c:set var="mode" value="monitoring" scope="request"/>
		<c:set var="defaultSort" value="timeToRedLine"/>
		<c:set var="defaultSortDir" value="DESC"/>
	</c:when>
	<c:when test="${ApplicationTabsBean == 'extorderlist'}">
		<c:set var="mode" value="externalsearch" scope="request"/>
		<c:set var="defaultSort" value="deliveryToDat"/>
		<c:set var="defaultSortDir" value="DESC"/>
	</c:when>
	<c:otherwise>
		<c:set var="mode" value="search" scope="request"/>
		<c:set var="defaultSort" value="deliveryToDat"/>
		<c:set var="defaultSortDir" value="DESC"/>
	</c:otherwise>
</c:choose>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/OrderAjaxService.js'/>"></script>

<script type="text/javascript" src='<crh-common:url value="/js/logon-validation.js"/>'></script>

<style type="text/css">

	.dynlabel {
		font-size: 12px;
		color: #666666; 	
	}
	
	.listclass {
		overflow: hidden;
	}
	
	.tinylabel {
		font-size: 11px;
	}
	
	table tr td .tinylabel {
		padding-bottom: 0; 
		padding-top: 0;
	}
	
	/* IE */
	.x-panel-body {
		zoom: normal;
	}
	
	.x-panel-bwrap {
		zoom: normal;
	}
</style>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.form.PeriodSelector.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.OrderCountingPagingToolbar.js'/>"></script>

<script type="text/javascript">
var actions = {
		ACTION_SAVE_PREFERENCES: '<%=BestellungSearchEntryAction.ACTION_SAVE_PREFERENCES%>'
	};

var orderMonitor;
var autoRefresh = false;

var texts = {
	extjsProcessing: '<fmt:message key="extjs.processing"/>'
};

Ext.onReady(function(){
	// --- Filters ---
	<c:choose>
		<c:when test="${mode == 'externalsearch'}">
			<jsp:include page="BestellungExternalSearchFiltersInc.jsp"/>
		</c:when>
		<c:otherwise>
			<jsp:include page="BestellungSearchFiltersInc.jsp"/>
		</c:otherwise>
	</c:choose>
	

	// --- Result list ---
<c:choose>
	<c:when test="${mode != 'externalsearch'}">
		<jsp:include page="BestellungSearchResultGridInc.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="BestellungExternalSearchResultGridInc.jsp"/>
	</c:otherwise>
</c:choose>

    // --- Initial data load ---
    orderSearchStore.setDefaultSort('<c:out value="${defaultSort}"/>', '<c:out value="${defaultSortDir}"/>');
	searchArea.initSearchCrits(false, grid);
	initStores();
	
<c:if test="${mode != 'search'}">
    searchArea.searchOrders();
</c:if>
    
<c:if test="${mode == 'search'}">
    Ext.get('buttonCalc').on('click', function() {
    	calcQuantities();	
    });
</c:if>
    
    // --- Buttons ---
    
<c:if test="${mode != 'externalsearch'}">
	Ext.get('buttonToggleAdvanced').on('click', function() {
		searchArea.toggleAdvancedSearch(); 
	});
</c:if>

Ext.get('buttonSearch').on('click', logonPopupHideCallback);

<c:if test="${mode == 'search'}">
	Ext.get('buttonExport').on('click', function() {
		OrderAjaxService.checkExportRestriction(searchArea.getSearchCrits(), function(data){
			if(data) {
				var url = 'BestellungSearchEntry.do?export';
				var crits = searchArea.getSearchCrits();
				if (crits.orderNumber) {
					url += '&orderNumber=' + crits.orderNumber;
				}
				if (crits.ldsDeliveryNoteNo) {
					url += '&ldsDeliveryNoteNo=' + crits.ldsDeliveryNoteNo;
				}
				if (crits.soldtoSapBpId) {
					url += '&soldtoSapBpId=' + crits.soldtoSapBpId;
				}
				if (crits.shiptoSapBpId) {
					url += '&shiptoSapBpId=' + crits.shiptoSapBpId;
				}
				if (crits.billtoSapBpId) {
					url += '&billtoSapBpId=' + crits.billtoSapBpId;
				}
				if (crits.sapMaterialId) {
					url += '&sapMaterialId=' + crits.sapMaterialId;
				}
				if (crits.deliveryFromDat) {
					url += '&deliveryFromDat=' + crits.deliveryFromDat;
				}
				if (crits.deliveryToDat) {
					url += '&deliveryToDat=' + crits.deliveryToDat;
				}
				if (crits.deliveryTypeCdList) {
					url += '&deliveryTypeCdList=' + crits.deliveryTypeCdList;
				}
				if (crits.destinationCdList) {
					url += '&destinationCdList=' + crits.destinationCdList;
				}
				if (crits.shippingpointId) {
					url += '&shippingpointId=' + crits.shippingpointId;
				}
				if (crits.liveFlag) {
					url += '&liveFlag=' + crits.liveFlag;
				}
				if (crits.matPackagingType) {
					url += '&matPackagingType=' + crits.matPackagingType;
				}
				if (crits.poolId) {
					url += '&poolId=' + crits.poolId;
				}
				if (crits.execCarrierId) {
					url += '&execCarrierId=' + crits.execCarrierId;
				}
				if (crits.carrierIdList) {
					url += '&carrierIdList=' + crits.carrierIdList;
				}
				if (crits.statusList) {
					url += '&statusList=' + crits.statusList;
				}
				if (crits.creationTypeList) {
					url += '&creationTypeList=' + crits.creationTypeList;
				}
				if (crits.sareaId) {
					url += '&sareaId=' + crits.sareaId;
				}
				if (crits.shiptoSofficeCd) {
					url += '&shiptoSofficeCd=' + crits.shiptoSofficeCd;
				}
				if (crits.shiptoSgroupCd) {
					url += '&shiptoSgroupCd=' + crits.shiptoSgroupCd;
				}
				if (crits.plantCdList) {
					url += '&plantCdList=' + crits.plantCdList;
				}
				if (crits.vehicleId) {
					url += '&vehicleId=' + crits.vehicleId;
				}
				if (crits.vehicleType) {
					url += '&vehicleType=' + crits.vehicleType;
				}
		
				window.open(url);
			} else {
				Logon.message.warning('<fmt:message key="ordersearch.infomessage2"/>');
			}
		});
	});
</c:if>

<c:if test="${mode == 'externalsearch'}">
Ext.get('buttonExport').on('click', function() {
	var url = 'BestellungExternalSearchEntry.do?export';
	var crits = searchArea.getSearchCrits();
	if (crits.extOrderNumber) {
		url += '&extOrderNumber=' + crits.extOrderNumber;
	}
	if (crits.deliveryFromDat) {
		url += '&deliveryFromDat=' + crits.deliveryFromDat;
	}
	if (crits.deliveryToDat) {
		url += '&deliveryToDat=' + crits.deliveryToDat;
	}
	if (crits.statusList) {
		url += '&statusList=' + crits.statusList;
	}
	if (crits.vehicleId) {
		url += '&vehicleId=' + crits.vehicleId;
	}
	if (crits.extDeliveryNote) {
		url += '&extDeliveryNote=' + crits.extDeliveryNote;
	}

	window.open(url);
});
</c:if>

<c:if test="${mode == 'externalsearch'}">
Ext.get('buttonSavePreferences').on('click', function() {
	var crits = searchArea.getSearchCrits();
	var columnWidths = searchArea.getVisibleColumnWidths();
	var columnIndexes = searchArea.getVisibleColumnIndexes();
	
	var params = {"formAction":actions.ACTION_SAVE_PREFERENCES,
			"inDeliveryFromDate": crits.deliveryFromDat,
			"inDeliveryToDate": crits.deliveryToDat,
			"inOrderStatusList": crits.statusList,
			"extDeliveryNote": crits.extDeliveryNote,
			"vehicleCompositeId": crits.vehicleCompositeId,
			"vehicleName": crits.vehicleName,
			
			"wExtOrderNumber": columnWidths.extOrderNumber,
			"wExtDeliveryNote": columnWidths.extDeliveryNote,
			"wShiptoName": columnWidths.shiptoName,
			"wShiptoLoadUnload": columnWidths.shiptoLoadUnload,
			"wShiptoContactPersonTxt": columnWidths.shiptoContactPersonTxt,
			
			"wShiptoStreetHouseNo": columnWidths.shiptoStreetHouseNo,
			"wShiptoCountry": columnWidths.shiptoCountry,
			"wShiptoPostalCode": columnWidths.shiptoPostalCode,
			"wShiptoCity": columnWidths.shiptoCity,
			"wStatusTxt": columnWidths.statusTxt,
			
			"wMaterialName": columnWidths.materialName,
			"wOrderedQuantity": columnWidths.orderedQuantity,
			"wOrderedUomName": columnWidths.orderedUomName,
			"wDeliveredQuantity": columnWidths.deliveredQuantity,
			"wDeliveredUomName": columnWidths.deliveredUomName,
			
			"wShippingpointName": columnWidths.shippingpointName,
			"wSpLoadUnload": columnWidths.spLoadUnload,
			"wSpContactPersonTxt": columnWidths.spContactPersonTxt,
			"wSpStreetHouseNo": columnWidths.spStreetHouseNo,
			"wSpCountry": columnWidths.spCountry,
			
			"wSpPostalCode": columnWidths.spPostalCode,
			"wSpCity": columnWidths.spCity,
			"wDeliveryFrom": columnWidths.deliveryFrom,
			"wDeliveryTo": columnWidths.deliveryTo,
			"wPoolName": columnWidths.poolName,
			
			"wCarrierName": columnWidths.carrierName,
			"wTruckName": columnWidths.truckName,
			
			"idxExtOrderNumber": columnIndexes.extOrderNumber,
			"idxExtDeliveryNote": columnIndexes.extDeliveryNote,
			"idxExtOrderNumber": columnIndexes.extOrderNumber,
			"idxExtDeliveryNote": columnIndexes.extDeliveryNote,
			"idxShiptoName": columnIndexes.shiptoName,
			"idxShiptoLoadUnload": columnIndexes.shiptoLoadUnload,
			"idxShiptoContactPersonTxt": columnIndexes.shiptoContactPersonTxt,
			
			"idxShiptoStreetHouseNo": columnIndexes.shiptoStreetHouseNo,
			"idxShiptoCountry": columnIndexes.shiptoCountry,
			"idxShiptoPostalCode": columnIndexes.shiptoPostalCode,
			"idxShiptoCity": columnIndexes.shiptoCity,
			"idxStatusTxt": columnIndexes.statusTxt,
			
			"idxMaterialName": columnIndexes.materialName,
			"idxOrderedQuantity": columnIndexes.orderedQuantity,
			"idxOrderedUomName": columnIndexes.orderedUomName,
			"idxDeliveredQuantity": columnIndexes.deliveredQuantity,
			"idxDeliveredUomName": columnIndexes.deliveredUomName,

			"idxShippingpointName": columnIndexes.shippingpointName,
			"idxSpLoadUnload": columnIndexes.spLoadUnload,
			"idxSpContactPersonTxt": columnIndexes.spContactPersonTxt,
			"idxSpStreetHouseNo": columnIndexes.spStreetHouseNo,
			"idxSpCountry": columnIndexes.spCountry,
			
			"idxSpPostalCode": columnIndexes.spPostalCode,
			"idxSpCity": columnIndexes.spCity,
			"idxDeliveryFrom": columnIndexes.deliveryFrom,
			"idxDeliveryTo": columnIndexes.deliveryTo,
			"idxPoolName": columnIndexes.poolName,
			
			"idxCarrierName": columnIndexes.carrierName,
			"idxTruckName": columnIndexes.truckName
	};
	
	addFormElements(params, "BestellungExternalSearchForm");
	Ext.Ajax.request({
		url: 'BestellungExternalSearchEntry.do',
		params: params,
		success : function(response) {
			var result = Ext.decode(response.responseText);
			if ('success' in result){
				Logon.message.success(result.success);
			}
		}
	});
</c:if>

<c:if test="${mode != 'externalsearch'}">
Ext.get('buttonSavePreferences').on('click', function() {
	var crits = searchArea.getSearchCrits();
	var columnWidths = searchArea.getVisibleColumnWidths();
	var columnIndexes = searchArea.getVisibleColumnIndexes();
	
	var params = {"formAction":actions.ACTION_SAVE_PREFERENCES,
			"inDeliveryFromDate": crits.deliveryFromDat,
			"ldsDeliveryNoteNo": crits.ldsDeliveryNoteNo,
			"inDeliveryToDate": crits.deliveryToDat,
			"inOrderStatusList": crits.statusList,
			"inOrderCreationTypeList": crits.creationTypeList,
			"inDestinationCdList": crits.destinationCdList,
			"inCarrierIdList": crits.carrierIdList,
			"matPackagingType" : crits.matPackagingType,
			"sapMaterialId": crits.sapMaterialId,
			"sareaId": crits.sareaId,
			"soldtoSapBpId": crits.soldtoSapBpId,
			"liveFlag": crits.liveFlag,
			"poolId": crits.poolId,
			"execCarrierId": crits.execCarrierId,
			"inPlantCdList": crits.plantCdList,
			"shiptoSofficeCd": crits.shiptoSofficeCd,
			"shiptoSapBpId": crits.shiptoSapBpId,
			"shippingpointId": crits.shippingpointId,
			"shiptoSgroupCd": crits.shiptoSgroupCd,
			"billtoSapBpId": crits.billtoSapBpId,
			"vehicleCompositeId": crits.vehicleCompositeId,
			"vehicleName": crits.vehicleName,
			"inDeliveryTypeCdList": crits.deliveryTypeCdList,
			"columnWidthValues": columnWidths,
			
			"wTimeToRedLine": columnWidths.timeToRedLine,
			"wOrderNumber": columnWidths.orderNumber,
			"wLdsDeliveryNoteNo": columnWidths.ldsDeliveryNoteNo,
			"wDistributionDestCd": columnWidths.distributionDestCd,
			"wSoldtoName1": columnWidths.soldtoName1,
			"wShiptoName1": columnWidths.shiptoName1,
			
			"wShiptoCity": columnWidths.shiptoCity,
			"wStatus": columnWidths.status,
			"wMaterialTxt": columnWidths.materialTxt,
			"wOrderedQuantity": columnWidths.orderedQuantity,
			"wOrderedUomTxt": columnWidths.orderedUomTxt,
			
			"wLoadedQuantity": columnWidths.loadedQuantity,
			"wLoadedUomTxt": columnWidths.loadedUomTxt,
			"wReturnedQuantity": columnWidths.returnedQuantity,
			"wReturnedUomTxt": columnWidths.returnedUomTxt,
			"wDeliveredQuantity": columnWidths.deliveredQuantity,
			
			"wDeliveredUomTxt": columnWidths.deliveredUomTxt,
			"wDeliveryTypeTxt": columnWidths.deliveryTypeTxt,
			"wShippingpointName": columnWidths.shippingpointName,
			"wDeliveryFromDat": columnWidths.deliveryFromDat,
			"wDeliveryToDat": columnWidths.deliveryToDat,
			
			"wPlannedDeliveryDat": columnWidths.plannedDeliveryDat,
			"wPoolName": columnWidths.poolName,
			"wCommCarrierName": columnWidths.commCarrierName,
			"wExecCarrierName": columnWidths.execCarrierName,
			"wBilltoName1": columnWidths.billtoName1,
			
			"wDriverFullName": columnWidths.driverFullName,
			"wVehicleName": columnWidths.vehicleName,
			"wOrderValidationCode": columnWidths.orderValidationCode,
			"wNoteOrderMgmt": columnWidths.noteOrderMgmt,
			"wNoteDelivery": columnWidths.noteDelivery,
			"wNoteDispatching": columnWidths.noteDispatching,
			"wNoteTruckDriver": columnWidths.noteTruckDriver,
			"wNoteRailwayDisp": columnWidths.noteRailwayDisp,
			"wOrderNumberFromSeqUsage": columnWidths.orderNumberFromSeqUsage,
			"wShiptoAddress": columnWidths.shiptoAddress,
			"wLoadingTs": columnWidths.loadingTs,
			"wLdsDeliveryNoteNo": columnWidths.ldsDeliveryNoteNo,
			"wItrackStatusCd": columnWidths.itrackStatusCd,

			"idxTimeToRedLine": columnIndexes.timeToRedLine,
			"idxOrderNumber": columnIndexes.orderNumber,
			"idxLdsDeliveryNoteNo": columnIndexes.ldsDeliveryNoteNo,
			"idxDistributionDestCd": columnIndexes.distributionDestCd,
			"idxSoldtoName1": columnIndexes.soldtoName1,
			"idxShiptoName1": columnIndexes.shiptoName1,
			
			"idxShiptoCity": columnIndexes.shiptoCity,
			"idxStatus": columnIndexes.status,
			"idxMaterialTxt": columnIndexes.materialTxt,
			"idxOrderedQuantity": columnIndexes.orderedQuantity,
			"idxOrderedUomTxt": columnIndexes.orderedUomTxt,
			
			"idxLoadedQuantity": columnIndexes.loadedQuantity,
			"idxLoadedUomTxt": columnIndexes.loadedUomTxt,
			"idxReturnedQuantity": columnIndexes.returnedQuantity,
			"idxReturnedUomTxt": columnIndexes.returnedUomTxt,
			"idxDeliveredQuantity": columnIndexes.deliveredQuantity,
			
			"idxDeliveredUomTxt": columnIndexes.deliveredUomTxt,
			"idxDeliveryTypeTxt": columnIndexes.deliveryTypeTxt,
			"idxShippingpointName": columnIndexes.shippingpointName,
			"idxDeliveryFromDat": columnIndexes.deliveryFromDat,
			"idxDeliveryToDat": columnIndexes.deliveryToDat,
			
			"idxPlannedDeliveryDat": columnIndexes.plannedDeliveryDat,
			"idxPoolName": columnIndexes.poolName,
			"idxCommCarrierName": columnIndexes.commCarrierName,
			"idxExecCarrierName": columnIndexes.execCarrierName,
			"idxBilltoName1": columnIndexes.billtoName1,
			
			"idxDriverFullName": columnIndexes.driverFullName,
			"idxVehicleName": columnIndexes.vehicleName,
			"idxOrderValidationCode": columnIndexes.orderValidationCode,
			"idxNoteOrderMgmt": columnIndexes.noteOrderMgmt,
			"idxNoteDelivery": columnIndexes.noteDelivery,
			"idxNoteDispatching": columnIndexes.noteDispatching,
			"idxNoteTruckDriver": columnIndexes.noteTruckDriver,
			"idxNoteRailwayDisp": columnIndexes.noteRailwayDisp,
			"idxOrderNumberFromSeqUsage": columnIndexes.orderNumberFromSeqUsage,
			"idxShiptoAddress": columnIndexes.shiptoAddress,
			"idxLoadingTs": columnIndexes.loadingTs,
			"idxItrackStatusCd": columnIndexes.itrackStatusCd
	};
</c:if>
	
<c:choose>
	<c:when test="${mode == 'search'}">
		Ext.Ajax.request({
			url: 'BestellungSearchEntry.do',
			params: params,
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if ('success' in result){
					Logon.message.success(result.success);
				}
			}
		});
	</c:when>
	<c:when test="${mode == 'monitoring'}">
		Ext.Ajax.request({
			url: 'BestellungMonitoringEntry.do',
			params: params,
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if ('success' in result){
					Logon.message.success(result.success);
				}
			}
		});
	</c:when>
</c:choose>

});

Ext.get('buttonResetField').on('click', function() {
	searchArea.initSearchCrits(true, grid);
});

// --- Automatic refresh ---
<c:if test="${mode == 'monitoring'}">
	orderMonitor = {
		refreshRate: <%= ConfigBeanProvider.getConfigBean().getAppDefaultMonitoringRefreshInterval() %>,
		taskRunner: new Ext.util.TaskRunner(),
		task: {
		    run: function() {
		        if (this.nextRefresh == 1) {
		            this.nextRefresh = orderMonitor.refreshRate;
				    orderSearchStore.setDefaultSort('<c:out value="${defaultSort}"/>', '<c:out value="${defaultSortDir}"/>');
				    searchArea.searchOrders();
		        }
		        else {
		            this.nextRefresh--;
		        }
	            grid.setTitle('Incoming orders (refreshed in ' + this.nextRefresh + ' seconds)');
	            
		    },
		    nextRefresh: <%= ConfigBeanProvider.getConfigBean().getAppDefaultMonitoringRefreshInterval() %>,
		    interval: 1000, // 1 second (interval for the counter, not for the refresh)
		    active: false
		},
		start: function() {
			this.task.nextRefresh = this.refreshRate;
			//this.task.scope = orderMonitor.task;
			//this.taskRunner.start(this.task);
			if (!this.task.active) {
				Ext.TaskMgr.start(this.task);
				this.task.active = true;
			}
		},
		stop: function() {
			if (this.task.active) {
				Ext.TaskMgr.stop(orderMonitor.task);
				this.task.active = false;
				grid.setTitle('<fmt:message key="ordersearch.table.header.title"/>');
			}
		}
	}
	
	autoRefreshField.setValue(autoRefresh);
</c:if>	
   
});

// TODO eliminate
function toCommaDelimitedStr(arr) {
	var res = '';
	for (var i=0;i<arr.length;i++) {
		res += arr[i];
		if (i + 1<arr.length) {
			res += ',';
		}
	}	
	return res;
}

<c:if test="${mode == 'externalsearch'}">
function refreshOrders() {

	// called from the external popup on closing, can be used to refresh the search result list.
}
</c:if>
</script>

<%-- Only for Monitoring --%>
<c:if test="${mode == 'monitoring'}">
	<jsp:include page="../SubTab7Tile.jsp"/>
	<!-- Begin table 3 rows -->
	<table class="big_form2">
	
		<tr class="classicblue">
			<th class="classicblue"><fmt:message key="tabitem.monitoring" />
			/ <fmt:message key="tabsubitem.7.1" /></th>
		</tr>
	      <tr>
	    
	      <td>
			<jsp:include page="../Sub1Tab7Tile.jsp"/>
</c:if>   

<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
<tr>
	<td class="border" style="background-color: F9F9F9; padding: 2px 0 0 10px;">
		<div id="divSimpleSearch">
<c:choose>
	<c:when test="${mode != 'externalsearch'}">
			<table style="width: 100%;">
				<colgroup>
					<col width="15%">
					<col width="*">
					<col width="12%">
					<col width="12%">
					<col width="12%">
					<col width="12%">
					<col width="12%">
					<col width="15%">
				</colgroup>
				<tr>
					<td class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="width: 100%;">
							<colgroup>
								<col width="50%">
								<col width="50%">
							</colgroup>
							<tr>
								<td style="padding: 0; margin: 0;">
									<fmt:message key="fields.order.delivery.from"/>
								</td>
								<td style="padding: 0; margin: 0;">
									<fmt:message key="fields.order.delivery.to"/>
								</td>
							</tr>
						</table>
					</td>
					<c:if test="${mode == 'monitoring'}">
					<td class="tinylabel">
						<fmt:message key="bestellung.monitoring.live"/>
					</td>
					</c:if>
					<td class="tinylabel">
						<fmt:message key="fields.order.number"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="fields.order.ldsDeliveryNoteNo"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="fields.order.distributiondestination"/>
					</td>					
					<td class="tinylabel">
						<fmt:message key="fields.order.status"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="fields.produkt.produkt"/>
					</td>
					<c:if test="${mode != 'monitoring'}">
					<td class="tinylabel">
						<input id="buttonCalc" type="button" class="SmallButtonStyle" value="<fmt:message key="bestellung.search.button.calc"/>"/>
					</td>
					<td rowspan="2" class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
							<tr>
								<td>
									<input id="buttonSearch" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.search"/>"/><br/>
								</td>
								<td>
									<input id="buttonExport" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.export"/>"/><br/>
								</td>
							</tr>
							<tr>
								<td>
									<input id="buttonToggleAdvanced" type="button" class="SmallButtonStyleDown" value="<fmt:message key="buttons.expand"/>"/>
								</td>
							</tr>
						</table>
					</td>
					</c:if>
					<c:if test="${mode == 'monitoring'}">
					<td rowspan="2" class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
							<tr>
								<td>
									<input id="buttonSearch" type="button" class="SmallButtonStyle" value="<fmt:message key="bestellung.monitoring.refresh"/>"/><br/>
								</td>
							</tr>
							<tr>
								<td>
									<input id="buttonToggleAdvanced" type="button" class="SmallButtonStyleDown" value="<fmt:message key="buttons.expand"/>"/>
								</td>
							</tr>
						</table>
					</td>
					</c:if>
					<td rowspan="2" class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
							<tr>
								<td>
									<input type="button" id="buttonSavePreferences" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>"/><br/>
								</td>
							</tr>
							<tr>
								<td>
									<input type="button" id="buttonResetField" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" style="width: 100%; padding: 0; margin: 0;">
							<colgroup>
								<col width="50%">
								<col width="50%">
							</colgroup>
							<tr>
								<td style="padding: 0; margin: 0;">
									<input type="text" id="deliveryFromDatStr" style="width: 10px;"/>
								</td>
								<td style="padding: 0; margin: 0;">
									<table cellspacing="0" cellpadding="0" style="width: 100%; border-collapse: collapse;">
										<colgroup>
											<col>
											<col width="100%">
										</colgroup>
										<tr>
											<td style="padding: 0; margin: 0;">
												<input type="text" id="deliveryToDatStr" style="width: 10px;" />
											</td>
											<td style="padding: 0; margin: 0; align: left; vertical-align: top;">
												<div id="periodSelectorTriggerId" style="padding: 0; margin: 0; align: left; vertical-align: top;"></div>
												<%-- <div id="periodSelectorId" style="visibility: hidden;"></div> --%> 
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
					<c:if test="${mode == 'monitoring'}">
					<td>
						<input type="text" id="autoRefresh"/>
					</td>
					</c:if>
					<td>
					  	<input type="text" id="orderNumber"/>
					</td>
					<td>
					  	<input type="text" id="ldsDeliveryNoteNo"/>
					</td>
					<td>
						<input type="text" id="distributionDestCdField" style="width: 180px;">
					</td>					
					<td>
						<input type="text" id="orderStatusField" style="width: 180px;">
					</td>
					<td>
						<input type="text" id="sapMatCombo"/>						
					</td>
					<c:if test="${mode != 'monitoring'}">
					<td>
						<input type="text" id="summedQuantity" style="width: 40%" />
						<input type="text" id="orderItemCount" style="width: 40%" />
					</td>
					</c:if>
				</tr>
			</table>
	</c:when>
	<c:when test="${mode == 'externalsearch'}">
			<table style="width: 100%;">
				<colgroup>
					<col width="16%">
					<col width="10%">
					<col width="14%">
					<col width="14%">
					<col width="*">
				</colgroup>
				<tr>
					<td class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="width: 100%;">
							<colgroup>
								<col width="50%">
								<col width="50%">
							</colgroup>
							<tr>
								<td style="padding: 0; margin: 0;">
									<fmt:message key="fields.order.delivery.from"/>
								</td>
								<td style="padding: 0; margin: 0;">
									<fmt:message key="fields.order.delivery.to"/>
								</td>
							</tr>
						</table>
					</td>
					<td class="tinylabel">
						<fmt:message key="fields.order.number"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="fields.order.status"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.truck"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.deliveryNote"/>
					</td>
					<td rowspan="2" class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
							<tr>
								<td>
									<input id="buttonSearch" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.search"/>"/><br/>
								</td>
								<td>
									<input id="buttonExport" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.export"/>"/><br/>
								</td>
							</tr>
						</table>
					</td>
					<td rowspan="2" class="tinylabel">
						<table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
							<tr>
								<td>
									<input type="button" id="buttonSavePreferences" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>"/><br/>
								</td>
							</tr>
							<tr>
								<td>
									<input type="button" id="buttonResetField" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table cellspacing="0" cellpadding="0" style="width: 100%; padding: 0; margin: 0;">
							<colgroup>
								<col width="50%">
								<col width="50%">
							</colgroup>
							<tr>
								<td style="padding: 0; margin: 0;">
									<input type="text" id="deliveryFromDatStr" style="width: 10px;"/>
								</td>
								<td style="padding: 0; margin: 0;">
									<table cellspacing="0" cellpadding="0" style="width: 100%; border-collapse: collapse;">
										<colgroup>
											<col>
											<col width="100%">
										</colgroup>
										<tr>
											<td style="padding: 0; margin: 0;">
												<input type="text" id="deliveryToDatStr" style="width: 10px;" />
											</td>
											<td style="padding: 0; margin: 0; align: left; vertical-align: top;">
												<div id="periodSelectorTriggerId" style="padding: 0; margin: 0; align: left; vertical-align: top;"></div>
												<%-- <div id="periodSelectorId" style="visibility: hidden;"></div> --%> 
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
					<td>
					  	<input type="text" id="orderNumber"/>
					</td>
					<td>
						<input type="text" id="orderStatusField" style="width: 180px;">
					</td>
					<td>
						<input type="text" id="vehicleCompositeId"/>						
					</td>
					<td>
						<input type="text" id="deliveryNote"/>						
					</td>
				</tr>
			</table>
	</c:when>
	
</c:choose>

<c:if test="${mode != 'externalsearch'}">					
		<div id="divAdvancedSearch" style="visibility: hidden; border-top: 1px solid #CCC;">
			<table style="width: 100%;">
				<colgroup>
					<col width="20%">
					<col width="20%">
					<col width="20%">
					<col width="20%">
					<col width="20%">
				</colgroup>
				<tr>
					<td class="tinylabel">
						<fmt:message key="fields.shipto.sales.area"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="dispokarte.texte.kunde"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.pool"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.plant"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.mng.field.creation.type"/>
					</td>
				</tr>
				<tr>
					<td>
						<input type="text" id="sareaId"/>
					</td>
					<td>
						<input type="text" id="soldtoCombo"/>						
					</td>
					<td>
						<input type="text" id="poolId"/>						
					</td>
					<td>
						<input type="text" id="plantCd"/>
					</td>
					<td>
						<input type="text" id="orderCreationTypeField">
					</td>
				</tr>

				<tr>
					<td class="tinylabel">
						<fmt:message key="orderdetail.rep.field.sales.office"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.material.label.shipto" />
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.commcarrier"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.shpoint"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="bestellung.monitoring.fields.matpackagingtype"/>
					</td>
				</tr>
				<tr>
					<td>
						<input type="text" id="sofficeCd"/>
					</td>
					<td>
						<input type="text" id="shiptoCombo"/>						
					</td>
					<td>
						<input type="text" id="carrierId"/>						
					</td>
					<td>
						<input type="text" id="shippingpointId"/>
					</td>
					<td>
						<input type="text" id="matPackagingTypeCombo"/>
					</td>
				</tr>

				<tr>
					<td class="tinylabel">
						<fmt:message key="orderdetail.rep.field.sales.group"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.material.label.billto" />
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.execcarrier"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.mng.field.delivery.type"/>
					</td>
					<td class="tinylabel">
						<fmt:message key="orderdetail.main.field.truck"/>
					</td>
				</tr>
				<tr>
					<td>
						<input type="text" id="sgroupCd"/>
					</td>
					<td>
						<input type="text" id="billtoCombo"/>						
					</td>
					<td>
						<input type="text" id="execCarrierId"/>						
					</td>
					<td>
						<input type="text" id="deliveryTypeCd"/>
					</td>
					<td>
						<input type="text" id="vehicleCompositeId"/>	
					</td>
				</tr>
			</table>
		</div>
</c:if> <%-- END if not externalsearch --%>
	</td>
</tr>
</table>

<div id="divSearchResults" style="margin-top: 2px"/>

<%-- Only for Monitoring --%>
<c:if test="${mode == 'monitoring'}">
			</td>
		</tr>
	</table>
</c:if>

<%-- END $RCSfile: BestellungSearchTile.jsp,v $ --%>
