<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.forms.auftrag.OrderDetailForm"%>

<%@page import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ page import="com.crh.logon.order.web.ajax.OrderDetailAjaxService"%>

<!-- JavaScript Start -->
<script type="text/javascript">

// Record definition for Combo's datasource
var vendorRec = Ext.data.Record.create([
                                	    {name: "id"},
                                	    {name: "label"}
                                	    ]);
                                	
// datasource for Combo
var vendorStore = new Ext.data.Store(
		{
			proxy : new Ext.data.DWRProxyForPager(
					function(skip, count, sort, query, callback) {
						OrderDetailAjaxService
								.fetchAvailableBindings(
										skip,
										count,
										query,
										callback);
					}, true),
					
			reader : new Ext.data.ListRangeReader({
				id : 'id',
				totalProperty : 'totalSize'
			}, vendorRec)
		});
 
 // returns true, if the given id is already displayed
 // in the upper list
 function isTpSapVendorIdIsOnTheList(tpSapVendorId) {
	var id = 	"row"+tpSapVendorId;
	 var found =  document.getElementById("row"+tpSapVendorId);
	 var exists = (found != undefined);
	 return !exists;
 }
 
 // Enables or disables the add button (+). If the tpSapVendorId is already added, disables the button
 function updateAddButtonState(tpSapVendorId) {
	 var addButton = document.getElementById("addButton");
	 var canBeAdded = isTpSapVendorIdIsOnTheList(tpSapVendorId);

		if (canBeAdded) {
			addButton.style.visibility = 'visible';
		}	
		else {
			addButton.style.visibility = 'hidden';
		}
			
 }
 
 	// tpSapVendorId of the selected item in the combo
 	var lastSelectedId;
 
	var selectionCombo = new Ext.ux.TwinCombo({
		id : 'searchInputField',
		applyTo : 'searchInputField',
		store : vendorStore,
		listeners : {
			
			select : function(combo, record, index) {
		    
				var tpSapVendorId = vendorStore.getAt(index).id; 

				lastSelectedId = tpSapVendorId;
				updateAddButtonState(tpSapVendorId);
				
				
			}
		},
		displayField : 'label',
		valueField : 'id',
		pageSize: 20,
		width: 300,
		listWidth: 300,
		typeAhead: false,
		forceSelection: true,
		triggerAction: 'all',
		minChars: 3,
		selectOnFocus: true

	});

	// Deletes a row, given by id "rowXXX" from the table with id "existingBindingsTable"
	function deleteRow(rowId) {
		
		OrderDetailAjaxService.updateBindingsList(rowId.substring(3), false, {
			callback: function() {
				var row = document.getElementById(rowId);
				row.parentNode.removeChild(row);
			}
		});
	}	
	
	// inserts the selected item to the list from the Combo's selected item
	function addNewItem() {
		
		var selectionComboValue = selectionCombo.getValue();
		var record = selectionCombo.findRecord(selectionCombo.valueField || selectionCombo.displayField, selectionComboValue);
		var index = selectionCombo.store.indexOf(record);
		var tpSapVendorId = selectionCombo.getValue();
		var label = selectionCombo.store.data.items[index].data.label;
		
		OrderDetailAjaxService.updateBindingsList(tpSapVendorId, true, {
			callback: function(position) {
				// successfull callback handler
				var table = document.getElementById("existingBindingsTable");
				// inserts the new row at position + 1 (since the first row is the header)
				var row = table.insertRow(position + 1);
				var idCell = row.insertCell(0);
				var labelCell = row.insertCell(1);
				
				idCell.innerHTML = label;
				
				var deleteButtonHTML = getDeleteButtonImageHTML(tpSapVendorId);
				labelCell.innerHTML = deleteButtonHTML;
				
				row.id = "row"+tpSapVendorId;
				
				updateAddButtonState(tpSapVendorId);
			}
		});
	}
	
	// Returns the HTML code for the Delete button
	function getDeleteButtonImageHTML(id) {
		return "<img src=\"" + getDeleteButtonImageURL() + "\" onclick=\"" + getDeleteRowJavaScript(id) + "\" />";
	}
	
	// Returns the JavaScript for the onClick event for the delete button
	function getDeleteRowJavaScript(id) {
		return "deleteRow('row" + id + "')";
	}
	
	// Returns the image URL for the delete button
	function getDeleteButtonImageURL() {
		return "<crh-common:url value="/images/delete2.png" />";
	}
	
	// Displays the Popup to create a new TpSapOrderEntity
	function showVendorPfPopup() {
		openVendorPfPopup();
		// reload combo data
		selectionCombo.lastQuery = null;
		
	}
	
</script>

<!-- Javascript end -->

<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>

<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />


<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />

<table id="existingBindingsTable">

<tr><td colspan="2">

<!-- Third Party Freighters label -->
<div class="labelblue">
<fmt:message key="orderdetail.third.party.freighters.third.party.freighters" />
</div>
</td></tr>
 
<c:forEach items="${form.existingBindings}" var="bindings">
    <tr id="row<c:out value="${bindings.id}" />">
        <td >
        <c:out value="${bindings.label}" /></td>
        <td><img src="<crh-common:url value="/images/delete2.png" />" onclick="deleteRow('row<c:out value="${bindings.id}" />')" /></td>
    </tr>
</c:forEach>

<tr><td>
<!-- search field -->
<input type="text" id="searchInputField"/>
</td>

<!-- Add Button -->
<td><img style="visibility:hidden" id="addButton" onClick="addNewItem()"  src="<crh-common:url value='/images/add.png'/>" /></td></tr>
<tr><td colspan="2"><input class="ViewButtonStyle" value="<fmt:message key="orderdetail.third.party.freighters.create.new.sap.vendor.partner.function" />" onclick="showVendorPfPopup()" type="button"></td></tr>


</table>

<hr
	style="border: 0px none; height: 1px; background-color: rgb(204, 204, 204);" />


<!--</html:form>-->
