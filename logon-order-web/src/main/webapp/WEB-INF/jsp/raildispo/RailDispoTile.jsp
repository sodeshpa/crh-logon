<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RailDispoTile.jsp,v $ --%>

<%@ page session="true"
	import="com.crh.logon.web.utils.Authorization"
%>
<%@page import="com.crh.logon.order.web.actions.raildispo.DispoAction"%>
<%@page import="com.crh.logon.order.web.actions.raildispo.RailDispoExportPopupAction"%>

<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<%@page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.utils.time.DateTimePatterns" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<script type="text/javascript" src="<crh-common:url value="/js/logonPackageHandlingHelper.js"/>"></script>
<script type="text/javascript">

<%-- Initialize the helper with the constants set from here ( we did NOT hardcode these in the .js itself) --%>
logonPackageHandlingHelper.init('<%=Constant.PACKAGE_TYPE_BULK_POWDER%>', '<%=Constant.PACKAGE_TYPE_BULK_DUMPED%>', '<%=Constant.PACKAGE_TYPE_BBAG%>', '<%=Constant.PACKAGE_TYPE_SERVICE%>');

var SPECIAL_ORDER_DIST_CD_LIST = [
"<%= StringUtils.join(Constant.DISTRIBUTION_DEST_SPECIAL, "\",\"") %>" 
];

var actions = {
		ACTION_SAVE_PREFERENCES: '<%=DispoAction.ACTION_SAVE_PREFERENCES%>',
		ACTION_RESET_PREFERENCES:  '<%=DispoAction.ACTION_RESET_PREFERENCES%>',
		ACTION_FILTER_RAILDISPO:  '<%=DispoAction.ACTION_FILTER_RAIL_DISPOLIST%>',
		ACTION_RESET_FILTER_RAILDISPO:  '<%=DispoAction.ACTION_RESET_FILTER_RAIL_DISPOLIST%>'
	};

function disableShiftClick(e) {
	if (!e) {
		e = window.event;
	}
	if (e && (e.shiftKey || e.ctrlKey)) {
		if (e.preventDefault) {
			e.preventDefault()
		}
		return false;
	}
}

Ext.getDoc().on('mousedown', disableShiftClick);

document.onselectstart = disableShiftClick;

var authorderdetail = false;
var authrail = false;
var authrailspecial = false;

<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_R %>">
	authorderdetail = true;
</crh-common:authorize>

<crh-common:authorize function="<%= Authorization.RAIL_DISPATCHING_W %>">
	authrail = true;
</crh-common:authorize>

<crh-common:authorize function="<%= Authorization.RAIL_DISPATCHING_SW %>">
	authrailspecial = true;
</crh-common:authorize>

var plantEtranMap = {};
<c:forEach var="plant" items="${order_raildispo_DispoForm.plants}">
plantEtranMap['<c:out value="${plant.plantCd}"/>'] = <c:out value="${plant.etranFlag != null && plant.etranFlag}"/>;
</c:forEach>

function isEtranEnabled() {
	var selectedPlantCd = Ext.get('railplantselect').getValue();
	return plantEtranMap[selectedPlantCd];
}

var errortexts = {
	NoPlan: '<fmt:message key="raildispo.errors.noplan"/>',
	CantArrive: '<fmt:message key="raildispo.errors.cantarrive"/>',
	NotTransmitted: '<fmt:message key="raildispo.errors.nottrasmitted"/>',
	NotLeft: '<fmt:message key="raildispo.errors.notleft"/>',
	NotArrived: '<fmt:message key="raildispo.errors.notarrived"/>',
	MaterialMismatch: '<fmt:message key="raildispo.errors.materialmismatch"/>',
	WeightTransmitted: '<fmt:message key="raildispo.errors.weightTransmitted"/>',
	OrderTransmitted: '<fmt:message key="raildispo.errors.orderTransmitted"/>',
	MatCompatMismatch: '<fmt:message key="raildispo.errors.matCompatMismatch"/>',
	EtranValidation: '<fmt:message key="raildispo.errors.etran.validation"/>'
};

var texts =	{
	Nr: '<fmt:message key="raildispo.texte.Nr"/>',
	State: '<fmt:message key="raildispo.texte.State"/>',
	Product:'<fmt:message key="raildispo.texte.Product"/>',
	OrderedQuantity:'<fmt:message key="raildispo.texte.OrderedQuantity"/>',
	Customer:'<fmt:message key="raildispo.texte.Customer"/>',
	Comment:'<fmt:message key="raildispo.texte.Comment"/>',
	Shipto: '<fmt:message key="raildispo.texte.Shipto"/>',
	Delivery: '<fmt:message key="raildispo.texte.Delivery"/>',
	From: '<fmt:message key="raildispo.texte.From"/>',
	To: '<fmt:message key="raildispo.texte.To"/>',
	Pool: '<fmt:message key="raildispo.texte.Pool"/>',
	Carrier: '<fmt:message key="raildispo.texte.Carrier"/>',
	OnDemand: '<fmt:message key="raildispo.texte.OnDemand"/>',
	PossibleWagonTypes: '<fmt:message key="raildispo.texte.PossibleWagonTypes"/>',
	Prio: '<fmt:message key="raildispo.texte.Prio"/>',
	DefaultPrio: '<fmt:message key="raildispo.texte.DefaultPrio"/>',
	SwitchingTimeDep: '<fmt:message key="raildispo.texte.SwitchingTimeDep"/>',
	SwitchingTimeArr: '<fmt:message key="raildispo.texte.SwitchingTimeArr"/>',
	Departure: '<fmt:message key="raildispo.texte.Departure"/>',
	Arrival: '<fmt:message key="raildispo.texte.Arrival"/>',
	DaysOffset: '<fmt:message key="raildispo.texte.DaysOffset"/>',
	RailcarNr: '<fmt:message key="raildispo.texte.RailcarNr"/>',
	RailcarType: '<fmt:message key="raildispo.texte.RailcarType"/>',
	Track: '<fmt:message key="raildispo.texte.Track"/>',
	Position: '<fmt:message key="raildispo.texte.Position"/>',
	LoadingStatus: '<fmt:message key="raildispo.texte.LoadingStatus"/>',
	LoadingTime: '<fmt:message key="raildispo.texte.LoadingTime"/>',
	LastProduct: '<fmt:message key="raildispo.texte.LastProduct"/>',
	ArrivalTime: '<fmt:message key="raildispo.texte.ArrivalTime"/>',
	ContainerNr: '<fmt:message key="raildispo.texte.ContainerNr"/>',
	ContainerType: '<fmt:message key="raildispo.texte.ContainerType"/>',
	TareMeasure: '<fmt:message key="raildispo.texte.TareMeasure"/>',
	TareMasterData: '<fmt:message key="raildispo.texte.TareMasterData"/>',
	RawMeasure: '<fmt:message key="raildispo.texte.RawMeasure"/>',
	NetWeight: '<fmt:message key="raildispo.texte.NetWeight"/>',
	LoadingStateLoaded: '<fmt:message key="raildispo.texte.LoadingStateLoaded"/>',
	LoadingStateLoading: '<fmt:message key="raildispo.texte.LoadingStateLoading"/>',
	LoadingStateEmpty: '<fmt:message key="raildispo.texte.LoadingStateEmpty"/>',
	EditContainers: '<fmt:message key="raildispo.texte.editcontainers"/>',
	DestRailway: '<fmt:message key="raildispo.texte.DestRailway"/>',
	Dispatcher : '<fmt:message key="dispo.texte.dispatcher"/>',
	DeliveryNote : '<fmt:message key="dispo.texte.deliverynote"/>',
	CustNote: '<fmt:message key="dispo.texte.customer"/>',
	SwitchingTimeExplanation: '<fmt:message key="raildispo.texte.SwitchingTimeExplanation"/>',
	Preloads: '<fmt:message key="raildispo.texte.Preloads"/>',
	SpecialOrder: '<fmt:message key="raildispo.texte.addspecialorder"/>',
	First: '<fmt:message key="raildispo.texte.First"/>',
	Last: '<fmt:message key="raildispo.texte.Last"/>',
	PreLoadWagon: '<fmt:message key="raildispo.texte.preLoadWagon"/>',
	EtranReturn: '<fmt:message key="raildispo.texte.etran.return"/>',
	EtranOutbound: '<fmt:message key="raildispo.texte.etran.outbound"/>',
	Etran: '<fmt:message key="raildispo.texte.etran"/>'
};

var heightFactor = <c:out value="${order_raildispo_DispoForm.heightFactor}"/>;

var dispodate;
var orderList;
var wagonList;

var lastPageReload = new Date();

var filterEnabled = false;

function modifyButtonState(button, actionAllowed) {
	button.disabled=!actionAllowed;
	button.className = (actionAllowed)?"SmallButtonStyle":"SmallButtonStyleDisabled";
}

function dispodateIsToday() {
	ddate = dispodate.getValue();
	today = new Date();
	return today.clearTime().getElapsed(ddate.clearTime()) == 0;
}
	
function resizeTables() {
	var dispotable = Ext.get('dispocontainer');
	var ordertable = Ext.get('orderlistcontainer');
	var tablesheight = document.body.clientHeight - dispotable.getTop() - 60;
	dispotable.setHeight(Math.round(tablesheight * heightFactor));
	ordertable.setHeight(Math.round(tablesheight * (1-heightFactor)) - 15);
	
	refreshOrders();
	
}


function updateWagonList(wagons) {
	wagonList = new WagonList(Ext.getDom('dispocontainer'), wagons);
	wagonList.onSelectionChange = setDispoButtonsState;
	setDispoButtonsState();
}


function refreshWagons() {
	RailDispoAjaxService.getWagonList(updateWagonList);
}

function updateOrderList(orders) {
	orderList = new RailOrderList(Ext.getDom('orderlistcontainer'), orders);
	refreshWagons();
}

function refreshOrders() {
	RailDispoAjaxService.getOrderList(updateOrderList);
}		

function checkFiltersContent(filters) {
	var buttonActivation = false;
	for(var i=0; i<filters.length;i++) {
		buttonActivation +=checkFilterContent(filters[i].getValue());
	}
	return buttonActivation;
}

function filterWagonsAndOrders(isFilterEmpty) {
	modifyButtonState(Ext.getDom('btnFilterRailDispo'), false);
	modifyButtonState(Ext.getDom('btnResetFilterRailDispo'), isFilterEmpty);
	setFilterValues();
}

Ext.onReady(function() {
	document.body.style.overflow = "hidden";
	Ext.QuickTips.init();
	Ext.apply(Ext.QuickTips.getQuickTip(), {
		maxWidth: 400,
		dismissDelay: 0
	});
	var qt = Ext.QuickTips.getQuickTip();

	dispodate = new Ext.form.DateField({
		applyTo : 'DispoDayPicker',
		showToday: false,
		format: '<%= DateTimePatterns.JS_DATE %>'
	});
	dispodate.on('valid', function(event) {
		if(!dispodate.getValue()){
			dispodate.setValue(new Date());
		}
		RailDispoAjaxService.setDispoDate(dispodate.getValue().format('<%= DateTimePatterns.JS_DATE %>'));
		refreshOrders();
	});
			
	window.onresize = resizeTables;
	resizeTables();
	
	var filters= new Array(Ext.get('wagonNrFilter'),Ext.get('orderNrFilter'),Ext.get('productNameFilter'));
	modifyButtonState(Ext.getDom('btnResetFilterRailDispo'), checkFiltersContent(filters));
	
	var dd = Ext.get("tableresize").initDD("tableresize", null, {
		startDrag: function(x, y) {
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
		onInvalidDrop: function(e) {
			this.getEl().style.backgroundColor = "transparent";
			var dispotable = Ext.get('dispocontainer');
			var tablesheight = document.body.clientHeight - dispotable.getTop() - 60;
			var newHeight = Ext.get(this.getEl()).getTop() - 40 - dispotable.getTop();
			heightFactor = newHeight/tablesheight;
			if (heightFactor < this.minfact) {
				heightFactor = this.minfact;
			}
			else if (heightFactor > this.maxfact) {
				heightFactor = this.maxfact;
			}
			resizeTables();
			this.getEl().style.top = "0px";
		},
		maxfact: 0.9,
		minfact: 0.1
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

	Ext.get('buttonExport').on('click', function() {
		var url = 'RailDispoExportPopup.do?export';
		window.location = url;
	});
	
	Ext.get('wagonFilterContainer').on('keyup', function(event) {
		var isFilterEmpty = checkFiltersContent(filters);
		if(event.keyCode == 13 && isFilterEmpty !== 0) {
			filterWagonsAndOrders(isFilterEmpty);
		}
		else {
			modifyButtonState(Ext.getDom('btnFilterRailDispo'), isFilterEmpty);
		}
	});
	
	Ext.get('btnFilterRailDispo').on('click', function() {
		var isFilterEmpty = checkFiltersContent(filters);
		filterWagonsAndOrders(isFilterEmpty);
	});

	Ext.get('btnResetFilterRailDispo').on('click', function() {
		modifyButtonState(Ext.getDom('btnFilterRailDispo'), false);
		modifyButtonState(Ext.getDom('btnResetFilterRailDispo'), false);
		
		for(var i=0; i<filters.length;i++) {
			Ext.getDom(filters[i]).value="";
		}
		clearFilterValues();
	});
});
</script>

<c:if test="${!order_raildispo_DispoForm.dbIsEmpty}">

<html:form action="raildispo/Dispo.do" styleId='DispoForm'>

<script type="text/javascript">
	function setSelectedPlant() {
		a = {'setPlant':''};
		shippingpointList.collapse();
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
			url: 'Dispo.do', 
			params: a,
			success: function(response) {
				eval(response.responseText);
				refreshOrders();
			}
		});
	}
	
	function setOrderNumberFilter() {
		a = {'setOrderFilter':''};
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
			url: 'Dispo.do', 
			params: a,
			success: function(response) {
				eval(response.responseText);
			}
		});
	}
	
	function setSelectedCheckboxes() {
		a = {'setShippingpoints':''};
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
		   url: 'Dispo.do',
		   params: a,
		   success: function(response) {
			eval(response.responseText);
		  	refreshOrders();
		   }
		});
	
	}
	
	function setSelectedTrackCheckboxes() {
		a = {'setTracks':''};
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
		   url: 'Dispo.do',
		   params: a,
		   success: function(response) {
			eval(response.responseText);
		  	refreshOrders();
		   }
		});
	}

	function setShowNoTrackCheckbox() {
		a = {'setShowNoTrack':''};
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
		   url: 'Dispo.do',
		   params: a,
		   success: function(response) {
			eval(response.responseText);
		  	refreshOrders();
		   }
		});
	}
	
	function setSelectedStatus() {
		a = {'setStatus':''};
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
		   url: 'Dispo.do',
		   params: a,
		   success: function(response) {
			eval(response.responseText);
		  	refreshOrders();
		   }
		});
	}

	function setHideTransmittedCheckbox() {		
	a = {'setHideTransmitted':''};
		addFormElements(a, "DispoForm"); <%-- add form data --%>
		Ext.Ajax.request({
		   url: 'Dispo.do',
		   params: a,
		   success: function(response) {
			eval(response.responseText);
		  	refreshOrders();
		   }
		});
	}
	
	function setFilterValues() {
		var a = { 'formAction': actions.ACTION_FILTER_RAILDISPO,
				'wagonNumberFilter': Ext.get('wagonNrFilter').getValue(), 
				'orderNumberFilter': Ext.get('orderNrFilter').getValue(),
				'productNameFilter': Ext.get('productNameFilter').getValue()
		};
		addFormElements(a, "DispoForm");
		Ext.Ajax.request({
			url : 'Dispo.do',
			params : a,
			success: function() {		
				refreshOrders();
			}
		});
	}
	
	function clearFilterValues() {
		var params = { "formAction": actions.ACTION_RESET_FILTER_RAILDISPO };
		
		Ext.Ajax.request({
			url : 'Dispo.do',
			params : params,
			success: function() {
				refreshOrders();
			}
		});
	}

	function setAllCheckboxes(value, container) {
		list = Ext.query("input[type='checkbox']", Ext.getDom(container));
		for (i = 0; i < list.length; i++) {
			list[i].checked = value;
		}
	}
	
	function setDispoButtonsState() {
		modifyButtonState(Ext.getDom("buttonTransmit"), wagonList.canTransmitSelection() && authrail);
		modifyButtonState(Ext.getDom("buttonCancel"), wagonList.canCancelSelection() && authrail);		
		modifyButtonState(Ext.getDom("buttonUnloaded"), wagonList.canSetUnloadedSelection() && authrail);
		modifyButtonState(Ext.getDom("buttonAtshipto"), wagonList.canSetAtshiptoSelection() && authrail);
	}
	
	Ext.onReady(function() {
		shippingpointList = new Ext.form.TriggerField({
			readOnly:true
		});
		shippingpointList.listPanel = Ext.get('DivShippingpointsEdit');
		shippingpointList.onTriggerClick = function() {
			this.listPanel.load({
				url: 'Dispo.do',
				params: {'editShippingpoints':''},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts:true,
				callback: function(el) {
					el.show();
				}	
			});
			Ext.getDoc().on('mousewheel', this.collapseIf, this);
			Ext.getDoc().on('mousedown', this.collapseIf, this); 
		}
		shippingpointList.collapse = function() {
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
		  	Ext.getDoc().un('mousewheel', this.collapseIf, this);
			Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		shippingpointList.collapseIf = function(e) {
			if(!e.within(this.listPanel)) {
				this.collapse();
			}
		}
		shippingpointList.applyToMarkup('ShippingpointsField');
				
		tracksList = new Ext.form.TriggerField({
			readOnly:true
		});
		tracksList.listPanel = Ext.get('DivTracksEdit');
		tracksList.onTriggerClick = function() {
			this.listPanel.load({
				url: 'Dispo.do',
				params: {'editTracks':''},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts:true,
				callback: function(el) {
					el.show();
				}	
			});
			Ext.getDoc().on('mousewheel', this.collapseIf, this);
			Ext.getDoc().on('mousedown', this.collapseIf, this); 
		}
		tracksList.collapse = function() {
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
		  	Ext.getDoc().un('mousewheel', this.collapseIf, this);
			Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		tracksList.collapseIf = function(e) {
			if(!e.within(this.listPanel)) {
				this.collapse();
			}
		}
		tracksList.applyToMarkup('TracksField');		
				
		statusList = new Ext.form.TriggerField({
			readOnly:true
		});
		statusList.listPanel = Ext.get('DivStatusEdit');
		statusList.onTriggerClick = function() {
			this.listPanel.load({
				url: 'Dispo.do',
				params: {'editStatus':''},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts:true,
				callback: function(el) {
					el.show();
				}	
			});
			Ext.getDoc().on('mousewheel', this.collapseIf, this);
			Ext.getDoc().on('mousedown', this.collapseIf, this); 
		}
		statusList.collapse = function() {
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
		  	Ext.getDoc().un('mousewheel', this.collapseIf, this);
			Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		statusList.collapseIf = function(e) {
			if(!e.within(this.listPanel)) {
				this.collapse();
			}
		}
		statusList.applyToMarkup('StatusField');
		
		<jsp:include page="ShippingpointsTracksTile.jsp"/>
						
		refreshOrders();

		updateRunner = new Ext.util.TaskRunner();
		updateRunner.start({
			run: function() {
				if (wagonList && !wagonList.draggingInProgress && !isPopupShown()) {
					var now = new Date();
					var diff = now.getElapsed(lastPageReload);
					if (diff < 7200000) { // 2 hours
						Ext.QuickTips.getQuickTip().hide();
						refreshOrders();
					}
					else {
						window.location = document.URL; // reload whole window
					}
				}	
			},
			interval: 120000 
		}); 		
			
		setSelectedPlant();
	});
	
	function onOrderDetailClose() {
		refreshOrders();
	}	
	
	function logonPopupHideCallback() {
		refreshOrders();
	}
	
	function cancelAssignments() {
		var assids = wagonList.getSelectedAssignmentIds();
		if (assids.transmitted) {
			Logon.message.warning(errortexts.OrderTransmitted);
		}
		RailDispoAjaxService.cancelAssignment(assids.list, authrailspecial, refreshOrders);
	}

	function setUnloaded(i) {
		var assids = wagonList.getSelectedAssignmentIds().list;
		if (i >= assids.length) {

			return;
		}
		var wagonNos = wagonList.getSelectedWagonNos();
		var wagonNo = wagonNos[i];
		var assid = assids[i];

		RailDispoAjaxService.checkBimodalLoadBalanceAssignment(assids[i], 
			function(balance) {
				if (balance != 0) {
					Ext.MessageBox.buttonText.yes = '<fmt:message key="option.yes"/>';
					Ext.MessageBox.buttonText.no = '<fmt:message key="option.no"/>';
					Ext.MessageBox.confirm('<fmt:message key="dispo.messagebox.wagonnotempty.confirm"/>',
						String.format('<fmt:message key="dispo.errors.wagonNotEmpty"/>', ' ' + wagonNo, balance),
						function(btn) {
							if (btn == 'yes') {
								RailDispoAjaxService.setUnloaded(assids[i], refreshWagons);
							}
							setUnloaded(i + 1);
						});
				}
				else {
					RailDispoAjaxService.setUnloaded(assids[i], refreshWagons);
					setUnloaded(i + 1);
				}
			});
	}
	
	function checkFilterContent(filterValue) {
		if(filterValue != "" && filterValue !== undefined) {
			return true;
		}
		return false;
	}

</script>

<table class="big_form2">
	<tbody>
	<tr>
		<td width="1" class="label">
			<fmt:message key="raildispo.texte.plant"/>
		</td>
		<td width="1"> 

			<html:select size="1" style="width:140px;" property="selectedPlant" onchange="setSelectedPlant()" styleId="railplantselect">
				<c:forEach var="plant" varStatus="status" items="${order_raildispo_DispoForm.plants}">
					<html:option value="${plant.plantCd}"><c:out value="${plant.plantName}"/></html:option>
				</c:forEach>					
			</html:select>
			
		</td>
		<td width="1" class="label">
			<nobr><fmt:message key="raildispo.texte.shippingpoints"/></nobr>
		</td>
		<td width="1">
			<input type="text" id="ShippingpointsField" style="width:125px;color: black">
			<div class="border" id="DivShippingpointsEdit" style="z-index:40;background:white;width:200px; position: absolute;visibility:hidden">
			</div>
		</td>
		<td width="1" class="label">
			<fmt:message key="dispo.texte.date"/>
		</td>
		<td  width="1">
			<html:text styleClass='textfield' style='width:75px' styleId='DispoDayPicker' property="stringDate"/>
		</td>
		<td width="1" class="label">
			<fmt:message key="raildispo.texte.Tracks"/>
		</td>
		<td width="1">
			<input type="text" id="TracksField" style="width:125px;color: black">
			<div class="border" id="DivTracksEdit" style="z-index:40;background:white;width:280px; position: absolute;visibility:hidden">
			</div>
		</td>
		<td width="1">
			<nobr>
			<html:checkbox property="showNoTrack" styleId="NoTrackField" onclick="setShowNoTrackCheckbox()"></html:checkbox>
			<fmt:message key="raildispo.texte.NoTrack"/></nobr>
		</td>		
		<td width="1" class="label">
			<fmt:message key="raildispo.texte.wagonstatus"/>
		</td>
		<td width="1">
			<input type="text" id="StatusField" style="width:125px;color: black">
			<div class="border" id="DivStatusEdit" style="z-index:40;background:white;width:200px; position: absolute;visibility:hidden">
			</div>
		</td>
		<td width="1">
			<input type="button" id="buttonSavePreferences" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>"/><br/>
		</td>
		<td width="1">
			<input type="button" id="buttonResetField" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>"/>
		</td>
			<td width="1">
				<input type="button" id="buttonExport" class="ViewButtonStyle" value="<fmt:message key="buttons.export"/>" title="<fmt:message key="raildispo.button.exportAlt"/>"/>
			</td>		
		<td width="100%">
		</td>
	</tr>
	</tbody>
</table>
<table id="wagonFilterTable" style="width:100%; border-collapse: separate; border-spacing:0;">
	<tr>
	<td>
		<p class="label" style="margin-top: 8px;">
		<fmt:message key="dispo.texte.disposition"/>
		<span id="outputSpan"> </span>
		</p>
	</td>
	<td>
		<div id="wagonFilterContainer" style="float:right">
			<table class="big_form" style="width:auto">
				<tr>
					<td class="label">
						<fmt:message key="application.label.filter"/>
					</td>
					<td>
						<fmt:message key="application.label.wagon"/>
						<c:set var="wagonNumberFilterTitle">
							<fmt:message key="wagon.wagonno"/>
						</c:set>
						<html:text styleId="wagonNrFilter" property="wagonNumberFilter" title="${wagonNumberFilterTitle}" />
					</td>
					<td>
						<fmt:message key="dispo.texte.order"/>
						<c:set var="orderNumberFilterTitle">
							<fmt:message key="fields.order.number"/>
						</c:set>
						<html:text styleId="orderNrFilter" property="orderNumberFilter" title="${orderNumberFilterTitle}" />
					</td>
					<td>
						<fmt:message key="application.label.material"/>
						<c:set var="productNameFilterTitle">
							<fmt:message key="raildispo.filter.product.alt"/>
						</c:set>
						<html:text styleId="productNameFilter" property="productNameFilter" title="${productNameFilterTitle}" />
					</td>
					<td>
						<input id="btnFilterRailDispo" class="SmallButtonStyleDisabled" type="button"
							value="<fmt:message key="application.label.filter"/>" disabled>
						<input id="btnResetFilterRailDispo" class="SmallButtonStyleDisabled" type="button"
							value="<fmt:message key="buttons.reset"/>" disabled>
					</td>
				</tr>
			</table>
		</div>
	</td></tr>
</table>
</html:form>

<div id="dispocontainer" class="dispocontainer"></div>
<table style="width:100%">
	<tr>
		<td class="label" style="padding-top: 8px">
			<fmt:message key="dispo.texte.orders"/>
		</td>
		<td class="buttons right" style="vertical-align: top">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.unloaded"/>" id="buttonUnloaded" 
				onclick="modifyButtonState(this,false);setUnloaded(0)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.atshipto"/>" id="buttonAtshipto" 
				onclick="modifyButtonState(this,false);RailDispoAjaxService.setAtShipto(wagonList.getSelectedAssignmentIds().list, refreshWagons)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="raildispo.button.transmit"/>" id="buttonTransmit" 
				onclick="modifyButtonState(this,false);RailDispoAjaxService.transmitToSAP(wagonList.getSelectedAssignmentIds().list, refreshWagons)">
			<input type="button" class="SmallButtonStyle" value="<fmt:message
				key="dispo.button.remove"/>" id="buttonCancel"
				onclick="modifyButtonState(this,false);cancelAssignments()">
		</td>
		<td style="width:10px">
		</td>
	</tr>
</table>
<div id="tableresize" style="position:relative;background-color:transparent;width:100%; height:3px; z-index:30; cursor: row-resize; text-align:center;font-size: 1px">
	<img style='background-color:#999; width: 300px;height:1px;'></img>
</div>
<div id="orderlistcontainer" class="dispocontainer"></div>
<table style="width:100%">
	<tr>
		<td class="buttons right" style="vertical-align: top;">
		</td>
		<td style="width:10px">
		</td>
	</tr>
</table>

</c:if>

<%-- END $RCSfile: RailDispoTile.jsp,v $ --%>
