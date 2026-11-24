<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RailCarOptimizationTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<%@page import="com.crh.logon.order.web.actions.railcaroptimization.RailCarOptimizationAction" %>

<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/dispo.css"/>">

<style type="text/css">
.zebra0 td.zebradark {
	background-color:#EEEEEE;
}
.zebra1 td.zebradark {
	background-color:#EEEEEE;
}

.zebra0 div {
	background-color:#EEEEEE;
}
.zebra1 div {
	background-color:#EEEEEE;
}

</style>

<script type="text/javascript">

function disableShiftClick(e) {
	if (!e) {
		e = window.event;
	}
	if (e && (e.shiftKey || e.ctrlKey)){
		if (e.preventDefault){
			e.preventDefault()
		}
		return false;
	}
}

Ext.getDoc().on('mousedown', disableShiftClick);

document.onselectstart = disableShiftClick;

var errortexts ={

};

var texts =	{

		};
    
	var dispodate;
	var orderList;
	var wagonList;
	
	<%--var lastPageReload = new Date();--%>
	
	function modifyButtonState(button, actionAllowed){
		button.disabled=!actionAllowed;
		button.className = (actionAllowed)?"SmallButtonStyle":"SmallButtonStyleDisabled";
	}
	
	function dispodateIsToday(){
		ddate = dispodate.getValue();
		today = new Date();
		return today.clearTime().getElapsed(ddate.clearTime()) == 0;
	}

	function refreshCountData(){
		var selected = dwr.util.getValue('selectedPlant');
		var params = {'<%=RailCarOptimizationAction.ACTION_SHOW_WAGON_TYPE_COUNT_TABLE%>':'<%=RailCarOptimizationAction.ACTION_SHOW_WAGON_TYPE_COUNT_TABLE%>'};
		addFormElements(params, "order_RailCarOptimizationForm");
			var diva = Ext.get('wagontypecountcontainer');
			diva.load({
				url: 'RailCarOptimization.do', 
				params: params,
				text: texts.extjsLoading,
				scripts: true,
				callback: function(){}
			});		
	}
	function refreshWagons(origparams){
		var selected = dwr.util.getValue('selectedPlant');
		var params = {'<%=RailCarOptimizationAction.ACTION_SHOW_WAGONS%>':'<%=RailCarOptimizationAction.ACTION_SHOW_WAGONS%>'};
		addFormElements(params, "order_RailCarOptimizationForm");
		if (origparams) {
			addFormElements(origparams, "order_RailCarOptimizationForm");
		}
			var diva = Ext.get('wagoncontainer');
			diva.load({
				url: 'RailCarOptimization.do', 
				params: params,
				text: texts.extjsLoading,
				scripts: true,
				callback: refreshCountData
			});
    }


	Ext.onReady(function(){
		Ext.QuickTips.init();
		Ext.apply(Ext.QuickTips.getQuickTip(), {
		    maxWidth: 400,
		    dismissDelay: 0
		});
		var qt = Ext.QuickTips.getQuickTip();

		dispodate = new Ext.form.DateField({
	    	applyTo : 'DispoDayPicker',
	    	showToday: false,
	    	format:  'd.m.Y'
		});
		dispodate.on('valid', function(event){
			if(!dispodate.getValue()){
				dispodate.setValue(new Date());
			}
			var params = {'stringDate':dispodate.getValue().format('d.m.Y')};
			refreshWagons(params);
		});


		<%--		
		
		var dd = Ext.get("tableresize").initDD("tableresize", null, {
			startDrag: function(x, y){
				this.getEl().style.backgroundColor = "#999";
				var dispotable = Ext.get('wagoncontainer');
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
				var dispotable = Ext.get('wagoncontainer');
				var tablesheight = document.body.clientHeight - dispotable.getTop() - 60;
				var newHeight = Ext.get(this.getEl()).getTop() - 40 - dispotable.getTop();
				heightFactor = newHeight/tablesheight;
				if (heightFactor < this.minfact){
					heightFactor = this.minfact;
				}
				else if (heightFactor > this.maxfact){
					heightFactor = this.maxfact;
				}
				resizeTables();
				this.getEl().style.top = "0px";
			},
			maxfact: 0.9,
			minfact: 0.1
		});
		 		--%>	
	});
	
	
</script> 



<jsp:include page="../SubTab7Tile.jsp"/>
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.4"/></th>
      </tr>
      <tr>
    
      <td>
	<jsp:include page="../Sub1Tab7Tile.jsp"/>

   <%-- %>crh-common:authorize function="<%= Authorization.MASTER_DATA_GLOBAL_PRODUCT_CHANGE_MATRIX %>"--%>

<html:form action="railcaroptimization/RailCarOptimization.do" styleId='order_RailCarOptimizationForm'>

<table class="big_form2">
  <tbody>
  	<tr>
		<td width="1" class="label">
			<fmt:message key="railcaroptimization.texte.plant"/>
		</td>
		<td width="1"> 
<script type="text/javascript">
	function setSelectedPlant(){
		a = {'<%=RailCarOptimizationAction.ACTION_SET_PLANT%>':''};
		shippingpointList.collapse();
		addFormElements(a, "order_RailCarOptimizationForm"); <%-- add form data --%>
		Ext.Ajax.request({
			url: 'RailCarOptimization.do', 
			params: a,
			success: function(response){
				eval(response.responseText);
				refreshWagons();
			}
		});
	}
	
    function setSelectedShippingpointCheckboxes(){
		a = {'<%=RailCarOptimizationAction.ACTION_SET_SHIPPINGPOINTS%>':''};
		addFormElements(a, "order_RailCarOptimizationForm"); <%-- add form data --%>
		Ext.Ajax.request({
		   url: 'RailCarOptimization.do',
		   params: a,
		   success: function(response){
		    eval(response.responseText);
		   	refreshWagons();
		   }
		});
    
    }

    function setSelectedWagonTypeCheckboxes(){
		a = {'<%=RailCarOptimizationAction.ACTION_SET_WAGON_TYPES%>':''};
		addFormElements(a, "order_RailCarOptimizationForm"); <%-- add form data --%>
		Ext.Ajax.request({
		   url: 'RailCarOptimization.do',
		   params: a,
		   success: function(response){
		    eval(response.responseText);
		   	refreshWagons();
		   }
		});
    }
    

	function setAllCheckboxes(value, container){
		list = Ext.query("input[type='checkbox']", Ext.getDom(container));
		for (i = 0; i < list.length; i++){
			list[i].checked = value;
		}
	}
	
	
	Ext.onReady(function(){
		Ext.QuickTips.init();
		Ext.apply(Ext.QuickTips.getQuickTip(), {
		    maxWidth: 400,
		    dismissDelay: 0
		});
		
		shippingpointList = new Ext.form.TriggerField({
			readOnly:true
		});
		
		
		shippingpointList.listPanel = Ext.get('DivShippingpointsEdit');
		shippingpointList.onTriggerClick = function(){
			this.listPanel.load({
				url: 'RailCarOptimization.do',
				params: {'<%=RailCarOptimizationAction.ACTION_EDIT_SHIPPINGPOINTS%>':''},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts:true,
				callback: function(el){
					el.show();
				}	
			});
	        Ext.getDoc().on('mousewheel', this.collapseIf, this);
	        Ext.getDoc().on('mousedown', this.collapseIf, this); 
		}
		shippingpointList.collapse = function(){
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
	       	Ext.getDoc().un('mousewheel', this.collapseIf, this);
	        Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		shippingpointList.collapseIf = function(e){
	        if(!e.within(this.listPanel)){
	            this.collapse();
	        }
		}
		shippingpointList.applyToMarkup('ShippingpointsField');
			
		<jsp:include page="RailCarOptimizationShippingpointsTile.jsp"/>

		wagonTypeList = new Ext.form.TriggerField({
			readOnly:true
		});
		wagonTypeList.listPanel = Ext.get('DivWagonTypesEdit');
		wagonTypeList.onTriggerClick = function(){
			this.listPanel.load({
				url: 'RailCarOptimization.do',
				params: {'<%=RailCarOptimizationAction.ACTION_EDIT_WAGONTYPES%>':''},
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts:true,
				callback: function(el){
					el.show();
				}	
			});
	        Ext.getDoc().on('mousewheel', this.collapseIf, this);
	        Ext.getDoc().on('mousedown', this.collapseIf, this); 
		}
		wagonTypeList.collapse = function(){
			this.listPanel.hide();
			this.listPanel.dom.innerHTML = "";
	       	Ext.getDoc().un('mousewheel', this.collapseIf, this);
	        Ext.getDoc().un('mousedown', this.collapseIf, this); 
		}
		wagonTypeList.collapseIf = function(e){
	        if(!e.within(this.listPanel)){
	            this.collapse();
	        }
		}
		wagonTypeList.applyToMarkup('WagonTypesField');
			
		<jsp:include page="RailCarOptimizationWagonTypesTile.jsp"/>

		
								
		refreshWagons();
		
	<%-- Is periodical update needed?
		updateRunner = new Ext.util.TaskRunner();
		updateRunner.start({
		    run: function(){
		    	if (wagonList && !wagonList.draggingInProgress && !isPopupShown()){
		    		var now = new Date();
					var diff = now.getElapsed(lastPageReload);
		    		if (diff < 7200000) { // 2 hours
			    		Ext.QuickTips.getQuickTip().hide();
						refreshWagons();
					}
					else {
						window.location = document.URL; // reload whole window
					}
				}	
		    },
		    interval: 120000 
		}); --%> 	 	
			
		
	});
	
	function onOrderDetailClose(){
		refreshWagons();
	}	
	
	function logonPopupHideCallback() {
		refreshWagons();
	}

	function logonPopupHideCallback() {
		refreshWagons();
	}


</script>

			<html:select size="1" style="width:150px;" property="selectedPlant" onchange="setSelectedPlant()">
				<c:forEach var="plant" varStatus="status" items="${order_RailCarOptimizationForm.plants}">
					<html:option value="${plant.plantCd}"><c:out value="${plant.plantName}"/></html:option>
				</c:forEach>					
			</html:select>
			
 		</td>
 		<td width="1" class="label">
			<nobr><fmt:message key="railcaroptimization.texte.shippingpoints"/></nobr>
		</td>
		<td width="1">
			<input type="text" id="ShippingpointsField" style="width:150px;color: black">
			<div class="border" id="DivShippingpointsEdit" style="z-index:40;background:white;width:200px; position: absolute;visibility:hidden">
			</div>
 		</td>
 		<td width="1" class="label">
 			<fmt:message key="dispo.texte.date"/>
 		</td>
 		<td  width="1">
			<html:text styleClass='textfield' style='width:100px' styleId='DispoDayPicker' property="stringDate"/>
 		</td>
		<td width="1">
			<input type="text" id="WagonTypesField" style="width:150px;color: black">
			<div class="border" id="DivWagonTypesEdit" style="z-index:40;background:white;width:200px; position: absolute;visibility:hidden">
			</div>
 		</td> 		
 		<td width="100%">
 		</td>
    </tr>
  </tbody>
</table>

<p class="label" style="margin-top: 8px">
	<fmt:message key="railcaroptimization.texte.RailcarSummary" />
	<span id="outputSpan"> </span>
</p>
<div align="center" id="wagontypecountcontainer" ></div>

<p class="label" style="margin-top: 8px">
	<fmt:message key="railcaroptimization.texte.AlreadyDispatched" />
	
	<span id="outputSpan2"> </span>
</p>
<div id="wagoncontainer" ></div>
</td>
</tr>
</table>
</html:form>


<%-- END $RCSfile: RailCarOptimizationTile.jsp,v $ --%>
