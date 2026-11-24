<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.order.web.ajax.OrderAjaxService"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"><script type="text/javascript"></c:if>

	// --- Common properties for all filter-comboboxes --- //
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

    // --- Buttons ---
	Ext.get('buttonToggleAdvanced').on('click', function() {
		toggleAdvancedSearch(); 
	});
	Ext.get('buttonSearch').on('click', function() {
		loadFilters();
	});

	
	// ----- Simple Search (Monitoring) ----- //
	lastUpdateDatField = new Ext.form.DateField({
		id: 'lastUpdateDatField',
    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>' + ' H:i:s',
    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'lastUpdateDatStr',
    	readOnly: true,
    	hideTrigger: true,
    	width: 160
	});
	var autoRefreshField = new Ext.form.Checkbox({
		id: 'autoRefreshField',
    	applyTo: 'autoRefresh',
    	checked: false,
    	handler: function(element, checked) {
    		orderMonitor.stop();
    		if (checked) {
    			orderMonitor.start();
    		}
    	}
	});
	var orderNumberText = new Ext.form.TextField({
		id: 'orderNumberText',
		applyTo: 'orderNumber',
		maskRe: /\d/i
	});
	
	var orderStatusStore = new Ext.data.SimpleStore({
	    fields: ['cd', 'txt', 'checked'],
	    data : [ 
	<c:forEach var="orderStatus" varStatus="status" items="${order_auftrag_LiveKarteForm.orderStatusList}">
		[
	'<c:out value="${orderStatus.orderStatusTxPK.orderStatusBs.orderStatusCd}"/>',
			'<c:out value="${orderStatus.txt}"/>',
			true
		]
		<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	    ]
	});
	
	orderStatusCheckComboBox = new Ext.ux.CheckComboBox({
	    store: orderStatusStore,
	    displayField: 'txt',
	    checkedField: 'checked',
	    valueField: 'cd',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'all',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});
	orderStatusCheckComboBox.applyToMarkup('orderStatusField');
	 
	function toggleAdvancedSearch() {

		var divMap = Ext.getDom('map');
		
		if (advancedSearch) {
			//hide
			Ext.get("buttonToggleAdvanced").dom.value = '<fmt:message key="buttons.expand"/>';
			Ext.get("buttonToggleAdvanced").removeClass('SmallButtonStyleUp');
			Ext.get("buttonToggleAdvanced").addClass('SmallButtonStyleDown');
			Ext.get("divAdvancedSearch").slideOut('t', {
			    useDisplay: true
			});

	        //set map height
			var windowHeight = getWindowHeight();
			mapHeight = ((windowHeight - 290) > 310) ? (windowHeight - 290) : 310; 
			divMap.style.height =  mapHeight + "px";
			gmap.checkResize();
		}
		else {
			//expand
			Ext.get("buttonToggleAdvanced").dom.value = '<fmt:message key="buttons.collapse"/>';
			Ext.get("buttonToggleAdvanced").removeClass('SmallButtonStyleDown');
			Ext.get("buttonToggleAdvanced").addClass('SmallButtonStyleUp');
			Ext.get("divAdvancedSearch").show();
			Ext.getDom('divAdvancedSearch').style.height = "274px";
			
			//set map height
			var windowHeight = getWindowHeight();
			var mapHeight = ((windowHeight - 600) > 310) ? (windowHeight - 600) : 310;  
			divMap.style.height =  mapHeight + "px";
			gmap.checkResize();
		}
		advancedSearch = !advancedSearch; 
	}


	// --- Automatic refresh ---
	lastUpdateDat = new Date();
	lastUpdateDatField.setValue(lastUpdateDat);
	orderMonitor = {
		refreshRate: <%=ConfigBeanProvider.getConfigBean().getAppDefaultMonitoringRefreshInterval()%>,
		taskRunner: new Ext.util.TaskRunner(),
		task: {
		    run: function() {
		        if (this.nextRefresh == 1) {
		            this.nextRefresh = orderMonitor.refreshRate;
					//reload dynamic objects
		    		loadAllMapObjects();
		    		lastUpdateDat = new Date();
				    lastUpdateDatField.setValue(lastUpdateDat);
		        }
		        else {
		            this.nextRefresh--;
		        }
		        Ext.getDom('liveMapTimeLeftRefreshId').innerHTML = '(<fmt:message key="livemap.label.refresh.in"/> ' 
			        + this.nextRefresh 
			        + ' <fmt:message key="livemap.label.refresh.in.seconds"/>)';
	        },
		    nextRefresh: <%=ConfigBeanProvider.getConfigBean().getAppDefaultMonitoringRefreshInterval()%>,
		    interval: 1000, // 1 second (interval for the counter, not for the refresh)
		    active: false
		},
		start: function() {
			this.task.nextRefresh = this.refreshRate;
			if (!this.task.active) {
				Ext.TaskMgr.start(this.task);
				this.task.active = true;
			}
		},
		stop: function() {
			if (this.task.active) {
				Ext.TaskMgr.stop(orderMonitor.task);
				this.task.active = false;
				Ext.getDom('liveMapTimeLeftRefreshId').innerHTML = '';
			}
		}
	}

	
<%-- For JS syntax highlighting in Eclipse --%><c:if test="false"></script></c:if>
	