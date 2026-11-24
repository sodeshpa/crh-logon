<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderDetailTile.jsp,v $ --%>

<%@page session="true"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.taglib.OrderDetailButtonLogicTag"%>

<%@page
	import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<%@page import="com.crh.logon.order.web.forms.auftrag.OrderDetailForm"%>


<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<c:set var="orderDetailUrl">
    /../order/auftrag/OrderDetail.do
</c:set>
<c:set var="returnReasonPopupUrl">
    /../order/auftrag/ReturnReasonPopupEntry.do
</c:set>

<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />
<c:set var="order" value="${form.order}"/>
<c:set var="customerOrderDeliveryWindowDateTimeFromToString" value="${form.customerOrderDeliveryWindowDateTimeFromToString}"/>

<c:set var="displayShipToTimezone" scope="request" value="${form.displayShipToUTCOffset}"/>
<c:set var="shipToTimeZoneFmtOffset" scope="request" value="${form.shiptoTimeZoneAsUTCOffset}"/>

<%-- DWR services --%>
<script type="text/javascript" src="<crh-common:url
		value='/dwr/interface/LiveKarteAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/dwr/interface/OrderDetailAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/dwr/interface/DispoAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/dwr/interface/ITrackAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/logon-gmap.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value="/js/orderDetailMap.js"/>"></script>
<script type="text/javascript" src="<crh-common:url
		value="/js/markermanager.js"/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.MaterialCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.TableStyleCheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url
		value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>

<c:set var="ORDER_STATUS_DELETED"><%=Constant.ORDER_STATUS_DELETED%></c:set>
<c:set var="ORDER_STATUS_DISPATCHED"><%=Constant.ORDER_STATUS_DISPATCHED%></c:set>
<c:set var="ORDER_STATUS_DELIVERED"><%=Constant.ORDER_STATUS_DELIVERED%></c:set>
<c:set var="ORDER_STATUS_FINISHED"><%=Constant.ORDER_STATUS_FINISHED%></c:set>
<c:set var="ORDER_STATUS_NEW"><%=Constant.ORDER_STATUS_NEW%></c:set>
<c:set var="ORDER_STATUS_TRANSMITTED_TO_POOL"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_POOL%></c:set>
<c:set var="ORDER_STATUS_TRANSMITTED_TO_POOL_ACK"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK%></c:set>
<c:set var="ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT"><%=Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT%></c:set>
<c:set var="ORDER_STATUS_UNLOADING"><%=Constant.ORDER_STATUS_UNLOADING%></c:set>
<c:set var="ORDER_STATUS_SHIPPING"><%=Constant.ORDER_STATUS_SHIPPING%></c:set>
<c:set var="ORDER_STATUS_TRANSMITTED_TO_DRIVER"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER%></c:set>
<c:set var="ORDER_STATUS_TRANSMITTED_TO_RAIL"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL%></c:set>

<c:set var="DELIVERY_TYPE_ROAD_DIRECT"><%=Constant.DELIVERY_TYPE_ROAD_DIRECT%></c:set>
<c:set var="DELIVERY_TYPE_RAIL_DIRECT"><%=Constant.DELIVERY_TYPE_RAIL_DIRECT%></c:set>
<c:set var="DELIVERY_TYPE_BIMODAL_ROAD"><%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%></c:set>
<c:set var="DELIVERY_TYPE_BIMODAL_RAIL"><%=Constant.DELIVERY_TYPE_BIMODAL_RAIL%></c:set>
<c:set var="DELIVERY_TYPE_PICKUP"><%=Constant.DELIVERY_TYPE_PICKUP%></c:set>
<c:set var="DISTRIBUTION_DEST_REPAIR"><%=Constant.DISTRIBUTION_DEST_REPAIR%></c:set>

<c:set var="isDispatched">
	<c:out value="${form.dispatchedFlag}" />
</c:set>

<c:set var="isTransmittedToDriver">
	<c:out value="${form.transmittedToDriver}" />
</c:set>

<c:set var="isTransmittedToRailFlag">
	<c:out value="${form.transmittedToRailFlag}" />
</c:set>

<c:set var="isAssigned">
	<c:out value="${form.assignment != null}" />
</c:set>

<c:set var="isRailOrder">
	<c:out value="${order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_RAIL_DIRECT 
	|| order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL}" />
</c:set>

<c:set var="isForwardToPoolDisabled">true</c:set>
<c:if test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_NEW)}">
    <c:set var="isForwardToPoolDisabled">false</c:set>
</c:if>
<c:set var="isForwardToCommCarrierDisabled">true</c:set>
<c:if test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_NEW) ||
            (order.orderStatus.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_POOL) ||
            (order.orderStatus.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_POOL_ACK)}">
    <c:set var="isForwardToCommCarrierDisabled">false</c:set>
</c:if>

<c:set var="isPickup">false</c:set>
<c:if test="${(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_PICKUP)}">
    <c:set var="isPickup">true</c:set>
</c:if>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>

<c:set var="isDelivTimeDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_DELIVERY_DATE_TIME %>">
<c:set var="isDelivTimeDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isWagonEmptyReportDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
<c:if test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_SHIPPING) ||
            (order.orderStatus.orderStatusCd == ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) ||
            (order.orderStatus.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_RAIL) ||
            (order.orderStatus.orderStatusCd == ORDER_STATUS_UNLOADING)}">
    <c:set var="isWagonEmptyReportDisabled">false</c:set>
</c:if>
</crh-common:authorize>

<c:set var="isForwardingAgentDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_FORWARDING_AGENT %>">
<c:set var="isForwardingAgentDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isSpecialDetailDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_W %>">
<c:set var="isSpecialDetailDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isSPIndicatorDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_SPI %>">
<c:set var="isSPIndicatorDisabled">false</c:set>
</crh-common:authorize>

<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%>
<script type="text/javascript">

	Ext.QuickTips.init();
	Ext.apply(Ext.QuickTips.getQuickTip(), {
		maxWidth: 400,
		dismissDelay: 0
	});
	<crh-common:authorize function="<%= Authorization.DISPATCHING_W %>">
	<c:if test="${order.itrackStatus.itrackStatusCd == 2}">
		Logon.message.warning('<fmt:message key="orderdetail.main.warning.different.shipto"/>');
	</c:if>
	</crh-common:authorize>
	var orderId = <c:out value="${order_auftrag_OrderDetailForm.order.orderId}" />;
	
	function sortComboByName(comboId) {
		
		function toArray( obj ) {
			var i, arr = [];
			for ( i = obj.length; i--; ){
				arr[i] = obj[i];
			}
			return arr;
		}

		// custom compare function for sorting 
		function sortByText( el1, el2 ) {  
			if ( el1.text < el2.text ) return -1;
			else if( el1.text > el2.text ) return 1;
			else return 0;
		}
		
		// select the elements to be ordered
		var itemsId = document.getElementById(comboId);
		
		if (itemsId == null) {
			return;
		}
		
		var items   = itemsId.getElementsByTagName("option");
		var len     = items.length;

		// convert to array, to make sorting possible
		items = toArray( items );
		items = items.sort( sortByText );

		// append them back to the parent in order
		for ( var i = 0; i < len; i++ ) {
		  itemsId.appendChild( items[i] );
		}
	}

	/*
	 Returns the hour and minute in an array from the Ext.form.TimeField given by id
	*/
	function getTimePartsArrayHM(id) {
		// the String has the following format: "HH:MM"
		var timeString =  Ext.get(id).getValue();
		return timeString.split(":");
	}
	
	/*
	 Returns the year(4 digit), month, day from the Ext.form.DateField given by id.
	 The year is calculated using the current century from the current date since the DateField only stores the last two digit of the year
	*/
	function getDatePartsArrayYMD(id) {
		// the String has the following format: "dd.mm.yy"
		var dateParts = Ext.get(id).getValue().split('.');
		var currentDate = new Date();
		var century = ('' + currentDate.getFullYear()).substring(0,2);
		return [century + dateParts[2], dateParts[1], dateParts[0]];
	}
	
	/*
	 Returns a JS Date object from the Ext.form.DateField and Ext.form.TimeField given by id
	*/
	function getDateTime(dateId, timeId) {
		var dateParts = getDatePartsArrayYMD(dateId)
		var timeParts =  getTimePartsArrayHM(timeId);
		var date = new Date(dateParts[0], dateParts[1]-1, dateParts[2], timeParts[0], timeParts[1], 0, 0); 

		return date;
	}

	// Customer Order Delivery Window From Date/Time
	var customerOrderDeliveryWindowFromDateTime;
	// Customer Order Delivery Window To Date/Time
	var customerOrderDeliveryWindowToDateTime;
	// Array of date fields for validation
	var dateFields;
	
	/*
	 Validates the from-to delivery date interval Ext.form.DateField and Ext.form.TimeField controls.
	 Returns true if the value is valid, or a String containing the error message if the value is invalid
	*/
	function validateOrderDateFields() {

	    var id = this.getId();
		var orderFromDateTime = getDateTime('deliveryFromDate', 'orderDetailDeliveryFromTime');
		var orderToDateTime = getDateTime('deliveryToDate', 'orderDetailDeliveryToTime');
		
		if (orderFromDateTime > orderToDateTime) {
			return '<fmt:message key="orderdetail.main.errors.wrong.deliv.time"/>';				
		}

        return true;
	}

	/*
	 When a field is changed this listener is called and also validates the other fields
	*/
	function selectListenerForDateTimeFields(field) {
			for (var i=0; i<dateFields.length; i++) {
				if (dateFields[i] != field) {
					// make validation called
					dateFields[i].validate();
				}
			}
	}

	/*
	 Converts dateString in the format "yyyy,mm,dd,HH,MM" to Date object
	*/
	function getDateFromDateString(dateString) {
		var p = dateString.split(",");
		return new Date(p[0],p[1]-1,p[2],p[3],p[4],0,0);
	}

	/*
	 Converts dateString in the format "yyyy,mm,dd,HH,MM" to Date object
	*/
	function getMinDateFromDateString(dateString) {
		var p = dateString.split(",");
		return new Date(p[0],p[1]-1,p[2],0,0,0,0); 
	}
	
	/*
	 Converts dateString in the format "yyyy,mm,dd,HH,MM" to Date object
	*/
	function getMaxDateFromDateString(dateString) {
		var p = dateString.split(",");
		var nextDay = parseInt(p[2]);
		return new Date(p[0],p[1]-1,nextDay,0,0,0,0); 
	}
	// Delivery date from/to fields
	function initDelivFromTo() {

        <c:if test="${customerOrderDeliveryWindowDateTimeFromToString != null && customerOrderDeliveryWindowDateTimeFromToString != ''}" >
            var customerOrderDeliveryWindowFromTo = '<c:out value="${customerOrderDeliveryWindowDateTimeFromToString}"/>';
            var customerOrderDeliveryWindowDates = customerOrderDeliveryWindowFromTo.split(";");
            customerOrderDeliveryWindowFromDateTime = getDateFromDateString(customerOrderDeliveryWindowDates[0]);
		    customerOrderDeliveryWindowToDateTime = getDateFromDateString(customerOrderDeliveryWindowDates[1]);
        </c:if>
       
		var delFromDat = new Ext.form.DateField({
			id: 'orderDetailDelFromDat',
	    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'deliveryFromDate',
	    	//value: '<c:out value="${cust_WebBestellungForm.deliveryFromDatStr}"/>',
	        allowBlank: false,
		    disabled: <c:out value="${isDispatched || isDelivTimeDisabled}"/>,
		    //vtype: 'daterange',
			endDateField: 'delToDat',
	    	width: 82,
	    	validator: validateOrderDateFields,
	    	listeners : {
	    	      select :selectListenerForDateTimeFields
	    	}
		});

		var delToDat = new Ext.form.DateField({
			id: 'orderDetailDelToDat',
	    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'deliveryToDate',
	    	//value: '<c:out value="${cust_WebBestellungForm.deliveryToDatStr}"/>',
	        allowBlank: false,
		    disabled: <c:out value="${isDispatched || isDelivTimeDisabled}"/>,
		    //vtype: 'daterange',
			startDateField: 'delFromDat',	    
	    	width: 82,
	    	validator: validateOrderDateFields,
	    	listeners : {
	    	      select :selectListenerForDateTimeFields
	    	}

		});
		
		var delFromTime = new Ext.form.TimeField({
			id: 'orderDetailDelFromTime',
	    	increment: 15,
	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'orderDetailDeliveryFromTime',
		    disabled: <c:out value="${isDispatched || isDelivTimeDisabled}"/>,
	    	width: 82,
	    	validator: validateOrderDateFields,
	    	listeners : {
	    	      select :selectListenerForDateTimeFields
	    	}

		});

		var delToTime = new Ext.form.TimeField({
			id: 'orderDetailDelToTime',
	    	increment: 15,
	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'orderDetailDeliveryToTime',
		    disabled: <c:out value="${isDispatched || isDelivTimeDisabled}"/>,
	    	width: 82,
	    	validator: validateOrderDateFields,
	    	listeners : {
	    	      select :selectListenerForDateTimeFields
	    	}

	    });
		
		dateFields = [delFromDat, delToDat, delFromTime, delToTime];
	}

	
	function hide(){
			//at several places the screens have to be reloaded/initialized and so on... define function logonPopupHideCallback() on the caller page if you need to call it when calling hidepopup 
			hidePopupCallFunction();
	}
	
	function changeShPoint(plantCd) {

		if (plantCd == '-1') {
			document.getElementById('plantCodeId').value = "";
		}
		else {
			document.getElementById('plantCodeId').value = plantCd;
		}	

		document.getElementById('shPointNoId').value = "";
		
		var diva = Ext.get('shPointDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {
				'formAction' :'<%=OrderDetailAction.FORWARD_SHIPPING_POINT%>',
				'plantCd' :plantCd
			},
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true,
			callback: function() { changeLdPoint('-1');}
		});
	}

	function changeLdPoint(shPointNo) {
		if (shPointNo == '-1') {
			document.getElementById('shPointNoId').value = "";
		} 
		else {
			document.getElementById('shPointNoId').value = shPointNo;
		}

		var diva = Ext.get('LPointDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {
				'formAction' :'<%=OrderDetailAction.FORWARD_LOADING_POINT%>',
				'shPointNo' :shPointNo
			},
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true
		});
		
	}

	function changePool(poolId) {

		document.getElementById('commCarrierVendorSapId').value = "";
		document.getElementById('execCarrierVendorSapId').value = "";
		
		var diva = Ext.get('commCarrierDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {
				'formAction' :'<%=OrderDetailAction.FORWARD_COMM_CARRIER%>',
				'poolId' : poolId
			},
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true,
			callback: function(){
				document.getElementById('orderBean.commCarrierCombo').value = "-1";
				document.getElementById('orderBean.execCarrierCombo').value = "-1";
				sortComboByName("orderBean.commCarrierCombo");
				sortComboByName("orderBean.execCarrierCombo");
				sortComboByName("orderBean.loadingPointCombo");	
			}
		});
	}

	
	function changeCommCarrier(carrierSapId) {
		if (carrierSapId == '-1') {
			document.getElementById('commCarrierVendorSapId').value = "";
			document.getElementById('execCarrierVendorSapId').value = "";
			document.getElementById('orderBean.execCarrierCombo').value = "-1";
		} 
		else {
			var poolId = document.getElementById('poolId').value;
			var commCarrierId = document.getElementById("orderBean.commCarrierCombo").value;
			document.getElementById('commCarrierVendorSapId').value = carrierSapId;
			
			var diva = Ext.get('execCarrierDiv');
			diva.load( {
				url :'<c:url value="${orderDetailUrl}"/>',
				params : {
					'formAction' :'<%=OrderDetailAction.FORWARD_EXEC_CARRIER%>',
					'poolId' : poolId,
					'commCarrierId': commCarrierId
				},
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true,
				callback: function() {
					document.getElementById('orderBean.execCarrierCombo').value = "-1";
				}
			});
		}
		
	}

	function changeExecCarrier(carrierSapId) {
		if (carrierSapId == '-1') {
			document.getElementById('execCarrierVendorSapId').value = "";
		} 
		else {
			document.getElementById('execCarrierVendorSapId').value = carrierSapId;
		}
	}
	
    function openReturnReasonPopup(orderItemId, sapMaterialId, index) {
        
        var diva = Ext.get('logonPopup2');
        diva.hide();
        diva.load( {
            url :'<c:url value="${returnReasonPopupUrl}"/>',
            params : {
                'formAction'  : '<%=OrderDetailAction.FORWARD_RETURN_REASON_POPUP%>',
                'selectedOrderItemId' : orderItemId,
                'selectedSapMaterialId' : sapMaterialId
            },
            text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
            scripts :true
        });
        var greyLayer = Ext.get('grayout2');

        grayoutScreen(greyLayer);
    }
    
    function openVendorPfPopup() {
    	//TODO armin 2014/11/24: throw out this horrible popup solution (had to use it for now to remain safe..)
    	var popup2 = Ext.get('logonPopup2');
  		
    	function initializeVendorPfPopup() {
    		popup2.center().show();
    		
    		var vendorRec = Ext.data.Record.create([
          	    {name: "id", mapping: "id"},
          	    {name: "name", mapping: "name"}
          	]);
    		
    		var vendorStore = new Ext.data.Store({
    			proxy: new Ext.data.DWRProxyForPager(
    					function(skip, count, sort, query, callback) { OrderDetailAjaxService.fetchSapVendorList(skip, count, sort, query, callback) }, true),
    			reader: new Ext.data.ListRangeReader({id:'id', totalProperty:'totalSize'}, vendorRec)
    		});
    		
    		var vendorCombo = new Ext.ux.TwinCombo({
    			applyTo: 'partnerFunctionVendor',
    			store: vendorStore,
    			displayField: 'name',
    			valueField: 'id',
    			pageSize: 20,
    			width: 180,
    			listWidth: 300,
    			triggerAction: 'all',
    			selectOnFocus: true
    		});
    		
    		Ext.get("vendorpfcancel").on('click', function() {
    			hidePopup(popup2);
    		});
    		
    		Ext.get("vendorpfsave").on('click', function() {
    			var code = Ext.get('partnerFunctionCode').getValue();
    			var vendor = vendorCombo.getValue();
    			
    			OrderDetailAjaxService.saveVendorPf(code, vendor, function() {
    				hidePopup(popup2);
    			});
    		});
    	}

        popup2.hide().load({
            url :'<c:url value="${orderDetailUrl}"/>',
            params : { 'formAction'  : '<%=OrderDetailAction.FORWARD_VENDOR_PF_POPUP%>' },
            text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
            callback: initializeVendorPfPopup
        });
        
        var greyLayer2 = Ext.get('grayout2');
        grayoutScreen(greyLayer2);
    }

	var currentTab = '<%=OrderDetailAction.FORWARD_MATERIAL_TAB%>';
	function setCurrentTab(newTab) {

		//change selected tab
		if ( document.getElementById(currentTab) ) {
			document.getElementById(currentTab).className = '';
		}
		document.getElementById(newTab).className = 'highlight';
		currentTab = newTab;

		//save with a form
	    form = Ext.getDom('OrderDetailForm');
		p = {'formAction' :'<%=OrderDetailAction.LOAD_TAB%>',
			 'selectedTab' :newTab
			};
		addFormElements(p, form);
			
		var diva = Ext.get('TabsDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : p,
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
	}
	
	function reloadOrderDirectly(orderId) {
		orderDetailReload = true;
		logonPopup=Ext.get('logonPopup');
		hidePopup();
		showPopup('/logon/order/auftrag/OrderDetailEntry.do?orderid=' + orderId);
	}
	
	</script>
	
	<jsp:include page="orderdetail/OrderDetailAuthParameters.jsp"></jsp:include>

	<script type="text/javascript">
	
	var exactCustOrderFlagChecked = null;
	var exactCustOrderFlagSwiched = false;
	function exactCustOrderFlagClicked(chk) {
		exactCustOrderFlagChecked = chk.checked;
		exactCustOrderFlagSwiched = !exactCustOrderFlagSwiched;
	}
	
	var exactSingleOrderFlagChecked = null;
	var exactSingleOrderFlagSwiched = false;
	function exactSingleOrderFlagClicked(chk) {
		exactSingleOrderFlagChecked = chk.checked;
		exactSingleOrderFlagSwiched = !exactSingleOrderFlagSwiched;	
	}
	
	function saveCustorder(action) {
		form = Ext.getDom('OrderDetailForm');
	
		var p = {};
		p = {'exactCustorderQtyFlag' :exactCustOrderFlagChecked,
				'exactSdorderQtyFlag' :exactSingleOrderFlagChecked};
		
		p.formAction = action;
		
		addFormElements(p, form);
		
		var diva = Ext.get('TabsDiv');
		diva.load({
			url :'<c:url value="${orderDetailUrl}"/>',
			params : p,
			text :'<fmt:message key="extjs.loading"/>',
			scripts :true
		});
	}
	
	var orderUpdated = orderDetailReload ? orderUpdated : false ;
	var orderDetailReload = false;
	
	function saveOrder(action) {
		//pressing Save button on any tab
		if (action == '<%=OrderDetailAction.FORWARD_SAVE_ORDER%>') {
			saveOrderConfirmIfNeeded(action);
		}
		//pressing Save & Close button on any tab
		else if (action == '<%=OrderDetailAction.FORWARD_SAVE_CLOSE_ORDER%>') {
			saveOrderConfirmIfNeeded(action);
		}
	}

	function saveOrderInc(action) {

		<%------------------------------------------------------------------------------------------------%>
		<%--------------------PARAMS SENT IN CASE USER HAS AUTHORIZATION TO SOME FUNCTIONALITY -----------%>
		<%-------------ORDER ATTRIBUTES SHOULD BE MAPPED ONLY IN CASE USER HAS AUTHORIZED TO CHANGE---------%>
		var aParams = getAuthParams();
		
		form = Ext.getDom('OrderDetailForm');
		p = {'formAction' :action, "authParams" :aParams, "currentTab" :currentTab};
		addFormElements(p, form);

		var diva = Ext.get('saveDiv');
		
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : p,
			text :'<fmt:message key="orderdetail.message.saving"/>',
			scripts :true
		});
		orderUpdated = true;
	}
	
	function saveOrderConfirmIfNeeded(action) {
		<c:choose>
			<c:when test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED)}">
				saveOrderWithConfirmation(action);   
			</c:when>
			<c:otherwise>
				saveOrderInc(action);
			</c:otherwise>
		</c:choose>
	}
	
	function replacePopup(action) {
		var aParams = getAuthParams();
	     form = Ext.getDom('OrderDetailForm');
		 p = {'formAction' :action,"authParams":aParams};
		 addFormElements(p, form);

		var diva = Ext.get('logonPopup');
		diva.hide();
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : p,
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true
		});
	}
	
	function closePopup() {
		if(orderUpdated && (typeof(allOrders) != "undefined")){ 
			allOrders.reload();
		}
			
		logonPopup=Ext.get('logonPopup');
		hide();
		if (typeof(onOrderDetailClose)=='function') {
			onOrderDetailClose();
		}
	}
	
	<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
	function doReportWagonEmpty() {
		var diva = Ext.get('OrderDetailDiv');
		diva.load({
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {'formAction' :'<%=OrderDetailAction.FORWARD_REPORT_WAGON_AS_EMPTY%>'},
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true,
					callback: function() { 
						if (typeof(onOrderDetailClose)=='function') {
							onOrderDetailClose();
						}
						hidePopup();
					}
		});
	}
	function reportWagonEmpty() {
		OrderDetailAjaxService.checkBimodalLoadBalance(<%=((OrderDetailForm)session.getAttribute("order_auftrag_OrderDetailForm")).getOrder().getOrderId()%>, 
				function(balance) {
					if (balance != 0) {
						Ext.MessageBox.buttonText.yes = '<fmt:message key="option.yes"/>';
						Ext.MessageBox.buttonText.no = '<fmt:message key="option.no"/>';
						Ext.MessageBox.confirm('<fmt:message key="dispo.messagebox.wagonnotempty.confirm"/>',
								String.format('<fmt:message key="dispo.errors.wagonNotEmpty"/>', '', balance),
								function(btn) {
									if (btn == 'yes') {
										doReportWagonEmpty();
									}
								});
					}
					else {
						doReportWagonEmpty();
					}
		});
	} 
	</crh-common:authorize>
	
	function sendToSAP(){
		Ext.MessageBox.confirm('<fmt:message key="orderdetail.messagebox.sendtosap.title"/>',
				'<fmt:message key="orderdetail.messagebox.sendtosap.text"/>',
				function(btn) {
					if (btn === 'yes') {
						sendToSapAction();
					}
				});	
	}
	
	function sendToSapAction(){
		var diva = Ext.get('OrderDetailDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {'formAction' :'<%=OrderDetailAction.FORWARD_SEND_ORDER_TO_SAP%>'},
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true
		});
	}

	function confirmUnloadLocationAction(){
		var diva = Ext.get('OrderDetailDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {'formAction' :'<%=OrderDetailAction.FORWARD_CONFIRM_UNLOAD_LOCATION%>'},
			text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts :true
		});
	}
	
	function deleteOrder() {
		
		showCommonReasonDialog.createDelegate(this,[
	 		{
			 	title: '<fmt:message key="orderdetail.popup.delete.title"/> - <c:out value="${order.orderNumber}" />',
			 	reason:'<fmt:message key="orderdetail.popup.delete.reason"/>',
			 	addText:
					<c:if test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED)}" > 	 
 						'<fmt:message key="orderdetail.messagebox.specialdeleteclosed.text"/> ' + 
					</c:if>
					'<fmt:message key="orderdetail.messagebox.text"/>',
			 	CancelText: '<fmt:message key="buttons.cancel"/>',
		     	SaveText:'<fmt:message key="orderdetail.messagebox.confirm"/>'
		 	},
			function(inputDialogText){
				var diva = Ext.get('OrderDetailDiv');
				diva.load( {
					url :'<c:url value="${orderDetailUrl}"/>',
					params : {'formAction' :'<%=OrderDetailAction.FORWARD_DELETE_ORDER%>', 'reason': inputDialogText },
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
			}
			])();
		

	}

	function saveOrderWithConfirmation(action) {
		Ext.MessageBox.confirm('<fmt:message key="orderdetail.messagebox.confirm"/>',
			'<fmt:message key="orderdetail.messagebox.specialupdatespi.text"/>',
			function(btn) {
				if (btn == 'yes') {
					saveOrderInc(action);
				}
			});	
	}
	
	function reloadOrder(orderId) {
		var diva = Ext.get('OrderDetailDiv');
		diva.load( {
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {'formAction' :'<%=OrderDetailAction.FORWARD_RELOAD_ORDER%>' ,
					  'reloadOrderId' :	orderId},
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
				
	}

	function setRailHoldBackReOrderFlag(el) {
		this.value=el.checked;
		Ext.MessageBox.confirm('<fmt:message key="orderdetail.messagebox.confirm"/>',
				<c:choose>
					<c:when test="${form.orderBean.railHoldBackReOrderFlag}">				
						'<fmt:message key="orderdetail.messagebox.setholdbackoff.repair.order.text"/>',
					</c:when>
					<c:otherwise>
						'<fmt:message key="orderdetail.messagebox.setholdbackon.repair.order.text"/>',
					</c:otherwise>
				</c:choose> 
						function(btn) {
							if (btn == 'yes') {
								var diva = Ext.get('OrderDetailDiv');
								diva.load( {
									url :'<c:url value="${orderDetailUrl}"/>',
									params : {'formAction' :'<%=OrderDetailAction.FORM_ACTION_HOLD_BACK_RETURN_ORDER%>',
										'orderBean.railHoldBackReOrderFlag':el.checked},
											text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
											scripts :true
										});
							}
							else {
								el.checked = !el.checked;
							}
						});	
	}
	
	// Load vehicle wrappers for vehicle type combobox
	var vehicleTypeWrappers = <c:out value="${form.allowedVehicleTypesJSON}" escapeXml="false"/>;
	var enabledTruckTypes = <c:out value="${form.truckTypeCodesEnabledInOrderJSON}" escapeXml="false"/>;
	
	var initializeOrderDetailWidgets = function() {
		
		//console.log("Initializing order detail widgets");
		
		// Helper function used by the vehicle comboBox that
		// sets the checkmarks of all those elements whose truck
		// type code appears in the specified list
		function setEnabledVehicleTypes(typeCodes)
		{
			for (var i = 0; i < typeCodes.length; i++)
	    	{
	    		var truckTypeCd = typeCodes[i];
	    		var posInComboBox = this.store.find(this.valueField, new RegExp('^' + truckTypeCd + '$'));
	    		if (posInComboBox > -1) {
	    			this.setChecked(truckTypeCd, true);
	    		}
	    	}
		}
		
		var widgets = {
			allowedVehiclesCombo: new Ext.ux.TableStyleCheckComboBox({
				transform: 'allowedVehiclesCombo',
				store: new Ext.data.JsonStore({
					fields: [
						{name: 'truckTypeCd', mapping: 'typeCd'},
						{name: 'priority', mapping: 'priority'},
						{name: 'truckTypeName', mapping: 'typeName'},
						{name: 'maxGrossWeightT', mapping: 'maxGrossWeightT'}
					],
					data: vehicleTypeWrappers
				}),
				displayField: 'truckTypeName',
				checkedField: 'checked',
				valueField: 'truckTypeCd',
				additionalColumns: ['priority', 'maxGrossWeightT'],
				checkboxColumnTitle:
					'<fmt:message key="orderdetail.main.field.allowed.vehicles.type"/>',
				additionalColumnTitles: [
					'<fmt:message key="orderdetail.main.field.allowed.vehicles.prio"/>',
					'<fmt:message key="orderdetail.main.field.allowed.vehicles.maxgrossweight"/>'
				],
				mode: 'local',
				triggerAction: 'all',
				emptyText:'all',
				selectOnFocus: true,
				width: 135,
				listWidth: 350,
				maxHeight: 400, // LOGON-5682: Usability of truck type list
				checkboxColumnWidth: 150,
				additionalColumnWidths: [50, 150],
				disabled: <c:out value="${isAssigned || isRailOrder}"/>,
				// TODO (marius/2012-10-16): When should this be editable? also, this property
				// doesn't seem to have an effect
				editable: false,
				setEnabledVehicleTypes: setEnabledVehicleTypes,
				listeners: {
					change: function(combobox) {
						combobox.updateSelectedVehicles();
					}
				},
				getSelectedTruckTypes: function() {
					return this.getCheckedValues() ? this.getCheckedValues().join(',') : '';
				},
				// Update the hidden input element "selectedVehicles" with the current combobox
				// selection. Needed for saving.
				updateSelectedVehicles: function() {
					var selectedOnes = this.getSelectedTruckTypes();
					Ext.getDom("selectedVehicles").value = selectedOnes;
				}
			})
		};
		
		// Update checkboxes of vehicle combobox based on selections of the order
		widgets.allowedVehiclesCombo.setEnabledVehicleTypes(enabledTruckTypes);
		widgets.allowedVehiclesCombo.updateSelectedVehicles();
		
	};
	
	onPopupReady(function() {
		// TODO 19-Oct-2012/romek: There must be a better way to avoid the race condition!
		if (!Ext.ux.TableStyleCheckComboBox) {
			customEventHelper.one(document, 'tableStyleCheckComboBoxLoaded', function() {
				initializeOrderDetailWidgets();
			});
		}
		else {
			initializeOrderDetailWidgets();
		}
	});

	var orderDetailDistanceValidationPopUpText = "<c:out value='${form.distanceValidationPopUpText}' escapeXml='false'/>";
	function orderDetailDistanceRulesPopUp(){

		Ext.MessageBox.alert('<fmt:message key="application.errors.custordervalidation.distance.rules.popup.TITLE" />',orderDetailDistanceValidationPopUpText);
	}


	</script>



<div id="OrderDetailDiv" class="customer_max" style="width: 1000px; padding: 0;" ><html:form
	action="auftrag/OrderDetail.do" styleId="OrderDetailForm"  method="post" enctype="multipart/form-data"
	style="margin-top: 0px;">
	<%-- <html:hidden property="formAction" value="" />--%>
	<input type="hidden" name="selectedOrderItemId" id="selectedOrderItemId" property="selectedOrderItemId" value="" />
	
	<!-- Begin table 2 rows -->
	<table class="big_form2" style="width: 100%; border-spacing: 2px;">
		<colgroup>
			<col width="30%">
			<col width="70%">
		</colgroup>
		<tr>
			<th colspan="2" class="classicblue"><div id="pageTitleDiv">
				<div style="font-size: 14px;"><c:out
				value="${form.title}" />&nbsp;&nbsp;&nbsp;<c:out
				value="${form.orderBean.orderStatus}" />
				</div></div>
				</th>
		</tr>
		<tr>
			<td style="width: 30%;"><!-- Begin Linke Spalte Inhalt -->
			<div id="mainSectionId">

			<div class="subform"><span class="labelblue"><fmt:message
				key="orderdetail.main.label.period" /></span><br>
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
				<colgroup>
					<col width="30%">
					<col width="70%">
				</colgroup>
				<tbody>
					<tr>
						<td valign="bottom"
							style="white-space: nowrap; border: 0px; padding-left: 0px;">
						<p class="formtitle"><fmt:message
							key="orderdetail.main.field.orderby" /></p>
						</td>
						<td><span class="textfielddisabled_h" style="width: 190px;"
							title="<c:out value="${form.orderBean.contactPerson}" />">
						<c:out
							value="${form.orderBean.contactPerson}" />&nbsp;
						</span></td>
					</tr>
					<tr>
						<td valign="bottom"
							style="white-space: nowrap; border: 0px; padding-left: 0px;">
						<p class="formtitle"><fmt:message
							key="orderdetail.main.field.createdby" /></p>
						</td>
						<td><span class="textfielddisabled_h" style="width: 190px;"
							title="<c:out value="${form.orderBean.createdByPerson}" />"><c:out
							value="${form.orderBean.createdByPerson}" />&nbsp;</span>
						</td>
					</tr>
					<tr>
					<tr>
						<td valign="bottom"
							style="white-space: nowrap; border: 0px; padding-left: 0px;">
						</td>
						<td>
						<div class="textfielddisabled_h" style="float: left; width: 82px;">
						<c:out
							value="${form.orderBean.createdByDate}" />&nbsp;
						</div>
						<div class="textfielddisabled_h"
							style="float: left; margin-left: 4px; width: 40px;"><c:out
							value="${form.orderBean.createdByTime}" />&nbsp;
						</div>
						</td>
					</tr>
					<c:if test="${(((order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_ROAD_DIRECT) || 
									(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_ROAD)) &&
									(!isTransmittedToDriver)) ||
								 (((order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_RAIL_DIRECT) || 
									(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL)) &&
									(!isTransmittedToRailFlag))}">
					<td valign="bottom"
							style="white-space: nowrap; border: 0px; padding-left: 0px;">
						<p class="formtitle"><fmt:message
							key="orderdetail.main.field.onhold" /></p>
						</td>
						<td><html:checkbox property="orderBean.onHoldOrderFlag" />
						</td>
					</tr>					
					</c:if>
					<tr>
						<td valign="bottom"
							style="white-space: nowrap; border: 0px; padding-left: 0px;">
						<p class="formtitle"><fmt:message
							key="orderdetail.main.field.created.timestamp" /></p>
							<c:if test="${displayShipToTimezone}">
								<c:out value="${shipToTimeZoneFmtOffset}"/>
							</c:if>
						</td>
						<td>
						<div style="float: left; width: 83px;"><html:text
							styleId="deliveryFromDate" property="orderBean.delivDateFrom" />
						<html:text styleId="orderDetailDeliveryFromTime"
							property="orderBean.delivTimeFrom" /></div>
						<div
							style="float: left; margin-left: 4px; vertical-align: middle;">-</div>
						<div style="float: left; margin-left: 4px; width: 83px;"><html:text
							styleId="deliveryToDate" property="orderBean.delivDateTo" /> <html:text
							styleId="orderDetailDeliveryToTime"
							property="orderBean.delivTimeTo" /></div>
						</td>
					</tr>
				<tr>
					<td colspan="2">
						<input title="<fmt:message key="application.errors.custordervalidation.distance.rules.popup.TITLE" />" type="button"
								name="validationRules" id="validationRules"
								value="<fmt:message key="application.errors.custordervalidation.distance.rules.popup.TITLE" />"
								<c:if test="${isDelivTimeDisabled}"> disabled </c:if>
								class="ViewButtonStyle" onClick="orderDetailDistanceRulesPopUp();">
					</td>
				</tr>
				</tbody>
			</table>

			</div>

			<br>
	
			<!-- Contract panel -->
			<div class="subform">
				<span class="labelblue"><fmt:message key="orderdetail.main.label.contract" /></span>
				<br>
				<table border="0" cellspacing="0" cellpadding="0" width="97%">
					<colgroup>
						<col width="30%">
						<col width="70%">
					</colgroup>
					<tbody>
						<tr>
							<td valign="bottom" class="formtitle" style="white-space: nowrap; border: 0px;">
								<fmt:message key="orderdetail.main.field.contract" />
							</td>
							<td valign="bottom" class="formtitle" style="white-space: nowrap; border: 0px;">
								<c:if test="${order.sapContract != null}">
									<c:out value="${order.sapContract.sapContractNo}"/>
								</c:if>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

		<br>

		<div class="subform"><!-- Begin Content Versand --> <span
			class="labelblue"><fmt:message
			key="orderdetail.main.label.shipping" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="98%">

			<colgroup>
				<col width="30%">
				<col width="10%">
				<col width="60%">
			</colgroup>
			<tbody>
				<tr>
					<td valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><fmt:message
						key="orderdetail.main.field.delivery.type" />
						</td>
					<td colspan="2" valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><html:select
						styleClass="textfield" size="1" style="width: 150px;"
						property="orderBean.deliveryTypeCd" disabled="true">
						<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
						<c:forEach var="entity" varStatus="status"
							items="${form.deliveryTypeList}">
							<html:option
								value="${entity.deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd}">
								<c:out value="${entity.txt}" />
							</html:option>
						</c:forEach>
					</html:select>
						<c:out value="${form.orderBean.distributionDestCd}"/>						
					</td>
				</tr>
				<tr>
					<td valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;">
						<fmt:message key="orderdetail.main.field.SP.indicator" />
						</td>
					<td colspan="2" valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><html:select
						styleClass="textfield" size="1" style="width: 150px;"
						disabled="${isSPIndicatorDisabled}"
						property="orderBean.spiCd">
						<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
						<c:forEach var="entity" varStatus="status"
							items="${form.spiList}">
							<html:option
								value="${entity.orderSpiTxPK.orderSpiBs.orderSpiCd}">
								<c:out value="${entity.txt}" />
							</html:option>
						</c:forEach>
					</html:select>					
					</td>
				</tr>
				<tr>
					<td valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><fmt:message
						key="orderdetail.main.field.plant" /></td>
					<td valign="bottom" style="border: 0px;"><html:text
						styleClass="textfield" style="width:45px;"
						property="orderBean.plantCd" disabled="true" styleId="plantCodeId" /></td>
					<td valign="bottom" style="border: 0px;">
					<div id="deliveryPlantDiv" style="border: 0px; padding: 0px; width: 100%;">
						<jsp:include page="orderdetail/DeliveryPlant.jsp"></jsp:include>
					</div>
					</td>
				</tr>

				<tr>
					<td valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><fmt:message
						key="orderdetail.main.field.shpoint" /></td>
					<td valign="bottom"><html:text styleClass="textfield"
						style="width:45px;" property="orderBean.shPointNo" disabled="true"
						styleId="shPointNoId" /></td>
					<td valign="bottom"><nobr>
					<div id="shPointDiv"
						style="border: 0px; padding: 0px; width: 100%;"><jsp:include
						page="orderdetail/ShPoint.jsp"></jsp:include></div>
					</nobr></td>
				</tr>
				<tr>
					<td colspan="2" valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><fmt:message
						key="orderdetail.main.field.allowed.vehicles" /></td>
					<td valign="bottom"><nobr>
					<%--
					<input type="text" id="DeliveringPlantsField" style="width: 100px; color: black">
					<div class="border" id="DivDeliveringPlantsEdit"
					     style="z-index: 40; background: white; width: 200px; position: absolute; visibility: hidden">
					</div>
					--%>
					<%-- Check combo box of allowed vehicle types --%>
					<html:text property="allowedVehiclesCombo" styleId="allowedVehiclesCombo"/>
					<html:hidden property="orderBean.selectedVehicles" styleId="selectedVehicles"/>
					</nobr></td>
				</tr>
			</tbody>
		</table>
		</div>

		<br>
		
		<div class="subform"><!-- Begin Content Spediteur --> <span
			class="labelblue"><fmt:message
			key="orderdetail.main.label.carrier" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="97%">
			<colgroup>
				<col width="30%">
				<col width="1%">
				<col width="29%">
				<col width="40%">
			</colgroup>
			<tbody>
				<tr>
					<td valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><fmt:message
						key="orderdetail.main.field.pool" /></td>
					<td valign="bottom">
					<div id="autoPoolAssignmentFlagDiv">
						<c:set var="forwardToPool"><fmt:message key="orderdetail.main.field.forward.pool" /></c:set>
					 	<html:checkbox property="orderBean.autoPoolAssignmentFlag" title="${forwardToPool}"
							disabled="${isSpecialDetailDisabled || isForwardToPoolDisabled}" />
						</div>
					</td>
					<td colspan="2" valign="bottom">
					<html:select
						styleClass="textfield" size="1" style="width: 100%;"
						property="orderBean.poolId" disabled="${isDispatched || isSpecialDetailDisabled}"
						styleId="poolId" onchange="javascript:changePool(this.options[this.selectedIndex].value)">
						<c:set var="contains">false</c:set>
						<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
						<c:forEach var="entity" varStatus="status"
							items="${form.poolList}">
							<c:if test="${entity.poolTxPK.poolId == form.orderBean.poolId}">
								<c:set var="contains">true</c:set>
							</c:if>
							<html:option value="${entity.poolTxPK.poolId}">
								<c:out value="${entity.name}" />
							</html:option>
						</c:forEach>
						<%-- Removed with LOGON-5399
						<c:if test="${contains == 'false'}">
							<html:option value="${form.orderBean.poolId}">
								<c:out value="${form.orderBean.poolName}" />
							</html:option>
						</c:if>
						 --%>
					</html:select></td>
				</tr>
  
				<tr>
					<td valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><fmt:message
						key="orderdetail.main.field.commcarrier" /></td>
					<td valign="bottom">
					<div id="autoCommCarrierAssignmentFlagDiv">
						<c:set var="forwardToCommCarrier"><fmt:message key="orderdetail.main.field.forward.commcarrier" /></c:set>
						<html:checkbox property="orderBean.autoCommCarrierAssignmentFlag" title="${forwardToCommCarrier}" 
							disabled="${isSpecialDetailDisabled || isForwardToCommCarrierDisabled}" />
					</div>
					</td>
					<td valign="bottom">
					<html:text styleClass="textfield"
						style="width:100%;" property="orderBean.commCarrierVendorSapId"
						disabled="true" styleId="commCarrierVendorSapId" /></td>
					<td valign="bottom"><nobr>
					<div id="commCarrierDiv"
						style="border: 0px; padding: 0px; width: 100%;"><jsp:include
						page="orderdetail/CommCarrier.jsp"></jsp:include></div>
					</nobr></td>
				</tr>
				<tr>
					<td valign="bottom" class="formtitle"
						style="white-space: nowrap; border: 0px;"><fmt:message
						key="orderdetail.main.field.execcarrier" /></td>
					<td valign="bottom">
						&nbsp;
					</td>
					<td valign="bottom">
					<html:text styleClass="textfield"
						style="width:100%;" property="orderBean.execCarrierVendorSapId"
						disabled="true" styleId="execCarrierVendorSapId" /></td>
					<td valign="bottom"><nobr>
					<div id="execCarrierDiv"
						style="border: 0px; padding: 0px; width: 100%;"><jsp:include
						page="orderdetail/ExecCarrier.jsp"></jsp:include></div>
					</nobr></td>
				</tr>				
			</tbody>
		</table>
		</div>
		<br>

		<c:if test="${not form.orderBean.showPickupInfo}">
			<div class="subform"><!-- Begin Transportmittel --> <span
				class="labelblue"><fmt:message
				key="orderdetail.main.label.transportation" /><br>
			</span>
				<table border="0" cellspacing="0" cellpadding="0" width="97%">
					<colgroup>
						<col width="30%">
						<col width="30%">
						<col width="40%">
					</colgroup>
					<tbody>
						<tr>
							<td valign="bottom" class="formtitle"
								style="white-space: nowrap; border: 0px;"><fmt:message
								key="orderdetail.main.field.truck" /></td>
	
							<c:if
								test="${not empty form.orderBean.truckId}">
								<td><span class="textfielddisabled_h" style="width: 92px;"
									title="<c:out
							value="${form.orderBean.plateNo}" />">
								<c:out value="${form.orderBean.plateNo}" />&nbsp;
								</span></td>
								<td><span class="textfielddisabled_h" style="width: 92px;"
									title="<c:out
							value="${form.orderBean.truckName}" />">
								<c:out
									value="${form.orderBean.truckName}" />&nbsp;
								</span></td>
							</c:if>
							<c:if
								test="${empty form.orderBean.truckId}">
								<td colspan="2"><span class="textfielddisabled_h"
									style="width: 190px;"
									title="<c:out
							value="${form.orderBean.plateNo}" />">
								<c:out value="${form.orderBean.plateNo}" />&nbsp;
								</span></td>
							</c:if>
						</tr>
						<tr>
							<td valign="bottom" class="formtitle"
								style="white-space: nowrap; border: 0px;"><fmt:message
								key="orderdetail.main.field.transport" /></td>
							<td valign="bottom"><span class="textfielddisabled_h"
								style="width: 92px;"
								title="<c:out
							value="${form.orderBean.trspCategory}" />">
							<c:out
								value="${form.orderBean.trspCategory}" />&nbsp;
							</span></td>
							<td valign="bottom"><span class="textfielddisabled_h"
								style="width: 92px;"
								title="<c:out
							value="${form.orderBean.trspType}" />">
							<c:out value="${form.orderBean.trspType}" />&nbsp;
							</span></td>
						</tr>
						<tr>
							<td valign="bottom" class="formtitle"
								style="white-space: nowrap; border: 0px;"><fmt:message
								key="orderdetail.main.field.driver" /></td>
							<td colspan="2"><span class="textfielddisabled_h"
								style="width: 190px;"
								title="<c:out
							value="${form.orderBean.driverName}" />">
							<c:out
								value="${form.orderBean.driverName}" />&nbsp;
							</span></td>
						</tr>
					</tbody>
				</table>
			</div>
		</c:if>
		<c:if test="${form.orderBean.showPickupInfo}">
			<div class="subform"><!-- Begin Transportmittel --> <span
				class="labelblue"><fmt:message
				key="orderdetail.main.label.transportation" /><br>
			</span>
				<table border="0" cellspacing="0" cellpadding="0" width="97%">
					<colgroup>
						<col width="30%">
						<col width="30%">
						<col width="40%">
					</colgroup>
					<tbody>
						<tr>
							<td valign="bottom" class="formtitle"
								style="white-space: nowrap; border: 0px;"><fmt:message
								key="orderdetail.main.field.ws_truck_plate_no" /></td>
	
							<td colspan="2"><span class="textfielddisabled_h"
								style="width: 180px;"
								title="<c:out
							value="${form.orderBean.wsTruckPlateNo}" />">
							<c:out
								value="${form.orderBean.wsTruckPlateNo}" />&nbsp;
							</span></td>
						</tr>
						<tr>
							<td valign="bottom" class="formtitle"
								style="white-space: nowrap; border: 0px;"><fmt:message
								key="orderdetail.main.field.ws_trailer_plate_no" /></td>
							<td colspan="2"><span class="textfielddisabled_h"
								style="width: 180px;"
								title="<c:out
							value="${form.orderBean.wsTrailerPlateNo}" />">
							<c:out
								value="${form.orderBean.wsTrailerPlateNo}" />&nbsp;
							</span></td>
						</tr>
						<tr>
							<td valign="bottom" class="formtitle"
								style="white-space: nowrap; border: 0px;"><fmt:message
								key="orderdetail.main.field.ws_carrier" /></td>
							<td colspan="2"><span class="textfielddisabled_h"
								style="width: 180px;"
								title="<c:out
							value="${form.orderBean.wsCarrier}" />">
							<c:out
								value="${form.orderBean.wsCarrier}" />&nbsp;
							</span></td>
						</tr>
					</tbody>
				</table>
			</div>
		</c:if>


		<br>
		<div class="subform" id="orderStatusListDiv"><span class="labelblue"><fmt:message
			key="orderdetail.main.label.order.status" /> </span><br>
		<textarea name="orderStatusList" cols="80" class="textfield"
			style="height:85px;width: 99%"><c:forEach var="entity"
			varStatus="status"
			items="${form.orderBean.orderStatusList}"><c:out value="${entity}" />
</c:forEach></textarea></div>

			</div>
		</td>
		<!-- Beginn subtab-Table -->
		<td style="width: 70%;">
		<table cellpadding="0" cellspacing="0" class="compact"
			style="border: 0px; width:98%; margin: 0px; padding: 0px; background-color: #FFFFFF;">
			<tr>
				<td
					style="border: 0px; padding: 0px; margin: 0px; background-color: #FFFFFF;">
				<div class="subnaviborder">
				<ul id="subnavigation2">
					<crh-common:authorize
						function="<%=Authorization.ORDER_DETAIL_MATERIAL_R%>">
						<li><a id="<%=OrderDetailAction.FORWARD_MATERIAL_TAB%>"
							href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_MATERIAL_TAB%>')"
							class="highlight"><span class="outer" style="white-space: nowrap;"><span
							class="inner"><fmt:message key="orderdetail.tab.material" /></span></span></a></li>
					</crh-common:authorize>
					<crh-common:authorize
						function="<%=Authorization.ORDER_DETAIL_S_EC_MOD_BEFORE_SAP%>">
						<li><a id="<%=OrderDetailAction.FORWARD_SERVICES_EXTRA_COST_TAB%>"
							href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_SERVICES_EXTRA_COST_TAB%>')"><span
							class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.services.extra.cost" /></span></span></a></li>
					</crh-common:authorize>
					<c:if test="${order.custorder != null}">
						<crh-common:authorize
							function="<%=Authorization.ORDER_DETAIL_MATERIAL_R%>">
							<li><a id="<%=OrderDetailAction.FORWARD_ORDER_CUSTORDER_TAB%>"
								href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_ORDER_CUSTORDER_TAB%>')"><span
								class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.order.custorder" /></span></span></a></li>
						</crh-common:authorize>
					</c:if>
					<crh-common:authorize
						function="<%=Authorization.ORDER_DETAIL_MAP%>">
						<li><a id="<%=OrderDetailAction.FORWARD_MAP_TAB%>"
							href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_MAP_TAB%>')"><span
							class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.map" /></span></span></a></li>
					</crh-common:authorize>
					<crh-common:authorize
						function="<%=Authorization.ORDER_DETAIL_REPORT_R%>">
						<li><a id="<%=OrderDetailAction.FORWARD_REPORTING_TAB%>"
							href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_REPORTING_TAB%>')"><span
							class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.reporting" /></span></span></a></li>
					</crh-common:authorize>
					<crh-common:authorize
						function="<%=Authorization.ORDER_DETAIL_ORDER_MANAGEMENT%>">
						<li><a id="<%=OrderDetailAction.FORWARD_MNG_TAB%>"
							href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_MNG_TAB%>')"><span
							class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.management" /></span></span></a></li>
					</crh-common:authorize>
					<crh-common:authorize
						function="<%=Authorization.ORDER_DETAIL_ORDER_LOG %>">
						<li><a id="<%=OrderDetailAction.FORWARD_ORDER_LOG_TAB%>"
							href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_ORDER_LOG_TAB%>')"><span
							class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.order.log" /></span></span></a></li>
					</crh-common:authorize>
					<c:if test="${(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_ROAD_DIRECT) || 
									(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_ROAD)}">
						<crh-common:authorize
							function="<%=Authorization.ORDER_DETAIL_TRUCK_LOG%>">
							<li><a id="<%=OrderDetailAction.FORWARD_TRUCK_LOG_TAB%>"
								href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_TRUCK_LOG_TAB%>')"><span
								class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.truck.log" /></span></span></a></li>
						</crh-common:authorize>
					</c:if>
					<c:if test="${(order.distributionDest.distributionDestCd == DISTRIBUTION_DEST_REPAIR)}">
						<crh-common:authorize
							function="<%=Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT %>">
						<li><a id="<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>"
								href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>')"><span
								class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.rep.maint" /></span></span></a></li>	
						</crh-common:authorize>
					</c:if>
					<crh-common:authorize
						function="<%=Authorization.ORDER_DETAIL_W%>">
						<li><a id="<%=OrderDetailAction.FORWARD_THIRD_PARTY_FREIGHTERS_TAB%>"
							href="javascript:setCurrentTab('<%=OrderDetailAction.FORWARD_THIRD_PARTY_FREIGHTERS_TAB%>')"><span
							class="outer" style="white-space: nowrap;"><span class="inner"><fmt:message key="orderdetail.tab.third.party.freighters" /></span></span></a></li>
					</crh-common:authorize>
				</ul>
				</div>
				</td>
			</tr>
		</table>
	<%-- For most of the people the material tab is visible. For the moment the only screen is the rep tab which needs to be shown by default if material tab is not available--%>
	<%-- Sequence matters! Default must be the last.--%>
	<crh-common:authorize function="<%= Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT %>">
		<c:set var="authorized">REP_TAB</c:set>
	</crh-common:authorize>
	<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_R %>">
		<c:set var="authorized">MATERIAL_TAB</c:set>
	</crh-common:authorize>
	
		<div id="TabsDiv" class="subform"
			style="overflow-y:auto;overflow-x:hidden;border-top: 0px; padding: 0px; height:650px;width: 670px;">
			<c:choose>
				<c:when test="${authorized eq 'REP_TAB'}">
				<crh-common:authorize function="<%= Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT %>">
				<script language="text/javascript">
						onPopupReady(function() {setCurrentTab('<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>')});
				</script>
				</crh-common:authorize>
				</c:when>
				<c:otherwise>
				<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_R %>">
					<jsp:include page="orderdetail/MaterialTab.jsp"></jsp:include>
				</crh-common:authorize>
				</c:otherwise>
			</c:choose>
			</div>
		</td>
		</tr>
		<tr>
			<td colspan="2">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="buttons left" width="60%">
					<c:set var="ORDER_DETAIL_DELETE_SPECIAL_DISABLED">true</c:set>
					<c:if test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED || order.orderStatus.orderStatusCd == ORDER_STATUS_DELIVERED)}" >
					<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_DELETE_SPECIAL%>">
						<c:set var="ORDER_DETAIL_DELETE_SPECIAL_DISABLED">false</c:set>
					</crh-common:authorize>
					</c:if>
					
					<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_W%>">
						<input type="button" 
							value="<fmt:message key="orderdetail.main.button.delete"/>"
							onClick="deleteOrder();"
						<crh-common:evaluateIfDisabled orderObjName="order" buttonFunc ="<%=OrderDetailButtonLogicTag.DELETE_ORDER %>" >
							disabled
							class="ViewButtonStyleDisabled"
							<c:if test="${((order.orderStatus.orderStatusCd > ORDER_STATUS_DISPATCHED) 
								&& (order.orderStatus.orderStatusCd < ORDER_STATUS_DELIVERED)) && ORDER_DETAIL_DELETE_SPECIAL_DISABLED }" >
							title='<fmt:message key="orderdetail.message.delete.dispatched" />'
							</c:if>
							<c:if test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_DELIVERED || order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED ) && ORDER_DETAIL_DELETE_SPECIAL_DISABLED}" >
							title='<fmt:message key="orderdetail.message.delete.delivered" />'
							</c:if>
							<c:if test="${order.orderStatus.orderStatusCd == ORDER_STATUS_DELETED}" >
							title='<fmt:message key="orderdetail.message.delete.deleted" />'
							</c:if>
						</crh-common:evaluateIfDisabled>
							class="ViewButtonStyle"
						/>
						
					</crh-common:authorize>
					<crh-common:authorize function="<%= Authorization.ORDER_DELIVERED_SEND_TO_SAP%>">
						<c:if test="${order.orderStatus.orderStatusCd == ORDER_STATUS_DELIVERED}" >
							<input type="button" value="<fmt:message key="orderdetail.main.button.sendToSap"/>"
							<c:if test="${order.forceToSapFlag == true}" >
								disabled class="ViewButtonStyleDisabled"
							</c:if>
							<c:if test="${order.forceToSapFlag != true}" >
								class="ViewButtonStyle"  onClick="sendToSAP()"
							</c:if>
							 />
						</c:if>
						
					</crh-common:authorize>
					<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN%>">
					<c:if test="${(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_RAIL_DIRECT) || 
					(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL)}">
						<input type="button" class="ViewButtonStyle"
					<crh-common:evaluateIfDisabled orderObjName="order" buttonFunc ="<%=OrderDetailButtonLogicTag.REPORT_WAGON_EMPTY %>" >
						disabled
						class="ViewButtonStyleDisabled"						
					</crh-common:evaluateIfDisabled>
							class="ViewButtonStyle"					
						 onClick="javascript:reportWagonEmpty();"
							value="<fmt:message key="orderdetail.main.button.setdelivered"/>">
					</c:if>
					</crh-common:authorize>
					<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_W%>">
					<c:if test="${(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_RAIL_DIRECT) || 
					(order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL)}">
										
							<span title="<fmt:message key="wagondetail.checkbox.holdbackreflag.title"/>">
								<input type="checkbox" onclick="javascript:setRailHoldBackReOrderFlag(this)" <c:if test="${form.orderBean.railHoldBackReOrderFlag}">checked</c:if>
							<crh-common:evaluateIfDisabled orderObjName="order"  buttonFunc ="<%=OrderDetailButtonLogicTag.SET_HOLD_BACK_RE_ORDER_FLAG %>" >
								disabled								
							</crh-common:evaluateIfDisabled>
								/>
							</span>
					</c:if>
					</crh-common:authorize>
					<crh-common:authorize function="<%= Authorization.DISPATCHING_W%>">
						<c:if test="${order.orderStatus.orderStatusCd == ORDER_STATUS_DELIVERED}">
							<c:if test="${order.itrackStatus.itrackStatusCd == 2}" >
								<input type="button" value="<fmt:message key="orderdetail.main.button.checkUnload"/>"
								class="ViewButtonStyle"  onClick="confirmUnloadLocationAction()"/>
							</c:if>
						</c:if>
					</crh-common:authorize>
					</td>
					<td>
					<div id="saveDiv"></div>
					</td>
					<td class="buttons right"><nobr> <input type="button"
						class="ViewButtonStyle"
						value="<fmt:message key="orderdetail.main.button.cancel"/>"
						onClick="closePopup()">
<c:set var='canSave' value='false'/>
<crh-common:authorize
	function="<%= Authorization.ORDER_DETAIL_W  %>"
	function2="<%= Authorization.ORDER_DETAIL_DELIVERY_DATE_TIME %>"
	function3="<%= Authorization.ORDER_DETAIL_MATERIAL_W %>"
	function4="<%= Authorization.ORDER_DETAIL_MATERIAL_QUANTITY_UOM %>"
	function5="<%= Authorization.ORDER_DETAIL_MATERIAL_BILLTO_PARTY %>"
	function6="<%= Authorization.ORDER_DETAIL_MATERIAL_PAYER_PARTY %>"
	function7="<%= Authorization.ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO %>"
	function8="<%= Authorization.ORDER_DETAIL_MATERIAL_FORWARDING_AGENT %>"
	function9="<%= Authorization.ORDER_DETAIL_MATERIAL_DELIVERY_NOTE %>"
	function10="<%= Authorization.ORDER_DETAIL_REPORT_W %>"
	function11="<%= Authorization.ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG %>"
	function12="<%= Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CARR %>"
	function13="<%= Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CRH %>"
	function14="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_DRIVER %>"
	function15="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_DISPATCHING %>"
	function16="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_SHIPPING %>"
	function17="<%= Authorization.ORDER_DETAIL_ORDER_MANAGEMENT %>"
	function18="<%= Authorization.ORDER_DETAIL_UPDATE_BP_SPECIAL%>"
	function19="<%= Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT%>"
	>
	
		<c:set var='canSave' value='true'/>
</crh-common:authorize>
<c:if test="${(order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED)}" >
	<c:set var='canSave' value='false'/>
	<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_UPDATE_BP_SPECIAL%>">
		<c:set var='canSave' value='true'/>
	</crh-common:authorize>
</c:if>
<c:if test="${canSave}">
						<input type="button" class="ViewButtonStyle" onClick="saveOrder('<%=OrderDetailAction.FORWARD_SAVE_ORDER%>')"
							value="<fmt:message key="orderdetail.main.button.save"/>">
						<input type="button" class="ViewButtonStyle" onClick="saveOrder('<%=OrderDetailAction.FORWARD_SAVE_CLOSE_ORDER%>')"
							value="<fmt:message key="orderdetail.main.button.save.close"/>">
</c:if>
						</nobr></td>
				</tr>
			</table>

			</td>
		</tr>
	</table>
</html:form>
</div>

<script type="text/javascript">

	initDelivFromTo();
	
	sortComboByName("orderBean.commCarrierCombo");
	sortComboByName("orderBean.execCarrierCombo");
	sortComboByName("orderBean.loadingPointCombo");	
	
	Ext.get('logonPopup').center();
	Ext.get('logonPopup').show();
	
</script>

<div id="DivAllowedVehicleTypesEdit" style="display: none; visibility: hidden"></div>

<%-- END $RCSfile: OrderDetailTile.jsp,v $ --%>
