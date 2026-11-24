<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: DispoTile2.jsp,v $ --%>

<%@ page session="true"
	import="com.crh.logon.web.utils.Authorization"
%>
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.actions.dispo.DispoAction"%>
<%@ page import="com.crh.logon.utils.time.DateTimePatterns" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<%-- 
	If there are no pools to choose from (eg. because of data authorization),
	the screen could not be used.
	(Note: A warning message describes the situation for the user.)
  --%>
<c:if test="${!empty order_dispo_DispoForm.pools}">

<script type="text/javascript" src="<crh-common:url value="/js/logon-gmap.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/dispo2.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/logon-validation.js"/>"></script>

<script type="text/javascript">
	var CurrentDate = new Date(<c:out value="${order_dispo_DispoForm.currentMillisInUserTimeZone}"/>);
	var lastCurrentDateRefresh = new Date();
	
	// needed for ajax refresh
	function getRealCurrentDate() {
		var msSinceLastCurrentDateRefresh = (new Date()).getTime() - lastCurrentDateRefresh.getTime();
		var realTime = new Date(CurrentDate.getTime() + msSinceLastCurrentDateRefresh);
		
		return realTime;
	}

	function clearTime(date) {
		var d = new Date(date.getTime());

		// clear time
		d.setUTCHours(0);
		d.setUTCMinutes(0);
		d.setUTCSeconds(0);
		d.setUTCMilliseconds(0);

		return d;
	}

	function dateToUTC(localdate) {
		// take the values from the local directly over to UTC - in other words,
		// we sort of fake" the UTC time by merely creating a new object with the
		// same values of our local date
		var millis = Date.UTC(
				localdate.getFullYear(),
				localdate.getMonth(),
				localdate.getDate(),
				localdate.getHours(),
				localdate.getMinutes(),
				localdate.getSeconds(),
				localdate.getMilliseconds());

		return new Date(millis);
	}

	function getCurrentDispoDateAsUTC() {
		return dateToUTC(dispodate.getValue());
	}

    function setSelectedDisplayFilterOption() {
		hideScheduledCustorders = Ext.getDom("hideScheduledCustordersCheckbox").checked;
		hidePlanningTrucks = Ext.getDom("hidePlanningTrucksCheckbox").checked;
		hideRealTrucks = Ext.getDom("hideRealTrucksCheckbox").checked;
		hideCustorderHeaders = Ext.getDom("hideCustorderHeadersCheckbox").checked;
		hideInactiveTrucks = Ext.getDom("hideInactiveTrucksCheckbox").checked;

		if (hideRealTrucks){
			if (!hidePlanningTrucks) {
				heightFactorPlanning += heightFactorReal;
			}
			heightFactorReal = 0;
		}
		else if (heightFactorReal == 0){
			if (heightFactorPlanning > 0){
				heightFactorReal = heightFactorPlanning / 2;
				heightFactorPlanning = heightFactorPlanning / 2;
			}
			else {
				heightFactorReal = 0.6;
			}
		}
		if (hidePlanningTrucks){
			if (!hideRealTrucks) {
				heightFactorReal += heightFactorPlanning;
			}
			heightFactorPlanning = 0;
		}
		else if (heightFactorPlanning == 0){
			if (heightFactorReal > 0){
				heightFactorPlanning = heightFactorReal / 2;
				heightFactorReal = heightFactorReal / 2;
			}
			else {
				heightFactorPlanning = 0.6;
			}
		}
				
		modifyButtonState(Ext.getDom("buttonSelectAllForTransmit"), false);
		a = { 'setDisplay' : '' };
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
			url: 'Dispo.do',
			params: a,
			success: function(response) {
				eval(response.responseText);
				refreshTrucks();
				refreshOrders();
			}
		});
	}
</script>

<script type="text/javascript">

var actions = {
		ACTION_SAVE_PREFERENCES: '<%=DispoAction.ACTION_SAVE_PREFERENCES%>',
		ACTION_RESET_PREFERENCES:  '<%=DispoAction.ACTION_RESET_PREFERENCES%>'
	};

/** The interval of the auto refresh is configurable in sec. */
var AUTO_REFRESH_INTERVAL = <%=ConfigBeanProvider.getConfigBean().getAppDefaultDispoRefreshInterval()%>*1000;
var MIN_TIME_BEFORE_REFRESH = <%=ConfigBeanProvider.getConfigBean().getAppDefaultDispoMinTimeBeforeReload()%>*1000;
var IS_AUTO_REFRESH_ENABLED = AUTO_REFRESH_INTERVAL > 0;

var TRUCK_PRIORITY_TRUCK_INACTIVE = <%=Constant.TRUCK_PRIORITY_TRUCK_INACTIVE%>;
var TRUCK_PRIORITY_MAX = <%=Constant.TRUCK_PRIORITY_MAX%>;
var TRUCK_PRIORITY_MIN = <%=Constant.TRUCK_PRIORITY_MIN%>;

var ORDER_STATUS_DISPATCHED =  <%=Constant.ORDER_STATUS_DISPATCHED%>;
var ORDER_STATUS_LOADING = <%=Constant.ORDER_STATUS_LOADING%>;
var ORDER_STATUS_DELIVERED = <%=Constant.ORDER_STATUS_DELIVERED%>;

var ASSIGN_STATUS_TR_TRANSMITTED = <%=Constant.ASSIGN_STATUS_TR_TRANSMITTED%>;

var TRUCK_TRANSPORT_TYPE_TRAILER = <%=Constant.TRUCK_TRSP_CAT_TRAILER%>;

var MAP_KEY_TRUCK = "truck";
var MAP_KEY_TRAILER = "trailer";

var DELIVERY_TYPE_BIMODAL_ROAD =  <%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>;

/*******************************************
 * GLOBAL DEBUG SWITCH FOR DISPO SCREEN
 * Enable (e.g. via Firebug console) to see
 * debug messages.
 *******************************************/
var dispoDebugEnabled = false;


var planningTruckCharacteristicCd = <%=Constant.VEHICLE_CHARACTERISTIC_PLANNING%>;

var authpool = false;
var authcarrier = false;
var authorderdetail = false;
var authUnrestrictedExtOrderDispatching = false;
var updateRunner = null;
var updateTask = null;
var lastPageReload = new Date();
var selectedCustorder;

/*******************************************
* DISPO Default values
* LOGON-8967
 *******************************************/
var TIMEGRID_DEFAULT = 4;

<crh-common:authorize function="<%= Authorization.DISPATCHING_W %>">
    authcarrier = true;
</crh-common:authorize>

<crh-common:authorize function="<%= Authorization.DISPATCHING_POOL_W %>">
    authpool = true;
</crh-common:authorize>

<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_R %>">
    authorderdetail = true;
</crh-common:authorize>

<crh-common:authorize function="<%= Authorization.DISPATCHING_CREATE_EXT_ORD %>">
	authUnrestrictedExtOrderDispatching = true;
</crh-common:authorize>
var gmapsRouteError = '<fmt:message key="gmaps.texte.gmapsRouteError"/>';
var errortexts = 
	{cantResolveRouteConflict:  '<fmt:message key="dispo.errors.cantResolveRouteConflict"/>',
	 noAssignmentForLoad: '<fmt:message key="dispo.errors.noAssignmentForLoad"/>',
	 routeConflict : '<fmt:message key="dispo.errors.routeConflict"/>',
	 wrongLoad: '<fmt:message key="dispo.errors.wrongLoad"/>',
	 cantResolveLoadConflict: '<fmt:message key="dispo.errors.cantResolveLoadConflict"/>',
	 truckUnavailable: '<fmt:message key="dispo.errors.truckUnavailable"/>',
	 notTransmitted: '<fmt:message key="dispo.errors.asignmentNotTransmitted"/>',
	 notAcked: '<fmt:message key="dispo.errors.asignmentNotAcknowledged"/>',
	 noTrailer: '<fmt:message key="dispo.errors.noTrailer"/>' ,
	 overMidnight: '<fmt:message key="dispo.errors.overMidnight"/>',
	 wagonNotArrived:  '<fmt:message key="dispo.errors.wagonNotArrived"/>',
	 incompleteComposition:  '<fmt:message key="dispo.errors.incompleteComposition"/>',
	NotReroutable: '<fmt:message key="dispo.reroute.errors.notreroutable"/>',
	OneAssignment: '<fmt:message key="dispo.reroute.errors.oneassignment"/>',
	RerouteSuccessful: '<fmt:message key="dispo.reroute.errors.noerror"/>',
	RerouteUnsuccessful: '<fmt:message key="dispo.reroute.errors.unsuccessful"/>',
	TourNoMatching: '<fmt:message key="dispo.reroute.errors.tournomatching"/>',
	RerouteWeightLess: '<fmt:message key="dispo.reroute.errors.weightless"/>',
	RerouteWeightMore: '<fmt:message key="dispo.reroute.errors.weightmore"/>',
	RerouteMaterialDiff: '<fmt:message key="dispo.reroute.errors.materialdiff"/>',
	RerouteProdPlantDiff: '<fmt:message key="dispo.reroute.errors.prodplantdiff"/>',
	RerouteStructureDiff: '<fmt:message key="dispo.reroute.errors.structurediff"/>'
	};
var texts =
	{
		VehicleType: '<fmt:message key="dispo.texte.VehicleType"/>',
		VehicleName: '<fmt:message key="dispo.texte.VehicleName"/>',
		Driver: '<fmt:message key="dispo.texte.Driver"/>',
		VehicleState: '<fmt:message key="dispo.texte.VehicleState"/>',
		PDAState: '<fmt:message key="dispo.texte.PDAState"/>',
		Product:'<fmt:message key="dispo.texte.Product"/>',
		NetWeight: '<fmt:message key="dispo.texte.NetWeight"/>',
		Nr: '<fmt:message key="dispo.texte.Nr"/>',
		Shippingpoint: '<fmt:message key="dispo.texte.Shippingpoint"/>',
		Loadingpoint: '<fmt:message key="dispo.texte.Loadingpoint"/>',
		Shipto: '<fmt:message key="dispo.texte.Shipto"/>',
		City: '<fmt:message key="dispo.texte.City"/>',
		CommCarrier: '<fmt:message key="dispo.texte.commcarrier"/>',
		ExecCarrier: '<fmt:message key="dispo.texte.execcarrier"/>',
		unavailable	: '<fmt:message key="dispo.texte.unavailable"/>',
		Dispatcher : '<fmt:message key="dispo.texte.dispatcher"/>',
		DeclineCarrier : '<fmt:message key="dispo.texte.declineCarrier"/>',
		DeliveryNote : '<fmt:message key="dispo.texte.deliverynote"/>',
		Notes : '<fmt:message key="dispo.texte.notes"/>',
		Distance : '<fmt:message key="dispo.texte.distance"/>',
		LoadingTime : '<fmt:message key="dispo.texte.loadingtime"/>',
		LatestLoadingTime: '<fmt:message key="dispo.texte.latestloadingtime"/>',
		LnfAddress: '<fmt:message key="dispo.texte.lnfaddress"/>', 
		DrivingTime : '<fmt:message key="dispo.texte.drivingtime"/>',
		UnloadingTime : '<fmt:message key="dispo.texte.unloadingtime"/>',
		LunchBreak : '<fmt:message key="dispo.texte.lunchbreak"/>',
		Break : '<fmt:message key="dispo.texte.break"/>',
		NewBreak : '<fmt:message key="dispo.texte.newbreak"/>',
		AddExternalOrder : '<fmt:message key="dispo.texte.addexternalorder"/>',
		Messages : '<fmt:message key="dispo.texte.messages"/>',
		PdaOnline : '<fmt:message key="dispo.texte.pdaonline"/>',
		PdaOffline : '<fmt:message key="dispo.texte.pdaoffline"/>',
		NoPda: '<fmt:message key="dispo.texte.nopda"/>',
		Order: '<fmt:message key="dispo.texte.order"/>',
		Orders: '<fmt:message key="dispo.texte.orders"/>',
		CustNote: '<fmt:message key="dispo.texte.customer"/>',
		Extern: '<fmt:message key="dispo.texte.external"/>',
		PdaTransError : '<fmt:message key="dispo.texte.pdaTransError"/>',
		TruckLoading : '<fmt:message key="dispo.texte.truckLoading"/>',
		Verladeauftrag : '<fmt:message key="dispo.texte.verladeauftrag"/>',
		LoadOrderItem: '<fmt:message key="dispo.texte.loadOrderItem"/>',
		Time : '<fmt:message key="dispo.texte.deliverytime"/>',
		Railcar : '<fmt:message key="dispo.texte.Railcar"/>',
		NoCarrier : '<fmt:message key="dispo.texte.option.no.carrier"/>',
		LineBetweenPoolCarriers: '<fmt:message key="dispo.texte.option.line"/>',
		Revenue:'<fmt:message key="dispo.texte.revenue"/>',
		Carrierrevenue:'<fmt:message key="dispo.texte.carrierrevenue"/>',
		PreschedulingDone:'<fmt:message key="dispo.texte.PreschedulingDone"/>',
		PreschedulingRequired:'<fmt:message key="dispo.texte.PreschedulingRequired"/>',
		TotalFreightCostPerTruck:'<fmt:message key="dispo.texte.TotalFreightCostPerTruck"/>',
		PlanningInfoOnTruck:'<fmt:message key="dispo.texte.PlanningInfoOnTruck"/>',
		Type:'<fmt:message key="dispo.texte.Type"/>',
		HomePlant:'<fmt:message key="dispo.texte.HomePlant"/>',
		Load:'<fmt:message key="dispo.texte.Load"/>',
		PlanningTrucks:'<fmt:message key="dispo.planning.texte.PlanningTrucks"/>',
		Carrier:'<fmt:message key="dispo.planning.texte.Carrier"/>',
		Freightcost:'<fmt:message key="dispo.planning.texte.Freightcost"/>',
		TruckInactive:'<fmt:message key="dispo.texte.trucks.truckinactive"/>',
		NoHomePlant: '<fmt:message key="dispo.texte.homeplant.notset"/>',
		dispoDeallocationConfirmTitle: '<fmt:message key="dispo.deallocation.confirm.title"/>',
		optionYes: '<fmt:message key="option.yes"/>',
		optionNo: '<fmt:message key="option.no"/>',
        TruckTypeStatus: '<fmt:message key="dispo.texte.truckTypeStatus"/>',
        TruckPrio: '<fmt:message key="dispo.texte.truckprio.short"/>'
	};
var cusOrdTexts = {
		custOrdNr : '<fmt:message key="dispo.texte.orderlist.columns.custordernr"/>',
		soldTo : '<fmt:message key="dispo.texte.orderlist.columns.soldto"/>',
		shiptoCityAddress : '<fmt:message key="dispo.texte.orderlist.columns.shiptoaddress"/>',
		material : '<fmt:message key="dispo.texte.material"/>',
		ordSched : '<fmt:message key="dispo.texte.orderlist.columns.orderedscheduledquantity"/>',
		truckTypes : '<fmt:message key="dispo.texte.orderlist.columns.trucktypes"/>',
		qtyDlvr : '<fmt:message key="dispo.texte.orderlist.columns.deliveredquantity"/>',
		loaded : '<fmt:message key="dispo.texte.orderlist.columns.loadedquantity"/>',
		open : '<fmt:message key="dispo.texte.orderlist.columns.openquantity"/>',
		lastDl : '<fmt:message key="dispo.texte.orderlist.columns.lastdelivery"/>',
		tol : '<fmt:message key="dispo.texte.orderlist.columns.tolerance"/>',
		shippingPoint: '<fmt:message key="dispo.texte.shippingpt"/>',
		commCarr: '<fmt:message key="dispo.texte.orderlist.columns.commcarrier"/>',
		opCarr: '<fmt:message key="dispo.texte.orderlist.columns.opcarrier"/>',
		multiple: '<fmt:message key="dispo.texte.orderlist.trucktypes.multiple"/>',
		shipto : '<fmt:message key="dispo.texte.orderlist.columns.shipto"/>'
};
var materialPopupTexts = {
		orderedQty: '<fmt:message key="dispo.texte.orderlist.materialpopup.orderedqty"/>',
		scheduledQty: '<fmt:message key="dispo.texte.orderlist.materialpopup.scheduledqty"/>',
		deliveredQty: '<fmt:message key="dispo.texte.orderlist.materialpopup.deliveredqty"/>',
		loadedQty: '<fmt:message key="dispo.texte.orderlist.materialpopup.loadedqty"/>',
		openQty: '<fmt:message key="dispo.texte.orderlist.materialpopup.openqty"/>',
		lastDelivery: '<fmt:message key="dispo.texte.orderlist.materialpopup.lastdelivery"/>'
};

var defaultMapCenter = 
	{  
		lat : <%=Constant.MAP_CENTER_LAT%>,
		lng : <%=Constant.MAP_CENTER_LNG%>,
		zoomLevel : <%=Constant.MAP_CENTER_ZOOM_LEVEL%>
	};

var gmapEnabled = false;
var markersArray = [];

var startHour = <c:out value="${order_dispo_DispoForm.startHour}"/>;
var hourSpan = <c:out value="${order_dispo_DispoForm.hourSpan}"/>;
var heightFactorReal = <c:out value="${order_dispo_DispoForm.heightFactorReal}"/>;
var heightFactorPlanning = <c:out value="${order_dispo_DispoForm.heightFactorPlanning}"/>;
var maxNrOfSelectableAssignments = <c:out value="${order_dispo_DispoForm.maxNrOfSelectableAssignments}"/>;
var selectedPool = null;
var selectedCarriers = null;
var sumFreightCostSpanUpdate;
var hidePlanningTrucks = <c:out value="${order_dispo_DispoForm.hidePlanningTrucks}"/>;
var hideRealTrucks = <c:out value="${order_dispo_DispoForm.hideRealTrucks}"/>;
var hideScheduledCustorders = <c:out value="${order_dispo_DispoForm.hideScheduledCustorders}"/>;
var hideCustorderHeaders = <c:out value="${order_dispo_DispoForm.hideCustorderHeaders}"/>;
var hideInactiveTrucks = <c:out value="${order_dispo_DispoForm.hideInactiveTrucks}"/>;
var showMaterialInfo = <c:out value="${order_dispo_DispoForm.selectedFilterForVisual}"/>;
var orderNumber = '';
var truckpriority = '';
<c:if test="${not empty order_dispo_DispoForm.truckPriority}">
	truckpriority = <c:out value="${order_dispo_DispoForm.truckPriority}"/>
</c:if>
var lastAccessTime = null;
<c:if test="${!empty order_dispo_DispoForm.orderNumber}">
orderNumber = <c:out value="${order_dispo_DispoForm.orderNumber}"/>;
</c:if>

var currentPoolOptions = [];
<c:forEach var="pool" varStatus="status" items="${order_dispo_DispoForm.allPools}">
	currentPoolOptions.push({
		poolId:'<c:out value="${pool.poolTxPK.poolId}"/>',
		poolName:  '<c:out value="${pool.name}"/>'
	});
</c:forEach>

	function dispoDebug(msg)
	{
		if (dispoDebugEnabled && typeof console !== 'undefined') {
			var d=new Date();
			var n=d.toTimeString(); 
			console.log(n + " => " + msg);
		}
	}

	function updateRunnerSetEnabled(setEnabled) {
		
		if (setEnabled === true) {
			updateRunner.start(updateTask);
		}
		if (setEnabled === false) {
			updateRunner.stopAll();
		}		
	}
	
	function updateOrderList(orders){
	    if (!dispo || !dispo.draggingInProgress) {	    	
			orderList = new OrderList(Ext.getDom('orderlistcontainer'), startHour, startHour + hourSpan, orders);
			orderList.onSelectionChange = setOrderListButtonsState;
			setOrderListButtonsState();
	    	pingCustomerOrders(); 
		}
	}

	function updatePackageTypeTrspCatMaps(map) 
	{
		// Need to set this function if it doesn't exist (like in IE versions)
		if(!Array.prototype.indexOf) {
		    Array.prototype.indexOf = function(needle) {
        		for(var i = 0; i < this.length; i++) {
            		if(this[i] === needle) {
        	        	return i;
            		}
        		}
        		return -1;
    		};
		}
		
		var truckMap = map[MAP_KEY_TRUCK];
		var trailerMap = map[MAP_KEY_TRAILER];
		
		function packageTypeMatchesTrspCatMap(packageType, transportCode, vehicleMap)
		{
			if (vehicleMap.hasOwnProperty(packageType)) {
				var allowedTransportCodes = vehicleMap[packageType];
				if (allowedTransportCodes.indexOf(transportCode) > -1) {
					return true;
				}
			}
			return false;			
		}

		window.packageTypeMatchesTruckTrspCatMap = function(packageType, transportCode) 
		{
			return packageTypeMatchesTrspCatMap(packageType, transportCode, truckMap);
		};
		
		window.packageTypeMatchesTrailerTrspCatMap = function(packageType, transportCode) 
		{
			return packageTypeMatchesTrspCatMap(packageType, transportCode, trailerMap);
		};
	}

	function getTrucks(truckAndAssignmentDifference){ 
		var trucks = [];						
		var truckModification = truckAndAssignmentDifference.truckModification;				
		if(truckModification) {
	    	var newTrucks = truckModification.newTrucks;
	    	if(newTrucks) {
				for ( var i = 0; i < newTrucks.length; i++) {
					var indexedTruckObject = newTrucks[i];
					if(indexedTruckObject) {
						trucks.push(indexedTruckObject.object);						
					}
				}
	    	}	    	
		}	
		return trucks;
	}

	function getAssignments(truckAndAssignmentDifference){ 
		var assignmentModification = truckAndAssignmentDifference.assignmentModification;
		var assignments = [];
		if(assignmentModification) {
			assignments = assignmentModification.assignments;
		}		
		return assignments;
	}
	
	/**
	 * Calls the re-initializing function for trucks and assignments.
	 * @param list - comes from DWR call, should contain two items:
	 *   - list[0]: list of trucks
	 *   - list[1]: list of assignments
	 */
	function updateTruckAndAssignmentList(truckAndAssignmentDifference){
	    if (!dispo || !dispo.draggingInProgress) {
	    	var trucks = getTrucks(truckAndAssignmentDifference);
	    	var assignments = getAssignments(truckAndAssignmentDifference);
			lastAccessTime = truckAndAssignmentDifference.lastAccessTime;			    	
	    	
	    	updateTruckList(trucks);	    	

			updateAssignmentListLw(assignments);
		}
	}
	 
	function refreshDispoDifference(domStructureChanged) {
		dispo.init(dispo.alltrucks);
		if(dispo.currentTimeIndicator) {
			dispo.redrawTimeIndicators();			
		}
		
		if (domStructureChanged) {
			
		}
	}

	function updateTruckAndAssignmentListDifference(truckAndAssignmentDifference) {
	    if (!dispo || !dispo.draggingInProgress) {
			var truckModification = truckAndAssignmentDifference.truckModification;
			var assignmentModification = truckAndAssignmentDifference.assignmentModification;

			updateTruckListDifference(truckModification);
			
			if(assignmentModification) {
				var assignments = assignmentModification.assignments;
				updateAssignmentListLw(assignments);
			}
			
			lastAccessTime = truckAndAssignmentDifference.lastAccessTime;			    	
	    }
	}
	
	function updateTruckListDifference(truckDifference) {				
		var newTrucks = truckDifference.newTrucks;
		var removedTrucks = truckDifference.removedTrucks;
		var domStructureChanged = removedTrucks.length > 0 || newTrucks.length > 0;
		
		for (var i = removedTrucks.length - 1 ; i > -1 ; i--) {
			dispo.removeSingleTruck(removedTrucks[i].object, removedTrucks[i].index);
		}
		
		for (var i = 0 ; i < newTrucks.length; i++) {
			dispo.addSingleTruck(newTrucks[i].object, newTrucks[i].index);
		}
				
		refreshDispoDifference(domStructureChanged);
	}
	
	/**
	 * Re-initializes DispoGeneral with the given list of trucks.
	 */
	function updateTruckList(trucks) {
		dispo = new DispoGeneral( startHour, startHour + hourSpan, trucks, dispodateIsToday(), TIMEGRID_DEFAULT);
		dispo.onSelectionChange = setDispoButtonsState;
	}

	function updateTruckListLw(truckAndAssignmentDifference){
	    if (!dispo || !dispo.draggingInProgress) {	    	
	    	var trucks = getTrucks(truckAndAssignmentDifference);
	    	var assignments = getAssignments(truckAndAssignmentDifference);
	    	
			dispo.updateTrucks(trucks);	    	
			updateAssignmentListLw(assignments);	    	
			lastAccessTime = truckAndAssignmentDifference.lastAccessTime;			    	
		}
	}

	function updateTruckListOneLine(truckAndAssignmentDifference){
	    if (!dispo || !dispo.draggingInProgress) {
	    	var trucks = getTrucks(truckAndAssignmentDifference);
	    	var assignments = getAssignments(truckAndAssignmentDifference);
	    	
			dispo.updateTrucks(trucks);
			dispo.setAssignmentsForOneLine(assignments);
	    	pingCustomerOrders();
			setDispoButtonsState();	
		}
	}	
	
	function updateAssignmentListLw(assignments){
		if (!dispo || !dispo.draggingInProgress) {
			dispo.setAssignments(assignments);
	    	pingCustomerOrders();
		}
		setDispoButtonsState();	
	}	
	
	function refreshOrders() {
		sumFreightCostSpanUpdate.plannedOrdersLoaded = false;
		DispoAjaxService.getOrderListWithLocalEJB(updateOrderList);
	}

    function refreshTrucks(){
    	sumFreightCostSpanUpdate.trucksLoaded = false;
    	DispoAjaxService.getTruckAndAssignmentListWithLocalEJB(null, updateTruckAndAssignmentList);    	
    }
    
    function refreshTrucksDifference() {
    	DispoAjaxService.getTruckAndAssignmentModificationListWithLocalEJB(updateTruckAndAssignmentListDifference);
    }
    
    /**
    * Redraws assignments of truck without reloading from the server.
    */
    function redrawAssignmentsOfTruck(truckId) {
    	if (!dispo) {
    		return;
    	}
    	var assignments = [];
    	for (var i = 0; i < allAssignments.length; i++) {
			if (allAssignments[i].assignment.truckId === truckId) {
				assignments.push(allAssignments[i]);
			}
		}
    	dispo.setAssignmentsForOneLine(assignments);
    }

    /**
     * Redraws assignments of truck without reloading from the server.
     */
     function redrawAssignmentsOfTruck(truckId) {
     	if (!dispo) {
     		return;
     	}
     	var assignments = [];
     	for (var i = 0; i < allAssignments.length; i++) {
 			if (allAssignments[i].assignment.truckId === truckId) {
 				assignments.push(allAssignments[i]);
 			}
 		}
     	dispo.setAssignmentsForOneLine(assignments);
     }

    /**
     * Redraws the truck area (including assignments) on the screen 
     * without reloading the trucks and the assigments from the server.
     */
    function redrawTrucksAndAssignments() {
    	if (dispo !== null) {
    		sumFreightCostSpanUpdate.trucksLoaded = false;
			
    		// Re-draw the trucks
    		updateTruckList(dispo.alltrucks);
    		
    		// Re-draw assignments on trucks
    		updateAssignmentListLw(allAssignments);
   		}
    }
    
    /**
     * Redraws the unallocated order list area on the screen
     * without reloading the orders from the server.
     */
    function redrawOrderList() {
    	if (orderList !== null) {
    		sumFreightCostSpanUpdate.plannedOrdersLoaded = false;
    	
    		updateOrderList(orderList.custorders);
   		}
    }
    
    /*
     * This method creates a refresher function. It has the feature that if an object
     * is currently being refreshed, no new refresh is started. This is to avoid
     * parallel refreshing of the same thing.
     *
     * In principle this refresher is a wrapper around any asynchronous call,
     * which prohibits parallel calls (within the same refresher).
     *
     * Usage:
     * var myRefresher = makeRefresher(myAjaxCall, myCallback);
     * myRefresher.refresh();
     *
     * -> This calls myAjaxCall() (which is expected to take a callback as parameter)
     *    and passes the result to myCallback.
     *    If myRefresher.refresh() is called again while the ajax function of the first call
     *    has not finished, then it does nothing.
     *
     * See truckRefresher and orderRefresher for examples below.
     */
    function makeRefresher(service, callback, label)
    {
		var refreshInProgress = false;
		
		// We initially set this to the current time, because things are anyway
		// load, no need to immediately kick in the auto refresh.
		var previousRefreshCompleted = new Date();

		var refresher = {
				"refresh" : function()
				{
					if (refreshInProgress)
					{
						dispoDebug(label + " refresh already in progress, aborting.");
						return;
					}
					
					var now = new Date();
					var timeSinceLastRefresh = now.getElapsed(previousRefreshCompleted);
					if (timeSinceLastRefresh < MIN_TIME_BEFORE_REFRESH) {
						dispoDebug("Last " + label + " refresh less than " + MIN_TIME_BEFORE_REFRESH
								+ "ms ago, skipping refresh.");
						return;
					}
					
					// All good, we can go a head and refresh
					dispoDebug("Starting " + label + " refresh...");
					refreshInProgress = true;
					service(this.refreshComplete);
				},

				"refreshComplete" : function(response)
				{
					dispoDebug("Processing " + label + " refresh response");
					callback(response);
					refreshInProgress = false;
					previousRefreshCompleted = new Date(); // Reset
					dispoDebug(label + " refresh done");
				}
		};
		
		return refresher;

    }

	var truckRefresher = makeRefresher(
			function(cb) {
				sumFreightCostSpanUpdate.trucksLoaded = false;
				
				var staleTruckIds = [];				
				var staleTruckIdsWithPdaStatus = {};				
				for ( var i = 0; i < dispo.alltrucks.length; i++) {
					var truck = dispo.alltrucks[i];					
					staleTruckIds.push(truck.truck.truckId);
					staleTruckIdsWithPdaStatus[truck.truck.truckId] = truck.truck.pdaSessionStatusCd;
				}
				
				DispoAjaxService.getTruckAndAssignmentModificationListWithLocalEJB(null, staleTruckIds, staleTruckIdsWithPdaStatus, lastAccessTime, cb);
			},
			updateTruckAndAssignmentListDifference,
			'truck');

	var orderRefresher = makeRefresher(
			function(cb) {
				sumFreightCostSpanUpdate.plannedOrdersLoaded = false;
				DispoAjaxService.getOrderListWithLocalEJB(cb);
				},
			updateOrderList,
			'order');

    function loadPackageTypeTransportCategoryMaps() {
    	DispoAjaxService.getTrspCategoriesForPackageTypes(updatePackageTypeTrspCatMaps);
    }
    
    function refreshAssignments(){
    	// refreshes assignment list and displayed truck state fields
		// without redrawing the complete assignment table
    	sumFreightCostSpanUpdate.assignmentsLoaded = false;
    	DispoAjaxService.getTruckAndAssignmentListWithLocalEJB(null, updateTruckListLw);
	}	
	
	//ajax callback
	var currentAssignment;
	function showReroutingPopupIfRoutable(isAssignmentReroutable) {
		if (isAssignmentReroutable) {
			var orders = '';
			var orderWrapper;
			var orderId;
			if (orderList && orderList.custorders) {
				for (var i = 0; i < orderList.custorders.length; i++) {
					if (orderList.custorders[i].orderWrappers) {
						for (var j = 0; j < orderList.custorders[i].orderWrappers.length; j++) {
							orderWrapper = orderList.custorders[i].orderWrappers[j];
							orderId = orderWrapper.order.orderId;
							orders = orders.concat(orderId + "_");
						}
					}
				}
			}
			showPopup("AssignmentEdit.do?reroute=" + currentAssignment + "&orders=" + orders,"", undefined, undefined, false, 120);
			var truckId = Ext.getDom("ai"+dispo.selection[0]).parentNode.assignment.assignment.truckId;
			showLoad(currentAssignment);
		}
		else {
			Logon.message.info('<fmt:message key="dispo.reroute.errors.notreroutable"/>');
			return;
		}
	}

	function showLoad(currentAssignment) {
		var actualTruckId = Ext.getDom("ai"+currentAssignment).parentNode.assignment.assignment.truckId;
		
		//retrieving the truck object
		var truck;
		for (i = 0; i < dispo.alltrucks.length; i++){
			var cellcnt = 0;
			var rowcells = [];
			truckId = dispo.alltrucks[i].truck.truckId;
			if (actualTruckId === truckId) {
				truck = dispo.alltrucks[i].truck;
				break;
			}
		}
		
		if (!truck) {
			return;
		}
		
		//retrieving assignment object
		var assignment;
		for (var j = 0; j < allAssignments.length; j++) {
			if (allAssignments[j].assignment.assignmentId == currentAssignment) {
				assignment = allAssignments[j];
			}
		}
		if (!assignment) {
			return;
		}
		
		//getting load-type assignmentParts of the assignment
		truck.loads = new Array();
		var loadAssignmentParts = new Array();
		for (var j = 0; j < assignment.parts.length; j++) {
			if (assignment.parts[j].assignmentPart.assignmentPartTypeCd == 3) {
				loadAssignmentParts.push(assignment.parts[j]);
			}
		}

		//adding all orderitem data to the truck's load
		for (var i = 0; i < loadAssignmentParts.length; i++) {
			for (var j = 0; j < loadAssignmentParts[i].orderItems.length; j++) {
				var orderItem = loadAssignmentParts[i].orderItems[j];
				truck.loads.push({
						loadingTs: new Date(),
						materialDescription: orderItem.materialName,
						netQuantity: orderItem.loadedQuantity,
						uomTxt: orderItem.uomName,
						assignmentId: currentAssignment
				});
			}
		}
		
		var content = dispo.disporeal.buildLoadQtyContent(truck);
		truckElement = Ext.getDom('trhc' + actualTruckId);
		if (truckElement) {
			var children = truckElement.children[2]; 
			if (children) {
				children.innerHTML = content;		
			}
		}
		
	}

	function hideLoad(currentAssignment) {
		var actualTruckId = Ext.getDom("ai"+currentAssignment).parentNode.assignment.assignment.truckId;
		
		var truck;
		for (i = 0; i < dispo.alltrucks.length; i++){
			var cellcnt = 0;
			var rowcells = [];
			truckId = dispo.alltrucks[i].truck.truckId;
			if (actualTruckId === truckId) {
				truck = dispo.alltrucks[i].truck;
				truck.loads = new Array();
				break;
			}
		}
		var content = dispo.disporeal.buildLoadQtyContent(truck);
		truckElement = Ext.getDom('trhc' + actualTruckId);
		if (truckElement) {
			var children = truckElement.children[2]; 
			if (children) {
				children.innerHTML = content;		
			}
		}
	}

	function showReroutingPopup(assignmentList){
		if (!assignmentList || assignmentList.length != 1) {
			Logon.message.info('<fmt:message key="dispo.reroute.errors.oneassignment"/>');
			return;
		}
		
		currentAssignment = assignmentList[0];
		DispoAjaxService.checkAssignmentReroutable(currentAssignment, showReroutingPopupIfRoutable);
	}
	
	var dispodate;
	var currentDispoDate;
	var icons = {};
	
	function modifyButtonState(button, actionAllowed){
		button.disabled=!actionAllowed;
		button.className = (actionAllowed)?"SmallButtonStyle":"SmallButtonStyleDisabled";
	}
	
	function dispodateIsToday(){
		var today = new Date();
		return clearTime(today).getElapsed(currentDispoDate) == 0;
	}
	
	function setDispoButtonsState(){
		modifyButtonState(Ext.getDom("buttonTransmit"), dispo.canTransmitSelection() && authcarrier);
		modifyButtonState(Ext.getDom("buttonCancel"), dispo.canCancelSelection() && authcarrier);		
		modifyButtonState(Ext.getDom("buttonAtShipto"), dispodateIsToday() && dispo.canSetAtShiptoSelection() && authcarrier);
		modifyButtonState(Ext.getDom("buttonUnloaded"), dispodateIsToday() && dispo.canSetUnloadedSelection() && authcarrier);
		modifyButtonState(Ext.getDom("buttonReturned"), dispodateIsToday() && dispo.canSetReturnedSelection() && authcarrier);
		modifyButtonState(Ext.getDom("buttonLoadAtCarrierStock"), dispodateIsToday() && dispo.canLoadAtCarrierStock() && authcarrier);
		modifyButtonState(Ext.getDom("buttonEnterShippingpoint"), dispodateIsToday() && dispo.canEnterShippingpoint() && authpool);
		modifyButtonState(Ext.getDom("buttonSelectAllForTransmit"), dispo.canSelectForTransmission() && authcarrier);
		modifyButtonState(Ext.getDom("buttonReroute"), dispo.canRerouteSelection() && authcarrier);
	}
	
	function setOrderListButtonsState(){
		if (Ext.getDom("buttonTransmitToCarrier")){
			modifyButtonState(Ext.getDom("buttonTransmitToCarrier"), orderList.canTransmitSelection());
		}
		modifyButtonState(Ext.getDom("buttonAcceptOrder"), orderList.canAcceptSelection() && (authpool || authcarrier));
		modifyButtonState(Ext.getDom("buttonCancelOrder"), orderList.canCancelSelection() && (authpool || authcarrier));
	}
	
	
	
	function resizeTables(){
	    var dispotable = Ext.get('dispocontainer');
		var dispoplanningtable = Ext.get('dispoplanningcontainer');
		var ordertable = Ext.get('orderlistcontainer');


		if(hideRealTrucks) {
			dispotable.dom.style.display="none";
			Ext.get('tableresize2').dom.style.display="none";
			heightFactorPlanning += heightFactorReal;
			heightFactorReal = 0;
		}
		else {
			dispotable.dom.style.display="";			
			Ext.get('tableresize2').dom.style.display="";
		}
		
		if(hidePlanningTrucks) {
			dispoplanningtable.dom.style.display="none";
			Ext.get('tableresize1').dom.style.display="none";
			heightFactorReal += heightFactorPlanning;
			heightFactorPlanning = 0;
		}
		else {
			dispoplanningtable.dom.style.display="";			
			Ext.get('tableresize1').dom.style.display="";
		}

		if(hideRealTrucks && hidePlanningTrucks){
			Ext.get('schedulingHeaderTable').dom.style.display="none";
			Ext.get('buttonsCell').dom.style.display="none";
		}
		else {
			Ext.get('schedulingHeaderTable').dom.style.display="";
			Ext.get('buttonsCell').dom.style.display="";
		}

		var tablesheight = document.body.clientHeight - ordertable.getTop() - 60;
		
		if (!hideRealTrucks){
			tablesheight = document.body.clientHeight - dispotable.getTop() - 60;
		}
		
		if (!hidePlanningTrucks){
			tablesheight = document.body.clientHeight - dispoplanningtable.getTop() - 60;
		}


		if (!hideRealTrucks){
			dispotable.setHeight(Math.round(tablesheight * heightFactorReal));
		}
		
		if (!hidePlanningTrucks){
			dispoplanningtable.setHeight(Math.round(tablesheight * heightFactorPlanning));
		}
				
		ordertable.setHeight(Math.round(tablesheight * (1-heightFactorReal-heightFactorPlanning)) - 15);
		
		redrawTrucksAndAssignments();
		redrawOrderList();
	}
	
	function showHomePlantInputDialog(truckId) {
		a = {'truckId': truckId};
		showPopup('EditHomePlantEntry.do', null, a);
	}
	
	function showInputDialog(title, field, selection, callback, showDialogInputField){
			
			var orderlist = null;
			if (selection.length == 1){
				var el = Ext.getDom("ai"+selection[0]);
				if (el != null) {
					ass = el.parentNode.assignment;
					if ( ass.isNotAuthorized ) {
						orderlist = '';
						orderlist +='<fmt:message key="dispo.texte.warning.changing.notauthorized.assignment"/><br><br>'
					}
					if (ass.orderCnt > 1){
						if ( orderlist == null ) {
							orderlist = '';						
						}
						orderlist += '<table width="100%" class="big_form2" id="DialogOrdersTable" style="border-spacing:2px" >';
						orderlist += '<tr><td colspan="5" class="label"><fmt:message key="dispo.texte.orders"/></td></tr>';
						orderlist += '<tr><th>&nbsp;</th><th><fmt:message key="dispo.texte.Nr"/></th><th><fmt:message key="dispo.texte.Shipto"/>' +
						'</th><th><fmt:message key="dispo.texte.Product"/></th><th><fmt:message key="dispo.texte.Orderstatus"/></th></tr>';
						var parts = ass.parts;
						var cnt = 0;
						for (var i = 0; i < parts.length; i++) {
							if (parts[i].assignmentPart.orderNumber != null) {
								/* LOGON-5629 LRQ 073.2 StartLoading does not Block Deallocation Functionality
								 * Check if the order is loading, and disable deallocation. 
								 */
								var isDeallocationDisabled = parts[i].assignmentPart.orderStatusCd >= ORDER_STATUS_DELIVERED || 
									parts[i].assignmentPart.orderStatusCd == ORDER_STATUS_LOADING;
								
								orderlist += '<tr class="zebra'+ (cnt%2) +
									'"><td><input type="checkbox" name="dotcb' + parts[i].assignmentPart.orderNumber +
										'" value="' + parts[i].assignmentPart.orderNumber + '" ' +
										(isDeallocationDisabled ? 'disabled="true"' : 'checked') +
										'/></td><td>' +
										parts[i].assignmentPart.orderNumber + 
									'</td><td><nobr>' + parts[i].assignmentPart.orderShiptoName + 
									'</nobr></td><td><nobr>' + buildItemsLine(parts[i].orderItems) +
									'</nobr></td><td><nobr>' + parts[i].assignmentPart.orderStatusTxt + '</nobr></td></tr>';
								cnt++;
							}
						}
						orderlist += '</table>';
					}
				}
			}

			if (showDialogInputField || orderlist != null) {				
			showCommonReasonDialog.createDelegate(this,[
					{
						title:title, 
						reason:field, 
						addText: orderlist,
					 	CancelText: '<fmt:message key="buttons.cancel"/>',
					 	SaveText:'<fmt:message key="buttons.save"/>'
					},					
					function(dialogText){
						var tbl = Ext.getDom('DialogOrdersTable');
						var orders = null;
						if (tbl != null){
							orders = [];
							var list = Ext.query("input[type='checkbox']", tbl);
							for (i = 0; i < list.length; i++){
								if (list[i].checked){
									orders[orders.length] = list[i].value;
								}
							}
						}
						hidePopup();
		        		callback(selection,  dialogText, orders);
					},
					function(){					
						setDispoButtonsState();
						setOrderListButtonsState();
						hidePopup();
					},
					showDialogInputField ? { showInputField:true, required:false} : { showInputField:false, required:false}
					])();
			}
			else {
				callback(selection);
			}
	}	
	
	function showCancelOrderDialog(orderNumber){
		showInputDialog('<fmt:message key="dispo.button.cancelOrder"/>',
				'<fmt:message key="dispo.texte.cancelorderreason"/>',
				orderNumber, cancelOrder, true);
	}
	
	function cancelOrder(orderNumber, reason){
		DispoAjaxService.cancelOrderCarrier(orderNumber, reason, authpool, refreshOrders);
	}

	function resetCarrierDeclineNote(orderId){
		DispoAjaxService.resetCarrierDeclineNote(orderId, refreshOrders);
	}
	
	function selectBeforeReleaseBasedOnTruck(button){
		// Get dom of row containing the orders relevant to the clicked button
		var btnRowId = button.parentNode.parentNode.id;
		var btnRowIdNumber = btnRowId.substr(4);
		var orderRowId = "trc" + btnRowIdNumber;
		var orderRow = document.getElementById(orderRowId);
		var domOfOrderRow = Ext.getDom(orderRow);
		
		// Select orders from this row
		dispo.selectAllFromTruckForRelease(domOfOrderRow);
		
		// Update button states
		setDispoButtonsState();
	} 
	
	function selectAndReleaseBasedOnTruck(button){
		// Select corresponding orders
		selectBeforeReleaseBasedOnTruck(button);		
		
		// Release selected orders
		showReleaseAssignmentsDialog(dispo.selection);
		
		// Update button states
		setDispoButtonsState();
    }
	
	function showReleaseAssignmentsDialog(selection){
		//releasing not authorized orders should happen one-by-one, selectAllForTransmit must guarantee that it is only possible to select a not authorized order in a single selection
		if ( selection.length == 1 ){

			var selectedEl = Ext.get("ai"+ selection[0]).dom.parentNode.assignmentObject;
		
			if (selectedEl.el.dom.assignment.isNotAuthorized) {
				showCommonReasonDialog.createDelegate(this,[
		                        					{
		                        						title:'<fmt:message key="dispo.texte.releasetransport"/>', 
		                        						addText:'<fmt:message key="dispo.texte.releasetransport.dialogtext"/>', 
		                        					 	CancelText: '<fmt:message key="buttons.cancel"/>',
		                        					 	SaveText:'<fmt:message key="buttons.save"/>'
		                        					},					
		                        					function(){
		                        						hidePopup();
		                        						transmitToDriver(selection);
		                        					},
		                        					function(){					
		                        						setDispoButtonsState();
		                        						setOrderListButtonsState();
		                        						hidePopup();
		                        					},
		                        					{ showInputField:false, required:false}
				])();
			}
			else {
				transmitToDriver(selection);	
			}
		}
		else {
			transmitToDriver(selection);
		}			
	}

	function showCancelAssignmentsDialog(selection) {
		showInputDialog('<fmt:message key="dispo.texte.canceltransport"/>',
				'<fmt:message key="dispo.texte.canceltransportreason"/>',
				selection, confirmCancelAssignments, false);
	}
	
	
	function createDeallocationConfirmationMsg(orderNumbers, truckName) {
		var message;
		var orderListStr;
		
		if (orderNumbers.length == 1) {
			orderListStr = orderNumbers[0];
			message =	'<fmt:message key="dispo.deallocation.confirm.single"> \
							<fmt:param value="{orderListStr}"/> \
							<fmt:param value="{truckName}"/> \
						</fmt:message>';
		}	
		else {
			orderListStr = '<br/>';
			for(var idx = 0; idx < orderNumbers.length; idx++) {
				var orderNumber = orderNumbers[idx];
				orderListStr += '* ' + orderNumber + '<br/>';
			}
			message =	'<fmt:message key="dispo.deallocation.confirm.multiple"> \
							<fmt:param value="{orderListStr}"/> \
							<fmt:param value="{truckName}"/> \
						</fmt:message>';
		}
		
		return message
			.replace('{orderListStr}', orderListStr)
			.replace('{truckName}', truckName);
	}
	
	function anyTruckHasPrescheduledAssignmentOrPlanningTruck(selection) {
		for (i = 0; i < selection.length; i++) {
			var assignmentId = selection[i];
			var assignmentObj = Ext.getDom('ai' + assignmentId);
			
			if (assignmentObj != null) {
				var truckAssignment = assignmentObj.parentNode.assignment.assignment;
				var truckId = truckAssignment.truckId;
				if (truckHasPrescheduledAssignmentOrPlanningTruck(truckId)){
					return true;
				}	
			}
		}
		return false;
	}
	
	function truckHasPrescheduledAssignmentOrPlanningTruck(truckId, modifiedAssignments) {
		var camion = Ext.get('cd' + truckId);
		if (camion && camion.dom && camion.dom.camion && isPlanningTruck(camion.dom.camion)){
			return true;
		}
		if (camion && camion.dom && camion.dom.assignmentList){			
			for (var j = 0; j < camion.dom.assignmentList.length; j++) {
				var assignment = camion.dom.assignmentList[j].assignment;
				if (assignment.plannedTruckId) {
					return true;
				}
			}
		}
		
		if (modifiedAssignments) {
			for (i = 0; i < modifiedAssignments.length; i++) {
				var assignment = modifiedAssignments[i];
				if (assignment.plannedTruckId) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	function cancelAssignments(selection, cancelAssignmentText, orders){
			var refreshAll = anyTruckHasPrescheduledAssignmentOrPlanningTruck(selection);
			DispoAjaxService.cancelAssignment(selection, cancelAssignmentText, orders, 
			function(truckAndAssignmentDifference) {
				// Remove the assignment divs from the camion div
				for (i = 0; i < selection.length; i++) {
					var assignmentId = selection[i];
					var assignmentObj = Ext.getDom('ai' + assignmentId);
	
					if (assignmentObj != null) {
					    var camion = assignmentObj.parentNode.parentNode;
					
		    			camion.style.height = 37;
					    camion.row.style.height = 39;
					    camion.hrow.style.height = 39;
					    camion.assignmentList = [];
					    Ext.DomHelper.overwrite(camion, {
					    	tag : 'div',
						    cls : 'ddselect'
					    });
					}
				}
				//there are real assignments rendered as shadow assignments planning trucks
				//and these real assignments are not returned by the list so the screen must be
				//refreshed therefore
				if (refreshAll) {
					refreshTrucks();
				}
				else {
					// Delete the cancelled assignments from "cache"
					dispo.deleteAssignmentFromAllAssignments(selection);
					updateTruckListOneLine(truckAndAssignmentDifference);
				}
				refreshOrders();
			}
		);
	}
	
	function getTruckIdByAssignment(assignmentId) {
		return Ext.getDom("ai"+assignmentId).parentNode.assignment.assignment.truckId;
	}
	
	function getTruckNameById(truckId) {
		for (var i = 0; i < dispo.alltrucks.length; ++i) {
			var truck = dispo.alltrucks[i].truck;
			if (truck.truckId == truckId) {
				return truck.truckName;
			}
		}
	}
	
	function lockAssignmentsForDD(selection) {

		for (i = 0; i < selection.length; i++){
			var selectedEl = Ext.get("ai"+selection[i]).dom.parentNode.assignmentObject;
			if ((selectedEl) && (selectedEl.dd)) {
				selectedEl.dd.lock();
			}
		}
	}

	function unlockAssignmentsForDD(selection) {

		for (i = 0; i < selection.length; i++){
			var selectedEl = Ext.get("ai"+selection[i]).dom.parentNode.assignmentObject;
			if ((selectedEl) && (selectedEl.dd)) {
				selectedEl.dd.unlock();
			}
		}
	}
	
	function transmitToDriver(selection) {
		modifyButtonState(Ext.getDom("buttonSelectAllForTransmit"), false);
		lockAssignmentsForDD(selection);
		setCustomeDwrEngineExceptionHandler(function(err, exc, c) {
			setCustomeDwrEngineExceptionHandler(null);
			refreshTrucks();
		});
		DispoAjaxService.transmitToDriver(selection, function(truckAndAssignmentDifference) {
			setCustomeDwrEngineExceptionHandler(null);
			
		
			//there are real assignments rendered as shadow assignments planning trucks
			//and these real assignments are not returned by the list so the screen must be
			//refreshed therefore			
			if (anyTruckHasPrescheduledAssignmentOrPlanningTruck(selection)) {	
				refreshTrucks();
			}
			else {
				updateTruckListOneLine(truckAndAssignmentDifference);
			}
		});
	}
	
	var mapDrawer = null;
	GMapAPILoader.onAPIReady(function(){
		icons.stIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_shipto.png",
				  new google.maps.Size(21, 21),
				  new google.maps.Point(0,0),
				  new google.maps.Point(11,11));
		
		icons.spIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_ordermap_shipping_point.png",
				  new google.maps.Size(23, 24),
				  new google.maps.Point(0,0),
				  new google.maps.Point(12,12));

		icons.coordIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_non_geo_event.png",
				  new google.maps.Size(10, 10),
				  new google.maps.Point(0,0),
				  new google.maps.Point(5,5));

		icons.selectIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_non_geo_event.png",
				  new google.maps.Size(35, 35),
				  new google.maps.Point(0,0),
				  new google.maps.Point(18,18));

		icons.truckIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_truck_partially_loaded_inside_geofence.png",
				  new google.maps.Size(23, 23),
				  new google.maps.Point(0,0),
				  new google.maps.Point(2, 23));
		
		gmapEnabled = true;
	});
	function buildQuantityAndUomText(quantity, uomTxt, addParentheses) {
		if (quantity != null) {
			var pre = (addParentheses) ? '(' : '';
			var text = quantity + ' ' + uomTxt;
			var post = (addParentheses) ? ')' : '';
			
			return pre + text + post;
		}
		
		return '';
	}
	
	function buildCustorderMaterialInfo(qt, matInfoList, lastDelivery) {
		var materialTableRows = [];
		
		for (var i = 0; i < matInfoList.length; i++) {
			var sumQtyEntity = matInfoList[i].custorderItemSumQtyEntity;
			
			var orderedQuantity = buildQuantityAndUomText(
					sumQtyEntity.orderedQuantity, sumQtyEntity.orderedUomTxt, false);
			var scheduledQuantityInBaseUom = buildQuantityAndUomText(
					sumQtyEntity.scheduledQuantityInBaseUom, sumQtyEntity.baseUomTxt, true);
			var deliveredQuantityInBaseUom = buildQuantityAndUomText(
					sumQtyEntity.deliveredQuantityInBaseUom, sumQtyEntity.baseUomTxt, true);
			var loadedQuantityInBaseUom = buildQuantityAndUomText(
					sumQtyEntity.loadedQuantityInBaseUom, sumQtyEntity.baseUomTxt, false);
			var openQuantityInBaseUom = buildQuantityAndUomText(
					sumQtyEntity.openQuantityInBaseUom, sumQtyEntity.baseUomTxt, false);
			
			materialTableRows[i] = {tag: 'tr', children: [
				{tag: 'td', html: matInfoList[i].materialName, cls: 'firstColumn'},
				{tag: 'td', html: orderedQuantity},
				{tag: 'td', html: scheduledQuantityInBaseUom},
				{tag: 'td', html: deliveredQuantityInBaseUom},
				{tag: 'td', html: loadedQuantityInBaseUom},
				{tag: 'td', html: openQuantityInBaseUom},
				{tag: 'td', html: lastDelivery}
			]};
		}
		
		var materialTable = {tag: 'table', cls: 'materialTable', children: [
			{tag: 'thead', children: [
				{tag: 'tr', children: [
					{tag: 'th', html: '', cls: 'firstColumn'},
					{tag: 'th', html: materialPopupTexts.orderedQty},
					{tag: 'th', html: materialPopupTexts.scheduledQty},
					{tag: 'th', html: materialPopupTexts.deliveredQty},
					{tag: 'th', html: materialPopupTexts.loadedQty},
					{tag: 'th', html: materialPopupTexts.openQty},
					{tag: 'th', html: materialPopupTexts.lastDelivery}
				]},
				{tag: 'body', children: materialTableRows}
			]}
		]};
		
		qt.materialPopup = Ext.get(Ext.DomHelper.append(qt.getLayoutTarget().up('div'), materialTable));
		qt.materialPopup.dom.style.height = 20 + (i * 18) + 'px';
		qt.setWidth('840px');
		qt.materialPopup.dom.style.position = 'relative';
		qt.materialPopup.show();
	}
	
	Ext.onReady(function(){
		document.body.style.overflow = "hidden";
		Ext.QuickTips.init();
		Ext.apply(Ext.QuickTips.getQuickTip(), {
		    maxWidth: 400,
		    dismissDelay: 0
		});
		
		
		var qt = Ext.QuickTips.getQuickTip();
// 		qt.materialPopup = Ext.get(Ext.DomHelper.append(qt.getLayoutTarget().up('div'),'<div id="materialTable" style="display:none;position:relative;width:300px"></div>'));

		qt.on('show', function (){
			if (!this.activeTarget) {return};
			if (!this.activeTarget.el) {return};
			var clist = Ext.query("table[class='materialTable']");
			for(var i=0;i<clist.length;i++){
				Ext.get(clist[i]).remove();
			}
				
			//material info popup -> ajax call to get data			
			if(this.activeTarget.el.className == 'custorderMaterialInfo'){
				var materialInfoEl = this.activeTarget.el;
				var qt = this;
				var custorderNr = qt.activeTarget.el.getAttribute('custorderNr');
				var lastDelivery = qt.activeTarget.el.getAttribute('lastDelivery');
				DispoAjaxService.getDetailedMaterialInfo(custorderNr,function(custorderItemSumQtyWrapperList) { 
					buildCustorderMaterialInfo(qt, custorderItemSumQtyWrapperList, lastDelivery);});
				
			}
			
			var ao = Ext.get(this.activeTarget.el).up('div[class=auftragOuter]',5);
			if(ao == null){
				var sourcediv = Ext.get(this.activeTarget.el).dom.getAttribute('sourcediv');
				if(!Ext.isEmpty(sourcediv)){
					ao = Ext.get(sourcediv).up('div[class=auftragOuter]',5);
				}
			}

		});
		qt.on('hide', function (){
// 			this.materialPopup.hide();
// 			var materialTable = Ext.get('materialTable')
			var clist = Ext.query("table[class='materialTable']");
			for(var i=0;i<clist.length;i++){
				Ext.get(clist[i]).remove();
			}
// 			if(materialTable != null){
// 				materialTable.remove();
// 			}
// 			Ext.DomHelper.remove(Ext.get('materialTable'));
		});
		GMapAPILoader.load(true);
		dispodate = new Ext.form.DateField({
	    	applyTo : 'DispoDayPicker',
	    	showToday: false,
	    	format:  '<%= DateTimePatterns.JS_DATE %>',
	    	validateBlur: function(){return false;}
		});
		currentDispoDate = clearTime(getCurrentDispoDateAsUTC());
		
		dispodate.on('valid', function(event){
			if(!dispodate.getValue()){
				dispodate.setValue(new Date());
			}
			currentDispoDate = clearTime(getCurrentDispoDateAsUTC());
			DispoAjaxService.setDispoDate(dispodate.getValue().format('<%= DateTimePatterns.JS_DATE %>'), function() {
			      refreshTrucks();
			      refreshOrders();
			});
		});
		
		sumFreightCostSpanUpdate = new SumFreightCostSpanUpdate(); 
		carrierList = new Ext.form.TriggerField({
			readOnly:true
		});
		carrierList.listPanel = Ext.get('DivCarriersEdit');
		carrierList.onTriggerClick = function(){
			this.listPanel.load({
				url: 'Dispo.do',
				params: {'editCarriers':''},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts:true,
				callback: function(el){
					el.show();
				}	
			});
	        Ext.getDoc().on('mousewheel', this.collapseIf, this);
	        Ext.getDoc().on('mousedown', this.collapseIf, this); 
		}
		carrierList.collapse = function(){
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
	       	Ext.getDoc().un('mousewheel', this.collapseIf, this);
	        Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		carrierList.collapseIf = function(e){
	        if(!e.within(this.listPanel)){
	            this.collapse();
	        }
		}
		carrierList.singleCarrierSelected = false;
		carrierList.applyToMarkup('CarriersField');
		
		<jsp:include page="CarriersTile.jsp"/>

			
		DispoAjaxService.fetchCurrentCarriers(function (carriers){
		  currentCarriers = new Ext.util.MixedCollection();
		  currentCarriers.addAll(carriers);
	      //refreshTrucks();
	   	  //refreshOrders();
	   	});
			   			
		window.onresize = resizeTables;
		resizeTables();
		
		// Load the trucks and orders for the first time
		refreshTrucks();
		refreshOrders()
		
		var dd = Ext.get("tableresize1").initDD("tableresize1", null, {
			startDrag: function(x, y){
				this.getEl().style.backgroundColor = "#999";
				var dispoplanning = Ext.get('dispoplanningcontainer');
				var tablesheight = document.body.clientHeight - dispoplanning.getTop() - 60;
				var currentTop = Ext.get(this.getEl()).getTop();
				var min = dispoplanning.getTop() + tablesheight * this.minfact;
				var maxHeightFactor = hideRealTrucks ? this.maxfact : ((heightFactorPlanning + heightFactorReal) * this.maxfact);
				var max = dispoplanning.getTop() + tablesheight * maxHeightFactor + 40;
				this.setXConstraint(0, 0, 0);
				this.setYConstraint(((currentTop - min) < 0)?0:(currentTop - min), ((max - currentTop) < 0)?0:(max - currentTop), 0);		
				this.resetConstraints();
			},
			onInvalidDrop: function(e){
				this.getEl().style.backgroundColor = "transparent";
				var dispoplanning = Ext.get('dispoplanningcontainer');
				var tablesheight = document.body.clientHeight - dispoplanning.getTop() - 60;
				var newHeight = Ext.get(this.getEl()).getTop() - dispoplanning.getTop();
				var newHeightFactorPlanning = newHeight/tablesheight;
				var maxHeightFactor = hideRealTrucks ? this.maxfact : ((heightFactorPlanning + heightFactorReal) * this.maxfact);
				if (newHeightFactorPlanning < this.minfact){
					newHeightFactorPlanning = this.minfact;
				}
				else if (newHeightFactorPlanning > maxHeightFactor){
					newHeightFactorPlanning = maxHeightFactor;
				}
				if (hideRealTrucks){
					heightFactorPlanning = newHeightFactorPlanning;
					heightFactorReal = 0;
				}
				else {
					heightFactorReal = (heightFactorReal + heightFactorPlanning) - newHeightFactorPlanning;
					heightFactorPlanning = newHeightFactorPlanning;
				}
				resizeTables();
				this.getEl().style.top = "0px";
			},
			maxfact: 0.9,
			minfact: 0.1
		});
		
		var dd2 = Ext.get("tableresize2").initDD("tableresize2", null, {
			startDrag: function(x, y){
				this.getEl().style.backgroundColor = "#999";
				var dispotable = Ext.get('dispocontainer');
				var tablesheight = document.body.clientHeight - dispotable.getTop() - 60;
				var currentTop = Ext.get(this.getEl()).getTop();
				var min = dispotable.getTop() + tablesheight * this.minfact + 40;
				var max = dispotable.getTop() + tablesheight * this.maxfact + 40;
				this.setXConstraint(0, 0, 0);
				this.setYConstraint(((currentTop - min) < 0)?0:(currentTop - min), ((max - currentTop) < 0)?0:(max - currentTop), 0);		
				this.resetConstraints();
			},
			onInvalidDrop: function(e){
				this.getEl().style.backgroundColor = "transparent";
				var dispotable = Ext.get('dispocontainer');
				var tablesheight = document.body.clientHeight - dispotable.getTop() - 60;
				var newHeight = Ext.get(this.getEl()).getTop() - 40 - dispotable.getTop();
				var heightFactor = newHeight/tablesheight;
				if (heightFactor < this.minfact){
					heightFactor = this.minfact;
				}
				else if (heightFactor > this.maxfact){
					heightFactor = this.maxfact;
				}
				if (hidePlanningTrucks){
					heightFactorReal = heightFactor;
					heightFactorPlanning = 0;
				}
				else {
					heightFactorReal = (1 - heightFactorPlanning) * heightFactor;
				}
				resizeTables();
				this.getEl().style.top = "0px";
			},
			maxfact: 0.9,
			minfact: 0.1
		});
		 
		updateRunner = new Ext.util.TaskRunner();
		updateTask = {
			    run: function(){
			    	if (dispo && !dispo.draggingInProgress && !isPopupShown()){
			    		var now = new Date();
						var diff = now.getElapsed(lastPageReload);
			    		if (diff < 7200000) { // 2 hours
				    		Ext.QuickTips.getQuickTip().hide();
			    			truckRefresher.refresh();
			    			orderRefresher.refresh();
						}
						else {
							window.location = document.URL; // reload whole window
						}
					}	
			   },
			    interval: AUTO_REFRESH_INTERVAL 
			};
		updateRunnerSetEnabled(IS_AUTO_REFRESH_ENABLED);
		
		// *** Filter options for display ***

		displayField = new Ext.form.TriggerField({
			readOnly: true
		});
		displayField.listPanel = Ext.get('divDisplayEdit');
		displayField.onTriggerClick = function() {
			this.listPanel.load({
				url: 'Dispo.do',
				params: { 'editDisplay' : '' },
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true,
				callback: function(el) {
					el.show();
				}
			});
			Ext.getDoc().on('mousewheel', this.collapseIf, this);
			Ext.getDoc().on('mousedown', this.collapseIf, this);
		}
		displayField.collapse = function() {
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
			Ext.getDoc().un('mousewheel', this.collapseIf, this);
			Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		displayField.collapseIf = function(e) {
			if (!e.within(this.listPanel)) {
				this.collapse();
			}
		}
		displayField.applyToMarkup('displayField');
		
		<jsp:include page="DispoDisplayTile.jsp"/>
		
		// *** Delivering plants ***

		deliveringPlantList = new Ext.form.TriggerField({
			readOnly: true
		});
		deliveringPlantList.listPanel = Ext.get('divDeliveringPlantsEdit');
		deliveringPlantList.onTriggerClick = function() {
			this.listPanel.load({
				url: 'Dispo.do',
				params: { 'editDeliveringPlants' : '' },
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true,
				callback: function(el) {
					el.show();
				}
			});
			Ext.getDoc().on('mousewheel', this.collapseIf, this);
			Ext.getDoc().on('mousedown', this.collapseIf, this);
		}
		deliveringPlantList.collapse = function() {
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
			Ext.getDoc().un('mousewheel', this.collapseIf, this);
			Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		deliveringPlantList.collapseIf = function(e) {
			if (!e.within(this.listPanel)) {
				this.collapse();
			}
		}
		deliveringPlantList.singleDeliveringPlantSelected = false;
		deliveringPlantList.applyToMarkup('deliveringPlantsField');
		
		loadPackageTypeTransportCategoryMaps();
		
		<jsp:include page="DispoDeliveringPlantsTile.jsp"/>
		
		// *** Filter options for order delivery status ***
		
		orderStatusList = new Ext.form.TriggerField({
			readOnly: true
		});
		orderStatusList.listPanel = Ext.get('divOrderStatusEdit');
		orderStatusList.onTriggerClick = function() {
			this.listPanel.load({
				url: 'Dispo.do',
				params: { 'editOrderStatuses' : '' },
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true,
				callback: function(el) {
					el.show();
				}
			});
			Ext.getDoc().on('mousewheel', this.collapseIf, this);
			Ext.getDoc().on('mousedown', this.collapseIf, this);
		}
		orderStatusList.collapse = function() {
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
			Ext.getDoc().un('mousewheel', this.collapseIf, this);
			Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		orderStatusList.collapseIf = function(e) {
			if (!e.within(this.listPanel)) {
				this.collapse();
			}
		}		
		orderStatusList.applyToMarkup('orderStatusField');
		
		<jsp:include page="DispoOrderStatusTile.jsp"/>
		
		// *** Filter options for customer order number ***
		
		function filterByOrderNbr(field, newValue, oldValue) {
			DispoAjaxService.setOrderNumber(newValue, function() {
				orderNumber = newValue;
				refreshTrucks();
				refreshOrders();
			});
		}
		
		var orderNumberText = new Ext.form.TextField({
			id: 'orderNumberText',
			applyTo: 'orderNumber',
			value: orderNumber,
			maskRe: /\d/i,
			listeners: {
				change: filterByOrderNbr
			}
		});
		
		function setTruckPriorityFilter(field, newValue, oldValue) {
			
			DispoAjaxService.setTruckPriorityFilter(newValue, function() {
				truckpriority = newValue;
		    	refreshTrucks();
		   		refreshOrders();
		   		var hiddenTrickPriority = document.getElementById('hiddenTruckPriority');
		   		//struts converts empty to 0 in case of Long but we need null.
				hiddenTrickPriority.value = newValue;
			});
		}
		
		var truckPriorityText = new Ext.form.TextField({
			id: 'truckPriorityText',
			applyTo: 'truckpriority',
			value: truckpriority,
			maskRe: /\d/i,
			maxLength: 2,
			listeners: {
				change: setTruckPriorityFilter
			}
		});
		
		Ext.get('buttonSavePreferences').on('click', function() {
			var params = { "formAction": actions.ACTION_SAVE_PREFERENCES };
			
			addFormElements(params, "DispoForm"); // add form data
			Ext.Ajax.request({
				url: 'Dispo.do',
				params: params
			});
		});
		
		Ext.get('buttonResetField').on('click', function() {
			var params = { "formAction": actions.ACTION_RESET_PREFERENCES };
			
			Ext.Ajax.request({
				url : 'Dispo.do',
				params : params,
				success: function() {
					// Refresh the whole page to refresh the filters
					window.location = document.URL;
				}
			});
		});
	
	});

	// *** Dispo export popup ***

	function showDispoExportPopup() {
		/*
		 * Check if the export popup can be displayed, that is, there is at least one carrier selectable,
		 * if not, we will only display an error message, otherwise the popup will be shown.
		 */
		var div = Ext.get('ShowDispoExportPopupDiv');
		
		div.load({
			url: '../dispo/Dispo.do',
			params: {
				'showDispoExportPopup': true
			},
			scripts: true,
			nocache: true
		});
	}

</script> 

<html:form action="dispo/Dispo.do" styleId='DispoForm'>

<table class="big_form2" style="border-spacing: 0px; padding: 0px;">
  <tbody>
  	<tr>
		<td width="1" class="label" style="vertical-align:bottom;">
			<fmt:message key="dispo.texte.pools"/>
		</td>
		<td width="1" class="label" style="vertical-align:bottom;">
			<fmt:message key="dispo.texte.carriers"/>
		</td>
		<td width="1" class="label" style="vertical-align:bottom;">
 			<fmt:message key="dispo.texte.date"/>
 		</td>
    	<td  width="1"  class="label" style="vertical-align:bottom;">
    		<fmt:message key="dispo.texte.range"/>
    	</td>
    	<td width="1" class="label" style="vertical-align:bottom;">
			<fmt:message key="dispo.texte.deliveringplants"/>
		</td>    	
    	<td width="1" class="label" style="vertical-align:bottom;">
			<fmt:message key="dispo.texte.deliverystatus"/>
		</td>
    	<td width="1" class="label" style="vertical-align:bottom;">
			<fmt:message key="dispo.texte.ordernumber"/>
		</td>
		<td width="1" class="label" style="vertical-align:bottom;">
			<fmt:message key="dispo.texte.truckprio"/>
		</td>
		<td width="1" class="label" style="vertical-align:bottom;">
			<fmt:message key="dispo.texte.trucks.display"/>
 		</td>
 		<td width="1">
			<input type="button" id="buttonSavePreferences" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>"/><br/>
 		</td>
 		<!-- Auto-update cell -->
	<% if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) { %>
 		<td width="1" class="label" style="vertical-align:bottom;">
 		</td>
	<% } %>
	</tr>
	<tr>
		<td width="1" style="padding-top: 0px;"> 
			<script type="text/javascript">
				function setSelectedPool(){
					a = {'setPool':''};
					carrierList.collapse();
					addFormElements(a, "DispoForm"); <%-- add form data --%>
					Ext.Ajax.request({
						url: 'Dispo.do', 
						params: a,
						success: function(response){
							eval(response.responseText);
							DispoAjaxService.fetchCurrentCarriers(function (carriers){
								  currentCarriers = new Ext.util.MixedCollection();
								  currentCarriers.addAll(carriers);								  
						      refreshTrucks();
						   	  refreshOrders();
						   	});
						}
					});
				}
				
				function setAllCheckboxes(value, container){
					list = Ext.query("input[type='checkbox']", Ext.getDom(container));
					for (i = 0; i < list.length; i++){
						list[i].checked = value;
					}
				}
				
				function setHourSpan(newSpan){
					hourSpan =  newSpan * 1;
					if ((startHour + hourSpan) > 24){
						startHour = 24 - hourSpan;
					} 
					DispoAjaxService.setStartHourAndHourSpan(null, hourSpan, function() {
				    	refreshTrucks();
				   		refreshOrders();
					});
				}
				
				function setReturned(assingmentIds) {
					DispoAjaxService.setReturned(assingmentIds, updateTruckListOneLine);
				}

				function setUnloaded(assingmentIds) {
					DispoAjaxService.setUnloaded(assingmentIds, updateTruckListOneLine);
				}
				
				function setAtShipto(assingmentIds) {
					DispoAjaxService.setAtShipto(assingmentIds, updateTruckListOneLine);
				}
				
				function loadAtCarrierStock(assingmentIds) {
					DispoAjaxService.loadAtCarrierStock(assingmentIds, updateTruckListOneLine);
				}
				
				function enterShippingpoint(assingmentIds) {
					DispoAjaxService.enterShippingpoint(assingmentIds, updateTruckListOneLine);
				}
				
				function acceptOrder(){
					if (authcarrier){
						DispoAjaxService.acceptOrderCarrier(orderList.selection.order.orderNumber, authpool, refreshOrders);
					}
					else if (authpool){
						DispoAjaxService.acceptOrderPool(orderList.selection.order.orderNumber, refreshOrders);
					}
				}
			
				function transmitToCarrier(){
					DispoAjaxService.transmitToCarrier(orderList.selection.order.orderNumber, refreshOrders);
				}
				
				function createExternalOrder(){
					a = {'createExternalOrder':'newOrder', 'dateString': dispodate.value};
					showPopup('ExternalOrderEntry.do', null, a);
				}
				
				onOrderDetailClose = refreshOrders;
			
			</script>
	
			<html:select size="1" style="width: 100px;" property="selectedPool" onchange="setSelectedPool()" styleClass="x-form-text, x-form-field">
				<c:forEach var="pool" varStatus="status" items="${order_dispo_DispoForm.pools}">
					<html:option value="${status.count - 1}"><c:out value="${pool.name}"/></html:option>
				</c:forEach>					
			</html:select>
				
	 	</td>
		<td width="1" style="vertical-align:top; padding-top: 0px;">
			<input type="text" id="CarriersField" style="width: 100px; color: black"/>
			<div class="border" id="DivCarriersEdit" style="z-index:40;background:white;width:200px; position: absolute;visibility:hidden">
			</div>
 		</td>
 		<td width="1" style="vertical-align:top; padding-top: 0px;">
			<html:text styleClass='textfield' style='width: 80px' styleId='DispoDayPicker' property="stringDate"/>
 		</td>
    	<td width="1" style="vertical-align:top; padding-top: 0px;">
    		<html:select property="hourSpan" style="width:75px;" onchange="setHourSpan(this.value)">
    			<html:option value="2">2 h</html:option>
    			<html:option value="3">3 h</html:option>
    			<html:option value="4">4 h</html:option>
    			<html:option value="5">5 h</html:option>
    			<html:option value="6">6 h</html:option>
    			<html:option value="7">7 h</html:option>
    			<html:option value="8">8 h</html:option>
    			<html:option value="9">9 h</html:option>
    			<html:option value="10">10 h</html:option>
    			<html:option value="11">11 h</html:option>
    			<html:option value="12">12 h</html:option>
    			<html:option value="13">13 h</html:option>
    			<html:option value="14">14 h</html:option>
    			<html:option value="15">15 h</html:option>
    			<html:option value="16">16 h</html:option>
    			<html:option value="17">17 h</html:option>
    			<html:option value="18">18 h</html:option>
    			<html:option value="19">19 h</html:option>
    			<html:option value="20">20 h</html:option>
    			<html:option value="21">21 h</html:option>
    			<html:option value="22">22 h</html:option>
    			<html:option value="23">23 h</html:option>
    			<html:option value="24">24 h</html:option>
    		</html:select>
    	</td>
    	
    	<!-- Delivering plants -->

		<td width="1" style="vertical-align:top; padding-top: 0px;">
			<input type="text" id="deliveringPlantsField" style="width: 100px; color: black"/>
			<div class="border" id="divDeliveringPlantsEdit"
			     style="z-index: 40; background: white; width: 200px; position: absolute; visibility: hidden">
			</div>
 		</td>
 		
 		<!-- Delivery status -->
 		
 		<td width="1" style="vertical-align:top; padding-top: 0px;">
    		<input type="text" id="orderStatusField" style="width: 100px;"/>
    		<div class="border" id="divOrderStatusEdit"
			     style="z-index: 40; background: white; width: 200px; position: absolute; visibility: hidden">
			</div>
    	</td>
    	
    	<!-- Customer order -->
    	
    	<td width="1" style="vertical-align:top; padding-top: 0px;">
    		<input type="text" id="orderNumber" style="width: 100px;"/>
    	</td>
    	
    	<!--  Truck priority -->
		 <td width="1" style="vertical-align:top; padding-top: 0px;">
    		<input type="text" id="truckpriority" style="width: 100px;"/>
    		<html:hidden property="truckPriority" styleId="hiddenTruckPriority" />
    	</td>
    	
    	<!-- Display options filter -->
			
		<td width="1" style="vertical-align:top; padding-top: 0px;">
			<input type="text" id="displayField" style="width: 120px; color: black"/>
			<div class="border" id="divDisplayEdit"
			     style="z-index: 40; background: white; width: 200px; position: absolute; visibility: hidden">
			</div>
		</td>
        <td width="1">
			<input type="button" id="buttonResetField" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>"/>
 		</td>
<% if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) { %>
 		<td width="1" style="vertical-align:top; padding-top: 0px;">
 			<nobr>
 				<input id="devModeAutoUpdate" type="checkbox" onclick="updateRunnerSetEnabled(this.checked);" checked="true"/>
 				<label for="devModeAutoUpdate">auto-update</label>
 			</nobr>
 			<span style="font-size: xx-small;"><nobr>(button shown in dev mode only)</nobr></span>
 		</td>
<% } %>

    </tr>
  </tbody>
</table>

</html:form>
<table style="width:100%" id="schedulingHeaderTable">
<tr>
	<td>
		<p class="label" style="margin-top: 8px">
			<fmt:message key="dispo.texte.disposition"/>
			<span id="outputSpan"> </span>
		</p>
	</td>
	<td align="right">
		<input onclick="showDispoExportPopup()" id="buttonExport" type="button" class="ViewButtonStyle"
				value="<fmt:message key="buttons.export.scheduling"/>"/> 
	</td>
	<td style="width:15px">
	</td>
</tr>
</table>
<div id="dispoplanningcontainer" class="dispocontainer"></div>
<div id="tableresize1" style="position:relative;background-color:transparent;width:100%; height:3px; z-index:30; cursor: row-resize; text-align:center;font-size: 1px">
	<img style='background-color:#999; width: 300px;height:1px;'></img>
</div>
<div id="dispocontainer" class="dispocontainer"></div>
<table style="width:100%">
	<tr>
		<td class="label" style="padding-top: 8px">
			<fmt:message key="dispo.texte.orders"/>
		</td>
		<td class="buttons right" id="buttonsCell" style="vertical-align: top">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.reroute"/>" id="buttonReroute" 
				onclick="modifyButtonState(this,false); showReroutingPopup(dispo.selection)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.returned"/>" id="buttonReturned" 
				onclick="modifyButtonState(this,false);setReturned(dispo.selection)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.unloaded"/>" id="buttonUnloaded" 
				onclick="modifyButtonState(this,false);setUnloaded(dispo.selection)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.atshipto"/>" id="buttonAtShipto" 
				onclick="modifyButtonState(this,false);setAtShipto(dispo.selection)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.loadAtCarrierStock"/>" id="buttonLoadAtCarrierStock" 
				onclick="modifyButtonState(this,false);loadAtCarrierStock(dispo.selection)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.entersp"/>" id="buttonEnterShippingpoint" 
				onclick="modifyButtonState(this,false);enterShippingpoint(dispo.selection)">								
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.transmit"/>" id="buttonTransmit" 
				onclick="modifyButtonState(this,false);showReleaseAssignmentsDialog(dispo.selection)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.remove"/>" id="buttonCancel"
				onclick="modifyButtonState(this,false);showCancelAssignmentsDialog(dispo.selection)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.selectallfortransmit"/>" id="buttonSelectAllForTransmit"
				onclick="modifyButtonState(this,false);dispo.selectAllForTransmit()">				
			
		</td>
		<td style="width:10px">
		</td>
	</tr>
</table>
<div id="tableresize2" style="position:relative;background-color:transparent;width:100%; height:3px; z-index:30; cursor: row-resize; text-align:center;font-size: 1px">
	<img style='background-color:#999; width: 300px;height:1px;'></img>
</div>
<div id="orderlistcontainer" class="dispocontainer"></div>
<table style="width:100%">
	<tr>
		<td class="buttons right" style="vertical-align: top;">
<crh-common:authorize function="<%= Authorization.DISPATCHING_CREATE_EXT_ORD %>">
			<input type="button" class="SmallButtonStyle" value="Create External Order" id="buttonCreateExternalOrder" 
				onclick="createExternalOrder()">
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.DISPATCHING_POOL_W %>">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.transmitToCarrier"/>" id="buttonTransmitToCarrier" 
				onclick="modifyButtonState(this,false);transmitToCarrier()">			
</crh-common:authorize>
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.acceptOrder"/>" id="buttonAcceptOrder" 
				onclick="modifyButtonState(this,false);acceptOrder()">			
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.cancelOrder"/>" id="buttonCancelOrder" 
				onclick="modifyButtonState(this,false);showCancelOrderDialog(orderList.selection.order.orderNumber)">			
		</td>
		<td style="width:10px">
		</td>
	</tr>
</table>

<div id="ShowDispoExportPopupDiv" style="display: none; visibility: hidden"></div>

</c:if>

<%-- END $RCSfile: DispoTile2.jsp,v $ --%>
