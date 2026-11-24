<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WagonDetailTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../../includeTLDs.jsp"%>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<%@page import="com.crh.logon.persistence.entities.common.WagonEntity"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.admin.web.forms.masterdata.WagonDetailForm"%>
<%@page import="com.crh.logon.taglib.OrderDetailButtonLogicTag"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>

<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<%@page import="com.crh.logon.taglib.OrderDetailButtonLogicTag"%><c:set var="ORDER_STATUS_FINISHED"><%=Constant.ORDER_STATUS_FINISHED%></c:set>
<c:set var="ORDER_STATUS_TRANSMITTED_TO_RAIL"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL%></c:set>

<c:set var="form" value="${sessionScope.admin_WagonDetailForm}"/>

<c:set var="order" value="${sessionScope.admin_WagonDetailForm.orderBean.order}"/>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/WagonAdminAjaxService.js'/>"></script>
<c:set var="wagonDetailUrl">
	/../admin/WagonDetail.do
</c:set>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript">

	<c:if test="${!empty form.orderBean.orderId}">
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
								form = Ext.getDom('WagonDetailFormId');
								storeValueAsHiddenField('WagonDetailFormId', 'orderBean.railHoldBackReOrderFlag', el.checked);
								storeValueAsHiddenField('WagonDetailFormId', 'formAction', '<%=WagonDetailAction.FORM_ACTION_HOLD_BACK_RETURN_ORDER%>');
								
								Logon.message.showLoading("<fmt:message key="common.saving"/>");
								Ext.get('WagonDetailFormId').dom.submit();
							}
							else {
								el.checked = !el.checked;
							}
						});
	
	}



	function displayWagonOrderDeleteDialog() {
		
		showCommonReasonDialog.createDelegate(this,[
	 			{
					title: '<fmt:message key="orderdetail.popup.delete.title"/> - <c:out value="${form.orderBean.orderNumber}" />',
				 	reason:'<fmt:message key="orderdetail.popup.delete.reason"/>',
				 	addText:
						<c:if test="${(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED)}" >			                        					 	 
					 	'<fmt:message key="orderdetail.messagebox.specialdeleteclosed.text"/> ' + 
						</c:if>
						'<fmt:message key="orderdetail.messagebox.text"/>',
				 	CancelText: '<fmt:message key="buttons.cancel"/>',
				 	SaveText:'<fmt:message key="orderdetail.messagebox.confirm"/>'
					},
				function(inputDialogText) {
						storeValueAsHiddenField('WagonDetailFormId', 'formAction', '<%=WagonDetailAction.FORM_ACTION_DEALLOCATE_AND_DELETE_ORDER%>');
						storeValueAsHiddenField('WagonDetailFormId', 'reason', inputDialogText);
						Logon.message.showLoading("<fmt:message key="common.saving"/>");
						Ext.get('WagonDetailFormId').dom.submit();
				}
				])();
	}

	<crh-common:authorize function="<%= Authorization.RAIL_DISPATCHING_SW %>">
	function deallocateOrder() {
		Ext.MessageBox.buttonText.yes = '<fmt:message key="orderdetail.messagebox.delete"/>';
		Ext.MessageBox.buttonText.no = '<fmt:message key="orderdetail.messagebox.cancel"/>';

		Ext.MessageBox.confirm('<fmt:message key="orderdetail.messagebox.confirm"/>',
				<c:if test="${(form.orderBean.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_RAIL)}" >
					'<fmt:message key="orderdetail.message.delete.transmitted"/> ' + 
				</c:if>
				'<fmt:message key="orderdetail.messagebox.text.deallocate"/>',
						function(btn) {
							if (btn == 'yes') {
								storeValueAsHiddenField('WagonDetailFormId', 'formAction', '<%=WagonDetailAction.FORM_ACTION_DEALLOCATE_ORDER%>');
								Logon.message.showLoading("<fmt:message key="common.saving"/>");
								Ext.get('WagonDetailFormId').dom.submit();
							}
						});
	}
	</crh-common:authorize>
	
	
	<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
	function doReportWagonEmpty() {
		form = Ext.getDom('WagonDetailFormId');
		storeValueAsHiddenField('WagonDetailFormId', 'formAction', '<%=WagonDetailAction.FORM_ACTION_REPORT_AS_EMPTY%>');
		Logon.message.showLoading("<fmt:message key="common.saving"/>");
		Ext.get('WagonDetailFormId').dom.submit();
	}

	function reportWagonEmpty() {
		WagonAdminAjaxService.checkBimodalLoadBalance(<c:out value="${form.orderBean.orderId}"/>, 
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
	</c:if> 
	

 function onOrderDetailClose(){
	selectWagon('<c:out value="${form.wagonId}"/>');
}

function logonPopupHideCallback(){
	onOrderDetailClose();
}
function hidePopupCallFunction(){
	onOrderDetailClose();
}

	var activeTab = '<%=WagonDetailAction.FORWARD_LOAD_TAB%>';
	var selectedTab = '<c:out value="${admin_WagonDetailForm.selectedTab}"/>';
	function setCurrentWagonDetailTab(newTab) {
		if (selectedTab == '<%=WagonDetailAction.FORWARD_LOAD_TAB%>' && !checkComments()){
			return;
		}
		selectedTab = newTab;
		form = Ext.getDom('WagonDetailFormId');
		p = {'formAction' :'<%=WagonDetailAction.FORM_ACTION_LOAD_TAB%>',
			 'selectedTab' :selectedTab,
			 'activeTab' :activeTab
			};
		addFormElements(p, form);
			
		var diva = Ext.get('TabsDiv');
		diva.load( {
			url :'<c:url value="${wagonDetailUrl}"/>',
			params : p,
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
	}

	function requestInfo(info, divId) {
	
		//save with a form
	    form = Ext.getDom('WagonDetailFormId');
		p = {'formAction' :'<%=WagonDetailAction.FORM_ACTION_LOAD_INFO%>',
			 'requestedInfo' :info
			};
		addFormElements(p, form);
			
		var diva = Ext.get(divId);
		diva.load( {
			url :'<c:url value="${wagonDetailUrl}"/>',
			params : p,
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
	}

	function checkComments(){
		
		var confirmCommentTrim = checkCommentLength("wagonDetailLoad.wagonNoteDisposition",'<fmt:message key="confirm.trim.textarealength"/>','<fmt:message key="wagon.note_disposition"/>',<%=WagonEntity.DBSIZE_WAGON_NOTE_DISPOSITION + ""%>);
		if (!confirmCommentTrim) {
				return false;
		}
		confirmCommentTrim = checkCommentLength("wagonDetailLoad.wagonNoteRailwaySpedition",'<fmt:message key="confirm.trim.textarealength"/>','<fmt:message key="wagon.note_railway_spedition"/>',<%=WagonEntity.DBSIZE_WAGON_NOTE_RAILWAY_SPEDITION + ""%>);
		if (!confirmCommentTrim) {
			return false;
		}
		confirmCommentTrim = checkCommentLength("wagonDetailMaintenance.wagonNoteMaintenance", '<fmt:message key="confirm.trim.textarealength"/>','<fmt:message key="wagon.note_maintenance"/>',  <%=WagonEntity.DBSIZE_WAGON_NOTE_MAINTENANCE + ""%>);
		if (!confirmCommentTrim) {
			return false;
		}
		return true;
	}

	function saveWagon() {
		if(selectedTab == '<%=WagonDetailAction.FORWARD_LOAD_TAB%>' && !checkComments()) {
			return false;
		}
			
		storeValueAsHiddenField('WagonDetailFormId', 'formAction', '<%=WagonDetailAction.FORM_ACTION_SAVE%>');
		storeValueAsHiddenField('WagonDetailFormId', 'selectedTab', selectedTab);
		storeValueAsHiddenField('WagonDetailFormId', 'activeTab', activeTab);

		Logon.message.showLoading("<fmt:message key="common.saving"/>");
	
		Ext.get('WagonDetailFormId').dom.submit();
	}

	function copyWagon() {
		showPopup('WagonDetail.do?formAction='+ '<%=WagonDetailAction.FORM_ACTION_COPY%>');
	}

	function resetWagon() {
		//storeValueAsHiddenField('WagonDetailFormId', 'formAction', '<%=WagonDetailAction.FORWARD_RESET%>');
		//storeValueAsHiddenField('WagonDetailFormId', 'selectedTab', selectedTab);
		//storeValueAsHiddenField('WagonDetailFormId', 'activeTab', activeTab);
		// Ext.get('WagonDetailFormId').dom.submit();
		form = Ext.getDom('WagonDetailFormId');
		p = {'formAction' :'reset',
			 'selectedTab' :selectedTab,
			 'activeTab' :activeTab
			};
		addFormElements(p, form);
			
		var diva = Ext.get('TabsDiv');
		diva.load( {
			url :'<c:url value="${wagonDetailUrl}"/>',
			params : p,
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
	}
	
	function selectWagon(id) {
		window.location = "./WagonDetailEntry.do?wagonid="+id;
	}

<%--	function deleteWagon() {

		Ext.MessageBox.buttonText.yes = '<fmt:message key="orderdetail.messagebox.delete"/>';
		Ext.MessageBox.buttonText.no = '<fmt:message key="orderdetail.messagebox.cancel"/>';
		Ext.MessageBox.confirm('<fmt:message key="orderdetail.messagebox.confirm"/>',
			'<fmt:message key="orderdetail.messagebox.text"/>',
			function(btn) {
				if (btn == 'yes') {

					logonPopup.load( {
						url :'<c:url value="${wagonDetailUrl}"/>',
						params : {'formAction' :'<%=WagonDetailAction.FORM_ACTION_DELETE%>'},
							text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
							scripts :true
						});
					hidePopup();
				}
			});
	}--%>
	
	var arrivalLastKnownStationDate;
	var arrivalLastKnownStationTime;
	var lastKnownStationCombo;
	var lastKnownStationNoCombo;
	var currentTrackCombo;
	var currentPosCombo;
	
	Ext.onReady(function() {
		var wagonList = Ext.get('wagonlist');
		
		if (wagonList) {
			wagonList.setHeight(Ext.get('wagonlistcolumn').getHeight());
			Ext.get('maindata').setHeight(wagonList.getHeight());
		}
		
		arrivalLastKnownStationDate = new Ext.form.DateField({
			id: 'wagonDetailMain.arrivalLastKnownStationDate',
			format: 'd.m.Y',
			applyTo: 'wagonDetailMain.arrivalLastKnownStationDate',
			allowBlank: true,
			style: 'width:110px;',
			width:'110px',
			//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
			markInvalid:function(msg){
				msg = msg.replace(this.format, "dd.mm.yyyy");
				Ext.form.DateField.superclass.markInvalid.call(this,msg);
			},
			value:'<c:out value="${form.wagonDetailMain.arrivalLastKnownStationDate}"/>'
		});

		arrivalLastKnownStationTime = new Ext.form.TimeField({
			id: 'wagonDetailMain.arrivalLastKnownStationTime',
			format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'wagonDetailMain.arrivalLastKnownStationTime',
			allowBlank: true,
			style: 'width:50px;',
			width:'50px'			
		});	
		arrivalLastKnownStationTime.setValue('<c:out value="${form.wagonDetailMain.arrivalLastKnownStationTime}"/>');
		arrivalLastKnownStationDate.setValue('<c:out value="${form.wagonDetailMain.arrivalLastKnownStationDate}"/>');
		if (activeTab != selectedTab) {
			setCurrentWagonDetailTab(selectedTab);
		}

		var lastKnownStationComboSearchRec = Ext.data.Record.create(
				[
					{name: "railwayStationId"},
					{name: "railwayStationName"},
					{name: "railwayStationNo"}		
				]);

		var currentTrackRec = Ext.data.Record.create(
				[
					{name: "compositeId"},
					{name: "plantTrackCd"},
					{name: "trackName"},
					{name: "displayTrackName"},
					{name: "txt"},
					{name: "maxTrackPositionNo"}
				]);

		
    var lastKnownStationStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { WagonAdminAjaxService.getRailwayStationsForName(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'railwayStationId', totalProperty:'totalSize'}, lastKnownStationComboSearchRec)
    });

    var lastKnownStationNoStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { WagonAdminAjaxService.getRailwayStationsForNo(skip, count, sort, query, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'railwayStationId', totalProperty:'totalSize'}, lastKnownStationComboSearchRec)
    });   

    var currentTrackStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) {var rwsId = lastKnownStationNoCombo.getValue(); WagonAdminAjaxService.fetchTrackListForRailwayStation(skip, count, sort, rwsId, callback)}, true),
		reader: new Ext.data.ListRangeReader({id:'plantTrackCd', totalProperty:'totalSize'}, currentTrackRec)
    });  

	lastKnownStationCombo =   new Ext.ux.TwinCombo({
		transform:'wagonDetailMain.lastKnownRailwayStationId',
    	id: 'wagonDetailMain.lastKnownRailwayStationId',
        store: lastKnownStationStore,
        displayField: 'railwayStationName',
        valueField: 'railwayStationId',
        pageSize: 20,
        minChars:2,
        width: Ext.get('wagonDetailMain.wagonNo').getWidth(),
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true,
        clearValue:function(){
			Ext.ux.TwinCombo.superclass.clearValue.call(this);
			if(lastKnownStationNoCombo.getValue()){
				lastKnownStationNoCombo.clearValue();
			}
			this.reset();
        },
        reset:function(){
			Ext.ux.TwinCombo.superclass.reset.call(this);
		}   
	});

	lastKnownStationNoCombo =   new Ext.ux.TwinCombo({
		transform:'lastKnownStationNo',
    	id: 'lastKnownStationNo',
        store: lastKnownStationNoStore,
        displayField: 'railwayStationNo',
        valueField: 'railwayStationId',
        pageSize: 20,
        width: Ext.get('wagonDetailMain.wagonNo').getWidth(),
        listWidth: 250,
        minChars:2,
        typeAhead: false,
        triggerAction: 'all',
        selectOnFocus: true,
        clearValue:function(){
			Ext.ux.TwinCombo.superclass.clearValue.call(this);
			if(lastKnownStationCombo.getValue()){				
				lastKnownStationCombo.clearValue();
			}
			this.reset();
    	},
        reset:function(){
    		currentTrackCombo.clearValue();
   	    		currentTrackCombo.store.removeAll();
   	    		currentTrackCombo.reset();
  	    		currentTrackCombo.disable();

			Ext.ux.TwinCombo.superclass.reset.call(this);
		}        
	});


	currentTrackCombo =   new Ext.ux.TwinCombo({
		transform:'wagonDetailMain.currentPlantTrackCd',
    	id: 'wagonDetailMain.currentPlantTrackCd',
        store: currentTrackStore,
        displayField: 'displayTrackName',
        valueField: 'plantTrackCd',
        pageSize: 20,
        listWidth: 250,
        typeAhead: false,
        tpl:'<tpl for="."><div class="x-combo-list-item">{displayTrackName}</div></tpl>',
        triggerAction: 'all',
        selectOnFocus: true,
        editable:false,
        clearValue:function(){
			Ext.ux.TwinCombo.superclass.clearValue.call(this);
			this.reset();
        },
        reset:function(){
       		Ext.ux.TwinCombo.superclass.reset.call(this);
       		currentPosCombo.clearValue();
       		currentPosCombo.store.removeAll();
       		currentPosCombo.disable();
       	}
	});

	var rt = Ext.data.Record.create([{name: 'no'}]);
		
	currentPosCombo = new Ext.form.ComboBox({
		id: 'wagonDetailMain.currentTrackPositionNo',
	    typeAhead: false,
	    mode: 'local',
	    editable: false,
	    forceSelection:false,
	    allowBlank:true,
	    shadow: false,
	    width: 50,
	    triggerAction: 'all',
	    transform:'wagonDetailMain.currentTrackPositionNo',
	    emptyText: '...',
	    style: 'margin-bottom: 2px',
	    store: new Ext.data.JsonStore({
        	fields: [
             		{name: 'no'}
             	],
             	data : []
	    }),
	    displayField: 'no',
	    valueField: 'no',
		updateOptions: function(record){
			this.reset();	
			this.store.removeAll();
			var max  = 0;
			if(record.data.maxTrackPositionNo){
				 max = parseFloat(record.data.maxTrackPositionNo);
			}
			<c:if test="${empty form.wagonDetailMain.currentTrackPositionNo}" >
			<%--In this case always show the maximum+1 because the wagon does not have yet a place on that truck--%>
			max = max+1;
			</c:if>
			<c:if test="${!empty form.wagonDetailMain.currentTrackPositionNo}" >
			<%--In this case show the maximum+1 only if the wagon would switch track--%>
			if (record.data.plantTrackCd != '<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.plantTrackCd}" />'){
				max = max+1;
			}
			</c:if>			
			for (var i=max;i>=1;i--){
				var data = {"no":i};
				var r = new rt(data, i);
				this.store.insert(0, r);
			}
			var data = {"no":'...'};
			var r = new rt(data, i);
			this.store.insert(0, r);
		}
		    
	});
	currentPosCombo.disable();
	currentTrackCombo.disable();

	currentTrackCombo.on('select', function(combo, record) {
		currentPosCombo.enable();
		currentPosCombo.updateOptions(record);		
	});

	lastKnownStationNoCombo.on('select', function(combo, record) {
		lastKnownStationCombo.reset();
		lastKnownStationStore.insert(0, record);
		lastKnownStationCombo.preselectValue(record.data.railwayStationId, false);
		currentTrackCombo.reset();
		currentTrackCombo.enable();
		if (currentTrackCombo.store.lastOptions){
			currentTrackCombo.store.reload(currentTrackCombo.store.lastOptions);
		}
	});

	lastKnownStationCombo.on('select', function(combo, record) {
		lastKnownStationNoCombo.reset();
		lastKnownStationNoStore.insert(0, record);
		lastKnownStationNoCombo.preselectValue(record.data.railwayStationId, false);
		currentTrackCombo.reset();
		currentTrackCombo.enable();
		if (currentTrackCombo.store.lastOptions){
			currentTrackCombo.store.reload(currentTrackCombo.store.lastOptions);
		}
	});
	
	<c:if test="${!empty form.wagonDetailMain.lastKnownRailwayStation.railwayStationId}" >
	var defaultData = {
		    railwayStationId: '<c:out value="${form.wagonDetailMain.lastKnownRailwayStation.railwayStationId}"/>',
		    railwayStationName: '<c:out value="${form.wagonDetailMain.lastKnownRailwayStation.railwayStationName}"/>',
			railwayStationNo: '<c:out value="${form.wagonDetailMain.lastKnownRailwayStation.railwayStationNo}"/>'			    
		};

	
	var r = new lastKnownStationComboSearchRec(defaultData, '<c:out value="${form.wagonDetailMain.lastKnownRailwayStation.railwayStationId}"/>');
	lastKnownStationStore.insert(0, r);
	lastKnownStationNoStore.insert(0, r);

	lastKnownStationCombo.preselectValue(<c:out value="${form.wagonDetailMain.lastKnownRailwayStation.railwayStationId}" />, false);
	lastKnownStationNoCombo.preselectValue(<c:out value="${form.wagonDetailMain.lastKnownRailwayStation.railwayStationId}" />, false);

	<c:if test="${!empty form.wagonDetailMain.currentPlantRailwayStation}" >
	var plantRailwaystation = {
		    plantTrackCd: '<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.plantTrackCd}"/>',
		    txt: '<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.txt}"/>',
			trackName: '<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.trackName}"/>',
			displayTrackName: '<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.displayTrackName}"/>',
			maxTrackPositionNo:'<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.maxTrackPositionNo}"/>' 					    
		};
	var r = new currentTrackRec(plantRailwaystation, '<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.plantTrackCd}"/>');
	currentTrackStore.insert(0, r);
	currentTrackCombo.preselectValue(<c:out value="${form.wagonDetailMain.currentPlantRailwayStation.plantTrackCd}"/>, false);
	currentPosCombo.enable();
	currentPosCombo.updateOptions(r);		
	currentPosCombo.setValue('<c:out value="${form.wagonDetailMain.currentTrackPositionNo}"/>');
	</c:if>
	
	currentTrackCombo.enable();
	</c:if>
	});

</script>

<div class="customer_max">
<jsp:include page="../../SubTab9Tile.jsp"/>   
<table class="big_form2">
	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.6"/></th>
	</tr>
	<tr>
    	<td>
            <jsp:include page="../../Sub6Tab9Tile.jsp"/>
   			<table class="classicblue"  style="width:100%;" style="margin-top:4px">
				<tr>
      				<th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.6"/> / <fmt:message key="tabsubitem.9.6.1"/></th>
	            </tr>           
					<tr>
						<td>
<html:form action="WagonDetail.do" styleId="WagonDetailFormId" style="margin-top: 0px;">
<c:set var="datePattern" value="dd/MM/yyyy" scope="request"/> 
<c:set var="timePattern" value="HH:mm" scope="request"/> 
		
<table class="classicblue" style="border: 0px; padding: 0px; width: 99%;">
	<colgroup>
		<col width="40%">
		<col width="60%">
	</colgroup>
	<tr>
	<td>	
	<table class="big_form2" style="spacing:0px; border: 0px; padding: 0px;width:100%;" >
<c:if test="${!empty form.wagonIdMap }">	            
                            <colgroup>
                            <col width="30%">
                            <col width="70%">
                            </colgroup>		
</c:if>
                            
				<tr>
<c:if test="${!empty form.wagonIdMap }">
			<td id="wagonlistcolumn" >
                    <div id="wagonlist" class="subformlist" style="font-size:12px;">
                          <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="width:100%;">
                            <colgroup>
                            <col width="99%">
                            <col width="1%">
                            </colgroup>
	<c:set var="zebracount" value="1"/>
	<c:forEach var="entry" items="${form.wagonIdMap}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
                              <tr  <c:if test="${entry.key eq form.wagonId }">class="selected"</c:if><c:if test="${entry.key ne form.wagonId }"> class="zebra<c:out value="${zebracount}" />" </c:if> >
                                <td><nobr><a href="#" onclick='selectWagon(<c:out value="${entry.key}"/>)'><crh-common:wagonNumberFormat wagonNumber="<%=(String)((Map.Entry)pageContext.getAttribute("entry")).getValue() %>"/></a></nobr></td>
                              </tr>
	</c:forEach>
                          </table>
                          </div>			
			</td>
</c:if>				
				<td  style="vertical-align: top;">
					<div id="mainSection">
						<div id="maindata" class="subform" style="width:100%;">
							<!--  main edit -->
							<table class="classicblue" style="border: 0px; padding: 0px; width: 100%;">
								<colgroup>
									<col width="35%"></col>
				  					<col width="65%"></col>
								</colgroup>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagon.wagonno"/>
										</td>
										<td>
											<html:text disabled="true" styleId="wagonDetailMain.wagonNo" property="wagonDetailMain.wagonNo" style="width: 100%;" styleClass="textfielddisabled"/>
										</td>
									</tr>
								
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagon.wagontype"/>
										</td>
										<td>
											<html:text disabled="true" styleId="wagonDetailMain.wagonTypeName" property="wagonDetailMain.wagonTypeName" style="width: 100%;" styleClass="textfielddisabled"/>
										</td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.group"/>
										</td>
										<td>
											Group TBD later
										</td>
									</tr>
							</table>
							<br>							
							<table class="classicblue" style="border: 0px; padding: 0px; width: 100%;">
								<colgroup>
									<col width="35%"></col>
				  					<col width="65%"></col>
								</colgroup>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.ordercurrent"/>
										</td>
										<td>
											<table style="padding:0px;margin:0px;border-spacing:0px;"><tr><td style="padding:0px;" align="left"><c:out value="${form.orderBean.orderNumber}"/></td><td>&nbsp;</td><td style="padding:0px;" align="left"><c:out value="${form.orderBean.distributionDestCd}"/></td></tr></table>										
										</td>
									</tr>									
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.status"/>
										</td>
										<td>
											<c:out value="${form.orderBean.orderStatus }"/>
										</td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.since"/>
										</td>
										<td>
											<table style="padding:0px;margin:0px;border-spacing:0px;"><tr><td style="padding:0px;" align="left"><c:out value="${form.orderBean.orderStatusSince }"/></td><td>&nbsp;</td><td style="padding:0px;" align="left"><c:out value="${form.orderBean.orderStatusSinceTimeString}"/></td></tr></table>
										</td>
									</tr>
							</table>
							<br>							
							<table class="classicblue" style="border: 0px; padding: 0px; width: 100%;">
								<colgroup>
									<col width="35%"></col>
				  					<col width="65%"></col>
								</colgroup>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<nobr><fmt:message key="wagondetail.text.arrival"/></nobr>
										</td>
										<td>
											<table style="padding:0px;margin:0px;border-spacing:0px;">
												<tr>
													<td style="padding:0px;" align="left">
														<input type = "text" id="wagonDetailMain.arrivalLastKnownStationDate" name="wagonDetailMain.arrivalLastKnownStationDate" value=""/>
													</td>
													<td>&nbsp;</td>
													<td style="padding:0px;" align="left">
														<input type = "text" id="wagonDetailMain.arrivalLastKnownStationTime" name="wagonDetailMain.arrivalLastKnownStationTime" value=""/>
													</td>
												</tr>
											</table>
										</td>
										
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<nobr><fmt:message key="wagondetail.text.lastarrivalstation"/></nobr>
										</td>
										<td>
											<input type="text" value="" name="wagonDetailMain.lastKnownRailwayStationId" id="wagonDetailMain.lastKnownRailwayStationId" style="width: 100%"/>
										</td>
										
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.stationcurrno"/>
										</td>
										<td>
											<input type="text" value="" id="lastKnownStationNo" style="width: 150px"/>
										</td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.track"/>/<fmt:message key="wagondetail.text.pos"/>
										</td>
										<td>
											<table style="padding:0px;margin:0px;border-spacing:0px;"><tr><td style="padding:0px;width: 70%;" align="left"><input type="text" value="" name="wagonDetailMain.currentPlantTrackCd" id="wagonDetailMain.currentPlantTrackCd"/></td><td>&nbsp;</td><td style="padding:0px;width:30%;" align="left"><input type="hidden" id="wagonDetailMain.currentTrackPositionNo" name="wagonDetailMain.currentTrackPositionNo" /></td></tr></table>										
										</td>
									</tr>										
							</table>
							<br>
							<table class="classicblue" style="border: 0px; padding: 0px; width: 100%;">
								<colgroup>
									<col width="35%"></col>
				  					<col width="65%"></col>
								</colgroup>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.stationto"/>
										</td>
										<td>
											<c:out value="${form.wagonDetailMain.stationTo.railwayStationName }"/>
										</td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.stationtono"/>
										</td>
										<td>
											<c:out value="${form.wagonDetailMain.stationTo.railwayStationNo}"/>
										</td>
									</tr>									
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.arrival"/>
										</td>
										<td>
										<c:if test="${!empty form.wagonDetailMain.arrivalDate}">
											<c:out value="${form.wagonDetailMain.arrivalDate}" />
										</c:if>
										<c:if test="${empty form.wagonDetailMain.arrivalDate}">
										<c:if test="${!empty form.wagonDetailMain.plannedArrivalDate}">
											planned: <c:out value="${form.wagonDetailMain.plannedArrivalDate}"/>
										</c:if>
										</c:if>
										</td>
									</tr>
							</table>
							<br>
							<table class="classicblue" style="border: 0px; padding: 0px; width: 100%;">
								<colgroup>
									<col width="35%"></col>
				  					<col width="65%"></col>
								</colgroup>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.stationfrom"/>
										</td>
										<td>
											<c:out value="${form.wagonDetailMain.stationFrom.railwayStationName}"/>
										</td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.stationfromno"/>
										</td>
										<td>
											<c:out value="${form.wagonDetailMain.stationFrom.railwayStationNo}"/>
										</td>
									</tr>
									<tr>
										<td class="label" style="vertical-align: bottom;">
											<fmt:message key="wagondetail.text.departure"/>
										</td>
										<td>
										<c:if test="${!empty form.wagonDetailMain.departureDate}">
      	    								<c:out value="${form.wagonDetailMain.departureDate}"/>
      	    							</c:if>
      	    							<c:if test="${empty form.wagonDetailMain.departureDate}">
      	    							<c:if test="${!empty form.wagonDetailMain.plannedDepartureDate}">
      	    								planned: <c:out value="${form.wagonDetailMain.plannedDepartureDate}"/>
      	    							</c:if>
      	    							</c:if>      	    																			
										</td>
									</tr>																	
							</table>
							<div style="width: 100%; text-align: right;">
						<c:if test="${!empty form.orderBean.orderId}">							
						<input type="button" onClick="javascript:reportWagonEmpty();" value="<fmt:message key="wagondetail.button.setdelivered"/>"
						<crh-common:evaluateIfDisabled orderObjName="order" buttonFunc="<%=OrderDetailButtonLogicTag.REPORT_WAGON_EMPTY%>" >
							disabled
							class="SmallButtonStyleDisabled"
						</crh-common:evaluateIfDisabled>
							class="SmallButtonStyle"
						/>
						&nbsp;							
						<input type="button" value="<fmt:message key="wagondetail.button.dealocate.delete"/>" onClick="displayWagonOrderDeleteDialog();"
						<crh-common:evaluateIfDisabled orderObjName="order" buttonFunc="<%=OrderDetailButtonLogicTag.DEALLOCATE_AND_DELETE_ORDER %>" >
							disabled
							class="SmallButtonStyleDisabled"
						</crh-common:evaluateIfDisabled>
						 class="SmallButtonStyle"
						/>
						&nbsp;	
						<input type="button" value="<fmt:message key="wagondetail.button.deallocate"/>" onClick="deallocateOrder();"
						<crh-common:evaluateIfDisabled orderObjName="order" buttonFunc="<%=OrderDetailButtonLogicTag.DEALLOCATE_ORDER%>" >
							disabled
							class="SmallButtonStyleDisabled"
						</crh-common:evaluateIfDisabled>
						 class="SmallButtonStyle"
						/>							
						</c:if>						
						<c:if test="${!empty form.orderBean.orderId}">
						&nbsp;						
							<span title="<fmt:message key="wagondetail.checkbox.holdbackreflag.title"/>">
								<input type="checkbox" onclick="javascript:setRailHoldBackReOrderFlag(this)" <c:if test="${form.orderBean.railHoldBackReOrderFlag}">checked</c:if>
							<crh-common:evaluateIfDisabled orderObjName="order"  buttonFunc ="<%=OrderDetailButtonLogicTag.SET_HOLD_BACK_RE_ORDER_FLAG %>" >
								disabled								
							</crh-common:evaluateIfDisabled>
								/>
							</span>							
						</c:if>
						<c:if test="${empty form.orderBean.orderId}">
						&nbsp;
							<input type="button" value="<fmt:message key="wagondetail.text.new.order"/>"
									onclick="showPopup('/logon/order/auftrag/OrderSpecialEntry.do?wagonid=<c:out value="${form.wagonId}"/>')" class="SmallButtonStyle" />
						</c:if>	
							</div>
						</div>
					</div>
				</td>
				</tr>
<%-- Notes --%>
	<tr>
	<td colspan="2" style=vertical-align:bottom;">
<div  class="subform" style="font-size:12px;vertical-align:bottom;padding:0px;">	
<table class="classicblue" style="border: 0px; padding: 0px; width: 99%;">
	<colgroup>
		<col width="50%">
		<col width="50%">
	</colgroup>
		<tr style="height:15px;">
			<td class="label" style="vertical-align: top;">
				<fmt:message key="wagon.note_disposition"/>
			</td>
			<td class="label" style="vertical-align: top;">
				<fmt:message key="wagon.note_railway_spedition"/>
			</td>
		</tr>
		<tr>
			<td>
				<c:set var="textarealength"><%=WagonEntity.DBSIZE_WAGON_NOTE_DISPOSITION + ""%></c:set>
				<html:textarea disabled="${readonly}" onkeypress="return checkMaxLength(event, this, ${textarealength})" property="wagonDetailLoad.wagonNoteDisposition" styleId="wagonDetailLoad.wagonNoteDisposition" style="width: 100%;height:60px;" cols="3"/>
			</td>
			<td>
				<c:set var="textarealength"><%=WagonEntity.DBSIZE_WAGON_NOTE_RAILWAY_SPEDITION + ""%></c:set>			
				<html:textarea disabled="${readonly}" onkeypress="return checkMaxLength(event, this, ${textarealength})" property="wagonDetailLoad.wagonNoteRailwaySpedition" styleId="wagonDetailLoad.wagonNoteRailwaySpedition" style="width: 100%;height:60px;" cols="3"/>
			</td>
		</tr>
</table>
</div>
</td>
<td>
</td>
</tr>
</table>
</td>
				<td style="vertical-align: top;">
					<div id="tabbeddata" class="subform" >
							<!-- Beginn subtab-Table -->
							<table cellpadding="0" cellspacing="0" class="compact">
								<tr>
									<td>
										<div class="subnaviborder">
											<ul id="subnavigation2">
												<li><a id="<%=WagonDetailAction.FORWARD_LOAD_TAB%>"
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_LOAD_TAB%>')" class="highlight">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.load"/></span></span>
												</a></li>
												<li><a id="<%=WagonDetailAction.FORWARD_CONTROL_TAB%>"
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_CONTROL_TAB%>')">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.control"/></span></span>
												</a></li>
												<li><a id="<%=WagonDetailAction.FORWARD_MAINTENANCE_TAB%>"
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_MAINTENANCE_TAB%>')">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.maintenance"/></span></span>
												</a></li>
												<li><a id="<%=WagonDetailAction.FORWARD_TYPE_TAB%>"
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_TYPE_TAB%>')">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.type"/></span></span>
												</a></li>
												<li><a id="<%=WagonDetailAction.FORWARD_BUILD_TAB%>"
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_BUILD_TAB%>')">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.build"/></span></span>
												</a></li>
												<li><a id="<%=WagonDetailAction.FORWARD_WHEELSET_TAB%>"
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_WHEELSET_TAB%>')">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.wheelset"/></span></span>
												</a></li>
												<li><a id="<%=WagonDetailAction.FORWARD_MAP_TAB%>" 
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_MAP_TAB%>')">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.map"/></span></span>
												</a></li>
												<li><a id="<%=WagonDetailAction.FORWARD_RFID_TAB%>"
														href="javascript:setCurrentWagonDetailTab('<%=WagonDetailAction.FORWARD_RFID_TAB%>')">
														<span class="outer"><span class="inner"><fmt:message key="wagondetail.tabitem.rfids"/></span></span>
												</a></li>
												
											</ul>
										</div>
									</td>
								</tr>
							</table>
		
							<div id="TabsDiv" class="subform" style="overflow:auto;font-size:12px; margin-top:4px;height:550px;">
								<jsp:include page="LoadTab.jsp"></jsp:include>
							</div>
						</div>
						</td>
</tr>


					<tr style="height:15px;">
				<td>
								<input type="button" value="<fmt:message key="buttons.back"/>" class="ViewButtonStyle" onclick='window.location="WagonSearch.do"'/>
				</td>
				<td colspan="2" style=vertical-align:bottom;">
	<table class="big_form2" style="spacing:0px; border: 0px; padding: 0px;" >
		<tr>
			<td style="text-align: right;">
				<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_W %>">
					&nbsp;<input type="button" value="<fmt:message key="wagondetail.text.copywagon"/>" class="ViewButtonStyle" onclick="copyWagon();"/>				
					&nbsp;<input type="button" value="<fmt:message key="wagon.button.reset"/>" class="ViewButtonStyle" onclick="resetWagon();"/>
					&nbsp;<input type="button" value="<fmt:message key="buttons.save"/>" class="ViewButtonStyle" onclick="saveWagon();"/>
				</crh-common:authorize>
			</td>
		</tr>
	</table>				
				</td>
				</tr>
	</table>
	</html:form>
		</td>
		</tr>
		</table>

	<div id="showCreateSpecialOrderPopupDiv" style="display: none; visibility: hidden"></div>

</div>
