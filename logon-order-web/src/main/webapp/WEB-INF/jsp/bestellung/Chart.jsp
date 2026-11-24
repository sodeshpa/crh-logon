<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.order.web.ajax.ChartAjaxService"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>
<%@page import="com.crh.logon.taglib.fallbacklogic.DefaultFallBackLogicTag"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ChartAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>

<c:set var="filterPage" scope="page"><%= request.getParameter("filterPage") %></c:set>
<c:set var="imgWidth" scope="page"><%= request.getParameter("imgWidth") %></c:set>
<c:set var="imgHeight" scope="page"><%= request.getParameter("imgHeight") %></c:set>
<c:set var="checkboxWidth" scope="page"><%= request.getParameter("checkboxWidth") %></c:set>
<c:set var="isOnCallCenter" scope="page"><%= request.getParameter("isOnCallCenter") %></c:set>
<c:set var="isChartDisplayable" scope="page"><%= request.getParameter("isChartDisplayable") %></c:set>
<c:set var="isLandfill" scope="page"><%= request.getParameter("isLandfill") %></c:set>

<c:if test="${empty filterPage}">
    <c:set var="filterPage" value="${order_ChartForm.filterPage}" scope="request" />
</c:if>

<c:if test="${empty imgWidth}">
    <c:set var="imgWidth" value="${order_ChartForm.imgWidth}" scope="request" />
</c:if>

<c:if test="${empty imgHeight}">
    <c:set var="imgHeight" value="${order_ChartForm.imgHeight}" scope="request" />
</c:if>

<c:if test="${empty checkboxWidth}">
    <c:set var="checkboxWidth" value="${order_ChartForm.checkboxWidth}" scope="request" />
</c:if>

<input type="hidden" id="filterPage" name="filterPage" value='<c:out value="${filterPage}" escapeXml="false"/>' />  
<input type="hidden" id="imgWidth" />  
<input type="hidden" id="imgHeight" />
<input type="hidden" id="checkboxWidth" />

<script type="text/javascript">

	var blockChartRefresh = false;
	
	// define the filters
	var poolCheckCombo = null;
	var carrierCheckCombo = null;
	var transCatCombo = null;
	var truckTypeCheckCombo = null;	
	var deliveryTypeCheckCombo = null;
	var plantCombo = null;
	var shippingPointCheckCombo = null;
	var productTypeCombo = null;
	var chartTypeCombo = null;
	var wagonTrspCatCheckCombo = null;
	var wagonTypeCheckCombo = null;
	var selectedDayDatField = null;
	
	// hidden fields    
	var filterPage = null;;	
	var imgWidth = null;
	var imgHeight = null;
	var checkboxWidth = null;
	var selectedDayDat = null;

    function refreshChartArea(params) {
        // --- Do not refresh the chart, when the user clicks on the "Save" button immediately
        chartLoadingInProgress = true;
        refreshChartAreaToDefer.defer(500, refreshChartAreaToDefer, [params]);
    }

    function refreshChartAreaToDefer2(params) {
        
        if (!blockChartRefresh) {                       	
            params = (Ext.isEmpty(params) ? {} : params);
            
            addFormElements(params, 'truckPlanningForm');
            
            var isChartDisplayable = params['isChartDisplayable'];
            if (isChartDisplayable == undefined) {
            		params['isChartDisplayable'] = true;
            		isChartDisplayable = true;
            }
            
            if (isChartDisplayable == true){
            		Ext.get('divChartLoadMask').mask('Refreshing chart...', 'x-mask-loading');
            }

            Ext.get('divChart').load({
                url: 'Chart.do', 
                params: params,
                scripts: true,
                text: '',
                timeout: 4000,
                callback: function(el, success) {
                    if (!success) {
                        Logon.message.error('An error occurred during the Chart-reloading');
                    } 
                    else {                    	
            			var divChart = Ext.getDom("chart");
            	    	divChart.style.display = "";
                    	var callCenterHeading = Ext.getDom("callCenterHeading");
                    	
                    	// marius/20130527: quick fix for LOGON-6852. Call Center heading doesn't always
                    	// exist, depending on the context where this screen is included.
                    	if (callCenterHeading !== null) {
                    		callCenterHeading.style.display = "";
                    	}
                    	
                    	if (isChartDisplayable == true){
							Ext.get('divChartLoadMask').unmask();
						}
                    }
                },
                nocache: true
            });
        }
    }
      
    function addChartFormParamas(params) {
        params['poolIds'] = poolCheckCombo.getCheckedValues() ? poolCheckCombo.getCheckedValues().join(',') : null;
        params['carrierIds'] = carrierCheckCombo.getCheckedValues() ? carrierCheckCombo.getCheckedValues().join(',') : null;
        params['truckTypes'] = truckTypeCheckCombo.getCheckedValues() ? truckTypeCheckCombo.getCheckedValues().join(',') : null;
        params['deliveryTypes'] = deliveryTypeCheckCombo.getCheckedValues() ? deliveryTypeCheckCombo.getCheckedValues().join(',') : null;
        params['transCat'] = transCatCombo.getValue();
        params['plant'] = plantCombo.getValue();
        params['shippingPoints'] = shippingPointCheckCombo.getCheckedValues() ? shippingPointCheckCombo.getCheckedValues().join(',') : null;
        params['productTypes'] = productTypeCombo.getValue();
        params['wagonTrspCat'] = wagonTrspCatCheckCombo.getCheckedValues() ? wagonTrspCatCheckCombo.getCheckedValues().join(',') : null;
        params['wagonType'] = wagonTypeCheckCombo.getCheckedValues() ? wagonTypeCheckCombo.getCheckedValues().join(',') : null;
        params['chartType'] = chartTypeCombo.getValue();
        params['selectedDayDat'] = selectedDayDatField.getValue() ? selectedDayDatField.getValue() : undefined;
        params['companycode'] = companycodeCombo.getValue();
        
        // -- chart Screen properties
        params['filterPage'] = filterPage.getValue();
        params['imgWidth'] = imgWidth.getValue();
        params['imgHeight'] = imgHeight.getValue();
        params['checkboxWidth'] = checkboxWidth.getValue();
        
     	// -- Company code from the material combo box
     	if (filterPage.value == 'orderEntry') {
         	var selectedMatRecord = editorList[0].orderItems[0].sapMatCombo.getSelectedRecord();
         	if (selectedMatRecord !== undefined){
        	 	var selectedMaterialCompanyCode = editorList[0].orderItems[0].sapMatCombo.getSelectedRecord().data.companyCode;              		
         	}		
     	} 
	 	if (!Ext.isEmpty(selectedMaterialCompanyCode)) {
	 		params['companycode'] = selectedMaterialCompanyCode;
	 	}
    }
    
    Ext.onReady(function(){
    	// --- Common properties for all filter-comboboxes ---
    	Ext.form.ComboBox.prototype.shadow = false;
    	Ext.form.ComboBox.prototype.emptyText = "Select...";
    	Ext.form.ComboBox.prototype.minChars = 1;
    	Ext.form.ComboBox.prototype.allowBlank = true;
    	Ext.form.ComboBox.prototype.forceSelection = true;
    	Ext.menu.Menu.prototype.shadow = false;
    	
        Ext.get('buttonRefresh').on('click', function() {
            var params = (Ext.isEmpty(params) ? {} : params);
            
            addChartFormParamas(params);
            
            if (filterPage.value == 'orderEntry') {
           	 refreshChartAreaToDefer(params);
        	}
            else {
           	 	refreshChartAreaToDefer2(params);            	
            }      	
        });
        
        <c:if test="${filterPage != 'truckPlanning'}" >  
        	// --- Recalculate cycle time after modifying unloading time ---
    		Ext.get('unloadingTime').on('change', function() {       
        		updateTravelTimeFields('unloadingTimeChangeHandler');  
    		});
    	</c:if>
    
        // --- Pool ---
       poolCheckCombo = new Ext.ux.CheckComboBox({
            store: new Ext.data.JsonStore({
                fields: ['value', 'name', 'checked'],
                data : <c:out value="${order_ChartForm.json['poolList']}" escapeXml="false"/> 
            }),
            displayField: 'name',
            checkedField: 'checked',
            valueField: 'value',
            typeAhead: true,
            mode: 'local',
            triggerAction: 'all',
            emptyText:'inactive',
            selectOnFocus: true,
            width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
            listWidth: 250
        });
        poolCheckCombo.applyToMarkup('poolId');
        
        poolCheckCombo.on('change', function(checkbox, record) {  
        	carrierCheckCombo.clearValue();
        	carrierCheckCombo.disable();
        	var poolids = (poolCheckCombo !== undefined && poolCheckCombo.getCheckedValues()) ? poolCheckCombo.getCheckedValues().join(',') : null;       	
        	ChartAjaxService.fetchExecCarrierList(poolids, function(res) { 
        		carrierCheckCombo.store.loadData(res);
        		carrierCheckCombo.initList();
            	carrierCheckCombo.enable();
    		});
    	});    
        
        // --- Carrier ---
       carrierCheckCombo = new Ext.ux.CheckComboBox({
   	    	store: new Ext.data.JsonStore({
        		fields: ['value', 'name', 'checked'],
        		data : <c:out value="${order_ChartForm.json['execCarrierList']}" escapeXml="false"/>  
    		}),
            displayField: 'name',
            checkedField: 'checked',
            valueField: 'value',
            typeAhead: true,
            triggerAction: 'all',
            emptyText:'inactive',
            selectOnFocus: true,
            mode: 'local',
            width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
            listWidth: 250
	});
    carrierCheckCombo.applyToMarkup('carrierIds');
    
    // -- Transport Category --
    transCatCombo = new Ext.form.ComboBox({
    	id: 'transCatCombo',
        applyTo:'transCatId',
    	selectOnFocus: true,
    	editable: false,
        typeAhead: false,
        triggerAction: 'all',
        store: new Ext.data.JsonStore({
        	fields: ['value', 'name'],
        	data : <c:out value="${order_ChartForm.json['transCatList']}" escapeXml="false"/>
    	}),
        mode: 'local',
        width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
        listWidth: 250,
        valueField: 'value',
        displayField: 'name',
        value: <c:out value="${order_ChartForm.transCat}" escapeXml="false"/>
    });
    
    transCatCombo.on('select', function(combo, record) {
    	if (record.data['value'] == <%=Constant.CHART_TRANS_CAT_RAIL %>) {
    		truckTypeCheckCombo.disable();
    		wagonTypeCheckCombo.enable();
    		wagonTrspCatCheckCombo.enable();
    	}
    	else {
    		truckTypeCheckCombo.enable();
    		wagonTypeCheckCombo.disable();
    		wagonTrspCatCheckCombo.disable();
    	}   	
    	
    	poolCheckCombo.clearValue();
    	poolCheckCombo.disable();
 		ChartAjaxService.fetchPoolList(record.data['value'], function(res) { 
 			poolCheckCombo.store.loadData(res);
 			poolCheckCombo.enable();
 			poolCheckCombo.initList();
 		});
 		
		carrierCheckCombo.clearValue();
    	carrierCheckCombo.disable();
    	var poolids = (poolCheckCombo !== undefined && poolCheckCombo.getCheckedValues()) ? poolCheckCombo.getCheckedValues().join(',') : null;       	
    	ChartAjaxService.fetchExecCarrierList(poolids, function(res) { 
    		carrierCheckCombo.store.loadData(res);
    		carrierCheckCombo.enable();
    		carrierCheckCombo.initList();
		});
 	});
    
    // --- Truck Type ---
   truckTypeCheckCombo = new Ext.ux.CheckComboBox({
	    store: new Ext.data.JsonStore({
    		fields: ['value', 'name', 'checked'],
    		data : <c:out value="${order_ChartForm.json['truckTypeList']}" escapeXml="false"/>  
		}),
        displayField: 'name',
        checkedField: 'checked',
        valueField: 'value',
        typeAhead: true,
        triggerAction: 'all',
        emptyText:'inactive',
        selectOnFocus: true,
        mode: 'local',
        width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
		maxHeight: 400, // LOGON-5682: Usability of truck type list
        listWidth: 250
	});
   truckTypeCheckCombo.applyToMarkup('truckTypesIds');
    
    // --- Delivery Type ---
   deliveryTypeCheckCombo = new Ext.ux.CheckComboBox({
	    store: new Ext.data.JsonStore({
    		fields: ['value', 'name', 'checked'],
    		data : <c:out value="${order_ChartForm.json['deliveryTypeList']}" escapeXml="false"/>  
		}),
        displayField: 'name',
        checkedField: 'checked',
        valueField: 'value',
        typeAhead: true,
        triggerAction: 'all',
        emptyText:'inactive',
        selectOnFocus: true,
        mode: 'local',
        width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
        listWidth: 250
	});
	deliveryTypeCheckCombo.applyToMarkup('deliveryTypeIds');
   
   // -- companycode --
   companycodeCombo = new Ext.ux.TwinCombo({
   		id: 'companycodeCombo',
       	applyTo:'companycode',
   		selectOnFocus: true,
   		editable: false,
       	typeAhead: false,
       	triggerAction: 'all',
       	store: new Ext.data.JsonStore({
       		fields: ['value', 'name'],
       		data : <c:out value="${order_ChartForm.json['companycodeList']}" escapeXml="false"/>
   		}),
       	mode: 'local',
       	width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
       	listWidth: 250,
       	valueField: 'value',
       	// Company code: <c:out value="${order_ChartForm.companycode}" escapeXml="false"/>
       	<c:if test="${not empty order_ChartForm.companycode}">
       		value: '<c:out value="${order_ChartForm.companycode}" escapeXml="false"/>',
       	</c:if>
       	displayField: 'name',
	    onTrigger2Click: function() {
	    	plantCombo.clearValue();
		   	plantCombo.disable();
	    	shippingPointCheckCombo.clearValue();
	    	shippingPointCheckCombo.disable();
	    	this.clearValue();
	    }
   });
   
   companycodeCombo.on('select', function(combo, record) {
	   	plantCombo.clearValue();
	   	plantCombo.disable();
		ChartAjaxService.fetchPlantList(record.data['value'], function(res) { 
			plantCombo.store.loadData(res);
			plantCombo.initList();
			plantCombo.enable();
		});
		shippingPointCheckCombo.clearValue();
		shippingPointCheckCombo.disable();
	});
   
   // -- Plant --
   plantCombo = new Ext.ux.TwinCombo({
   		id: 'plantCombo',
       	applyTo:'plantId',
   		selectOnFocus: true,
   		editable: false,
       	typeAhead: false,
       	triggerAction: 'all',
       	store: new Ext.data.JsonStore({
       		fields: ['value', 'name'],
       		data : <c:out value="${order_ChartForm.json['plantBsList']}" escapeXml="false"/>
   		}),
       	mode: 'local',
       	width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
       	listWidth: 250,
       	valueField: 'value',
       	<c:if test="${not empty order_ChartForm.plant}">
       		value: '<c:out value="${order_ChartForm.plant}" escapeXml="false"/>',
       	</c:if>
       	displayField: 'name',
	    onTrigger2Click: function() {
	    	shippingPointCheckCombo.clearValue();
	    	shippingPointCheckCombo.disable();
	    	this.clearValue();
	    }
   });
   
   plantCombo.on('select', function(combo, record) {
		shippingPointCheckCombo.clearValue();
		shippingPointCheckCombo.disable();
		ChartAjaxService.fetchShippingPointList(record.data['value'], function(res) { 
			shippingPointCheckCombo.store.loadData(res);
			shippingPointCheckCombo.enable();
			shippingPointCheckCombo.initList();
		});
	});
   
   // --- Shipping point ---
   shippingPointCheckCombo = new Ext.ux.CheckComboBox({
	    	store: new Ext.data.JsonStore({
    		fields: ['value', 'name', 'checked'],
    		data : <c:out value="${order_ChartForm.json['shippingPointList']}" escapeXml="false"/>  
		}),
        displayField: 'name',
        checkedField: 'checked',
        valueField: 'value',
        typeAhead: true,
        triggerAction: 'all',
        emptyText:'inactive',
        selectOnFocus: true,
        mode: 'local',
        width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
        listWidth: 250,
        selectShippingPoint: function(spId) {
        	var posInComboBox = this.store.find(this.valueField, new RegExp('^' + spId + '$'));
        	if (posInComboBox > -1) {
        		this.selectNone();
        		this.setChecked(spId, true);
        	}
        	else {
        		this.selectAll();
        	}
        }
	});
   shippingPointCheckCombo.applyToMarkup('shippingPointIds'); 
   // This function intended to run on Order Entry, not on Truck Planning.
   <c:if test="${filterPage == 'orderEntry'}" >
   (function() {
		var selectedShippingPoint = '<c:out value="${order_ChartForm.selectedShippingPoint}" escapeXml="false"/>';
		if (selectedShippingPoint === '') {
			shippingPointCheckCombo.selectAll();
		}
		else {
			shippingPointCheckCombo.selectShippingPoint(selectedShippingPoint);
		}
   })();
   </c:if>
		
   // --- product type ---
   productTypeCombo = new Ext.ux.TwinCombo({
	    	store: new Ext.data.JsonStore({
    		fields: ['value', 'name'],
    		data : <c:out value="${order_ChartForm.json['productTypeList']}" escapeXml="false"/>  
		}),
		applyTo:'productTypeIds',
        displayField: 'name',
        valueField: 'value',
       	<c:if test="${not empty order_ChartForm.productTypes}">
   			value: '<c:out value="${order_ChartForm.productTypes}" escapeXml="false"/>',
   		</c:if>
        typeAhead: true,
        triggerAction: 'all',
        emptyText:'inactive',
        selectOnFocus: true,
        mode: 'local',
        width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
        listWidth: 250,
        boxMaxHeight: 400
	});
   
   // -- chart type --
   chartTypeCombo = new Ext.form.ComboBox({
   	id: 'chartTypeCombo',
    applyTo:'chartType',
   	selectOnFocus: true,
   	editable: false,
       typeAhead: false,
       triggerAction: 'all',
       store: new Ext.data.JsonStore({
       	fields: ['value', 'name'],
       	data : <c:out value="${order_ChartForm.json['chartTypeList']}" escapeXml="false"/>
   	}),
       mode: 'local',
       width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
       listWidth: 250,
       valueField: 'value',
       displayField: 'name',
       value: <c:out value="${order_ChartForm.chartType}" escapeXml="false" />
   }); 
    
    // --- wagon trsp category ---
    wagonTrspCatCheckCombo = new Ext.ux.CheckComboBox({
 	    	store: new Ext.data.JsonStore({
     		fields: ['value', 'name', 'checked'],
     		data : <c:out value="${order_ChartForm.json['wagonTrspCatList']}" escapeXml="false"/>  
 		}),
         displayField: 'name',
         checkedField: 'checked',
         valueField: 'value',
         typeAhead: true,
         triggerAction: 'all',
         emptyText:'inactive',
         selectOnFocus: true,
         mode: 'local',
         width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
         listWidth: 250,
         boxMaxHeight: 400
 	});
    wagonTrspCatCheckCombo.applyToMarkup('wagonTrspCat');
    
    // --- wagon type ---
    wagonTypeCheckCombo = new Ext.ux.CheckComboBox({
 	    store: new Ext.data.JsonStore({
     		fields: ['value', 'name', 'checked'],
     		data : <c:out value="${order_ChartForm.json['wagonTypeList']}" escapeXml="false"/>  
 		}),
         displayField: 'name',
         checkedField: 'checked',
         valueField: 'value',
         typeAhead: true,
         triggerAction: 'all',
         emptyText:'inactive',
         selectOnFocus: true,
         mode: 'local',
         width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
         listWidth: 250,
         boxMaxHeight: 400
 	});
    wagonTypeCheckCombo.applyToMarkup('wagonType');
    
    // -- Date
	selectedDayDatField = new Ext.form.DateField({
    	applyTo: 'selectedDayDat',
		id: 'selectedDayDat',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
    	width: <c:out value="${checkboxWidth}" escapeXml="false"/>,
    	value: '<c:out value="${order_ChartForm.selectedDayDat}" escapeXml="false"/>'
	});

	filterPage = new Ext.form.Hidden({
		id: 'filterPage',
		applyTo: 'filterPage',
		hidden: true,
		value: '<c:out value="${filterPage}" escapeXml="false"/>'
	});
	
	imgWidth = new Ext.form.Hidden({
		id: 'imgWidth',
		applyTo: 'imgWidth',
		hidden: true,
		value: '<c:out value="${imgWidth}" escapeXml="false"/>'
	});
	
	imgHeight = new Ext.form.Hidden({
		id: 'imgHeight',
		applyTo: 'imgHeight',
		hidden: true,
		value: '<c:out value="${imgHeight}" escapeXml="false"/>'
	});
	
	checkboxWidth = new Ext.form.Hidden({
		id: 'checkboxWidth',
		applyTo: 'checkboxWidth',
		hidden: true,
		value: '<c:out value="${checkboxWidth}" escapeXml="false"/>'
	});

	if (<c:out value="${order_ChartForm.transCat}" /> == <%=Constant.CHART_TRANS_CAT_RAIL %>) {
		truckTypeCheckCombo.disable();
		wagonTypeCheckCombo.enable();
		wagonTrspCatCheckCombo.enable();
	}
	else {
		truckTypeCheckCombo.enable();
		wagonTypeCheckCombo.disable();
		wagonTrspCatCheckCombo.disable();
	} 	 
  	<c:if test="${empty order_ChartForm.companycode}">
		 // disable the plant and shipingpoint
  		plantCombo.clearValue();
  		plantCombo.disable();
		
		shippingPointCheckCombo.clearValue();
		shippingPointCheckCombo.disable();
	</c:if>
	<c:if test="${filterPage != 'truckPlanning'}" > 
		updateTravelTimeFields('chart');	
	</c:if>
});
</script>  
  
<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2">
    <tr>
        <td class="border">
            <div id="divSimpleSearch">
                <c:choose> 
                    <c:when test="${filterPage == 'truckPlanning'}" >            
                        <jsp:include page="ChartTruckPlanningFilters.jsp" />
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="ChartOrderEntryFilters.jsp" />
                    </c:otherwise>
                </c:choose>
                <table style="width: 100%;">
                    <tr>
                        <td>
                            <div id="divChartLoadMask">
                                <div id="divChart" style="width: 100%; height: <c:out value="${imgHeight}"/>px" align="center">
                                    <div id="chart" style="display: none; height: <c:out value="${imgHeight}"/>px">
                                    	<c:if test="${isChartDisplayable}">                                 
                                        <c:url var="chartUrl" value="/bestellungChart.servlet">
                                            <c:param name="_dc" value="${param._dc}"/>
                                            <c:param name="width" value="${imgWidth}"/>
                                            <c:param name="height" value="${imgHeight}"/>
                                        </c:url>
                                        <img id="chartImg" src="<c:out value="${chartUrl}"/>" width="<c:out value="${imgWidth}"/>"
                                            height="<c:out value="${imgHeight}"/>" />
                                   		</c:if>
                                    </div>
                                </div> 
                            </div>
                        </td>
                    </tr>                                            
                </table>
            </div>
        </td>
    </tr>
    <tr>
		<td>			 
			<c:if test="${isOnCallCenter}">
			<% if (request.getSession().getAttribute("order_bestellung_BestellungForm") != null) { %>
				<table id="callCenterHeading" style="display: none">
					<tr>
						<td class="label">
							<fmt:message key="bestellung.chart.loadingtime"/>
						</td>
						<td class="label">
							<fmt:message key="bestellung.chart.driveto"/>
						</td>
						<td class="label">
							<fmt:message key="bestellung.chart.unloadingtime"/>
						</td>
						<td class="label">
							<fmt:message key="bestellung.chart.return"/>
						</td>
						<td class="label">
							<fmt:message key="bestellung.chart.cycletime"/>
						</td>
					</tr>
					<tr>
						<%pageContext.setAttribute("shipTo", ((BestellungForm)request.getSession().getAttribute("order_bestellung_BestellungForm")).getShipTo());%>
						<%pageContext.setAttribute("editorList", ((BestellungForm)request.getSession().getAttribute("order_bestellung_BestellungForm")).getEditorList());%>
						<c:if test="${isLandfill}">
						<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_SHIPTO_LOADING_TIME_MIN_LF %>" propertyInputFieldName="loadingTime" propertyCheckboxName="loadingTimeCheckbox" objectName="shipTo" objectName2="editorList">
							<td>
								<crh-common:defaultFallbackInputField></crh-common:defaultFallbackInputField>
							</td>
						</crh-common:defaultFallback>
						</c:if>
						<c:if test="${!isLandfill}">
						<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_ORDER_CREATION_LOADING_TIME_MIN %>" propertyInputFieldName="loadingTime" propertyCheckboxName="loadingTimeCheckbox" objectName="shipTo" objectName2="editorList">
							<td>
								<crh-common:defaultFallbackInputField></crh-common:defaultFallbackInputField>
							</td>
						</crh-common:defaultFallback>
						</c:if>
							<td>
								<%-- Used to display read-only values, so do not need server side field for this value --%>
								<input type="text" id="cchDrivetoTime" disabled/>
							</td>
						<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_ORDER_CREATION_UNLOADING_TIME_MIN %>" propertyInputFieldName="unloadingTime" propertyCheckboxName="unloadingTimeCheckbox" objectName="shipTo" objectName2="editorList">
							<td>
								<crh-common:defaultFallbackInputField></crh-common:defaultFallbackInputField>
							</td>
						</crh-common:defaultFallback>
							<td>
								<%-- Used to display read-only values, so do not need server side field for this value --%>
								<input type="text" id="cchReturnTime" disabled/>
							</td>
							<td>
								<%-- Used to display read-only values, so do not need server side field for this value --%>
								<input type="text" id="cchCycleTime" disabled/>
							</td>
					</tr>
				</table>
			<% } %>
			</c:if>
		</td>
	</tr>
</table>